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
          <el-button type="primary" v-perm="'employee:add'" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--列表:优秀实践，copy domain修改列表-->
    <el-table :data="pageData.rows" highlight-current-row v-loading="listLoading" @selection-change="selsChange"
              style="width: 100%;">
      <el-table-column type="selection" width="55">
      </el-table-column>
      <el-table-column type="index" width="60">
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="100" sortable>
      </el-table-column>
      <el-table-column prop="phone" label="电话" width="150" sortable>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" width="200" sortable>
      </el-table-column>
      <el-table-column prop="department.name" label="所属部门" width="150" sortable>
      </el-table-column>
      <el-table-column prop="shop.name" label="所属店铺" width="150" sortable>
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="100" sortable>
      </el-table-column>
      <el-table-column prop="state" label="状态" width="100" sortable>
        <template scope="scope">
          <span style="color: green" v-if="scope.row.state==0">禁用</span>
          <span style="color: red" v-else="scope.row.state==-1">启用</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="120">
        <template scope="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button type="danger" v-perm="'employee:delete'" size="small" @click="handleDel(scope.$index, scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--工具条-->
    <el-col :span="24" class="toolbar">
      <el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0">批量删除</el-button>
      <el-pagination layout="prev, pager, next" @current-change="handleCurrentChange" :current-page="query.currentPage"
                     :page-size="query.pageSize" :total="pageData.total" style="float:right;">
      </el-pagination>
    </el-col>

    <!--编辑界面
        v-model="editFormVisible" 老版本打开对话框
    -->
    <el-dialog :title="title" :visible.sync="employeeFormVisible" :close-on-click-modal="false">
      <!--ref="employeeForm" 指定表单名称，以后可以通过this.$refs.employeeForm-->
      <el-form :model="employee" label-width="80px" :rules="employeeFormRules" ref="employeeForm">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="employee.username" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="employee.phone" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="employee.email" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="employee.password" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model="employee.age" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="employee.state">
            <el-radio class="radio" :label="0">禁用</el-radio>
            <el-radio class="radio" :label="1">启用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="部门" prop="parent">
          <el-cascader v-model="employee.department"
                       :options="deptTree"
                       :show-all-levels="false"
                       :props="{
									checkStrictly: true,
									label:'name',
									value:'id'
							    }"
                       clearable></el-cascader>
        </el-form-item>
        <el-form-item label="角色" prop="roles">
          <el-select v-model="employee.roles" multiple placeholder="请选择" value-key="id">
            <el-option
                v-for="item in roleList"
                :key="item.id"
                :label="item.name"
                :value="item">
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.sn }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="employeeFormVisible = false">取消</el-button>
        <el-button type="primary" @click.native="save" :loading="employeeLoading">保存</el-button>
      </div>
    </el-dialog>

  </section>
</template>

<script>
export default {
  data() {
    return {
      title: '',
      pageData: {
        total: 0,//分页条
        rows: []//数据表格
      },
      query: {
        keyword: '',
        currentPage: 1,//分页条
        pageSize: 10 //分页条
      },
      listLoading: false,
      sels: [],//列表选中列
      employeeFormVisible: false,//编辑界面是否显示
      employeeLoading: false,
      employeeFormRules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'}
        ],
        phone: [
          {required: true, message: '请输入手机号', trigger: 'blur'}
        ],
        email: [
          {required: true, message: '请输入邮箱', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'}
        ]
      },

      //编辑界面数据
      employee: {
        id: null,
        username: '',
        phone: '',
        email: '',
        password: '',
        age: 0,
        state: 1,
        department: {},
        roles: null
      },
      deptTree: [],
      roleList: []
    }
  },
  methods: {
    getDeptTree() {
      this.$http.get("/org/dept/tree")
          .then(result => {
            this.deptTree = result.data.data;
          })
    },
    search() {
      //修改页码
      this.query.currentPage = 1;
      //改查询条件=双向绑定输入时自动改变，只需要重新查询一次就ok
      this.getEmployees();
    },
    handleCurrentChange(val) {
      this.query.currentPage = val;
      this.getEmployees();
    },
    //获取用户列表
    getEmployees() {
      this.listLoading = true; //加载条
      this.$http.post("/org/employee/queryPage", this.query)
          .then(result => {
            this.pageData.total = result.data.data.total;
            this.pageData.rows = result.data.data.list;
            this.listLoading = false; //去除加载条
          })
          .catch(result => {
            alert("抱错了！");
          })

    },
    //删除
    handleDel: function (index, row) {
      this.$confirm('确认删除该记录吗?', '温馨提示', {
        type: 'warning'
      }).then(() => {
        this.listLoading = true;
        let url = "/org/employee/" + row.id;
        this.$http.delete(url)
            .then((result) => {
              console.log(result)
              result = result.data; //{sucesss:true,message:"xx"}
              this.listLoading = false;
              if (result.success) { //成功提示
                this.$message({
                  message: '删除成功',
                  type: 'success'
                });
              } else {
                this.$message({ //错误提示
                  message: result.message,
                  type: 'error'
                });
              }
              //重新加载数据
              this.getEmployees();
            });
      }).catch(() => {
      });
    },
    //显示编辑界面
    handleEdit: function (index, row) {
      //修改对话框标题
      this.title = "修改员工"
      //弹出对话框
      this.employeeFormVisible = true;
      //回显
      this.employee = Object.assign({}, row, { password: '' });
      // 现在可以直接赋值对象数组了，因为 HTML 里配置了 value-key="id"
      // 只有当 row.roles 为 null 时才给空数组，防止报错
      this.employee.roles = row.roles || [];
      // 强制把对象变成 ID
      if (row.department) {
        this.employee.department = row.department.id;
      }
      // 因为修改时密码可以为空，所以需要去掉密码的必填校验
      this.employeeFormRules.password = [{ required: false, message: '请输入密码', trigger: 'blur' }];

    },
    //显示新增界面
    handleAdd: function () {
      //修改对话框标题
      this.title = "添加员工"
      //弹出对话框
      this.employeeFormVisible = true;
      //置空数据，防止先点修改，后点添加有值
      this.employee = {
        id: null,
        username: '',
        phone: '',
        email: '',
        password: '',
        age: 0,
        state: 1,
        department: {},
        roles: null
      };
      this.employeeFormRules.password = [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ];
    },
    //保存
    save: function () {
      this.$refs.employeeForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认保存吗？', '提示', {}).then(() => {
            this.employeeLoading = true;
            let para = Object.assign({}, this.employee);
            console.log(this.employee.department)
            //处理上级员工参数格式 parent:[1,34]---->parent:{id:34}
            let departmentArr = para.department;
            if (departmentArr) {
              para.department = {"id": departmentArr[departmentArr.length - 1]}
            }
            this.$http.put("/org/employee", para)
                .then((result) => {
                  this.employeeLoading = false;
                  result = result.data;
                  if (result.success) {
                    this.$message({
                      message: '提交成功',
                      type: 'success'
                    });
                    this.employeeFormVisible = false; //关闭对话框
                    this.$refs['employeeForm'].resetFields();//重置表单
                    this.getEmployees(); //重新加载
                  } else {
                    this.$message({
                      message: result.message,
                      type: 'error'
                    });
                    //失败了可能修改了提交
                  }
                });
          });
        }
      });
    },
    selsChange: function (sels) {
      this.sels = sels;
      console.log(this.sels)
    },
    //批量删除
    batchRemove: function () {
      //遍历选择记录把id放到一个数组中
      var ids = this.sels.map(item => item.id); //[1,2,3]
      this.$confirm('确认删除选中记录吗？', '温馨提示', {
        type: 'warning'
      }).then(() => {
        this.listLoading = true;
        this.$http.delete("/org/employee/batch", {data: ids})
            .then((result) => {
              this.listLoading = false;
              result = result.data;
              if (result.success) {
                this.$message({
                  message: '删除成功',
                  type: 'success'
                });
              } else {
                this.$message({
                  message: result.message,
                  type: 'error'
                });
              }
              //跳转到第一页
              this.handleCurrentChange(1);
            });
      }).catch(() => {

      });
    },
    getRoleList() {
      this.$http.get("/system/role/queryAll")
          .then(result => {
            if (result.data) {
              this.roleList = result.data.data;
            }
          })
    }
  },
  mounted() {
    this.getEmployees();
    this.getDeptTree();
    this.getRoleList();
  }
}

</script>

<style scoped>

</style>