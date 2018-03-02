<template>
    <div class="fillcontain">
        <headTop></headTop>
        <div class="table_container">
            <div style="margin: 15px;">
                <el-input placeholder="请输入事务组ID进行搜索" v-model="searchValue">
                    <el-button slot="append" icon="search" @click="search"></el-button>
                </el-input>
            </div>
            <el-table
                    :border=true
                    ref="multipleTable"
                    :data="tableData"
                    highlight-current-row
                    style="width: 100%">
                <el-table-column
                        type="expand">
                    <template slot-scope="props">
                        <el-form label-position="left" inline class="demo-table-expand">
                            <el-table
                                    :row-style="subRow"
                                    :data="props.row.itemVOList" style="width: 100%">
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="120"
                                        :show-overflow-tooltip=true
                                        prop="transId"
                                        label="事务ID">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="120"
                                        prop="txGroupId"
                                        label="事务组ID">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="80"
                                        prop="role"
                                        label="角色">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        :show-overflow-tooltip=true
                                        width="120"
                                        prop="status"
                                        label="状态">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="80"
                                        prop="consumeTime"
                                        label="耗时(s)">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="120"
                                        prop="waitMaxTime"
                                        label="最大等待时间">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        :show-overflow-tooltip=true
                                        width="180"
                                        prop="modelName"
                                        label="模块名称">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="120"
                                        prop="taskKey"
                                        label="线程标识">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="180"
                                        prop="tmDomain"
                                        label="协调者地址">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="240"
                                        :show-overflow-tooltip=true
                                        prop="targetClass"
                                        label="调用方接口">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        width="120"
                                        prop="targetMethod"
                                        label="调用方方法">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        prop="message"
                                        align="center"
                                        :show-overflow-tooltip=true
                                        label="结果信息"
                                        width="180">
                                </el-table-column>
                                <el-table-column
                                        label-class-name="subTableHeaderFont"
                                        prop="createDate"
                                        label="创建时间"
                                        width="180">
                                </el-table-column>
                            </el-table>
                        </el-form>
                    </template>
                </el-table-column>
                <el-table-column
                        align="center"
                        type="selection"
                        width="40">
                </el-table-column>
                <el-table-column
                        property="id"
                        width="180"
                        align="center"
                        label="事务组ID">
                </el-table-column>
                <el-table-column
                        width="120"
                        property="role"
                        align="center"
                        label="事务角色">
                </el-table-column>
                <el-table-column
                        width="120"
                        align="center"
                        property="status"
                        label="事务状态">
                </el-table-column>
                <el-table-column
                        width="100"
                        align="center"
                        property="consumeTime"
                        label="耗时(s)">
                </el-table-column>
                <el-table-column
                        width="200"
                        align="center"
                        property="createDate"
                        label="创建时间">
                </el-table-column>
                <el-table-column
                        align="center"
                        min-width="200"
                        :show-overflow-tooltip=true
                        property="targetClass"
                        label="事务发起方接口">
                </el-table-column>
                <el-table-column
                        align="center"
                        width="120"
                        :show-overflow-tooltip=true
                        property="targetMethod"
                        label="发起方方法">
                </el-table-column>
            </el-table>
            <div style="">
                <div style="margin-top: 20px; margin-left:20px;float: left">
                    <el-button type="danger" @click="deleteAll()">删除勾选数据</el-button>
                </div>
                <div class="Pagination" style="text-align: left;margin-top: 20px;float: right">
                    <el-pagination
                            @size-change="handleSizeChange"
                            @current-change="handleCurrentChange"
                            :current-page="paging.currentPage"
                            :page-sizes="[10,20,50, 100, 200]"
                            :page-size="paging.limit"
                            layout="total, sizes, prev, pager, next, jumper"
                            :total="count">
                    </el-pagination>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import headTop from '../components/headTop'
    export default {
        data() {
            return {

                tableData: [],
                searchValue: "",
                paging: {
                limit: 10,
                currentPage: 1,
                },
                count: 0,
                res: null,
                baseUrl: document.getElementById('serverIpAddress').href
            }
        },
        components: {
            headTop,
        },
        created() {
            this.$http.post(this.baseUrl + '/tx/listPage', {
                "pageParameter": {
                    "currentPage": this.paging.currentPage,
                    "pageSize": this.paging.limit,
                },
                "taskKey": "",
                "txGroupId": ""
            }).then(
                response => {
                    if (response.body.code == 200 && response.body.data.dataList!=null) {
                        let rp = response.body;
                        this.count = rp.data.page.totalCount;
                        this.tableData = rp.data.dataList
                    } else {
                        this.$message({
                            type: 'error',
                            message: '获取数据失败或者数据为空!'
                        });
                    }

                    console.log("success!");
                },
                response => {
                    this.$message({
                        type: 'error',
                        message: response
                    });
                }
            )
        },
        methods: {
            search: function () {
                this.$http.post(this.baseUrl + '/tx/listPage', {
                    "pageParameter": {
                        "pageSize": this.paging.limit,
                    },
                    "taskKey": "",
                    "txGroupId": this.searchValue
                }).then(
                    response => {
                        if (response.body.code == 200) {
                            let rp = response.body;
                            this.count = rp.data.page.totalCount;
                            this.tableData = rp.data.dataList
                        } else {
                            this.$message({
                                type: 'error',
                                message: '获取数据失败!'
                            });
                        }

                        console.log("success!");
                    },
                    response => {
                        this.$message({
                            type: 'error',
                            message: response
                        });
                    }
                )
            },
            deleteAll: function () {
                var Selection = this.$refs.multipleTable.selection;
                var groupIds = [];
                var slen = Selection.length;
                for (var i = 0; i < slen; i++) {
                    groupIds.push(Selection[i].id);
                }
                //delete row and update tableData but don't send post request to update all data
                var oldTableData=this.tableData;
                var tlen=oldTableData.length;
                this.$http.post(this.baseUrl + '/tx/batchRemove', groupIds).then(
                    response => {
                        if (response.body.code == 200) {
                            this.$message({type: 'success', message: '删除数据成功!'});
                            for (let x=0;x<slen;x++)
                            {
                                for(let j=0;j<tlen;j++)
                                {
                                    if (groupIds[x]==oldTableData[j].id)
                                    {
                                        oldTableData.splice(j,1);
                                        tlen=tlen-1;
                                    }
                                }
                            }
                            this.count =this.count-slen;
                        } else {
                            this.$message({
                                type: 'error',
                                message: '删除数据失败!'
                            });
                        }
                    },
                    response => {
                        this.$message({
                            type: 'error',
                            message: response
                        });
                    }
                )

            },
            subRow: function () {
                return {"font-size": "0.85em"};
            },
            subTableHeader: function () {
                return {"background-color": "red", "font-size": "0.6em"};
            },
            async initData() {
                try {
                    const countData = await getUserCount();
                    if (countData.status == 1) {
                        this.count = countData.count;
                    } else {
                        throw new Error('获取数据失败');
                    }
                    this.getUsers();
                } catch (err) {
                    console.log('获取数据失败', err);
                }
            },
            handleSizeChange(val) {
                this.paging.limit = val;
                console.log(`每页 ${val} 条`+this.paging.currentPage);
            },
            handleCurrentChange(val) {
                this.paging.currentPage = val;
            },
        },
        watch: {
            paging: {
                handler: function () {
                    this.$http.post(this.baseUrl + '/tx/listPage', {
                        "pageParameter": {
                            "currentPage": this.paging.currentPage,
                            "pageSize": this.paging.limit,
                        },
                        "taskKey": "",
                        "txGroupId": ""
                    }).then(
                        response => {
                            if (response.body.code == 200) {
                                let rp = response.body;
                                this.count = rp.data.page.totalCount;
                                this.tableData = rp.data.dataList
                            } else {
                                this.$message({
                                    type: 'error',
                                    message: '获取数据失败!'
                                });
                            }

                            console.log("success!");
                        },
                        response => {
                            this.$message({
                                type: 'error',
                                message: response
                            });
                        }
                    )
                },
                deep: true
            }

        }
    }
</script>

<style lang="less">
    @import '../style/mixin';

    .table_container {
        padding: 0px;
    }

    .subTableHeaderFont {
        font-size: 0.9em;
        padding: 0px;
        text-align: center;
        color: rebeccapurple !important;
        background-color: white !important;
    }

    .el-table__expanded-cell {
        padding: 5px !important;
    }
</style>
