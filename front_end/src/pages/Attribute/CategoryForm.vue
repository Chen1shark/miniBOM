<template>
  <el-dialog :model-value="visible" :title="mode === 'create' ? '新建分类' : '编辑分类'" width="600px" @close="handleClose">
    <el-form :model="formData" label-width="120px">
      <!-- 分类基本信息 -->
      <el-collapse v-model="activeNames">
        <el-collapse-item title="分类基本信息" name="basic">
          <el-form-item label="分类编码" required>
            <el-input v-model="formData.categoryCode" :disabled="mode === 'edit'" placeholder="请输入分类编码"></el-input>
          </el-form-item>
          <el-form-item label="分类中文名称" required>
            <el-input v-model="formData.categoryNameZh" placeholder="请输入分类中文名称"></el-input>
          </el-form-item>
          <el-form-item label="分类英文名称" required>
            <el-input v-model="formData.categoryNameEn" placeholder="请输入分类英文名称"></el-input>
          </el-form-item>
          <el-form-item label="分类描述">
            <el-input v-model="formData.description" type="textarea" placeholder="请输入分类描述"></el-input>
          </el-form-item>
          <el-form-item label="分类状态" required>
            <el-select v-model="formData.status" placeholder="请选择分类状态">
              <el-option label="有效" value="valid"></el-option>
              <el-option label="失效" value="invalid"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="父节点">
            <el-input v-model="formData.parentNode" placeholder="请输入父节点"></el-input>
             <!-- TODO: 可能需要一个树形选择器 -->
          </el-form-item>
        </el-collapse-item>

        <!-- 属性信息 -->
        <el-collapse-item title="属性信息" name="attributes">
           <!-- TODO: 实现属性关联功能 -->
          <el-button type="primary" @click="addAttribute">添加属性</el-button>
          <el-button type="danger" @click="removeSelectedAttributes">删除选中属性</el-button>
          <el-table :data="formData.attributes" style="width: 100%; margin-top: 10px;" @selection-change="handleAttributeTableSelectionChange">
             <el-table-column type="selection" width="55" />
             <el-table-column prop="nameZh" label="属性中文名称" />
             <el-table-column prop="nameEn" label="属性英文名称" />
             <el-table-column prop="dataType" label="数据类型" />
             <el-table-column label="操作">
                <template #default="scope">
                   <el-button size="small" type="danger" @click="removeAttribute(scope.$index)">移除</el-button>
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

  <!-- 属性选择对话框 -->
  <attribute-selection-dialog 
    :visible="showAttributeSelection" 
    @close="showAttributeSelection = false" 
    @confirm="handleAttributeSelectConfirm">
  </attribute-selection-dialog>

</template>

<script setup>
import { ref, watch } from 'vue';
import AttributeSelectionDialog from './AttributeSelectionDialog.vue'; // 导入属性选择对话框组件

const props = defineProps({
  visible: { type: Boolean, default: false },
  mode: { type: String, default: 'create' }, // 'create' or 'edit'
  initData: { type: Object, default: () => ({}) },
});

const emit = defineEmits(['close', 'submit']);

const formData = ref({});
const activeNames = ref(['basic', 'attributes']); // 默认展开基本信息和属性信息
// 新增：控制属性选择对话框显示
const showAttributeSelection = ref(false);
// 新增：属性表格选中项
const selectedAttributeTableRows = ref([]);

watch(() => props.initData, (newVal) => {
  formData.value = { ...newVal, attributes: newVal.attributes || [] };
}, { immediate: true });

const handleClose = () => {
  emit('close');
};

const handleSubmit = () => {
  // TODO: 表单验证
  emit('submit', formData.value);
};

const addAttribute = () => {
   // TODO: 实现添加属性的逻辑，可能需要一个弹窗选择现有属性
   console.log('打开属性选择对话框');
   showAttributeSelection.value = true; // 打开属性选择对话框
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

// 新增：处理属性选择对话框确认事件
const handleAttributeSelectConfirm = (selectedAttrs) => {
  console.log('选中的属性:', selectedAttrs);
  // 将选中的属性添加到当前分类的属性列表中，避免重复添加
  const existingAttributeIds = new Set(formData.value.attributes.map(attr => attr.id));
  const newAttributes = selectedAttrs.filter(attr => !existingAttributeIds.has(attr.id));
  formData.value.attributes.push(...newAttributes);
  showAttributeSelection.value = false; // 关闭属性选择对话框
};

</script>

<style scoped>
/* 可以添加样式 */
</style> 