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
          <!--          v-perm="'role:add'"-->
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--列表:优秀实践，copy domain修改列表-->
    <el-table :data="pageData.data" highlight-current-row v-loading="listLoading" @selection-change="selsChange"
              style="width: 100%;">
      <el-table-column type="selection" width="55">
      </el-table-column>
      <el-table-column type="index" width="60">
      </el-table-column>
      <el-table-column prop="name" label="名称" width="220" sortable>
      </el-table-column>
      <el-table-column prop="sn" label="编码" width="180" sortable>
      </el-table-column>
      <el-table-column label="操作">
        <template scope="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <!--          v-perm="'role:delete'"-->
          <el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">
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

    <!--新增和编辑对话框-->
    <el-dialog :title="title" :visible.sync="roleFormVisible" :close-on-click-modal="false">
      <!--ref="roleForm" 指定表单名称，以后可以通过this.$refs.roleForm-->
      <el-form :model="role" label-width="80px" :rules="roleFormRules" ref="roleForm">
        <el-form-item label="名称" prop="name">
          <el-input v-model="role.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="sn">
          <el-input v-model="role.sn" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="分配菜单" prop="menus">
          <el-cascader v-model="role.menus" clearable
                       placeholder="试试搜索：指南"
                       :options="menuTree"
                       :props="{
									multiple: true,
								 	label:'name',
									value:'id'
								 }"
                       filterable></el-cascader>
        </el-form-item>

        <el-form-item label="分配权限" prop="permissions">
          <el-select v-model="role.permissions" multiple placeholder="请选择">
            <el-option
                v-for="item in permissionList"
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
        <el-button @click.native="roleFormVisible = false">取消</el-button>
        <el-button type="primary" @click.native="save" :loading="roleLoading">保存</el-button>
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
        data: []//数据表格
      },
      query: {
        keyword: '',
        currentPage: 1,//分页条
        pageSize: 10 //分页条
      },
      listLoading: false,
      sels: [],//列表选中列
      roleFormVisible: false,//编辑界面是否显示
      roleLoading: false,
      roleFormRules: {
        name: [
          {required: true, message: '请输入名称', trigger: 'blur'}
        ],
        sn: [
          {required: true, message: '请输角色编码', trigger: 'blur'}
        ],
        menus: [
          {required: true, message: '请分配菜单', trigger: 'blur'}
        ],
        permissions: [
          {required: true, message: '请分配权限', trigger: 'blur'}
        ],
      },
      menuTree: [],
      permissionList: [],

      //编辑界面数据
      role: {
        id: null,
        name: '',
        sn: '',
        menus: [22],
        permissions: null
      }
    }
  },
  methods: {
    handleChange(value) {
      this.role.index = value;
    },
    search() {
      //修改页码
      this.query.currentPage = 1;
      //改查询条件=双向绑定输入时自动改变，只需要重新查询一次就ok
      this.getRoles();
    },
    handleCurrentChange(val) {
      this.query.currentPage = val;
      this.getRoles();
    },
    //获取用户列表
    getRoles() {
      this.listLoading = true; //加载条
      this.$http.post("/system/role/queryPage", this.query)
          .then(result => {
            this.pageData.total = result.data.data.total;
            this.pageData.data = result.data.data.list;
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
        let url = "/system/role/" + row.id;
        this.$http.delete(url)
            .then((result) => {
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
              this.getRoles();
            });
      }).catch(() => {
      });
    },
    //显示编辑界面
    handleEdit: function (index, row) {
      //修改对话框标题
      this.title = "修改角色";
      this.getMenuTree();//加载父级角色

      //回显   克隆
      this.role = Object.assign({}, row);  // row : id, name sn
      if (row.menus) {//需要回显，这里要处已经拥有的权限的格式
        let menus = [];
        for (let i = 0; i < row.menus.length; i++) {
          let menu = row.menus[i];
          let menuTmp = [];
          if (menu.parent_id != null && menu.parent_id != "") {
            menuTmp.push(menu.parent_id);
          }
          menuTmp.push(menu.id);
          menus.push(menuTmp);
        }
        this.role.menus = menus;

        console.debug(this.role)
      }

      //弹出对话框
      this.roleFormVisible = true;

    },
    //显示新增界面
    handleAdd: function () {
      //修改对话框标题
      this.title = "添加角色";
      //弹出对话框
      this.roleFormVisible = true;
      //置空数据，防止先点修改，后点添加有值
      this.role = {
        id: null,
        name: '',
        sn: '',
        menus: null,
        permissions: null
      };
    },
    //保存
    save: function () {
      this.$refs.roleForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认保存吗？', '提示', {}).then(() => {
            this.roleLoading = true;
            let para = Object.assign({}, this.role);
            //处理分配菜单参数格式 menus:[[22,23],[22,24],[22,25]]---->menus:[23,24,25]
            let menusArr = para.menus;
            if (menusArr) {
              let menusTmp = [];
              for (var i = 0; i < menusArr.length; i++) {
                let menuTmp = menusArr[i];
                menusTmp.push(menuTmp[menuTmp.length - 1]);
              }
              para.menus = menusTmp;
            }
            this.$http.put("/system/role/save", para)
                .then((result) => {
                  this.roleLoading = false;
                  result = result.data;
                  if (result.success) {
                    this.$message({
                      message: '提交成功',
                      type: 'success'
                    });
                    this.roleFormVisible = false; //关闭对话框
                    this.$refs['roleForm'].resetFields();//重置表单
                    this.getRoles(); //重新加载
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
    },
    //批量删除
    batchRemove: function () {
      //遍历选择记录把id放到一个数组中
      var ids = this.sels.map(item => item.id); //[1,2,3]
      this.$confirm('确认删除选中记录吗？', '温馨提示', {
        type: 'warning'
      }).then(() => {
        this.listLoading = true;
        this.$http.post("/system/role", ids)
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
    getMenuTree() {
      this.$http.get("/system/menu/tree")
          .then(result => {
            if (result.data.success) {
              this.menuTree = result.data.data;
            }
          });
    },
    getPermissionList() {
      this.$http.get("/system/permission/queryAll")
          .then(result => {
            if (result.data.success) {
              this.permissionList = result.data.data;
            }
          });
    }
  },
  mounted() {
    this.getRoles();
    this.getMenuTree();//加载菜单树
    this.getPermissionList();//加载权限下拉列表
  }
}

</script>

<style scoped>

</style>