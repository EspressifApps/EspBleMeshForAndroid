<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide"><i class="icon-left back"></i></span>{{$t('capabilities')}}</h4>
      <div v-show="!getBlueEnable"  @click="showBlueFail" class="bluetooth-second-base">
        <i class="icon-bluetooth bluetooth-second-style"></i>
      </div>
      <span @click="deviceProvision()" class="right-top-text">{{$t('provision')}}</span>
      <ul v-show="flag" class="add-ul add-provision-ul">
        <!--        <i class="icon-light"></i>-->
        <li v-for="(item, i) in oobMethods" @click.stop="provision(i)"><span>{{getOobMethods(item)}}</span></li>
<!--        <li @click.stop="provision('2')"><span>{{$t('fastprovision')}}</span></li>-->
      </ul>
    </div>
    <div class="device-detail">
      <!--        设备名称-->
      <div @click="deviceNameClick" class="item">
        <span class="name">{{$t('devicename')}}</span>
        <span class="content">{{deviceName}}</span>
        <i class="icon-right text-color"></i>
      </div>
      <!--        Network Key-->
      <div @click="networkKeyClick" class="item">
        <span class="name">{{$t('networkkey')}}</span>
        <span class="content">{{networkKey}}</span>
<!--        <i class="icon-right text-color"></i>-->
      </div>
    </div>
  </div>
</template>

<script>
  import {mapMutations, mapGetters, mapState} from 'vuex'
  import {MessageBox, Toast, Indicator} from 'mint-ui'
  import Util from '../assets/tool/utils'
  import Constant from '../assets/tool/constant'
  export default {
    data () {
      return {
        blueEnable: false,
        isBleScan: true,
        locationGranted: true,
        locationEnabled: true,
        deviceName: '',
        networkKey: 'Primary Network Key',
        flag: false,
        oobMethods: [],
        deviceId: '',
        proxyAddress: '',
        connectContent: 0,
        connectData: '',
        capabilitiesTimeOut: ''
      }
    },
    created: function () {
      const self = this
      window['scanCallback'] = (res) => {
        self.scanCallback(res)
      }
      window['provisioningCallback'] = (res) => {
        self.provisioningCallback(res)
      }
      window['connectCallback'] = (res) => {
        self.connectCallback(res)
      }
      window['onBackPressed'] = () => {
        self.onBackPressed()
      }
    },
    methods: {
      ...mapMutations({
        setNum: 'SET_NUM'
      }),
      getOobMethods (item) {
        if (item === Constant.NO_OOB_AUTHENTICATION) {
          return 'No OOB'
        } else if (item === Constant.STATIC_OOB_AUTHENTICATION) {
          return 'Static OOB'
        } else if (item === Constant.OUTPUT_OOB_AUTHENTICATION) {
          return 'Output OOB'
        } else if (item === Constant.INPUT_OOB_AUTHENTICATION) {
          return 'Input OOB'
        }
      },
      provision (item) {
        var self = this
        self.flag = false
        var provisionData = {'name': self.deviceName, 'oobMethod': self.oobMethods[item]}
        console.log('选择配网类型： ' + item + '配网参数： ' + JSON.stringify(provisionData))
        Indicator.open()
        self.capabilitiesTimeOut = setTimeout(function () {
          Indicator.close()
          self.messageRemind(self.$t('provisionTimeout'))
        }, 6000)
        EspBleMesh.startProvisioning(JSON.stringify(provisionData))
      },
      deviceProvision () {
        var self = this
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
        if (Util._isEmpty(self.oobMethods)){
          return
        }
        if (self.oobMethods.length === 1) {
          console.log('选择配网类型' + self.oobMethods[0])
          self.provision(0)
        }else {
          self.flag = true
        }
      },
      showBlueFail () {
        console.log('点击蓝牙按钮')
      },
      hide () {
        EspBleMesh.disconnect()
        this.flag = false
        this.$router.goBack()
      },
      deviceNameClick () {
        var self = this
        console.log('点击编辑设备名称')
        MessageBox.prompt(self.$t('devicename'), {inputValue: self.deviceName, inputPlaceholder: self.$t('devicename'),
            inputValidator:function(v){if (Util.stringToBytes(v).length > 32){return false} {}},
            inputErrorMessage: self.$t('longDesc'),
            confirmButtonText: self.$t('confirmBtn'), cancelButtonText: self.$t('cancelBtn')}).then(function(obj) {
              self.deviceName = obj.value;
        }).catch(function(err) {
          // self.onBackIndex();
        });
      },
      networkKeyClick () {
        console.log('点击选择密钥')
      },
      onBackPressed () {
        EspBleMesh.disconnect()
        this.flag = false
        this.$router.goBack()
      },
      homeDeviceScan () {
        var self = this
        console.log('homeDeviceScan 123')
        if (!self.blueEnable) {
          self.messageRemind(self.$t('notbluetooth'))
        }
        if (!self.locationEnabled) {
          self.messageRemind(self.$t('notlocation'))
        }
        if (!self.locationGranted) {
          self.messageRemind(self.$t('notgps'))
        }
        if (self.blueEnable && self.locationEnabled && self.locationGranted) {
          console.log('home 开始 proxy 蓝牙扫描')
          var scanData = {'type':'proxy'}
          EspBleMesh.startScan(JSON.stringify(scanData))
        }
      },
      messageRemind (msg) {
        Toast({
          message: msg,
          position: 'bottom'
        })
      },
      scanCallback (res) {
        var self = this
        res = Util.base64().decode(res)
        console.log('Home scan proxy device: ' + res)
        res = JSON.parse(res)
        if (!Util._isEmpty(res)) {
          EspBleMesh.stopScan()
          for (let i = 0; i < res.length; ++i) {
            if (res[i].address === self.proxyAddress) {
              self.connectData = {'id': res[i].id, 'type': 'proxy', 'address': self.proxyAddress}
              console.log('FBY home connectData: ' + JSON.stringify(self.connectData))
              self.$store.commit('setBleConnectAddress', self.proxyAddress)
              EspBleMesh.connect(JSON.stringify(self.connectData))
              return
            }
          }
          res.sort(Util.sortBy("rssi"));
          self.connectData = {'id': res[0].id, 'type': 'proxy', 'address': self.proxyAddress}
          console.log('FBY home connectData: ' + JSON.stringify(self.connectData))
          self.$store.commit('setBleConnectAddress', self.proxyAddress)
          EspBleMesh.connect(JSON.stringify(self.connectData))
        }
      },
      connectCallback (res) {
        var self = this
        res = Util.base64().decode(res)
        console.log('FBY connectCallback' + res)
        res = JSON.parse(res)
        if (!Util._isEmpty(res)) {
          switch(res.status) {
            case Constant.STATUS_GATT_CONNECTED:
              console.log('设备蓝牙连接成功 12345678900987654321')
              self.$store.commit('setIsBleConnect', true)
              self.connectContent = 0
              break
            case Constant.STATUS_GATT_DISCONNECTED:
              if (self.connectContent < 2) {
                if (!Util._isEmpty(self.connectData)) {
                  EspBleMesh.connect(JSON.stringify(self.connectData))
                }
              }
              self.connectContent ++
              break
            case Constant.STATUS_PROXY_READY:
              console.log('设备详情获取成功：' + res)
              self.connectContent = 0
              break
          }
        }
      },
      provisioningCallback (res) {
        var self = this
        res = Util.base64().decode(res)
        console.log('FBY provisioningCallback' + res)
        res = JSON.parse(res)
        if (!Util._isEmpty(res)) {
          if (res.status === Constant.STATUS_PROVISIONING_COMPLETE) {
            clearTimeout(self.capabilitiesTimeOut)
            self.$store.commit('setSigProvisionList', res.node)
            self.proxyAddress = res.node.address
            self.homeDeviceScan()
            setTimeout(function () {
              Indicator.close()
              MessageBox({
                $type: 'alert',
                title: self.$t('success'),
                message: self.$t('provisioningcomplete'),
                showConfirmButton: false,
                closeOnClickModal: false,
                showCancelButton: true,
                cancelButtonText: self.$t('confirmBtn')
              }).then(action => {
                console.log('点击确定按钮')
                self.removeDevices()
                self.backHome(res.node)
              })
            }, 2000)
          } else if (res.status === Constant.STATUS_FAILED) {
            console.log('capabilities STATUS_FAILED')
          }
        }
      },
      removeDevices () {
        var self = this
        var scanDeviceList = self.$store.state.scanProvisioning
        scanDeviceList.forEach(function (item, i) {
          if (item.id === self.deviceId) {
            scanDeviceList.splice(i, 1);
            return false;
          }
        })
        self.$store.commit('setScanProvisioning', scanDeviceList)
      },
      backHome (node) {
        var self = this
        self.$router.goRight({
          name: 'home',
          query: {
            node: node,
            type: Constant.CAPABILITIES
          }
        })
      }
    },
    mounted () {
      this.connectContent = 0
      console.log('sigprovision 传参打印' + JSON.stringify(this.$route.query))
      this.deviceName = this.$route.query.name
      this.oobMethods = this.$route.query.oobMethods
      this.deviceId = this.$route.query.deviceId
    },
    computed: {
      ...mapGetters([
        'number'
      ]),
      ...mapState({
        number: state => state.home.number
      }),
      getBlueEnable () {
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
  .device-detail {
     margin: 70px 30px;
     border: 1px solid @tab-general-bg-color;
   }
  .device-detail .item {
    display: flex;
    height: 45px;
    justify-content: space-between;
    align-items: center;
    padding: 0 10px;
  }
  .device-detail .name {
    flex: 1;
  }
  .device-detail .content {
    color: @Secondary-font-color;
    margin-right: 10px;
  }
  .text-color {
    color: @Secondary-font-color;
    font-size: 12px;
  }
  .add-provision-ul {
    top: 50px !important;
  }
</style>
