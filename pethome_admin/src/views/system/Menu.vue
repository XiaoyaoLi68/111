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
          <el-button type="primary" v-perm="'menu:add'" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--列表:优秀实践，copy domain修改列表-->
    <el-table :data="pageData.list" highlight-current-row v-loading="listLoading" @selection-change="selsChange"
              style="width: 100%;">
      <el-table-column type="selection" width="55">
      </el-table-column>
      <el-table-column type="index" width="60">
      </el-table-column>
      <el-table-column prop="name" label="名称" width="120" sortable>
      </el-table-column>
      <el-table-column prop="parent.name" label="父级菜单" width="120" sortable>
      </el-table-column>
      <el-table-column prop="url" label="访问URL" width="160" sortable>
      </el-table-column>
      <el-table-column prop="component" label="组件路径" width="220" sortable>
      </el-table-column>
      <el-table-column prop="icon" label="图标" width="200" sortable>
      </el-table-column>
      <el-table-column prop="intro" label="简介" width="200" sortable>
      </el-table-column>
      <!--<el-table-column prop="state" label="状态" width="120" sortable :formatter="stateFormatter">-->
      <el-table-column prop="state" label="状态" width="100" sortable>
        <template scope="scope">
          <span style="color: green" v-if="scope.row.state==0">正常</span>
          <span style="color: red" v-else="scope.row.state==-1">禁用</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="120">
        <template scope="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button type="danger" v-perm="'menu:delete'" size="small" @click="handleDel(scope.$index, scope.row)">
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
    <el-dialog :title="title" :visible.sync="menuFormVisible" :close-on-click-modal="false">
      <!--ref="menuForm" 指定表单名称，以后可以通过this.$refs.menuForm-->
      <el-form :model="menu" label-width="80px" :rules="menuFormRules" ref="menuForm">
        <el-form-item label="上级菜单" prop="parent">
          <el-select v-model="menu.parent" value-key="id" placeholder="请选择">
            <el-option
                v-for="item in menuTree"
                :key="item.id"
                :label="item.name"
                :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="menu.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="index">
          <el-input-number v-model="menu.index" controls-position="right" @change="handleChange" :min="1"
                           :max="10"></el-input-number>
        </el-form-item>
        <el-form-item label="访问URL" prop="url">
          <el-input v-model="menu.url" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="组件路径" prop="component">
          <el-input v-model="menu.component" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="标识" prop="sn">
          <el-input v-model="menu.sn" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="menu.icon" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="menu.state">
            <el-radio class="radio" :label="0">正常</el-radio>
            <el-radio class="radio" :label="-1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="menuFormVisible = false">取消</el-button>
        <el-button type="primary" @click.native="save" :loading="menuLoading">保存</el-button>
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
        list: []//数据表格
      },
      query: {
        keyword: '',
        currentPage: 1,//分页条
        pageSize: 10 //分页条
      },
      listLoading: false,
      sels: [],//列表选中列
      menuFormVisible: false,//编辑界面是否显示
      menuLoading: false,
      menuFormRules: {
        name: [
          {required: true, message: '请输入名称', trigger: 'blur'}
        ],
        index: [
          {required: true, message: '请输入标识', trigger: 'blur'}
        ],
        url: [
          {required: true, message: '请输入访问路径', trigger: 'blur'}
        ],
        component: [
          {required: true, message: '请输入组件路径', trigger: 'blur'}
        ],
      },
      menuTree: [],

      //编辑界面数据
      menu: {
        id: null,
        name: '',
        sn: '',
        index: 1,
        icon: '',
        url: '',
        component: '',
        intro: '',
        state: 0,
        parent: null
      }
    }
  },
  methods: {
    handleChange(value) {
      this.menu.index = value;
    },
    search() {
      //修改页码
      this.query.currentPage = 1;
      //改查询条件=双向绑定输入时自动改变，只需要重新查询一次就ok
      this.getMenus();
    },
    handleCurrentChange(val) {
      this.query.currentPage = val;
      this.getMenus();
    },
    //获取用户列表
    getMenus() {
      this.listLoading = true; //加载条
      this.$http.post("/system/menu/queryPage", this.query)
          .then(result => {
            this.pageData = result.data.data;
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
        let url = "/system/menu/" + row.id;
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
              this.getMenus();
            });
      }).catch(() => {
      });
    },
    //显示编辑界面
    handleEdit: function (index, row) {
      //修改对话框标题
      this.title = "修改菜单";
      this.getMenuTree();//加载父级菜单
      //弹出对话框
      this.menuFormVisible = true;
      //回显
      this.menu = Object.assign({}, row);
    },
    //显示新增界面
    handleAdd: function () {
      //修改对话框标题
      this.title = "添加菜单"
      //弹出对话框
      this.menuFormVisible = true;
      //置空数据，防止先点修改，后点添加有值
      this.menu = {
        id: null,
        name: '',
        sn: '',
        dirPath: '',
        state: 0,
        manager: {},
        parent: null
      };
      this.getMenuTree();//加载父级菜单
    },
    //保存
    save: function () {
      this.$refs.menuForm.validate((valid) => {
        if (valid) {
          this.$confirm('确认保存吗？', '提示', {}).then(() => {
            this.menuLoading = true;
            let para = Object.assign({}, this.menu);
            //处理上级菜单参数格式 parent:[1,34]---->parent:{id:34}
            let parentArr = para.parent;
            if (parentArr) {
              para.parent = {"id": parentArr[parentArr.length - 1]}
            }
            this.$http.put("/system/menu", para)
                .then((result) => {
                  this.menuLoading = false;
                  result = result.data;
                  if (result.success) {
                    this.$message({
                      message: '提交成功',
                      type: 'success'
                    });
                    this.menuFormVisible = false; //关闭对话框
                    this.$refs['menuForm'].resetFields();//重置表单
                    this.getMenus(); //重新加载
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
        this.$http.patch("/system/menu", ids)
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
      this.$http.get("/system/menu/first")
          .then(result => {
            if (result.data) {
              this.menuTree = result.data;
            }
          });
    }
  },
  mounted() {
    this.getMenus();
  }
}

</script>

<style scoped>

</style>