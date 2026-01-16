import babelpolyfill from 'babel-polyfill'
import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';
//import './assets/theme/theme-green/index.css'
import VueRouter from 'vue-router'
import store from './vuex/store'
import Vuex from 'vuex'
//import NProgress from 'nprogress'
//import 'nprogress/nprogress.css'
import routes from './routes'
/*import Mock from './mock'
Mock.bootstrap();*/
import 'font-awesome/css/font-awesome.min.css'
import BaiduMap from 'vue-baidu-map'


Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.use(Vuex)

Vue.use(BaiduMap, {
    /* Visit http://lbsyun.baidu.com/apiconsole/key for details about app key. */
    ak: 'BvjDJrFkRbFaqVYgE79fzJbljtDtaJpk'
})
import '@/common/js/permissions'
//NProgress.configure({ showSpinner: false });
const router = new VueRouter({
    routes
})
import xxyy from "./vue_axios";
// 路由的静态资源拦截器
// 默认是否进入到登录页面
router.beforeEach((to, from, next) => {

    if (to.path == '/login' || to.path == '/shopRegister') {

        if (localStorage.getItem("token")) {
            location.reload();
            localStorage.removeItem('token');
            localStorage.removeItem('loginInfo');
            localStorage.removeItem('menus');
            localStorage.removeItem('permissions');
        }
        next(); //
    } else {
        let loginInfo = JSON.parse(localStorage.getItem('loginInfo'));
        if (!loginInfo && to.path != '/login') {
            next({path: '/login'})
        } else {
            next();
        }
    }
})

new Vue({
    //el: '#app',
    //template: '<App/>',
    router,
    store,
    //components: { App }
    render: h => h(App)
}).$mount('#app')

//处理页面刷新动态路由失效问题
initIndexRouters();

function initIndexRouters() {
    if (!localStorage.menus) {
        return;
    }
    //防止重复配置路由
    if (router.options.routes.length > 6) {
        return;
    }
    let menus = localStorage.getItem('menus');
    menus = JSON.parse(menus);
    let tempRouters = [];
    menus.forEach(menu => {
        let indexRouter = {
            path: '/',
            iconCls: menu.icon,
            name: menu.name,
            component: resolve => require(['@/views/Home'], resolve),
            children: []
        }
        menu.children.forEach(cMenu => {
            let cr = {
                path: cMenu.url,
                name: cMenu.name,
                component: resolve => require(['@/views/' + cMenu.component], resolve)
            }
            indexRouter.children.push(cr)
        })
        tempRouters.push(indexRouter)
        router.options.routes.push(indexRouter)
    })
    //动态路由配置
    router.addRoutes(tempRouters);
}



