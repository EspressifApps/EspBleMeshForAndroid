<template>
  <div style="height: 100%; width: 100%;" v-show="true">
    <div v-show="foodIndex < 3" class="picker-wrapper">
      <div class="picker-info" :class="{'height-100': !pickerShow || !currentStatus}">
        <div v-show="pickerShow && currentStatus && isShowRgb" :id="colorId" class="color-picker">
        </div>
<!--        , 'box-shadow': boxShadow-->
        <div class="white-color flex flex-ac flex-jcc" :class="{'black-bg': !currentStatus}" :style="{'background': borderColor}">
          <span class="icon-light"></span>
        </div>
      </div>
    </div>
    <div v-show="currentStatus && foodIndex < 3" class="range-wrapper">
      <div v-show="pickerShow && isShowRgb">
        <div class="range-info-wrap">
          <div class="range-title flex flex-ac flex-jcb"><span class="blod">{{$t('luminance')}}</span><span>{{currentLuminance}}%</span></div>
          <div class="range-info">
            <mt-range
              v-model="currentLuminance"
              :min="0"
              :max="100"
              @change="editDeviceL"
              :step="1">
            </mt-range>
          </div>
        </div>
        <div class="range-info-wrap">
          <div class="range-title flex flex-ac flex-jcb"><span class="blod">{{$t('saturation')}}</span><span>{{currentSaturation}}%</span></div>
          <div class="range-info">
            <mt-range
              v-model="currentSaturation"
              :min="0"
              :max="100"
              @change="editDeviceS"
              :step="1">
            </mt-range>
          </div>
        </div>
      </div>
      <div v-show="!pickerShow">
        <div class="range-info-wrap">
          <div class="range-title flex flex-ac flex-jcb"><span class="blod">{{$t('brightness')}}</span><span>{{currentBrightness}}%</span></div>
          <div class="range-info">
            <mt-range
              v-model="currentBrightness"
              :min="0"
              :max="100"
              @change="editDeviceB"
              :step="1">
            </mt-range>
          </div>
        </div>
        <div class="range-info-wrap">
          <div class="range-title flex flex-ac flex-jcb"><span class="blod">{{$t('btnTemperature')}}</span><span>{{currentTemperature * 13 + 2700}}K</span></div>
          <div class="range-info">
            <mt-range
              v-model="currentTemperature"
              :min="0"
              :max="100"
              @change="editDeviceT"
              :step="1">
            </mt-range>
          </div>
        </div>
      </div>
    </div>
    <div class="control flex flex-ac">
<!--      v-show="isgroups!==1"-->
      <div @click="close()" class="flex-1 control-item flex flex-ac flex-v" :class="{'active': !currentStatus}">
        <div><i class="icon-power"></i></div>
        <span v-show="!currentStatus">{{$t('openLight')}}</span>
        <span v-show="currentStatus">{{$t('closeLight')}}</span>
      </div>
      <div v-show="isShowRgb" @click="showPicker(1)" class="flex-1 control-item flex flex-ac flex-v" :class="{'active': currentStatus && foodIndex === 1}">
        <div><i class="icon-cg"></i></div>
        <span>{{$t('colorLight')}}</span>
      </div>
      <div v-show="isShowRgb" @click="showPicker(2)" class="flex-1 control-item flex flex-ac flex-v" :class="{'active': currentStatus && foodIndex === 2}">
        <div><i class="icon-bg"></i></div>
        <span>{{$t('whiteLight')}}</span>
      </div>
      <div v-show="isShowRgb" @click="showPicker(3)" class="flex-1 control-item flex flex-ac flex-v" :class="{'active': currentStatus && foodIndex === 3}">
        <div><i class="icon-setting"></i></div>
        <span>{{$t('settings')}}</span>
      </div>
      <div v-show="isShowRgb" @click="showPicker(4)" class="flex-1 control-item flex flex-ac flex-v" :class="{'active': currentStatus && foodIndex === 4}">
        <div><i class="icon-mic"></i></div>
        <span>{{$t('microphone')}}</span>
      </div>
    </div>
  </div>
</template>
<script>
import Constant from '../assets/tool/constant'
import Raphael from 'raphael'
import Util from '../assets/tool/utils'
export default {
  props: {
    colorId: {
      type: String
    },
    temperatureId: {
      type: String
    },
    colorType: {
      type: String
    },
    uuids: {
      type: Array
    },
    deviceDetail: {
      type: Object
    },
    isgroups: {
      type: String
    },
    homeMenuSelect: {
      type: String
    }
  },
  data () {
    return {
      blueEnable: true,
      locationGranted: true,
      locationEnabled: true,
      initSize: 240,
      showColor: false,
      isShowRgb: true,
      pickerShow: true,
      foodIndex: 1,
      device: [],
      deviceList: [],
      currentHue: this.$store.state.currentHue,
      currentSaturation: this.$store.state.currentSaturation,
      currentLuminance: this.$store.state.currentLuminance,
      currentTemperature: this.$store.state.currentTemperature,
      currentBrightness: this.$store.state.currentBrightness,
      boxShadow: 'none',
      borderColor: '',
      currentStatus: false,
      connectContent: 0,
      connectData: '',
      homeDeviceList: this.$store.state.homeDeviceList,
      fastNodesList: this.$store.state.fastNodesList,
      homedeviceGroup: this.$store.state.deviceGroupList,
      nowTime: ''
    }
  },
  created () {
    const self = this
    window['scanCallback'] = (res) => {
      self.scanCallback(res)
    }
    window['connectCallback'] = (res) => {
      self.connectCallback(res)
    }
  },
  mounted () {
    var self = this
    var nowDate = new Date()
    self.nowTime = nowDate.getTime()
    console.log('当前时间： ' + self.nowTime)
    self.connectContent = 0
    // this.deviceList = [
    //   {'name': 'light-1', 'mac': '000000001', 'group': 'group1', 'tid': '1', 'characteristics': [[{'cid': '1', 'value': '1'}]]},
    //   {'name': 'light-2', 'mac': '000000002', 'group': 'group2', 'tid': '1', 'characteristics': [{'cid': '1', 'value': '1'}]},
    //   {'name': 'light-3', 'mac': '000000003', 'group': 'group3', 'tid': '1', 'characteristics': [{'cid': '1', 'value': '1'}]},
    //   {'name': 'light-4', 'mac': '000000004', 'group': 'group4', 'tid': '1', 'characteristics': [{'cid': '1', 'value': '1'}]},
    //   {'name': 'light-5', 'mac': '000000005', 'group': 'group5', 'tid': '1', 'characteristics': [{'cid': '1', 'value': '1'}]},
    //   {'name': 'light-6', 'mac': '000000006', 'group': 'group6', 'tid': '1', 'characteristics': [{'cid': '1', 'value': '1'}]},
    //   {'name': 'light-7', 'mac': '000000007', 'group': 'group7', 'tid': '1', 'characteristics': [{'cid': '1', 'value': '1'}]},
    //   {'name': 'light-8', 'mac': '000000008', 'group': 'group8', 'tid': '1', 'characteristics': [{'cid': '0', 'value': '1'}]},
    //   {'name': 'light-9', 'mac': '000000009', 'group': 'group9', 'tid': '1', 'characteristics': [{'cid': '0', 'value': '1'}]},
    //   {'name': 'light-10', 'mac': '000000010', 'group': 'group10', 'tid': '1', 'characteristics': [{'cid': '0', 'value': '1'}]}]
    if (self.isgroups === 1) {
      self.deviceList = self.$store.state.deviceGroupList
    } else {
      self.deviceList = self.$store.state.homeDeviceList
    }
    console.log('operation 打印 deviceList： ' + JSON.stringify(self.deviceList))
    console.log('operation 传参打印 deviceDetail' + JSON.stringify(self.deviceDetail) + 'uuids :' + self.uuids)
    // this.device = {'name': 'light-1', 'mac': '000000001', 'group': 'group1', 'tid': '1', 'characteristics': [[{'cid': '1', 'value': '1'}]]}
    this.show()
  },
  methods: {
    show: function () {
      var self = this
      var hueValue = 0
      var saturation = 100
      var luminance = 100
      if (self.colorType === Constant.RECENT_TYPE_DEVICE) {
        var obj = self.getDeviceColor()
        hueValue = obj.hueValue
        saturation = obj.saturation
        luminance = obj.luminance
      } else {
        self.showRgb()
      };
      var h = hueValue / 360
      var s = saturation / 100
      var b = luminance / 100
      var hsbColor = 'hsb(' + h + ',' + s + ',' + b + ')'
      self.currentHue = hueValue
      self.currentSaturation = saturation
      self.currentLuminance = luminance
      console.log('FBY show hueValue: ' + hueValue + ' saturation: ' + saturation + ' luminance: ' + luminance)
      self.setBordeColor(h, s, 1, b)
      self.initColor(hsbColor)
      setTimeout(function () {
        self.showColor = true
        self.getDeviceStatus()
      })
    },
    getDeviceColor: function () {
      var self = this
      var hueValue = 0
      var saturation = 0
      var luminance = 100
      // var tid = self.deviceDetail.tid
      self.isShowRgb = true
      self.pickerShow = true
      // if (tid !== Constant.TABLE_LAMP_LIGHT && tid !== Constant.FLOOR_LAMP_LIGHT &&
      //   tid !== Constant.CHANDELIER_LIGHT && tid !== Constant.WALL_LAMP_LIGHT) {
      //   self.isShowRgb = true
      //   self.pickerShow = true
      // } else {
      //   console.log('1111')
      //   self.isShowRgb = false
      //   self.pickerShow = false
      // }
      if (!Util._isEmpty(self.deviceDetail.color) && self.deviceDetail.color.length === 3) {
        hueValue = self.deviceDetail.color[0] * 360
        saturation = parseInt(self.deviceDetail.color[1] * 100)
        luminance = parseInt(self.deviceDetail.color[2] * 100)
      }
      return {hueValue: hueValue,
        saturation: saturation,
        luminance: luminance}
    },
    showRgb: function () {
      var self = this
      var flag = false
      console.log('forEach 1')
      self.deviceList.forEach(function (item, i) {
        if (self.uuids.indexOf(item.address) !== -1) {
          var tid = item.tid
          if (tid !== Constant.TABLE_LAMP_LIGHT && tid !== Constant.FLOOR_LAMP_LIGHT &&
            tid !== Constant.CHANDELIER_LIGHT && tid !== Constant.WALL_LAMP_LIGHT) {
            flag = true
            return false
          }
        }
      })
      if (flag) {
        self.pickerShow = true
      } else {
        console.log('2222')
        self.pickerShow = false
      }
      // console.log('2222 isShowRgb:' + isShowRgb + 'flag' + flag)
      self.isShowRgb = flag
    },
    getDeviceStatus: function () {
      var self = this
      self.currentStatus = false
      // self.currentStatus = true
      if (!Util._isEmpty(self.uuids) && self.uuids.length > 0) {
        self.deviceList.forEach(function (item, i) {
          if (self.uuids.indexOf(item.address) > -1) {
            if (self.isgroups === 1) {
              if (item.isOnOff) {
                self.currentStatus = true
                return false
              }
            } else {
              if (item.status) {
                self.currentStatus = true
                return false
              }
            }
          }
        })
      }
    },
    hide: function () {
      this.$emit('colorShow')
    },
    hideColor: function () {
      this.showColor = false
    },
    initWarmCold: function(currentTemperature, currentBrightness) {
      var r3 = 248,
        g3 = 207,
        b3 = 109,
        r2 = 255,
        g2 = 255,
        b2 = 255,
        r = 0,
        g = 0,
        b = 0;
      var percentage = currentTemperature / 100 * 2;
      r = r2 - Math.floor((r2 - r3) * percentage);
      g = g2 - Math.floor((g2 - g3) * percentage);
      b = b2 - Math.floor((b2 - b3) * percentage);
      this.borderColor = "rgba(" + r + "," + g + "," + b + "," + (currentBrightness / 100) + ")";
      console.log('initWarmCold borderColor: ' + this.borderColor)
      this.boxShadow = "0px 0px " + (currentBrightness * 1.1) +"px " + this.borderColor;
    },
    initColor: function (hsbColor) {
      // var doc = $(document)
      // var width = doc.width()
      // var height = doc.height()
      var width = document.documentElement.clientWidth || window.innerWidth || document.body.clientWidth
      var height = document.documentElement.clientHeight || window.innerHeight || document.body.clientHeight
      if (height <= 520) {
        this.initSize = height * 0.31
      } else {
        this.initSize = height * 0.345
      }
      if (this.initSize > 240) {
        this.initSize = 240
      }
      this._initColorPicker(hsbColor, this.colorId, (width - this.initSize) / 2, 100, true)
    },
    showPicker (index) {
      var self = this
      if (self.currentStatus) {
        console.log('showPicker index: ' + index)
        self.foodIndex = index
        if (index === 1) {
          self.$emit('operationSet', 1)
          self.pickerShow = true
          self.setBordeColor(self.currentHue / 360, self.currentSaturation / 100, 1, self.currentLuminance / 100)
        } else if (index === 2) {
          self.$emit('operationSet', 2)
          self.pickerShow = false
          self.initWarmCold(self.currentTemperature, self.currentBrightness);
          // this._initColorPicker([self.currentTemperature, self.currentBrightness], this.colorId, (width - this.initSize) / 2, 100, true)
        } else if (index === 3) {
          self.$emit('operationSet', 3)
        } else if (index === 4) {
          self.$emit('operationSet', 4)
        }
      }
    },
    changValue: function (type, value) {
      var self = this
      switch (type) {
        case 'luminance': self.currentLuminance = value; break
        case 'saturation': self.currentSaturation = value; break
        default: break
      }
    },
    changRange: function (type) {
      var self = this
      switch (type) {
        case 'luminance': self.editDeviceL(self.currentLuminance); break
        case 'saturation': self.editDeviceS(self.currentSaturation); break
        default: break
      }
    },
    editDeviceH: function (hueValue) {
      // hueValue = Math.round(parseFloat(hueValue) * 360)
      // this.editDevice(Constant.HUE_CID, hueValue)
      var hslArr = [hueValue, this.currentSaturation / 100, this.currentLuminance / 100]
      console.log('editDeviceH: ' + hslArr)
      this.editDevice(hslArr)
    },
    editDeviceWhite: function () {
      if (!this.currentStatus) {
        this.close()
      } else {
        this.editDeviceS(0)
        this.currentSaturation = 0
      }
    },
    editDeviceS: function (saturation) {
      // this.editDevice(Constant.SATURATION_CID, this.currentSaturation)
      var hslArr = [this.currentHue / 360, this.currentSaturation / 100, this.currentLuminance / 100]
      console.log('editDeviceS: ' + hslArr)
      this.editDevice(hslArr)
    },
    editDeviceL: function (luminance) {
      // this.editDevice(Constant.VALUE_CID, this.currentLuminance)
      var hslArr = [this.currentHue / 360, this.currentSaturation / 100, this.currentLuminance / 100]
      console.log('editDeviceL: ' + hslArr)
      this.editDevice(hslArr)
    },
    editDeviceT: function(temperature) {
      console.log('editDeviceT: ' + this.currentTemperature)
      var ctlArr = [0, this.currentTemperature / 100, this.currentBrightness / 100]
      this.editDevice(ctlArr)
    },
    editDeviceB: function(brightness) {
      console.log('editDeviceB: ' + this.currentBrightness)
      var ctlArr = [0, this.currentTemperature / 100, this.currentBrightness / 100]
      this.editDevice(ctlArr)
    },
    // 开关事件
    close: function () {
      var self = this
      console.log('FBY close deviceDetail: ' + JSON.stringify(self.deviceDetail))
      var sendMeshData = {}
      var nodeAdressArr = []
      if (self.isgroups === 0) {
        self.currentStatus = !self.deviceDetail.status
      } else {
        self.currentStatus = !self.deviceDetail.isOnOff
      }
      sendMeshData[Constant.KEY_OP_CODE] = Constant.GENERIC_ON_OFF_SET_UNACKNOWLEDGED
      if (self.isgroups === 0) {
        if (self.homeMenuSelect > 1) {
          sendMeshData[Constant.KEY_DST_ADDRESS] = self.deviceDetail.elementAddress
          sendMeshData[Constant.KEY_NODE_ADDRESS] = self.deviceDetail.nodeAddress
          nodeAdressArr.push(self.deviceDetail.nodeAddress)
        } else {
          sendMeshData[Constant.KEY_DST_ADDRESS] = self.deviceDetail.address
          sendMeshData[Constant.KEY_NODE_ADDRESS] = self.deviceDetail.address
          nodeAdressArr.push(self.deviceDetail.address)
        }
      } else {
        sendMeshData[Constant.KEY_DST_ADDRESS] = self.deviceDetail.address
        nodeAdressArr.push(self.deviceDetail.address)
      }
      sendMeshData[Constant.KEY_STATE] = self.currentStatus
      if (self.$store.state.isBleConnect) {
        Util.sendMeshMessage(self, sendMeshData)
      } else {
        Util.messageRemind(self.$t('trybleconnect'))
        self.homeDeviceScan()
      }
      console.log('close nodeAdressArr: ' + JSON.stringify(nodeAdressArr) + 'isgroups: ' + self.isgroups)
      nodeAdressArr.forEach(function (item) {
        if (self.isgroups === 0) {
          Util.updateNodesStatus(self, item, self.currentStatus, 'status')
        } else {
          Util.updateGroupsStatus(self, item, self.currentStatus, 'isOnOff')
        }
      })
    },
    scanCallback (res) {
      var self = this
      Util.scanCallback(res, self)
    },
    connectCallback (res) {
      var self = this
      Util.connectCallback(res, self, false)
    },
    editDevice: function (colorArr) {
      var self = this
      var nextDate = new Date()
      // 防止重复点击
      if (nextDate.getTime() - self.nowTime >= 1000) {
        self.nowTime = nextDate.getTime()
      } else {
        Util.messageRemind(self.$t('donotfast'))
        return
      }
      // var meshs = []
      // // var changeList = []
      // // var macs = this.macs
      // meshs.push({cid: cid, value: value})
      // if (cid === Constant.HUE_CID) {
      //   meshs.push({cid: Constant.SATURATION_CID, value: 100})
      //   self.currentSaturation = 100
      //   // TODO
      //   // $("#" + self.colorId + "saturation").slider( "value", 100);
      // }
      // console.log('FBY getDeviceColor hueValue: ' + hueValue + ' saturation: ' + saturation + ' luminance: ' + luminance)
      var sendMeshData = {}
      if (self.foodIndex === 2) {
        // 缓存数据
        self.$store.commit('setCurrentTemperature', self.currentHue)
        self.$store.commit('setCurrentBrightness', self.currentHue)
        sendMeshData[Constant.KEY_OP_CODE] = Constant.LIGHT_CTL_SET_UNACKNOWLEDGED
        sendMeshData[Constant.KEY_CTL] = colorArr
        self.initWarmCold(self.currentTemperature, self.currentBrightness);
      } else {
        // 缓存数据
        self.$store.commit('setCurrentHue', self.currentHue)
        self.$store.commit('setCurrentSaturation', self.currentHue)
        self.$store.commit('setCurrentLuminance', self.currentHue)
        sendMeshData[Constant.KEY_OP_CODE] = Constant.LIGHT_HSL_SET_UNACKNOWLEDGED
        sendMeshData[Constant.KEY_HSL] = colorArr
        self.setBordeColor(self.currentHue / 360, self.currentSaturation / 100, 1, self.currentLuminance / 100)
      }
      sendMeshData[Constant.KEY_DST_ADDRESS] = self.deviceDetail.address

      // 非群组设备
      if (self.isgroups === 0) {
        sendMeshData[Constant.KEY_NODE_ADDRESS] = self.deviceDetail.address
      }
      if (self.$store.state.isBleConnect) {
        Util.sendMeshMessage(self, sendMeshData)
      } else {
        Util.homeDeviceScan(self)
        Util.messageRemind(this.$t('bledisconnect'))
      }
      // this.setDeviceStatus(colorArr)
      if (self.isgroups === 1) {
        self.deviceList.forEach(function (item) {
          if (self.uuids.indexOf(item.address) > -1) {
            item.models.forEach(function (modelItem) {
              Util.updateNodesStatus(self, modelItem.nodeAddress, colorArr, 'color')
            })
          }
        })
      } else {
        Util.updateNodesStatus(self, self.deviceDetail.address, colorArr, 'color')
      }
    },
    scanTimeout () {
      var self = this
      var isRemind = Util.scanTimeout(this)
      if (isRemind) {
        Util.messageRemind(self.$t('unconnectable'))
      }
    },
    setDeviceStatus: function (colorValue) {
      var self = this
      var changeList = []
      self.deviceList.forEach(function (item, i) {
        if (self.uuids.indexOf(item.address) > -1) {
          item.color = colorValue
        }
        changeList.push(item)
      })
      self.deviceList = changeList
      // if (cid === Constant.STATUS_CID) {
      //   self.getDeviceStatus()
      // }
    },
    setBordeColor: function (h, s, p, l) {
      var rgb = Raphael.getRGB('hsl(' + h + ',' + s + ',' + l + ')')
      this.borderColor = 'rgba(' + Math.floor(rgb.r) + ', ' + Math.floor(rgb.g) + ', ' + Math.floor(rgb.b) + ', ' + p + ')'
    },
    _initColorPicker: function (hsbColor, id, left, top, flag) {
      var self = this
      // $("#" + id).empty();
      // vm.$refs('#' + id).empty()
      Raphael(function () {
        var cp = Raphael.colorwheel(left, top, self.initSize, hsbColor, document.getElementById(id), flag)
        var h = 0
        var isChange = false
        var onchange = function (item) {
          return function (clr) {
            clr = Raphael.color(clr, self.pickerShow)
            h = clr.h
            isChange = true
            self.currentHue = h * 360
            console.info(JSON.stringify(clr))
          }
        }
        cp.onchange = onchange(cp)
        document.getElementById(id).addEventListener('touchend', () => {
          if (isChange) {
            self.editDeviceH(h)
            isChange = false
          }
        })
      })
    }
  },
  computed: {
    getBlueEnable () {
      const self = this
      self.blueEnable = this.$store.state.blueInfo
      self.locationGranted = this.$store.state.locationGranted
      self.locationEnabled = this.$store.state.locationEnabled
      console.log('状态实时更新 blueEnable:' + this.blueEnable + '  locationGranted:' + this.locationGranted + '  locationEnabled:' + this.locationEnabled)
      return self.blueEnable
    }
  }
}
</script>
<style scoped lang="less">
  @import '../assets/css/index.less';
  .color-picker {
    position: relative;
    color: rgba(128,128,128,1);
  }
  .range-wrapper {
    padding: 0 15px;
  }
  .range-info-wrap {
    padding: 10px 0;
    .range-title {
      margin-bottom: 6px;
    }
  }

</style>
