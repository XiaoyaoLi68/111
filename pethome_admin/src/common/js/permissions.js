import Vue from 'vue';
// 注册一个全局自定义指令 `v-perm`
Vue.directive('perm', {
    // 当被绑定的元素插入到 DOM 中时……
    inserted: (el, binding, vnode) => {
        const value = binding.value;//显示这个按钮需要的权限 // role:delete
        let permissions = localStorage.getItem('permissions');//当前用户拥有什么权限
        if (permissions){
            let auths = JSON.parse(permissions); // 转换成数组
            if (auths && auths.join(",").indexOf(value)==-1) {
                el.parentNode.removeChild(el);
            }
        }
    }
});