import { defineStore } from 'pinia'
import {ref} from 'vue'

export const useStore = defineStore('store',()=>{

  const tdForPart = ref([]);
  tdForPart.value=[
    {
      id: 1,
      partCode: "CP-1001",
      partName: "主控板",
      version: "V2.3.1",
      assemblyMode: "螺丝固定",
      categoryCode: "EL-001",
      source: "内部生产",
      weight: 0.45,
      height: 5.2,
      width: 12.5,
      length: 18.0
    },
    {
      id: 2,
      partCode: "CP-1002",
      partName: "显示屏",
      version: "V1.2.0",
      assemblyMode: "卡扣安装",
      categoryCode: "DP-002",
      source: "外购",
      weight: 0.32,
      height: 2.8,
      width: 15.6,
      length: 24.5
    },
    {
      id: 3,
      partCode: "CP-1003",
      partName: "电池组",
      version: "V3.1.5",
      assemblyMode: "插槽安装",
      categoryCode: "BT-003",
      source: "外购",
      weight: 1.25,
      height: 8.0,
      width: 6.5,
      length: 9.5
    },
    {
      id: 4,
      partCode: "CP-1004",
      partName: "外壳上盖",
      version: "V1.0.0",
      assemblyMode: "卡扣固定",
      categoryCode: "CS-004",
      source: "内部生产",
      weight: 0.78,
      height: 3.5,
      width: 16.0,
      length: 26.0
    },
    {
      id: 5,
      partCode: "CP-1005",
      partName: "散热片",
      version: "V2.1.3",
      assemblyMode: "导热胶粘贴",
      categoryCode: "HS-005",
      source: "外购",
      weight: 0.15,
      height: 1.2,
      width: 8.0,
      length: 10.0
    },
    {
      id: 6,
      partCode: "CP-1006",
      partName: "电源接口",
      version: "V1.5.2",
      assemblyMode: "焊接",
      categoryCode: "PI-006",
      source: "外购",
      weight: 0.08,
      height: 1.5,
      width: 3.0,
      length: 4.5
    },
    {
      id: 7,
      partCode: "CP-1007",
      partName: "按键组",
      version: "V1.0.1",
      assemblyMode: "卡扣安装",
      categoryCode: "BT-007",
      source: "内部生产",
      weight: 0.12,
      height: 2.0,
      width: 5.0,
      length: 8.0
    },
    {
      id: 8,
      partCode: "CP-1008",
      partName: "摄像头模块",
      version: "V2.2.0",
      assemblyMode: "螺丝固定",
      categoryCode: "CM-008",
      source: "外购",
      weight: 0.25,
      height: 3.0,
      width: 3.5,
      length: 4.0
    },
    {
      id: 9,
      partCode: "CP-1009",
      partName: "扬声器",
      version: "V1.3.4",
      assemblyMode: "胶水固定",
      categoryCode: "SP-009",
      source: "外购",
      weight: 0.18,
      height: 2.5,
      width: 4.0,
      length: 4.0
    },
    {
      id: 10,
      partCode: "CP-1010",
      partName: "主板支架",
      version: "V1.0.0",
      assemblyMode: "螺丝固定",
      categoryCode: "BR-010",
      source: "内部生产",
      weight: 0.35,
      height: 2.0,
      width: 11.0,
      length: 15.0
    },
    {
      id: 11,
      partCode: "CP-1011",
      partName: "天线模块",
      version: "V1.8.2",
      assemblyMode: "焊接",
      categoryCode: "AN-011",
      source: "外购",
      weight: 0.05,
      height: 0.8,
      width: 2.5,
      length: 6.0
    },
    {
      id: 12,
      partCode: "CP-1012",
      partName: "外壳底盖",
      version: "V1.0.0",
      assemblyMode: "卡扣固定",
      categoryCode: "CS-012",
      source: "内部生产",
      weight: 0.82,
      height: 3.5,
      width: 16.0,
      length: 26.0
    },
    {
      id: 13,
      partCode: "CP-1013",
      partName: "风扇组件",
      version: "V2.0.5",
      assemblyMode: "螺丝固定",
      categoryCode: "FN-013",
      source: "外购",
      weight: 0.28,
      height: 3.0,
      width: 5.0,
      length: 5.0
    },
    {
      id: 14,
      partCode: "CP-1014",
      partName: "接口面板",
      version: "V1.2.3",
      assemblyMode: "螺丝固定",
      categoryCode: "IP-014",
      source: "内部生产",
      weight: 0.22,
      height: 2.5,
      width: 8.0,
      length: 12.0
    },
    {
      id: 15,
      partCode: "CP-1015",
      partName: "LED指示灯",
      version: "V1.0.2",
      assemblyMode: "焊接",
      categoryCode: "LD-015",
      source: "外购",
      weight: 0.03,
      height: 0.5,
      width: 0.5,
      length: 1.0
    }
  ];//用于part&BoM的测试数据

  return{tdForPart};

  
})
