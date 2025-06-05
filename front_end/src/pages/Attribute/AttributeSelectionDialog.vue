<template>
  <el-dialog title="选择属性" :model-value="visible" width="800px" @close="handleClose">
    <div class="attribute-selection-dialog">
      <div class="search-area">
         <el-input v-model="searchAttribute" placeholder="搜索属性中文名/英文名" clearable @keyup.enter="fetchAttributes"></el-input>
         <el-button type="primary" @click="fetchAttributes">搜索</el-button>
      </div>

      <el-table :data="attributeList" style="width: 100%; margin-top: 10px;" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="nameZh" label="属性中文名称" width="180" />
        <el-table-column prop="nameEn" label="属性英文名称" width="180" />
        <el-table-column prop="dataType" label="数据类型" width="100" />
         <el-table-column prop="descZh" label="属性中文描述" show-overflow-tooltip />
      </el-table>
       <!-- TODO: 添加分页 -->

    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  // initSelectedAttributes: { type: Array, default: () => [] }, // 可能需要传入已选属性
});

const emit = defineEmits(['close', 'confirm']);

const searchAttribute = ref('');
const attributeList = ref([]); // 可选属性列表
const selectedAttributes = ref([]); // 当前选中的属性

// 模拟获取属性列表的方法
const fetchAttributes = () => {
  console.log('Fetching attributes with search:', searchAttribute.value);
  // TODO: 调用接口获取属性列表数据，可能需要分页和搜索参数
  // 模拟数据
  attributeList.value = [
    {
      id: 1,
      nameZh: '产品型号',
      nameEn: 'Product_Model',
      dataType: '字符串型',
      descZh: '这是一个产品型号的描述信息',
    },
    {
      id: 2,
      nameZh: '产品重量',
      nameEn: 'Product_Weight',
      dataType: '实数型',
      descZh: '产品的重量信息',
    },
    {
      id: 3,
      nameZh: '产品尺寸',
      nameEn: 'Product_Size',
      dataType: '字符串型',
      descZh: '产品的尺寸信息',
    },
     {
      id: 4,
      nameZh: '颜色',
      nameEn: 'Color',
      dataType: '字符串型',
      descZh: '产品的颜色',
    },
     {
      id: 5,
      nameZh: '材质',
      nameEn: 'Material',
      dataType: '字符串型',
      descZh: '产品的材质',
    },
  ];
};

const handleSelectionChange = (selection) => {
  selectedAttributes.value = selection;
};

const handleClose = () => {
  emit('close');
};

const handleConfirm = () => {
  emit('confirm', selectedAttributes.value);
};

// 当对话框可见时加载属性列表
watch(() => props.visible, (newVal) => {
  if (newVal) {
    fetchAttributes();
     selectedAttributes.value = []; // 打开对话框时清空之前的选择
     searchAttribute.value = ''; // 清空搜索框
  }
});

// onMounted(() => {
//   fetchAttributes(); // 可以在这里初始加载，也可以等 visible 为 true 时再加载
// });
</script>

<style scoped>
.attribute-selection-dialog {
  padding: 0 20px 20px 20px; /* 调整内边距 */
}

.search-area {
   display: flex;
   gap: 10px; /* 搜索框和按钮之间的间距 */
}
</style> 