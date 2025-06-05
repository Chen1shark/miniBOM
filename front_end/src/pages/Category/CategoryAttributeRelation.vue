<template>
  <div class="category-attribute-relation">
    <h3>查看所有属性和分类的关系</h3>
    <el-table :data="attributeRelationData" style="width: 100%;" border>
      <!-- 属性列 -->
      <el-table-column prop="attributeNameZh" label="属性名称 (中文)" width="180"></el-table-column>
      <el-table-column prop="attributeNameEn" label="属性名称 (英文)" width="180"></el-table-column>

      <!-- 动态生成分类列 -->
      <el-table-column v-for="category in categories" :key="category.categoryCode" :label="category.categoryCode + ' ' + category.categoryNameZh" width="100" align="center">
        <template #default="scope">
          <!-- 使用勾选框表示关联关系 -->
          <el-checkbox :model-value="isAttributeRelatedToCategory(scope.row, category)" disabled></el-checkbox>
        </template>
      </el-table-column>
    </el-table>
     <!-- TODO: 可能需要分页 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  attributeRelationData: { // 属性与分类关系数据
    type: Array,
    default: () => [],
  },
  categories: { // 用于表格顶部的分类数据
    type: Array,
    default: () => [],
  },
   filter: { // 筛选条件 (来自树节点点击)
      type: Object,
      default: null,
   }
});

// const attributeRelationData = ref([]); // 移除内部状态
// const categoriesForRelation = ref([]); // 移除内部状态

// 移除数据获取方法
// const fetchRelationData = () => {
//   // TODO: 调用接口获取属性与分类的关系数据
//   console.log('Fetching category attribute relation data');
//   // 模拟数据
//   // attributeRelationData 结构: [{ attributeId: 1, attributeNameZh: '属性1', attributeNameEn: 'Attribute1', relatedCategoryIds: ['categoryCode1', 'categoryCode2'] }, ...]
//   // categoriesForRelation 结构: [{ categoryCode: 'categoryCode1', categoryNameZh: '分类1' }, ...]

//   categoriesForRelation.value = [
//     { categoryCode: '0529A', categoryNameZh: '分类A' },
//     { categoryCode: '0529B', categoryNameZh: '分类B' },
//     { categoryCode: '2306', categoryNameZh: '分类C' },
//     { categoryCode: '2305', categoryNameZh: '分类D' },
//     { categoryCode: '2304', categoryNameZh: '分类E' },
//     { categoryCode: '2303', categoryNameZh: '分类F' },
//     { categoryCode: '2302', categoryNameZh: '分类G' },
//     { categoryCode: '2301', categoryNameZh: '分类H' },
//     { categoryCode: '2307', categoryNameZh: '分类I' },
//     { categoryCode: '0528D', categoryNameZh: '分类J' },
//   ];

//   attributeRelationData.value = [
//     {
//       attributeId: 1,
//       attributeNameZh: 'License销售政策',
//       attributeNameEn: 'License Sales Policy',
//       relatedCategoryCodes: ['0529A', '0529B'],
//     },
//     {
//       attributeId: 2,
//       attributeNameZh: '介质供货方式',
//       attributeNameEn: 'Medium Supply Method',
//       relatedCategoryCodes: ['0529A', '0529B'],
//     },
//      {
//       attributeId: 3,
//       attributeNameZh: '上行接口类型',
//       attributeNameEn: 'Uplink Interface Type',
//       relatedCategoryCodes: [],
//     },
//       {
//       attributeId: 4,
//       attributeNameZh: '备选声明方式',
//       attributeNameEn: 'Alternative Declaration Method',
//       relatedCategoryCodes: ['2304'],
//     },
//      {
//       attributeId: 5,
//       attributeNameZh: '接口模式',
//       attributeNameEn: 'Interface Mode',
//       relatedCategoryCodes: [],
//     },
//      {
//       attributeId: 6,
//       attributeNameZh: '耐压水平',
//       attributeNameEn: 'Withstand Voltage Level',
//       relatedCategoryCodes: [],
//     },
//       {
//       attributeId: 7,
//       attributeNameZh: '线缆导体截面积',
//       attributeNameEn: 'Cable Conductor Cross-sectional Area',
//       relatedCategoryCodes: ['2307'],
//     },
//      {
//       attributeId: 8,
//       attributeNameZh: '一级漏铜响应时间',
//       attributeNameEn: 'Primary Copper Leakage Response Time',
//       relatedCategoryCodes: ['0529A'],
//     },
//       {
//       attributeId: 9,
//       attributeNameZh: '微球种类',
//       attributeNameEn: 'Microsphere Type',
//       relatedCategoryCodes: [],
//     },
//     {
//       attributeId: 10,
//       attributeNameZh: 'Cover Lens材质',
//       attributeNameEn: 'Cover Lens Material',
//       relatedCategoryCodes: [],
//     },
//     {
//       attributeId: 11,
//       attributeNameZh: '氧指数',
//       attributeNameEn: 'Oxygen Index',
//       relatedCategoryCodes: ['2303'],
//     },
//     {
//       attributeId: 12,
//       attributeNameZh: '应用产品',
//       attributeNameEn: 'Applied Product',
//       relatedCategoryCodes: [],
//     },
//      {
//       attributeId: 13,
//       attributeNameZh: '电池柜(架)信息',
//       attributeNameEn: 'Battery Cabinet (Rack) Information',
//       relatedCategoryCodes: ['0528D'],
//     },
//   ];
// };

const isAttributeRelatedToCategory = (attribute, category) => {
  return attribute.relatedCategoryCodes.includes(category.categoryCode);
};

// 移除 onMounted 钩子
// onMounted(() => {
//   fetchRelationData();
// });
</script>

<style scoped>
.category-attribute-relation {
  padding: 20px;
}
</style> 