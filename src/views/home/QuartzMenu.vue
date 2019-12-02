<template>

    <el-submenu v-if="menuinfo.children" :index="menuinfo.index" >
        <template slot="title">
            <i class="el-icon-setting"></i>
            <span>{{menuinfo.title}}</span>
        </template>
        <quartz-menu v-for="info in menuinfo.children" :menuinfo="info" :key="info.index"></quartz-menu>
    </el-submenu>
    <el-menu-item v-else :index="menuinfo.index">
        <i class="el-icon-setting"></i>
        {{menuinfo.title}}
    </el-menu-item>
</template>

<script>
    export default {
        name: "QuartzMenu",
        props: ['menuinfo']
    }
</script>

<style scoped>

</style>