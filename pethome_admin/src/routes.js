import Login from './views/Login.vue'
import NotFound from './views/404.vue'
import Home from './views/Home.vue'
import Main from './views/Main.vue'
import Table from './views/nav1/Table.vue'
import Pet from './views/pet/Pet.vue'
import SearchMasterMsgPool from './views/searchMsg/SearchMasterMsgPool.vue'
import SearchMasterMsgToAudit from './views/searchMsg/SearchMasterMsgToAudit.vue'
import SearchMasterMsgTohandle from './views/searchMsg/SearchMasterMsgTohandle.vue'
import SearchMasterMsgFinish from './views/searchMsg/SearchMasterMsgFinish.vue'
import echarts from './views/charts/echarts.vue'
import Department from './views/org/Department'
import Systemdictionarytype from "./views/system/Systemdictionarytype.vue";
import Systemdictionarydetail from "./views/system/Systemdictionarydetail.vue";
import ShopRegister from "./views/org/ShopRegister.vue";
import Shop from "./views/org/Shop.vue";
import Menu from "./views/system/Menu.vue";
import Permission from "./views/system/Permission.vue";
import Role from "./views/system/Role.vue";
import Employee from "./views/org/Employee.vue";

let routes = [
    {
        path: '/shopRegister',
        component: ShopRegister,
        name: '店铺入驻',
        hidden: true  //隐藏 hidden: true
    },
    {
        path: '/login',
        component: Login,
        name: '',
        hidden: true  //隐藏 hidden: true
    },
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },
    {
        path: '/',
        component: Home,
        name: 'Charts',
        iconCls: 'el-icon-s-home',
        leaf: true,//只有一个节点
        children: [
            { path: '/echarts', component: echarts, name: 'echarts' }
        ]
    },
    { path: '/main', component: Main },
    {
        path: '/',
        component: Home,
        name: '组织架构管理',
        iconCls: 'el-icon-message',//图标样式class
        children: [
            { path: '/department', component: Department, name: '部门管理' },
            { path: '/employee', component: Employee, name: '员工管理' },
            { path: '/shop', component: Shop, name: '店铺管理' },
        ]
    },
    {
        path: '/',
        component: Home,
        name: '宠物管理',
        leaf: true,//只有一个节点
        iconCls: 'el-icon-s-home',
        children: [
            { path: '/pet', component: Pet, name: '宠物管理' }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '寻主消息',
        iconCls: 'el-icon-s-home',
        children: [
            { path: '/searchMasterMsgPool', component: SearchMasterMsgPool, name: '寻主池' },
            { path: '/searchMasterMsgToAudit', component: SearchMasterMsgToAudit, name: '待审核' },
            { path: '/searchMasterMsgTohandle', component: SearchMasterMsgTohandle, name: '待处理' },
            { path: '/searchMasterMsgFinish', component: SearchMasterMsgFinish, name: '完成' },
        ]
    },
    {
        path: '/',
        component: Home,
        name: '系统管理',
        iconCls: 'el-icon-setting',//图标样式class
        children: [
            { path: '/role', component: Role, name: '角色管理' },
            { path: '/permission', component: Permission, name: '权限管理' },
            { path: '/menu', component: Menu, name: '菜单管理' },
            { path: '/log', component: Table, name: '系统日志' },
            { path: '/systemdictionarytype', component: Systemdictionarytype, name: '数据字典类型' },
            { path: '/systemdictionarydetail', component: Systemdictionarydetail, name: '数据字典详情' },
        ]
    }/*,
    {
        path: '/',
        component: Home,
        name: '导航二',
        iconCls: 'fa fa-id-card-o',
        children: [
            { path: '/page4', component: Page4, name: '页面4' },
            { path: '/page5', component: Page5, name: '页面5' }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '',
        iconCls: 'fa fa-address-card',
        leaf: true,//只有一个节点
        children: [
            { path: '/page6', component: Page6, name: '导航三' }
        ]
    }*/
];

export default routes;