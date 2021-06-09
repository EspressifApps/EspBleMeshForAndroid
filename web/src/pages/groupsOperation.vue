<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide"><i class="icon-left back"></i></span>{{groupsName}}</h4>
    </div>
<!--    <div class="operation-set-btn">-->
<!--      <span class="operation-btn" :class="{'operation': !onoff}" @click="operationSet(0)">{{$t('operation')}}</span>-->
<!--      <span class="set-btn" :class="{'operation': onoff}" @click="operationSet(1)">{{$t('settings')}}</span>-->
<!--    </div>-->
    <!--    操作-->
    <div class="operate-way-info no-padding-bottom" id="content-info">
      <div id="color-wrapper" class="color-wrapper">
        <color-picker :colorId="colorId" :colorType="operateType" :deviceDetail="groupInfo" :uuids="[groupInfo.address]" :isgroups="1" v-on:operationSet="operationSet"></color-picker>
      </div>
    </div>
    <!--    设置-->
    <div v-show="isOperation === 3" class="set-base">
      <div class="device-detail">
        <!--        群组名称-->
        <div @click="groupName(1)" class="item">
          <span class="name">{{$t('groupname')}}</span>
          <span class="content">{{groupsName}}</span>
          <i class="icon-right text-color"></i>
        </div>
        <!--        群组设备-->
        <div @click="groupName(2)" class="item">
          <span class="name">{{$t('groupDevice')}}</span>
          <span class="content">{{groupInfo.models.length}}</span>
          <i class="icon-right text-color"></i>
        </div>
        <!--        应用信息-->
        <div @click="groupName(3)" class="item">
          <span class="name">{{$t('appinfo')}}</span>
          <span class="content">{{appInfo}}</span>
<!--          <i class="icon-right text-color"></i>-->
        </div>
        <!--        群组描述-->
        <div @click="groupName(4)" class="item">
          <span class="name">{{$t('groupdetail')}}</span>
          <span class="content">{{groupInfo.description}}</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
      <div class="device-detail">
        <!--        在线升级-->
        <div @click="groupName(5)" class="item">
          <span class="name">{{$t('onlineUpgrade')}}</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
      <div class="device-detail">
        <!--        开发者模式-->
        <div @click="groupName(6)" class="item">
          <span class="name">{{$t('developermodel')}}</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
      <div class="delete-btn">
        <mt-button @click="groupName(7)"  class="mint-button mint-button--danger mint-button--large is-plain">
          <label class="mint-button-text">{{$t('disbandgroup')}}</label>
        </mt-button>
      </div>
    </div>
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
import {MessageBox} from "mint-ui";
export default {
  components: {
    colorPicker
  },
  data () {
    return {
      groupsName: this.$store.state.groupInfo.name,
      groupsDescription: this.$store.state.groupInfo.description,
      groupInfo: this.$store.state.groupInfo,
      colorId: 'device-color',
      temperatureId: 'device-temperature',
      operateType: Constant.RECENT_TYPE_DEVICE,
      isRoom: 'false',
      onoff: this.$store.state.groupInfo.onoff,
      isOperation: 0,
      appInfo: '灯',
      sensitivity: 50,
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
      recordAudio['dstAddress'] = self.groupInfo.address
      console.log('operationSet: ' + JSON.stringify(recordAudio))
      EspBleMesh.startRecordAudio(JSON.stringify(recordAudio))
    },
    scanDevice () {
      console.log('开始扫描')
    },
    showBlueFail () {
      console.log('点击蓝牙按钮')
    },
    hide () {
      this.$store.commit('setIsOperation', this.isOperation)
      EspBleMesh.stopRecordAudio()
      this.$router.goBack()
    },
    onBackPressed () {
      EspBleMesh.stopRecordAudio()
      this.$router.goBack()
    },
    // hiddenOperationSet () {
    //   this.isOperation = true
    // },
    // operationSet () {
    //   console.log('操作设置切换')
    //   this.onoff = !this.onoff
    //   this.isOperation = false
    // },
    operationSet (index) {
      console.log('操作设置切换: ' + index)
      console.log('麦克风灵敏度： ' + this.sensitivity)
      var self = this
      this.isOperation = index
      var recordAudio = {}
      if (index === 4) {
        EspBleMesh.stopRecordAudio()
        recordAudio['interval'] = 50 + 500 * self.sensitivity / 100
        recordAudio['dstAddress'] = self.groupInfo.address
        console.log('operationSet: ' + JSON.stringify(recordAudio))
        EspBleMesh.startRecordAudio(JSON.stringify(recordAudio))
      } else {
        EspBleMesh.stopRecordAudio()
      }
      // this.onoff = !this.onoff
    },
    groupName (index) {
      var self = this
      console.log('点击第' + index + '列')
      if (!this.$store.state.isBleConnect) {
        Util.messageRemind(this.$t('bledisconnect'))
        return
      }
      switch(index) {
        case 1:
          MessageBox.prompt(self.$t('groupname'), {inputValue: self.groupsName, inputPlaceholder: self.$t('groupname'),
            inputValidator:function(v){if (Util.stringToBytes(v).length > 32){return false} {}},
            inputErrorMessage: self.$t('longDesc'),
            confirmButtonText: self.$t('confirmBtn'), cancelButtonText: self.$t('cancelBtn')}).then(function(obj) {
            self.groupsName = obj.value;
            self.updateGroups(obj.value, self.groupsDescription)
          }).catch(function(err) {
            // self.onBackIndex();
          });
          break
        case 2:
          this.$store.commit('setIsOperation', this.isOperation)
          self.$router.togo({
            name: 'addDevices',
            query: self.groupInfo
          })
          break
        case 3:
          break
        case 4:
          MessageBox.prompt(self.$t('groupdetail'), {inputValue: self.groupsDescription, inputPlaceholder: self.$t('groupdetail'),
            inputValidator:function(v){if (Util.stringToBytes(v).length > 32){return false} {}},
            inputErrorMessage: self.$t('longDesc'),
            confirmButtonText: self.$t('confirmBtn'), cancelButtonText: self.$t('cancelBtn')}).then(function(obj) {
            self.groupsDescription = obj.value;
            self.updateGroups(self.groupsName, obj.value)
          }).catch(function(err) {
            // self.onBackIndex();
          });
          break
        case 5:
          break
        case 6:
          break
        case 7:
          self.disbandGroup()
          break
      }
    },
    updateGroups (name, description) {
      var self = this
      var updateGroups = {}
      updateGroups['address'] = self.groupInfo.address
      updateGroups['name'] = name
      updateGroups['description'] = description
      console.log('FBY 修改群组信息： ' + JSON.stringify(updateGroups))
      EspBleMesh.updateGroup(JSON.stringify(updateGroups))
    },
    // 解绑群组
    disbandGroup () {
      var self = this
      var sendMeshData = {}
      var removeData = {}
      removeData['address'] = self.groupInfo.address
      console.log('FBY 删除群组本地数据： ' + JSON.stringify(removeData))
      EspBleMesh.removeGroup(JSON.stringify(removeData))
      self.groupInfo.models.forEach(function (item) {
        sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_MODEL_SUBSCRIPTION_DELETE
        sendMeshData[Constant.KEY_DST_ADDRESS] = item.nodeAddress
        sendMeshData[Constant.KEY_ELEMENT_ADDRESS] = item.elementAddress
        sendMeshData[Constant.KEY_GROUP_ADDRESS] = self.groupInfo.address
        Util.sendMeshMessage(self, sendMeshData)
      })
      setTimeout(function () {
        self.hide()
      }, 500)
    }
  },
  mounted () {
    var self = this
  },
  computed: {
    ...mapGetters([
      'number'
    ]),
    ...mapState({
      number: state => state.home.number
    }),
    blueEnable () {
      console.log('group operation 页面蓝牙发生变化' + this.$store.state.blueInfo)
      return this.$store.state.blueInfo
    },
    getGroupInfo () {
      return this.$store.state.groupInfo
    },
    getBleConnect () {
      return this.$store.state.isBleConnect
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
    background: transparent;
    width: 100%;
    height: calc(100% - 80px);
  }
  .no-padding-bottom {
    padding-bottom: 0 !important;
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
  .range-info-wrap {
    padding: 60px 20px;
    .range-title {
      margin-bottom: 6px;
    }
  }
</style>
