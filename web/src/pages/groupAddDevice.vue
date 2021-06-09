<template>
  <div class="content-box">
    <div class="title-info">
      <h4 class="app-title"><span @click="hide"><i class="icon-left back"></i></span>{{$t('adddevice')}}</h4>
      <span @click="addGroupDevice()" class="right-top-text">{{$t('submit')}}</span>
    </div>
<!--  群组-->
    <div class="groups-header header">
      <span class="header-title flex flex-ac flex-jcc">{{$t('groups')}}</span>
<!--      <div @click="selectAllGroup()" class="radio-info flex flex-ac flex-jcc">-->
<!--        <span class="span-radio"-->
<!--        :class="{'active': (groupSelected === groupCount && groupCount !== 0)}">-->
<!--          <span></span></span>-->
<!--      </div>-->
    </div>
    <div class="content-info flex-wrapper group-base">
      <div class="overflow-touch">
        <div v-for="(item, index) in groupList" :key="index" @click.self="switchGroup(index)" class="item">
          <div @click.stop="switchGroup(index)" class="item-icon-circle">
            <i class="icon-groups"></i>
            <span>{{item.models.length}}</span>
          </div>
          <div @click.stop="switchGroup(index)" class="item-name">
            <span>{{item.name}}</span>
            <span class="desc">{{getGroupDes(item.description)}}{{item.address.toString(16)}}</span>
          </div>
          <div @click.stop="switchGroup(index)" class="item-power-small">
            <span :data-value="item.address" class="span-radio" :class="{'active': isGroupSelected(item.address)}"><span></span></span>
          </div>
        </div>
      </div>
    </div>
<!--  所有节点-->
    <div class="groups-header header">
      <span class="header-title flex flex-ac flex-jcc">{{$t('networknodes')}}</span>
<!--      <div @click="selectAllNode()" class="radio-info flex flex-ac flex-jcc">-->
<!--        <span class="span-radio"-->
<!--              :class="{'active': (nodeSelected === nodeCount && nodeCount !== 0)}">-->
<!--          <span></span></span>-->
<!--      </div>-->
    </div>
    <div class="content-info flex-wrapper node-base">
      <div class="overflow-touch">
        <div v-for="(item, index) in deviceList" :key="index" class="">
          <div @click="switchNodes(item.address, index)" class="node-first-base item">
            <div class="item-icon-circle">
              <i :class="getIcon(item.tid)"></i>
            </div>
            <div class="item-name">
              <span>{{item.name}}</span>
<!--              <span class="desc">{{item.address}}</span>-->
            </div>
            <div class="item-power-small">
<!--              || (elementSelected === elementCount[item.address] && elementCount[item.address] !== 0)-->
              <span :data-value="item.address" class="span-radio" :class="[isNodeSelected(item.address) ? 'active': '', isNodeDisable(item.address) ? 'disable': '']"><span></span></span>
            </div>
          </div>
          <div class="node-second-base item" v-for="(itemSub, indexSub) in item.elements" :key="indexSub" @click="switchElements(item.address, itemSub.address, item.address)">
            <div class="item-name elements">
              <span>{{itemSub.name}}</span>
<!--              <span class="desc">{{itemSub.address}}</span>-->
            </div>
            <div class="item-power-small">
              <span :data-value="item.address" class="span-radio" :class="[isElementSelected(itemSub.address, item.address) ? 'active': '', isNodeDisable(item.address) ? 'disable': '']"><span></span></span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Util from "../assets/tool/utils"
import {mapGetters, mapState} from "vuex";
import Constant from "../assets/tool/constant"
import {Indicator} from 'mint-ui'
export default {
  data () {
    return {
      blueEnable: true,
      locationGranted: true,
      locationEnabled: true,
      groupList: [],
      deviceList: [],
      groupInfo: this.$store.state.groupInfo,
      isSelectedGroupsAddress: [],
      isAllGroupsSelect: false,
      groupCount: 0,
      groupSelected: 0,
      isSelectedNodesAddress: [],
      isAllNodesSelect: false,
      nodeCount: 0,
      nodeSelected: 0,
      isSelectedElementsAddress: {},
      selectedElementsAddress: {},
      isAllElementsSelect: {},
      elementCount: {},
      elementSelected: 0,
      elementSelectedArr: [],
      connectData: '',
      connectContent: 0,
      isBleConnect: false,
      groupAddNodeTimeOut: '',
      currentGroupArr: []
    }
  },
  created () {
    var self = this
    window['onBackPressed'] = () => {
      self.onBackPressed()
    }
  },
  methods: {
    hide () {
      this.$router.goBack()
    },
    getGroupDes (des) {
      if (!Util._isEmpty(des)) {
        return des + '  '
      } else {
        return ''
      }
    },
    backHome () {
      var self = this
      self.$router.goRight({
        name: 'groupsOperation'
      })
    },
    getIcon: function (tid) {
      return Util.getIconUtil(tid)
    },
    onBackPressed () {
      this.$router.goBack()
    },
    addGroupDevice () {
      var self = this
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        self.sendBLEMeshData()
      } else {
        Util.homeDeviceScan(self)
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
      var self = this
      var sendMeshData = {}
      var elementsArr = []
      var newGroupElements = self.groupInfo.models
      var newElements = []
      Indicator.open()
      self.groupAddNodeTimeOut = setTimeout(function () {
        Indicator.close()
        Util.messageRemind(self.$t('groupaddaodetimeout'))
      }, 10000)
      // 获取当前群组中节点信息
      console.log('addGroupDevice newGroupElements: ' + JSON.stringify(newGroupElements) + ' groupInfo: ' + JSON.stringify(self.groupInfo))
      if (!Util._isEmpty(newGroupElements)) {
        newGroupElements.forEach(function (item) {
          newElements.push(item.elementAddress)
        })
      }
      // 添加节点到群组
      self.addNodeToGroup(0, newElements)
      // console.log('addGroupDevice isSelectedElementsAddress: ' + JSON.stringify(self.isSelectedElementsAddress) + ' newElements: ' + JSON.stringify(newElements))
      // self.deviceList.forEach(function (item) {
      //   elementsArr = self.isSelectedElementsAddress[item.address]
      //   if (!Util._isEmpty(elementsArr) && elementsArr.length > 0) {
      //     elementsArr.forEach(function (elementItem) {
      //       if (newElements.indexOf(elementItem) === -1) {
      //         sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_MODEL_SUBSCRIPTION_ADD
      //         sendMeshData[Constant.KEY_DST_ADDRESS] = item.address
      //         sendMeshData[Constant.KEY_GROUP_ADDRESS] = self.groupInfo.address
      //         sendMeshData[Constant.KEY_ELEMENT_ADDRESS] = elementItem
      //         console.log('addGroupDevice node sendMeshData:' + JSON.stringify(sendMeshData))
      //         Util.sendMeshMessage(sendMeshData)
      //       }
      //     })
      //   }
      // })

      // 添加群组到新群组
      self.addGroupToGroup(0, newElements)
      // console.log('addGroupDevice groupList: ' + JSON.stringify(self.groupList) + ' newElements: ' + JSON.stringify(newElements))
      // self.groupList.forEach(function (item) {
      //   if (self.isSelectedGroupsAddress.indexOf(item.address) !== -1) {
      //     sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_MODEL_SUBSCRIPTION_ADD
      //     sendMeshData[Constant.KEY_GROUP_ADDRESS] = self.groupInfo.address
      //     console.log('addGroupDevice item:' + JSON.stringify(item))
      //     item.models.forEach(function (modelItem) {
      //       sendMeshData[Constant.KEY_DST_ADDRESS] = modelItem.nodeAddress
      //       if (newElements.indexOf(modelItem.elementAddress) === -1) {
      //         sendMeshData[Constant.KEY_ELEMENT_ADDRESS] = modelItem.elementAddress
      //         console.log('addGroupDevice group sendMeshData:' + JSON.stringify(sendMeshData))
      //         Util.sendMeshMessage(sendMeshData)
      //       }
      //     })
      //   }
      // })

    },
    // 延时添加节点到群组
    addNodeToGroup (index, newElements) {
      console.log('index: ' + index + ' newElements: ' + newElements)
      var self = this
      var elementsArr = []
      if (self.deviceList.length > 0) {
        try {
          if (index < self.deviceList.length) {
            var item = self.deviceList[index]
            if (item.address) {
              elementsArr = self.isSelectedElementsAddress[item.address]
              console.log('addNodeToGroup elementsArr1222111: ' + JSON.stringify(elementsArr))
              if (!Util._isEmpty(elementsArr)) {
                if (elementsArr.length > 0) {
                  console.log('addNodeToGroup isSelectedElementsAddress: ' + JSON.stringify(self.isSelectedElementsAddress))
                  console.log('addNodeToGroup elementsArr: ' + JSON.stringify(elementsArr))
                  var subIndex = 0
                  self.addElementToGroup(elementsArr, subIndex, index, newElements)
                } else {
                  self.addNodeToGroup(index + 1, newElements)
                }
              } else {
                self.addNodeToGroup(index + 1, newElements)
              }
            } else {
              self.addNodeToGroup(index + 1, newElements)
            }
          }
        } catch(err) {
          console.error('addNodeToGroup err: ' + err)
          self.addNodeToGroup(index + 1, newElements)
        }
      }
    },
    addElementToGroup (elementsArr, subIndex, index, newElements) {
      var self = this
      console.log('addElementToGroup: ' + JSON.stringify(elementsArr))
      self.sendNodeData(elementsArr, subIndex, index, newElements).then((val) => {
        if ( subIndex < val < elementsArr.length) {
          setTimeout(() => {
            self.addElementToGroup(elementsArr, val, index, newElements)
          }, 500)
        } else if (val === elementsArr.length && index < self.deviceList.length) {
          setTimeout(() => {
            self.addNodeToGroup(index + 1, newElements)
          }, 500)
        }
      })
    },
    sendNodeData (elementsArr, subIndex, index, newElements) {
      console.log('sendNodeData: ' + JSON.stringify(newElements) + 'elementsArr: ' + JSON.stringify(elementsArr) + 'subIndex: ' + subIndex)
      var self = this
      return new Promise((resolve, reject) => {
        var elementItem = elementsArr[subIndex]
        var sendMeshData = {}
        if (newElements.indexOf(elementItem) === -1) {
          sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_MODEL_SUBSCRIPTION_ADD
          sendMeshData[Constant.KEY_DST_ADDRESS] = self.deviceList[index].address
          sendMeshData[Constant.KEY_GROUP_ADDRESS] = self.groupInfo.address
          sendMeshData[Constant.KEY_ELEMENT_ADDRESS] = elementItem
          console.log('addGroupDevice node sendMeshData:' + JSON.stringify(sendMeshData))
          Util.sendMeshMessage(self, sendMeshData)
        }
        subIndex++
        resolve(subIndex)
      })
    },
    // 延时添加群组到群组
    addGroupToGroup (index, newElements) {
      var self = this
      var groupModels = []
      console.log('addGroupToGroup groupList: ' + JSON.stringify(self.groupList))
      if (self.groupList.length > 0) {
        if (index < self.groupList.length) {
          var item = self.groupList[index]
          if (self.isSelectedGroupsAddress.indexOf(item.address) !== -1) {
            groupModels = item.models
            console.log('addGroupToGroup: ' + JSON.stringify(groupModels))
            if (!Util._isEmpty(groupModels)) {
              if (groupModels.length > 0) {
                var subIndex = 0
                self.addModelToGroup(groupModels, subIndex, index, newElements)
              } else {
                self.addGroupToGroup(index + 1, newElements)
              }
            } else {
              self.addGroupToGroup(index + 1, newElements)
            }
          } else {
            self.addGroupToGroup(index + 1, newElements)
          }
        }
      }
    },
    addModelToGroup (groupModels, subIndex, index, newElements) {
      var self = this
      self.sendGroupData(groupModels, subIndex, newElements).then((val) => {
        console.log('addModelToGroup: ' + JSON.stringify(groupModels))
        if ( subIndex < val < groupModels.length) {
          setTimeout(() => {
            self.addModelToGroup(groupModels, val, index, newElements)
          }, 500)
        } else if (val === groupModels.length && index < self.groupList.length) {
          setTimeout(() => {
            self.addGroupToGroup(index + 1, newElements)
          }, 500)
        }
      })
    },
    sendGroupData (groupModels, subIndex, newElements) {
      var self = this
      return new Promise((resolve, reject) => {
        var modelItem = groupModels[subIndex]
        var sendMeshData = {}
        sendMeshData[Constant.KEY_OP_CODE] = Constant.CONFIG_MODEL_SUBSCRIPTION_ADD
        sendMeshData[Constant.KEY_GROUP_ADDRESS] = self.groupInfo.address
        sendMeshData[Constant.KEY_DST_ADDRESS] = modelItem.nodeAddress
        if (newElements.indexOf(modelItem.elementAddress) === -1) {
          sendMeshData[Constant.KEY_ELEMENT_ADDRESS] = modelItem.elementAddress
          console.log('addGroupDevice group sendMeshData:' + JSON.stringify(sendMeshData))
          Util.sendMeshMessage(self, sendMeshData)
        }
        subIndex++
        resolve(subIndex)
      })
    },
    // 群组
    switchGroup (index) {
      var self = this
      var address = self.groupList[index].address
      console.log('选择群组' + address)
      var num = this.isSelectedGroupsAddress.indexOf(address)
      if (num === -1) {
        this.isSelectedGroupsAddress.push(address)
      } else {
        this.isSelectedGroupsAddress.splice(num, 1)
      }
      this.groupSelected = this.isSelectedGroupsAddress.length
      if (self.groupSelected === self.groupCount) {
        self.isAllGroupsSelect = true
      }
    },
    isGroupSelected (address) {
      // console.log('选择群组1' + address)
      var self = this
      var flag = false
      if (self.isSelectedGroupsAddress.indexOf(address) !== -1) {
        flag = true
      }
      return flag
    },
    selectAllGroup () {
      console.log('2')
      var self = this
      if (self.isAllGroupsSelect) {
        self.groupSelected = 0
        self.isSelectedGroupsAddress = []
        self.isAllGroupsSelect = false
      } else {
        self.groupSelected = self.groupCount
        var allAddress = []
        self.groupList.forEach(function(item) {
          allAddress.push(item.address)
        })
        self.isSelectedGroupsAddress = allAddress
        self.isAllGroupsSelect = true
      }
    },
    // 节点
    switchNodes (address, index) {
      var self = this
      console.log('选择节点：' + address + ' model: ' + JSON.stringify(self.groupInfo.models))
      if (!Util._isEmpty(self.groupInfo.models)) {
        for (let item of self.groupInfo.models) {
          if (item.nodeAddress === address) {
            console.log('选择节点：' + address + 'item： ' + JSON.stringify(item))
            return
          }
        }
      }
      var num = this.isSelectedNodesAddress.indexOf(address)
      if (num === -1) {
        this.isSelectedNodesAddress.push(address)
      } else {
        this.isSelectedNodesAddress.splice(num, 1)
      }
      self.isNodeAllSelect()
      // 处理 Elements
      if (self.isAllElementsSelect[address]) {
        self.elementSelected = 0
        self.isSelectedElementsAddress[address] = []
        self.isAllElementsSelect[address] = false
      } else {
        self.elementSelected = self.elementCount[address]
        var allAddress = self.isSelectedElementsAddress
        var allElementAddress = []
        self.deviceList[index].elements.forEach(function(item) {
          allElementAddress.push(item.address)
        })
        allAddress[address] = allElementAddress
        self.isSelectedElementsAddress = allAddress
        self.isAllElementsSelect[address] = true
      }
    },
    nodeAdd (address) {
      var self = this
      var num = self.isSelectedNodesAddress.indexOf(address)
      if (num === -1) {
        this.isSelectedNodesAddress.push(address)
      }
      self.isNodeAllSelect()
    },
    nodedelete (address) {
      var self = this
      var num = self.isSelectedNodesAddress.indexOf(address)
      if (num !== -1) {
        this.isSelectedNodesAddress.splice(num, 1)
      }
      self.isNodeAllSelect(address)
    },
    // 根据 elements 判断是否全选
    isElementToNodeSelect (address) {
      var self = this
      self.elementSelected = self.isSelectedElementsAddress[address].length
      if (self.elementSelected === self.elementCount[address]) {
        self.isAllNodesSelect = true
      }
    },
    // 判断 node 是否全选
    isNodeAllSelect () {
      var self = this
      this.nodeSelected = this.isSelectedNodesAddress.length
      if (self.nodeSelected === self.nodeCount) {
        self.isAllNodesSelect = true
      }
    },
    isNodeSelected (address) {
      var self = this
      var flag = false
      console.log('111选择节点: ' + address + ' self.isSelectedNodesAddress: ' + self.isSelectedNodesAddress)
      if (self.isSelectedNodesAddress.indexOf(address) !== -1) {
        flag = true
      }
      return flag
    },
    isNodeDisable (address) {
      var self = this
      var flag = false
      if (!Util._isEmpty(self.groupInfo.models)) {
        self.groupInfo.models.forEach(function (item) {
          if (item.nodeAddress === address) {
            flag = true
            if (self.currentGroupArr.indexOf(address) !== -1) {
              self.currentGroupArr.push(address)
            }
          }
        })
      }
      return flag
    },
    selectAllNode () {
      console.log('1')
      var self = this
      if (self.isAllNodesSelect) {
        self.nodeSelected = 0
        self.isSelectedNodesAddress = []
        self.isAllNodesSelect = false
      } else {
        self.nodeSelected = self.nodeCount
        var allAddress = []
        self.deviceList.forEach(function(item) {
          if (self.currentGroupArr.indexOf(item.address) !== -1) {
            allAddress.push(item.address)
          }
        })
        self.isSelectedNodesAddress = allAddress
        self.isAllNodesSelect = true
      }
      var elementSelectedBool = true
      self.deviceList.forEach(function (item) {
        elementSelectedBool = self.isAllElementsSelect[item.address] && elementSelectedBool
      })
      self.deviceList.forEach(function (item, i) {
        // 处理 Elements
        if (elementSelectedBool) {
          self.elementSelected = 0
          self.isSelectedElementsAddress[item.address] = []
          self.isAllElementsSelect[item.address] = false
        } else {
          self.elementSelected = self.elementCount[item.address]
          var allAddress = self.isSelectedElementsAddress
          var allElementAddress = []
          self.deviceList[i].elements.forEach(function(elementItem) {
            allElementAddress.push(elementItem.address)
          })
          allAddress[item.address] = allElementAddress
          self.isSelectedElementsAddress = allAddress
          self.isAllElementsSelect[item.address] = true
        }
      })
    },
    // Elements
    switchElements (address, elementsAddress, nodeAddress) {
      var self = this
      if (!Util._isEmpty(self.groupInfo.models)) {
        for (let item of self.groupInfo.models) {
          if (item.nodeAddress === address) {
            console.log('选择节点：' + address + 'item： ' + JSON.stringify(item))
            return
          }
        }
      }
      self.selectedElementsAddress = self.isSelectedElementsAddress
      console.log(' nodeAddress: ' + nodeAddress + ' 选择 Elements: ' + elementsAddress + ' self.isSelectedElementsAddress: ' + JSON.stringify(self.isSelectedElementsAddress))
      if (Util._isEmpty(self.selectedElementsAddress[nodeAddress])) {
        self.selectedElementsAddress[nodeAddress] = []
      }
      var nodeNum = self.selectedElementsAddress[nodeAddress].length
      self.elementSelectedArr = self.selectedElementsAddress[nodeAddress]
      if (nodeNum > 0) {
        var num = self.elementSelectedArr.indexOf(elementsAddress)
        if (num === -1) {
          self.elementSelectedArr.push(elementsAddress)
        } else {
          self.elementSelectedArr.splice(num, 1)
        }
      } else {
        console.log('12345')
        self.elementSelectedArr.push(elementsAddress)
      }
      self.selectedElementsAddress[nodeAddress] = self.elementSelectedArr
      self.isSelectedElementsAddress = {}
      self.isSelectedElementsAddress = self.selectedElementsAddress
      console.log('更新 isSelectedElementsAddress: ' + JSON.stringify(self.isSelectedElementsAddress))
      self.elementSelected = self.elementSelectedArr.length
      if (self.elementSelected === self.elementCount[nodeAddress]) {
        self.isAllElementsSelect[nodeAddress] = true
        self.nodeAdd(nodeAddress)
      } else {
        self.isAllElementsSelect[nodeAddress] = false
        self.nodedelete(nodeAddress)
      }
    },
    isElementSelected (elementsAddress, nodeAddress) {
      var self = this
      var flag = false
      var elementArr = []
      console.log(' nodeAddress: ' + nodeAddress + ' 11111选择 Elements1: ' + elementsAddress + JSON.stringify(self.isSelectedElementsAddress))
      if (JSON.stringify(self.isSelectedElementsAddress) !== '{}') {
        elementArr = self.isSelectedElementsAddress[nodeAddress]
        console.log('elementArr 长度：' + elementArr)
        if (!Util._isEmpty(elementArr)) {
          if (elementArr.length > 0) {
            if (elementArr.indexOf(elementsAddress) !== -1) {
              flag = true
            }
          }
        }
      }
      return flag
    },
    //设备状态变化回调
    meshMessageCallback (res) {
      var self = this
      Util.meshMessageCallback(res, self)
    },
    configModelSubscriptionCallback (res) {
      var self = this
      clearTimeout(self.groupAddNodeTimeOut)
      Util.messageRemind(this.$t('deviceaddgroupsuc'))
      Indicator.close()
      setTimeout(function () {
        // self.backHome()
        self.hide()
      }, 500)
    },
    scanCallback (res) {
      var self = this
      Util.scanCallback(res, self)
    },
    connectCallback (res) {
      var self = this
      Util.connectCallback(res, self, true)
    },
  },
  mounted () {
    var self = this
    var groupAllList = []
    window['scanCallback'] = (res) => {
      self.scanCallback(res)
    }
    window['connectCallback'] = (res) => {
      self.connectCallback(res)
    }
    window['meshMessageCallback'] = (res) => {
      self.meshMessageCallback(res)
    }
    self.connectContent = 0
    groupAllList = self.$store.state.deviceGroupList
    console.log('FBY groupAddDevice groupList123: ' + JSON.stringify(groupAllList))
    if (groupAllList.length < 1) {
      // self.groupList = [
      //   {'name': '客厅灯', 'address': 10, 'description': '客厅顶灯 + 落地灯', 'models': [{'nodeAddress': 2, 'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'},{'nodeAddress': 3, 'elementAddress': 3, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]},
      //   {'name': '客厅灯', 'address': 11, 'description': '客厅顶灯 + 落地灯', 'models': [{'nodeAddress': 2, 'elementAddress': 2, 'modelId': 4096, 'name': 'My Node -- Generic On Off Server'}]}]
    } else {
      console.log('FBY groupAddDevice groupAllList: ' + JSON.stringify(groupAllList))
      console.log('FBY groupAddDevice groupList: ' + JSON.stringify(self.groupList))
      let list = groupAllList.filter(item => item.address === self.groupInfo.address)
      self.groupList = list
      console.log('FBY groupAddDevice groupList: ' + JSON.stringify(self.groupList))
    }
    self.groupCount = self.groupList.length
    self.deviceList = self.$store.state.homeDeviceList
    // console.log('addDevice deviceList1:' + JSON.stringify(self.deviceList))
    if (self.deviceList.length < 1) {
      // self.deviceList = [
      //   {'name': 'light-1', 'uuid': '000000001', 'tid': '1', 'status': false, 'address': 2,
      //     'elements': [{'name': 'Element: 0x0002', 'address': 2}, {'name': 'Element: 0x0003', 'address': 3},
      //       {'name': 'Element: 0x0004', 'address': 4}, {'name': 'Element: 0x0005', 'address': 5}]},
      //   {'name': 'light-2', 'uuid': '000000002', 'tid': '1', 'status': false, 'address': 3,
      //     'elements': [{'name': 'Element: 0x0003', 'address': 3}, {'name': 'Element: 0x0004', 'address': 4},
      //       {'name': 'Element: 0x0005', 'address': 5}, {'name': 'Element: 0x0006', 'address': 6}]}]
      // self.deviceList = [
      //   {'name': 'light-1', 'uuid': '000000001', 'tid': '1', 'status': false, 'address': 2,
      //     'elements': [{'name': 'Element: 0x0002', 'address': 2}, {'name': 'Element: 0x0003', 'address': 3}]},
      //   {'name': 'light-2', 'uuid': '000000002', 'tid': '1', 'status': false, 'address': 3,
      //     'elements': [{'name': 'Element: 0x0003', 'address': 3}, {'name': 'Element: 0x0004', 'address': 4}]}]
    }
    // console.log('addDevice deviceList2:' + JSON.stringify(self.deviceList))
    // console.log('groups 传参打印' + JSON.stringify(this.$route.query))
    self.nodeCount = self.deviceList.length
    self.deviceList.forEach(function (item) {
      self.elementCount[item.address] = item.elements.length
    })
  },
  activated () {
    console.log('groupAddDevice activated 走了')
  },
  computed: {
    blueEnable () {
      console.log('addDevice 页面蓝牙发生变化' + this.$store.state.blueInfo)
      return this.$store.state.blueInfo
    },
    getGroupInfo () {
      return this.$store.state.groupInfo
    },
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
  @import '../assets/css/index.less';
  .group-base {
    position: relative;
    top: 50px;
    height: calc(45% - 140px) !important;
  }
  .groups-header {
    position: relative;
    top: 50px;
    height: 45px;
    display: flex;
    justify-content: space-between;
  }
  .header-title {
    margin-left: 15px;
    font-weight: bold;
  }
  .node-base {
    position: relative;
    top: 50px;
    height: 55% !important;
  }
  .node-second-base .elements {
    margin-left: 50px;
    min-width: calc(100% - 160px) !important;
  }
</style>
