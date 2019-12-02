<template>
    <el-container>
        <el-header height="30px" style="padding-top: 5px;">
            <el-button icon="el-icon-add" type="primary" style="margin-top: 10px;" @click="addJob">添加任务</el-button>
            <el-button icon="el-icon-search" type="primary" style="margin-top: 10px;" @click="searchData">查询</el-button>
        </el-header>
        <el-main>
            <el-table :data="tableData" stripe border>
                <el-table-column type="index" width="50px"></el-table-column>
                <el-table-column property="schedule" label="定时器" width="120" ></el-table-column>
                <el-table-column property="group" label="分组" width="100"></el-table-column>
                <el-table-column property="jobname" label="任务名称" width="140"></el-table-column>
                <el-table-column property="jobstatus" label="任务状态" width="100"></el-table-column>
                <el-table-column property="execClass" label="执行类" width="300"></el-table-column>
                <el-table-column property="triggertype" label="触发器类型" width="100"></el-table-column>
                <el-table-column label="触发规则"  width="120">
                    <template slot-scope="scope">
                        <p v-if="scope.row.triggertype === 'SIMPLE'">
                            间隔:{{scope.row.triggerrule}}ms<br>重复次数:{{scope.row.repeatCount}}
                        </p>
                        <p v-else>
                            {{scope.row.triggerrule}}
                        </p>
                    </template>
                </el-table-column>
                <el-table-column label="参数信息" width="100">
                    <template slot-scope="scope">
                        <p v-if="scope.row.param">{{scope.row.param}}</p>
                        <p v-else></p>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button type="text" :disabled="scope.row.jobstatus === 'NORMAL'" @click="runJob(scope.row)" icon="el-icon-caret-right">执行</el-button>
                        <el-button type="text" :disabled="scope.row.jobstatus !== 'NORMAL'"  @click="pauseJob(scope.row)" icon="el-icon-video-pause">暂停</el-button>
                        <el-button type="text" :disabled="scope.row.jobstatus === 'NORMAL'"  @click="resetJob(scope.row)" icon="el-icon-setting">重设</el-button>
                        <el-button type="text" :disabled="scope.row.jobstatus === 'NORMAL'" @click="removeJob(scope.row)" icon="el-icon-delete">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>
        <!--对话框显示-->
        <el-dialog :visible="dialogVisible" width="400px">
            <el-form :model="forminfo" :rules="formrules" label-width="80px" ref="jobform">
                <el-form-item label="任务名称" prop="jobname">
                    <el-input v-model="forminfo.jobname" placeholder="请输入任务名称" ></el-input>
                </el-form-item>
                <el-form-item label="执行类" prop="execClass">
                    <el-input v-model="forminfo.execClass" placeholder="请输入执行类名"></el-input>
                </el-form-item>
                <el-form-item label="触发类型" prop="triggerType">
                    <el-select v-model="forminfo.triggerType" style="width: 100%;">
                        <el-option label="cron" value="CRON"></el-option>
                        <el-option label="simple" value="SIMPLE"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="触发规则" prop="triggerRule">
                    <el-input v-model="forminfo.triggerRule" placeholder="请输入触发规则"></el-input>
                </el-form-item>
                <el-form-item label="重复次数" v-if="forminfo.triggerType === 'SIMPLE'" prop="repeatCount">
                    <el-input-number v-model="forminfo.repeatCount" :min="-1"></el-input-number>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="saveJob">确定</el-button>
                    <el-button @click="closeDialog">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        <el-dialog :visible="resetTrigger" width="400px">
            <el-form :model="forminfo" :rules="formrules" label-width="80px" ref="triggerform">
                <el-form-item label="任务名称" prop="jobname">
                    <el-input v-model="forminfo.jobname" placeholder="请输入任务名称" :disabled="true"></el-input>
                </el-form-item>
                <el-form-item label="执行类" prop="execClass">
                    <el-input v-model="forminfo.execClass" placeholder="请输入执行类名" :disabled="true"></el-input>
                </el-form-item>
                <el-form-item label="触发类型" prop="triggerType">
                    <el-select v-model="forminfo.triggerType" style="width: 100%;">
                        <el-option label="cron" value="CRON"></el-option>
                        <el-option label="simple" value="SIMPLE"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="触发规则" prop="triggerRule">
                    <el-input v-model="forminfo.triggerRule" placeholder="请输入触发规则"></el-input>
                </el-form-item>
                <el-form-item label="重复次数" v-if="forminfo.triggerType === 'SIMPLE'" prop="repeatCount">
                    <el-input-number v-model="forminfo.repeatCount" :min="-1"></el-input-number>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitTrigger">确定</el-button>
                    <el-button @click="closeTrigger">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </el-container>
</template>

<script>

    import {queryJobs,addJob,runJob,pauseJob,removeJob,resetJob} from '../../api/quartzlist';

    export default {
        name: "QuartzList",
        data(){
            //校验类型
            var validateType = (rule,value,callback) => {
                if (value === ''){
                    callback(new Error("触发类型不能为空！"));
                }else if (this.forminfo.triggerRule) {
                    if (value === 'SIMPLE' && !Number.isInteger(this.forminfo.triggerRule-0)){
                        callback(new Error("简单模式，规则只能是整数"));
                    }else {
                        callback();
                    }
                }else {
                    callback();
                }
            };
            var validateRule = (rule,value,callback) => {
                if (value === ''){
                    callback(new Error("触发规则不能为空！"));
                }else if (this.forminfo.triggerType === 'SIMPLE' && !Number.isInteger(value-0)){
                    callback(new Error("简单模式，规则只能是整数"));
                }else {
                    callback();
                }
            };
            return {
                tableData:[],
                queryparam:{
                },
                dialogVisible: false,
                resetTrigger:false,
                forminfo:{
                    jobname:'',
                    triggerType:'',
                    triggerRule:'',
                    execClass:'',
                    repeatCount: -1
                },
                formrules:{
                    jobname:[
                        {required:true,message:"请输入任务名称！"}
                    ],
                    execClass:[
                        {required:true,message:"请输入执行类！"}
                    ],
                    triggerType:[
                        {validator: validateType,trigger:'blur'}
                    ],
                    triggerRule: [
                        {validator: validateRule,trigger:'blur'}
                    ]
                }

            }
        },
        mounted() {
            this.searchData();
        },
        methods:{
            searchData(){
                const me = this;
                queryJobs(this.queryparam)
                    .then( res => {
                        if (res.success){
                            me.tableData = res.data;
                        }else {
                            me.$alert(res.msg||'加载失败！');
                        }
                    })
                    .catch(res => {
                        me.$alert('加载失败！');
                    })
            },
            addJob(){
                this.dialogVisible = true;
            },
            closeDialog(){
                this.$refs['jobform'].resetFields();
                this.dialogVisible = false;
            },
            saveJob(){
                const me = this;
                this.$refs['jobform'].validate((valid) => {
                    console.log(valid)
                    if (valid){
                        addJob(me.forminfo)
                            .then(res =>{
                                if (!res.success) {
                                    me.$alert(res.msg||"保存失败！");
                                }else {
                                    me.$refs['jobform'].resetFields();
                                    me.dialogVisible = false;
                                    me.searchData();
                                }
                            })
                            .catch(error => {
                                me.$alert("保存失败！")
                            });
                    }else {
                        me.$message.error("校验不通过！");
                    }
                });
            },
            //运行任务
            runJob(row){
                runJob({
                    group: row.group,
                    jobname: row.jobname
                }).then( res =>{
                    if (res.success) {
                        this.searchData()
                    }else {
                        this.$alert(res.msg || '操作失败！')
                    }
                }).catch(error => {
                    this.$alert("操作失败！")
                })
            },
            pauseJob(row){
                pauseJob({
                    group: row.group,
                    jobname: row.jobname
                }).then( res =>{
                    if (res.success) {
                        this.searchData()
                    }else {
                        this.$alert(res.msg || '操作失败！')
                    }
                }).catch(error => {
                    this.$alert("操作失败！")
                })
            },
            removeJob(row){
                removeJob({
                    group: row.group,
                    jobname: row.jobname
                }).then( res =>{
                    if (res.success) {
                        this.searchData()
                    }else {
                        this.$alert(res.msg || '操作失败！')
                    }
                }).catch(error => {
                    this.$alert("操作失败！")
                })
            },
            resetJob(row){
                console.log(row)
                this.forminfo = {
                    jobname: row.jobname,
                    execClass: row.execClass,
                    triggerType: row.triggertype,
                    triggerRule: row.triggerrule,
                    repeatCount: row.repeatCount
                };
                this.resetTrigger = true;
            },
            submitTrigger(){
                const me = this;
                this.$refs['triggerform'].validate((valid) => {
                    console.log(valid)
                    if (valid){
                        resetJob(me.forminfo)
                            .then(res =>{
                                if (!res.success) {
                                    me.$alert(res.msg||"保存失败！");
                                }else {
                                    me.$refs['triggerform'].resetFields();
                                    me.resetTrigger = false;
                                    me.searchData();
                                }
                            })
                            .catch(error => {
                                me.$alert("保存失败！")
                            });
                    }else {
                        me.$message.error("校验不通过！");
                    }
                });
            },
            closeTrigger(){
                this.$refs['triggerform'].resetFields();
                this.resetTrigger = false;
            }
        }
    }
</script>

<style scoped>

</style>