import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    blueInfo: true,
    locationGranted: true,
    locationEnabled: true,
    systemInfo: '',
    scanProvisioning: [],
    scanProxy: [],
    homeDeviceList: [],
    sigProvisionList: [],
    deviceGroupList: [],
    homeSelectList: [],
    isBleConnect: false,
    isNavFooterShow: true,
    groupInfo: {},
    deviceInfo: {},
    bleConnectAddress: '',
    fastNodesList: [],
    homeMenu: 0,
    otawifiName: '',
    sendDataQueueArr: [],
    currentHue: 360,
    currentSaturation: 100,
    currentLuminance: 100,
    currentTemperature: 50,
    currentBrightness: 70,
    bleWillConnectAddress: '',
    bleScanAllDevice: [],
    isOperation: 0
  },
  mutations: {
    setIsOperation (state, info) {
      state.isOperation = info
    },
    setBleScanAllDevice (state, info) {
      state.bleScanAllDevice = info
    },
    setBleWillConnectAddress (state, info) {
      state.bleWillConnectAddress = info
    },
    setCurrentHue (state, info) {
      state.currentHue = info
    },
    setCurrentSaturation (state, info) {
      state.currentSaturation = info
    },
    setCurrentLuminance (state, info) {
      state.currentLuminance = info
    },
    setCurrentTemperature (state, info) {
      state.currentTemperature = info
    },
    setCurrentBrightness (state, info) {
      state.currentBrightness = info
    },
    setHomeSelectList (state, info) {
      state.homeSelectList = info
    },
    setSendDataQueueArr (state, info) {
      state.sendDataQueueArr = info
    },
    setotawifiName (state, info) {
      state.otawifiName = info
    },
    setHomeMenu (state, info) {
      state.homeMenu = info
    },
    setFastNodesList (state, info) {
      state.fastNodesList = info
    },
    setBleConnectAddress (state, info) {
      state.bleConnectAddress = info
    },
    setDeviceInfo (state, info) {
      state.deviceInfo = info
    },
    setGroupInfo (state, info) {
      state.groupInfo = info
    },
    setIsNavFooterShow (state, info) {
      state.isNavFooterShow = info
    },
    setDeviceGroupList (state, info) {
      state.deviceGroupList = info
    },
    setIsBleConnect (state, info) {
      state.isBleConnect = info
    },
    setSigProvisionList (state, info) {
      state.sigProvisionList = info
    },
    setHomeDeviceList (state, info) {
      state.homeDeviceList = info
    },
    setScanProvisioning (state, info) {
      state.scanProvisioning = info
    },
    setScanProxy (state, info) {
      state.scanProxy = info
    },
    setBlueInfo (state, info) {
      state.blueInfo = info
    },
    setLocationGranted (state, info) {
      state.locationGranted = info
    },
    setLocationEnabled (state, info) {
      state.locationEnabled = info
    },
    setSystemInfo (state, info) {
      state.systemInfo = info
    }
  }
})
