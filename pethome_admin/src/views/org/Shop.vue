<template>
  <section>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="filters">
        <el-form-item>
          <el-input v-model="query.keyword" placeholder="关键字"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="querySerch">查询</el-button>
        </el-form-item>
        <el-form-item>
<!--          <el-button type="primary" @click="handleAdd">新增</el-button>-->
          <el-button type="primary" @click="exportData">导出</el-button>
          <el-upload style="display: inline-block;"
                     action="http://localhost:8080/org/shop/importData">
            <el-button type="primary">导入</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-col>

    <el-table :data="pageData.data" highlight-current-row v-loading="listLoading" @selection-change="selsChange"
              style="width: 100%;">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table :data="props.row.auditLogs" style="width: calc(100% - 47px)" class="two-list">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column type="index" width="60">
            </el-table-column>
            <el-table-column prop="state" label="状态">
              <template slot-scope="props">
							  <span v-if="props.row.state ==1">
								<span style="color:#00B46D">通过</span>
							  </span>
                <span v-else>
								<span style="color:#D75C89">驳回</span>
                 			 </span>
              </template>
            </el-table-column>
            <el-table-column prop="note" label="备注"></el-table-column>
            <el-table-column prop="auditManager.username" label="审核人员"></el-table-column>
            <el-table-column prop="audit_time" label="审核时间"></el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column type="selection" width="55">
      </el-table-column>
      <el-table-column type="index" label="序号" width="60">
      </el-table-column>
      <el-table-column prop="name" label="店铺名称" width="120" sortable>
      </el-table-column>
      <el-table-column prop="tel" label="联系电话" width="150" sortable>
      </el-table-column>
      <el-table-column prop="registerTime" label="注册时间" width="150" sortable>
      </el-table-column>
      <el-table-column prop="state" label="状态" width="80" sortable>
        <template scope="scope">
          <span v-if="scope.row.state==0" style="color: darkgrey">待审核</span>
          <span v-if="scope.row.state==1" style="color: darkorange">审核通过待激活</span>
          <span v-if="scope.row.state==2" style="color: red">审核拒绝</span>
          <span v-if="scope.row.state==3" style="color: darkorchid">等待人工审核</span>
          <span v-if="scope.row.state==4" style="color: green">已激活</span>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="120" sortable>
      </el-table-column>
      <el-table-column prop="logo" label="图标" min-width="180" sortable>
        <template scope="scope">
          <img :src="scope.row.logo" style="width: 100px; height: 100px;">
        </template>
      </el-table-column>
      <el-table-column prop="admin.username" label="店长" min-width="80" sortable>
      </el-table-column>

      <el-table-column label="操作" width="250">
        <template scope="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="small" @click="handleAudit(scope.$index, scope.row)">审核</el-button>
          <el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--工具条-->
    <el-col :span="24" class="toolbar">
      <el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0">批量删除</el-button>
      <el-pagination layout="prev, pager, next"
                     @current-change="handleCurrentChange"
                     :current-page="query.currentPage"
                     :page-size="query.pageSize"
                     :total="pageData.total"
                     style="float:right;">
      </el-pagination>
    </el-col>

    <!--新增修改界面-->
    <el-dialog :title="title" :visible.sync="shopVisible" :close-on-click-modal="false">
      <el-form :model="shop" label-width="80px" :rules="shopFormRules" ref="shopForm">
        <el-form-item label="店铺名称" prop="name">
          <el-input v-model="shop.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="tel">
          <el-input v-model="shop.tel" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="shop.state">
            <el-radio class="radio" :label="0">待审核</el-radio>
            <el-radio class="radio" :label="1">审核通过待激活</el-radio>
            <el-radio class="radio" :label="2">审核拒绝</el-radio>
            <el-radio class="radio" :label="3">等待人工审核</el-radio>
            <el-radio class="radio" :label="4">已激活</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="shop.address" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="logo">
          <el-input v-model="shop.logo" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="店长">
          <el-select v-model="shop.employee" placeholder="请选择" value-key="id">
            <el-option
                v-for="employee in employees"
                :key="employee.id"
                :label="employee.username"
                :value="employee">
              <span style="float: left">{{ employee.username }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ employee.department.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="deleteBlurWarn">取消</el-button>
        <el-button type="primary" @click.native="save" :loading="shopLoading">提交</el-button>
      </div>
    </el-dialog>


    <el-dialog title="审核店铺" :visible.sync="shopAuditVisible" :close-on-click-modal="false">
      <el-form :model="shopAuditLog" label-width="80px" ref="shopAuditLogForm">
        <el-form-item label="备注" prop="note">
          <el-input type="textarea" v-model="shopAuditLog.note"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button>取消</el-button>
        <el-button type="primary" @click.native="auditPass">通过</el-button>
        <el-button type="primary" @click.native="auditReject">驳回</el-button>
      </div>
    </el-dialog>

  </section>
</template>

<script>
/*	import util from '../../common/js/util'
	//import NProgress from 'nprogress'
	import { getUserListPage, removeUser, batchRemoveUser, editUser, addUser } from '../../api/api';*/

export default {
  data() {
    return {
      title: '',
      employees: [],
      filters: {
        name: ''
      },
      pageData: {
        total: 0,
        data: []
      },
      query: {
        currentPage: 1,
        pageSize: 5,
        params: {}  // 条件
      },
      listLoading: false,
      sels: [],//列表选中列

      shopVisible: false,//编辑界面是否显示
      shopAuditVisible: false,//编辑界面是否显示
      shopLoading: false,
      shopFormRules: {
        name: [
          {required: true, message: '请输入店铺名称', trigger: 'blur'}
        ]
      },
      //编辑界面数据
      shopAuditLog: {
        shopId: null,
        note: ''
      },
      //编辑界面数据
      shop: {
        id: null,
        name: '',
        tel: '',
        registerTime: null,
        state: 1,
        address: '',
        logo: '',
        employee: null
      }

    }
  },
  methods: {

    // 导入
    importData() {
    },

    // 导出
    exportData() {
      // 发送导出请求
      window.location.href = "http://localhost:8080/org/shop/exportData";
      /* this.$http.post("/org/shop/export")
     .then(result=>{
         result = result.data;
         if(result.success){
                   this.$message({
                       message: '导出成功',
                       type: 'success'
                   });
       }else{
                   this.$message({
                       message: '导出失败',
                       type: 'error'
                   });
       }
     })*/
    },


    auditReject() {
      this.$http.post("/org/shop/reject", this.shopAuditLog)
          .then(result => {
            result = result.data;
            if (result.success) {
              this.$message({
                message: '驳回Ok',
                type: 'success'
              });
            } else {
              this.$message({
                message: '驳回失败',
                type: 'error'
              });
            }
            this.shopAuditVisible = false;
            this.getpagelist();
          })
    },
    auditPass() {
      this.$http.post("/org/shop/pass", this.shopAuditLog)
          .then(result => {
            result = result.data;
            if (result.success) {
              this.$message({
                message: '审核通过',
                type: 'success'
              });
            } else {
              this.$message({
                message: '审核失败',
                type: 'error'
              });
            }
            this.shopAuditVisible = false;
            this.getpagelist();
          })
    },
    //清除讲校验提示
    deleteBlurWarn() {
      this.shopVisible = false
    },
    //性别显示转换
    formatSex: function (row, column) {
      return row.sex == 1 ? '男' : row.sex == 0 ? '女' : '未知';
    },
    handleCurrentChange(currentPage) {
      this.query.currentPage = currentPage
      this.getpagelist();
    },
    //删除
    handleDel: function (index, row) {
      this.$confirm('确认删除该记录吗?', '提示', {
        type: 'warning'
      }).then(() => {
        this.listLoading = true;
        //NProgress.start();
        let para = {id: row.id};
        this.$http.delete("/org/shop/" + para.id).then((res) => {
          this.listLoading = false;
          //NProgress.done();
          this.getpagelist();
        }).catch(
            alert(res.data.message)
        );
      }).catch(() => {

      });
    },
    //显示编辑界面
    handleEdit: function (index, row) {
      this.title = "编辑"
      this.shopVisible = true;
      this.shop = Object.assign({}, row);
      this.getEmployees()
    },
    //显示审核界面
    handleAudit: function (index, row) {
      this.shopAuditVisible = true;
      console.log(row)
      this.shopAuditLog.shopId = row.id;
    },
    //显示新增界面
    handleAdd: function () {
      this.title = "新增"
      this.shopVisible = true;
      this.getEmployees()
      this.shop = {
        id: null,
        name: '',
        tel: '',
        registerTime: null,
        state: 1,
        address: '',
        logo: '',
        employee: null
      };
    },
    //保存
    save: function () {
      this.$refs.shopForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认提交吗？', '提示', {}).then(() => {
            this.shopLoading = true;
            //NProgress.start();
            let para = Object.assign({}, this.shop);
            console.log(para)

            this.$http.put("/org/shop", para)
                .then((res) => {
                  this.shopLoading = false;
                  //NProgress.done();
                  if (res.data.success) {
                    this.$message({
                      message: '保存成功',
                      type: 'success'
                    });
                    this.$refs['shopForm'].resetFields();
                    this.shopVisible = false;
                    this.getpagelist();
                  } else {
                    this.$message({
                      message: res.data.message,
                      type: 'error'
                    });
                  }

                });
          });
        }
      });
    },
    selsChange: function (sels) {
      this.sels = sels;
    },
    //批量删除
    batchRemove: function () {
      var ids = this.sels.map(item => item.id);
      this.$confirm('确认删除选中记录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        this.listLoading = true;
        //NProgress.start();
        this.$http.patch("/org/shop", ids)
            .then((res) => {
              this.listLoading = false;
              //NProgress.done();
              if (res.data.success) {
                this.$message({
                  message: '删除成功',
                  type: 'success'
                });
              } else {
                this.$message({
                  message: res.data.message,
                  type: 'error'
                });
              }

              this.querySerch();
            });
      }).catch(() => {

      });
    },
    //高级查询
    querySerch() {
      this.query.currentPage = 1
      this.getpagelist()
    },
    //获取所有用户
    getEmployees() {
      this.$http.get("/org/employee").then(res => {
        if (res) {
          this.employees = res.data
        }
      })
    },

    //获取分页列表店铺
    getpagelist() {
      this.$http.post("/org/shop/queryPage", this.query).then(res => {
        this.pageData.data = res.data.data.list;
        this.pageData.total = res.data.data.total;
      })
    }
  },
  mounted() {
    this.getpagelist();
  }
}

</script>

<style scoped>

</style>