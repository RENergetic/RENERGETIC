"use strict";(self["webpackChunkrenergetic_ui"]=self["webpackChunkrenergetic_ui"]||[]).push([[42],{7597:function(e,t,n){n.r(t),n.d(t,{default:function(){return xe}});var i=n(6252);function s(e,t,n,s,l,o){var a=(0,i.up)("HeatMapView");return(0,i.wg)(),(0,i.iD)("div",null,[(0,i.Wm)(a,{"heat-map":l.heatmap},null,8,["heat-map"])])}var l=n(8534),o=(n(5666),n(7941),n(9963)),a=n(3577),r={class:"grid"},c={class:"col-8"},d=(0,i.Uk)(" HeatMap View "),h={id:"heatmapContainer"},u={key:0,class:"col-3 ren"};function p(e,t,n,s,l,p){var m=(0,i.up)("DotMenu"),f=(0,i.up)("v-image"),g=(0,i.up)("v-layer"),y=(0,i.up)("v-stage"),b=(0,i.up)("Card"),k=(0,i.up)("MeasurementChart"),w=(0,i.up)("AreaDetails"),x=(0,i.up)("AccordionTab"),v=(0,i.up)("Accordion"),A=(0,i.up)("Listbox"),C=(0,i.up)("Tree"),K=(0,i.up)("recommendation-view"),T=(0,i.up)("notification-view"),M=(0,i.up)("measurements-view"),S=(0,i.up)("HeatMapSettings"),$=(0,i.up)("Dialog");return(0,i.wg)(),(0,i.iD)(i.HY,null,[(0,i.Wm)(m,{model:p.menuModel()},null,8,["model"]),(0,i._)("div",r,[(0,i._)("div",c,[(0,i.Wm)(b,null,{title:(0,i.w5)((function(){return[d]})),content:(0,i.w5)((function(){return[(0,i._)("div",h,[(0,i.Wm)(y,{id:"heatmap",ref:"stage",config:l.stageSize,onClick:p.onClick},{default:(0,i.w5)((function(){return[(0,i.wy)((0,i.Wm)(g,null,{default:(0,i.w5)((function(){return[(0,i.Wm)(f,{config:{image:l.bgImage}},null,8,["config"])]})),_:1},512),[[o.F8,null!=l.bgImage]])]})),_:1},8,["config","onClick"])])]})),_:1}),l.selectedAreas&&Object.keys(l.selectedAreas).length>0&&l.measurmenentState?((0,i.wg)(),(0,i.j4)(k,{key:0,objects:Object.keys(l.selectedAreas)},null,8,["objects"])):(0,i.kq)("",!0)]),l.bgImage?((0,i.wg)(),(0,i.iD)("div",u,[(0,i.Wm)(v,{class:"tile","active-index":null==l.selectedArea?-1:0},{default:(0,i.w5)((function(){return[(0,i.Wm)(x,{disabled:null==l.selectedArea},{header:(0,i.w5)((function(){return[(0,i.Uk)((0,a.zw)(e.$t("view.selected_area")),1)]})),default:(0,i.w5)((function(){return[(0,i.Wm)(w,{class:"tile","model-value":l.selectedArea,"onUpdate:modelValue":t[0]||(t[0]=function(e){return p.onAreaSelect(e)}),onDelete:t[1]||(t[1]=function(t){return e.areaDelete(l.selectedArea)})},null,8,["model-value"])]})),_:1},8,["disabled"])]})),_:1},8,["active-index"]),(0,i.Wm)(v,{class:"tile","active-index":0},{default:(0,i.w5)((function(){return[(0,i.Wm)(x,null,{header:(0,i.w5)((function(){return[(0,i.Uk)((0,a.zw)(e.$t("model.heatmap.areas")),1)]})),default:(0,i.w5)((function(){return[null!=n.heatMap?((0,i.wg)(),(0,i.j4)(A,{key:0,modelValue:l.selectedArea,"onUpdate:modelValue":t[2]||(t[2]=function(e){return l.selectedArea=e}),options:n.heatMap.areas,"option-label":"label",style:{width:"15rem"}},null,8,["modelValue","options"])):(0,i.kq)("",!0)]})),_:1})]})),_:1}),l.attributes?((0,i.wg)(),(0,i.j4)(v,{key:0,class:"tile","active-index":0},{default:(0,i.w5)((function(){return[(0,i.Wm)(x,null,{header:(0,i.w5)((function(){return[(0,i.Uk)((0,a.zw)(e.$t("model.heatmap.attributes")),1)]})),default:(0,i.w5)((function(){return[(0,i.Wm)(C,{"selection-keys":l.selectedAttributes,"onUpdate:selection-keys":t[3]||(t[3]=function(e){return l.selectedAttributes=e}),value:l.attributes,"selection-mode":"checkbox"},null,8,["selection-keys","value"])]})),_:1})]})),_:1})):(0,i.kq)("",!0),l.settings.recommendationVisibility&&l.selectedAreas?((0,i.wg)(),(0,i.j4)(v,{key:1,class:"tile","active-index":-1},{default:(0,i.w5)((function(){return[(0,i.Wm)(x,null,{header:(0,i.w5)((function(){return[(0,i.Uk)((0,a.zw)(e.$t("model.heatmap.recommendations")),1)]})),default:(0,i.w5)((function(){return[(0,i.Wm)(K,{objects:l.selectedAreas},null,8,["objects"])]})),_:1})]})),_:1})):(0,i.kq)("",!0),l.settings.notificationVisibility&&l.selectedAreas?((0,i.wg)(),(0,i.j4)(v,{key:2,class:"tile","active-index":0},{default:(0,i.w5)((function(){return[(0,i.Wm)(x,null,{header:(0,i.w5)((function(){return[(0,i.Uk)((0,a.zw)(e.$t("model.heatmap.notifications")),1)]})),default:(0,i.w5)((function(){return[(0,i.Wm)(T,{objects:l.selectedAreas},null,8,["objects"])]})),_:1})]})),_:1})):(0,i.kq)("",!0),l.settings.measurementsVisibility&&l.selectedAreas?((0,i.wg)(),(0,i.j4)(v,{key:3,class:"tile","active-index":0},{default:(0,i.w5)((function(){return[(0,i.Wm)(x,null,{header:(0,i.w5)((function(){return[(0,i.Uk)((0,a.zw)(e.$t("view.measurements")),1)]})),default:(0,i.w5)((function(){return[(0,i.Wm)(M,{objects:l.selectedAreas},null,8,["objects"])]})),_:1})]})),_:1})):(0,i.kq)("",!0)])):(0,i.kq)("",!0)]),(0,i.Wm)($,{visible:l.settingsDialog,"onUpdate:visible":t[5]||(t[5]=function(e){return l.settingsDialog=e}),style:{width:"50vw"},maximizable:!0,modal:!0,"dismissable-mask":!0},{default:(0,i.w5)((function(){return[(0,i.Wm)(S,{onUpdate:t[4]||(t[4]=function(e){return p.reloadSettings()})})]})),_:1},8,["visible"])],64)}n(1539),n(4747),n(3290);var m=n(6954),f=n(4019),g={name:"TreeNode",emits:["node-toggle","node-click","checkbox-change"],props:{node:{type:null,default:null},expandedKeys:{type:null,default:null},selectionKeys:{type:null,default:null},selectionMode:{type:String,default:null},templates:{type:null,default:null}},nodeTouched:!1,methods:{toggle(){this.$emit("node-toggle",this.node)},label(e){return"function"===typeof e.label?e.label():e.label},onChildNodeToggle(e){this.$emit("node-toggle",e)},onClick(e){m.p7.hasClass(e.target,"p-tree-toggler")||m.p7.hasClass(e.target.parentElement,"p-tree-toggler")||(this.isCheckboxSelectionMode()?this.toggleCheckbox():this.$emit("node-click",{originalEvent:e,nodeTouched:this.nodeTouched,node:this.node}),this.nodeTouched=!1)},onChildNodeClick(e){this.$emit("node-click",e)},onTouchEnd(){this.nodeTouched=!0},onKeyDown(e){const t=e.target.parentElement;switch(e.which){case 40:var n=t.children[1];if(n)this.focusNode(n.children[0]);else{const e=t.nextElementSibling;if(e)this.focusNode(e);else{let e=this.findNextSiblingOfAncestor(t);e&&this.focusNode(e)}}e.preventDefault();break;case 38:if(t.previousElementSibling)this.focusNode(this.findLastVisibleDescendant(t.previousElementSibling));else{let e=this.getParentNodeElement(t);e&&this.focusNode(e)}e.preventDefault();break;case 37:case 39:this.$emit("node-toggle",this.node),e.preventDefault();break;case 13:this.onClick(e),e.preventDefault();break}},toggleCheckbox(){let e=this.selectionKeys?{...this.selectionKeys}:{};const t=!this.checked;this.propagateDown(this.node,t,e),this.$emit("checkbox-change",{node:this.node,check:t,selectionKeys:e})},propagateDown(e,t,n){if(t?n[e.key]={checked:!0,partialChecked:!1}:delete n[e.key],e.children&&e.children.length)for(let i of e.children)this.propagateDown(i,t,n)},propagateUp(e){let t=e.check,n={...e.selectionKeys},i=0,s=!1;for(let l of this.node.children)n[l.key]&&n[l.key].checked?i++:n[l.key]&&n[l.key].partialChecked&&(s=!0);t&&i===this.node.children.length?n[this.node.key]={checked:!0,partialChecked:!1}:(t||delete n[this.node.key],s||i>0&&i!==this.node.children.length?n[this.node.key]={checked:!1,partialChecked:!0}:delete n[this.node.key]),this.$emit("checkbox-change",{node:e.node,check:e.check,selectionKeys:n})},onChildCheckboxChange(e){this.$emit("checkbox-change",e)},findNextSiblingOfAncestor(e){let t=this.getParentNodeElement(e);return t?t.nextElementSibling?t.nextElementSibling:this.findNextSiblingOfAncestor(t):null},findLastVisibleDescendant(e){const t=e.children[1];if(t){const e=t.children[t.children.length-1];return this.findLastVisibleDescendant(e)}return e},getParentNodeElement(e){const t=e.parentElement.parentElement;return m.p7.hasClass(t,"p-treenode")?t:null},focusNode(e){e.children[0].focus()},isCheckboxSelectionMode(){return"checkbox"===this.selectionMode}},computed:{hasChildren(){return this.node.children&&this.node.children.length>0},expanded(){return this.expandedKeys&&!0===this.expandedKeys[this.node.key]},leaf(){return!1!==this.node.leaf&&!(this.node.children&&this.node.children.length)},selectable(){return!1!==this.node.selectable&&null!=this.selectionMode},selected(){return!(!this.selectionMode||!this.selectionKeys)&&!0===this.selectionKeys[this.node.key]},containerClass(){return["p-treenode",{"p-treenode-leaf":this.leaf}]},contentClass(){return["p-treenode-content",this.node.styleClass,{"p-treenode-selectable":this.selectable,"p-highlight":this.checkboxMode?this.checked:this.selected}]},icon(){return["p-treenode-icon",this.node.icon]},toggleIcon(){return["p-tree-toggler-icon pi pi-fw",{"pi-chevron-down":this.expanded,"pi-chevron-right":!this.expanded}]},checkboxClass(){return["p-checkbox-box",{"p-highlight":this.checked,"p-indeterminate":this.partialChecked}]},checkboxIcon(){return["p-checkbox-icon",{"pi pi-check":this.checked,"pi pi-minus":this.partialChecked}]},checkboxMode(){return"checkbox"===this.selectionMode&&!1!==this.node.selectable},checked(){return!!this.selectionKeys&&(this.selectionKeys[this.node.key]&&this.selectionKeys[this.node.key].checked)},partialChecked(){return!!this.selectionKeys&&(this.selectionKeys[this.node.key]&&this.selectionKeys[this.node.key].partialChecked)}},directives:{ripple:f.Z}};const y={key:0,class:"p-checkbox p-component"},b={class:"p-treenode-label"},k={key:0,class:"p-treenode-children",role:"group"};function w(e,t,n,s,l,o){const r=(0,i.up)("TreeNode",!0),c=(0,i.Q2)("ripple");return(0,i.wg)(),(0,i.j4)("li",{class:o.containerClass},[(0,i.Wm)("div",{class:o.contentClass,tabindex:"0",role:"treeitem","aria-expanded":o.expanded,onClick:t[2]||(t[2]=(...e)=>o.onClick&&o.onClick(...e)),onKeydown:t[3]||(t[3]=(...e)=>o.onKeyDown&&o.onKeyDown(...e)),onTouchend:t[4]||(t[4]=(...e)=>o.onTouchEnd&&o.onTouchEnd(...e)),style:n.node.style},[(0,i.wy)((0,i.Wm)("button",{type:"button",class:"p-tree-toggler p-link",onClick:t[1]||(t[1]=(...e)=>o.toggle&&o.toggle(...e)),tabindex:"-1"},[(0,i.Wm)("span",{class:o.toggleIcon},null,2)],512),[[c]]),o.checkboxMode?((0,i.wg)(),(0,i.j4)("div",y,[(0,i.Wm)("div",{class:o.checkboxClass,role:"checkbox","aria-checked":o.checked},[(0,i.Wm)("span",{class:o.checkboxIcon},null,2)],10,["aria-checked"])])):(0,i.kq)("",!0),(0,i.Wm)("span",{class:o.icon},null,2),(0,i.Wm)("span",b,[n.templates[n.node.type]||n.templates["default"]?((0,i.wg)(),(0,i.j4)((0,i.LL)(n.templates[n.node.type]||n.templates["default"]),{key:0,node:n.node},null,8,["node"])):((0,i.wg)(),(0,i.j4)(i.HY,{key:1},[(0,i.Uk)((0,a.zw)(o.label(n.node)),1)],64))])],46,["aria-expanded"]),o.hasChildren&&o.expanded?((0,i.wg)(),(0,i.j4)("ul",k,[((0,i.wg)(!0),(0,i.j4)(i.HY,null,(0,i.Ko)(n.node.children,(e=>((0,i.wg)(),(0,i.j4)(r,{key:e.key,node:e,templates:n.templates,expandedKeys:n.expandedKeys,onNodeToggle:o.onChildNodeToggle,onNodeClick:o.onChildNodeClick,selectionMode:n.selectionMode,selectionKeys:n.selectionKeys,onCheckboxChange:o.propagateUp},null,8,["node","templates","expandedKeys","onNodeToggle","onNodeClick","selectionMode","selectionKeys","onCheckboxChange"])))),128))])):(0,i.kq)("",!0)],2)}g.render=w;var x={name:"Tree",emits:["node-expand","node-collapse","update:expandedKeys","update:selectionKeys","node-select","node-unselect"],props:{value:{type:null,default:null},expandedKeys:{type:null,default:null},selectionKeys:{type:null,default:null},selectionMode:{type:String,default:null},metaKeySelection:{type:Boolean,default:!0},loading:{type:Boolean,default:!1},loadingIcon:{type:String,default:"pi pi-spinner"},filter:{type:Boolean,default:!1},filterBy:{type:String,default:"label"},filterMode:{type:String,default:"lenient"},filterPlaceholder:{type:String,default:null},filterLocale:{type:String,default:void 0},scrollHeight:{type:String,default:null}},data(){return{d_expandedKeys:this.expandedKeys||{},filterValue:null}},watch:{expandedKeys(e){this.d_expandedKeys=e}},methods:{onNodeToggle(e){const t=e.key;this.d_expandedKeys[t]?(delete this.d_expandedKeys[t],this.$emit("node-collapse",e)):(this.d_expandedKeys[t]=!0,this.$emit("node-expand",e)),this.d_expandedKeys={...this.d_expandedKeys},this.$emit("update:expandedKeys",this.d_expandedKeys)},onNodeClick(e){if(null!=this.selectionMode&&!1!==e.node.selectable){const t=!e.nodeTouched&&this.metaKeySelection,n=t?this.handleSelectionWithMetaKey(e):this.handleSelectionWithoutMetaKey(e);this.$emit("update:selectionKeys",n)}},onCheckboxChange(e){this.$emit("update:selectionKeys",e.selectionKeys),e.check?this.$emit("node-select",e.node):this.$emit("node-unselect",e.node)},handleSelectionWithMetaKey(e){const t=e.originalEvent,n=e.node,i=t.metaKey||t.ctrlKey,s=this.isNodeSelected(n);let l;return s&&i?(this.isSingleSelectionMode()?l={}:(l={...this.selectionKeys},delete l[n.key]),this.$emit("node-unselect",n)):(this.isSingleSelectionMode()?l={}:this.isMultipleSelectionMode()&&(l=i&&this.selectionKeys?{...this.selectionKeys}:{}),l[n.key]=!0,this.$emit("node-select",n)),l},handleSelectionWithoutMetaKey(e){const t=e.node,n=this.isNodeSelected(t);let i;return this.isSingleSelectionMode()?n?(i={},this.$emit("node-unselect",t)):(i={},i[t.key]=!0,this.$emit("node-select",t)):n?(i={...this.selectionKeys},delete i[t.key],this.$emit("node-unselect",t)):(i=this.selectionKeys?{...this.selectionKeys}:{},i[t.key]=!0,this.$emit("node-select",t)),i},isSingleSelectionMode(){return"single"===this.selectionMode},isMultipleSelectionMode(){return"multiple"===this.selectionMode},isNodeSelected(e){return!(!this.selectionMode||!this.selectionKeys)&&!0===this.selectionKeys[e.key]},isChecked(e){return!!this.selectionKeys&&(this.selectionKeys[e.key]&&this.selectionKeys[e.key].checked)},isNodeLeaf(e){return!1!==e.leaf&&!(e.children&&e.children.length)},onFilterKeydown(e){13===e.which&&e.preventDefault()},findFilteredNodes(e,t){if(e){let n=!1;if(e.children){let i=[...e.children];e.children=[];for(let s of i){let i={...s};this.isFilterMatched(i,t)&&(n=!0,e.children.push(i))}}if(n)return!0}},isFilterMatched(e,{searchFields:t,filterText:n,strict:i}){let s=!1;for(let l of t){let t=String(m.gb.resolveFieldData(e,l)).toLocaleLowerCase(this.filterLocale);t.indexOf(n)>-1&&(s=!0)}return(!s||i&&!this.isNodeLeaf(e))&&(s=this.findFilteredNodes(e,{searchFields:t,filterText:n,strict:i})||s),s}},computed:{containerClass(){return["p-tree p-component",{"p-tree-selectable":null!=this.selectionMode,"p-tree-loading":this.loading,"p-tree-flex-scrollable":"flex"===this.scrollHeight}]},loadingIconClass(){return["p-tree-loading-icon pi-spin",this.loadingIcon]},filteredValue(){let e=[];const t=this.filterBy.split(","),n=this.filterValue.trim().toLocaleLowerCase(this.filterLocale),i="strict"===this.filterMode;for(let s of this.value){let l={...s},o={searchFields:t,filterText:n,strict:i};(i&&(this.findFilteredNodes(l,o)||this.isFilterMatched(l,o))||!i&&(this.isFilterMatched(l,o)||this.findFilteredNodes(l,o)))&&e.push(l)}return e},valueToRender(){return this.filterValue&&this.filterValue.trim().length>0?this.filteredValue:this.value}},components:{TreeNode:g}};const v={key:0,class:"p-tree-loading-overlay p-component-overlay"},A={key:1,class:"p-tree-filter-container"},C=(0,i.Wm)("span",{class:"p-tree-filter-icon pi pi-search"},null,-1),K={class:"p-tree-container",role:"tree"};function T(e,t,n,s,l,a){const r=(0,i.up)("TreeNode");return(0,i.wg)(),(0,i.j4)("div",{class:a.containerClass},[n.loading?((0,i.wg)(),(0,i.j4)("div",v,[(0,i.Wm)("i",{class:a.loadingIconClass},null,2)])):(0,i.kq)("",!0),n.filter?((0,i.wg)(),(0,i.j4)("div",A,[(0,i.wy)((0,i.Wm)("input",{type:"text",autocomplete:"off",class:"p-tree-filter p-inputtext p-component",placeholder:n.filterPlaceholder,onKeydown:t[1]||(t[1]=(...e)=>a.onFilterKeydown&&a.onFilterKeydown(...e)),"onUpdate:modelValue":t[2]||(t[2]=e=>l.filterValue=e)},null,40,["placeholder"]),[[o.nr,l.filterValue]]),C])):(0,i.kq)("",!0),(0,i.Wm)("div",{class:"p-tree-wrapper",style:{maxHeight:n.scrollHeight}},[(0,i.Wm)("ul",K,[((0,i.wg)(!0),(0,i.j4)(i.HY,null,(0,i.Ko)(a.valueToRender,(t=>((0,i.wg)(),(0,i.j4)(r,{key:t.key,node:t,templates:e.$slots,expandedKeys:l.d_expandedKeys,onNodeToggle:a.onNodeToggle,onNodeClick:a.onNodeClick,selectionMode:n.selectionMode,selectionKeys:n.selectionKeys,onCheckboxChange:a.onCheckboxChange},null,8,["node","templates","expandedKeys","onNodeToggle","onNodeClick","selectionMode","selectionKeys","onCheckboxChange"])))),128))])],4)],2)}function M(e,t){void 0===t&&(t={});var n=t.insertAt;if(e&&"undefined"!==typeof document){var i=document.head||document.getElementsByTagName("head")[0],s=document.createElement("style");s.type="text/css","top"===n&&i.firstChild?i.insertBefore(s,i.firstChild):i.appendChild(s),s.styleSheet?s.styleSheet.cssText=e:s.appendChild(document.createTextNode(e))}}var S="\n.p-tree-container {\n    margin: 0;\n    padding: 0;\n    list-style-type: none;\n    overflow: auto;\n}\n.p-treenode-children {\n    margin: 0;\n    padding: 0;\n    list-style-type: none;\n}\n.p-tree-wrapper {\n    overflow: auto;\n}\n.p-treenode-selectable {\n    cursor: pointer;\n    -webkit-user-select: none;\n       -moz-user-select: none;\n        -ms-user-select: none;\n            user-select: none;\n}\n.p-tree-toggler {\n    cursor: pointer;\n    -webkit-user-select: none;\n       -moz-user-select: none;\n        -ms-user-select: none;\n            user-select: none;\n    display: -webkit-inline-box;\n    display: -ms-inline-flexbox;\n    display: inline-flex;\n    -webkit-box-align: center;\n        -ms-flex-align: center;\n            align-items: center;\n    -webkit-box-pack: center;\n        -ms-flex-pack: center;\n            justify-content: center;\n    overflow: hidden;\n    position: relative;\n}\n.p-treenode-leaf > .p-treenode-content .p-tree-toggler {\n    visibility: hidden;\n}\n.p-treenode-content {\n    display: -webkit-box;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-align: center;\n        -ms-flex-align: center;\n            align-items: center;\n}\n.p-tree-filter {\n    width: 100%;\n}\n.p-tree-filter-container {\n    position: relative;\n    display: block;\n    width: 100%;\n}\n.p-tree-filter-icon {\n    position: absolute;\n    top: 50%;\n    margin-top: -.5rem;\n}\n.p-tree-loading {\n    position: relative;\n    min-height: 4rem;\n}\n.p-tree .p-tree-loading-overlay {\n    position: absolute;\n    z-index: 1;\n    display: -webkit-box;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-align: center;\n        -ms-flex-align: center;\n            align-items: center;\n    -webkit-box-pack: center;\n        -ms-flex-pack: center;\n            justify-content: center;\n}\n.p-tree-flex-scrollable {\n    display: -webkit-box;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-flex: 1;\n        -ms-flex: 1;\n            flex: 1;\n    height: 100%;\n    -webkit-box-orient: vertical;\n    -webkit-box-direction: normal;\n        -ms-flex-direction: column;\n            flex-direction: column;\n}\n.p-tree-flex-scrollable .p-tree-wrapper {\n    -webkit-box-flex: 1;\n        -ms-flex: 1;\n            flex: 1;\n}\n";M(S),x.render=T;var $=x,j=n(9394),_=n(4612),N={style:{display:"block",width:"10rem"}};function I(e,t,n,s,l,o){return(0,i.wg)(),(0,i.iD)("div",N," todo view list of recommendation "+(0,a.zw)(l.mRecommendations)+" objects:"+(0,a.zw)(n.objects),1)}var W={name:"RecommendationView",components:{},props:{recommendations:{type:Array,default:function(){return null}},objects:{type:Array,default:null}},data:function(){return{mRecommendations:[]}},computed:{},watch:{objects:{handler:function(){},deep:!0}},created:function(){this.mRecommendations=this.recommendations},methods:{}},D=n(3744);const V=(0,D.Z)(W,[["render",I]]);var E=V,z=n(3561),H={name:"Accordion",emits:["tab-close","tab-open","update:activeIndex"],props:{multiple:{type:Boolean,default:!1},activeIndex:{type:[Number,Array],default:null},lazy:{type:Boolean,default:!1}},data(){return{d_activeIndex:this.activeIndex}},watch:{activeIndex(e){this.d_activeIndex=e}},methods:{onTabClick(e,t,n){if(!this.isTabDisabled(t)){const t=this.isTabActive(n),i=t?"tab-close":"tab-open";this.multiple?t?this.d_activeIndex=this.d_activeIndex.filter((e=>e!==n)):this.d_activeIndex?this.d_activeIndex.push(n):this.d_activeIndex=[n]:this.d_activeIndex=this.d_activeIndex===n?null:n,this.$emit("update:activeIndex",this.d_activeIndex),this.$emit(i,{originalEvent:e,index:n})}},onTabKeydown(e,t,n){13===e.which&&this.onTabClick(e,t,n)},isTabActive(e){return this.multiple?this.d_activeIndex&&this.d_activeIndex.includes(e):e===this.d_activeIndex},getKey(e,t){return e.props&&e.props.header?e.props.header:t},isTabDisabled(e){return e.props&&e.props.disabled},getTabClass(e){return["p-accordion-tab",{"p-accordion-tab-active":this.isTabActive(e)}]},getTabHeaderClass(e,t){return["p-accordion-header",{"p-highlight":this.isTabActive(t),"p-disabled":this.isTabDisabled(e)}]},getTabAriaId(e){return this.ariaId+"_"+e},getHeaderIcon(e){const t=this.isTabActive(e);return["p-accordion-toggle-icon pi",{"pi-chevron-right":!t,"pi-chevron-down":t}]},isAccordionTab(e){return"AccordionTab"===e.type.name}},computed:{tabs(){const e=[];return this.$slots.default().forEach((t=>{this.isAccordionTab(t)?e.push(t):t.children&&t.children instanceof Array&&t.children.forEach((t=>{this.isAccordionTab(t)&&e.push(t)}))})),e},ariaId(){return(0,m.Th)()}}};const F={class:"p-accordion p-component"},L={key:0,class:"p-accordion-header-text"},O={class:"p-accordion-content"};function Z(e,t,n,s,l,r){return(0,i.wg)(),(0,i.j4)("div",F,[((0,i.wg)(!0),(0,i.j4)(i.HY,null,(0,i.Ko)(r.tabs,((e,t)=>((0,i.wg)(),(0,i.j4)("div",{key:r.getKey(e,t),class:r.getTabClass(t)},[(0,i.Wm)("div",{class:r.getTabHeaderClass(e,t)},[(0,i.Wm)("a",{role:"tab",class:"p-accordion-header-link",onClick:n=>r.onTabClick(n,e,t),onKeydown:n=>r.onTabKeydown(n,e,t),tabindex:r.isTabDisabled(e)?null:"0","aria-expanded":r.isTabActive(t),id:r.getTabAriaId(t)+"_header","aria-controls":r.getTabAriaId(t)+"_content"},[(0,i.Wm)("span",{class:r.getHeaderIcon(t)},null,2),e.props&&e.props.header?((0,i.wg)(),(0,i.j4)("span",L,(0,a.zw)(e.props.header),1)):(0,i.kq)("",!0),e.children&&e.children.header?((0,i.wg)(),(0,i.j4)((0,i.LL)(e.children.header),{key:1})):(0,i.kq)("",!0)],40,["onClick","onKeydown","tabindex","aria-expanded","id","aria-controls"])],2),(0,i.Wm)(o.uT,{name:"p-toggleable-content"},{default:(0,i.w5)((()=>[!n.lazy||r.isTabActive(t)?(0,i.wy)(((0,i.wg)(),(0,i.j4)("div",{key:0,class:"p-toggleable-content",role:"region",id:r.getTabAriaId(t)+"_content","aria-labelledby":r.getTabAriaId(t)+"_header"},[(0,i.Wm)("div",O,[((0,i.wg)(),(0,i.j4)((0,i.LL)(e)))])],8,["id","aria-labelledby"])),[[o.F8,!!n.lazy||r.isTabActive(t)]]):(0,i.kq)("",!0)])),_:2},1024)],2)))),128))])}function R(e,t){void 0===t&&(t={});var n=t.insertAt;if(e&&"undefined"!==typeof document){var i=document.head||document.getElementsByTagName("head")[0],s=document.createElement("style");s.type="text/css","top"===n&&i.firstChild?i.insertBefore(s,i.firstChild):i.appendChild(s),s.styleSheet?s.styleSheet.cssText=e:s.appendChild(document.createTextNode(e))}}var U="\n.p-accordion-header-link {\n    cursor: pointer;\n    display: -webkit-box;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-align: center;\n        -ms-flex-align: center;\n            align-items: center;\n    -webkit-user-select: none;\n       -moz-user-select: none;\n        -ms-user-select: none;\n            user-select: none;\n    position: relative;\n    text-decoration: none;\n}\n.p-accordion-header-link:focus {\n    z-index: 1;\n}\n.p-accordion-header-text {\n    line-height: 1;\n}\n";R(U),H.render=Z;var B=H,q={name:"AccordionTab",props:{header:null,disabled:Boolean}};function P(e,t,n,s,l,o){return(0,i.WI)(e.$slots,"default")}q.render=P;var Y=q,Q=n(3609),X=n(8382),G=n(9649),J=n(2125);function ee(e,t,n,s,l,o){var a=(0,i.up)("Settings");return(0,i.wg)(),(0,i.j4)(a,{schema:l.schema,model:l.model},null,8,["schema","model"])}var te=n(6062),ne={name:"HeatmapSettings",components:{Settings:te.Z},props:{},emits:["update"],data:function(){return{model:this.$store.getters["settings/heatmap"],panels:[],schema:{}}},computed:{},watch:{model:{handler:function(e){this.$store.commit("settings/heatmap",e),this.$emit("update")},deep:!0}},mounted:function(){var e=this;return(0,l.Z)(regeneratorRuntime.mark((function t(){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:e.$ren.dashboardApi.informationPanelList().then((function(t){e.panels=t})).then((function(){e.schema=e.getSchema()}));case 1:case"end":return t.stop()}}),t)})))()},created:function(){var e=this;return(0,l.Z)(regeneratorRuntime.mark((function t(){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:e.schema=e.getSchema();case 1:case"end":return t.stop()}}),t)})))()},methods:{getSchema:function(){var e=[{label:this.$t("settings.recommendations"),ext:{true:this.$t("settings.visible"),false:this.$t("settings.hidden")},type:Boolean,key:"recommendationVisibility"},{label:this.$t("settings.measurements"),ext:{true:this.$t("settings.visible"),false:this.$t("settings.hidden")},type:Boolean,key:"measurementsVisibility"},{label:this.$t("settings.notification"),ext:{true:this.$t("settings.visible"),false:this.$t("settings.hidden")},type:Boolean,key:"notificationVisibility"}];return e},toggle:function(e){this.$refs.menu.toggle(e)}}};const ie=(0,D.Z)(ne,[["render",ee]]);var se=ie,le=n(1079),oe={class:"p-fluid"},ae=["for"],re={class:"col-12"},ce=["id"];function de(e,t,n,s,l,o){return(0,i.wg)(),(0,i.iD)("div",oe,[(0,i.Uk)(" TODO: measurements view "+(0,a.zw)(n.objects)+" "+(0,a.zw)(l.measurements.length)+"dddddd "+(0,a.zw)(l.measurements)+" ",1),((0,i.wg)(!0),(0,i.iD)(i.HY,null,(0,i.Ko)(l.measurements,(function(t){return(0,i.wg)(),(0,i.iD)("div",{key:t,class:(0,a.C_)("field grid")},[(0,i._)("label",{for:t.key,class:"col-12"},(0,a.zw)(e.$t("model.measurements"+t.key)),9,ae),(0,i._)("div",re,[(0,i._)("span",{id:t.key},(0,a.zw)(t.value),9,ce)])])})),128))])}var he={name:"MeasurementsView",components:{},props:{objects:{type:Object,default:function(){return{}}}},data:function(){return{measurements:[],objectsId:[]}},watch:{objects:{handler:function(e){this.objectsId=Object.keys(e),this.refresh()},deep:!0}},created:function(){this.objectsId=Object.keys(this.objects),this.refresh()},methods:{refresh:function(){var e=this;return(0,l.Z)(regeneratorRuntime.mark((function t(){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,e.$ren.measurementApi.getCurrentMeasurements(Object.keys(e.objectsId)).then((function(t){e.measurements=t}));case 2:case"end":return t.stop()}}),t)})))()}}};const ue=(0,D.Z)(he,[["render",de]]);var pe=ue,me=900,fe=450,ge={name:"HeatMapView",components:{Card:X.Z,Listbox:z.Z,AreaDetails:_.Z,Accordion:B,AccordionTab:Y,Tree:$,HeatMapSettings:se,RecommendationView:E,DotMenu:j.Z,NotificationView:J.Z,MeasurementChart:le.Z,MeasurementsView:pe,Dialog:Q.Z},props:{heatMap:{type:Object,default:function(){return null}}},data:function(){return{stageSize:{width:me,height:fe},selectedArea:null,scale:1,bgImage:null,selectedAreas:{},attributes:null,selectedAttributes:[],recommendations:null,recommendationPanelState:-1,measurmenentState:!0,settingsDialog:!1,settings:this.$store.getters["settings/heatmap"]}},watch:{selectedAreas:{handler:function(){var e=(0,l.Z)(regeneratorRuntime.mark((function e(t){return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:if(!(t&&Object.keys(t).length>0)){e.next=5;break}return e.next=3,this.loadAttributes();case 3:e.next=6;break;case 5:this.attributes=null;case 6:case"end":return e.stop()}}),e,this)})));function t(t){return e.apply(this,arguments)}return t}(),deep:!0},heatMap:function(){var e=this;if(null!=this.heatMap){var t=new window.Image;t.src=this.heatMap.imgUrl,t.onload=function(){e.bgImage=t;var n=e.$refs.stage.getStage();e.scaleHeatMap(n,t),e.heatMap.areas.forEach((function(t,n){return e.drawArea(t,n)}))}}else console.info("todo: null")}},mounted:function(){var e=this;if(null!=this.heatMap){var t=new window.Image;t.src=this.heatMap.imgUrl,t.onload=function(){e.bgImage=t;var n=e.$refs.stage.getStage();e.scaleHeatMap(n,t),e.heatMap.areas.forEach((function(t,n){return e.drawArea(t,n)}))}}},methods:{reloadSettings:function(){this.settings=this.$store.getters["settings/heatmap"]},onAreaSelect:function(e){var t=this;if(null!=this.selectedArea){var n=this.selectedArea.id;this.toggleArea(n,!1)}if(null!=e){Object.keys(this.selectedAreas).forEach((function(e){t.toggleArea(e,!1)}));var i={};i[e.id]=1,this.selectedAreas=i;var s=e.id;this.toggleArea(s,!0)}},toggleArea:function(e,t){var n=this.$refs.stage.getStage().findOne("#".concat(e));null!=n&&(t?n.fill("#AAAAFFAA"):n.fill("#00D2FFAA"))},onClick:function(e){if("CANVAS"==e.target.nodeName&&this.addMode){var t=this.$refs.stage.getStage(),n=t.findOne("#".concat(this.current.id));if(null!=n){this.current.points.push([e.layerX/this.scale,e.layerY/this.scale]);var i=this.current.points,s=function(e,t){e.beginPath(),i.forEach((function(t){e.lineTo(t[0],t[1])})),e.closePath(),e.fillStrokeShape(t)};n.setSceneFunc(s)}}},drawArea:function(e,t){var n=this,i=new G["default"].Layer,s=this.$refs.stage.getStage(),l=new G["default"].Shape(this.getConfig(e)),o=e.id;l.on("click",(function(){n.selectedArea=n.heatMap.areas[t],1==n.selectedAreas[o]?(1==n.selectedAreas.length?n.selectedAreas={}:delete n.selectedAreas[o],n.toggleArea(e.id,!1)):(n.selectedAreas[o]=1,n.toggleArea(e.id,!0));var i=Object.keys(n.selectedAreas);if(1!=i.length)n.selectedArea=null;else{var s=Object.keys(n.selectedAreas);n.heatMap.areas.forEach((function(e){e.id==s[0]&&(n.selectedArea=e)}))}})),i.add(l),s.add(i),s.draw()},getConfig:function(e){return{sceneFunc:function(t,n){t.beginPath(),console.info(e),e.points.forEach((function(e){t.lineTo(e[0],e[1])})),t.closePath(),t.fillStrokeShape(n)},fill:"#00D2FFAA",stroke:"black",strokeWidth:1,opacity:.75,listening:!0,id:e.id}},loadAttributes:function(){var e=this;return(0,l.Z)(regeneratorRuntime.mark((function t(){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,e.$ren.measurementApi.attributes(Object.keys(e.selectedAreas)).then((function(t){e.attributes=t}));case 2:case"end":return t.stop()}}),t)})))()},scaleHeatMap:function(e,t){if(null!=t){var n=me/t.width;this.scale=n,e.width(t.width),e.height(t.height),e.scale({x:n,y:n})}},menuModel:function(){var e=this;return[{label:this.$t("menu.settings"),icon:"pi pi-fw pi-eye",command:function(){e.settingsDialog=!e.settingsDialog}}]}}};const ye=(0,D.Z)(ge,[["render",p]]);var be=ye,ke={name:"HeatMap",components:{HeatMapView:be},data:function(){return{heatmap:null}},created:function(){var e=this;return(0,l.Z)(regeneratorRuntime.mark((function t(){return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:e.$ren.dashboardApi.getHeatMap(e.$route.params.id).then((function(t){e.heatmap=t})).catch((function(t){e.$toast.add({severity:"error",summary:e.$t("view.toast.get_heatmap",{label:e.label,error:t}),life:3e3})}));case 1:case"end":return t.stop()}}),t)})))()},mounted:function(){},methods:{}};const we=(0,D.Z)(ke,[["render",s]]);var xe=we}}]);
//# sourceMappingURL=42-legacy.a4b77e1e.js.map