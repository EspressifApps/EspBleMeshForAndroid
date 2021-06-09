<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide"><i class="icon-left back"></i></span>{{title}}</h4>
    </div>
<!--    <div class="operation-set-btn">-->
<!--      <span class="operation-btn" :class="{'operation': !onoff}" @click="operationSet(0)">{{$t('operation')}}</span>-->
<!--      <span class="set-btn" :class="{'operation': onoff}" @click="operationSet(1)">{{$t('settings')}}</span>-->
<!--    </div>-->
<!--    操作-->
    <div class="operate-way-info no-padding-bottom" id="content-info">
      <div id="color-wrapper" class="color-wrapper">
        <color-picker :colorId="colorId" :colorType="operateType" :deviceDetail="controlDevice" :uuids="[controlDevice.address]" :isgroups="0" :homeMenuSelect="homeMenuSelect" v-on:operationSet="operationSet"></color-picker>
      </div>
    </div>
<!--    设置-->
    <div v-show="isOperation === 3" class="set-base">
      <div class="device-detail">
<!--        设备名称-->
        <div @click="setClick(1)" class="item">
          <span class="name">{{$t('devicename')}}</span>
          <span class="content">{{title}}</span>
          <i v-show="!isNode" class="icon-right text-color"></i>
        </div>
<!--        群组信息-->
        <div @click="setClick(2)" class="item">
          <span class="name">{{$t('groupinfo')}}</span>
          <span class="content">{{groupInfo}}</span>
<!--          <i class="icon-right text-color"></i>-->
        </div>
<!--        应用信息-->
        <div @click="setClick(3)" class="item">
          <span class="name">{{$t('appinfo')}}</span>
          <span class="content">{{appInfo}}</span>
<!--          <i class="icon-right text-color"></i>-->
        </div>
<!--        设备地址-->
        <div @click="setClick(4)" class="item">
          <span class="name">{{$t('devicedescription')}}</span>
          <span class="content">{{nodeAddress}}</span>
<!--          <i class="icon-right text-color"></i>-->
        </div>
      </div>
      <div class="device-detail">
<!--        在线升级-->
        <div @click="setClick(5)" class="item">
          <span class="name">{{$t('onlineUpgrade')}}</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
      <div class="device-detail">
<!--        开发者模式-->
        <div @click="setClick(6)" class="item">
          <span class="name">{{$t('developermodel')}}</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
<!--      重置设备-->
      <div class="delete-btn">
        <mt-button @click="setClick(7)"  class="mint-button mint-button--danger mint-button--large is-plain">
          <label class="mint-button-text">{{$t('resetdevice')}}</label>
        </mt-button>
      </div>
      <div class="delete-btn" v-show="homeMenuSelect !== 1">
        <mt-button @click="setClick(8)"  class="mint-button mint-button--danger mint-button--large is-plain">
          <label class="mint-button-text">{{$t('deletedevice')}}</label>
        </mt-button>
      </div>
    </div>
<!--    麦克风-->
    <div v-show="isOperation === 4" class="set-base">
      <div class="range-info-wrap">
        <div class="range-title flex flex-ac flex-jcb"><span class="blod"></span></div>
        <div class="range-info">
          <mt-range
            v-model="sensitivity"
            :min="0"
            :max="100"
            @change="editSensitivity"
            :step="1">
          </mt-range>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapMutations, mapGetters, mapState} from 'vuex'
import colorPicker from '../components/colorPicker'
import Constant from '../assets/tool/constant'
import Util from '../assets/tool/utils'
import {MessageBox, Indicator} from "mint-ui"
export default {
  components: {
    colorPicker
  },
  data () {
    return {
      blueEnable: false,
      controlDevice: this.$route.query.item,
      groupsList: [],
      title: 'light',
      colorId: 'device-color',
      temperatureId: 'device-temperature',
      operateType: Constant.RECENT_TYPE_DEVICE,
      isRoom: 'false',
      onoff: false,
      sensitivity: 50,
      isOperation: 0,
      groupInfo: '客厅',
      appInfo: '灯',
      nodeAddress: '',
      isNode: true,
      groupAddress: '',
      homeMenuSelect: this.$route.query.menuSelect
    }
  },
  watch: {
    '$store.state.blueInfo': function () {
      console.log('group operation blueEnable 页面蓝牙发生变化' + this.$store.state.blueInfo)
      if (!this.$store.state.blueInfo) {
        this.$store.commit('setIsBleConnect', false)
        Util.messageRemind(this.$t('bledisconnect'))
      }
    }
  },
  created () {
    var self = this
    window['meshMessageCallback'] = (res) => {
      self.meshMessageCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
  },
  methods: {
    ...mapMutations({
      setNum: 'SET_NUM'
    }),
    editSensitivity () {
      console.log('麦克风灵敏度： ' + this.sensitivity)
      var self = this
      var recordAudio = {}
      recordAudio['interval'] = 50 + 500 * self.sensitivity / 100
      recordAudio['dstAddress'] = self.controlDevice.elements[0].address
      recordAudio['nodeAddress'] = self.controlDevice.address
      console.log('operationSet: ' + JSON.stringify(recordAudio))
      EspBleMesh.startRecordAudio(JSON.stringify(recordAudio))
    },
    editDeviceB: function(brightness) {
      console.log('editDeviceB: ' + this.currentBrightness)
      var ctlArr = [0, this.currentTemperature / 100, this.currentBrightness / 100]
      this.editDevice(ctlArr)
    },
    scanDevice () {
      console.log('开始扫描')
    },
    showBlueFail () {
      console.log('点击蓝牙按钮')
    },
    hide () {
      EspBleMesh.stopRecordAudio()
      this.$router.goBack()
    },
    onBackPressed () {
      EspBleMesh.stopRecordAudio()
      this.$router.goBack()
    },
    operationSet (index) {
      console.log('操作设置切换: ' + index)
      var self = this
      this.isOperation = index
      var recordAudio = {}
      if (index === 4) {
        EspBleMesh.stopRecordAudio()
        recordAudio['interval'] = 50 + 500 * self.sensitivity / 100
        recordAudio['dstAddress'] = self.controlDevice.elements[0].address
        recordAudio['nodeAddress'] = self.controlDevice.address
        console.log('operationSet: ' + JSON.stringify(recordAudio))
        EspBleMesh.startRecordAudio(JSON.stringify(recordAudio))
      } else {
        EspBleMesh.stopRecordAudio()
      }
      // this.onoff = !this.onoff
    },
    setClick (index) {
      var self = this
      console.log('设备名字')
      if (!this.$store.state.isBleConnect) {
        Util.messageRemind(this.$t('bledisconnect'))
        return
      }
      switch (index) {
        case 1:
          if (self.isNode) {
            return
          }
          MessageBox.prompt(self.$t('devicename'), {inputValue: self.deviceName, inputPlaceholder: self.$t('devicename'),
            inputValidator:function(v){if (Util.stringToBytes(v).length > 32){return false} {}},
            inputErrorMessage: self.$t('longDesc'),
            confirmButtonText: self.$t('confirmBtn'), cancelButtonText: self.$t('cancelBtn')}).then(function(obj) {
            self.title = obj.value;
            var updateNodeNameData = {'name': self.title, 'address': self.nodeAddress}
            console.log('FBY operation updateNodeName: ' + JSON.stringify(updateNodeNameData))
            EspBleMesh.updateNodeName(JSON.stringify(updateNodeNameData))
          }).catch(function(err) {
            // self.onBackIndex();
          });
          break
        case 5:
          self.$router.togo({
            name: 'newNodeUpgrade'
          })
          break
        case 6:
          break
        case 7:
          self.configNodeReset()
          break
        case 8:
          if (self.homeMenuSelect === 1) {
            return
          } else if (self.homeMenuSelect === 0) {
            self.deleteNetworkNode()
          } else {
            self.groupsDeleteNode()
          }
          break
      }
    },
    // 删除网络节点本地数据
    deleteNetworkNode () {
      var self = this
      var deleteNodeData = {}
      deleteNodeData = {'address': self.controlDevice.address}
      console.log('FBY operation deleteNodeData: ' + JSON.stringify(deleteNodeData))
      EspBleMesh.deleteNode(JSON.stringify(deleteNodeData))
      Util.messageRemind(self.$t('localDataDel'))
      setTimeout(function () {
        self.hide()
      }, 1000)
    },
    // 重置设备
    configNodeReset () {
      var self = this
      var sendMeshData = {}
      Indicator.open()
      setTimeout(function () {
        Indicator.close()
      }, 5000)
      sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_NODE_RESET
      if (!self.isNode) {
        console.log('FBY operation reset group address' + JSON.stringify(self.controlDevice))
        sendMeshData[Constant.KEY_DST_ADDRESS] = self.controlDevice.nodeAddress
      } else {
        console.log('FBY operation reset node address' + JSON.stringify(self.controlDevice))
        sendMeshData[Constant.KEY_DST_ADDRESS] = self.controlDevice.address
      }
      console.log('operation node reset sendMeshData:' + JSON.stringify(sendMeshData))
      Util.sendMeshMessage(self, sendMeshData)
    },
    // 群组中删除设备
    groupsDeleteNode () {
      var self = this
      var sendMeshData = {}
      Indicator.open()
      setTimeout(function () {
        Indicator.close()
      }, 5000)
      sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_MODEL_SUBSCRIPTION_DELETE
      sendMeshData[Constant.KEY_DST_ADDRESS] = self.controlDevice.nodeAddress
      sendMeshData[Constant.KEY_GROUP_ADDRESS] = self.groupAddress
      sendMeshData[Constant.KEY_ELEMENT_ADDRESS] = self.controlDevice.elementAddress
      console.log('operation node sendMeshData:' + JSON.stringify(sendMeshData))
      Util.sendMeshMessage(self, sendMeshData)
    },
    //设备状态变化回调
    meshMessageCallback (res) {
      var self = this
      Util.meshMessageCallback(res, self, 'operation')
    },
    configModelSubscriptionCallback (res) {
      var self = this
      Indicator.close()
      Util.messageRemind(self.$t('groupsdeletedevice'))
      setTimeout(function () {
        self.hide()
      }, 500)
    },
    configNodeResetCallback (res) {
      var self = this
      Indicator.close()
      Util.messageRemind(self.$t('deviceresetsuc'))
      setTimeout(function () {
        self.hide()
      }, 500)
    }
  },
  mounted () {
    var self = this
    self.blueEnable = self.$store.state.blueInfo
    console.log('operation Home 传参打印 item: ' + JSON.stringify(self.$route.query.item))
    console.log('operation Home 传参打印 isNode: ' + JSON.stringify(self.$route.query.isNode))
    self.controlDevice = self.$store.state.deviceInfo
    console.log('operation controlDevice' + JSON.stringify(self.controlDevice))
    self.title = self.controlDevice.name
    self.nodeAddress = self.controlDevice.address
    self.groupsList = self.$store.state.deviceGroupList
    console.log('FBY operation groupsList: ' + JSON.stringify(self.groupsList))
    self.groupsList.forEach(function (item) {
      if (self.controlDevice.elements[0].models[0].groups[0] === item.address) {
        self.groupInfo = item.name
      }
    })
    if (!Util._isEmpty(self.$route.query)) {
      self.isNode = self.$route.query.isNode
      self.homeMenuSelect = self.$route.query.menuSelect
      self.groupAddress = self.$route.query.groupAddress
      console.log('isNode: ' + self.isNode + ' groupAddress: ' + self.groupAddress)
    }
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
      console.log('operation 页面蓝牙发生变化' + this.blueEnable)
      return this.blueEnable
    }
  }
}
</script>

<style scoped lang="less">
  @import '../assets/css/index.less';
  .right-text {
    top: 0px;
  }
  .operate-way-info {
    position: absolute;
    top: 50px;
    width: 100%;
    height: calc(100% - 80px);
    background: transparent;
  }
  .no-padding-bottom {
    padding-bottom: 0 !important;
  }
  .operation-set-base {
    width: 100%;
  }
  .delete-btn {
    margin: 30px;
  }
  /*设置列表样式*/
  .set-base {
    position: absolute;
    top: 50px;
    width: 100%;
    height: calc(100% - 150px);
    overflow: hidden;
  }
  .device-detail {
    margin: 30px;
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
    width: 30%;
  }
  .device-detail .content {
    flex: 1;
    color: @Secondary-font-color;
    margin-right: 10px;
    width: 50%;
    text-align: right;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .text-color {
    color: @Secondary-font-color;
    font-size: 12px;
  }
  .range-info-wrap {
    padding: 60px 20px;
    .range-title {
      margin-bottom: 6px;
    }
  }
</style>
