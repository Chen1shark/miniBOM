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
      masterId: i,
      partId: `CP-${1000 + i}`,
      name: `部件${i}`,
      version: `V${Math.floor(Math.random() * 3) + 1}.${Math.floor(Math.random() * 5)}.${Math.floor(Math.random() * 10)}`,
      partType: assemblyModes[assemblyModeIndex],
      businessCode: `${categories[categoryIndex]}-${String(i).padStart(3, '0')}`,
      source: sources[sourceIndex],
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

export default [
  // 获取所有零件接口
  {
    url: '/part/10000/1',
    method: 'get',
    response: ({ query }) => {
      console.log('Mock: 获取所有零件', query)
      return {
        code: 200,
        data: {
          list: mockData,
          total: mockData.length,
          page: 1,
          size: 10000
        },
        message: 'success'
      }
    }
  },
  
  // 创建零件接口
  {
    url: '/part/create',
    method: 'post',
    response: ({ body }) => {
      console.log('Mock: 创建零件', body)
      const newPart = {
        masterId: mockData.length + 1,
        ...body
      }
      mockData.push(newPart)
      return {
        code: 200,
        data: newPart,
        message: '创建成功'
      }
    }
  },
  
  // 更新零件接口
  {
    url: '/part/update',
    method: 'post',
    response: ({ body }) => {
      console.log('Mock: 更新零件', body)
      const index = mockData.findIndex(item => item.masterId === body.masterId)
      if (index !== -1) {
        mockData[index] = { ...mockData[index], ...body }
        return {
          code: 200,
          data: mockData[index],
          message: '更新成功'
        }
      }
      return {
        code: 404,
        message: '零件不存在'
      }
    }
  },
  
  // 删除零件接口
  {
    url: '/part/delete/new',
    method: 'delete',
    response: ({ body }) => {
      console.log('Mock: 删除零件', body)
      const index = mockData.findIndex(item => item.masterId === body)
      if (index !== -1) {
        mockData.splice(index, 1)
        return {
          code: 200,
          message: '删除成功'
        }
      }
      return {
        code: 404,
        message: '零件不存在'
      }
    }
  }
] 