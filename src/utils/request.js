import axios from 'axios';

const service = axios.create({
    // process.env.NODE_ENV === 'development' 来判断是否开发环境
    baseURL: process.env.VUE_APP_BASEURL,//配置不同环境的后台
    withCredentials:true,//携带cookie
    timeout: 5000
})


service.interceptors.request.use( config => {
    if (process.env.NODE_ENV == 'dev'){
        //开发环境配置测试权限信息
        config.headers.common = {
            "x-current-userinfo": encodeURIComponent("{'userId':'8','userAccount':'test','userName':'测试人员'}")
        };
    }
    return config;
}, error => {
    console.log(error);
    return Promise.reject();
})

service.interceptors.response.use(response => {
    if(response.status === 200){
        return response.data;
    }else{
        Promise.reject();
    }
}, error => {
    console.log(error);
    return Promise.reject();
})

export default service;