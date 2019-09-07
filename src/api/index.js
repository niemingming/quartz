import request from '../utils/request';

//统一的api地址管理
export const fetchData = (query) => {
    return request({
        url: '/ms/table/list',
        method: 'post',
        data: query
    })
}