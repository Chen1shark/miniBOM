<template>
  <div class="category-query-all-container">
    <el-card class="query-card">
      <div class="query-header">
         <el-checkbox v-model="showTreeView">查看分类树状图</el-checkbox>
         <el-checkbox v-model="showAllRelation" @change="handleShowAllRelationChange">查看所有属性和分类的关系</el-checkbox>
      </div>

      <div class="content-area">
         <div class="tree-view-container" v-if="showTreeView">
            <category-tree-view :treeData="categoryTreeData" @node-click="handleTreeNodeClick"></category-tree-view>
         </div>
         <div class="relation-table-container" :class="{'full-width': !showTreeView}">
            <category-attribute-relation 
               :attributeRelationData="filteredAttributeRelationData" 
               :categories="categoriesForRelationTable">
            </category-attribute-relation>
         </div>
      </div>

    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import CategoryTreeView from './CategoryTreeView.vue';
import CategoryAttributeRelation from './CategoryAttributeRelation.vue';

const showTreeView = ref(true); // 控制分类树状图的显示/隐藏
const relationFilter = ref(null); // 用于筛选右侧属性关系表格
const categoryTreeData = ref([]);
const attributeCategoryRelationData = ref([]);
const showAllRelation = ref(true); // 控制显示全部关系还是筛选后的关系，默认为全部

// 新增：用于属性关系表格列头的分类列表
const categoriesForRelationTable = ref([]);

const fetchCategoryTreeData = () => {
  // TODO: 调用接口获取分类树状数据
  console.log('Fetching category tree data in CateHome');
   // 模拟数据 (与 CategoryTreeView 中一致)
   categoryTreeData.value = [
    {
      id: 1,
      categoryCode: '01',
      categoryNameZh: '智慧环境生活',
      children: [
        {
          id: 2,
          categoryCode: '0101',
          categoryNameZh: '家居',
        },
      ],
    },
    {
      id: 3,
      categoryCode: '02',
      categoryNameZh: '装备',
    },
     {
      id: 4,
      categoryCode: '03',
      categoryNameZh: '汽车技术',
    },
     {
      id: 5,
      categoryCode: '04',
      categoryNameZh: '机电部件',
      children: [
        {
           id: 6,
           categoryCode: '0401',
           categoryNameZh: '结构部件',
           children: [
             {
               id: 7,
               categoryCode: '040101',
               categoryNameZh: '结构件零部件、附件',
             },
             {
                id: 8,
                categoryCode: '040102',
                categoryNameZh: '结构件组件',
             }
           ]
        },
         {
           id: 9,
           categoryCode: '0402',
           categoryNameZh: '连接器和附件',
           children: [
             {
               id: 10,
               categoryCode: '040201',
               categoryNameZh: '连接器',
             },
             {
                id: 11,
                categoryCode: '040202',
                categoryNameZh: '连接器附件',
             }
           ]
        },
          {
            id: 12,
            categoryCode: '0403',
            categoryNameZh: '线缆和附件',
          },
      ]
    },
  ];
};

const fetchAttributeCategoryRelationData = () => {
   // TODO: 调用接口获取属性与分类的关系数据
   console.log('Fetching attribute category relation data in CateHome');
   // 模拟数据 (与 CategoryAttributeRelation 中一致)
   attributeCategoryRelationData.value = [
    {
      attributeId: 1,
      attributeNameZh: 'License销售政策',
      attributeNameEn: 'License Sales Policy',
      relatedCategoryCodes: ['0529A', '0529B'],
    },
    {
      attributeId: 2,
      attributeNameZh: '介质供货方式',
      attributeNameEn: 'Medium Supply Method',
      relatedCategoryCodes: ['0529A', '0529B'],
    },
     {
      attributeId: 3,
      attributeNameZh: '上行接口类型',
      attributeNameEn: 'Uplink Interface Type',
      relatedCategoryCodes: [],
    },
      {
      attributeId: 4,
      attributeNameZh: '备选声明方式',
      attributeNameEn: 'Alternative Declaration Method',
      relatedCategoryCodes: ['2304'],
    },
     {
      attributeId: 5,
      attributeNameZh: '接口模式',
      attributeNameEn: 'Interface Mode',
      relatedCategoryCodes: [],
    },
     {
      attributeId: 6,
      attributeNameZh: '耐压水平',
      attributeNameEn: 'Withstand Voltage Level',
      relatedCategoryCodes: [],
    },
      {
      attributeId: 7,
      attributeNameZh: '线缆导体截面积',
      attributeNameEn: 'Cable Conductor Cross-sectional Area',
      relatedCategoryCodes: ['2307'],
    },
     {
      attributeId: 8,
      attributeNameZh: '一级漏铜响应时间',
      attributeNameEn: 'Primary Copper Leakage Response Time',
      relatedCategoryCodes: ['0529A'],
    },
      {
      attributeId: 9,
      attributeNameZh: '微球种类',
      attributeNameEn: 'Microsphere Type',
      relatedCategoryCodes: [],
    },
    {
      attributeId: 10,
      attributeNameZh: 'Cover Lens材质',
      attributeNameEn: 'Cover Lens Material',
      relatedCategoryCodes: [],
    },
    {
      attributeId: 11,
      attributeNameZh: '氧指数',
      attributeNameEn: 'Oxygen Index',
      relatedCategoryCodes: ['2303'],
    },
    {
      attributeId: 12,
      attributeNameZh: '应用产品',
      attributeNameEn: 'Applied Product',
      relatedCategoryCodes: [],
    },
     {
      attributeId: 13,
      attributeNameZh: '电池柜(架)信息',
      attributeNameEn: 'Battery Cabinet (Rack) Information',
      relatedCategoryCodes: ['0528D'],
    },
  ];

    // 从模拟数据中提取分类列表用于表格列头
    const uniqueCategoryCodes = new Set();
    attributeCategoryRelationData.value.forEach(attr => {
        attr.relatedCategoryCodes.forEach(code => uniqueCategoryCodes.add(code));
    });
    // 模拟获取分类名称，实际应根据 categoryCode 调用接口获取
    categoriesForRelationTable.value = Array.from(uniqueCategoryCodes).map(code => ({
        categoryCode: code,
        categoryNameZh: `分类${code}` // 模拟中文名称
    }));
    // 排序分类，以便表格列顺序一致
    categoriesForRelationTable.value.sort((a, b) => a.categoryCode.localeCompare(b.categoryCode));
};

const filteredAttributeRelationData = computed(() => {
  if (showAllRelation.value) {
    return attributeCategoryRelationData.value;
  } else if (relationFilter.value && relationFilter.value.categoryCode) {
    // 根据 relationFilter (点击的树节点) 过滤 attributeCategoryRelationData
    const filterCode = relationFilter.value.categoryCode;
    // 过滤属性，只保留与选中分类相关的属性
    return attributeCategoryRelationData.value.filter(attr =>
      attr.relatedCategoryCodes.includes(filterCode)
    );
  } else {
    // 当 showAllRelation 为 false 且没有选中的树节点时，可以显示空或者全部，这里选择显示空
    return [];
  }
});

const handleTreeNodeClick = (data) => {
   console.log('Tree node clicked:', data);
   // 根据点击的树节点筛选右侧的属性关系表格
   relationFilter.value = data; // 示例：将点击的数据作为筛选条件传递给右侧组件
   showAllRelation.value = false; // 切换到筛选模式
};

// 处理"查看所有属性和分类的关系"勾选框变化
const handleShowAllRelationChange = (value) => {
   if (value) {
      relationFilter.value = null; // 如果显示全部，则清除筛选条件
   } else {
       // 如果取消显示全部，但有选中的树节点，则应用树节点的筛选
       // 如果没有选中的树节点，保持 relationFilter 为 null，filteredAttributeRelationData 会显示空
   }
};

onMounted(() => {
   fetchCategoryTreeData();
   fetchAttributeCategoryRelationData();
});

</script>

<style scoped>
.category-query-all-container {
  padding: 20px;
}

.query-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.query-header {
  margin-bottom: 20px;
  display: flex; /* 使用 flex 布局 */
  align-items: center; /* 垂直居中 */
}

.content-area {
  display: flex;
}

.tree-view-container {
  width: 300px; /* 树状图区域固定宽度 */
  margin-right: 20px;
  flex-shrink: 0; /* 防止树状图区域缩小 */
}

.relation-table-container {
  flex-grow: 1; /* 属性关系表格区域填充剩余空间 */
}

.relation-table-container.full-width {
    flex-grow: 1; /* 或者设置 width: 100%; */
}

/* 可以添加更多样式 */
</style>
  