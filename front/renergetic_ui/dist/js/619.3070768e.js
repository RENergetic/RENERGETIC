"use strict";(self["webpackChunkrenergetic_ui"]=self["webpackChunkrenergetic_ui"]||[]).push([[619],{9619:function(e,a,l){l.r(a),l.d(a,{default:function(){return g}});var d=l(6252),i=l(3577);const t={style:{width:"30rem"}},s={class:"field grid"},o={for:"dasboardName",class:"col-fixed"},r={class:"col"},n={class:"field grid"},u={for:"dasboardUrl",class:"col-fixed"},h={class:"col"},m={class:"field grid"},c={for:"dasboardLabel",class:"col-fixed"},b={class:"col"},p={class:"field grid"},_={class:"col"},f={class:"col"};function v(e,a,l,v,w,$){const k=(0,d.up)("InputText"),U=(0,d.up)("Button"),g=(0,d.up)("Card");return(0,d.wg)(),(0,d.j4)(g,null,{title:(0,d.w5)((()=>[(0,d.Uk)((0,i.zw)(e.$t("view.add_dashboard")),1)])),content:(0,d.w5)((()=>[(0,d._)("div",t,[(0,d._)("div",s,[(0,d._)("label",o,(0,i.zw)(e.$t("model.dashboard.name")),1),(0,d._)("div",r,[(0,d.Wm)(k,{id:"dasboardName",modelValue:w.name,"onUpdate:modelValue":a[0]||(a[0]=e=>w.name=e)},null,8,["modelValue"])])]),(0,d._)("div",n,[(0,d._)("label",u,(0,i.zw)(e.$t("model.dashboard.url")),1),(0,d._)("div",h,[(0,d.Wm)(k,{id:"dasboardUrl",modelValue:w.url,"onUpdate:modelValue":a[1]||(a[1]=e=>w.url=e)},null,8,["modelValue"])])]),(0,d._)("div",m,[(0,d._)("label",c,(0,i.zw)(e.$t("model.dashboard.label")),1),(0,d._)("div",b,[(0,d.Wm)(k,{id:"dasboardLabel",modelValue:w.label,"onUpdate:modelValue":a[2]||(a[2]=e=>w.label=e)},null,8,["modelValue"])])]),(0,d._)("div",p,[(0,d._)("div",_,[(0,d.Wm)(U,{label:e.$t("view.button.submit"),onClick:$.submit},null,8,["label","onClick"])]),(0,d._)("div",f,[(0,d.Wm)(U,{label:e.$t("view.button.cancel"),onClick:$.cancel},null,8,["label","onClick"])])])])])),_:1})}var w=l(8382),$={name:"DashboardAdd",components:{Card:w.Z},emits:["UpdateMenu"],data(){return{id:0,name:"",url:"",label:"",editmode:!1}},mounted(){let e=this.$route.params.dashboard_id;null!=e&&(this.$ren.dashboardApi.get(e).then((a=>{console.log(a),this.id=e,this.name=a.name,this.url=a.url,this.label=a.label})),this.editmode=!0)},methods:{clear(){this.name="",this.url="",this.label=""},cancel(){this.$router.back()},async submit(){if(!this.editmode){let e={name:this.name,label:this.label,url:this.url};await this.$ren.dashboardApi.add(e).then((e=>{this.id=e.id,this.$store.commit("view/dashboardsAdd",e),this.$emit("UpdateMenu",null),this.$router.replace({name:"Dashboard",params:{dashboard_id:this.id}})}))}if(this.editmode){let e={id:this.id,name:this.name,label:this.label,url:this.url};await this.$ren.dashboardApi.update(e).then((e=>{this.$store.commit("view/dashboardsUpdate",e),this.$emit("UpdateMenu",null),this.$router.replace({name:"Dashboard",params:{dashboard_id:this.id}})}))}}}},k=l(3744);const U=(0,k.Z)($,[["render",v]]);var g=U}}]);
//# sourceMappingURL=619.3070768e.js.map