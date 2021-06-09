import Constant from './constant'
import Raphael from 'raphael'
import {Toast} from "mint-ui";
var sendDataQueue = []
function initColorWheel () {
  Raphael.colorwheel = function (x, y, size, initcolor, element, flag) {
    console.log(y)
    return new ColorWheel(x, y, size, initcolor, flag, element)
  }
  function angle (x, y) {
    return (x < 0) * 180 + Math.atan(-y / -x) * 180 / pi
  }
  var pi = Math.PI
  const ColorWheel = function (x, y, size, initcolor, flag, element) {
    size = size || 200
    var w3 = 3 * size / 200
    var w1 = size / 200
    var fi = 1.6180339887
    var segments = pi * size / 5
    var size20 = size / 20
    var size2 = size / 2
    var padding = 2 * size / 200
    var t = this
    var s = size - (size20 * 4)
    var r = element ? Raphael(element, size, size) : Raphael(x, y, size, size)
    var xy = s / 6 + size20 * 2 + padding
    var wh = s * 2 / 3 - padding * 2
    w1 < 1 && (w1 = 1)
    w3 < 1 && (w3 = 1)
    var a = pi / 2 - pi * 2 / segments * 1.3
    var R = size2 - padding
    var R2 = size2 - padding - size20 * 2
    var path = ['M', size2, padding, 'A', R, R, 0, 0, 1, R * Math.cos(a) + R + padding, R - R * Math.sin(a) + padding, 'L', R2 * Math.cos(a) + R + padding, (R - R2 * Math.sin(a) + padding) * 1.6, 'A', R2, R2, 0, 0, 0, size2, (padding + size20 * 2) * 1.6, 'z'].join()
    if (flag) {
      for (var i = 0; i < segments; i++) {
        r.path(path).attr({
          stroke: 'none',
          fill: 'hsb(' + i * (255 / segments) / 255 + ', 1, 1)',
          transform: 'r' + [(360 / segments) * i, size2, size2]
        })
      }
    } else {
      var r0 = 0
      var g0 = 0
      var b0 = 0
      var r1 = 248
      var g1 = 207
      var b1 = 109
      var r2 = 255
      var g2 = 255
      var b2 = 255
      var r3 = 164
      var g3 = 213
      var b3 = 255
      var num = segments / 4
      var num2 = num * 2
      var num3 = num * 3
      var percentage = 0
      for (let i = 0; i < segments; i++) {
        var hsb = ''
        if (i < num) {
          percentage = i / num
          r0 = Math.floor((r2 - r1) * percentage) + r1
          g0 = Math.floor((g2 - g1) * percentage) + g1
          b0 = Math.floor((b2 - b1) * percentage) + b1
        } else if (i < num2) {
          percentage = (i - num) / num
          r0 = r2 - Math.floor((r2 - r3) * percentage)
          g0 = g2 - Math.floor((g2 - g3) * percentage)
          b0 = b2 - Math.floor((b2 - b3) * percentage)
        } else if (i < num3) {
          percentage = (i - num2) / num
          r0 = Math.floor((r2 - r3) * percentage) + r3
          g0 = Math.floor((g2 - g3) * percentage) + g3
          b0 = Math.floor((b2 - b3) * percentage) + b3
        } else {
          percentage = (i - num3) / num
          r0 = r2 - Math.floor((r2 - r1) * percentage)
          g0 = g2 - Math.floor((g2 - g1) * percentage)
          b0 = b2 - Math.floor((b2 - b1) * percentage)
        }

        hsb = Raphael.rgb2hsb(r0, g0, b0)
        r.path(path).attr({
          stroke: 'none',
          fill: 'hsb(' + hsb.h + ',' + hsb.s + ',' + hsb.b + ')',
          transform: 'r' + [(360 / segments) * i, size2, size2]
        })
      }
    }
    r.path(['M', size2, padding, 'A', R, R, 0, 1, 1, size2 - 1, padding, 'l1,0', 'M', size2, padding + size20 * 2, 'A', R2, R2, 0, 1, 1, size2 - 1, padding + size20 * 2, 'l1,0']).attr({
      'stroke-width': w3,
      stroke: ''
    })
    t.cursorhsb = r.set()
    var h = size20 * 2 + 2
    t.cursorhsb.push(r.rect(size2 - h / fi / 2, padding - 1, h / fi, h * 1.6, 3 * size / 200 + 4.1).attr({
      stroke: '#000',
      opacity: 0.3,
      'stroke-width': w3
    }))
    t.cursorhsb.push(r.rect(size2 - h / fi / 2, padding - 1, h / fi, h * 1.6, 3 * size / 200 + 4.1).attr({
      stroke: '#fff',
      opacity: 0.9,
      'stroke-width': w1
    }))
    t.ring = r.path(['M', size2, padding, 'A', R, R, 0, 1, 1, size2 - 1, padding, 'l1,0M', size2, padding + size20 * 2, 'A', R2, R2, 0, 1, 1, size2 - 1, padding + size20 * 2, 'l1,0']).attr({
      fill: '#000',
      opacity: 0,
      stroke: 'none'
    })

    // rect drawing
    // t.main = r.rect(padding + h / fi / 2, size + padding * 2 + 80, size - padding * 2 - h / fi, h - padding * 2 - 4, 3 * size / 200 + 4.1).attr({
    //     stroke: '#fff',
    //     fill: '180-#fff-#000'
    // })
    t.main = r.rect(padding + h / fi / 2, size + padding * 2 + 80, size - padding * 2 - h / fi, 0, 3 * size / 200 + 4.1).attr({
      stroke: '#fff',
      fill: '180-#fff-#000'
    })

    t.cursor = r.set()
    // t.cursor.push(r.rect(size - padding - h / fi, size + padding + 80, ~~(h / fi), h-4, w3 + 4.1).attr({
    //     stroke: '#000',
    //     opacity: .5,
    //     'stroke-width': w3
    // }))
    // t.cursor.push(r.rect(size - padding - h / fi, size + padding + 80, ~~(h / fi), h-4, w3 + 4.1).attr({
    //     stroke: '#fff',
    //     opacity: 1,
    //     'stroke-width': w1
    // }))
    t.btop = t.main.clone().attr({
      stroke: '#000',
      fill: '#000',
      opacity: 0
    })
    t.bwidth = ~~(h / fi) / 2
    t.minx = padding + t.bwidth
    t.maxx = size - h / fi - padding + t.bwidth

    t.H = t.S = t.B = 1
    t.raphael = r
    t.size2 = size2
    t.size20 = size20
    t.padding = padding
    t.wh = wh
    t.x = x
    t.xy = xy
    t.y = y

    // events
    t.ring.drag(function (dx, dy, x, y) {
      t.hsbOnTheMove = true
      t.docOnMove(dx, dy, x, y, segments, flag)
    }, function (x, y) {
      t.hsbOnTheMove = true
      if (flag) {
        t.setHS(x - t.x, y - t.y)
      } else {
        t.setHSTH(x - t.x, y - t.y, segments, flag)
      }
    }, function () {
      t.hsbOnTheMove = false
    })
    t.btop.drag(function (dx, dy, x, y) {
      t.docOnMove(dx, dy, x, y)
    }, function (x, y) {
      t.clrOnTheMove = true
      // t.setB(x - t.x)
    }, function () {
      t.clrOnTheMove = false
    })
    if (flag) {
      t.setColor(initcolor || '#fff')
    } else {
      t.setTHColor(initcolor || [0, 0])
    }
    this.onchanged && this.onchanged(this.color())
  }
  var proto = ColorWheel.prototype

  proto.setH = function (x, y) {
    var d = Raphael.angle(x, y, 0, 0)
    // var rd = Raphael.rad(d)
    this.cursorhsb.attr({transform: 'r' + [d + 90, this.size2, this.size2]})
    this.H = (d + 90) / 360

    this.main.attr({fill: 'hsb(' + this.H + ',1,1)'})
    this.onchange && this.onchange(this.color())
  }
  proto.setHS = function (x, y) {
    var X = x - this.size2
    var Y = y - this.size2
    // var R = this.size2 - this.size20 / 2 - this.padding
    var d = angle(X, Y)
    // var rd = Raphael.rad(d)
    isNaN(d) && (d = 0)
    this.cursorhsb.attr({transform: 'r' + [d + 90, this.size2, this.size2]})
    this.H = (d + 90) / 360
    // this.main.attr({fill: '180-hsb(' + [this.H, 1] + ',1)-#000'})
    this.color('hsb(' + this.H + ', 1,' + this.B + ')')
    this.onchange && this.onchange(this.color())
  }
  proto.setHSTH = function (x, y, segments) {
    var X = x - this.size2
    var Y = y - this.size2
    var d = angle(X, Y)
    // var rd = d * pi / 180
    isNaN(d) && (d = 0)
    this.cursorhsb.attr({transform: 'r' + [d + 90, this.size2, this.size2]})
    // var hsb = this.getGradient(d + 90, segments)
    // this.color('hsb(' + hsb.h + ',' + hsb.s + ', ' + hsb.b + ')')
    // this.main.attr({fill: '180-hsb(' + [hsb.h, hsb.s] + ','+hsb.b+')-#000'})
    this.onchange && this.onchange(this.color())
  }
  proto.setB = function (x) {
    console.log(x)
    x < this.minx && (x = this.minx)
    x > this.maxx && (x = this.maxx)
    this.cursor.attr({x: x - this.bwidth})
    this.B = (x - this.minx) / (this.maxx - this.minx)
    if (this.B <= 0.05) {
      this.B = 0.05
    }
    this.onchange && this.onchange(this.color())
  }
  proto.getHSTH = function () {
    return this.cursorhsb[0]['_'].deg
  }
  proto.getB = function () {
    return this.B
  }
  proto.docOnMove = function (dx, dy, x, y, segments, flag) {
    if (this.hsbOnTheMove) {
      if (flag) {
        this.setHS(x - this.x, y - this.y)
      } else {
        this.setHSTH(x - this.x, y - this.y, segments)
      }
    }
    if (this.clrOnTheMove) {
      // this.setB(x - this.x, y - this.y)
    }
  }
  proto.remove = function () {
    this.raphael.remove()
    this.color = function () {
      return false
    }
  }
  proto.getGradient = function (val, segments) {
    var r0 = 0
    var g0 = 0
    var b0 = 0
    var r1 = 248
    var g1 = 207
    var b1 = 109
    var r2 = 255
    var g2 = 255
    var b2 = 255
    var r3 = 164
    var g3 = 213
    var b3 = 255
    var num = segments / 4
    var num2 = num * 2
    var num3 = num * 3
    var percentage = 0
    // var hsb = ''
    val = val * (segments / 360)
    if (val < num) {
      percentage = val / num
      r0 = Math.floor((r2 - r1) * percentage) + r1
      g0 = Math.floor((g2 - g1) * percentage) + g1
      b0 = Math.floor((b2 - b1) * percentage) + b1
    } else if (val < num2) {
      percentage = (val - num) / num
      r0 = r2 - Math.floor((r2 - r3) * percentage)
      g0 = g2 - Math.floor((g2 - g3) * percentage)
      b0 = b2 - Math.floor((b2 - b3) * percentage)
    } else if (val < num3) {
      percentage = (val - num2) / num
      r0 = Math.floor((r2 - r3) * percentage) + r3
      g0 = Math.floor((g2 - g3) * percentage) + g3
      b0 = Math.floor((b2 - b3) * percentage) + b3
    } else {
      percentage = (val - num3) / num
      r0 = r2 - Math.floor((r2 - r1) * percentage)
      g0 = g2 - Math.floor((g2 - g1) * percentage)
      b0 = b2 - Math.floor((b2 - b1) * percentage)
    }
    return Raphael.rgb2hsb(r0, g0, b0)
  }
  proto.setTHColor = function (color) {
    var d = color[0] * 1.8
    this.B = color[1] / 100

    this.cursorhsb.attr({transform: 'r' + [d, this.size2, this.size2]})
    var x = (this.maxx - this.minx) * this.B + this.minx - this.bwidth
    this.cursor.attr({x: x})
    return this
  }
  proto.setColor = function (color) {
    color = Raphael.color(color)
    color = Raphael.rgb2hsb(color.r, color.g, color.b)
    var d = color.h * 360
    this.H = color.h
    this.S = color.s
    // this.B = color.b

    this.cursorhsb.attr({transform: 'r' + [d, this.size2, this.size2]})
    // this.main.attr({fill: '180-hsb(' + [this.H, this.S] + ',1)-#000'})
    var x = (this.maxx - this.minx) * this.B + this.minx - this.bwidth
    this.cursor.attr({x: x})
    return this
  }
  proto.color = function (color) {
    if (color) {
      color = Raphael.color(color)
      color = Raphael.rgb2hsb(color.r, color.g, color.b)
      // var d = color.h * 360
      this.H = color.h
      this.S = color.s
      // this.B = color.b
      // this.cursorhsb.attr({transform: 'r' + [d, this.size2, this.size2]})
      // this.main.attr({fill: '180-hsb(' + [this.H, this.S] + ',1)-#000'})
      var x = (this.maxx - this.minx) * this.B + this.minx - this.bwidth
      this.cursor.attr({x: x})
      return this
    } else {
      return Raphael.hsb2rgb(this.H, this.S, this.B).hex
    }
  }
  Raphael.changColor = function (color, flag) {
    if (flag) {
      proto.setColor(color)
    } else {
      proto.setTHColor(color)
    }
  }
}
function getIconUtil (tid) {
  if (tid >= Constant.MIN_LIGHT && tid <= Constant.MAX_LIGHT) {
    return 'icon-light'
  } else if (tid >= Constant.MIN_SWITCH && tid <= Constant.MAX_SWITCH) {
    return 'icon-power'
  } else if (tid >= Constant.MIN_SENSOR && tid <= Constant.MAX_SENSOR) {
    return 'icon-sensor'
  } else if (tid >= Constant.CLIENT_LIGHT) {
    if (tid === Constant.TABLE_LAMP_LIGHT) {
      return 'icon-taideng'
    } else if (tid === Constant.FLOOR_LAMP_LIGHT) {
      return 'icon-floor_lamp'
    } else if (tid === Constant.CHANDELIER_LIGHT) {
      return 'icon-diaodeng'
    } else if (tid === Constant.WALL_LAMP_LIGHT) {
      return 'icon-bideng'
    } else {
      return 'icon-light'
    }
  } else {
    return 'icon-light'
  }
}
function _isEmpty (str) {
  if (str === '' || str === null || str === undefined || str === 'null' || str === 'undefined') {
    return true
  } else {
    return false
  }
}
function getColorUtil (nodeItem) {
  var self = this
  var hueValue = 0
  var saturation = 0
  var luminance = 0
  var status = 0
  var rgb = '#6b6b6b'
  status = nodeItem.status
  if (!self._isEmpty(nodeItem.color)) {
    if (nodeItem.color.length === 3) {
      hueValue = nodeItem.color[0]
      saturation = nodeItem.color[1]
      luminance = nodeItem.color[2]
      // console.log('FBY getColorUtil hueValue : ' + hueValue + ' saturation: ' + saturation + ' luminance: ' + luminance)
    }
  }
  if (status) {
    // console.log('FBY getColorUtil 2 : ' + JSON.stringify(nodeItem.color))
    rgb = Raphael.hsl2rgb(hueValue, saturation, luminance)
    // console.log('FBY getColorUtil: ' + rgb)
    // var v = luminance
    // if (v <= 0.4) {
    //   v *= 1.2
    // }
    // if (v <= 0.2) {
    //   v = 0.2
    // }
    // ', ' + v +
    rgb = 'rgba(' + Math.round(rgb.r) + ', ' + Math.round(rgb.g) + ', ' + Math.round(rgb.b) + ')'
  }
  // console.log('FBY getColorUtil rgb: ' + rgb)
  return rgb
}
function addBgClassUtils (e) {
  console.log('addBgClassUtils' + JSON.stringify(e.currentTarget))
  var doc = e.currentTarget.parent().parent().parent()
  doc.addClass('bg-white')
  setTimeout(function () {
    doc.removeClass('bg-white')
  }, 500)
}
function isLight (tid) {
  if ((tid >= Constant.MIN_LIGHT && tid <= Constant.MAX_LIGHT) || tid >= Constant.CLIENT_LIGHT) {
    return true
  } else {
    return false
  }
}
const Base64 = {
  _keyStr: 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=',
  encode: function (e) {
    var t = ''
    var n, r, i, s, o, u, a
    var f = 0
    e = this._utf8_encode(e)
    while (f < e.length) {
      n = e.charCodeAt(f++)
      r = e.charCodeAt(f++)
      i = e.charCodeAt(f++)
      s = n >> 2
      o = (n & 3) << 4 | r >> 4
      u = (r & 15) << 2 | i >> 6
      a = i & 63
      if (isNaN(r)) {
        u = a = 64
      } else if (isNaN(i)) {
        a = 64
      }
      t = t + this._keyStr.charAt(s) + this._keyStr.charAt(o) + this._keyStr.charAt(u) + this._keyStr.charAt(a)
    }
    return t
  },
  decode: function (e) {
    var t = ''
    var n, r, i
    var s, o, u, a
    var f = 0
    e = e.replace(/[^A-Za-z0-9+/=]/g, '')
    while (f < e.length) {
      s = this._keyStr.indexOf(e.charAt(f++))
      o = this._keyStr.indexOf(e.charAt(f++))
      u = this._keyStr.indexOf(e.charAt(f++))
      a = this._keyStr.indexOf(e.charAt(f++))
      n = s << 2 | o >> 4
      r = (o & 15) << 4 | u >> 2
      i = (u & 3) << 6 | a
      t = t + String.fromCharCode(n)
      if (u !== 64) {
        t = t + String.fromCharCode(r)
      }
      if (a !== 64) {
        t = t + String.fromCharCode(i)
      }
    }
    t = this._utf8_decode(t)
    return t
  },
  _utf8_encode: function (e) {
    e = e.replace(/rn/g, 'n')
    var t = ''
    for (var n = 0; n < e.length; n++) {
      var r = e.charCodeAt(n)
      if (r < 128) {
        t += String.fromCharCode(r)
      } else if (r > 127 && r < 2048) {
        t += String.fromCharCode(r >> 6 | 192)
        t += String.fromCharCode(r & 63 | 128)
      } else {
        t += String.fromCharCode(r >> 12 | 224)
        t += String.fromCharCode(r >> 6 & 63 | 128)
        t += String.fromCharCode(r & 63 | 128)
      }
    }
    return t
  },
  _utf8_decode: function (e) {
    var t = ''
    var n = 0
    var r = 0
    var c2 = 0
    var c3
    while (n < e.length) {
      r = e.charCodeAt(n)
      if (r < 128) {
        t += String.fromCharCode(r)
        n++
      } else if (r > 191 && r < 224) {
        c2 = e.charCodeAt(n + 1)
        t += String.fromCharCode((r & 31) << 6 | c2 & 63)
        n += 2
      } else {
        c2 = e.charCodeAt(n + 1)
        c3 = e.charCodeAt(n + 2)
        t += String.fromCharCode((r & 15) << 12 | (c2 & 63) << 6 | c3 & 63)
        n += 3
      }
    }
    return t
  }
}
function stringToBytes (str) {
  var ch
  var st
  var re = []
  for (var i = 0; i < str.length; i++) {
    ch = str.charCodeAt(i)
    st = []
    do {
      st.push(ch & 0xFF)
      ch = ch >> 8
    }
    while (ch)
    re = re.concat(st.reverse())
  }
  return re
}
function base64 () {
  return Base64
}
function getBLERssiIcon (rssi) {
  if (rssi > 0) {
    return ''
  } else if (rssi >= -55) {
    return require('../imgs/signal24.png')
  } else if (rssi >= -65) {
    return require('../imgs/signal23.png')
  } else if (rssi >= -70) {
    return require('../imgs/signal22.png')
  } else if (rssi >= -75) {
    return require('../imgs/signal1.png')
  } else {
    return require('../imgs/signal0.png')
  }
}
function sortBy (attr, rev) {
  if (rev === undefined) {
    rev = 1
  } else {
    rev = (rev) ? 1 : -1
  }
  return function (a, b) {
    a = a[attr]
    b = b[attr]
    if (a < b) {
      return rev * -1
    } else if (a > b) {
      return rev * 1
    }
    return 0
  }
}
function homeDeviceScan (self) {
  console.log('权限状态 blueEnable:' + self.blueEnable + 'locationGranted:' + self.locationGranted + 'locationEnabled:' + self.locationEnabled)
  if (!self.blueEnable) {
    this.messageRemind(self.$t('notbluetooth'))
    return
  }
  if (!self.locationEnabled) {
    this.messageRemind(self.$t('notlocation'))
    return
  }
  if (!self.locationGranted) {
    this.messageRemind(self.$t('notgps'))
    return
  }
  if (self.blueEnable && self.locationEnabled && self.locationGranted) {
    self.$store.commit('setBleScanAllDevice', [])
    console.log('home 开始 proxy 蓝牙扫描')
    var scanData = {'type':'proxy'}
    EspBleMesh.startScan(JSON.stringify(scanData))
  }
  setTimeout(function () {
    self.scanTimeout()
  }, 5000)
}
function scanTimeout (self) {
  var scanAllDevice = self.$store.state.bleScanAllDevice
  if (!this._isEmpty(scanAllDevice) && scanAllDevice.length > 0) {
    console.log('Home scan proxy device: ' + scanAllDevice)
    EspBleMesh.stopScan()
    var willConnectAddress = self.$store.state.bleWillConnectAddress
    if (_isEmpty(willConnectAddress)) {
      scanAllDevice.sort(this.sortBy("rssi"))
      self.connectData = {'id': scanAllDevice[0].id, 'type': 'proxy'}
      console.log('FBY home connectData: ' + JSON.stringify(self.connectData))
      self.$store.commit('setBleConnectAddress', scanAllDevice[0].address)
    } else {
      scanAllDevice.forEach(function (item) {
        if (item.address === willConnectAddress) {
          self.connectData = {'id': item.id, 'type': 'proxy'}
          console.log('FBY home connectData: ' + JSON.stringify(self.connectData))
          self.$store.commit('setBleConnectAddress', item.address)
        }
      })
    }
    if (JSON.stringify(self.connectData) === '{}') {
      return true
    } else {
      proxyDisconnect(self)
      EspBleMesh.connect(JSON.stringify(self.connectData))
      return false
    }
  } else {
    return true
  }
}
function scanCallback (res, self) {
  self.connectData = {}
  res = this.base64().decode(res)
  res = JSON.parse(res)
  var storeNodeAddress = []
  var scanAllDevice = self.$store.state.bleScanAllDevice
  scanAllDevice.forEach(function (item) {
    storeNodeAddress.push(item)
  })
  res.forEach(function (item) {
    if (storeNodeAddress.indexOf(item.address) === -1) {
      scanAllDevice.push(item)
    }
  })
  self.$store.commit('setBleScanAllDevice', scanAllDevice)
}
function connectCallback (res, self, isNext) {
  res = this.base64().decode(res)
  console.log('FBY connectCallback' + res)
  res = JSON.parse(res)
  if (!this._isEmpty(res)) {
    switch(res.status) {
      case Constant.STATUS_GATT_CONNECTED:
        console.log('设备蓝牙连接成功 12345678900987654321')
        this.messageRemind(self.$t('bleconnectsuc'))
        self.connectContent = 0
        self.isBleConnect = true
        self.$store.commit('setIsBleConnect', true)
        break
      case Constant.STATUS_GATT_DISCONNECTED:
        self.$store.commit('setIsBleConnect', false)
        if (self.connectContent < 2) {
          if (!this._isEmpty(self.connectData)) {
            EspBleMesh.connect(JSON.stringify(self.connectData))
          }
        }
        self.connectContent ++
        break
      case Constant.STATUS_PROXY_READY:
      case Constant.STATUS_PROXY_READY_NO_DST:
        if (isNext) {
          self.sendBLEMeshData()
        }
        break
    }
  }
}
function setMeshGroupsData (groups, nodes, self) {
  var newGroups = {}
  console.log('groups: ' + JSON.stringify(groups))
  groups.forEach(function (item, i) {
    item['isOnOff'] = false
    item['address'] = item.address
    console.log('groups address: ' + item.address)
    newGroups[item.address] = []
  })
  nodes.forEach(function (nodesItem) {
    let elements = nodesItem.elements
    console.log('elements: ' + JSON.stringify(elements))
    elements.forEach(function (elementsItem) {
      let models = elementsItem.models
      console.log('models: ' + JSON.stringify(models))
      models.forEach(function (modelsItem) {
        let modelsGroups = modelsItem.groups
        console.log('modelsGroups: ' + JSON.stringify(modelsGroups))
        modelsGroups.forEach(function (groupsItem) {
          if (!_isEmpty(groupsItem)) {
            if (modelsItem.id === Constant.CONFIG_ON_OFF_SERVER) {
              let groupModel = {}
              let groupModelArr = []
              groupModel[Constant.KEY_NODE_ADDRESS] = nodesItem.address
              groupModel[Constant.KEY_ELEMENT_ADDRESS] = elementsItem.address
              groupModel[Constant.KEY_MODEL_ID] = modelsItem.id
              groupModel['name'] = nodesItem.name
              groupModel['address'] = 'node: ' + nodesItem.address + '  element: ' + elementsItem.address + '  model: ' + modelsItem.id
              groupModel['status'] = false
              groupModel['tid'] = 1
              console.log('newGroups: ' + JSON.stringify(newGroups) + 'groupsItem: ' + groupsItem)
              newGroups[groupsItem].forEach(function (groudsModelItem) {
                groupModelArr.push(groudsModelItem.elementAddress)
              })
              if (groupModelArr.indexOf(elementsItem.address) === -1) {
                newGroups[groupsItem].push(groupModel)
              }
            } else {
              // groupModel['tid'] = 88
            }
          }
        })
      })
    })
  })
  var list = groups.map(item => {
    let obj = {...item}
    obj.models = newGroups[obj.address]
    return obj
  })
  // groups = unique1(groups)
  self.$store.commit('setDeviceGroupList', list)
  console.log('FBY Utils groups: ' + JSON.stringify(list))
  return list
}
function unique1 (arr){
  var hash = []
  var nodeAddressArr = []
  for (var i = 0; i < arr.length; i++) {
    nodeAddressArr.push(arr.nodeAddress)
  }
  for (var i = 0; i < arr.length; i++) {
    if(hash.indexOf(arr[i]) === -1){
      hash.push(arr[i]);
    }
  }
  return hash;
}
function sendMeshMessage (self, sendMeshData) {
  console.log('FBY sendMeshMessage: ' + JSON.stringify(sendMeshData))
  sendDataQueue.push(sendMeshData)
  if (sendDataQueue.length === 1) {
    setTimeout(() => {
      runSendMeshMessage(self)
    }, 300)
  }
}

function runSendMeshMessage (self) {
  var meshMessage = sendDataQueue.shift()
  self.$store.commit('setSendDataQueueArr', sendDataQueue)
  EspBleMesh.sendMeshMessage(JSON.stringify(meshMessage))
  if (sendDataQueue.length > 0) {
    setTimeout(() => {
      runSendMeshMessage(self)
    }, 300)
  }
}

function messageRemind (msg) {
  Toast({
    message: msg,
    position: 'bottom'
  })
}
function getMeshNetworkCallback(res, self) {
  var models = []
  res = this.base64().decode(res)
  res = JSON.parse(res)
  if (!_isEmpty(res)) {
    if (!this._isEmpty(res.nodes) && res.nodes.length > 0) {
      if (!this._isEmpty(res.groups)) {
        res.groups.forEach(function (item) {
          item['models'] = models
        })
        console.log('FBY getMeshNetworkCallback groups：' + JSON.stringify(res.groups))
        console.log('FBY getMeshNetworkCallback nodes：' + JSON.stringify(res.nodes))
        self.getMeshGroupData(res.groups, res.nodes)
      }
      res.nodes.forEach(function (resItem) {
        resItem['tid'] = '1'
        resItem['status'] = false
        resItem['color'] = [0, 1, 1]
      })
      self.homeDeviceList = res.nodes
      self.$store.commit('setHomeDeviceList', self.homeDeviceList)
      self.isBleConnect = self.$store.state.isBleConnect
      if (self.isBleConnect) {
        self.sendBLEMeshData()
      } else {
        self.homeDeviceScan(self)
      }
    } else {
      self.$store.commit('setHomeDeviceList', [])
      self.homeDeviceList = []
    }
  }
}
function sendBLEMeshData(self) {
  var sendFastData = {}
  // console.log('FBY sendBLEMeshData: ' + JSON.stringify(self.homeDeviceList))
  if (self.selected === 1) {
    sendFastData[Constant.KEY_OP_CODE] = Constant.FAST_PROV_NODE_ADDR_GET
    sendFastData[Constant.KEY_DST_ADDRESS] = self.$store.state.bleConnectAddress
    // console.log('FBY First provision sendFastData:' + JSON.stringify(sendFastData))
    self.sendMeshMessage(sendFastData)
  }
  var sendMeshData = {}
  sendMeshData[Constant.KEY_OP_CODE] = Constant.GENERIC_ON_OFF_GET
  sendMeshData[Constant.KEY_DST_ADDRESS] = 65535
  sendMeshData[Constant.KEY_NODE_ADDRESS] = 65535
  self.sendMeshMessage(sendMeshData)
  // var sendMeshData = {}
  // sendMeshData[Constant.KEY_OP_CODE] = Constant.LIGHT_HSL_GET
  // sendMeshData[Constant.KEY_DST_ADDRESS] = 65535
  // sendMeshData[Constant.KEY_NODE_ADDRESS] = 65535
  // self.sendMeshMessage(sendMeshData)
  // var sendMeshData = {}
  // sendMeshData[Constant.KEY_OP_CODE] = Constant.LIGHT_CTL_GET
  // sendMeshData[Constant.KEY_DST_ADDRESS] = 65535
  // sendMeshData[Constant.KEY_NODE_ADDRESS] = 65535
  // self.sendMeshMessage(sendMeshData)
  var sendMeshData = {}
  sendMeshData[Constant.KEY_OP_CODE] = Constant.CURRENT_VERSION_GET
  sendMeshData[Constant.KEY_DST_ADDRESS] = 65535
  sendMeshData[Constant.KEY_NODE_ADDRESS] = 65535
  self.sendMeshMessage(sendMeshData)
  // self.homeDeviceList.forEach(function (item) {
  //   var sendMeshData = {}
  //   sendMeshData[Constant.KEY_OP_CODE] = Constant.GENERIC_ON_OFF_GET
  //   sendMeshData[Constant.KEY_DST_ADDRESS] = item.elements[0].address
  //   sendMeshData[Constant.KEY_NODE_ADDRESS] = item.address
  //   self.sendMeshMessage(sendMeshData)
  //   sendMeshData = {}
  //   sendMeshData[Constant.KEY_OP_CODE] = Constant.LIGHT_HSL_GET
  //   sendMeshData[Constant.KEY_DST_ADDRESS] = item.elements[0].address
  //   sendMeshData[Constant.KEY_NODE_ADDRESS] = item.address
  //   self.sendMeshMessage(sendMeshData)
  //   sendMeshData = {}
  //   sendMeshData[Constant.KEY_OP_CODE] = Constant.LIGHT_CTL_GET
  //   sendMeshData[Constant.KEY_DST_ADDRESS] = item.elements[0].address
  //   sendMeshData[Constant.KEY_NODE_ADDRESS] = item.address
  //   self.sendMeshMessage(sendMeshData)
  //   sendMeshData = {}
  //   sendMeshData[Constant.KEY_OP_CODE] = Constant.CURRENT_VERSION_GET
  //   sendMeshData[Constant.KEY_DST_ADDRESS] = item.elements[0].address
  //   sendMeshData[Constant.KEY_NODE_ADDRESS] = item.address
  //   self.sendMeshMessage(sendMeshData)
  // })
}
function meshMessageCallback(res, self, typeStr) {
  res = this.base64().decode(res)
  res = JSON.parse(res)
  if (!this._isEmpty(res)) {
    if (res.opCode === Constant.CURRENT_VERSION_STATUS) {
      console.log('FBY meshMessageCallback CURRENT_VERSION_STATUS' + JSON.stringify(res))
      updateNodesStatus(self, res.nodeAddress, res.versionName, 'version')
    } else if (res.opCode === Constant.GENERIC_ON_OFF_STATUS) {
      console.log('GENERIC_ON_OFF_STATUS: ' + JSON.stringify(res))
      updateNodesStatus(self, res.nodeAddress, res.state, 'status')
    } else if (res.opCode === Constant.LIGHT_HSL_STATUS) {
      updateNodesStatus(self, res.nodeAddress, res.hsl, 'color')
    } else if (res.opCode === Constant.LIGHT_CTL_STATUS) {
      updateNodesStatus(self, res.nodeAddress, res.ctl, 'color')
    } else if (res.opCode === Constant.FAST_PROV_STATUS) {
      if (typeStr === 'firstProvision') {
        self.firstProvStatusCallback()
      }
    } else if (res.opCode === Constant.FAST_PROV_NODE_ADDR_STATUS) {
      self.getFastProvNodeAddrCallback(res.fastNodes)
    } else if (res.opCode === Constant.CONFIG_MODEL_SUBSCRIPTION_STATUS) {
      self.configModelSubscriptionCallback(res)
    } else if (res.opCode === Constant.CONFIG_NODE_RESET_STATUS) {
      self.configNodeResetCallback(res)
    }
  }
}
function proxyDisconnect(self) {
  EspBleMesh.disconnect()
  self.$store.commit('setIsBleConnect', false)
}
function updateGroupsStatus(self, groupAddress, state, itemKey) {
  console.log('updateGroupsStatus groupAddress: ' + groupAddress + ' state: ' + state)
  console.log('updateGroupsStatus homedeviceGroup old: ' + JSON.stringify(self.homedeviceGroup))
  self.homedeviceGroup.forEach(function (item, i) {
    if (item.address === groupAddress) {
      item[itemKey] = state
      self.homedeviceGroup.splice(i, 1, item)
      console.log('updateGroupsStatus homedeviceGroup new: ' + JSON.stringify(self.homedeviceGroup))
      self.$store.commit('setDeviceGroupList', self.homedeviceGroup)
      item.models.forEach(function (item) {
        updateNodesStatus(self, item.nodeAddress, self.currentStatus, 'status')
      })
    }
  })
}
function updateNodesStatus(self, nodeAddress, state, itemKey) {
  self.homeDeviceList.forEach(function (item, i) {
    if (item.address === nodeAddress) {
      item[itemKey] = state
      self.homeDeviceList.splice(i, 1, item)
      self.$store.commit('setHomeDeviceList', self.homeDeviceList)
    }
  })
  // 同步快速节点中设备开关状态
  self.fastNodesList.forEach(function (item, i) {
    if (item.address === nodeAddress) {
      item[itemKey] = state
      self.fastNodesList.splice(i, 1, item)
      self.$store.commit('setFastNodesList', self.fastNodesList)
    }
  })
  // 同步群组中设备开关状态
  self.homedeviceGroup.forEach(function (item, i) {
    item.models.forEach(function (modelsItem, j) {
      if (modelsItem.nodeAddress === nodeAddress) {
        modelsItem[itemKey] = state
        item.models.splice(j, 1, modelsItem)
      }
    })
    self.homedeviceGroup.splice(i, 1, item)
    self.$store.commit('setDeviceGroupList', self.homedeviceGroup)
  })
}
function fastNodesStatus(self, fastNodes) {
  console.log('FBY fastNodesStatus: ' + JSON.stringify(fastNodes))
  fastNodes.forEach(function (item, i) {
    item['status'] = false
    item['tid'] = 1
    item['name'] = '0x' + item.address.toString(16)
    fastNodes.splice(i, 1, item)
    self.$store.commit('setFastNodesList', fastNodes)
  })
  return fastNodes
}
export default {initColorWheel, getIconUtil, getColorUtil, addBgClassUtils, _isEmpty, isLight, base64, stringToBytes, getBLERssiIcon, sortBy,
  homeDeviceScan, scanTimeout, scanCallback, connectCallback, sendMeshMessage, setMeshGroupsData, messageRemind, getMeshNetworkCallback, sendBLEMeshData, meshMessageCallback,
  proxyDisconnect, updateGroupsStatus, updateNodesStatus, fastNodesStatus}
