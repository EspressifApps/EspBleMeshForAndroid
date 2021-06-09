
<template>
  <div class="page">
    <keep-alive>
      <router-view></router-view>
    </keep-alive>
    <div class="nav-footer" v-show="getIsNavFooterShow">
      <footer>
        <router-link v-for='(item, index) in navList' :to='item.path' :key='index'
                     v-bind:class="{'active':activeRoute == item.path}">
          <p class="footer-content" v-text="item.name"></p>
          <div :class="'footer-type-'+index"></div>
<!--          <p class="'footer-type-'+index"></p>-->
        </router-link>
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      navList: [
        {
          'path': '/home',
          'name': this.$t('nav.home')
        },
        {
          'path': '/groups',
          'name': this.$t('nav.skills')
        }
        // ,
        // {
        //   'path': '/settings',
        //   'name': this.$t('nav.settings')
        // }
      ]
    }
  },
  computed: {
    activeRoute () {
      console.log('activeRoute: ' + this.$route.path)
      return this.$route.path
    },
    getIsNavFooterShow () {
      return this.$store.state.isNavFooterShow
    }
  },
  methods: {

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less" >
@import '../assets/css/syle.less';
@import '../assets/css/index.less';
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: @color1;
  display: flex;
}
.flex {

}
.nav-footer {
  position: fixed;
  left: 0px;
  bottom: 0px;
  width: 100%;
  height: 49px;
  background: @tab-general-bg-color;
  display: block;
  footer {
    font-size: 0;
    border-top: 1px solid @text-shadow-color;
    border-bottom: 1px solid #F8F8F8;
    /*padding-top: 10px;*/
    line-height: 1.2;
    width: 100%;
    height: 49px;
    display: flex;
    a {
      text-decoration: none;
      color: @base-font-color;
      -webkit-box-flex: 1;
      -moz-box-flex: 1;
      -webkit-flex: 1;
      -ms-flex: 1;
      flex: 1;
      text-align: center;
      box-sizing: border-box;
      display: block;
      font-size: 22px;
      .footer-type-0, .footer-type-1, .footer-type-2 {
        display: inline-block;
        margin-bottom: 10px;
        height: 2px;
        width: 80px;
        background-size: cover;
        background-color: @tab-general-bg-color;
      }
      &:hover {
        text-decoration: none;
      }
      &.router-link-active.active {
        .footer-type-0 {
          background-color: white;
        }
        .footer-type-1 {
          background-color: white;
        }
        .footer-type-2 {
          background-color: white;
        }
      }
    }
    .footer-content {
      color: white;
      height: 25px;
      text-align: center;
      font-size: 18px;
      padding-top: 12px;
    }
  }
}
</style>
