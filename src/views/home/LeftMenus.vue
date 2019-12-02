<template>
    <div class="left-menu">
        <el-menu class="left-menu-item" router :collapse="isCollapse" >
            <quartz-menu v-for="menuinfo in menus" :menuinfo="menuinfo" :key="menuinfo.index"></quartz-menu>
        </el-menu>
    </div>
</template>

<script>
    import QuartzMenu from "./QuartzMenu";
    export default {
        name: "LeftMenus",
        components: {QuartzMenu},
        data(){
            return {
                isCollapse: false,
                menus: [
                    {
                        index: 'jobs',
                        title: '任务列表'
                    },
                    {
                        index: 'settings',
                        title: '设置',
                        children: [
                            {index: 'operation' , title: '操作'}
                        ]
                    }
                ]
            }
        },
        methods: {
            expand(){
                this.isCollapse = false;
            },
            collapse(){
                this.isCollapse = true;
            }
        }
    }
</script>

<style scoped>

    .left-menu{
        position: absolute;
        top: 0px;
        left: 0px;
        height: 100%;
    }

    .left-menu ul {
        height: 100%;
    }

    .left-menu-item:not(.el-menu--collapse) {
        width: 200px;
        min-height: 400px;
    }

</style>