// 模拟零件BOM数据
const generateMockData = () => {
  const data = []
  const categories = ['EL', 'DP', 'BT', 'CS', 'HS', 'PI', 'CM', 'SP', 'BR', 'AN']
  const sources = ['内部生产', '外购']
  const assemblyModes = ['螺丝固定', '卡扣安装', '插槽安装', '卡扣固定', '导热胶粘贴', '焊接', '胶水固定']

  for (let i = 1; i <= 100; i++) {
    const categoryIndex = Math.floor(Math.random() * categories.length)
    const sourceIndex = Math.floor(Math.random() * sources.length)
    const assemblyModeIndex = Math.floor(Math.random() * assemblyModes.length)
    
    data.push({
      id: i,
      partCode: `CP-${1000 + i}`,
      partName: `部件${i}`,
      version: `V${Math.floor(Math.random() * 3) + 1}.${Math.floor(Math.random() * 5)}.${Math.floor(Math.random() * 10)}`,
      categoryCode: assemblyModes[assemblyModeIndex],
      source: `${categories[categoryIndex]}-${String(i).padStart(3, '0')}`,
      weight: (Math.random() * 2).toFixed(2),
      height: (Math.random() * 10).toFixed(1),
      width: (Math.random() * 20).toFixed(1),
      length: (Math.random() * 30).toFixed(1)
    })
  }
  return data
}

// 模拟API响应
const mockData = generateMockData()

// 模拟获取所有数据接口
export const getAllParts = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        data: mockData,
        message: 'success'
      })
    }, 500)
  })
}

// 模拟获取单个零件详情接口
export const getPartDetail = (id) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const part = mockData.find(item => item.id === id)
      resolve({
        code: 200,
        data: part,
        message: 'success'
      })
    }, 500)
  })
} 