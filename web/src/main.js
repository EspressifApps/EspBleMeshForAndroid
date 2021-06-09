// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import axios from 'axios'
import VueAxios from 'vue-axios'
import Mint, {Toast} from 'mint-ui'
import less from 'less'
import 'mint-ui/lib/style.css'
import VueI18n from 'vue-i18n'
import FastClick from 'fastclick'
import Util from './assets/tool/utils'
import './assets/css/general.css'
import VueTouch from 'vue-touch'
import Constant from './assets/tool/constant'
Vue.use(VueTouch, {name: 'v-touch'})
FastClick.attach(document.body)
Vue.config.productionTip = false
axios.defaults.timeout = 30000
Vue.use(VueAxios, axios)
Vue.use(Mint)
Vue.use(less)
Vue.use(VueI18n)
/* eslint-disable no-new */
var i18n = new VueI18n({
  locale: 'zh',
  messages: {
    'zh': require('@/assets/lang/zh').message, // 中文语言包
    'en': require('@/assets/lang/en').message // 英文语言包
  }
})
new Vue({
  el: '#app',
  i18n,
  router,
  store,
  components: { App },
  template: '<App/>',
  mounted: function () {
    Util.initColorWheel()
    setTimeout(function () {
      EspBleMesh.getAppInfo()
      EspBleMesh.loadData('homeDeviceList')
      EspBleMesh.loadData('sigProvisionList')
      EspBleMesh.loadData('deviceGroupList')
      EspBleMesh.loadData('homeSelectList')
    }, 1000)
  },
  created: function () {
    const self = this
    window['getAppInfoCallback'] = (res) => {
      self.getAppInfoCallback(res)
    }
    window['requestPermissionsCallback'] = (res) => {
      self.requestPermissionsCallback(res)
    }
    window['phoneStateChangedCallback'] = (res) => {
      self.phoneStateChangedCallback(res)
    }
    window['onActivityPause'] = () => {
      self.onActivityPause()
    }
    window['loadDataCallback'] = (res) => {
      self.loadDataCallback(res)
    }
  },
  methods: {
    loadDataCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      res = JSON.parse(res)
      console.log('FBY loadDataCallback' + JSON.stringify(res))
      if (!Util._isEmpty(res)) {
        switch(res.key) {
          case Constant.KEY_HOME_DEVICE_LIST:
            if (Util._isEmpty(res.value)) {
              self.$store.commit('setHomeDeviceList', [])
            } else {
              self.$store.commit('setHomeDeviceList', JSON.parse(res.value))
            }
            break
          case Constant.KEY_SIG_PROVISION_LIST:
            if (Util._isEmpty(res.value)) {
              self.$store.commit('setSigProvisionList', [])
            } else {
              self.$store.commit('setSigProvisionList', JSON.parse(res.value))
            }
            break
          case Constant.KEY_DEVICE_GROUP_LIST:
            if (Util._isEmpty(res.value)) {
              self.$store.commit('setDeviceGroupList', [])
            } else {
              self.$store.commit('setDeviceGroupList', JSON.parse(res.value))
            }
            break
          case Constant.KEY_HOME_SELECT_LIST:
            if (Util._isEmpty(res.value)) {
              self.$store.commit('setHomeSelectList', JSON.parse([self.$t('networknodes'), self.$t('fastnodes')]))
            } else {
              self.$store.commit('setHomeSelectList', JSON.parse(res.value))
            }
            break
        }
      }
    },
    // 程序进入后台
    onActivityPause () {
      console.log('程序进入后台')
      // localStorage.setItem('homeDeviceList', JSON.stringify(this.$store.state.homeDeviceList))
      // localStorage.setItem('sigProvisionList', JSON.stringify(this.$store.state.sigProvisionList))
      // localStorage.setItem('deviceGroupList', JSON.stringify(this.$store.state.deviceGroupList))
      // localStorage.setItem('homeSelectList', JSON.stringify(this.$store.state.homeSelectList))
      var homeDeviceList = {}
      homeDeviceList['key'] = 'homeDeviceList'
      homeDeviceList['value'] = JSON.stringify(this.$store.state.homeDeviceList)
      EspBleMesh.saveData(JSON.stringify(homeDeviceList))
      var sigProvisionList = {}
      sigProvisionList['key'] = 'sigProvisionList'
      sigProvisionList['value'] = JSON.stringify(this.$store.state.sigProvisionList)
      EspBleMesh.saveData(JSON.stringify(sigProvisionList))
      var deviceGroupList = {}
      deviceGroupList['key'] = 'deviceGroupList'
      deviceGroupList['value'] = JSON.stringify(this.$store.state.deviceGroupList)
      EspBleMesh.saveData(JSON.stringify(deviceGroupList))
      var homeSelectList = {}
      homeSelectList['key'] = 'homeSelectList'
      homeSelectList['value'] = JSON.stringify(this.$store.state.homeSelectList)
      EspBleMesh.saveData(JSON.stringify(homeSelectList))
    },
    getAppInfoCallback: function (res) {
      res = Util.base64().decode(res)
      EspBleMesh.webLog('FBY getAppInfoCallback' + res)
      res = JSON.parse(res)
      if (res.language === 'zh') {
        this.$i18n.locale = 'zh'
      } else {
        this.$i18n.locale = 'en'
      }
      this.$store.commit('setSystemInfo', res)
      this.$store.commit('setBlueInfo', res.isBluetoothEnabled)
      if (res.osVersion >= 23) { // Android M
        if (!res.isLocationGranted) {
          EspBleMesh.requestPermissions()
        }
        this.$store.commit('setLocationGranted', res.isLocationGranted)
      }
      if (res.osVersion >= 28) { // Android P
        if (!res.isLocationEnabled) {
          // hint turn on GPS
        }
        this.$store.commit('setLocationEnabled', res.isLocationEnabled)
      }
    },
    requestPermissionsCallback: function (res) {
      res = Util.base64().decode(res)
      EspBleMesh.webLog('FBY getAppInfoCallback' + res)
      res = JSON.parse(res)
      this.$store.commit('setLocationGranted', res.isLocationGranted)
    },
    phoneStateChangedCallback: function (res) {
      res = Util.base64().decode(res)
      var systemInfo = this.$store.state.systemInfo
      EspBleMesh.webLog('FBY phoneStateChangedCallback' + res)
      res = JSON.parse(res)
      this.$store.commit('setBlueInfo', res.isBluetoothEnabled)
      if (!res.isBluetoothEnabled) {
        this.$store.commit('setIsBleConnect', false)
      }
      if (systemInfo.osVersion >= 28) {
        this.$store.commit('setLocationEnabled', res.isLocationEnabled)
      }
    }
  }
})

// // 将方法挂载到vue原型上
// Vue.prototype.changeData = function () {
//   alert('执行成功')
// }

