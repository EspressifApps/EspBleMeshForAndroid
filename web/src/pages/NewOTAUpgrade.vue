<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide()"><i class="icon-left back"></i></span>{{deviceInfo.name}}</h4>
      <span @click="nodeUpgrade()" class="right-top-text">{{$t('upgrade')}}</span>
    </div>
    <div class="upgrade-content-base">
      <div class="input-base upgrade-input-base">
        <span>Link: </span>
        <input class="input-content" v-model="binLinkStr" :placeholder="$t('otalink')" type="url">
      </div>
      <div class="input-base upgrade-input-base">
        <span>SSID: </span>
        <input class="input-content" v-model="ssidStr" :placeholder="$t('otassid')">
      </div>
      <div class="input-base upgrade-input-base">
        <span>Password: </span>
        <div class="input-content-base">
          <input class="input-content" v-model="password" :placeholder="$t('otapassword')" :type="typePwd">
          <img :src="seen ? openeye : nopeneye" class="input-pwd-img" @click="showPwd">
        </div>
      </div>
      <div class="input-base upgrade-input-base">
        <span>Version: </span>
        <input class="input-content" v-model="otaVersion" type="tel" :placeholder="$t('otaversion')">
      </div>
    </div>
  </div>
</template>

<script>
import Util from '../assets/tool/utils'
import Constant from '../assets/tool/constant'
import {Indicator, MessageBox} from 'mint-ui'
export default {
  name: 'text',
  data () {
    return {
      seen: false,
      openeye: require('../assets/pwdShow.png'),
      nopeneye: require('../assets/noPwdShow.png'),
      typePwd: 'password',
      deviceInfo: this.$store.state.deviceInfo,
      binLinkStr: 'https://resource.infiniteyuan.com/hello-world.bin',
      ssidStr: 'InfiniteYuan',
      password: 'infiniteyuan',
      otaVersion: '1.0.0',
      upgradeTimeOut: ''
    }
  },
  created () {
    var self = this
    window['getWiFiInfoCallback'] = (res) => {
      self.getWiFiInfoCallback(res)
    }
    window['otaCallback'] = (res) => {
      self.otaCallback(res)
    }
    window['getOtaBinVersionCodeCallback'] = (res) => {
      self.getOtaBinVersionCodeCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
  },
  methods: {
    showPwd () {
      console.log('显示密码')
      this.typePwd = this.typePwd === 'password' ? 'text' : 'password'
      this.seen = !this.seen
    },
    onBackPressed () {
      this.$router.goBack()
    },
    hide () {
      console.log('返回上一页')
      this.$router.goBack()
    },
    nodeUpgrade () {
      var self = this
      console.log('点击升级按钮 otaVersion： ' + self.otaVersion)
      // var versionArr = []
      if (!Util._isEmpty(self.otaVersion)) {
        // versionArr = self.otaVersion.split('.')
        // if (versionArr.length === 3) {
        //   let oneVersion = versionArr[0]
        //   let twoVersion = versionArr[1]
        //   let threeVersion = versionArr[2]
        //   oneVersion.toString(16)
        //   twoVersion.toString(16)
        //   threeVersion.toString(16)
        //   let allHexVersion = oneVersion + twoVersion + threeVersion
        //   console.log('allHexVersion: ' + allHexVersion);
        // } else {
        //   Util.messageRemind(self.$t('versionlength'))
        // }
        EspBleMesh.getOtaBinVersionCode(self.otaVersion)
      } else {
        Util.messageRemind(self.$t('otaparameter'))
        return
      }
    },
    getWiFiInfoCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY OTA getWiFiInfoCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        self.ssidStr = res.ssid
        console.log('ssidStr: ' + self.ssidStr + ' ssid: ' + res.ssid)
      }
    },
    otaCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY OTA otaCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        if (res.status === Constant.OTA_STATUS_MESSAGE_NOT_NEED) {
          console.log('fby 设备不需要升级')
          Indicator.close()
          clearTimeout(self.upgradeTimeOut)
          Util.messageRemind(self.$t('notneedupdate'))
        } else if (res.status === Constant.OTA_STATUS_MESSAGE_TIMEOUT) {
          console.log('fby 升级超时')
          Indicator.close()
          clearTimeout(self.upgradeTimeOut)
          Util.messageRemind(self.$t('upgradeFailDesc'))
        } else if (res.status === Constant.OTA_STATUS_MESSAGE_READY) {
          console.log('fby 链接Wi-Fi')
          Indicator.close()
          clearTimeout(self.upgradeTimeOut)
          Util.messageRemind(self.$t('deviceBgUpgrade'))
          setTimeout(() => {
            self.hide()
          }, 1000)
        } else if (res.status === Constant.OTA_STATUS_COMPLETE) {
          console.log('fby 升级完成')
          Indicator.close()
          clearTimeout(self.upgradeTimeOut)
          Util.messageRemind(self.$t('upgradeSucDesc'))
          self.hide()
        } else if (res.status === Constant.OTA_STATUS_CONNECTED) {
          console.log('fby 建立升级链接')
        } else if (res.status === Constant.OTA_STATUS_PROGRESS) {
          console.log('fby 升级中' + res.progress)
        }
      }
    },
    getOtaBinVersionCodeCallback (res) {
      var self = this
      var sendOTAMeshData = {}
      res = Util.base64().decode(res)
      console.log('FBY OTA otaCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        if (!Util._isEmpty(self.binLinkStr) && !Util._isEmpty(self.ssidStr) && !Util._isEmpty(self.password)) {
          sendOTAMeshData[Constant.KEY_DST_ADDRESS] = self.deviceInfo.address
          sendOTAMeshData[Constant.KEY_COMPANY_ID] = Constant.VALUE_COMPANY_ID
          sendOTAMeshData[Constant.KEY_VERSION_CODE] = res.versionCode
          sendOTAMeshData[Constant.KEY_BIN_ID] = 1
          sendOTAMeshData[Constant.KEY_CLEAR_FLASK] = false
          sendOTAMeshData[Constant.KEY_URL] = self.binLinkStr
          sendOTAMeshData[Constant.KEY_URL_SSID] = self.ssidStr
          sendOTAMeshData[Constant.KEY_URL_PASSWORD] = self.password
          console.log('FBY OTA 开始升级 sendOTAMeshData' + JSON.stringify(sendOTAMeshData))
          Indicator.open()
          self.upgradeTimeOut = setTimeout(function () {
            Indicator.close()
            self.messageRemind(self.$t('upgradeFailTimeOut'))
          }, 60000)
          EspBleMesh.sendOtaMeshMessage(JSON.stringify(sendOTAMeshData))
        } else {
          Util.messageRemind(self.$t('otaparameter'))
        }
      } else {
        Util.messageRemind(self.$t('otaparameter'))
        return
      }
    },
    backOperation () {
      var self = this
      self.$router.goRight({
        name: 'operation'
      })
    }
  },
  mounted () {
    // var self = this
    // EspBleMesh.getWiFiInfo()
  }
}
</script>

<style scoped lang="less">
  @import '../assets/css/index.less';
  .upgrade-content-base {
    /*position: relative;*/
    /*top: 50px;*/
    /*height: calc(100% - 50px) !important;*/
    /*z-index: -1;*/
    /*background-color: #26a2ff;*/
  }
  .upgrade-input-base {
    margin: 20px 30px !important;
  }
  input-content-base {
    position: relative;
  }
  .input-pwd-img {
    position: absolute;
    bottom: 10px;
    right: 10px;
    width: 30px;
    height: 30px;
  }
</style>
