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
          <span style="color: red" v-else-if="scope.row.state==1">启用</span>
          <span v-else>未知</span>
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

    <!--编辑界面-->
    <el-dialog :title="title" :visible.sync="employeeFormVisible" :close-on-click-modal="false">
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

        <el-form-item label="部门" prop="department">
          <el-cascader v-model="employee.department"
                       :options="deptTree"
                       :props="{
                          checkStrictly: true,
                          label:'name',
                          value:'id',
                          children: 'children',
                          emitPath: true
                       }"
                       clearable></el-cascader>
        </el-form-item>
        <el-form-item label="角色" prop="roles">
          <el-select v-model="employee.roles" multiple placeholder="请选择">
            <el-option
                v-for="item in roleList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
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
      employee: {
        id: null,
        username: '',
        phone: '',
        email: '',
        password: '',
        age: 0,
        state: 1,
        department: [],
        roles: []
      },
      deptTree: [],
      roleList: []
    }
  },
  methods: {
    // 初始化员工数据
    initEmployeeData() {
      return {
        id: null,
        username: '',
        phone: '',
        email: '',
        password: '',
        age: 0,
        state: 1,
        department: [],
        roles: []
      };
    },

    // 获取部门树
    getDeptTree() {
      this.$http.get("/org/dept/tree")
          .then(result => {
            this.deptTree = result.data.data;
          })
          .catch(error => {
            console.error("获取部门树失败:", error);
          });
    },

    // 获取角色列表
    getRoleList() {
      this.$http.get("/system/role/queryAll")
          .then(result => {
            if (result.data) {
              this.roleList = result.data.data;
            }
          })
          .catch(error => {
            console.error("获取角色列表失败:", error);
          });
    },

    // 查询
    search() {
      this.query.currentPage = 1;
      this.getEmployees();
    },

    // 分页切换
    handleCurrentChange(val) {
      this.query.currentPage = val;
      this.getEmployees();
    },

    // 获取员工列表（这是缺少的方法）
    getEmployees() {
      this.listLoading = true;
      this.$http.post("/org/employee/queryPage", this.query)
          .then(result => {
            this.pageData.total = result.data.data.total;
            this.pageData.rows = result.data.data.list;
            this.listLoading = false;
          })
          .catch(error => {
            console.error("获取员工列表失败:", error);
            this.listLoading = false;
            this.$message({
              message: '加载数据失败',
              type: 'error'
            });
          });
    },

    // 获取部门路径
    getDepartmentPath(deptId) {
      const findPath = (tree, id, path = []) => {
        for (let node of tree) {
          if (node.id === id) {
            path.push(node.id);
            return path;
          }
          if (node.children && node.children.length) {
            const found = findPath(node.children, id, [...path, node.id]);
            if (found.length) {
              return found;
            }
          }
        }
        return [];
      };

      return findPath(this.deptTree, deptId);
    },

    // 删除
    handleDel(index, row) {
      this.$confirm('确认删除该记录吗?', '温馨提示', {
        type: 'warning'
      }).then(() => {
        this.listLoading = true;
        let url = "/org/employee/" + row.id;
        this.$http.delete(url)
            .then((result) => {
              this.listLoading = false;
              result = result.data;
              if (result.success) {
                this.$message({
                  message: '删除成功',
                  type: 'success'
                });
                this.getEmployees();
              } else {
                this.$message({
                  message: result.message,
                  type: 'error'
                });
              }
            })
            .catch(error => {
              this.listLoading = false;
              this.$message({
                message: '删除失败',
                type: 'error'
              });
            });
      }).catch(() => {
        // 用户取消删除
      });
    },

    // 显示编辑界面（修复了回显问题）
    handleEdit(index, row) {
      this.title = "修改员工";
      this.employeeFormVisible = true;
      this.employeeFormRules.password = [{ required: false, message: '请输入密码', trigger: 'blur' }];

      // 等待DOM更新后设置数据
      this.$nextTick(() => {
        // 重置表单验证
        if (this.$refs.employeeForm) {
          this.$refs.employeeForm.clearValidate();
        }

        // 设置基本数据
        this.employee = {
          id: row.id,
          username: row.username,
          phone: row.phone,
          email: row.email,
          password: '', // 编辑时不显示原密码
          age: row.age || 0,
          state: row.state || 1,
          department: [],
          roles: []
        };

        // 部门回显
        if (row.department && row.department.id) {
          const path = this.getDepartmentPath(row.department.id);
          this.employee.department = path.length ? path : [row.department.id];
        }

        // 角色回显
        if (row.roles && Array.isArray(row.roles)) {
          this.employee.roles = row.roles.map(role => role.id);
        }

      });

    },

    // 显示新增界面
    handleAdd() {
      this.title = "添加员工";
      this.employeeFormVisible = true;
      this.employeeFormRules.password = [{ required: true, message: '请输入密码', trigger: 'blur' }];

      this.$nextTick(() => {
        // 重置表单
        this.employee = this.initEmployeeData();
        if (this.$refs.employeeForm) {
          this.$refs.employeeForm.resetFields();
          this.$refs.employeeForm.clearValidate();
        }
      });
    },

    // 保存
    save() {
      this.$refs.employeeForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认保存吗？', '提示', {}).then(() => {
            this.employeeLoading = true;
            let para = Object.assign({}, this.employee);

            // 处理部门参数：数组转对象
            if (para.department && Array.isArray(para.department)) {
              const deptId = para.department[para.department.length - 1];
              para.department = deptId ? {id: deptId} : null;
            }

            // 处理角色参数：数组转对象数组
            if (para.roles && Array.isArray(para.roles)) {
              para.roles = para.roles.map(roleId => ({id: roleId}));
            }

            // 根据是否有ID决定是新增还是修改
            const url = "/org/employee";
            const method = para.id ? "put" : "post";

            this.$http[method](url, para)
                .then((result) => {
                  this.employeeLoading = false;
                  result = result.data;
                  if (result.success) {
                    this.$message({
                      message: para.id ? '修改成功' : '添加成功',
                      type: 'success'
                    });
                    this.employeeFormVisible = false;
                    this.employee = this.initEmployeeData();
                    this.getEmployees();
                  } else {
                    this.$message({
                      message: result.message || '操作失败',
                      type: 'error'
                    });
                  }
                })
                .catch(error => {
                  this.employeeLoading = false;
                  this.$message({
                    message: '保存失败，请检查网络或服务器',
                    type: 'error'
                  });
                });
          }).catch(() => {
            // 用户取消保存
          });
        }
      });
    },

    // 选择变化
    selsChange(sels) {
      this.sels = sels;
    },

    // 批量删除
    batchRemove() {
      if (this.sels.length === 0) {
        this.$message({
          message: '请先选择要删除的记录',
          type: 'warning'
        });
        return;
      }

      var ids = this.sels.map(item => item.id);
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
                // 跳转到第一页
                this.query.currentPage = 1;
                this.getEmployees();
              } else {
                this.$message({
                  message: result.message || '删除失败',
                  type: 'error'
                });
              }
            })
            .catch(error => {
              this.listLoading = false;
              this.$message({
                message: '删除失败',
                type: 'error'
              });
            });
      }).catch(() => {
        // 用户取消删除
      });
    }
  },
  watch: {
    employeeFormVisible(newVal) {
      if (!newVal) {
        // 对话框关闭时重置数据
        this.employee = this.initEmployeeData();
        if (this.$refs.employeeForm) {
          this.$refs.employeeForm.clearValidate();
        }
      }
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
.toolbar {
  padding: 10px;
}
</style>