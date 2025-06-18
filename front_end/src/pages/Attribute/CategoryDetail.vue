<template>
  <div class="category-detail-container">
    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>分类详情</span>
          <el-button class="back-button" text @click="goBack">返回</el-button>
        </div>
      </template>

      <el-collapse v-model="activeNames">
        <el-collapse-item title="分类基本信息" name="basic">
          <el-form label-width="150px" :model="categoryData" disabled>
            <el-form-item label="分类码">
              <el-input v-model="categoryData.categoryCode"></el-input>
            </el-form-item>
            <el-form-item label="中文名称">
              <el-input v-model="categoryData.categoryNameZh"></el-input>
            </el-form-item>
            <el-form-item label="中文描述">
              <el-input v-model="categoryData.description" type="textarea"></el-input>
            </el-form-item>
            <el-form-item label="英文名称">
              <el-input v-model="categoryData.categoryNameEn"></el-input>
            </el-form-item>
            <el-form-item label="英文描述">
              <el-input v-model="categoryData.descriptionEn" type="textarea"></el-input>
            </el-form-item>
            <el-form-item label="分类状态">
              <el-input v-model="categoryData.isSyntheticDescription"></el-input>
            </el-form-item>
            <el-form-item label="父分类码">
              <el-input v-model="categoryData.parentCategoryCode"></el-input>
            </el-form-item>
            <el-form-item label="父分类名称">
              <el-input v-model="categoryData.parentCategoryName"></el-input>
            </el-form-item>
            <el-form-item label="父分类中文描述">
              <el-input v-model="categoryData.parentCategoryDescription" type="textarea"></el-input>
            </el-form-item>
            <el-form-item label="父分类英文描述">
              <el-input v-model="categoryData.parentCategoryDescriptionEn" type="textarea"></el-input>
            </el-form-item>
            <el-form-item label="父分类状态">
              <el-input v-model="categoryData.parentCategoryStatus"></el-input>
            </el-form-item>
          </el-form>
        </el-collapse-item>

        <el-collapse-item title="属性信息" name="attributes">
          <el-table :data="categoryData.attributes" style="width: 100%;">
            <el-table-column prop="serialNumber" label="属性序列号" width="100"></el-table-column>
            <el-table-column prop="nameZh" label="属性中文名称" width="120"></el-table-column>
            <el-table-column prop="nameEn" label="属性英文名称" width="140"></el-table-column>
            <el-table-column prop="descriptionZh" label="属性中文描述" show-overflow-tooltip width="150"></el-table-column>
            <el-table-column prop="dataType" label="数据类型" width="100"></el-table-column>
            <el-table-column prop="unit" label="单位" width="80"></el-table-column>
            <el-table-column prop="allowedValues" label="允许值" width="120"></el-table-column>
            <el-table-column prop="defaultValue" label="默认值" width="100"></el-table-column>
            <el-table-column prop="isRequired" label="是否必填" width="80"></el-table-column>
            <el-table-column prop="isSyntheticDescription" label="是否合成描述" width="120"></el-table-column>
            <el-table-column prop="isMultipleValues" label="是否多值" width="80"></el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { apiCateGet } from '@/api/CateGet';

const route = useRoute();
const router = useRouter();

const categoryData = ref({
  categoryCode: '',
  categoryNameZh: '',
  categoryNameEn: '',
  description: '',
  descriptionEn: '',
  parentCategoryName: '',
  parentCategoryCode: '',
  parentCategoryDescription: '',
  parentCategoryDescriptionEn: '',
  parentCategoryStatus: '',
  isSyntheticDescription: '',
  attributes: []
});
const activeNames = ref(['basic', 'attributes']); // 默认展开所有部分
const loading = ref(false);

// 根据ID查询分类详情
const fetchCategoryById = async (id) => {
  try {
    console.log('fetchCategoryById 接收到的 id:', id, '类型:', typeof id);
    
    if (!id || id === 'undefined' || id === 'null') {
      console.log('ID 无效，跳过查询');
      return null;
    }
    
    // 只传id参数，其他参数传空字符串
    const response = await apiCateGet(1, 10, '', '', BigInt(id).toString());
    
    if (response && response.code === 1 && response.data && response.data.records && response.data.records.length > 0) {
      return response.data.records[0];
    }
    return null;
  } catch (error) {
    console.error('根据ID查询分类失败:', error);
    return null;
  }
};

// 查询父分类信息
const fetchParentCategory = async (parentId) => {
  console.log('fetchParentCategory 接收到的 parentId:', parentId, '类型:', typeof parentId);
  
  if (!parentId || parentId === 'undefined' || parentId === 'null') {
    console.log('parentId 无效，跳过父分类查询');
    return null;
  }
  
  try {
    console.log('查询父分类，parentId:', parentId);
    const parentCategory = await fetchCategoryById(parentId);
    if (parentCategory) {
      console.log('父分类信息:', parentCategory);
      return {
        name: parentCategory.name || '',
        code: parentCategory.code || '',
        description: parentCategory.description || '',
        descriptionEn: parentCategory.descriptionEn || '',
        status: parentCategory.status ? '有效' : '失效'
      };
    }
    return null;
  } catch (error) {
    console.error('查询父分类失败:', error);
    return null;
  }
};

const fetchCategoryDetail = async (code) => {
  try {
    loading.value = true;
    console.log('Fetching category detail for code:', code);
    
    // 第一次：使用CateGet接口根据分类码查询分类信息（只传code）
    const response = await apiCateGet(1, 10, '', code, '');
    
    if (response && response.code === 1 && response.data && response.data.records && response.data.records.length > 0) {
      const category = response.data.records[0]; // 获取第一个匹配的分类
      console.log('获取到的分类信息:', category);
      console.log('分类的 parentId:', category.parentId, '类型:', typeof category.parentId);
      
      // 第二次：查询父分类信息（只传id）
      let parentInfo = null;
      if (category.parentId) {
        parentInfo = await fetchParentCategory(category.parentId);
      } else {
        console.log('分类没有父分类（parentId 为空）');
      }
      
      // 映射数据到表单
      categoryData.value = {
        categoryCode: category.code || '',
        categoryNameZh: category.name || '',
        categoryNameEn: category.nameEn || '',
        description: category.description || '',
        descriptionEn: category.descriptionEn || '',
        parentCategoryName: parentInfo ? parentInfo.name : '',
        parentCategoryCode: parentInfo ? parentInfo.code : '',
        parentCategoryDescription: parentInfo ? parentInfo.description : '',
        parentCategoryDescriptionEn: parentInfo ? parentInfo.descriptionEn : '',
        parentCategoryStatus: parentInfo ? parentInfo.status : '',
        isSyntheticDescription: category.status ? '有效' : '失效',
        attributes: category.extAttrs || []
      };
      
      console.log('Category data loaded:', categoryData.value);
    } else {
      ElMessage.error('未找到对应的分类信息');
      console.error('No category found for code:', code);
    }
  } catch (error) {
    console.error('获取分类详情失败:', error);
    ElMessage.error('获取分类详情失败');
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  const categoryCode = route.params.id;
  if (categoryCode) {
    fetchCategoryDetail(categoryCode);
  } else {
    ElMessage.error('分类码不能为空');
  }
});
</script>

<style scoped>
.category-detail-container {
  padding: 20px;
}

.detail-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-button {
  padding: 0;
  min-height: auto;
}

/* 可以根据需要添加更多样式 */
</style>