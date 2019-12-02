import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/jobs'
        },
        {
            path: '/',
            component: () => import(/* webpackChunkName: "home" */ '../views/home/Home.vue'),
            meta: {title: '定时任务管理'},
            children: [
                {
                    path: '/jobs',
                    meta: {title: '任务列表'},
                    component: () => import('../views/quartz/QuartzList.vue')
                }
            ]
        },

    ]
});
