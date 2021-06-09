<template>
  <div class="index-content-box">
    <div class="home-header">
      <img class="esp-logo" src="../../assets/EspressifLogo.png" @click="testClick()">
      <p class="home-network">Espressif Network</p>
      <div v-show="!getBlueEnable"  @click="showBlueFail" class="bluetooth-base">
        <i class="icon-bluetooth bluetooth-style"></i>
      </div>
      <i @click.stop="showUl()" class="icon-plus right-more"></i>
      <ul v-show="flag" class="add-ul">
<!--        <i class="icon-light"></i>-->
        <li @click.stop="homeMenu('1')"><span>{{$t('sigprovision')}}</span></li>
        <li @click.stop="homeMenu('2')"><span>{{$t('fastprovision')}}</span></li>
<!--        <li @click.stop="homeMenu('3')"><span>{{$t('newnetwork')}}</span></li>-->
      </ul>
    </div>
    <div class="select-menu-base">
      <div class="select-menu">
        <mt-navbar v-model="selected">
          <mt-tab-item v-for="(item, i) in selectList" :key="i" :id="i">{{item}}</mt-tab-item>
        </mt-navbar>
      </div>
      <i @click.stop="switchUI" class="icon-more home-switch-btn flex flex-ac flex-jcc"></i>
    </div>
    <div v-show="!isSwitchUI" class="content-info scan-device flex-wrapper search-device">
      <div class="overflow-touch">
        <mt-loadmore :top-method="loadTop" @top-status-change="handleTopChange" ref="loadmore">
          <div v-for="(item, i) in getDataList" :key="i" @click.self="operateItem(item)" class="item push-none">
            <div @click.stop="operateItem(item)" class="item-icon-circle" :class="getBxColor(item)">
              <i :class="getIcon(item.tid)" :style="{'color': getColor(item)}"></i>
              <!--            <span><i class="icon-link"></i></span>-->
            </div>
            <div @click.stop="operateItem(item)" class="item-name">
              <span>{{item.name}}</span>
              <span class="desc">{{$t('nodeAddresss')}}{{item.address}}&nbsp;&nbsp;&nbsp;&nbsp;{{$t('nodeVersion')}}{{item.version}}</span>
            </div>
            <div class="item-power">
              <div v-show="isLight(item.tid) && isBleConnect" class="switch-wrapper">
                <span :class="{'active': !item.status}" @click="close(item, false)">OFF</span>
                <span :class="{'active': item.status}" @click="close(item, true)">ON</span>
              </div>
            </div>
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
    <div v-show="isSwitchUI" class="home-switch-base">
      <div class="switch-base">
        <div @click.self="switchItem(item)" v-for="item in getHomeAllList(selected)" :key="item.address" class="switch-item">
          <div @click.stop="switchItem(item)" class="switch-item-content">
            <i class="icon-light switch-icon-light" :class="{'switch': item.status}"></i>
            <span :class="{'node-name': selected > 1}">{{item.name}}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapMutations, mapGetters, mapState} from 'vuex'
import Util from '../../assets/tool/utils'
import Constant from "../../assets/tool/constant";
import {Indicator, MessageBox, Toast} from "mint-ui";
export default {
  data () {
    return {
      homeDeviceList: this.$store.state.homeDeviceList,
      fastNodesList: this.$store.state.fastNodesList,
      selected: this.$store.state.homeMenu,
      blueEnable: true,
      locationGranted: true,
      locationEnabled: true,
      flag: false,
      onoff: false,
      isSwitchUI: false,
      isSwitchOnOff: false,
      selectList: this.$store.state.homeSelectList,
      proxyAddress: '',
      connectContent: 0,
      connectData: '',
      isBleConnect: false,
      homedeviceGroup: this.$store.state.deviceGroupList,
      pullLoad: false,
      loadShow: false,
      topStatus: '',
      startTime: 0,
      isGetDetailData: false,
      groupsList: [],
      groupsDetail: {},
      homeNewDevice:[]
    }
  },
  beforeDestroy () {
    console.log('即将离开 Home 页面')
  },
  watch: {
    '$route':'getPath'

  },
  created () {
    var self = this
    window['getMeshNetworkCallback'] = (res) => {
      self.getMeshNetworkCallback(res)
    }
    window['scanCallback'] = (res) => {
      self.scanCallback(res)
    }
    window['connectCallback'] = (res) => {
      self.connectCallback(res)
    }
    window['meshMessageCallback'] = (res) => {
      self.meshMessageCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
    window['getMeshInfoCallback'] = (res) => {
      self.getMeshInfoCallback(res)
    }
    window['getGroupsCallback'] = (res) => {
      self.getGroupsCallback(res)
    }
  },
  methods: {
    ...mapMutations({
      setNum: 'SET_NUM'
    }),
    getGroupsCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY getGroupsCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {
        self.groupsList = res
        if (self.selectList.length > 2) {
          self.selectList.splice(2, self.selectList.length - 2)
        }
        // res.forEach(function (item) {
        //   self.selectList.push(item.name)
        // })
        self.$store.commit('setHomeSelectList', self.selectList)
      }
    },
    getMeshInfoCallback (res) {
      var self = this
      res = Util.base64().decode(res)
      console.log('FBY getMeshInfoCallback' + res)
      res = JSON.parse(res)
      if (!Util._isEmpty(res)) {homedeviceGroup
        if (res.type === 'node' && self.selected === 0) {
          self.homeDeviceList = res.nodes
          self.$store.commit('setHomeDeviceList', self.homeDeviceList)
        } else if (res.type === 'fastNode' && self.selected === 1) {
          self.fastNodesList = res.fastNodes
        } else if (res.type === 'group' && self.groupsList[self.selected].address === res.address) {
          self.groupsDetail[self.selected] = res
        }
      }
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        self.sendBLEMeshData()
      } else {
        self.homeDeviceScan(self)
      }
      console.log('getMeshInfoCallback getHomeAllList 1')
      self.getHomeAllList(self.selected)
      console.log('getMeshInfoCallback getHomeAllList 2')
    },
    testClick () {
      // 新版本 OTA
      // this.$router.togo({
      //   name: 'newNodeUpgrade'
      // })
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
    loadTop () {
      console.log('下拉刷新')
      var self = this
      var meshInfo = {}
      self.isGetDetailData = true
      self.pullLoad = true
      setTimeout(function() {
        if (!self.loadShow) {
          // self.$refs.load.hide();
          self.loadShow = true
          if (self.selected === 0) {
            meshInfo['type'] = 'node'
          } else if (self.selected === 1) {
            meshInfo['type'] = 'fastNode'
          } else {
            meshInfo['type'] = 'group'
            meshInfo['address'] = self.groupsList[self.selected].address
          }
          // EspBleMesh.getMeshInfo(JSON.stringify(meshInfo))
          EspBleMesh.getMeshNetwork()
        } else {
          self.pullLoad = false
          self.$refs.loadmore.onTopLoaded()
        }
      }, 50)
      setTimeout(function () {
        self.loadShow = false
        self.pullLoad = false
        self.isGetDetailData = false
        self.$refs.loadmore.onTopLoaded()
      }, 5000)
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
    getPath(){
      var self = this
      console.log('路由发生变化' + this.$route.path);
      if (this.$route.path === '/home') {
        // EspBleMesh.getMeshNetwork()
      } else {
        EspBleMesh.stopScan()
      }
    },
    scanCallback (res) {
      var self = this
      Util.scanCallback(res, self)
    },
    connectCallback (res) {
      var self = this
      Util.connectCallback(res, self, self.isGetDetailData)
    },
    showBlueFail () {
      console.log('点击蓝牙图标')
    },
    showUl () {
      this.flag = !this.flag
    },
    getHomeAllList (selected) {
      var self = this
      self.$store.commit('setHomeMenu', selected)
      console.log('Home 菜单栏选择：' + selected)
      if (selected === 0) {
        console.log('FBY Home homeDeviceList: ' + JSON.stringify(self.homeDeviceList))
        return self.homeDeviceList
      } else if (selected === 1) {
        // console.log('FBY Home fastNodesList: ' + JSON.stringify(self.fastNodesList))
        return self.fastNodesList
      } else {
        console.log('FBY Home homedeviceGroup selected: ' + selected)
        console.log('FBY Home homedeviceGroup address: ' + JSON.stringify(self.homedeviceGroup))
        // console.log('FBY Home homedeviceGroup: ' + JSON.stringify(self.homedeviceGroup[selected - 2].models))
        if (!Util._isEmpty(self.homedeviceGroup[selected - 2])) {
          return self.homedeviceGroup[selected - 2].models
        } else {
          return []
        }
        // return self.groupsDetail[selected]
      }
    },
    homeMenu (item) {
      var self = this
      console.log('点击配网' + item)
      self.flag = false
      if (item === '1') {
        EspBleMesh.stopScan()
        self.isBleConnect = self.$store.state.isBleConnect
        if (self.isBleConnect) {
          Util.proxyDisconnect(self)
        }
        self.$router.togo({
          name: 'sigprovision',
          query: {
            'type': item
          }
        })
      } else if (item === '2') {
        self.isBleConnect = self.$store.state.isBleConnect
        if (self.isBleConnect) {
          EspBleMesh.stopScan()
          self.$router.togo({
            name: 'firstprovision',
            query: {
              'type': item
            }
          })
        } else {
          Util.messageRemind(self.$t('fastremind'))
        }
      }else {
        console.log('新建网络')
      }
    },
    operateItem (item, e) {
      console.log('点击 cell 控制设备' + JSON.stringify(item))
      var self = this
      self.flag = false
      if (self.pullLoad) {
        return
      }
      // TODO
      // self.$store.commit('setDeviceInfo', item)
      // // if (self.selected < 2) {
      // //   isNode = false
      // // }
      // self.$router.togo({
      //   name: 'operation',
      //   query: {
      //     'item': item,
      //     'isNode': true,
      //     'groupAddress': ''
      //   }
      // })
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        self.pushNextPage(item)
      } else {
        // Util.messageRemind(self.$t('devicebleconnecting'))
        EspBleMesh.stopScan()
        Util.messageRemind(self.$t('bleConnecting'))
        self.$store.commit('setBleWillConnectAddress', item.address)
        self.homeDeviceScan()
      }
    },
    pushNextPage (item) {
      var self = this
      var isNode = true
      var groupAddress = ''
      EspBleMesh.stopScan()
      self.$store.commit('setDeviceInfo', item)
      if (self.selected > 1) {
        groupAddress = self.homedeviceGroup[self.selected - 2].address
        isNode = false
      }
      self.$router.togo({
        name: 'operation',
        query: {
          'item': item,
          'isNode': isNode,
          'groupAddress': groupAddress,
          'menuSelect': self.selected
        }
      })
    },
    switchUI () {
      console.log('点击切换 UI')
      this.isSwitchUI = !this.isSwitchUI
    },
    getIcon: function (tid) {
      return Util.getIconUtil(tid)
    },
    getBxColor (item) {
      var self = this
      if (item.address === self.$store.state.bleConnectAddress) {
        return 'bx-red'
      } else {
        return ''
      }
    },
    getColor: function (item) {
      return Util.getColorUtil(item)
    },
    isLight (tid) {
      return Util.isLight(tid)
    },
    switchItem (item) {
      var self = this
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        self.close(item, !item.status)
      } else {
        Util.messageRemind(self.$t('devicebleconnecting'))
      }
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
      var nodeAdress = ''
      console.log('FBY close device: ' + JSON.stringify(device) + ' status: ' + status)
      sendMeshData[Constant.KEY_OP_CODE] = Constant.GENERIC_ON_OFF_SET_UNACKNOWLEDGED
      if (self.selected > 1) {
        sendMeshData[Constant.KEY_DST_ADDRESS] = device.elementAddress
        sendMeshData[Constant.KEY_NODE_ADDRESS] = device.nodeAddress
      } else {
        sendMeshData[Constant.KEY_DST_ADDRESS] = device.address
        sendMeshData[Constant.KEY_NODE_ADDRESS] = device.address
      }
      sendMeshData[Constant.KEY_STATE] = status
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        Util.sendMeshMessage(self, sendMeshData)
      }else {
        Util.messageRemind(self.$t('trybleconnect'))
        self.homeDeviceScan()
      }
      if (self.selected > 1) {
        nodeAdress = device.nodeAddress
      } else {
        nodeAdress = device.address
      }
      Util.updateNodesStatus(self, nodeAdress, status, 'status')
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
      var self = this
      console.log('getMeshGroupData : ' + JSON.stringify(self.selectList))
      if (self.selectList.length > 2) {
        self.selectList.splice(2, self.selectList.length - 2)
      }
      console.log('FBY Home getMeshGroupData:' + JSON.stringify(groups) + '     selectList: ' + JSON.stringify(self.selectList))
      groups = Util.setMeshGroupsData(groups, nodes, self)
      self.homedeviceGroup = groups
      self.homedeviceGroup.forEach(function (item) {
        self.selectList.push(item.name)
      })
      console.log('FBY Home getMeshGroupData selectList: ' + JSON.stringify(self.selectList))
      self.$store.commit('setHomeSelectList', self.selectList)
      console.log('FBY Home getMeshGroupData newGroups:' + JSON.stringify(groups))
    },
    //设备状态变化回调
    meshMessageCallback (res) {
      var self = this
      Util.meshMessageCallback(res, self, 'home')
    },
    getFastProvNodeAddrCallback (fastNodes) {
      var self = this
      self.fastNodesList = Util.fastNodesStatus(self, fastNodes)
      console.log('FBY getFastProvNodeAddrCallback fastNodesList: ' + JSON.stringify(self.fastNodesList))
    }
  },
  mounted () {
    console.log('mounted 走了')
  },
  activated() {
    var self = this
    console.log('activated 走了: ' + JSON.stringify(self.selectList))
    if (self.selectList.length > 1) {
      self.selectList.splice(0, 1, self.$t('networknodes'))
      self.selectList.splice(1, 1, self.$t('fastnodes'))
    } else {
      self.selectList.push(self.$t('networknodes'))
      self.selectList.push(self.$t('fastnodes'))
    }
    window['getMeshNetworkCallback'] = (res) => {
      self.getMeshNetworkCallback(res)
    }
    window['scanCallback'] = (res) => {
      self.scanCallback(res)
    }
    window['connectCallback'] = (res) => {
      self.connectCallback(res)
    }
    window['meshMessageCallback'] = (res) => {
      self.meshMessageCallback(res)
    }
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
    self.isBleConnect = self.$store.state.isBleConnect
    self.fastNodesList = self.$store.state.fastNodesList
    self.connectContent = 0
    self.startTime = 0
    // self.homeDeviceList = [
    //   {'name': 'light-1', 'address': '000000001', 'version': '1.0.1', 'tid': '1', 'status':false, 'characteristics': [[{'cid': '1'}]]},
    //   {'name': 'light-2', 'address': '00002', 'version': '1.0.1', 'tid': '1', 'status':false, 'characteristics': [{'cid': '1'}]},
    //   {'name': 'light-3', 'address': '03', 'version': '1.0.1', 'tid': '1', 'status':false, 'characteristics': [{'cid': '1'}]}]
    // self.homeGroupOne = [
    //   {'name': 'name-1', 'mac': '000000001', 'group': 'group1', 'tid': '1', 'characteristics': [[{'cid': '1'}]]},
    //   {'name': 'name-2', 'mac': '000000002', 'group': 'group2', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'name-3', 'mac': '000000003', 'group': 'group3', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'name-4', 'mac': '000000004', 'group': 'group4', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'name-5', 'mac': '000000005', 'group': 'group5', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'name-6', 'mac': '000000006', 'group': 'group6', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'name-7', 'mac': '000000007', 'group': 'group7', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'name-8', 'mac': '000000008', 'group': 'group8', 'tid': '1', 'characteristics': [{'cid': '0'}]},
    //   {'name': 'name-9', 'mac': '000000009', 'group': 'group9', 'tid': '1', 'characteristics': [{'cid': '0'}]}]
    // self.homeGroupTwo = [
    //   {'name': 'group-1', 'mac': '000000001', 'group': 'group1', 'tid': '1', 'characteristics': [[{'cid': '1'}]]},
    //   {'name': 'group-2', 'mac': '000000002', 'group': 'group2', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'group-3', 'mac': '000000003', 'group': 'group3', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'group-4', 'mac': '000000004', 'group': 'group4', 'tid': '1', 'characteristics': [{'cid': '1'}]},
    //   {'name': 'group-5', 'mac': '000000005', 'group': 'group5', 'tid': '1', 'characteristics': [{'cid': '1'}]}]
    console.log('capabilities 返回传参打印' + JSON.stringify(this.$route.query))
    if (self.$route.query.type === Constant.CAPABILITIES) {
      if (!Util._isEmpty(this.$route.query.node)) {
        self.homeDeviceList.push(this.$route.query.node)
        self.$store.commit('setHomeDeviceList', self.homeDeviceList)
        console.log('homeDeviceList 打印' + JSON.stringify(self.homeDeviceList))
      }
    }
    // EspBleMesh.getGroups()
  },
  deactivated() {
    console.log('deactivated 走了')
    EspBleMesh.stopScan()
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
    },
    getDataList () {
      this.homeDeviceList = this.$store.state.homeDeviceList
      // this.homeDeviceList = JSON.parse('[{\"name\":\"My Node\",\"uuid\":\"ffff30ae-a480-0a6e-0000-000000000000\",\"meshUuid\":\"DEFC8EC0-3D47-4B59-BC5A-DE8B65220F53\",\"address\":2,\"elements\":[{\"name\":\"Element: 0x0002\",\"address\":2,\"models\":[{\"id\":0,\"name\":\"Configuration Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":1,\"name\":\"Configuration Client\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":2,\"name\":\"Health Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4096,\"name\":\"Generic On Off Server\",\"type\":\"sig\",\"applicationKeys\":[0],\"groups\":[49154]},{\"id\":4100,\"name\":\"Generic Default Transition Timer Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4102,\"name\":\"Generic Power On Off Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4103,\"name\":\"Generic Power On Off Setup Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4098,\"name\":\"Generic Level Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4864,\"name\":\"Light Lightness Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4865,\"name\":\"Light Lightness Setup Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4871,\"name\":\"Light HSL Server\",\"type\":\"sig\",\"applicationKeys\":[0],\"groups\":[]},{\"id\":4872,\"name\":\"Light HSL Setup Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":4867,\"name\":\"Light Ctl Server\",\"type\":\"sig\",\"applicationKeys\":[0],\"groups\":[]},{\"id\":4868,\"name\":\"Light Ctl Setup Server\",\"type\":\"sig\",\"applicationKeys\":[],\"groups\":[]},{\"id\":48562176,\"name\":\"Vendor Model\",\"type\":\"vendor\",\"applicationKeys\":[],\"groups\":[]},{\"id\":48562177,\"name\":\"Vendor Model\",\"type\":\"vendor\",\"applicationKeys\":[],\"groups\":[]},{\"id\":48562179,\"name\":\"Vendor Model\",\"type\":\"vendor\",\"applicationKeys\":[],\"groups\":[]},{\"id\":48562178,\"name\":\"Vendor Model\",\"type\":\"vendor\",\"applicationKeys\":[],\"groups\":[]}]}],\"tid\":\"1\",\"color\":[0,1,1],\"status\":false}]')
      console.log('computed: ' + JSON.stringify(this.homeDeviceList))
      return this.getHomeAllList(this.selected)
    }
  }
}
</script>

<style lang="less">
  @import '../../assets/css/index.less';
  .home-switch-btn {
    width: 45px;
    height: 45px;
    color: @tab-general-bg-color;
    font-size: 22px;
  }
  .home-switch-base {
    position: relative;
    height: calc(100% - 195px) !important;
    width: 100%;
    background-color: white;
  }
  .switch-base {
    display: flex;
    /*flex-flow: row wrap;*/
    flex-wrap: wrap;
    max-height: 100%;
    overflow: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
  }
  .switch-item {
    height: 25vw;
    flex: 1;
    max-width: 25%;
    min-width: 25%;
    padding: 10px;
    box-sizing: border-box;
  }
  .switch-item-content {
    height: 100%;
    /*background-color: #4caf50;*/
    border: 1px solid @tab-general-bg-color;
    border-radius: 5px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  .switch-item-content .switch {
    color: @tab-general-bg-color;
  }
  .switch-icon-light {
    padding: 10%;
    font-size: 30px;
    color: @Secondary-font-color;
  }
  .switch-item-content .node-name {
    font-size: 8px;
    width: 80%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
</style>
