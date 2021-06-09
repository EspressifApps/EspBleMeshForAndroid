<template>
  <div class="index-content-box">
    <div class="home-header">
      <img class="esp-logo" src="../../assets/EspressifLogo.png">
      <p class="home-network">Espressif Network</p>
    </div>
    <div class="device-detail-base">
      <div class="device-detail">
        <!--        网络名称-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('networkname')}}</span>
          <span class="content">Espressif Network</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
      <span class="network-set flex flex-ae">{{$t('networksettings')}}</span>
      <div class="device-detail">
        <!--        供应商-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('provisioners')}}</span>
          <span class="content">1</span>
          <i class="icon-right text-color"></i>
        </div>
        <div class="item-strings"></div>
        <!--        网络密钥-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('networkkeys')}}</span>
          <span class="content">2</span>
          <i class="icon-right text-color"></i>
        </div>
        <div class="item-strings"></div>
        <!--        App 密钥-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('applicationkeys')}}</span>
          <span class="content">0</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
      <div class="device-detail reset-Netwotk">
        <!--        重置 Mesh 网络-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('resetmeshneteork')}}</span>
          <span class="content forget-network">{{$t('forgetnetwork')}}</span>
        </div>
      </div>
      <span class="network-set flex flex-ae">{{$t('about')}}</span>
      <div class="device-detail">
        <!--        App 版本号-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('applicationversion')}}</span>
          <span class="content">1.0.0</span>
        </div>
        <div class="item-strings"></div>
        <!--        源代码-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('sourcecode')}}</span>
          <span class="content">GitHub</span>
          <i class="icon-right text-color"></i>
        </div>
        <div class="item-strings"></div>
        <!--        问题报告-->
        <div @click="deviceName" class="item">
          <span class="name">{{$t('reportanissue')}}</span>
          <span class="content">Github/Issues</span>
          <i class="icon-right text-color"></i>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import {mapMutations, mapGetters, mapState} from 'vuex'
import {Indicator, Toast} from "mint-ui";
export default {
  data () {
    return {
      tittle: 'Home',
      startTime: 0
    }
  },
  created () {},
  methods: {
    ...mapMutations({
      setNum: 'SET_NUM'
    }),
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
    deviceName () {
      console.log('选项点击')
    }
  },
  activated() {
    var self = this
    self.startTime = 0
  },
  computed: {
    ...mapGetters([
      'number'
    ]),
    ...mapState({
      number: state => state.home.number
    })
  }
}
</script>

<style scoped lang="less">
@import '../../assets/css/index.less';
.device-detail-base {
  top: 100px;
  min-height: calc(100% - 149px);
  max-height: calc(100% - 149px);
  overflow: hidden;
  overflow-y: auto;
}
/*设置列表样式*/
.device-detail {
  width: 100%;
  /*height: calc(100% - 49px);*/
  overflow: hidden;
  box-sizing: border-box;
  border-bottom: 1px solid @page-strings-coloe;
  border-top: 1px solid @page-strings-coloe;
  background-color: white;
}
.network-set {
  height: 50px;
  padding-left: 20px;
  color: @page-text-coloe;
}
.device-detail .item {
  display: flex;
  height: 45px;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px 0 20px;
}
.item-strings {
  height: 1px;
  border-bottom: 1px solid @page-strings-coloe;
  margin-left: 20px;
}
.device-detail .name {
  flex: 1;
}
.device-detail .content {
  color: @page-text-coloe;
  margin-right: 10px;
}
.content.forget-network {
  color: @tab-general-bg-color;
}
.text-color {
  color: @page-text-coloe;
  font-size: 12px;
}
.device-detail.reset-Netwotk {
  margin-top: 30px;
}
</style>
