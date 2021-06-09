<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide"><i class="icon-left back"></i></span>{{$t('fastprovision')}}</h4>
      <div v-show="!getBlueEnable"  @click="showBlueFail" class="bluetooth-second-base">
        <i class="icon-bluetooth bluetooth-second-style"></i>
      </div>
      <span @click="deviceProvision()" class="right-top-text">{{$t('provision')}}</span>
    </div>
    <div class="input-base">
      <input class="input-content" v-model="provisionNumber" :placeholder="$t('configdevicenumber')" type="number">
    </div>
  </div>
</template>

<script>
import Constant from "../assets/tool/constant";
import Util from "../assets/tool/utils";
import {Indicator} from 'mint-ui'
export default {
  name: 'text',
  data () {
    return {
      selected: '',
      selectList: [],
      blueEnable: true,
      locationGranted: true,
      locationEnabled: true,
      provisionNumber: '',
      getFastProvNodeAddrTimeOut: '',
      firstAddress: '',
      isBleConnect: true,
      getFirstContent: 0
    }
  },
  created () {
    var self = this
    self.selectList.push(self.$t('allnodes'))
    window['meshMessageCallback'] = (res) => {
      self.meshMessageCallback(res)
    }
    window['scanCallback'] = (res) => {
      self.scanCallback(res)
    }
    window['connectCallback'] = (res) => {
      self.connectCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
  },
  methods: {
    showBlueFail () {
      console.log('点击蓝牙按钮')
    },
    scanCallback (res) {
      var self = this
      Util.scanCallback(res, self)
    },
    connectCallback (res) {
      var self = this
      Util.connectCallback(res, self, true)
    },
    hide () {
      this.$router.goBack()
    },
    onBackPressed () {
      this.$router.goBack()
    },
    deviceProvision () {
      var self = this
      var sendMeshData = {}
      console.log('配网数量： ' + self.provisionNumber)
      if (!Util._isEmpty(self.provisionNumber)) {
        self.isBleConnect = self.$store.state.isBleConnect
        if (self.isBleConnect) {
          sendMeshData[Constant.KEY_OP_CODE] = Constant.FAST_PROV_INFO_SET
          sendMeshData[Constant.KEY_DST_ADDRESS] = self.$store.state.bleConnectAddress
          sendMeshData[Constant.KEY_EXPECT_PROVISIONING_COUNT] = self.provisionNumber
          self.firstAddress = self.$store.state.bleConnectAddress
          console.log('FBY First provision sendMeshData:' + JSON.stringify(sendMeshData))
          Indicator.open()
          setTimeout(function () {
            Indicator.close()
            Util.messageRemind(self.$t('provisionTimeout'))
          }, 30000)
          Util.sendMeshMessage(self, sendMeshData)
        } else {
          Util.messageRemind(self.$t('bledisconnect'))
          // self.hide()
        }
      } else {
        Util.messageRemind(this.$t('configdevicenumber'))
      }
    },
    meshMessageCallback (res) {
      var self = this
      Util.meshMessageCallback(res, self, 'firstProvision')
    },
    firstProvStatusCallback () {
      // Indicator.close()
      var self = this
      self.getFastProvNodeAddrTimeOut = setTimeout(function () {
        console.log('FBY getFastProvNodeAddrTimeOut aaaaa: ' + self.getFirstContent)
        self.getFastProvNodeAddr()
      }, 5000)
      self.getFirstContent ++
    },
    getFastProvNodeAddr () {
      var self = this
      var sendMeshData = {}
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        sendMeshData[Constant.KEY_OP_CODE] = Constant.FAST_PROV_NODE_ADDR_GET
        sendMeshData[Constant.KEY_DST_ADDRESS] = self.firstAddress
        console.log('FBY First provision sendMeshData:' + JSON.stringify(sendMeshData))
        Util.sendMeshMessage(self, sendMeshData)

        if (self.getFirstContent < 3) {
          self.getFastProvNodeAddrTimeOut = setTimeout(function () {
            console.log('FBY getFastProvNodeAddrTimeOut: ' + self.getFirstContent)
            self.getFastProvNodeAddr()
          }, 5000)
          self.getFirstContent ++
        }
      } else {
        Util.messageRemind(self.$t('bledisconnect'))
        Util.homeDeviceScan(this)
      }
    },
    scanTimeout () {
      var self = this
      var isRemind = Util.scanTimeout(this)
      if (isRemind) {
        Util.messageRemind(self.$t('unconnectable'))
      }
    },
    sendBLEMeshData () {
      this.getFastProvNodeAddr()
    },
    getFastProvNodeAddrCallback (fastNodes) {
      var self = this
      self.getFirstContent = 5
      Indicator.close()
      Util.fastNodesStatus(self, fastNodes)
      clearTimeout(self.getFastProvNodeAddrTimeOut)
      Util.messageRemind(self.$t('fastconfigsuc'))
    }
  },
  mounted () {
    var self = this
    // self.firstAddress = self.$store.state.bleConnectAddress
    // this.getFastProvNodeAddr()
  },
  computed: {
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
</style>

