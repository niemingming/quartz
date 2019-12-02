<template>
    <div class="wrapper">
        <left-menus></left-menus>
        <div class="content-box">
            <router-view></router-view>
        </div>
    </div>

</template>

<script>
    import LeftMenus from "./LeftMenus";
    export default {
        name: "Home",
        components: {LeftMenus}
    }
</script>

<style scoped>
    .content-box {
        position: absolute;
        left: 200px;
        right: 0;
        top: 0px;
        bottom: 0;
        padding-bottom: 30px;
        -webkit-transition: left .3s ease-in-out;
        transition: left .3s ease-in-out;
        background: #f0f0f0;
    }
</style>