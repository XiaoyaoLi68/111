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
axios.interceptors.response.use(result=>{

    console.debug(result);
    let data = result.data;
    if(!data.success && data.message==="noLogin"){
        localStorage.removeItem("token");
        localStorage.removeItem("loginInfo")
        // 原始方式
        location.href="http://localhost/login.html";
    }
    return result;
},error => {
    Promise.reject(error);
})


// 配置静态资源拦截器
// 1. 获取到请求的uri
var url =location.href;
var uri = url.substring(url.lastIndexOf('/')+1);

if(uri!="login.html" && uri!="register.html" && uri!="index.html" && uri!="pet.html"){
    var loginInfo = localStorage.getItem("loginInfo");
    if(!loginInfo){
        location.href="http://localhost/login.html";
    }
}
