(function(e){function t(t){for(var r,o,i=t[0],s=t[1],u=t[2],d=0,f=[];d<i.length;d++)o=i[d],Object.prototype.hasOwnProperty.call(a,o)&&a[o]&&f.push(a[o][0]),a[o]=0;for(r in s)Object.prototype.hasOwnProperty.call(s,r)&&(e[r]=s[r]);l&&l(t);while(f.length)f.shift()();return c.push.apply(c,u||[]),n()}function n(){for(var e,t=0;t<c.length;t++){for(var n=c[t],r=!0,i=1;i<n.length;i++){var s=n[i];0!==a[s]&&(r=!1)}r&&(c.splice(t--,1),e=o(o.s=n[0]))}return e}var r={},a={index:0},c=[];function o(t){if(r[t])return r[t].exports;var n=r[t]={i:t,l:!1,exports:{}};return e[t].call(n.exports,n,n.exports,o),n.l=!0,n.exports}o.m=e,o.c=r,o.d=function(e,t,n){o.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},o.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},o.t=function(e,t){if(1&t&&(e=o(e)),8&t)return e;if(4&t&&"object"===typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(o.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var r in e)o.d(n,r,function(t){return e[t]}.bind(null,r));return n},o.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return o.d(t,"a",t),t},o.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},o.p="";var i=window["webpackJsonp"]=window["webpackJsonp"]||[],s=i.push.bind(i);i.push=t,i=i.slice();for(var u=0;u<i.length;u++)t(i[u]);var l=s;c.push([0,"chunk-vendors"]),n()})({0:function(e,t,n){e.exports=n("56d7")},"15de":function(e,t,n){},"1ec0":function(e,t,n){"use strict";n("9dcc")},2505:function(e,t,n){"use strict";n("d511")},3087:function(e,t,n){},"3aeb":function(e,t,n){"use strict";n("46b7")},"46b7":function(e,t,n){},"49fa":function(e,t,n){"use strict";n("3087")},"4b7e":function(e,t,n){},"4f2a":function(e,t,n){"use strict";n("7c4c")},"56cf":function(e,t,n){},"56d7":function(e,t,n){"use strict";n.r(t);n("e260"),n("e6cf"),n("cca6"),n("a79d");var r=n("7a23"),a={id:"lytheader"},c={id:"lytleftmenu"},o={id:"lyttopmenu"},i={id:"lytmain"},s={id:"lytfooter"};function u(e,t,n,u,l,d){var f=Object(r["y"])("Header"),b=Object(r["y"])("LeftMenu"),p=Object(r["y"])("TopMenu"),h=Object(r["y"])("Main"),g=Object(r["y"])("Footer");return Object(r["r"])(),Object(r["f"])(r["a"],null,[Object(r["g"])("header",a,[Object(r["i"])(f)]),Object(r["g"])("div",null,[Object(r["g"])("nav",c,[Object(r["i"])(b)]),Object(r["g"])("div",null,[Object(r["g"])("nav",o,[Object(r["i"])(p,{ip:l.ip,onChangeip:d.onchangeip},null,8,["ip","onChangeip"])]),Object(r["g"])("article",i,[Object(r["i"])(h,{ip:l.ip},null,8,["ip"])])])]),Object(r["g"])("footer",s,[Object(r["i"])(g)])],64)}var l=n("9b19"),d=n.n(l),f=function(e){return Object(r["u"])("data-v-0bbc73bf"),e=e(),Object(r["s"])(),e},b=f((function(){return Object(r["g"])("img",{src:d.a,id:"logo",alt:"logo"},null,-1)})),p=f((function(){return Object(r["g"])("h1",{id:"title"},[Object(r["g"])("span",null,"REN"),Object(r["h"])("ergetic")],-1)})),h=[b,p];function g(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("header",null,h)}var O={name:"Header"},j=(n("e619"),n("6b0d")),m=n.n(j);const v=m()(O,[["render",g],["__scopeId","data-v-0bbc73bf"]]);var w=v,y=(n("b0c0"),["value"]);function A(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("select",{name:"screen",id:"screen",onChange:t[0]||(t[0]=function(e){return o.goToScreen(e)})},[(Object(r["r"])(!0),Object(r["f"])(r["a"],null,Object(r["x"])(c.paths,(function(e){return Object(r["r"])(),Object(r["f"])("option",{value:e.name,key:e.name},Object(r["A"])(e.name),9,y)})),128))],32)}var C=n("b85c"),k=(n("ac1f"),n("5319"),n("caad"),n("2532"),n("1da1")),_=(n("99af"),n("96cf"),n("68ad")),I=n.n(_),N=n("bc3a"),U=n.n(N);U.a.defaults.headers.post["Access-Control-Allow-Origin"]="*",U.a.defaults.headers.post["Access-Control-Allow-Credentials"]="true",U.a.defaults.headers.post["Access-Control-Allow-Methods"]="GET, POST, DELETE, OPTIONS",U.a.defaults.headers.post["Access-Control-Allow-Headers"]="DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type, Origin",U.a.defaults.headers.post["Content-Type"]="application/json",U.a.defaults.headers.delete["Access-Control-Allow-Origin"]="*",U.a.defaults.headers.delete["Access-Control-Allow-Credentials"]="true",U.a.defaults.headers.delete["Access-Control-Allow-Methods"]="GET, POST, DELETE, OPTIONS",U.a.defaults.headers.delete["Access-Control-Allow-Headers"]="DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type, Origin",U.a.defaults.headers.delete["Content-Type"]="application/json";var T=void 0;T="http://front-ren-prototype.apps.paas-dev.psnc.pl/auth";var x={url:T,realm:"realm-renergetic",clientId:"renergetic-app"},R=new I.a(x),S={data:R,info:{app:"renergetic-app",realm:"realm-renergetic",url:T,clientId:void 0},install:function(e,t){var n=this;return Object(k["a"])(regeneratorRuntime.mark((function r(){return regeneratorRuntime.wrap((function(r){while(1)switch(r.prev=r.next){case 0:R.init({onLoad:"login-required",checkLoginIframe:!1}).then(function(){var r=Object(k["a"])(regeneratorRuntime.mark((function r(a){var c,o,i;return regeneratorRuntime.wrap((function(r){while(1)switch(r.prev=r.next){case 0:if(e.config.globalProperties.authenticated=a,R.authenticated=!!a,!a||void 0==R.resourceAccess["renergetic-app"]){r.next=22;break}c=Object(C["a"])(R.resourceAccess["renergetic-app"].roles),r.prev=4,c.s();case 6:if((o=c.n()).done){r.next=14;break}if(i=o.value,-1===i.indexOf("administrator")){r.next=12;break}return r.next=11,n.getClientId();case 11:n.info.clientId=r.sent.data[0].id;case 12:r.next=6;break;case 14:r.next=19;break;case 16:r.prev=16,r.t0=r["catch"](4),c.e(r.t0);case 19:return r.prev=19,c.f(),r.finish(19);case 22:e.component("keycloak",R),e.use(t).mount("#app");case 24:case"end":return r.stop()}}),r,null,[[4,16,19,22]])})));return function(e){return r.apply(this,arguments)}}()).catch((function(e){console.log("failed to initialize ",e)})),e.component("keycloak",R);case 2:case"end":return r.stop()}}),r)})))()},logout:function(){R.logout({redirectUri:window.location.origin}),localStorage.setItem("data",null)},getClientRoles:function(){var e=this;return Object(k["a"])(regeneratorRuntime.mark((function t(){var n;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return n={headers:{Authorization:"Bearer "+R.token}},t.abrupt("return",U.a.get("".concat(e.info.url,"/admin/realms/").concat(e.info.realm,"/clients/").concat(e.info.clientId,"/roles"),n).then((function(e){return e.data})).catch((function(t){console.warn(t.message),console.warn("No se puede conectar a ".concat(e.info.url))})));case 2:case"end":return t.stop()}}),t)})))()},getClientId:function(){var e=this;return Object(k["a"])(regeneratorRuntime.mark((function t(){var n;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return n={headers:{Authorization:"Bearer "+R.token}},t.abrupt("return",U.a.get("".concat(e.info.url,"/admin/realms/").concat(e.info.realm,"/clients?clientId=").concat(e.info.app),n));case 2:case"end":return t.stop()}}),t)})))()},getUsers:function(){var e=this;return Object(k["a"])(regeneratorRuntime.mark((function t(){var n;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return n={headers:{Authorization:"Bearer "+R.token},params:{clientId:e.info.app}},t.abrupt("return",U.a.get("".concat(e.info.url,"/admin/realms/").concat(e.info.realm,"/users"),n).then(function(){var t=Object(k["a"])(regeneratorRuntime.mark((function t(n){var r,a,c,o;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(!(n.data&&n.data.length>0)){t.next=28;break}r=Array(),a=Object(C["a"])(n.data),t.prev=3,a.s();case 5:if((c=a.n()).done){t.next=19;break}return o=c.value,t.t0=r,t.t1=o.id,t.t2=o.username,t.t3="".concat(o.firstName&&o.lastName?"".concat(o.firstName," ").concat(o.lastName):""),t.t4=o.email,t.next=14,e.getUserRoles(o.id);case 14:t.t5=t.sent,t.t6={id:t.t1,username:t.t2,name:t.t3,email:t.t4,roles:t.t5},t.t0.push.call(t.t0,t.t6);case 17:t.next=5;break;case 19:t.next=24;break;case 21:t.prev=21,t.t7=t["catch"](3),a.e(t.t7);case 24:return t.prev=24,a.f(),t.finish(24);case 27:return t.abrupt("return",r);case 28:case"end":return t.stop()}}),t,null,[[3,21,24,27]])})));return function(e){return t.apply(this,arguments)}}()).catch((function(t){console.warn(t.message),console.warn("No se puede conectar a ".concat(e.info.url))})));case 2:case"end":return t.stop()}}),t)})))()},getUserRoles:function(e){var t=this;return Object(k["a"])(regeneratorRuntime.mark((function n(){var r;return regeneratorRuntime.wrap((function(n){while(1)switch(n.prev=n.next){case 0:return r={headers:{Authorization:"Bearer "+R.token}},n.abrupt("return",U.a.get("".concat(t.info.url,"/admin/realms/").concat(t.info.realm,"/users/").concat(e,"/role-mappings/clients/").concat(t.info.clientId),r).then((function(e){return e.data})).catch((function(e){console.warn(e.message),console.warn("No se puede conectar a ".concat(t.info.url))})));case 2:case"end":return n.stop()}}),n)})))()},assignRolesToUser:function(e,t){var n=this,r={headers:{Authorization:"Bearer "+R.token},Accept:"*/*","Content-Type":"application/json"};U.a.post("".concat(this.info.url,"/admin/realms/").concat(this.info.realm,"/users/").concat(e,"/role-mappings/clients/").concat(this.info.clientId),t,r).then((function(e){return e})).catch((function(e){console.warn(e.message),console.warn("No se puede conectar a ".concat(n.info.url))}))},unAssignRolesToUser:function(e,t){var n=this,r={headers:{Authorization:"Bearer "+R.token,Accept:"*/*","Content-Type":"application/json"},data:t};U.a.delete("".concat(this.info.url,"/admin/realms/").concat(this.info.realm,"/users/").concat(e,"/role-mappings/clients/").concat(this.info.clientId),r).then((function(e){return e})).catch((function(e){console.warn(e.message),console.warn("No se puede conectar a ".concat(n.info.url))}))},createUser:function(e){var t=this;return Object(k["a"])(regeneratorRuntime.mark((function n(){var r;return regeneratorRuntime.wrap((function(n){while(1)switch(n.prev=n.next){case 0:return r={headers:{Authorization:"Bearer "+R.token},Accept:"*/*","Content-Type":"application/json"},n.abrupt("return",U.a.post("".concat(t.info.url,"/admin/realms/").concat(t.info.realm,"/users"),e,r));case 2:case"end":return n.stop()}}),n)})))()},deleteUser:function(e){var t=this;return Object(k["a"])(regeneratorRuntime.mark((function n(){var r;return regeneratorRuntime.wrap((function(n){while(1)switch(n.prev=n.next){case 0:return r={headers:{Authorization:"Bearer "+R.token},Accept:"*/*","Content-Type":"application/json"},n.abrupt("return",U.a.delete("".concat(t.info.url,"/admin/realms/").concat(t.info.realm,"/users/").concat(e),r));case 2:case"end":return n.stop()}}),n)})))()}},M={name:"TopMenu",props:{ip:String},data:function(){return{paths:[]}},methods:{goToScreen:function(e){this.$router.replace({name:e.target.value})},getPaths:function(){var e,t=Object(C["a"])(this.$router.getRoutes());try{for(t.s();!(e=t.n()).done;){var n=e.value;if(void 0!=S.data.resourceAccess["renergetic-app"]){var r,a=Object(C["a"])(S.data.resourceAccess["renergetic-app"].roles);try{for(a.s();!(r=a.n()).done;){var c=r.value;if(void 0!=n.meta.roles&&n.meta.roles.includes(c)&&void 0!=n.meta.requiresAuth){this.paths.push({name:n.name,url:n.path});break}}}catch(o){a.e(o)}finally{a.f()}}void 0==n.meta.roles&&void 0!=n.meta.requiresAuth&&this.paths.push({name:n.name,url:n.path})}}catch(o){t.e(o)}finally{t.f()}}},mounted:function(){var e=this;this.$watch((function(){return e.$route}),(function(e){document.getElementById("screen").value=e.name})),S.ready?this.getPaths():setTimeout((function(){return e.getPaths()}),200)}};n("2505");const L=m()(M,[["render",A],["__scopeId","data-v-133cca69"]]);var P=L;function E(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("div")}var B={name:"Footer"};n("6672");const q=m()(B,[["render",E],["__scopeId","data-v-7a5ae921"]]);var z=q;function H(e,t,n,a,c,o){var i=Object(r["y"])("router-view");return Object(r["r"])(),Object(r["d"])(i)}var F={name:"Main",props:{ip:String}};const D=m()(F,[["render",H]]);var V=D;function $(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])(r["a"],null,[Object(r["g"])("div",{onClick:t[0]||(t[0]=function(){return o.logout&&o.logout.apply(o,arguments)})},"Logout"),Object(r["g"])("div",{onClick:t[1]||(t[1]=function(){return o.showLeftMenu&&o.showLeftMenu.apply(o,arguments)})},"Click to "+Object(r["A"])(c.showMenu?"hide":"show")+" left menu",1)],64)}var X=n("1344"),G=Object(X["a"])(),K=G,W={name:"Footer",data:function(){return{showMenu:!1}},methods:{showLeftMenu:function(){this.showMenu=!this.showMenu,K.emit("show",{view:"leftmenu",show:this.showMenu})},logout:function(){S.logout({redirectUri:window.location.origin})}}};n("a2b6");const J=m()(W,[["render",$],["__scopeId","data-v-1c24fea3"]]);var Q=J,Y={name:"App",data:function(){return{keycloak:S,ip:"http://127.0.0.1/api/islands"}},components:{Header:w,TopMenu:P,LeftMenu:z,Main:V,Footer:Q},mounted:function(){K.on("show",this.changeVisibility),this.changeVisibility({view:"leftmenu",show:!1})},methods:{onchangeip:function(e){this.ip=e},changeVisibility:function(e){console.log("#app .".concat(e.view)),document.querySelector("#lyt".concat(e.view.toLowerCase())).style="display: ".concat(e.show?"flex":"none")}}};n("3aeb");const Z=m()(Y,[["render",u],["__scopeId","data-v-cbbd5a40"]]);var ee=Z,te=(n("d3b7"),n("25f0"),n("fb6a"),n("6c02")),ne={id:"main"};function re(e,t,n,a,c,o){var i=Object(r["y"])("router-link");return Object(r["r"])(),Object(r["f"])("article",ne,[Object(r["g"])("header",null,[Object(r["g"])("h2",null,"Bienvenido "+Object(r["A"])(c.user),1)]),(Object(r["r"])(!0),Object(r["f"])(r["a"],null,Object(r["x"])(c.paths,(function(e){return Object(r["r"])(),Object(r["f"])("div",{key:e.name},[Object(r["i"])(i,{to:e.url},{default:Object(r["E"])((function(){return[Object(r["h"])(Object(r["A"])(e.name),1)]})),_:2},1032,["to"])])})),128))])}var ae={name:"Login",data:function(){return{user:"",paths:[]}},methods:{getName:function(){void 0!=S.data&&void 0!=S.data.idTokenParsed&&(this.user=S.data.idTokenParsed.name)},getPaths:function(){var e,t=Object(C["a"])(this.$router.getRoutes());try{for(t.s();!(e=t.n()).done;){var n=e.value;if(void 0!=S.data.resourceAccess["renergetic-app"]){var r,a=Object(C["a"])(S.data.resourceAccess["renergetic-app"].roles);try{for(a.s();!(r=a.n()).done;){var c=r.value;if(void 0!=n.meta.roles&&n.meta.roles.includes(c)){this.paths.push({name:n.name,url:n.path});break}}}catch(o){a.e(o)}finally{a.f()}}void 0==n.meta.roles&&void 0!=n.meta.requiresAuth&&this.paths.push({name:n.name,url:n.path})}}catch(o){t.e(o)}finally{t.f()}}},mounted:function(){var e=this;S.ready?(this.getName(),this.getPaths()):setTimeout((function(){e.getName(),e.getPaths()}),200)}};n("49fa");const ce=m()(ae,[["render",re],["__scopeId","data-v-6a2bbf61"]]);var oe=ce,ie=function(e){return Object(r["u"])("data-v-06256c2a"),e=e(),Object(r["s"])(),e},se={id:"main"},ue=ie((function(){return Object(r["g"])("header",null,[Object(r["g"])("h2",null,"List islands")],-1)}));function le(e,t,n,a,c,o){var i=Object(r["y"])("NewIsland"),s=Object(r["y"])("SeeIslands");return Object(r["r"])(),Object(r["f"])("article",se,[Object(r["g"])("section",null,[Object(r["g"])("header",null,[Object(r["g"])("h2",null,Object(r["A"])(-1==c.show_builds?"Create island":"Add build"),1)]),Object(r["i"])(i,{ip:c.ip,onEventAdd:o.eventAddTag,show_builds:c.show_builds},null,8,["ip","onEventAdd","show_builds"])]),Object(r["g"])("section",null,[ue,Object(r["i"])(s,{ip:c.ip,onEventBuilds:o.changeShowBuilds,ref:"seeTag",show_builds:c.show_builds},null,8,["ip","onEventBuilds","show_builds"])])])}var de=function(e){return Object(r["u"])("data-v-bb931d6c"),e=e(),Object(r["s"])(),e},fe=de((function(){return Object(r["g"])("br",null,null,-1)})),be=["placeholder"],pe=de((function(){return Object(r["g"])("br",null,null,-1)}));function he(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("div",null,[Object(r["F"])(Object(r["g"])("input",{type:"text","onUpdate:modelValue":t[0]||(t[0]=function(e){return c.inputName=e}),placeholder:"Name"},null,512),[[r["C"],c.inputName]]),fe,Object(r["F"])(Object(r["g"])("input",{type:"text","onUpdate:modelValue":t[1]||(t[1]=function(e){return c.inputLocation=e}),placeholder:-1==n.show_builds?"Location":"Description",onKeyup:t[2]||(t[2]=Object(r["G"])((function(){return o.addIsland&&o.addIsland.apply(o,arguments)}),["enter"]))},null,40,be),[[r["C"],c.inputLocation]]),pe,Object(r["g"])("button",{onClick:t[3]||(t[3]=function(){return o.addIsland&&o.addIsland.apply(o,arguments)})},"Create")])}n("a9e3");var ge={name:"NewIsland",props:{ip:String,show_builds:Number},data:function(){return{inputName:"",inputLocation:""}},methods:{addIsland:function(){var e,t=this;this.inputName&&this.inputLocation&&(U.a.defaults.headers.post["Access-Control-Allow-Origin"]="*",U.a.defaults.headers.post["Access-Control-Allow-Credentials"]="true",U.a.defaults.headers.post["Access-Control-Allow-Methods"]="GET, POST, OPTIONS",U.a.defaults.headers.post["Access-Control-Allow-Headers"]="DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,apikey",e=-1==this.show_builds?{name:this.inputName,location:this.inputLocation}:{name:this.inputName,description:this.inputLocation,idIsland:this.show_builds},U.a.post(this.ip+(-1==this.show_builds?"/api-postgre/1.0/api/islands":"/api-buildings/1.0/api/buildings"),e).then((function(){t.inputName="",t.inputLocation="",t.$emit("event-add")})))}}};n("1ec0");const Oe=m()(ge,[["render",he],["__scopeId","data-v-bb931d6c"]]);var je=Oe,me=(n("a4d3"),n("e01a"),function(e){return Object(r["u"])("data-v-3fc0318f"),e=e(),Object(r["s"])(),e}),ve={key:0},we=me((function(){return Object(r["g"])("caption",{style:{display:"none"}},"Listado de islas",-1)})),ye=me((function(){return Object(r["g"])("thead",null,[Object(r["g"])("tr",null,[Object(r["g"])("th",{scope:"col"},"Server data"),Object(r["g"])("th",{scope:"col"},"Island"),Object(r["g"])("th",{scope:"col"},"Location")]),Object(r["g"])("tr",null,[Object(r["g"])("th",{colspan:"3",scope:"row"},[Object(r["g"])("hr")])])],-1)})),Ae=["id"],Ce=["onClick"],ke={colspan:"3"},_e={key:1},Ie=me((function(){return Object(r["g"])("caption",{style:{display:"none"}},"Listado de islas",-1)})),Ne=me((function(){return Object(r["g"])("thead",null,[Object(r["g"])("tr",null,[Object(r["g"])("th",{scope:"col"},"Building"),Object(r["g"])("th",{scope:"col"},"Description"),Object(r["g"])("th",{scope:"col"},"Location")]),Object(r["g"])("tr",null,[Object(r["g"])("th",{colspan:"3",scope:"row"},[Object(r["g"])("hr")])])],-1)})),Ue={colspan:"3"};function Te(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("div",null,[-1==n.show_builds?(Object(r["r"])(),Object(r["f"])("table",ve,[we,ye,Object(r["g"])("tbody",null,[(Object(r["r"])(!0),Object(r["f"])(r["a"],null,Object(r["x"])(c.islands,(function(e){return Object(r["r"])(),Object(r["f"])("tr",{key:e.id,id:"isle"+e.id},[Object(r["g"])("td",null,[Object(r["g"])("button",{class:"show_builds",onClick:function(t){return o.showBuilds(e.id)}},"Show Buildings",8,Ce)]),Object(r["g"])("td",null,Object(r["A"])(e.name),1),Object(r["g"])("td",null,Object(r["A"])(e.location),1)],8,Ae)})),128)),Object(r["g"])("tr",null,[Object(r["g"])("td",ke,[Object(r["g"])("button",{onClick:t[0]||(t[0]=function(){return o.listIslands&&o.listIslands.apply(o,arguments)})},"Update table")])])])])):(Object(r["r"])(),Object(r["f"])("table",_e,[Ie,Ne,Object(r["g"])("tbody",null,[(Object(r["r"])(!0),Object(r["f"])(r["a"],null,Object(r["x"])(c.builds,(function(e){return Object(r["r"])(),Object(r["f"])("tr",{key:e.id},[Object(r["g"])("td",null,Object(r["A"])(e.name),1),Object(r["g"])("td",null,Object(r["A"])(e.description),1),Object(r["g"])("td",null,Object(r["A"])(e.idIsland),1)])})),128)),Object(r["g"])("tr",null,[Object(r["g"])("td",Ue,[Object(r["g"])("button",{onClick:t[1]||(t[1]=function(e){return o.hideBuilds()})},"Go Back")])])])]))])}var xe={name:"SeeIslands",props:{ip:String,show_builds:Number},data:function(){return{islands:[],builds:[]}},methods:{listIslands:function(){var e=this;U.a.defaults.headers.get["Access-Control-Allow-Origin"]="*",U.a.defaults.headers.get["Access-Control-Allow-Credentials"]="true",U.a.defaults.headers.get["Access-Control-Allow-Methods"]="GET, POST, OPTIONS",U.a.defaults.headers.get["Access-Control-Allow-Headers"]="DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,apikey",-1==this.show_builds?U.a.get(this.ip+"/api-postgre/1.0/api/islands").then((function(t){return e.islands=t.data})).catch((function(t){console.warn(t.message),console.warn("No se puede conectar a ".concat(e.ip)),e.islands=[]})):U.a.get(this.ip+"/api-buildings/1.0/api/buildings",{params:{islandId:this.show_builds}}).then((function(t){return e.builds=t.data})).catch((function(t){console.warn(t.message),console.warn("No se puede conectar a ".concat(e.ip)),e.builds=[]}))},showBuilds:function(e){var t=this;U.a.defaults.headers.get["Access-Control-Allow-Origin"]="*",U.a.defaults.headers.get["Access-Control-Allow-Credentials"]="true",U.a.defaults.headers.get["Access-Control-Allow-Methods"]="GET, POST, OPTIONS",U.a.defaults.headers.get["Access-Control-Allow-Headers"]="DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,apikey",this.$emit("event-builds",e),U.a.get(this.ip+"/api-buildings/1.0/api/buildings",{params:{islandId:e}}).then((function(e){return t.builds=e.data})).catch((function(e){console.warn(e.message),console.warn("No se puede conectar a ".concat(t.ip)),t.builds=[]}))},hideBuilds:function(){this.$emit("event-builds",-1)}},mounted:function(){this.listIslands()},watch:{ip:function(){this.listIslands()}}};n("7172");const Re=m()(xe,[["render",Te],["__scopeId","data-v-3fc0318f"]]);var Se=Re,Me={name:"Islands",data:function(){return{ip:location.origin,show_builds:-1}},components:{NewIsland:je,SeeIslands:Se},methods:{eventAddTag:function(){this.$refs.seeTag.listIslands()},changeShowBuilds:function(e){this.show_builds=e},resizeMain:function(){}},mounted:function(){this.resizeMain(),window.addEventListener("resize",this.resizeMain)},unmounted:function(){window.removeEventListener("resize",this.resizeMain)}};n("af96");const Le=m()(Me,[["render",le],["__scopeId","data-v-06256c2a"]]);var Pe=Le,Ee={id:"heatdemand"},Be=["src"];function qe(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("article",Ee,[Object(r["g"])("iframe",{title:"graph",src:c.graphicUrl},null,8,Be)])}var ze={data:function(){return{graphicUrl:"http://grafana-ren-prototype.apps.paas-dev.psnc.pl/d-solo/nq5m1s0nk/renergetic-forecast?orgId=1&from=1634427441815&to=1636724258711&theme=light&panelId=2"}}};n("4f2a");const He=m()(ze,[["render",qe],["__scopeId","data-v-425c4a04"]]);var Fe=He;function De(e,t){return Object(r["r"])(),Object(r["f"])("h1",null,"Oops, it looks like the page you're looking for is forbidden.")}const Ve={},$e=m()(Ve,[["render",De]]);var Xe=$e;function Ge(e,t){return Object(r["r"])(),Object(r["f"])("h1",null,"Oops, it looks like the page you're looking for doesn't exist.")}const Ke={},We=m()(Ke,[["render",Ge]]);var Je=We,Qe=function(e){return Object(r["u"])("data-v-b9a49994"),e=e(),Object(r["s"])(),e},Ye={id:"main"},Ze=Qe((function(){return Object(r["g"])("header",null,[Object(r["g"])("h2",null,"Create user")],-1)})),et=Qe((function(){return Object(r["g"])("header",null,[Object(r["g"])("h2",null,"List users")],-1)}));function tt(e,t,n,a,c,o){var i=Object(r["y"])("NewUser"),s=Object(r["y"])("SeeUsers");return Object(r["r"])(),Object(r["f"])("article",Ye,[Object(r["g"])("section",null,[Ze,Object(r["i"])(i,{onEventAdd:o.eventAddTag},null,8,["onEventAdd"])]),Object(r["g"])("section",null,[et,Object(r["i"])(s,{ref:"seeTag"},null,512)])])}n("a15b");var nt=function(e){return Object(r["u"])("data-v-e531e9ba"),e=e(),Object(r["s"])(),e},rt={key:0},at=nt((function(){return Object(r["g"])("caption",{style:{display:"none"}},"Listado de islas",-1)})),ct=nt((function(){return Object(r["g"])("thead",null,[Object(r["g"])("tr",null,[Object(r["g"])("th",{scope:"col"},"Username"),Object(r["g"])("th",{scope:"col"},"Name"),Object(r["g"])("th",{scope:"col"},"Roles")]),Object(r["g"])("tr",null,[Object(r["g"])("th",{colspan:"3",scope:"row"},[Object(r["g"])("hr")])])],-1)})),ot=["id"],it=["onClick"],st=["onClick"],ut={colspan:"4"},lt={key:1},dt={colspan:"2"},ft=["onClick"],bt={key:0},pt=["value"],ht={colspan:"2"};function gt(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("div",null,[c.edit_user?(Object(r["r"])(),Object(r["f"])("table",lt,[Object(r["g"])("thead",null,[Object(r["g"])("th",dt,Object(r["A"])("".concat(c.user.username,"'s roles")),1)]),Object(r["g"])("tbody",null,[(Object(r["r"])(!0),Object(r["f"])(r["a"],null,Object(r["x"])(c.user.roles,(function(e){return Object(r["r"])(),Object(r["f"])("tr",{key:e},[Object(r["g"])("td",null,Object(r["A"])(e),1),Object(r["g"])("td",null,[Object(r["g"])("button",{class:"delete_user",onClick:function(t){return o.quitRole(e)}},"Quit",8,ft)])])})),128)),c.available_roles.length>0?(Object(r["r"])(),Object(r["f"])("tr",bt,[Object(r["g"])("td",null,[Object(r["g"])("select",{name:"role_select",id:"role",onChange:t[1]||(t[1]=function(e){return o.selectRole(e.target.value)})},[(Object(r["r"])(!0),Object(r["f"])(r["a"],null,Object(r["x"])(c.available_roles,(function(e){return Object(r["r"])(),Object(r["f"])("option",{value:e.name,key:e.name},Object(r["A"])(e.name),9,pt)})),128))],32)]),Object(r["g"])("td",null,[Object(r["g"])("button",{class:"edit_user",onClick:t[2]||(t[2]=function(){return o.addRole&&o.addRole.apply(o,arguments)})},"Add")])])):Object(r["e"])("",!0),Object(r["g"])("tr",null,[Object(r["g"])("td",ht,[Object(r["g"])("button",{onClick:t[3]||(t[3]=function(e){return o.editUser(void 0)})},"Back")])])])])):(Object(r["r"])(),Object(r["f"])("table",rt,[at,ct,Object(r["g"])("tbody",null,[(Object(r["r"])(!0),Object(r["f"])(r["a"],null,Object(r["x"])(c.users,(function(e){return Object(r["r"])(),Object(r["f"])("tr",{key:e.id,id:"user"+e.id},[Object(r["g"])("td",null,Object(r["A"])(e.username),1),Object(r["g"])("td",null,Object(r["A"])(e.name),1),Object(r["g"])("td",null,Object(r["A"])(e.roles.join(", ")),1),Object(r["g"])("td",null,[Object(r["g"])("button",{class:"edit_user",onClick:function(t){return o.editUser(e)}},"Edit",8,it)]),Object(r["g"])("td",null,[Object(r["g"])("button",{class:"delete_user",onClick:function(t){return o.delUser(e)}},"Delete",8,st)])],8,ot)})),128)),Object(r["g"])("tr",null,[Object(r["g"])("td",ut,[Object(r["g"])("button",{onClick:t[0]||(t[0]=function(){return o.getUsers&&o.getUsers.apply(o,arguments)})},"Update table")])])])]))])}n("a434");var Ot={name:"SeeUsers",data:function(){return{edit_user:!1,user:void 0,available_roles:[],selected_role:void 0,users:[]}},methods:{getUsers:function(){var e=this;return Object(k["a"])(regeneratorRuntime.mark((function t(){var n,r,a,c,o,i,s;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return e.users=[],t.next=3,S.getUsers();case 3:e.users=t.sent,n=Object(C["a"])(e.users);try{for(n.s();!(r=n.n()).done;){a=r.value,c=[],o=Object(C["a"])(a.roles);try{for(o.s();!(i=o.n()).done;)s=i.value,c.push(s.name)}catch(u){o.e(u)}finally{o.f()}a.roles=c}}catch(u){n.e(u)}finally{n.f()}case 6:case"end":return t.stop()}}),t)})))()},editUser:function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:void 0;this.user=e,void 0!=this.user?(this.getAvailableRoles(),this.edit_user=!0):(this.getUsers(),this.edit_user=!1)},getUser:function(e){var t,n=Object(C["a"])(this.users);try{for(n.s();!(t=n.n()).done;){var r=t.value;if(r.id==e)return r}}catch(a){n.e(a)}finally{n.f()}},delUser:function(){var e=this,t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:void 0;void 0!=t&&S.deleteUser(t.id).then((function(){return e.getUsers()}))},getAvailableRoles:function(){var e=this;return Object(k["a"])(regeneratorRuntime.mark((function t(){var n,r,a,c;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return e.available_roles=[],t.next=3,S.getClientRoles();case 3:n=t.sent,r=Object(C["a"])(n);try{for(r.s();!(a=r.n()).done;)c=a.value,e.user.roles.includes(c.name)||e.available_roles.push(c)}catch(o){r.e(o)}finally{r.f()}e.available_roles.length>0&&(e.selected_role=e.available_roles[0].name);case 7:case"end":return t.stop()}}),t)})))()},addRole:function(){var e,t=Object(C["a"])(this.available_roles);try{for(t.s();!(e=t.n()).done;){var n=e.value;if(n.name==this.selected_role){S.assignRolesToUser(this.user.id,Array(n)),this.user.roles.push(n.name),this.available_roles.splice(this.available_roles.indexOf(n),1);break}}}catch(r){t.e(r)}finally{t.f()}},quitRole:function(e){var t=this;return Object(k["a"])(regeneratorRuntime.mark((function n(){var r,a,c;return regeneratorRuntime.wrap((function(n){while(1)switch(n.prev=n.next){case 0:return n.t0=C["a"],n.next=3,S.getClientRoles();case 3:n.t1=n.sent,r=(0,n.t0)(n.t1);try{for(r.s();!(a=r.n()).done;)c=a.value,c.name==e&&(S.unAssignRolesToUser(t.user.id,Array(c)),t.user.roles.splice(t.user.roles.indexOf(c.name),1))}catch(o){r.e(o)}finally{r.f()}case 6:case"end":return n.stop()}}),n)})))()},selectRole:function(e){this.selected_role=e}},mounted:function(){this.getUsers()}};n("6050");const jt=m()(Ot,[["render",gt],["__scopeId","data-v-e531e9ba"]]);var mt=jt,vt=function(e){return Object(r["u"])("data-v-0d57ab45"),e=e(),Object(r["s"])(),e},wt=vt((function(){return Object(r["g"])("br",null,null,-1)})),yt=vt((function(){return Object(r["g"])("br",null,null,-1)})),At=vt((function(){return Object(r["g"])("br",null,null,-1)})),Ct=vt((function(){return Object(r["g"])("br",null,null,-1)})),kt=vt((function(){return Object(r["g"])("br",null,null,-1)}));function _t(e,t,n,a,c,o){return Object(r["r"])(),Object(r["f"])("div",null,[Object(r["F"])(Object(r["g"])("input",{type:"text","onUpdate:modelValue":t[0]||(t[0]=function(e){return c.username=e}),placeholder:"Username*"},null,512),[[r["C"],c.username]]),wt,Object(r["F"])(Object(r["g"])("input",{type:"text","onUpdate:modelValue":t[1]||(t[1]=function(e){return c.firstName=e}),placeholder:"First Name"},null,512),[[r["C"],c.firstName]]),yt,Object(r["F"])(Object(r["g"])("input",{type:"text","onUpdate:modelValue":t[2]||(t[2]=function(e){return c.lastName=e}),placeholder:"Last Name"},null,512),[[r["C"],c.lastName]]),At,Object(r["F"])(Object(r["g"])("input",{type:"text","onUpdate:modelValue":t[3]||(t[3]=function(e){return c.email=e}),placeholder:"Email"},null,512),[[r["C"],c.email]]),Ct,Object(r["F"])(Object(r["g"])("input",{type:"text","onUpdate:modelValue":t[4]||(t[4]=function(e){return c.password=e}),placeholder:"Password*",onKeyup:t[5]||(t[5]=Object(r["G"])((function(){return o.addUser&&o.addUser.apply(o,arguments)}),["enter"]))},null,544),[[r["C"],c.password]]),kt,Object(r["g"])("button",{onClick:t[6]||(t[6]=function(){return o.addUser&&o.addUser.apply(o,arguments)})},"Create")])}var It={name:"NewUser",data:function(){return{username:"",firstName:"",lastName:"",email:"",password:""}},methods:{addUser:function(){var e=this;if(""!=this.username&&""!=this.password){var t={enabled:!0,attributes:{},groups:[],username:this.username?this.username:"",firstName:this.firstName?this.firstName:"",lastName:this.lastName?this.lastName:"",email:this.email?this.email:"",emailVerified:"false",credentials:[{type:"password",value:this.password,temporary:!1}]};S.createUser(t).then((function(){e.username="",e.firstName="",e.lastName="",e.email="",e.password="",e.$emit("event-add")}))}}}};n("c515");const Nt=m()(It,[["render",_t],["__scopeId","data-v-0d57ab45"]]);var Ut=Nt,Tt={name:"Administration",components:{SeeUsers:mt,NewUser:Ut},data:function(){return{}},methods:{eventAddTag:function(){this.$refs.seeTag.getUsers()}}};n("91b0");const xt=m()(Tt,[["render",tt],["__scopeId","data-v-b9a49994"]]);var Rt=xt,St=function(){return[{path:"/",name:"Home",component:oe,meta:{requiresAuth:!0}},{path:"/islands",name:"Islands",component:Pe,meta:{requiresAuth:!0}},{path:"/graphs",name:"HeatDemand",component:Fe,meta:{requiresAuth:!0,roles:["manager","administrator"]}},{path:"/administration",name:"Administration",component:Rt,meta:{requiresAuth:!0,roles:["administrator"]}},{path:"/forbidden",name:"Forbidden",component:Xe},{path:"/:catchAll(.*)",component:Je}]};function Mt(e,t){if(!t||0==t.length)return!0;for(var n=0;n<t.length;n++)if(-1!==e.indexOf(t[n]))return!0;return!1}var Lt=function(e){var t=Object(te["a"])({history:Object(te["b"])(),routes:St()});return t.beforeEach((function(t,n,r){var a=window.location.toString(),c=e.component("keycloak");t.meta.requiresAuth?c.authenticated?void 0==t.meta.roles||void 0!=c.resourceAccess["renergetic-app"]&&Mt(c.resourceAccess["renergetic-app"].roles,t.meta.roles)?r():r({name:"Forbidden"}):c.login({redirectUri:a.slice(0,-1)+t.path}):c.authenticated?r():c.login({redirectUri:window.location.origin})})),t},Pt=Object(r["c"])(ee),Et=Lt(Pt);Pt.use(S,Et)},6050:function(e,t,n){"use strict";n("b82c")},6186:function(e,t,n){},6672:function(e,t,n){"use strict";n("d424")},"702e":function(e,t,n){},7172:function(e,t,n){"use strict";n("4b7e")},"7c4c":function(e,t,n){},"91b0":function(e,t,n){"use strict";n("a132")},"9b19":function(e,t,n){e.exports=n.p+"static/img/logo.41e2d33e.svg"},"9dcc":function(e,t,n){},a132:function(e,t,n){},a2b6:function(e,t,n){"use strict";n("56cf")},af96:function(e,t,n){"use strict";n("15de")},b82c:function(e,t,n){},c515:function(e,t,n){"use strict";n("702e")},d424:function(e,t,n){},d511:function(e,t,n){},e619:function(e,t,n){"use strict";n("6186")}});