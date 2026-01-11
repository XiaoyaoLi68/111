<template>
  <el-form :model="loginForm" :rules="rules2" ref="ruleForm2" label-position="left" label-width="0px" class="demo-ruleForm login-container">
    <h3 class="title">系统登录</h3>
    <el-form-item prop="username">
      <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="账号"></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
    </el-form-item>
    <el-checkbox v-model="checked" checked class="remember">记住密码</el-checkbox>
    <el-form-item style="width:100%;">
      <el-button type="primary" style="width:47%;" @click.native.prevent="handleSubmit2" :loading="logining">登录</el-button>
      <el-button type="success" style="width:47%;" @click.native.prevent="shopRegister" :loading="logining">店铺入驻</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
  import { requestLogin } from '../api/api';
  //import NProgress from 'nprogress'
  export default {
    data() {
      return {
        logining: false,
        loginForm: {
          username: '东邪',
          password: '123',
          type: 0 // 0 : 管理端
        },
        rules2: {
          username: [
            { required: true, message: '请输入账号', trigger: 'blur' },
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            //{ validator: validaePass2 }
          ]
        },
        checked: true
      };
    },
    methods: {
      initIndexRouters(){
        //防止重复配置路由
        if(this.$router.options.routes.length>6){
          return;
        }
        let menus = localStorage.getItem('menus');
        menus = JSON.parse(menus);
        let tempRouters = [];
        menus.forEach(menu=>{
          let indexRouter = {
            path: '/',
            name: menu.name,
            iconCls: menu.icon,
            component: resolve => require(['@/views/Home'], resolve),
            children: []
          }
          console.log(menu.name)
          menu.children.forEach(cMenu=>{
            let cr = {
              path: cMenu.url,
              name: cMenu.name,
              component: resolve => require(['@/views/'+cMenu.component], resolve)
            }
            indexRouter.children.push(cr)
          })
          tempRouters.push(indexRouter)
          this.$router.options.routes.push(indexRouter)
        })
        //动态路由配置
        console.log(this.$router)
        this.$router.addRoutes(tempRouters);
      },


      // 店铺入驻
      shopRegister(){
        this.$router.push({ path: '/shopRegister' });

      },

      handleReset2() {
        this.$refs.ruleForm2.resetFields();
      },
      handleSubmit2(ev) {
        var _this = this;
        this.$refs.ruleForm2.validate((valid) => {

          if (valid) {
            // 自己写发送请求
            this.$http.post("/login/accountLogin",this.loginForm)
                .then(resp=>{
                  // 成功
                  if(resp.data.success){

                    // 把token，loginInfo 存入localStorage中
                    // 解构表达式， 对象
                    let {token,loginInfo,permissions,menus} = resp.data.data;
                    localStorage.setItem("token",token);
                    // localStorage: 只能存字符串
                    localStorage.setItem("loginInfo",JSON.stringify(loginInfo));
                    localStorage.setItem("permissions",JSON.stringify(permissions));
                    localStorage.setItem("menus",JSON.stringify(menus));

                    this.initIndexRouters(); // 动态路由菜单

                    // 跳转页面
                    this.$router.push({ path: '/echarts' });

                  }else{
                    this.$message.error(resp.data.message)
                  }

                }).catch(resp=>{
                  this.$message.error('网络繁忙，请稍后重试')
            })


          } else {
            return false;
          }
        });
      }
    }
  }

</script>

<style lang="scss" scoped>
  .login-container {
    /*box-shadow: 0 0px 8px 0 rgba(0, 0, 0, 0.06), 0 1px 0px 0 rgba(0, 0, 0, 0.02);*/
    -webkit-border-radius: 5px;
    border-radius: 5px;
    -moz-border-radius: 5px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
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
</style>