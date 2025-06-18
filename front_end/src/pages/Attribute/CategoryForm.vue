<template>
  <el-dialog :model-value="visible" :title="mode === 'create' ? '新建分类' : '编辑分类'" width="800px" @close="handleClose">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
      <!-- 分类基本信息 -->
      <el-collapse v-model="activeNames">
        <el-collapse-item title="分类基本信息" name="basic">
          <el-form-item label="分类编码" prop="code">
            <el-input v-model="formData.code" :disabled="mode === 'edit'"></el-input>
          </el-form-item>
          <el-form-item label="分类中文名称" prop="name">
            <el-input v-model="formData.name"></el-input>
          </el-form-item>
          <el-form-item label="分类英文名称" prop="nameEn">
            <el-input v-model="formData.nameEn"></el-input>
          </el-form-item>
          <el-form-item label="中文描述" prop="description">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              placeholder="请输入中文描述"
            />
          </el-form-item>
          <el-form-item label="英文描述" prop="descriptionEn">
            <el-input
              v-model="formData.descriptionEn"
              type="textarea"
              :rows="3"
              placeholder="请输入英文描述"
            />
          </el-form-item>
          <el-form-item label="分类状态" required>
            <el-select v-model="formData.status" placeholder="请选择分类状态">
              <el-option label="有效" :value="true"></el-option>
              <el-option label="失效" :value="false"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="父节点" prop="parentId">
            <el-select
              v-model="formData.parentId"
              filterable
              remote
              :remote-method="searchParentCategories"
              :loading="loading"
              placeholder="请输入父节点名称搜索"
              clearable
              @change="handleParentChange"
              :disabled="props.mode === 'edit'"
            >
              <el-option
                v-for="item in parentOptions"
                :key="item.id"
                :label="`${item.name}-${item.nameEn}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-collapse-item>

        <!-- 分类属性信息 - 只在编辑模式下显示 -->
        <el-collapse-item v-if="mode === 'edit'" title="分类属性信息" name="attributes">
          <div class="attribute-actions">
          <el-button type="primary" @click="addAttribute">添加属性</el-button>
          <el-button type="danger" @click="removeSelectedAttributes">删除选中属性</el-button>
          </div>
          
          <!-- 属性列表 -->
          <el-table 
            :data="formData.attributes" 
            style="width: 100%; margin-top: 10px;" 
            @selection-change="handleAttributeTableSelectionChange"
            v-loading="loading"
          >
             <el-table-column type="selection" width="55" />
             <el-table-column prop="nameZh" label="属性中文名称" />
             <el-table-column prop="nameEn" label="属性英文名称" />
             <el-table-column prop="dataType" label="数据类型" />
            <el-table-column prop="description" label="中文描述" show-overflow-tooltip />
            <el-table-column prop="descriptionEn" label="英文描述" show-overflow-tooltip />
             <el-table-column label="操作">
                <template #default="scope">
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="handleDeleteAttribute(scope.$index)"
                  :disabled="!scope.row.linkId"
                >
                  删除
                </el-button>
                </template>
             </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>

    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>

  <!-- 属性选择对话框 - 只在编辑模式下显示 -->
  <attribute-selection-dialog 
    v-if="mode === 'edit'"
    :visible="showAttributeSelection" 
    :attribute-list="attributeList"
    @close="showAttributeSelection = false" 
    @confirm="handleAttributeSelectConfirm"
    @search="handleAttributeSearch">
  </attribute-selection-dialog>

</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import AttributeSelectionDialog from './AttributeSelectionDialog.vue'; // 导入属性选择对话框组件
import { apiCateGet } from '@/api/CateGet';
import { apiCateCreate } from '@/api/CateCreate';
import { apiCateUpdate } from '@/api/CateUpdate';
import { apiCateQueryAttribute } from '@/api/CateQueryAttribute';
import { apiAttributeGet } from '@/api/attributeGet';
import { apiCateAddAttr } from '@/api/CateAddAttr';
import { apiCateAttrDel } from '@/api/CateAttrDel';
import { ElMessage, ElMessageBox } from 'element-plus';

const props = defineProps({
  visible: { type: Boolean, default: false },
  mode: { type: String, default: 'create' }, // 'create' or 'edit'
  initData: { type: Object, default: () => ({}) },
});

const emit = defineEmits(['close', 'submit']);

const formRef = ref(null);
const loading = ref(false);
const parentOptions = ref([]);
const attributeList = ref([]);

const formData = ref({
  code: '',
  name: '',
  nameEn: '',
  parentId: null,
  description: '',
  descriptionEn: '',
  status: true,
  attributes: []
});

const activeNames = ref(['basic']); // 默认只展开基本信息
// 新增：控制属性选择对话框显示
const showAttributeSelection = ref(false);
// 新增：属性表格选中项
const selectedAttributeTableRows = ref([]);

const rules = {
  code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入分类中文名', trigger: 'blur' }],
  nameEn: [{ required: true, message: '请输入分类英文名', trigger: 'blur' }]
};

// 加载分类属性
const loadCategoryAttributes = async (categoryId) => {
  if (!categoryId) return;
  try {
    loading.value = true;
    const response = await apiCateQueryAttribute(BigInt(categoryId));
    
    // 检查响应结构
    let attributes = [];
    if (response && response.data) {
      attributes = Array.isArray(response.data) ? response.data : [];
    } else if (Array.isArray(response)) {
      attributes = response;
    }
    
    // 将属性数据同步到表单数据中
    formData.value.attributes = attributes.map(attr => ({
      id: attr.id || '',
      linkId: attr.linkId || '',
      nameZh: attr.name || '',
      nameEn: attr.nameEn || '',
      dataType: attr.type || '',
      description: attr.description || '',
      descriptionEn: attr.descriptionEn || ''
    }));
  } catch (error) {
    console.error('获取分类属性失败:', error);
    ElMessage.error('获取分类属性失败');
    // 确保在错误时也初始化一个空数组
    formData.value.attributes = [];
  } finally {
    loading.value = false;
  }
};

// 监听编辑模式下的初始数据
watch(() => props.initData, (newVal) => {
  if (newVal) {
    formData.value = { 
      ...newVal,
      attributes: []
    };
    // 如果是编辑模式，加载分类属性并展开属性面板
    if (props.mode === 'edit') {
      loadCategoryAttributes(newVal.id);
      activeNames.value = ['basic', 'attributes'];
    }
  }
}, { immediate: true });

// 监听模式变化
watch(() => props.mode, (newMode) => {
  if (newMode === 'edit') {
    activeNames.value = ['basic', 'attributes'];
  } else {
    activeNames.value = ['basic'];
  }
}, { immediate: true });

const handleClose = () => {
  emit('close');
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res;
        if (props.mode === 'create') {
          // 创建请求数据的副本
          const requestData = {
            ...formData.value,
            // 将 parentId 转换为 bigint
            parentId: formData.value.parentId ? BigInt(formData.value.parentId).toString() : null,
            // 将属性列表中的 id 转换为 bigint
            attributes: formData.value.attributes?.map(attr => ({
              ...attr,
              id: BigInt(attr.id)
            })) || []
          };
          res = await apiCateCreate(requestData);
        } else {
          // 编辑模式
          res = await apiCateUpdate({
            id: BigInt(formData.value.id),
            name: formData.value.name,
            nameEn: formData.value.nameEn,
            description: formData.value.description,
            descriptionEn: formData.value.descriptionEn
          });
        }

        if (res.code === 1) {
          ElMessage.success(props.mode === 'create' ? '创建成功' : '更新成功');
  emit('submit', formData.value);
        } else {
          ElMessage.error(res.msg || (props.mode === 'create' ? '创建失败' : '更新失败'));
        }
      } catch (error) {
        console.error(props.mode === 'create' ? '创建分类失败:' : '更新分类失败:', error);
        ElMessage.error(props.mode === 'create' ? '创建分类失败' : '更新分类失败');
      }
    }
  });
};

// 获取所有属性
const loadAllAttributes = async (page = 1, pageSize = 10, attributeName = '') => {
  try {
    loading.value = true;
    const response = await apiAttributeGet(page, pageSize, attributeName);
    if (response && response.data && response.data.list) {
      // 映射属性数据
      attributeList.value = response.data.list.map(attr => ({
        id: attr.id,
        nameZh: attr.name,
        nameEn: attr.nameEn,
        dataType: attr.type,
        description: attr.description,
        descriptionEn: attr.descriptionEn,
        selected: formData.value.attributes.some(selectedAttr => selectedAttr.id === attr.id)
      }));
    }
  } catch (error) {
    console.error('获取属性列表失败:', error);
    ElMessage.error('获取属性列表失败');
  } finally {
    loading.value = false;
  }
};

// 处理属性搜索
const handleAttributeSearch = (keyword) => {
  loadAllAttributes(1, 10, keyword);
};

// 添加属性按钮点击事件
const addAttribute = async () => {
  // 只在编辑模式下允许添加属性
  if (props.mode !== 'edit') {
    ElMessage.warning('新建模式下不能添加属性');
    return;
  }
  
  try {
    loading.value = true;
    // 加载所有属性
    await loadAllAttributes();
    // 显示属性选择对话框
    showAttributeSelection.value = true;
  } catch (error) {
    console.error('加载属性失败:', error);
    ElMessage.error('加载属性失败');
  } finally {
    loading.value = false;
  }
};

// 属性选择确认
const handleAttributeSelectConfirm = async (selectedAttributes) => {
  try {
    loading.value = true;
    // 将选中的属性添加到表单数据中
    const newAttributes = selectedAttributes.map(attr => ({
      id: attr.id,
      nameZh: attr.nameZh,
      nameEn: attr.nameEn,
      dataType: attr.dataType,
      description: attr.description,
      descriptionEn: attr.descriptionEn
    }));

    // 检查是否有选中的属性
    if (newAttributes.length === 0) {
      ElMessage.warning('请选择要添加的属性');
      return;
    }
    
    console.log('选中的属性:', newAttributes); // 添加日志
    
    // 调用添加属性接口
    const response = await apiCateAddAttr({
      classificationId: BigInt(formData.value.id).toString(),
      sourceIds: newAttributes.map(attr => BigInt(attr.id).toString())
    });

    console.log('添加属性响应:', response); // 添加日志

    if (response.code === 1) {
      // 添加成功，更新本地数据
      formData.value.attributes = [
        ...formData.value.attributes,
        ...newAttributes.filter(newAttr => 
          !formData.value.attributes.some(existingAttr => existingAttr.id === newAttr.id)
        )
      ];
      ElMessage.success('添加属性成功');
    } else {
      ElMessage.error(response.msg || '添加属性失败');
    }
  } catch (error) {
    console.error('添加属性失败:', error);
    ElMessage.error('添加属性失败');
  } finally {
    loading.value = false;
    showAttributeSelection.value = false;
  }
};

const removeAttribute = (index) => {
   formData.value.attributes.splice(index, 1);
};

// 新增：处理属性表格选中变化
const handleAttributeTableSelectionChange = (selection) => {
   selectedAttributeTableRows.value = selection;
};

// 新增：删除属性表格中选中的属性
const removeSelectedAttributes = () => {
   // 从 formData.value.attributes 中移除 selectedAttributeTableRows 中包含的属性
   const selectedIds = new Set(selectedAttributeTableRows.value.map(attr => attr.id));
   formData.value.attributes = formData.value.attributes.filter(attr => !selectedIds.has(attr.id));
   selectedAttributeTableRows.value = []; // 清空选中项
};

// 搜索父节点分类
const searchParentCategories = async (query) => {
  if (query) {
    loading.value = true;
    try {
      const res = await apiCateGet(1, 20, query, '', '');
      if (res.code === 1) {
        parentOptions.value = res.data.records;
      } else {
        ElMessage.error(res.msg || '获取父节点列表失败');
      }
    } catch (error) {
      console.error('获取父节点列表失败:', error);
      ElMessage.error('获取父节点列表失败');
    } finally {
      loading.value = false;
    }
  } else {
    parentOptions.value = [];
  }
};

// 处理父节点变化
const handleParentChange = (value) => {
  if (value) {
    const selectedParent = parentOptions.value.find(item => item.id === value);
    if (selectedParent) {
      // 可以在这里添加一些额外的逻辑，比如设置默认值等
      console.log('选中的父节点:', selectedParent);
    }
  }
};

// 删除属性
const handleDeleteAttribute = async (index) => {
  try {
    const attribute = formData.value.attributes[index];
    if (!attribute.linkId) {
      ElMessage.error('属性linkId不存在，无法删除');
      return;
    }

    await ElMessageBox.confirm('确定要删除该属性吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const response = await apiCateAttrDel({
      ids: [BigInt(attribute.linkId).toString()]
    });
    
    if (response.code === 1) {
      ElMessage.success('删除成功');
      // 从表单数据中移除该属性
      formData.value.attributes.splice(index, 1);
    } else {
      ElMessage.error(response.msg || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除属性失败:', error);
      ElMessage.error('删除属性失败');
    }
  }
};

onMounted(() => {
  if (props.mode === 'edit' && props.initData) {
    formData.value = { ...props.initData };
  }
});

</script>

<style scoped>
/* 可以添加样式 */
.el-select {
  width: 100%;
}
.el-collapse-item {
  margin-bottom: 20px;
}
.attribute-actions {
  margin-bottom: 10px;
}
</style> 