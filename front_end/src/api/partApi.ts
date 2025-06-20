import httpRequest from '@/request/index'

interface createPart{
   name: string,
   number: string,
   source: string,
   partType: string,
   categoryId: string,
   clsAttrs:{
      height:string,
      Brand:  string,
      Weight: string,
      Size: string,
      Number: string
   }

}

interface updatePart{
    masterId: string,
    name: string,
    number: string,
    source: string,
    partType: string,
    categoryId: string,
    clsAttrs: {
        height: string,
        Brand: string,
        Weight: string,
        Size: string,
        Number: string,
     

}
}


//得到所有part
export function apiGetAllpart(){
        return httpRequest({
              url: '/part/10000/1?isFilterOld=true' ,           
              method:'get',

        })
}

//创建part
export function apiCreateNewPart(data1:createPart){
      return httpRequest({
        url: '/part/create',
        method: 'post',
        data: data1
      })
     
}


//根据masterId更新
export function apiUpdate(data2:updatePart){
     return httpRequest({
        url: '/part/update',
        method: 'post',
        data: data2,
        
     }) 
} 



//根据masterID删除
 export function apideletePart(data3:string){
      return httpRequest({
        url: '/part/delete/new',
        method: 'delete',
        data: data3
      })

 }






