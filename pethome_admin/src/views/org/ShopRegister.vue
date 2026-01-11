<template>
  <div>
    <!--:model="shop" 数据双向绑定-->
    <!--ref="shopForm" id="shopForm",给form去一个名字-->
    <!--:rules="formRules" 校验规则-->
    <el-form :model="shop" ref="shopForm" :rules="formRules" label-position="left" label-width="100px"
             class="demo-ruleForm login-container">
      <h3 class="title">店铺入驻</h3>
      <el-form-item prop="name" label="名称">
        <el-input type="text" v-model="shop.name" auto-complete="off" placeholder="请输入店铺名称！"></el-input>
      </el-form-item>
      <el-form-item prop="tel" label="座机">
        <el-input type="text" v-model="shop.tel" auto-complete="off" placeholder="请输入座机！"></el-input>
      </el-form-item>
      <el-form-item prop="address" label="店铺地址">
        <el-input type="text" v-model="shop.address" auto-complete="off" placeholder="请输入地址！"></el-input>
        <el-button size="small" type="primary" @click="selectAdrress">选择</el-button>
      </el-form-item>

      <!--
        action: 文件上传的路径
        on-success: 上传成功的回调函数
        on-remove: 删除文件后的回调函数
       -->
      <el-form-item prop="logo" label="店铺Logo">
        <el-upload
            class="upload-demo"
            action="http://localhost:8080/org/shop/fileUpload"
            :on-remove="handleRemove"
            :on-success="handleSuccess"
            :file-list="fileList"
            list-type="picture">
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
      </el-form-item>
      <h3 class="title">管理员信息设置</h3>
      <el-form-item prop="admin.username" label="账号">
        <el-input type="text" v-model="shop.admin.username" auto-complete="off" placeholder="请输入账号！"></el-input>
      </el-form-item>
      <el-form-item prop="admin.phone" label="手机号码">
        <div class="phone-verify-wrapper">
          <el-input type="text" v-model="shop.admin.phone" auto-complete="off" placeholder="请输入电话！" class="phone-input"></el-input>
          <el-button type="primary" @click="sendVerifyCode" :disabled="countdown > 0" class="verify-btn">
            {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
          </el-button>
        </div>
      </el-form-item>
      <el-form-item prop="admin.verifyCode" label="验证码">
        <el-input type="text" v-model="shop.admin.verifyCode" auto-complete="off" placeholder="请输入验证码！"></el-input>
      </el-form-item>
      <el-form-item prop="admin.email" label="电子邮件">
        <el-input type="text" v-model="shop.admin.email" auto-complete="off" placeholder="请输入邮件！"></el-input>
      </el-form-item>
      <el-form-item prop="admin.password" label="密码">
        <el-input type="password" v-model="shop.admin.password" auto-complete="off"
                  placeholder="请输入密码！"></el-input>
      </el-form-item>
      <el-form-item prop="admin.comfirmPassword" label="确认密码">
        <el-input type="password" v-model="shop.admin.comfirmPassword" auto-complete="off"
                  placeholder="请输入确认密码！"></el-input>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button type="primary" style="width:100%;" @click.native.prevent="settledIn">入驻</el-button>
      </el-form-item>
    </el-form>
    <el-dialog
        title="选择地址"
        :visible.sync="dialogVisable"
        width="30%">
      <baidu-map :center="mapCenter" :zoom="11" @click="getClickInfo">
        <bm-view class="map"></bm-view>
        <bm-marker :position="{lng: selectedLng, lat: selectedLat}" v-if="selectedLng && selectedLat"></bm-marker>
        <bm-control :offset="{width: '10px', height: '10px'}">
          <!--:sugStyle="{zIndex: 2100} 让搜索提示上浮-->
          <bm-auto-complete v-model="keyword" :sugStyle="{zIndex: 2100}">
            <div style="margin-bottom:10px">
              <input id="searchInput" type="text" placeholder="请输入关键字" class="searchinput"/>
              <el-button type="success" @click="selectAdrressConfirm">确定</el-button>
            </div>
          </bm-auto-complete>
        </bm-control>
        <bm-local-search :keyword="keyword" :auto-viewport="true" @searchcomplete="handleSearchComplete"></bm-local-search>
      </baidu-map>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisable=false">取 消</el-button>
        <el-button type="primary" @click="confirmAddress">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.shop.admin.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    var validatePhone = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码'))
      } else {
        callback()
      }
    }
    return {
      keyword: '',
      dialogVisable: false,
      fileList: [],
      countdown: 0, // 倒计时秒数
      timer: null, // 定时器
      selectedLng: '', // 选择的经度
      selectedLat: '', // 选择的纬度
      mapCenter: {lng: 116.403765, lat: 39.914850}, // 地图中心点
      //shop:shop 为了做数据表单校验不要嵌套对象
      shop: {
        admin: {
          username: "",
          phone: "",
          email: "",
          password: "",
          comfirmPassword: "",
          verifyCode: "" // 新增验证码字段
        },
        name: '',
        tel: '',
        address: '',
        logo: '',
        longitude: '', // 新增经度字段
        latitude: '' // 新增纬度字段
      },
      formRules: {
        name: [
          {required: true, message: '请输入店铺名称!', trigger: 'blur'}
        ],
        tel: [
          {required: true, message: '请输入店铺座机!', trigger: 'blur'}
        ],
        address: [
          {required: false, message: '请输入店铺地址!', trigger: 'blur'}
        ],
        'admin.phone': [
          {required: true, validator: validatePhone, trigger: 'blur'}
        ],
        'admin.verifyCode': [
          {required: true, message: '请输入验证码!', trigger: 'blur'}
        ],
        // logo: [
        //     { required: true, message: '请输入店铺logo!', trigger: 'blur' }
        // ],
        'admin.email': [
          {type: 'email', required: true, message: '请输入邮箱!', trigger: 'blur'}
        ],
        'admin.username': [
          {required: true, message: '请输入账号!', trigger: 'blur'}
        ],
        'admin.password': [
          {required: true, message: '请输入密码!', trigger: 'blur'}
        ],
        'admin.comfirmPassword': [
          {required: true, validator: validatePass2, trigger: 'blur'} //自定义校验规则
        ]
      }
    };
  },
  methods: {
    // 发送验证码
    sendVerifyCode() {
      // 先验证手机号是否正确
      if (!this.shop.admin.phone) {
        this.$message({
          message: '请先输入手机号码!',
          type: 'warning'
        });
        return;
      }
      if (!/^1[3-9]\d{9}$/.test(this.shop.admin.phone)) {
        this.$message({
          message: '请输入正确的手机号码!',
          type: 'warning'
        });
        return;
      }

      // 发送验证码请求
      this.$http.post("/org/shop/sendVerifyCode", {
        phone: this.shop.admin.phone
      }).then((res) => {
        if (res.data.success) {
          this.$message({
            message: '验证码已发送!',
            type: 'success'
          });
          // 开始倒计时
          this.countdown = 3;
          this.timer = setInterval(() => {
            this.countdown--;
            if (this.countdown <= 0) {
              clearInterval(this.timer);
              this.timer = null;
            }
          }, 1000);
        } else {
          this.$message({
            message: res.data.message || '验证码发送失败!',
            type: 'error'
          });
        }
      }).catch(() => {
        this.$message({
          message: '验证码发送失败!',
          type: 'error'
        });
      });
    },
    // 地图点击事件，获取经纬度
    getClickInfo(e) {
      this.selectedLng = e.point.lng;
      this.selectedLat = e.point.lat;
      // 更新地图中心点
      this.mapCenter = {lng: e.point.lng, lat: e.point.lat};
    },
    // 搜索完成后获取第一个结果的经纬度
    handleSearchComplete(results) {
      if (results && results.length > 0) {
        const firstResult = results[0];
        this.selectedLng = firstResult.point.lng;
        this.selectedLat = firstResult.point.lat;
        // 更新地图中心点
        this.mapCenter = {lng: firstResult.point.lng, lat: firstResult.point.lat};
      }
    },
    // 确认选择地址（点击对话框的确定按钮）
    confirmAddress() {
      // 将选中的经纬度赋值给shop对象
      if (this.selectedLng && this.selectedLat) {
        this.shop.longitude = this.selectedLng;
        this.shop.latitude = this.selectedLat;
      }
      this.dialogVisable = false;
    },
    selectAdrressConfirm() {
      //获取值搜索框值,设置给地址
      var searchInputV = document.getElementById("searchInput").value;
      this.shop.address = searchInputV;
      // 同时保存经纬度
      if (this.selectedLng && this.selectedLat) {
        this.shop.longitude = this.selectedLng;
        this.shop.latitude = this.selectedLat;
      }
      //关闭对话框
      this.dialogVisable = false;
    },
    selectAdrress() {
      this.dialogVisable = true;
    },
    //文件上传成功回调
    handleSuccess(response, file, fileList) {
      // 将后端返回的文件地址保存到shop.logo中
      if (response && response.data) {
        this.shop.logo = response.data;
        this.$message({
          message: '文件上传成功!',
          type: 'success'
        });
      }
    },
    //文件删除
    handleRemove(file, fileList) {
      console.debug(file, fileList)
      // 判断file是否有response属性
      if (file.response && file.response.data) {
        var filePath = file.response.data;
        this.$http.delete("/org/shop/delete/?url=" + filePath)
            .then(res => {
              if (res.data.success) {
                // 清空logo字段
                this.shop.logo = '';
                this.$message({
                  message: '删除成功!',
                  type: 'success'
                });
              } else {
                this.$message({
                  message: '删除失败!',
                  type: 'error'
                });
              }
            })
      } else {
        // 清空logo字段
        this.shop.logo = '';
      }
    },
    //图片预览
    handlePreview(file) {
      console.log(file);
    },
    //提交入驻
    settledIn() {
      this.$refs.shopForm.validate((valid) => {
        //校验表单成功后才做一下操作
        if (valid) {
          // 检查是否选择了地址并获取了经纬度
          /*if (!this.shop.longitude || !this.shop.latitude) {
            this.$message({
              message: '请选择店铺地址并确保获取到经纬度信息!',
              type: 'warning'
            });
            return;
          }*/

          this.$confirm('确认入驻吗？', '提示', {}).then(() => {
            //拷贝后面对象的值到新对象,防止后面代码改动引起模型变化
            let para = Object.assign({}, this.shop); // shop 本身这个参数里面就有店铺和管理员信息，现在包含经纬度和验证码

            // 确保经纬度是数字类型
            para.longitude = parseFloat(para.longitude);
            para.latitude = parseFloat(para.latitude);
            if(para.logo.data){
              para.logo = para.logo.data;
            }
            //alert(JSON.stringify(para.logo))
            console.log('提交的数据:', para); // 调试用，查看提交的数据

            // 为了后台好接收，封装一个对象放到里面
            //判断是否有id有就是修改,否则就是添加
            this.$http.post("/org/shop/onboarding", para).then((res) => {
              if (res.data.success) {
                this.$message({
                  message: '操作成功!',
                  type: 'success'
                });
                //重置表单
                this.$refs['shopForm'].resetFields();
                // 清除经纬度
                this.selectedLng = '';
                this.selectedLat = '';
                this.shop.longitude = '';
                this.shop.latitude = '';
                this.shop.logo = '';
                //跳转登录页面
                this.$router.push({path: '/login'});
              } else {
                this.$message({
                  message: res.data.message,
                  type: 'error'
                });
              }

            });
          });
        }
      })
    }
  },
  // 组件销毁前清除定时器
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }
}

</script>
<style lang="scss" scoped>
.login-container {
  -webkit-border-radius: 5px;
  border-radius: 5px;
  -moz-border-radius: 5px;
  background-clip: padding-box;
  margin: 180px auto;
  width: 500px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;

  .title {
    margin: 0px auto 40px auto;
    text-align: center;
    color: #505458;
  }

  .remember {
    margin: 0px 0px 35px 0px;
  }
}

.phone-verify-wrapper {
  display: flex;
  align-items: center;
  width: 100%;

  .phone-input {
    flex: 1;
    margin-right: 10px;
  }

  .verify-btn {
    flex-shrink: 0;
    white-space: nowrap;
  }
}

.map {
  width: 100%;
  height: 300px;
}

.searchinput {
  width: 300px;
  box-sizing: border-box;
  padding: 9px;
  border: 1px solid #dddee1;
  line-height: 20px;
  font-size: 16px;
  height: 38px;
  color: #333;
  position: relative;
  border-radius: 4px;
}
</style>