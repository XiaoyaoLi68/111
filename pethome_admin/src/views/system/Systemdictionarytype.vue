<template>
	<section>
		<!--高级查询-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item>
					<el-input v-model="filters.name" placeholder="姓名"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getDepartments">查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="handleAdd">新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--分页列表
          :data="departments" 绑定数据
          highlight-current-row 当前行高亮，选中行背景有颜色
          v-loading="listLoading" 盲等框(加载框)true 显示，false关闭
          @selection-change="selsChange" 选中改变时触发
          style="width: 100%;" 宽度
          type="selection" 显示多选框
          type="index" 序号
          sortable 排序
          :formatter="formatSex" 格式，不要直接删除
          size="small" 按钮大小 小按钮
          @click 点击事件
		-->
		<el-table :data="departments" highlight-current-row v-loading="listLoading" @selection-change="selsChange" style="width: 100%;">
			<el-table-column type="selection" width="55">
			</el-table-column>
			<el-table-column type="index" width="60">
			</el-table-column>
			<el-table-column prop="name" label="部门名称" width="120">
			</el-table-column>
			<el-table-column prop="sn" label="部门编号" width="100" sortable>
			</el-table-column>
			<el-table-column prop="dirPath" label="部门地址" width="100">
			</el-table-column>
			<el-table-column prop="state" label="状态" width="120" sortable>
        <template scope="scope"><!--scope当前表格的所有值-->
          <span v-if="scope.row.state==1" style="color: green">启用</span>
          <span v-else-if="scope.row.state==0" style="color: red">禁用</span>
        </template>
			</el-table-column>
			<el-table-column prop="manager.username" label="部门管理员" width="120">

			</el-table-column>
      <el-table-column prop="parent.name" label="上级部门" width="120">

      </el-table-column>
			<el-table-column label="操作">
				<template scope="scope">
					<el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
					<el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<!--2.分页-->
		<el-col :span="24" class="toolbar">
			<el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0">批量删除</el-button>
			<el-pagination layout="prev, pager, next" @current-change="handleCurrentChange"
                     :current-page="currentPage" :page-size="pageSize" :total="total" style="float:right;">
			</el-pagination>
		</el-col>

		<!--编辑界面-->
		<el-dialog width="50%" title="编辑" :visible.sync="editFormVisible" :close-on-click-modal="false">
			<el-form label-width="80px" :rules="editFormRules" ref="editForm">
				<el-form-item label="部门名称">
					<el-input v-model="editForm.name" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="状态">
					<el-radio-group v-model="editForm.state">
						<el-radio class="radio" :label="1">启用</el-radio>
						<el-radio class="radio" :label="0">禁用</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="部门编号" prop="sn">
					<el-input v-model="editForm.sn"></el-input>
				</el-form-item>

        <el-form-item label="部门管理者" >
<!--          <el-input v-model="editForm.managerId" ></el-input>-->
          <el-select clearable v-model="editForm.managerId" placeholder="请选择">
            <el-option
                v-for="emp in emps"
                :key="emp.id"
                :label="emp.username"
                :value="emp.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="上级部门" >
          <el-cascader clearable
              v-model="editForm.parentId"
              :options="deptTree"
              :props="{
                checkStrictly: true,
                value: 'id',
                label: 'name',
              }"
          ></el-cascader>
        </el-form-item>

			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="editFormVisible = false">取消</el-button>
				<el-button type="primary" @click="editSubmit" :loading="editLoading">提交</el-button>
			</div>
		</el-dialog>

		<!--新增界面-->
<!--		<el-dialog title="新增" :visible.sync="addFormVisible" :close-on-click-modal="false">
			<el-form :model="addForm" label-width="80px" :rules="addFormRules" ref="addForm">
				<el-form-item label="姓名" prop="name">
					<el-input v-model="addForm.name" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="性别">
					<el-radio-group v-model="addForm.sex">
						<el-radio class="radio" :label="1">男</el-radio>
						<el-radio class="radio" :label="0">女</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="年龄">
					<el-input-number v-model="addForm.age" :min="0" :max="200"></el-input-number>
				</el-form-item>
				<el-form-item label="生日">
					<el-date-picker type="date" placeholder="选择日期" v-model="addForm.birth"></el-date-picker>
				</el-form-item>
				<el-form-item label="地址">
					<el-input type="textarea" v-model="addForm.addr"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="addFormVisible = false">取消</el-button>
				<el-button type="primary" @click.native="addSubmit" :loading="addLoading">提交</el-button>
			</div>
		</el-dialog>-->
	</section>
</template>

<script>
	export default {
		data() {
			return {

        emps:[],

        deptTree:[],
        //1.数据列表展示需要的数据模型
        departments: [],//接收列表的list数据
        listLoading: false,//盲等框，默认关闭

        //2.分页列表
        total: 0,//总条数
        currentPage:1, //当前页
        pageSize:5, //每页展示的条数


				filters: {
					name: ''
				},

				sels: [],//列表选中列

				editFormVisible: false,//编辑界面是否显示
				editLoading: false,
				editFormRules: {
					name: [
						{ required: true, message: '请输入姓名', trigger: 'blur' }
					]
				},
				//编辑界面数据
				editForm: {
					id: null,
					name: '',
					sn: '',
					state: 0,
					managerId: null,
					parentId: null,
          dirPath: '',
				},

				//addFormVisible: false,//新增界面是否显示
				addLoading: false,
				addFormRules: {  // 校验规则
					name: [
						{ required: true, message: '请输入姓名', trigger: 'blur' }
					],
          sn: [
            { required: true, message: '请输入部门编号', trigger: 'blur' }
          ]
				},
				//新增界面数据
			/*	addForm: {
					name: '',
					sex: -1,
					age: 0,
					birth: '',
					addr: ''
				}*/

			}
		},
		methods: {

      // 查询部门tree
      queryDeptTree(){
        this.$http.get("/org/dept/tree")
            .then(resp=>{
              if(resp.data.success){
                this.deptTree = resp.data.data;
              }
            }).catch(resp=>{
              console.debug(resp);
              this.$message.error('网络繁忙，请稍后重试');
        })
      },

      /**
       * 查询所有员工信息
       */
      queryAllEmp(){
        this.$http.get("/org/emp/queryAll")
            .then(res=>{
              if(res.data.success){
                this.emps = res.data.data;
              }
        }).catch(res=>{
          this.$message.error('网络繁忙，请稍后重试');
        })
      },

      //1.获取部门列表
      getDepartments() {
        let para = {
          //传参，当前页和每页的条数
          currentPage: this.currentPage,
          pageSize: this.pageSize
        };
        this.listLoading = true;//打开盲等框
        //请求真实的后端接口，axios发送请求
        this.$http.post("/org/dept/queryPage",para).then(res=>{
          //成功
          //1.关闭盲等框
          this.listLoading = false;  // 忙等框
          //2.给列表赋值 departments,res.data 是封装的后端数据，res.data.data返回的数据{“totals”:50,"data":list}
          this.departments = res.data.data.data;//分页后，返回就是当前页的list数据
          //3.总条数赋值
          this.total= res.data.data.total;

          console.debug(res.data);
        }).catch(res=>{
          this.$message.error('网络繁忙，请稍后重试【400/404】');
        })
      },
      //2.分页点击事件，获取当前页码
      handleCurrentChange(val) {
        this.currentPage = val;//给当前页赋值
        this.getDepartments();//重新请求列表
      },

			//性别显示转换
			formatSex: function (row, column) {
				return row.sex == 1 ? '男' : row.sex == 0 ? '女' : '未知';
			},
			//删除
			handleDel: function (index, row) {
				this.$confirm('确认删除该记录吗?', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					//NProgress.start();
					let para = { id: row.id };
					removeUser(para).then((res) => {
						this.listLoading = false;
						//NProgress.done();
						this.$message({
							message: '删除成功',
							type: 'success'
						});
						this.getDepartments();
					});
				}).catch(() => {

				});
			},
			//显示编辑界面
			handleEdit: function (index, row) {

        // 克隆
				this.editForm = Object.assign({}, row);

        //   /2/5/6  ["","2","5","6"], 前端回显操作。
        if(this.editForm.parentId!=null){
          var dirPath= this.editForm.dirPath;

          var dirPathArr = dirPath.split("/");

          var parentIdArr= [];

          for (var i = 1; i < dirPathArr.length-1; i++){
            parentIdArr.push(parseInt(dirPathArr[i]))
          }
          this.editForm.parentId = parentIdArr;
        }

        this.editFormVisible = true;

			},

			//显示新增界面
			handleAdd: function () {
				this.editFormVisible = true;
				this.editForm = {
              id: null,
              name: '',
              sn: '',
              state: 0,
              managerId: null,
              parentId: null,
              dirPath: '',
        };
			},

			//保存
			editSubmit: function () {

        var ef = this.editForm;
        if(ef.parentId==null || ef.parentId.length == 0){
           ef.parentId = null;
        }else{
          ef.parentId = ef.parentId[ef.parentId.length-1]
        }
        this.$http.post("/org/dept/save",ef)
            .then(res=>{
            if(res.data.success){
              // 提示
              this.$message({
                message: '操作成功',
                type: 'success'
              });
              // 关闭模态框
              this.editFormVisible = false;
              // 刷新页面
              this.getDepartments();
            }else {
              this.$message.error("网络异常，稍后重试");
            }
        }).catch(res=>{
          this.$message.error('网络繁忙，请稍后重试【400/404】');
        })
			},
			//新增
			addSubmit: function () {

			},
			selsChange: function (sels) {
				this.sels = sels;
			},
			//批量删除
			batchRemove: function () {
				var ids = this.sels.map(item => item.id).toString();
				this.$confirm('确认删除选中记录吗？', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					//NProgress.start();
					let para = { ids: ids };
					batchRemoveUser(para).then((res) => {
						this.listLoading = false;
						//NProgress.done();
						this.$message({
							message: '删除成功',
							type: 'success'
						});
						this.getDepartments();
					});
				}).catch(() => {

				});
			}
		},
    //页面加载事件
		mounted() {
			this.getDepartments();

      this.queryAllEmp();

      // 部门树
      this.queryDeptTree();
		}
	}

</script>

<style scoped>

</style>