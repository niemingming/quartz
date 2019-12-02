import request from '../utils/request';

//任务列表查询
export const queryJobs = (params) => {
    return request({
        url: '/api/v1/quartzjobs',
        method: 'get',
        params
    })
}
//新增定时任务
export const addJob = (params) => {
    return request({
        url: '/api/v1/jobs',
        method: 'post',
        params
    })
}
//执行定时任务，指恢复
export const runJob = (params) => {
    return request({
        url: '/api/v1/jobs/run',
        method: 'put',
        params
    })
}
//暂停定时任务
export const pauseJob = (params) => {
    return request({
        url:'/api/v1/jobs/pause',
        method: 'put',
        params
    })
}
//删除定时任务
export const removeJob = (params) => {
    return request({
        url: '/api/v1/jobs',
        method: 'delete',
        params
    })
}
//重设定时任务
export const resetJob = (params) => {
    return request({
        url: '/api/v1/jobs/reset',
        method: 'put',
        params
    })
}