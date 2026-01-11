<template>
  <section>
    <!--工具条 -->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="query">
        <el-form-item>
          <el-input v-model="query.keyword" placeholder="关键字"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-on:click="search">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary">新增</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--列表:优秀实践，copy domain修改列表-->
    <el-table :data="pageData.list" highlight-current-row v-loading="listLoading" @selection-change="selsChange"
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
      <el-table-column type="index" width="60">
      </el-table-column>
      <el-table-column prop="title" label="标题" width="250" sortable>
      </el-table-column>
      <el-table-column prop="name" label="名称" width="100" sortable>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="120" sortable>
      </el-table-column>
      <!--private Employee manager;//管理员-->
      <!--private SearchMasterMsg parent;//上级部门-->
      <el-table-column prop="type.name" label="品种" width="120" sortable>
      </el-table-column>
      <el-table-column prop="user.username" label="用户" width="200" sortable>
      </el-table-column>
      <el-table-column prop="shop.name" label="店铺" width="120" sortable>
      </el-table-column>
      <el-table-column label="操作" min-width="120">
        <template scope="scope">
          <el-button size="small" @click="handleAccept(scope.$index, scope.row)">接单</el-button>
          <el-button type="danger" size="small" @click="rejectOrder(scope.$index, scope.row)">拒单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--工具条-->
    <el-col :span="24" class="toolbar">
      <el-button type="danger" :disabled="this.sels.length===0">批量删除</el-button>
      <el-pagination layout="prev, pager, next" @current-change="handleCurrentChange" :current-page="query.currentPage"
                     :page-size="query.pageSize" :total="pageData.total" style="float:right;">
      </el-pagination>
    </el-col>

    <!--编辑界面
        v-model="editFormVisible" 老版本打开对话框
    -->
    <el-dialog title="接单" :visible.sync="acceptSearchMasterMsgFormVisible" :close-on-click-modal="false">


      <!--ref="searchMasterMsgForm" 指定表单名称，以后可以通过this.$refs.searchMasterMsgForm-->
      <el-form :model="acceptOrderForm" label-width="80px" :rules="acceptSearchMasterMsgFormRules"
               ref="acceptSearchMasterMsgForm">
        <el-form-item label="处理人">
          <!--下拉框-->
          <el-select v-model="acceptOrderForm.handler" value-key="id" placeholder="请选择" style="width: 350px">
            <el-option
                v-for="employee in employees"
                :key="employee.id"
                :label="employee.username"
                :value="employee">
              <span style="float: left">{{ employee.username }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ employee.email }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="sn">
          <el-input v-model="acceptOrderForm.note" auto-complete="off"></el-input>
        </el-form-item>
        接单完成后，就会创建对应收购订单！请到收购订单中操作！
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="acceptSearchMasterMsgFormVisible = false">取消</el-button>
        <el-button type="primary" @click.native="acceptOrder" :loading="acceptSearchMasterMsgLoading">确定</el-button>
      </div>
    </el-dialog>

  </section>
</template>

<script>
export default {
  data() {
    return {
      title: '',
      employees: [],
      pageData: {
        total: 0,//分页条
        list: []//数据表格
      },
      query: {
        keyword: '',
        currentPage: 1,//分页条
        pageSize: 10 //分页条
      },
      listLoading: false,
      sels: [],//列表选中列
      acceptSearchMasterMsgFormVisible: false,//编辑界面是否显示
      acceptSearchMasterMsgLoading: false,
      acceptSearchMasterMsgFormRules: {
        note: [
          {required: true, message: '请输入备注！', trigger: 'blur'}
        ]
      },

      //编辑界面数据
      acceptOrderForm: {
        id: null,
        handler: {},
        note: ''
      },
    }
  },
  methods: {
    acceptOrder() {
      //消息id 处理人 note

      this.$http.post("/searchMaster/searchMasterMsg/accept",
          {
            msgId: this.acceptOrderForm.id,
            handler: this.acceptOrderForm.handler.id,
            note: this.acceptOrderForm.note
          })
          .then((result) => {
            console.log(result)
            result = result.data; //{sucesss:true,message:"xx"}
            this.listLoading = false;
            if (result.success) { //成功提示
              this.$message({
                message: '接单成功',
                type: 'success'
              });
            } else {
              this.$message({ //错误提示
                message: result.message,
                type: 'error'
              });
            }
            this.acceptSearchMasterMsgFormVisible = false;//关闭对话框
            //重新加载数据
            this.getSearchMasterMsgs();
          });

    },
    search() {
      //修改页码
      this.query.currentPage = 1;
      //改查询条件=双向绑定输入时自动改变，只需要重新查询一次就ok
      this.getSearchMasterMsgs();
    },
    handleCurrentChange(val) {
      this.query.currentPage = val;
      this.getSearchMasterMsgs();
    },
    //获取用户列表
    getSearchMasterMsgs() {
      this.listLoading = true; //加载条
      this.$http.post("/searchMaster/searchMasterMsg/toHandle", this.query)
          .then(result => {
            this.pageData = result.data.data;
            this.listLoading = false; //去除加载条
          })
          .catch(result => {
            alert("抱错了！");
          })

    },
    //删除
    rejectOrder: function (index, row) {
      this.$confirm('确认拒绝吗?', '温馨提示', {
        type: 'warning'
      }).then(() => {
        this.listLoading = true;
        let url = "/searchMaster/searchMasterMsg/reject/" + row.id;
        this.$http.post(url)
            .then((result) => {
              console.log(result)
              result = result.data; //{sucesss:true,message:"xx"}
              this.listLoading = false;
              if (result.success) { //成功提示
                this.$message({
                  message: '拒绝成功',
                  type: 'success'
                });
              } else {
                this.$message({ //错误提示
                  message: result.message,
                  type: 'error'
                });
              }
              //重新加载数据
              this.getSearchMasterMsgs();
            });
      }).catch(() => {
      });
    },
    //显示编辑界面
    handleAccept: function (index, row) {
      console.log(row);
      this.acceptSearchMasterMsgFormVisible = true;

      this.acceptOrderForm.id = row.id;
      if (row.shop) {
        let shopId = row.shop.id;
        this.getEmployees(shopId);
      }


    },
    selsChange: function (sels) {
      this.sels = sels;
    },
    getEmployees(shopId) {
      this.$http.get("/employee/list/" + shopId)
          .then(result => {
            this.employees = result.data;
          })
    }

  },
  mounted() {
    this.getSearchMasterMsgs();
  }
}

</script>

<style scoped>

</style>