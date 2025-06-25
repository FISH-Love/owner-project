// 查询列表数据
const getSetMealPage = (params) => {
  return $axios({
    url: '/setMeal/page',
    method: 'get',
    params
  })
}

// 删除数据接口
const deleteSetMeal = (ids) => {
  return $axios({
    url: '/setMeal',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editSetMeal = (params) => {
  return $axios({
    url: '/setMeal',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addSetMeal = (params) => {
  return $axios({
    url: '/setMeal',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情接口
const querySetMealById = (id) => {
  return $axios({
    url: `/setMeal/${id}`,
    method: 'get'
  })
}

// 批量起售禁售
const setMealStatusByStatus = (params) => {
  return $axios({
    url: `/setMeal/status/${params.status}`,
    method: 'post',
    params: { ids: params.ids }
  })
}
