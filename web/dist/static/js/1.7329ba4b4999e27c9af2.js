webpackJsonp([1],{"+tPU":function(e,t,s){s("xGkn");for(var n=s("7KvD"),o=s("hJx8"),i=s("/bQp"),r=s("dSzd")("toStringTag"),c="CSSRuleList,CSSStyleDeclaration,CSSValueList,ClientRectList,DOMRectList,DOMStringList,DOMTokenList,DataTransferItemList,FileList,HTMLAllCollection,HTMLCollection,HTMLFormElement,HTMLSelectElement,MediaList,MimeTypeArray,NamedNodeMap,NodeList,PaintRequestList,Plugin,PluginArray,SVGLengthList,SVGNumberList,SVGPathSegList,SVGPointList,SVGStringList,SVGTransformList,SourceBufferList,StyleSheetList,TextTrackCueList,TextTrackList,TouchList".split(","),d=0;d<c.length;d++){var l=c[d],a=n[l],u=a&&a.prototype;u&&!u[r]&&o(u,r,l),i[l]=i.Array}},"//Fk":function(e,t,s){e.exports={default:s("U5ju"),__esModule:!0}},"/bQp":function(e,t){e.exports={}},"2KxR":function(e,t){e.exports=function(e,t,s,n){if(!(e instanceof t)||void 0!==n&&n in e)throw TypeError(s+": incorrect invocation!");return e}},"3fs2":function(e,t,s){var n=s("RY/4"),o=s("dSzd")("iterator"),i=s("/bQp");e.exports=s("FeBl").getIteratorMethod=function(e){if(void 0!=e)return e[o]||e["@@iterator"]||i[n(e)]}},"4mcu":function(e,t){e.exports=function(){}},"82Mu":function(e,t,s){var n=s("7KvD"),o=s("L42u").set,i=n.MutationObserver||n.WebKitMutationObserver,r=n.process,c=n.Promise,d="process"==s("R9M2")(r);e.exports=function(){var e,t,s,l=function(){var n,o;for(d&&(n=r.domain)&&n.exit();e;){o=e.fn,e=e.next;try{o()}catch(n){throw e?s():t=void 0,n}}t=void 0,n&&n.enter()};if(d)s=function(){r.nextTick(l)};else if(!i||n.navigator&&n.navigator.standalone)if(c&&c.resolve){var a=c.resolve(void 0);s=function(){a.then(l)}}else s=function(){o.call(n,l)};else{var u=!0,f=document.createTextNode("");new i(l).observe(f,{characterData:!0}),s=function(){f.data=u=!u}}return function(n){var o={fn:n,next:void 0};t&&(t.next=o),e||(e=o,s()),t=o}}},"880/":function(e,t,s){e.exports=s("hJx8")},"94VQ":function(e,t,s){"use strict";var n=s("Yobk"),o=s("X8DO"),i=s("e6n0"),r={};s("hJx8")(r,s("dSzd")("iterator"),function(){return this}),e.exports=function(e,t,s){e.prototype=n(r,{next:o(1,s)}),i(e,t+" Iterator")}},BO1k:function(e,t,s){e.exports={default:s("fxRn"),__esModule:!0}},CXw9:function(e,t,s){"use strict";var n,o,i,r,c=s("O4g8"),d=s("7KvD"),l=s("+ZMJ"),a=s("RY/4"),u=s("kM2E"),f=s("EqjI"),h=s("lOnJ"),p=s("2KxR"),v=s("NWt+"),m=s("t8x9"),g=s("L42u").set,S=s("82Mu")(),A=s("qARP"),E=s("dNDb"),_=s("iUbK"),x=s("fJUb"),y=d.TypeError,G=d.process,b=G&&G.versions,C=b&&b.v8||"",L=d.Promise,T="process"==a(G),D=function(){},N=o=A.f,w=!!function(){try{var e=L.resolve(1),t=(e.constructor={})[s("dSzd")("species")]=function(e){e(D,D)};return(T||"function"==typeof PromiseRejectionEvent)&&e.then(D)instanceof t&&0!==C.indexOf("6.6")&&-1===_.indexOf("Chrome/66")}catch(e){}}(),k=function(e){var t;return!(!f(e)||"function"!=typeof(t=e.then))&&t},O=function(e,t){if(!e._n){e._n=!0;var s=e._c;S(function(){for(var n=e._v,o=1==e._s,i=0,r=function(t){var s,i,r,c=o?t.ok:t.fail,d=t.resolve,l=t.reject,a=t.domain;try{c?(o||(2==e._h&&R(e),e._h=1),!0===c?s=n:(a&&a.enter(),s=c(n),a&&(a.exit(),r=!0)),s===t.promise?l(y("Promise-chain cycle")):(i=k(s))?i.call(s,d,l):d(s)):l(n)}catch(e){a&&!r&&a.exit(),l(e)}};s.length>i;)r(s[i++]);e._c=[],e._n=!1,t&&!e._h&&P(e)})}},P=function(e){g.call(d,function(){var t,s,n,o=e._v,i=M(e);if(i&&(t=E(function(){T?G.emit("unhandledRejection",o,e):(s=d.onunhandledrejection)?s({promise:e,reason:o}):(n=d.console)&&n.error&&n.error("Unhandled promise rejection",o)}),e._h=T||M(e)?2:1),e._a=void 0,i&&t.e)throw t.v})},M=function(e){return 1!==e._h&&0===(e._a||e._c).length},R=function(e){g.call(d,function(){var t;T?G.emit("rejectionHandled",e):(t=d.onrejectionhandled)&&t({promise:e,reason:e._v})})},I=function(e){var t=this;t._d||(t._d=!0,(t=t._w||t)._v=e,t._s=2,t._a||(t._a=t._c.slice()),O(t,!0))},j=function(e){var t,s=this;if(!s._d){s._d=!0,s=s._w||s;try{if(s===e)throw y("Promise can't be resolved itself");(t=k(e))?S(function(){var n={_w:s,_d:!1};try{t.call(e,l(j,n,1),l(I,n,1))}catch(e){I.call(n,e)}}):(s._v=e,s._s=1,O(s,!1))}catch(e){I.call({_w:s,_d:!1},e)}}};w||(L=function(e){p(this,L,"Promise","_h"),h(e),n.call(this);try{e(l(j,this,1),l(I,this,1))}catch(e){I.call(this,e)}},(n=function(e){this._c=[],this._a=void 0,this._s=0,this._d=!1,this._v=void 0,this._h=0,this._n=!1}).prototype=s("xH/j")(L.prototype,{then:function(e,t){var s=N(m(this,L));return s.ok="function"!=typeof e||e,s.fail="function"==typeof t&&t,s.domain=T?G.domain:void 0,this._c.push(s),this._a&&this._a.push(s),this._s&&O(this,!1),s.promise},catch:function(e){return this.then(void 0,e)}}),i=function(){var e=new n;this.promise=e,this.resolve=l(j,e,1),this.reject=l(I,e,1)},A.f=N=function(e){return e===L||e===r?new i(e):o(e)}),u(u.G+u.W+u.F*!w,{Promise:L}),s("e6n0")(L,"Promise"),s("bRrM")("Promise"),r=s("FeBl").Promise,u(u.S+u.F*!w,"Promise",{reject:function(e){var t=N(this);return(0,t.reject)(e),t.promise}}),u(u.S+u.F*(c||!w),"Promise",{resolve:function(e){return x(c&&this===r?L:this,e)}}),u(u.S+u.F*!(w&&s("dY0y")(function(e){L.all(e).catch(D)})),"Promise",{all:function(e){var t=this,s=N(t),n=s.resolve,o=s.reject,i=E(function(){var s=[],i=0,r=1;v(e,!1,function(e){var c=i++,d=!1;s.push(void 0),r++,t.resolve(e).then(function(e){d||(d=!0,s[c]=e,--r||n(s))},o)}),--r||n(s)});return i.e&&o(i.v),s.promise},race:function(e){var t=this,s=N(t),n=s.reject,o=E(function(){v(e,!1,function(e){t.resolve(e).then(s.resolve,n)})});return o.e&&n(o.v),s.promise}})},EGZi:function(e,t){e.exports=function(e,t){return{value:t,done:!!e}}},EqBC:function(e,t,s){"use strict";var n=s("kM2E"),o=s("FeBl"),i=s("7KvD"),r=s("t8x9"),c=s("fJUb");n(n.P+n.R,"Promise",{finally:function(e){var t=r(this,o.Promise||i.Promise),s="function"==typeof e;return this.then(s?function(s){return c(t,e()).then(function(){return s})}:e,s?function(s){return c(t,e()).then(function(){throw s})}:e)}})},L42u:function(e,t,s){var n,o,i,r=s("+ZMJ"),c=s("knuC"),d=s("RPLV"),l=s("ON07"),a=s("7KvD"),u=a.process,f=a.setImmediate,h=a.clearImmediate,p=a.MessageChannel,v=a.Dispatch,m=0,g={},S=function(){var e=+this;if(g.hasOwnProperty(e)){var t=g[e];delete g[e],t()}},A=function(e){S.call(e.data)};f&&h||(f=function(e){for(var t=[],s=1;arguments.length>s;)t.push(arguments[s++]);return g[++m]=function(){c("function"==typeof e?e:Function(e),t)},n(m),m},h=function(e){delete g[e]},"process"==s("R9M2")(u)?n=function(e){u.nextTick(r(S,e,1))}:v&&v.now?n=function(e){v.now(r(S,e,1))}:p?(i=(o=new p).port2,o.port1.onmessage=A,n=r(i.postMessage,i,1)):a.addEventListener&&"function"==typeof postMessage&&!a.importScripts?(n=function(e){a.postMessage(e+"","*")},a.addEventListener("message",A,!1)):n="onreadystatechange"in l("script")?function(e){d.appendChild(l("script")).onreadystatechange=function(){d.removeChild(this),S.call(e)}}:function(e){setTimeout(r(S,e,1),0)}),e.exports={set:f,clear:h}},M6a0:function(e,t){},Mhyx:function(e,t,s){var n=s("/bQp"),o=s("dSzd")("iterator"),i=Array.prototype;e.exports=function(e){return void 0!==e&&(n.Array===e||i[o]===e)}},"NWt+":function(e,t,s){var n=s("+ZMJ"),o=s("msXi"),i=s("Mhyx"),r=s("77Pl"),c=s("QRG4"),d=s("3fs2"),l={},a={};(t=e.exports=function(e,t,s,u,f){var h,p,v,m,g=f?function(){return e}:d(e),S=n(s,u,t?2:1),A=0;if("function"!=typeof g)throw TypeError(e+" is not iterable!");if(i(g)){for(h=c(e.length);h>A;A++)if((m=t?S(r(p=e[A])[0],p[1]):S(e[A]))===l||m===a)return m}else for(v=g.call(e);!(p=v.next()).done;)if((m=o(v,S,p.value,t))===l||m===a)return m}).BREAK=l,t.RETURN=a},OaGn:function(e,t){},PzxK:function(e,t,s){var n=s("D2L2"),o=s("sB3e"),i=s("ax3d")("IE_PROTO"),r=Object.prototype;e.exports=Object.getPrototypeOf||function(e){return e=o(e),n(e,i)?e[i]:"function"==typeof e.constructor&&e instanceof e.constructor?e.constructor.prototype:e instanceof Object?r:null}},RPLV:function(e,t,s){var n=s("7KvD").document;e.exports=n&&n.documentElement},"RY/4":function(e,t,s){var n=s("R9M2"),o=s("dSzd")("toStringTag"),i="Arguments"==n(function(){return arguments}());e.exports=function(e){var t,s,r;return void 0===e?"Undefined":null===e?"Null":"string"==typeof(s=function(e,t){try{return e[t]}catch(e){}}(t=Object(e),o))?s:i?n(t):"Object"==(r=n(t))&&"function"==typeof t.callee?"Arguments":r}},U5ju:function(e,t,s){s("M6a0"),s("zQR9"),s("+tPU"),s("CXw9"),s("EqBC"),s("jKW+"),e.exports=s("FeBl").Promise},Yobk:function(e,t,s){var n=s("77Pl"),o=s("qio6"),i=s("xnc9"),r=s("ax3d")("IE_PROTO"),c=function(){},d=function(){var e,t=s("ON07")("iframe"),n=i.length;for(t.style.display="none",s("RPLV").appendChild(t),t.src="javascript:",(e=t.contentWindow.document).open(),e.write("<script>document.F=Object<\/script>"),e.close(),d=e.F;n--;)delete d.prototype[i[n]];return d()};e.exports=Object.create||function(e,t){var s;return null!==e?(c.prototype=n(e),s=new c,c.prototype=null,s[r]=e):s=d(),void 0===t?s:o(s,t)}},bRrM:function(e,t,s){"use strict";var n=s("7KvD"),o=s("FeBl"),i=s("evD5"),r=s("+E39"),c=s("dSzd")("species");e.exports=function(e){var t="function"==typeof o[e]?o[e]:n[e];r&&t&&!t[c]&&i.f(t,c,{configurable:!0,get:function(){return this}})}},dNDb:function(e,t){e.exports=function(e){try{return{e:!1,v:e()}}catch(e){return{e:!0,v:e}}}},dSzd:function(e,t,s){var n=s("e8AB")("wks"),o=s("3Eo+"),i=s("7KvD").Symbol,r="function"==typeof i;(e.exports=function(e){return n[e]||(n[e]=r&&i[e]||(r?i:o)("Symbol."+e))}).store=n},dY0y:function(e,t,s){var n=s("dSzd")("iterator"),o=!1;try{var i=[7][n]();i.return=function(){o=!0},Array.from(i,function(){throw 2})}catch(e){}e.exports=function(e,t){if(!t&&!o)return!1;var s=!1;try{var i=[7],r=i[n]();r.next=function(){return{done:s=!0}},i[n]=function(){return r},e(i)}catch(e){}return s}},e6n0:function(e,t,s){var n=s("evD5").f,o=s("D2L2"),i=s("dSzd")("toStringTag");e.exports=function(e,t,s){e&&!o(e=s?e:e.prototype,i)&&n(e,i,{configurable:!0,value:t})}},fJUb:function(e,t,s){var n=s("77Pl"),o=s("EqjI"),i=s("qARP");e.exports=function(e,t){if(n(e),o(t)&&t.constructor===e)return t;var s=i.f(e);return(0,s.resolve)(t),s.promise}},fxRn:function(e,t,s){s("+tPU"),s("zQR9"),e.exports=s("g8Ux")},g8Ux:function(e,t,s){var n=s("77Pl"),o=s("3fs2");e.exports=s("FeBl").getIterator=function(e){var t=o(e);if("function"!=typeof t)throw TypeError(e+" is not iterable!");return n(t.call(e))}},h65t:function(e,t,s){var n=s("UuGF"),o=s("52gC");e.exports=function(e){return function(t,s){var i,r,c=String(o(t)),d=n(s),l=c.length;return d<0||d>=l?e?"":void 0:(i=c.charCodeAt(d))<55296||i>56319||d+1===l||(r=c.charCodeAt(d+1))<56320||r>57343?e?c.charAt(d):i:e?c.slice(d,d+2):r-56320+(i-55296<<10)+65536}}},iUbK:function(e,t,s){var n=s("7KvD").navigator;e.exports=n&&n.userAgent||""},"jKW+":function(e,t,s){"use strict";var n=s("kM2E"),o=s("qARP"),i=s("dNDb");n(n.S,"Promise",{try:function(e){var t=o.f(this),s=i(e);return(s.e?t.reject:t.resolve)(s.v),t.promise}})},knuC:function(e,t){e.exports=function(e,t,s){var n=void 0===s;switch(t.length){case 0:return n?e():e.call(s);case 1:return n?e(t[0]):e.call(s,t[0]);case 2:return n?e(t[0],t[1]):e.call(s,t[0],t[1]);case 3:return n?e(t[0],t[1],t[2]):e.call(s,t[0],t[1],t[2]);case 4:return n?e(t[0],t[1],t[2],t[3]):e.call(s,t[0],t[1],t[2],t[3])}return e.apply(s,t)}},msXi:function(e,t,s){var n=s("77Pl");e.exports=function(e,t,s,o){try{return o?t(n(s)[0],s[1]):t(s)}catch(t){var i=e.return;throw void 0!==i&&n(i.call(e)),t}}},qARP:function(e,t,s){"use strict";var n=s("lOnJ");e.exports.f=function(e){return new function(e){var t,s;this.promise=new e(function(e,n){if(void 0!==t||void 0!==s)throw TypeError("Bad Promise constructor");t=e,s=n}),this.resolve=n(t),this.reject=n(s)}(e)}},qio6:function(e,t,s){var n=s("evD5"),o=s("77Pl"),i=s("lktj");e.exports=s("+E39")?Object.defineProperties:function(e,t){o(e);for(var s,r=i(t),c=r.length,d=0;c>d;)n.f(e,s=r[d++],t[s]);return e}},t8x9:function(e,t,s){var n=s("77Pl"),o=s("lOnJ"),i=s("dSzd")("species");e.exports=function(e,t){var s,r=n(e).constructor;return void 0===r||void 0==(s=n(r)[i])?t:o(s)}},uVFc:function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=s("BO1k"),o=s.n(n),i=s("//Fk"),r=s.n(i),c=s("mvHQ"),d=s.n(c),l=s("Y54T"),a=(s("NYxO"),s("yojn")),u=s.n(a),f=s("Au9i"),h={data:function(){return{blueEnable:!0,locationGranted:!0,locationEnabled:!0,groupList:[],deviceList:[],groupInfo:this.$store.state.groupInfo,isSelectedGroupsAddress:[],isAllGroupsSelect:!1,groupCount:0,groupSelected:0,isSelectedNodesAddress:[],isAllNodesSelect:!1,nodeCount:0,nodeSelected:0,isSelectedElementsAddress:{},selectedElementsAddress:{},isAllElementsSelect:{},elementCount:{},elementSelected:0,elementSelectedArr:[],connectData:"",connectContent:0,isBleConnect:!1,groupAddNodeTimeOut:"",currentGroupArr:[]}},created:function(){var e=this;window.onBackPressed=function(){e.onBackPressed()}},methods:{hide:function(){this.$router.goBack()},getGroupDes:function(e){return l.a._isEmpty(e)?"":e+"  "},backHome:function(){this.$router.goRight({name:"groupsOperation"})},getIcon:function(e){return l.a.getIconUtil(e)},onBackPressed:function(){this.$router.goBack()},addGroupDevice:function(){this.isBleConnect=this.$store.state.isBleConnect,this.isBleConnect?this.sendBLEMeshData():l.a.homeDeviceScan(this)},scanTimeout:function(){l.a.scanTimeout(this)&&l.a.messageRemind(this.$t("unconnectable"))},sendBLEMeshData:function(){var e=this,t=e.groupInfo.models,s=[];f.Indicator.open(),e.groupAddNodeTimeOut=setTimeout(function(){f.Indicator.close(),l.a.messageRemind(e.$t("groupaddaodetimeout"))},1e4),console.log("addGroupDevice newGroupElements: "+d()(t)+" groupInfo: "+d()(e.groupInfo)),l.a._isEmpty(t)||t.forEach(function(e){s.push(e.elementAddress)}),e.addNodeToGroup(0,s),e.addGroupToGroup(0,s)},addNodeToGroup:function(e,t){console.log("index: "+e+" newElements: "+t);var s=[];if(this.deviceList.length>0)try{if(e<this.deviceList.length){var n=this.deviceList[e];if(n.address)if(s=this.isSelectedElementsAddress[n.address],console.log("addNodeToGroup elementsArr1222111: "+d()(s)),l.a._isEmpty(s))this.addNodeToGroup(e+1,t);else if(s.length>0){console.log("addNodeToGroup isSelectedElementsAddress: "+d()(this.isSelectedElementsAddress)),console.log("addNodeToGroup elementsArr: "+d()(s));this.addElementToGroup(s,0,e,t)}else this.addNodeToGroup(e+1,t);else this.addNodeToGroup(e+1,t)}}catch(s){console.error("addNodeToGroup err: "+s),this.addNodeToGroup(e+1,t)}},addElementToGroup:function(e,t,s,n){var o=this;console.log("addElementToGroup: "+d()(e)),o.sendNodeData(e,t,s,n).then(function(i){t<i<e.length?setTimeout(function(){o.addElementToGroup(e,i,s,n)},500):i===e.length&&s<o.deviceList.length&&setTimeout(function(){o.addNodeToGroup(s+1,n)},500)})},sendNodeData:function(e,t,s,n){console.log("sendNodeData: "+d()(n)+"elementsArr: "+d()(e)+"subIndex: "+t);var o=this;return new r.a(function(i,r){var c=e[t],a={};-1===n.indexOf(c)&&(a[u.a.KEY_OP_CODE]=u.a.CONFIG_MODEL_SUBSCRIPTION_ADD,a[u.a.KEY_DST_ADDRESS]=o.deviceList[s].address,a[u.a.KEY_GROUP_ADDRESS]=o.groupInfo.address,a[u.a.KEY_ELEMENT_ADDRESS]=c,console.log("addGroupDevice node sendMeshData:"+d()(a)),l.a.sendMeshMessage(o,a)),i(++t)})},addGroupToGroup:function(e,t){var s=[];if(console.log("addGroupToGroup groupList: "+d()(this.groupList)),this.groupList.length>0&&e<this.groupList.length){var n=this.groupList[e];if(-1!==this.isSelectedGroupsAddress.indexOf(n.address))if(s=n.models,console.log("addGroupToGroup: "+d()(s)),l.a._isEmpty(s))this.addGroupToGroup(e+1,t);else if(s.length>0){this.addModelToGroup(s,0,e,t)}else this.addGroupToGroup(e+1,t);else this.addGroupToGroup(e+1,t)}},addModelToGroup:function(e,t,s,n){var o=this;o.sendGroupData(e,t,n).then(function(i){console.log("addModelToGroup: "+d()(e)),t<i<e.length?setTimeout(function(){o.addModelToGroup(e,i,s,n)},500):i===e.length&&s<o.groupList.length&&setTimeout(function(){o.addGroupToGroup(s+1,n)},500)})},sendGroupData:function(e,t,s){var n=this;return new r.a(function(o,i){var r=e[t],c={};c[u.a.KEY_OP_CODE]=u.a.CONFIG_MODEL_SUBSCRIPTION_ADD,c[u.a.KEY_GROUP_ADDRESS]=n.groupInfo.address,c[u.a.KEY_DST_ADDRESS]=r.nodeAddress,-1===s.indexOf(r.elementAddress)&&(c[u.a.KEY_ELEMENT_ADDRESS]=r.elementAddress,console.log("addGroupDevice group sendMeshData:"+d()(c)),l.a.sendMeshMessage(n,c)),o(++t)})},switchGroup:function(e){var t=this.groupList[e].address;console.log("选择群组"+t);var s=this.isSelectedGroupsAddress.indexOf(t);-1===s?this.isSelectedGroupsAddress.push(t):this.isSelectedGroupsAddress.splice(s,1),this.groupSelected=this.isSelectedGroupsAddress.length,this.groupSelected===this.groupCount&&(this.isAllGroupsSelect=!0)},isGroupSelected:function(e){var t=!1;return-1!==this.isSelectedGroupsAddress.indexOf(e)&&(t=!0),t},selectAllGroup:function(){console.log("2");if(this.isAllGroupsSelect)this.groupSelected=0,this.isSelectedGroupsAddress=[],this.isAllGroupsSelect=!1;else{this.groupSelected=this.groupCount;var e=[];this.groupList.forEach(function(t){e.push(t.address)}),this.isSelectedGroupsAddress=e,this.isAllGroupsSelect=!0}},switchNodes:function(e,t){if(console.log("选择节点："+e+" model: "+d()(this.groupInfo.models)),!l.a._isEmpty(this.groupInfo.models)){var s=!0,n=!1,i=void 0;try{for(var r,c=o()(this.groupInfo.models);!(s=(r=c.next()).done);s=!0){var a=r.value;if(a.nodeAddress===e)return void console.log("选择节点："+e+"item： "+d()(a))}}catch(e){n=!0,i=e}finally{try{!s&&c.return&&c.return()}finally{if(n)throw i}}}var u=this.isSelectedNodesAddress.indexOf(e);if(-1===u?this.isSelectedNodesAddress.push(e):this.isSelectedNodesAddress.splice(u,1),this.isNodeAllSelect(),this.isAllElementsSelect[e])this.elementSelected=0,this.isSelectedElementsAddress[e]=[],this.isAllElementsSelect[e]=!1;else{this.elementSelected=this.elementCount[e];var f=this.isSelectedElementsAddress,h=[];this.deviceList[t].elements.forEach(function(e){h.push(e.address)}),f[e]=h,this.isSelectedElementsAddress=f,this.isAllElementsSelect[e]=!0}},nodeAdd:function(e){-1===this.isSelectedNodesAddress.indexOf(e)&&this.isSelectedNodesAddress.push(e),this.isNodeAllSelect()},nodedelete:function(e){var t=this.isSelectedNodesAddress.indexOf(e);-1!==t&&this.isSelectedNodesAddress.splice(t,1),this.isNodeAllSelect(e)},isElementToNodeSelect:function(e){this.elementSelected=this.isSelectedElementsAddress[e].length,this.elementSelected===this.elementCount[e]&&(this.isAllNodesSelect=!0)},isNodeAllSelect:function(){this.nodeSelected=this.isSelectedNodesAddress.length,this.nodeSelected===this.nodeCount&&(this.isAllNodesSelect=!0)},isNodeSelected:function(e){var t=!1;return console.log("111选择节点: "+e+" self.isSelectedNodesAddress: "+this.isSelectedNodesAddress),-1!==this.isSelectedNodesAddress.indexOf(e)&&(t=!0),t},isNodeDisable:function(e){var t=this,s=!1;return l.a._isEmpty(t.groupInfo.models)||t.groupInfo.models.forEach(function(n){n.nodeAddress===e&&(s=!0,-1!==t.currentGroupArr.indexOf(e)&&t.currentGroupArr.push(e))}),s},selectAllNode:function(){console.log("1");var e=this;if(e.isAllNodesSelect)e.nodeSelected=0,e.isSelectedNodesAddress=[],e.isAllNodesSelect=!1;else{e.nodeSelected=e.nodeCount;var t=[];e.deviceList.forEach(function(s){-1!==e.currentGroupArr.indexOf(s.address)&&t.push(s.address)}),e.isSelectedNodesAddress=t,e.isAllNodesSelect=!0}var s=!0;e.deviceList.forEach(function(t){s=e.isAllElementsSelect[t.address]&&s}),e.deviceList.forEach(function(t,n){if(s)e.elementSelected=0,e.isSelectedElementsAddress[t.address]=[],e.isAllElementsSelect[t.address]=!1;else{e.elementSelected=e.elementCount[t.address];var o=e.isSelectedElementsAddress,i=[];e.deviceList[n].elements.forEach(function(e){i.push(e.address)}),o[t.address]=i,e.isSelectedElementsAddress=o,e.isAllElementsSelect[t.address]=!0}})},switchElements:function(e,t,s){if(!l.a._isEmpty(this.groupInfo.models)){var n=!0,i=!1,r=void 0;try{for(var c,a=o()(this.groupInfo.models);!(n=(c=a.next()).done);n=!0){var u=c.value;if(u.nodeAddress===e)return void console.log("选择节点："+e+"item： "+d()(u))}}catch(e){i=!0,r=e}finally{try{!n&&a.return&&a.return()}finally{if(i)throw r}}}this.selectedElementsAddress=this.isSelectedElementsAddress,console.log(" nodeAddress: "+s+" 选择 Elements: "+t+" self.isSelectedElementsAddress: "+d()(this.isSelectedElementsAddress)),l.a._isEmpty(this.selectedElementsAddress[s])&&(this.selectedElementsAddress[s]=[]);var f=this.selectedElementsAddress[s].length;if(this.elementSelectedArr=this.selectedElementsAddress[s],f>0){var h=this.elementSelectedArr.indexOf(t);-1===h?this.elementSelectedArr.push(t):this.elementSelectedArr.splice(h,1)}else console.log("12345"),this.elementSelectedArr.push(t);this.selectedElementsAddress[s]=this.elementSelectedArr,this.isSelectedElementsAddress={},this.isSelectedElementsAddress=this.selectedElementsAddress,console.log("更新 isSelectedElementsAddress: "+d()(this.isSelectedElementsAddress)),this.elementSelected=this.elementSelectedArr.length,this.elementSelected===this.elementCount[s]?(this.isAllElementsSelect[s]=!0,this.nodeAdd(s)):(this.isAllElementsSelect[s]=!1,this.nodedelete(s))},isElementSelected:function(e,t){var s=!1,n=[];return console.log(" nodeAddress: "+t+" 11111选择 Elements1: "+e+d()(this.isSelectedElementsAddress)),"{}"!==d()(this.isSelectedElementsAddress)&&(n=this.isSelectedElementsAddress[t],console.log("elementArr 长度："+n),l.a._isEmpty(n)||n.length>0&&-1!==n.indexOf(e)&&(s=!0)),s},meshMessageCallback:function(e){l.a.meshMessageCallback(e,this)},configModelSubscriptionCallback:function(e){var t=this;clearTimeout(t.groupAddNodeTimeOut),l.a.messageRemind(this.$t("deviceaddgroupsuc")),f.Indicator.close(),setTimeout(function(){t.hide()},500)},scanCallback:function(e){l.a.scanCallback(e,this)},connectCallback:function(e){l.a.connectCallback(e,this,!0)}},mounted:function(){var e=this,t=[];if(window.scanCallback=function(t){e.scanCallback(t)},window.connectCallback=function(t){e.connectCallback(t)},window.meshMessageCallback=function(t){e.meshMessageCallback(t)},e.connectContent=0,t=e.$store.state.deviceGroupList,console.log("FBY groupAddDevice groupList123: "+d()(t)),t.length<1);else{console.log("FBY groupAddDevice groupAllList: "+d()(t)),console.log("FBY groupAddDevice groupList: "+d()(e.groupList));var s=t.filter(function(t){return t.address===e.groupInfo.address});e.groupList=s,console.log("FBY groupAddDevice groupList: "+d()(e.groupList))}e.groupCount=e.groupList.length,e.deviceList=e.$store.state.homeDeviceList,e.deviceList.length,e.nodeCount=e.deviceList.length,e.deviceList.forEach(function(t){e.elementCount[t.address]=t.elements.length})},activated:function(){console.log("groupAddDevice activated 走了")},computed:{blueEnable:function(){return console.log("addDevice 页面蓝牙发生变化"+this.$store.state.blueInfo),this.$store.state.blueInfo},getGroupInfo:function(){return this.$store.state.groupInfo},getBlueEnable:function(){return this.blueEnable=this.$store.state.blueInfo,this.locationGranted=this.$store.state.locationGranted,this.locationEnabled=this.$store.state.locationEnabled,console.log("状态实时更新 blueEnable:"+this.blueEnable+"  locationGranted:"+this.locationGranted+"  locationEnabled:"+this.locationEnabled),this.blueEnable}}},p={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"content-box"},[s("div",{staticClass:"title-info"},[s("h4",{staticClass:"app-title"},[s("span",{on:{click:e.hide}},[s("i",{staticClass:"icon-left back"})]),e._v(e._s(e.$t("adddevice")))]),e._v(" "),s("span",{staticClass:"right-top-text",on:{click:function(t){return e.addGroupDevice()}}},[e._v(e._s(e.$t("submit")))])]),e._v(" "),s("div",{staticClass:"groups-header header"},[s("span",{staticClass:"header-title flex flex-ac flex-jcc"},[e._v(e._s(e.$t("groups")))])]),e._v(" "),s("div",{staticClass:"content-info flex-wrapper group-base"},[s("div",{staticClass:"overflow-touch"},e._l(e.groupList,function(t,n){return s("div",{key:n,staticClass:"item",on:{click:function(t){return t.target!==t.currentTarget?null:e.switchGroup(n)}}},[s("div",{staticClass:"item-icon-circle",on:{click:function(t){return t.stopPropagation(),e.switchGroup(n)}}},[s("i",{staticClass:"icon-groups"}),e._v(" "),s("span",[e._v(e._s(t.models.length))])]),e._v(" "),s("div",{staticClass:"item-name",on:{click:function(t){return t.stopPropagation(),e.switchGroup(n)}}},[s("span",[e._v(e._s(t.name))]),e._v(" "),s("span",{staticClass:"desc"},[e._v(e._s(e.getGroupDes(t.description))+e._s(t.address.toString(16)))])]),e._v(" "),s("div",{staticClass:"item-power-small",on:{click:function(t){return t.stopPropagation(),e.switchGroup(n)}}},[s("span",{staticClass:"span-radio",class:{active:e.isGroupSelected(t.address)},attrs:{"data-value":t.address}},[s("span")])])])}),0)]),e._v(" "),s("div",{staticClass:"groups-header header"},[s("span",{staticClass:"header-title flex flex-ac flex-jcc"},[e._v(e._s(e.$t("networknodes")))])]),e._v(" "),s("div",{staticClass:"content-info flex-wrapper node-base"},[s("div",{staticClass:"overflow-touch"},e._l(e.deviceList,function(t,n){return s("div",{key:n},[s("div",{staticClass:"node-first-base item",on:{click:function(s){return e.switchNodes(t.address,n)}}},[s("div",{staticClass:"item-icon-circle"},[s("i",{class:e.getIcon(t.tid)})]),e._v(" "),s("div",{staticClass:"item-name"},[s("span",[e._v(e._s(t.name))])]),e._v(" "),s("div",{staticClass:"item-power-small"},[s("span",{staticClass:"span-radio",class:[e.isNodeSelected(t.address)?"active":"",e.isNodeDisable(t.address)?"disable":""],attrs:{"data-value":t.address}},[s("span")])])]),e._v(" "),e._l(t.elements,function(n,o){return s("div",{key:o,staticClass:"node-second-base item",on:{click:function(s){return e.switchElements(t.address,n.address,t.address)}}},[s("div",{staticClass:"item-name elements"},[s("span",[e._v(e._s(n.name))])]),e._v(" "),s("div",{staticClass:"item-power-small"},[s("span",{staticClass:"span-radio",class:[e.isElementSelected(n.address,t.address)?"active":"",e.isNodeDisable(t.address)?"disable":""],attrs:{"data-value":t.address}},[s("span")])])])})],2)}),0)])])},staticRenderFns:[]};var v=s("VU/8")(h,p,!1,function(e){s("OaGn")},"data-v-5730758d",null);t.default=v.exports},"vIB/":function(e,t,s){"use strict";var n=s("O4g8"),o=s("kM2E"),i=s("880/"),r=s("hJx8"),c=s("/bQp"),d=s("94VQ"),l=s("e6n0"),a=s("PzxK"),u=s("dSzd")("iterator"),f=!([].keys&&"next"in[].keys()),h=function(){return this};e.exports=function(e,t,s,p,v,m,g){d(s,t,p);var S,A,E,_=function(e){if(!f&&e in b)return b[e];switch(e){case"keys":case"values":return function(){return new s(this,e)}}return function(){return new s(this,e)}},x=t+" Iterator",y="values"==v,G=!1,b=e.prototype,C=b[u]||b["@@iterator"]||v&&b[v],L=C||_(v),T=v?y?_("entries"):L:void 0,D="Array"==t&&b.entries||C;if(D&&(E=a(D.call(new e)))!==Object.prototype&&E.next&&(l(E,x,!0),n||"function"==typeof E[u]||r(E,u,h)),y&&C&&"values"!==C.name&&(G=!0,L=function(){return C.call(this)}),n&&!g||!f&&!G&&b[u]||r(b,u,L),c[t]=L,c[x]=h,v)if(S={values:y?L:_("values"),keys:m?L:_("keys"),entries:T},g)for(A in S)A in b||i(b,A,S[A]);else o(o.P+o.F*(f||G),t,S);return S}},xGkn:function(e,t,s){"use strict";var n=s("4mcu"),o=s("EGZi"),i=s("/bQp"),r=s("TcQ7");e.exports=s("vIB/")(Array,"Array",function(e,t){this._t=r(e),this._i=0,this._k=t},function(){var e=this._t,t=this._k,s=this._i++;return!e||s>=e.length?(this._t=void 0,o(1)):o(0,"keys"==t?s:"values"==t?e[s]:[s,e[s]])},"values"),i.Arguments=i.Array,n("keys"),n("values"),n("entries")},"xH/j":function(e,t,s){var n=s("hJx8");e.exports=function(e,t,s){for(var o in t)s&&e[o]?e[o]=t[o]:n(e,o,t[o]);return e}},zQR9:function(e,t,s){"use strict";var n=s("h65t")(!0);s("vIB/")(String,"String",function(e){this._t=String(e),this._i=0},function(){var e,t=this._t,s=this._i;return s>=t.length?{value:void 0,done:!0}:(e=n(t,s),this._i+=e.length,{value:e,done:!1})})}});
//# sourceMappingURL=1.7329ba4b4999e27c9af2.js.map