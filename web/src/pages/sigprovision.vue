<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide"><i class="icon-left back"></i></span>{{configType == 1 ? $t('sigprovision') : $t('fastprovision')}}</h4>
      <div v-show="!getBlueEnable"  @click="showBlueFail" class="bluetooth-second-base">
        <i class="icon-bluetooth bluetooth-second-style"></i>
      </div>
      <span @click="scanDevice(isBleScan)" class="right-top-text">{{isBleScan ? $t('scan') : $t('stopscan')}}</span>
    </div>
    <div class="device-list-base">
      <div class="overflow-touch">
        <div @click.self="provisionItem(item)" v-for="(item, index) in scanDeviceList" :key="index" class="device-list-cell">
          <div class="item-content">
            <span @click.stop="provisionItem(item)">{{item.name}}</span>
            <span @click.stop="provisionItem(item)" class="desc">{{item.id}}</span>
          </div>
          <img v-show="item.rssi <= 0" class="item-img" :src="getRssiIcon(item.rssi)"/>
          <i class="icon-right text-color"></i>
        </div>
      </div>
    </div>
    <div v-show="isBleConnect" class="connect-loading">
      <span>{{loadingMsg}}</span>
      <div class="loading-base">
        <div class="loading-content"></div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapMutations, mapGetters, mapState} from 'vuex'
import Util from "../assets/tool/utils";
import {Toast, Indicator, MessageBox} from 'mint-ui'
import Constant from '../assets/tool/constant'
export default {
  data () {
    return {
      blueEnable: false,
      scanDeviceList: [],
      configType: '',
      isBleScan: true,
      locationGranted: true,
      locationEnabled: true,
      pushDeviceId: '',
      connectContent: 0,
      connectData: '',
      timeOutLoading: '',
      loadingMsg: this.$t('connecting'),
      isBleConnect: false
    }
  },
  created: function () {
    const self = this
    window['scanCallback'] = (res) => {
      self.scanCallback(res)
    }
    window['connectCallback'] = (res) => {
      self.connectCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
    window['provisioningCallback'] = (res) => {
      self.provisioningCallback(res)
    }
  },
  watch: {
    '$route':'getPath'
  },
  methods: {
    ...mapMutations({
      setNum: 'SET_NUM'
    }),
    scanDevice (isBleScan) {
      var self = this
      console.log('扫描或者停止扫描 scanDevice : ' + isBleScan)
      if (isBleScan) {
        console.log('blueEnable:' + this.blueEnable + '  locationEnabled:' + this.locationEnabled + '  locationGranted:' + this.locationGranted)
        if (!self.blueEnable) {
          self.messageRemind(self.$t('notbluetooth'))
          return
        }
        if (!self.locationEnabled) {
          self.messageRemind(self.$t('notlocation'))
          return
        }
        if (!self.locationGranted) {
          self.messageRemind(self.$t('notgps'))
          return
        }
        var scanData = {'type':'provisioning'}
        EspBleMesh.startScan(JSON.stringify(scanData))
        self.isBleScan = false
      } else {
        EspBleMesh.webLog('停止扫描')
        EspBleMesh.stopScan()
        self.isBleScan = true
      }
    },
    getPath(){
      var self = this
      console.log('scan 路由发生变化' + this.$route.path);
    },
    messageRemind (msg) {
      Toast({
        message: msg,
        position: 'bottom'
      })
    },
    getRssiIcon (rssi) {
      return Util.getBLERssiIcon(rssi)
    },
    showBlueFail () {
      console.log('点击蓝牙按钮')
    },
    hide () {
      var self = this
      setTimeout(() => {
        EspBleMesh.stopScan()
      })
      self.scanDeviceList = []
      console.log('返回 Home 页面')
      this.isBleScan = true
      this.$store.commit('setScanProvisioning', [])
      self.$router.goBack()
    },
    onBackPressed () {
      this.isBleScan = true
      EspBleMesh.stopScan()
      this.$store.commit('setScanProvisioning', [])
      this.$router.goBack()
    },
    provisionItem (item) {
      var self = this
      self.loadingMsg = self.$t('connecting')
      console.log('FBY provisionItem: ' + self.loadingMsg)
      self.isBleConnect = true
      this.isBleScan = true
      EspBleMesh.stopScan()
      self.pushDeviceId = item.id
      self.connectData = {'id': item.id, 'type': 'provisioning'}
      console.log('FBY connectData: ' + JSON.stringify(self.connectData))
      // Indicator.open(self.loadingMsg)
      EspBleMesh.connect(JSON.stringify(self.connectData))
      self.timeOutLoading = setTimeout(function () {
        // Indicator.close()
        self.isBleConnect = false
        self.messageRemind(self.$t('connecttimeout'))
        EspBleMesh.disconnect()
      }, 25000)
    },
    scanCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        res.sort(Util.sortBy("rssi", 0));
        self.$store.commit('setScanProvisioning', res)
        console.log('scanCallback: ' + JSON.stringify(res))
        self.scanDeviceList = res;
      }
    },
    connectCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY connectCallback' + res + '重试次数' + self.connectContent)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        switch(res.status) {
          case Constant.STATUS_GATT_CONNECTED:
            console.log('设备蓝牙连接成功 12345678900987654321')
            self.loadingMsg = self.$t('devicebleconnectsuc')
            break
          case Constant.STATUS_GATT_DISCONNECTED:
            if (self.connectContent > 5) {
              self.isBleConnect = false
              clearTimeout(self.timeOutLoading)
              self.messageRemind(self.$t('bleconnectfail'))
            } else {
              self.loadingMsg = self.$t('devicebleconnectfail')
              if (!Util._isEmpty(self.connectData)) {
                console.log('fby connect 2')
                EspBleMesh.connect(JSON.stringify(self.connectData))
              }
              self.connectContent ++
            }
            break
          case Constant.STATUS_DEVICE_NOT_FOUND:
            self.isBleConnect = false
            clearTimeout(self.timeOutLoading)
            self.messageRemind(self.$t('bleparameterfail'))
            break
          case Constant.STATUS_PROVISIONING_READY:
            // Indicator.close()
            self.isBleConnect = false
            self.connectContent = 0
            self.loadingMsg = self.$t('connecting')
            clearTimeout(self.timeOutLoading)
            self.$router.togo({
              name: 'capabilities',
              query: {
                'name': res.name,
                'oobMethods': res.oobMethods,
                'deviceId': self.pushDeviceId
              }
            })
            break
        }
      }
    },
    provisioningCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY provisioningCallback' + res + '重试次数' + self.connectContent)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        if (res.status === Constant.STATUS_FAILED) {
          if (self.connectContent > 5) {
            self.isBleConnect = false
            clearTimeout(self.timeOutLoading)
            self.messageRemind(self.$t('provisioningfail'))
          } else {
            self.loadingMsg = self.$t('devicerssipool')
            if (!Util._isEmpty(self.connectData)) {
              console.log('fby FAILED 2')
              EspBleMesh.connect(JSON.stringify(self.connectData))
            }
            self.connectContent ++
          }
        }
      }
    }
  },
  mounted () {
    console.log('Sigprovision 传参打印' + JSON.stringify(this.$route.query))
    this.configType = this.$route.query.type
    this.scanDeviceList = this.$store.state.scanProvisioning
    this.connectContent = 0
    this.loadingMsg = this.$t('connecting')
    this.scanDevice(this.isBleScan)
    // this.scanDeviceList = [
    //   {'name': 'light-1', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '30'},
    //   {'name': 'light-2', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-80'},
    //   {'name': 'light-3', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-70'},
    //   {'name': 'light-4', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-60'},
    //   {'name': 'light-5', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-50'},
    //   {'name': 'light-6', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-40'},
    //   {'name': 'light-7', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-30'},
    //   {'name': 'light-8', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-20'},
    //   {'name': 'light-9', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-10'},
    //   {'name': 'light-10', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-30'},
    //   {'name': 'light-11', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-30'},
    //   {'name': 'light-12', 'id': 'B4:E6:2D:EB:09:87', 'rssi': '-30'}]
  },
  computed: {
    ...mapGetters([
      'number'
    ]),
    ...mapState({
      number: state => state.home.number
    }),
    getBlueEnable () {
      console.log('状态实时更新')
      this.blueEnable = this.$store.state.blueInfo
      this.locationGranted = this.$store.state.locationGranted
      this.locationEnabled = this.$store.state.locationEnabled
      return this.blueEnable
    }
  }
}
</script>

<style scoped lang="less">
  @import '../assets/css/index.less';
  .device-list-base {
    margin-top: 50px;
    height: calc(100% - 50px);
    background: #f6f6f6;
    box-sizing: border-box;
  }
  .device-list-cell {
    padding: 10px 20px;
    display: flex;
    align-items: center;
    min-width: calc(100% - 130px);
    box-sizing: border-box;
    border-bottom: 1px solid #e6e6e6;
    -webkit-transition: all .3s linear;
    transition: all .3s linear;
  }
  .item-content {
    display: flex;
    flex: 1;
    flex-flow: column;
  }
  .text-color {
    /*margin-top: 5px;*/
    /*width: 40px;*/
    color: @Secondary-font-color;
    /*font-size: 12px;*/
  }
  .device-list-cell .item-img {
    width: 32px;
    height: 28px;
    margin-right: 40px;
  }
  .connect-loading {
    width: 100%;
    height: 100%;
    z-index: 100000;
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    background-color: rgba(225, 225, 225, .3);
  }
  .loading-base {
    width: 50%;
    height: 10px;
    margin: 10px 25%;
    background-color: #a0a0a0;
    position: relative;
    overflow: hidden;
  }
  @keyframes scan-loading {
    0% {
      left: -90px;
    }
    50% {
      left: 50px;
    }
    100% {
      left: 190px;
    }
  }
  .loading-content {
    width: 40%;
    height: 10px;
    background-color: #fa2d28;
    position: absolute;
    animation: scan-loading 1s linear infinite;
  }
</style>
