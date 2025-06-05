<template>
  <div class="category-tree-view">
    <h3>分类树状图</h3>
    <el-tree :data="treeData" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  treeData: { // 定义 treeData prop
    type: Array,
    default: () => [],
  },
});

// 声明 emits
const emit = defineEmits(['node-click']);

// const treeData = ref([]); // 移除内部状态
const defaultProps = {
  children: 'children',
  label: 'categoryNameZh', // 假设以中文名称作为节点标签
};

// 移除数据获取方法
// const fetchCategoryTree = () => {
//   // TODO: 调用接口获取分类树状数据
//   console.log('Fetching category tree data');
//   // 模拟数据
//   treeData.value = [
//     {
//       id: 1,
//       categoryCode: '01',
//       categoryNameZh: '智慧环境生活',
//       children: [
//         {
//           id: 2,
//           categoryCode: '0101',
//           categoryNameZh: '家居',
//         },
//       ],
//     },
//     {
//       id: 3,
//       categoryCode: '02',
//       categoryNameZh: '装备',
//     },
//      {
//       id: 4,
//       categoryCode: '03',
//       categoryNameZh: '汽车技术',
//     },
//      {
//       id: 5,
//       categoryCode: '04',
//       categoryNameZh: '机电部件',
//       children: [
//         {
//            id: 6,
//            categoryCode: '0401',
//            categoryNameZh: '结构部件',
//            children: [
//              {
//                id: 7,
//                categoryCode: '040101',
//                categoryNameZh: '结构件零部件、附件',
//              },
//              {
//                 id: 8,
//                 categoryCode: '040102',
//                 categoryNameZh: '结构件组件',
//              }
//            ]
//         },
//          {
//            id: 9,
//            categoryCode: '0402',
//            categoryNameZh: '连接器和附件',
//            children: [
//              {
//                id: 10,
//                categoryCode: '040201',
//                categoryNameZh: '连接器',
//              },
//              {
//                id: 11,
//                categoryCode: '040202',
//                categoryNameZh: '连接器附件',
//              }
//            ]
//         },
//           {
//             id: 12,
//             categoryCode: '0403',
//             categoryNameZh: '线缆和附件',
//           },
//       ]
//     },
//   ];
// };

const handleNodeClick = (data) => {
  console.log('Node clicked:', data);
  // TODO: 处理节点点击事件，可能用于筛选右侧属性关系表格或跳转到分类详情
  // 如果需要在 CateHome 中处理点击事件，这里需要 emit 一个事件
  emit('node-click', data); // 触发 node-click 事件并传递节点数据
};

// 移除 onMounted 钩子
// onMounted(() => {
//   fetchCategoryTree();
// });
</script>

<style scoped>
.category-tree-view {
  padding: 20px;
  border-right: 1px solid #eee; /* 添加分隔线 */
}
</style> 