import Vue from 'vue'
//1.导入axios
import axios from "axios"
import routes from './routes'
import VueRouter from "vue-router";

//2.配置基础路径
axios.defaults.baseURL="http://localhost:8080"
//3.axios配置给Vue对象
Vue.prototype.$http=axios
//1 使用axios前置拦截器，让所有的请求都携带uToken
axios.interceptors.request.use(config=>{
    //携带token
    let token =  localStorage.getItem("token");
    if(token){
        config.headers['token']=token;
    }
    return config;
},error => {
    Promise.reject(error);
});
// =============================================================
//2 使用axios后置拦截器，处理没有登录请求
/*axios.interceptors.response.use(result=>{

    console.debug(result);
    let data = result.data;
    if(!data.success && data.message==="noLogin"){
        localStorage.removeItem("token");
        localStorage.removeItem("loginInfo")
        // 原始方式
        location.href="http://localhost:8083/#/login";
    }
    return result;
},error => {
    Promise.reject(error);
})*/


// 路由的静态资源拦截器 默认就是注释的
/*VueRouter.beforeEach((to, from, next) => {
    alert("路由。。。。");
    if (to.path == '/login') {
        localStorage.removeItem('token');
        localStorage.removeItem('loginInfo');
    }
    let loginInfo = JSON.parse(localStorage.getItem('loginInfo'));
    if (!loginInfo && to.path != '/login') {
        next({ path: '/login' })
    } else {
        next();
    }
})*/

