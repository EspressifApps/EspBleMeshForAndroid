<template>
<!--  <mt-button @click="todetail">home</mt-button>-->
  <div class="index-content-box">
    <div class="select-menu-base">
      <div class="select-menu">
        <mt-navbar v-model="selected">
          <mt-tab-item v-for="(item, i) in selectList" :key="item" :id="i">{{item}}</mt-tab-item>
        </mt-navbar>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'text',
  data () {
    return {
      selected: '',
      selectList: []
    }
  },
  methods: {
    todetail () {
      this.$router.goBack()
    }
  },
  mounted () {
    var self = this
    self.selectList = ['选项一', '选项二', '选项三', '选项四', '选项五', '选项六']
  }
}
</script>

<style scoped lang="less">
  @import '../assets/css/index.less';
</style>
