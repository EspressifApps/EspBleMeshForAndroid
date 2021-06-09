import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)
// 需要左方向动画的路由用this.$router.to('****')
Router.prototype.togo = function (path) {
  this.isleft = true
  this.isright = false
  this.push(path)
}
// 需要右方向动画的路由用this.$router.goRight('****')
Router.prototype.goRight = function (path) {
  this.isright = true
  this.isleft = false
  this.push(path)
}
// 需要返回按钮动画的路由用this.$router.goBack()，返回上一个路由
Router.prototype.goBack = function () {
  this.isright = true
  this.isleft = false
  this.go(-1)
}
// 点击浏览器返回按钮执行，此时不需要路由回退
Router.prototype.togoback = function () {
  this.isright = true
  this.isleft = false
}
// 点击浏览器前进按钮执行
Router.prototype.togoin = function () {
  this.isright = false
  this.isleft = true
}
export default new Router({
  routes: [
    {
      path: '/',
      name: 'index',
      component: (resolve) => require(['@/components/index'], resolve),
      redirect: '/home',
      children: [
        {
          path: '/home',
          name: 'home',
          component: (resolve) => require(['@/pages/index/home'], resolve)
        },
        {
          path: '/groups',
          name: 'groups',
          component: (resolve) => require(['@/pages/index/skills'], resolve)
        },
        {
          path: '/settings',
          name: 'settings',
          component: (resolve) => require(['@/pages/index/settings'], resolve)
        }
      ]
    },
    {
      path: '/Home/SIG',
      name: 'sigprovision',
      component: (resolve) => require(['@/pages/sigprovision'], resolve)
    },
    {
      path: '/Home/First',
      name: 'firstprovision',
      component: (resolve) => require(['@/pages/firstProvision'], resolve)
    },
    {
      path: '/Home/Operation',
      name: 'operation',
      component: (resolve) => require(['@/pages/operation'], resolve)
    },
    {
      path: '/Home/SIG/Capabilities',
      name: 'capabilities',
      component: (resolve) => require(['@/pages/capabilities'], resolve)
    },
    {
      path: '/Home/Text',
      name: 'text',
      component: (resolve) => require(['@/pages/text'], resolve)
    },
    {
      path: '/Groups/AddDevices',
      name: 'addDevices',
      component: (resolve) => require(['@/pages/groupAddDevice'], resolve)
    },
    {
      path: '/Groups/Operation',
      name: 'groupsOperation',
      component: (resolve) => require(['@/pages/groupsOperation'], resolve)
    },
    {
      path: '/Home/Operation/Upgrade',
      name: 'nodeUpgrade',
      component: (resolve) => require(['@/pages/OTAUpgrade'], resolve)
    },
    {
      path: '/Home/Operation/NewUpgrade',
      name: 'newNodeUpgrade',
      component: (resolve) => require(['@/pages/NewOTAUpgrade'], resolve)
    }
  ]
})
