<template>
  <div class="category-detail-container">
    <el-card class="detail-card">
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
              <el-input v-model="categoryData.description"></el-input>
            </el-form-item>
            <el-form-item label="分类父节点">
              <el-input v-model="categoryData.parentCategoryName"></el-input>
            </el-form-item>
            <el-form-item label="部件类型">
              <el-input v-model="categoryData.partType"></el-input>
            </el-form-item>
            <el-form-item label="分类名是否合成部件描述">
               <el-input v-model="categoryData.isSyntheticDescription"></el-input>
            </el-form-item>
             <el-form-item label="英文名称">
              <el-input v-model="categoryData.categoryNameEn"></el-input>
            </el-form-item>
             <el-form-item label="英文描述">
              <el-input v-model="categoryData.descriptionEn"></el-input>
            </el-form-item>
            <el-form-item label="是否MDA结构件小类">
               <el-input v-model="categoryData.isMdaStructure"></el-input>
            </el-form-item>
             <el-form-item label="默认部件类型">
               <el-input v-model="categoryData.defaultPartType"></el-input>
            </el-form-item>
             <el-form-item label="参考备注">
               <el-input v-model="categoryData.remarks" type="textarea"></el-input>
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

const route = useRoute();
const router = useRouter();

const categoryData = ref({});
const activeNames = ref(['basic', 'attributes']); // 默认展开所有部分

const fetchCategoryDetail = (id) => {
  // TODO: 调用接口根据ID获取分类详情数据
  console.log('Fetching category detail for ID:', id);
  // 模拟数据
  categoryData.value = {
    categoryCode: '0301',
    categoryNameZh: '印制板',
    description: '印制板描述',
    parentCategoryName: '电子元器件-印制电路、集成电路和微型组装件-印制电路',
    partType: '单板/整件/邮件/母板/整机(模型清单)/采购件',
    isSyntheticDescription: '是',
    categoryNameEn: 'PCB',
    descriptionEn: 'PCB description',
    isMdaStructure: '否',
    defaultPartType: '单板',
    remarks: '参考备注内容',
    attributes: [
      {
        serialNumber: 10,
        nameZh: '小类代码',
        nameEn: 'Category_Code',
        descriptionZh: '',
        dataType: '字符串定义',
        unit: '',
        allowedValues: '',
        defaultValue: '0301',
        isRequired: '是',
        isSyntheticDescription: '是',
        isMultipleValues: '否',
      },
       {
        serialNumber: 20,
        nameZh: '机型',
        nameEn: 'Equipment_Type',
        descriptionZh: '',
        dataType: '字符串定义',
        unit: '',
        allowedValues: '',
        defaultValue: 'Default',
        isRequired: '是',
        isSyntheticDescription: '是',
        isMultipleValues: '否',
      },
       {
        serialNumber: 30,
        nameZh: 'PART类型',
        nameEn: 'Part_Type',
        descriptionZh: '',
        dataType: '字符串定义',
        unit: '',
        allowedValues: '单板整件/部件母板整...',
        defaultValue: '单核',
        isRequired: '是',
        isSyntheticDescription: '是',
        isMultipleValues: '否',
      },
        {
        serialNumber: 40,
        nameZh: '板类型',
        nameEn: 'Board_Type',
        descriptionZh: '',
        dataType: '字符串定义',
        unit: '',
        allowedValues: 'TRX单板背板电源单...',
        defaultValue: '数字板',
        isRequired: '是',
        isSyntheticDescription: '是',
        isMultipleValues: '否',
      },
    ]
  };
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  const categoryId = route.params.id;
  if (categoryId) {
    fetchCategoryDetail(categoryId);
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