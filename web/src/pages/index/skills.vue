<template>
  <div class="index-content-box">
    <div class="home-header">
      <img class="esp-logo" src="../../assets/EspressifLogo.png">
      <p class="home-network">Espressif Network</p>
      <div v-show="!getBlueEnable"  @click="showBlueFail" class="bluetooth-base">
        <i class="icon-bluetooth bluetooth-style"></i>
      </div>
      <i @click.stop="showUl" class="icon-plus right-more"></i>
    </div>
    <div class="select-menu-base">
      <div class="select-menu">
        <mt-navbar v-model="selected">
          <mt-tab-item v-for="(item, i) in selectList" :key="item" :id="i">{{item}}</mt-tab-item>
        </mt-navbar>
      </div>
    </div>
    <div class="content-info scan-device flex-wrapper search-device">
      <div class="overflow-touch">
        <mt-loadmore :top-method="loadTop" @top-status-change="handleTopChange" ref="loadmore">
          <div v-for="(item,index) in homedeviceGroup" :key="item.group" @click.self="showColor(item)" class="item push-none">
            <mt-cell-swipe
              @touchstart.native="sideslip('start', item)"
              @touchmove.native="sideslip('move', item)"
              @touchend.native="sideslip('end', item)"
              :right="rightDelete(item, index)">
              <div @click.stop="showColor(item)" class="item-icon-circle">
                <i class="icon-groups"></i>
                <span>{{item.models.length}}</span>
              </div>
              <div @click.stop="showColor(item)" class="item-name">
                <span>{{item.name}}</span>
                <span class="desc">{{getGroupDes(item.description)}}{{item.address.toString(16)}}</span>
              </div>
              <div class="item-power">
<!--                v-show="isBleConnect"-->
                <div  class="switch-wrapper">
                  <span :class="{'active': !item.isOnOff}" @click="close(item, false)">OFF</span>
                  <span :class="{'active': item.isOnOff}" @click="close(item, true)">ON</span>
                </div>
              </div>
            </mt-cell-swipe>
          </div>
          <div slot="top" class="mint-loadmore-top">
            <span v-show="topStatus !== 'loading'" :class="{ 'rotate': topStatus === 'drop' }">↓</span>
            <div v-show="topStatus === 'loading'" id="loader-wrapper-small">
              <div class="loader loader-small"></div>
              <div class="loader-section section-left"></div>
              <div class="loader-section section-right"></div>
            </div>
          </div>
        </mt-loadmore>
      </div>
    </div>
  </div>
</template>

<script>
import {mapMutations, mapGetters, mapState} from 'vuex'
import Util from "../../assets/tool/utils";
import {MessageBox, Toast} from "mint-ui";
import Constant from "../../assets/tool/constant";
import home from "./home";
export default {
  data () {
    return {
      blueEnable: true,
      locationGranted: true,
      locationEnabled: true,
      selected: 0,
      selectList: [],
      skillsGroupOne:[],
      isBleConnect: false,
      connectContent: 0,
      connectData: '',
      homedeviceGroup:this.$store.state.deviceGroupList,
      groupName: '',
      groupDetail: '',
      homeDeviceList: this.$store.state.homeDeviceList,
      pullLoad: false,
      loadShow: false,
      topStatus: '',
      fastNodesList: this.$store.state.fastNodesList,
      startTime: 0,
      isSideslip: false,
      groupDefaultName: '群组'
    }
  },
  created () {
    const self = this
    window['createGroupCallback'] = (res) => {
      self.createGroupCallback(res)
    }
  },
  methods: {
    ...mapMutations({
      setNum: 'SET_NUM'
    }),
    getGroupDes (des) {
      if (!Util._isEmpty(des)) {
        return des + '  '
      } else {
        return ''
      }
    },
    rightDelete (item, index){
      return[{
        content: '删除',
        stop: false,
        style: { background: 'red', color: '#fff', padding: '0 15px'},
        handler: () => {this.delDevice(item, index)}
      }]
    },
    delDevice (item, index) {
      var self = this
      // 解绑群组
      var sendMeshData = {}
      var removeData = {}
      removeData['address'] = item.address
      console.log('FBY 删除群组本地数据： ' + JSON.stringify(removeData))
      EspBleMesh.removeGroup(JSON.stringify(removeData))
      item.models.forEach(function (modelsItem) {
        sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_MODEL_SUBSCRIPTION_DELETE
        sendMeshData[Constant.KEY_DST_ADDRESS] = modelsItem.nodeAddress
        sendMeshData[Constant.KEY_ELEMENT_ADDRESS] = modelsItem.elementAddress
        sendMeshData[Constant.KEY_GROUP_ADDRESS] = item.address
        Util.sendMeshMessage(self, sendMeshData)
      })
      self.homedeviceGroup.splice(index, 1)
      self.$store.commit('setDeviceGroupList', self.homedeviceGroup)
    },
    sideslip (type, item) {
      if (type === 'start') {
        this.isSideslip = false
      } else if (type === 'move') {
        this.isSideslip = true
      } else if (type === 'end') {
        if (!this.isSideslip) {
          // 单击跳转
          // this.requestItem(item)
        }
      }
    },
    loadTop () {
      console.log('下拉刷新')
      var self = this
      self.pullLoad = true
      setTimeout(function() {
        if (!self.loadShow) {
          // self.$refs.load.hide();
          self.loadShow = true
          EspBleMesh.getMeshNetwork()
        } else {
          self.pullLoad = false
          self.$refs.loadmore.onTopLoaded()
        }
      }, 50)
      setTimeout(function () {
        self.loadShow = false
        self.pullLoad = false
        self.$refs.loadmore.onTopLoaded()
      }, 3000)
    },
    onBackPressed () {
      var self = this
      Toast({
        message: self.$t('exitProgramDesc'),
        position: 'bottom',
        duration: 2000
      })
      if (self.startTime === 0) {
        self.startTime = new Date().getTime()
      } else {
        if (new Date().getTime() - self.startTime < 2000) {
          EspBleMesh.finish()
        } else {
          self.startTime = new Date().getTime()
        }
      }
    },
    handleTopChange (status) {
      this.topStatus = status
      var arr = document.getElementsByClassName('mint-loadmore-content')
      console.log('FBY arr' + JSON.stringify(arr))
      if (this.pullLoad) {
        arr[0].classList.add('pullLoad')
        this.topStatus = "loading"
        this.$refs.loadmore.topStatus = "loading"
      } else {
        arr[0].classList.remove('pullLoad')
      }

    },
    getSkillsAllList (select) {
      var self = this
      if (select === 0) {
        console.log('getSkillsAllList: ' + JSON.stringify(self.homedeviceGroup))
        return self.homedeviceGroup
      } else {
        return self.skillsGroupOne
      }
    },
    showBlueFail () {
      console.log('点击蓝牙图标')
    },
    scanCallback (res) {
      var self = this
      Util.scanCallback(res, self)
    },
    connectCallback (res) {
      var self = this
      Util.connectCallback(res, self, true)
    },
    showUl () {
      var self = this
      // self.$store.commit('setIsNavFooterShow', false)
      console.log('添加群组')
      const html = '<p id="p"><input class="new-group-name" id="r" value="'+self.groupDefaultName+'" placeholder="'+self.$t('groupname')+'"></p>'
      MessageBox({
        $type: 'confirm',
        title: self.$t('newgroup'),
        inputPlaceholder: self.$t('groupdetail'),
        message: html,
        showCancelButton: true,
        showInput: true
      }).then(({value, action}) => {
        console.log('名称：' + document.getElementById('r').value + '描述：' + value + 'action' + action);
        var createGroupData = {'name': document.getElementById('r').value, 'description': value}
        console.log('FBY skills createGroupData: ' + JSON.stringify(createGroupData))
        EspBleMesh.createGroup(JSON.stringify(createGroupData))
        document.getElementById('r').value = '群组'
      }).catch(err => {
        // console.log('取消')
        document.getElementById('r').value = '群组'
      })
      setTimeout(function () {
        document.getElementById('r').addEventListener('blur', self.animateWidthBlur, false)
        document.getElementById('r').addEventListener('focus', self.animateWidthFocus, false)
      }, 1000)
      // document.getElementById('r').addEventListener('blur', animateWidthBlur, true)
      // document.getElementById('r').blur(function () {
      //   // self.animateWidthBlur()
      //   console.log('blur')
      //   self.$store.commit('setIsNavFooterShow', true)
      // })
      // document.getElementById('r').focus(function () {
      //   // self.animateWidthFocus()
      //   console.log('focus')
      //   self.$store.commit('setIsNavFooterShow', false)
      // })
    },
    animateWidthBlur () {
      var self = this
      console.log('blur')
      self.$store.commit('setIsNavFooterShow', true)
    },
    animateWidthFocus () {
      var self = this
      console.log('focus')
      self.$store.commit('setIsNavFooterShow', false)
    },
    showColor (item) {
      var self = this
      console.log('cell 点击')
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        self.$store.commit('setGroupInfo', item)
        self.$router.togo({
          name: 'groupsOperation',
          query: item
        })
      } else {
        Util.messageRemind(self.$t('devicebleconnecting'))
      }
    },
    isLight (tid) {
      return Util.isLight(tid)
    },
    homeDeviceScan () {
      Util.homeDeviceScan(this)
    },
    scanTimeout () {
      var self = this
      var isRemind = Util.scanTimeout(this)
      if (isRemind) {
        Util.messageRemind(self.$t('unconnectable'))
      }
    },
    close (device, status) {
      var self = this
      var sendMeshData = {}
      sendMeshData[Constant.KEY_OP_CODE] = Constant.GENERIC_ON_OFF_SET_UNACKNOWLEDGED
      sendMeshData[Constant.KEY_DST_ADDRESS] = device.address
      sendMeshData[Constant.KEY_STATE] = status
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        Util.sendMeshMessage(self, sendMeshData)
      }else {
        Util.messageRemind(self.$t('trybleconnect'))
        self.homeDeviceScan()
      }
      console.log('FBY homedeviceGroup 1111' + JSON.stringify(self.homedeviceGroup))
      self.homedeviceGroup.forEach(function (item, i) {
        if (item.address === device.address) {
          console.log('FBY address' + item.address)
          item['isOnOff'] = status
          self.homedeviceGroup.splice(i, 1, item)
          self.$store.commit('setDeviceGroupList', self.homedeviceGroup)
        }
      })
      console.log('FBY homedeviceGroup 1111' + JSON.stringify(self.homedeviceGroup))
      self.homedeviceGroup.forEach(function (item) {
          item.models.forEach(function (modelsItem) {
            Util.updateNodesStatus(self, modelsItem.nodeAdress, status, 'status')
          })
      })
    },
    getGroups () {
      console.log('groups 页面获取群组信息')
      EspBleMesh.getGroups()
    },
    createGroupCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY connectCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        if (!Util._isEmpty(res.address)) {
          self.$store.commit('setGroupInfo', res)
          if (self.$store.state.isBleConnect) {
            self.$router.togo({
              name: 'addDevices',
              query: res
            })
          } else {
            Util.messageRemind(self.$t('trybleconnect'))
            self.homeDeviceScan()
          }
        }
      }
    },
    // 蓝牙连接成功发送数据
    sendBLEMeshData () {
      var self = this
      Util.sendBLEMeshData(self)
    },
    sendMeshMessage (sendMeshData) {
      var self = this
      Util.sendMeshMessage(self, sendMeshData)
    },
    //获取所有节点
    getMeshNetworkCallback (res) {
      var self = this
      Util.getMeshNetworkCallback(res, self)
    },
    // 处理群组数据
    getMeshGroupData (groups, nodes) {
      console.log('FBY skills getMeshGroupData:' + JSON.stringify(groups))
      var self = this
      groups = Util.setMeshGroupsData(groups, nodes, self)
      if (Util._isEmpty(groups)) {
        self.homedeviceGroup = self.$store.state.deviceGroupList
      } else  {
        self.homedeviceGroup = groups
      }
      console.log('FBY skills homedeviceGroup:' + JSON.stringify(self.homedeviceGroup))
      // if (self.homedeviceGroup.length < 1) {
        // self.homedeviceGroup = [
        //   {'name': '客厅灯', 'address': 10, 'description': '客厅顶灯 + 落地灯', 'models': [{'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'},{'elementAddress': 3, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]},
        //   {'name': '客厅灯', 'address': 10, 'description': '客厅顶灯 + 落地灯', 'models': [{'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]}]
      // }
    },
    //设备状态变化回调
    meshMessageCallback (res) {
      var self = this
      Util.meshMessageCallback(res, self, 'skills')
    },
    configModelSubscriptionCallback (res) {
      console('FBY groups configModelSubscriptionCallback: ' + JSON.stringify(res))
    },
    getFastProvNodeAddrCallback (fastNodes) {
      var self = this
      self.fastNodesList = Util.fastNodesStatus(self, fastNodes)
    }
  },
  mounted() {
  },
  activated() {
    var self = this
    window['scanCallback'] = (res) => {
      self.scanCallback(res)
    }
    window['connectCallback'] = (res) => {
      self.connectCallback(res)
    }
    window['getMeshNetworkCallback'] = (res) => {
      self.getMeshNetworkCallback(res)
    }
    window['meshMessageCallback'] = (res) => {
      self.meshMessageCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
    self.connectContent = 0
    self.startTime = 0
    self.isBleConnect = self.$store.state.isBleConnect
    setTimeout(function () {
      self.homedeviceGroup = self.$store.state.deviceGroupList
      for (let item of self.homedeviceGroup) {
        for (let modelItem of item.models) {
          if (modelItem.status) {
            Util.updateGroupsStatus(self, item.address,  true, 'isOnOff')
          }
        }
      }
    }, 3000)
    // TODO
    //  , 'User Scene'
    self.selectList = [self.$t('groupscontrol')]
    self.skillsGroupOne = [
      {'name': '客厅灯', 'address': 10, 'description': '客厅顶灯 + 落地灯', 'models': [{'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'},{'elementAddress': 3, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]},
      {'name': '客厅灯', 'address': 10, 'description': '客厅顶灯 + 落地灯', 'models': [{'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]},
      {'name': '客厅灯', 'address': 10, 'description': '客厅顶灯 + 落地灯', 'models': [{'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'},{'elementAddress': 3, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]},
      {'name': '客厅灯', 'address': 10, 'description': '客厅顶灯 + 落地灯', 'models': [{'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]}]
    // EspBleMesh.getMeshNetwork()
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
      console.log('状态实时更新 blueEnable:' + this.blueEnable + '  locationGranted:' + this.locationGranted + '  locationEnabled:' + this.locationEnabled)
      return this.blueEnable
    }
  }
}
</script>

<style scoped lang="less">
  /*@import '../../assets/css/index.less';*/
  @import '../../assets/css/general.css';
</style>
