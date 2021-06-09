<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide"><i class="icon-left back"></i></span>{{deviceInfo.name}}</h4>
      <span @click="nodeUpgrade()" class="right-top-text">{{$t('upgrade')}}</span>
    </div>
    <div class="content-info flex-wrapper upgrade-base">
      <div class="overflow-touch">
        <div v-for="item in upgradeBinList" :key="item.name" @click="switchUpgradeBin(item)" class="item">
          <div class="item-name">
            <span>{{item.name}}</span>
            <span class="desc">{{item.versionName}}</span>
          </div>
          <div class="item-power-small">
            <span :data-value="item.name" class="span-radio" :class="{'active': isUpgradeSelected(item.name)}"><span></span></span>
          </div>
        </div>
      </div>
    </div>
    <div v-show="upgrade" class="ota-upgrade">
      <div @click="hideUpgrade" class="mask"></div>
      <div class="upgrade-info">
        <h3>{{$t('otaUpdate')}}</h3>
        <div class="progress-info">
          <span :style="{'left': upgradeValue + '%'}" class="progress-value upgrade-progress-value" id="update-progress-vule" :class="{'active': upgradeFailure}">0%</span>
          <div class="ota-progress">
            <div class="ota-progress-progress upgradeProgress" id="update-progress-view" :class="{'active': upgradeFailure}"></div>
          </div>
          <p class="progress-details">
            <span>{{$t('deviceUpgradingDesc')}}</span>
            <span>{{getBinName()}}</span>
            <span class="text-right">{{timeCost}}</span>
          </p>
          <div v-show="!upgradeSuccess && !upgradeFailure" class="result-success">
            <button @click="stopUpgrade" class="btn register-btn">{{$t('cancelBtn')}}</button>
          </div>
          <div v-show="upgradeSuccess" class="result-success">
            <p>{{$t('upgradeSucDesc')}}</p>
            <div class="result-flex">
              <button @click="stopUpgrade" class="btn register-btn retry">{{$t('cancelBtn')}}</button>
              <button @click="otaReboot" class="btn register-btn">{{$t('confirmBtn')}}</button>
            </div>
          </div>
          <div v-show="upgradeFailure" class="result-failure">
            <p>{{$t('upgradeFailDesc')}}</p>
            <div class="result-flex">
              <button @click="retrySave" class="btn register-btn retry">{{$t('retryBtn')}}</button>
              <button @click="otaReboot" class="btn register-btn">{{$t('confirmBtn')}}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Util from '../assets/tool/utils'
import Constant from "../assets/tool/constant";
import {MessageBox} from "mint-ui";
export default {
  data () {
    return {
      deviceInfo: this.$store.state.deviceInfo,
      upgradeBinList: [],
      isSelectedVersion: '',
      isSelectedBin: {},
      upgrade: false,
      upgradeValue: 0,
      upgradeSuccess: false,
      upgradeFailure: false,
      timeCost: '00:00:00',
      timeCostId: '',
      scheduleId: ''
    }
  },
  created () {
    var self = this
    window['getOtaBinsCallback'] = (res) => {
      self.getOtaBinsCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
    window['otaCallback'] = (res) => {
      self.otaCallback(res)
    }
    window['onActivityPause'] = () => {
      self.onActivityPause()
    }
    window['onActivityResume'] = () => {
      self.onActivityResume()
    }
    window['getWiFiInfoCallback'] = (res) => {
      self.getWiFiInfoCallback(res)
    }
  },
  methods: {
    hide () {
      this.$router.goBack()
    },
    // 程序进入后台
    onActivityPause () {
      console.log('程序进入后台')
    },
    // 程序返回前台
    onActivityResume () {
      console.log('程序返回前台')
      EspBleMesh.getWiFiInfo()
    },
    getWiFiInfoCallback (res) {
      var self = this
      var sendOTAStartData = {}
      res = Util.base64().decode(res)
      console.log('FBY OTA getWiFiInfoCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        if (self.$store.state.otawifiName === res.ssid) {
          sendOTAStartData[Constant.KEY_NAME] = self.isSelectedBin.name
          sendOTAStartData[Constant.KEY_COMPANY_ID] = Constant.VALUE_COMPANY_ID
          sendOTAStartData[Constant.KEY_BIN_ID] = self.isSelectedBin.binId
          sendOTAStartData[Constant.KEY_VERSION_CODE] = self.isSelectedBin.versionCode
          console.log('FBY OTA 开始升级 sendOTAStartData' + JSON.stringify(sendOTAStartData))
          EspBleMesh.startOta(JSON.stringify(sendOTAStartData))
        } else {
          Util.messageRemind(self.$t('wificonnecterror'))
        }
      }
    },
    onBackPressed () {
      this.$router.goBack()
    },
    nodeUpgrade () {
      var self = this
      var sendOTAMeshData = {}
      if (!Util._isEmpty(self.isSelectedBin)) {
        sendOTAMeshData[Constant.KEY_DST_ADDRESS] = self.deviceInfo.address
        sendOTAMeshData[Constant.KEY_COMPANY_ID] = Constant.VALUE_COMPANY_ID
        sendOTAMeshData[Constant.KEY_BIN_ID] = self.isSelectedBin.binId
        sendOTAMeshData[Constant.KEY_VERSION_CODE] = self.isSelectedBin.versionCode
        sendOTAMeshData[Constant.KEY_CLEAR_FLASK] = false
        console.log('FBY OTA 开始升级 sendOTAMeshData' + JSON.stringify(sendOTAMeshData))
        EspBleMesh.sendOtaMeshMessage(JSON.stringify(sendOTAMeshData))
      } else {
        Util.messageRemind(self.$t('updatefile'))
      }
      // self.upgrade = true
      // self.getTime()
      // self.setSchedule()
    },
    setSchedule () {
      var self = this
      self.scheduleId = setInterval(function() {
        console.log(self.upgradeValue)
        if (self.upgradeValue >= 5 || self.upgradeFailure) {
          clearInterval(self.scheduleId)
          self.stopTime()
          self.upgradeFailure = true
        } else {
          self.upgradeValue += 2
          document.getElementById('update-progress-vule').innerText = self.upgradeValue + '%'
          document.getElementById('update-progress-view').style.width = self.upgradeValue + '%'
          // self.setProgress(self.upgradeValue)
        }
      }, 2000)
    },
    // 空白区域取消
    hideUpgrade () {
      if (this.upgradeFailure) {
        this.upgrade = false
        this.upgradeFailure = false
        clearInterval(this.scheduleId)
        // this.showPart = false
        // $("span.upgrade-progress-value").text("0%")
        // $("div.upgradeProgress").css("width", "0%")
      }
    },
    // 取消
    stopUpgrade () {
      this.upgrade = false
      this.upgradeFailure = false
      clearInterval(this.scheduleId)
      this.stopTime()
      // this.hideSuccess()
      // this.stopOTA()
    },
    getBinName () {
      if (!Util._isEmpty(this.isSelectedBin)) {
        return this.isSelectedBin.name
      }
      return ''
    },
    // 重试
    retrySave () {
      this.upgrade = false
      this.upgradeFailure = false
      clearInterval(this.scheduleId)
    },
    // 确定
    otaReboot () {
      this.upgrade = false
      this.upgradeFailure = false
      clearInterval(this.scheduleId)
    },
    getTime () {
      var self = this
      self.timeCost = '00:00:00'
      var time = 0
      self.timeCostId = setInterval(function() {
        time ++
        if (time < 60 ) {
          self.timeCost = '00:00:' + self.getSecond(time)
        } else if (time < 3600 ) {
          var m = (time / 60).toFixed(0)
          var s = time % 60
          self.timeCost = '00:' + self.getMinute(m) + ':' + self.getSecond(s)
        } else {
          var h = (time / 3600).toFixed(0)
          var m = (time % 3600 / 60).toFixed(0)
          var s = (time % 60)
          self.timeCost = self.getHour(h) + ':' + self.getMinute(m) + ':' + self.getSecond(s)
        }
      }, 1000)
    },
    getSecond (time) {
      var second = 0
      if (time < 10) {
        second= '0' + time
      } else if (time < 60 ) {
        second = time
      }
      return second
    },
    getMinute (time) {
      var minute = 0
      if (time < 10) {
        minute= '0' + time
      } else if (time < 60 ) {
        minute = time
      }
      return minute
    },
    getHour (time) {
      var hour = 0
      if (time < 10) {
        hour= '0' + time
      } else {
        hour = time
      }
      return hour
    },
    stopTime () {
      var self = this
      clearInterval(self.timeCostId)
    },
    // 升级文件
    switchUpgradeBin (item) {
      var self = this
      console.log('选择升级文件版本： ' + item.name)
      if (self.isSelectedVersion !== item.name) {
        self.isSelectedVersion = item.name
        self.isSelectedBin = item
      }
    },
    isUpgradeSelected (name) {
      // console.log('选择升级版本： ' + version)
      var self = this
      var flag = false
      if (self.isSelectedVersion === name) {
        flag = true
      }
      return flag
    },
    getOtaBinsCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY OTA getOtaBinsCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        self.upgradeBinList = res
      }
    },
    otaCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY OTA otaCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        if (res.status === Constant.OTA_STATUS_MESSAGE_NOT_NEED) {
          Util.messageRemind(self.$t('notneedupdate'))
        } else if (res.status === Constant.OTA_STATUS_MESSAGE_TIMEOUT) {
        } else if (res.status === Constant.OTA_STATUS_MESSAGE_READY) {
          console.log('链接Wi-Fi')
          self.$store.commit('setotawifiName', res.ssid)
          MessageBox({
            $type: 'alert',
            title: self.$t('switchnetwork'),
            message: 'SSID: ' + res.ssid + '\n' + ' PASSWORD: ' + res.password,
            showConfirmButton: false,
            closeOnClickModal: false,
            showCancelButton: true,
            cancelButtonText: self.$t('settings')
          }).then(action => {
            console.log('点击设置按钮')
            EspBleMesh.gotoSystemSettings('wifi')
            // self.removeDevices()
            // self.backHome(res.node)
          })
        } else if (res.status === Constant.OTA_STATUS_COMPLETE) {
          console.log('升级完成')
        } else if (res.status === Constant.OTA_STATUS_CONNECTED) {
          console.log('建立升级链接')
        } else if (res.status === Constant.OTA_STATUS_PROGRESS) {
          console.log('升级中' + res.progress)
        }
      }
    }
  },
  mounted () {
    var self = this
    console.log('mounted 走了 deviceInfo' + JSON.stringify(self.deviceInfo))
    EspBleMesh.getOtaBins()
    // self.upgradeBinList = [{'name': 'Node_Bin_v1.0.10', 'appVersionName': '1.0.10'}, {'name': 'Node_Bin_v1.0.9', 'appVersionName': '1.0.9'},
    //   {'name': 'Node_Bin_v1.0.8', 'appVersionName': '1.0.8'}, {'name': 'Node_Bin_v1.0.7', 'appVersionName': '1.0.7'}, {'name': 'Node_Bin_v1.0.6', 'appVersionName': '1.0.6'},
    //   {'name': 'Node_Bin_v1.0.5', 'appVersionName': '1.0.5'}, {'name': 'Node_Bin_v1.0.4', 'appVersionName': '1.0.4'}, {'name': 'Node_Bin_v1.0.3', 'appVersionName': '1.0.3'},
    //   {'name': 'Node_Bin_v1.0.2', 'appVersionName': '1.0.2'}, {'name': 'Node_Bin_v1.0.1', 'appVersionName': '1.0.1'}, {'name': 'Node_Bin_v1.0.0', 'appVersionName': '1.0.0'}]
  },
  activated () {
    console.log('activated 走了')
  }
}
</script>

<style scoped lang="less">
  @import '../assets/css/index.less';
  .upgrade-base {
    position: relative;
    top: 50px;
    height: calc(100% - 50px) !important;
  }
  .ota-upgrade {
    position: absolute;
    top: 0;
    bottom: 0;
    width: 100%;
    z-index: 200;
  }
  .ota-upgrade .mask {
    height: 100%;
    width: 100%;
    background: rgba(0, 0, 0, .6);
  }
  .upgrade-info {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 88%;
    max-width: 550px;
    background: #fff;
    border-radius: 5px;
    padding: 10px 20px;
    -webkit-transform: translate3d(-50%, -50%, 0);
    transform: translate3d(-50%, -50%, 0);
    box-sizing: border-box;
  }
  .upgrade-info h3 {
    position: relative;
    text-align: center;
  }
  .upgrade-details {
    position: absolute;
    right: 0;
    color: #fa2d28;
  }
  .upgrade-details-info {
    max-height: 200px;
    overflow: hidden;
    overflow-y: auto;
    margin-bottom: 10px;
  }
  .details-item {
    display: flex;
    justify-content: space-between;
    border-top: 1px solid #e6e6e6;
    padding: 6px;
  }
  .progress-info {
    margin: 20px 0;
  }
  .progress-info span {
    position: relative;
    bottom: -3px;
    color: #fa2d28;
  }
  .progress-info span.active {
    color: #999;
  }
  .progress-info p {
    color: #858585;
  }
  .upgrade-progress-value {
    display: inline-block;
    -webkit-transform: translate(-35%, 0);
    transform: translate(-35%, 0);
    transition: all .3s linear;
  }
  .ota-progress {
    margin-top: 5px;
    width: 100%;
    height: 10px;
    border: none;
    background: #ebebeb;
    border-radius: 5px;
  }
  .ota-progress-progress {
    background-color: #fa2d28;
    border-radius: 5px;
    height: 100%;
    width: 0;
    -webkit-transition: all .3s linear;
    transition: all .3s linear;
  }
  .ota-progress-progress.active {
    background-color: #999;
  }
  .progress-details {
    display: flex;
    align-items: center;
    margin: 10px 0;
  }
  .progress-details span {
    flex: 1;
    color: #858585;
    font-size: 12px;
  }
  .result-success p,
  .result-failure p {
    margin: 10px 0;
    color: #fa2d28;
    font-size: 12px;
  }
  .btn {
    display: inline-block;
    padding: 12px;
    font-size: 14px;
    width: 100%;
    min-width: 100px;
    margin: 0 auto;
    color: #fff;
    border-radius: 5px;
    -webkit-transition: all .3s linear;
    transition: all .3s linear;
  }
  .register-btn {
    background: #fa2d28;
    border: 1px solid #fa2d28;
  }
  .result-flex {
    display: flex;
    align-items: center;
  }
  .result-flex button:first-child {
    margin-right: 5px;
  }
  .retry {
    color: #656b79;
    background-color: #f6f8fa;
    border-color: #f6f8fa;
  }
  .text-right {
    text-align: right;
  }
</style>
