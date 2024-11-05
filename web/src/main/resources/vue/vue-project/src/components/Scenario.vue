<template>
  <dev v-for="scenario in scenarios">
    <button id="start" v-on:click="scenario_start">start</button>

    <button id="palse" v-on:click="scenario_palse">palse</button>

    <button id="stop" v-on:click="scenario_stop">stop</button>

    <button id="replay" v-on:click="scenario_replay">replay</button>

    <text>{{scenario.scenarioName}}}</text>

    <text v-for="step in scenario.steps">
        {{step.stepText}}
      <text>{{step.executeInfo}}}</text>
    </text>
  </dev>

</template>
<script>
import {reactive, ref} from "vue";
import {initWebSocket, sendMessage, closeWebsocket} from "./websocket.js";
import {s} from "vite/dist/node/types.d-aGj9QkWt.js";
let scenariosData = [];
export default {
  setup(){
    let getScenarioReq = {
      type:"request",
      content_type:"featureInfo",
      content:{}
    };
    initWebSocket("wss://127.0.0.1:8888/websocket",messageHandler);
    sendMessage(getScenarioReq);
    let scenarios = reactive(scenariosData);
    return {
      scenarios,
    }
  },
  methods:{

  }
}

function messageHandler(jsonMessage){
  if(jsonMessage['type'] === 'featureInfo'){
    //从jsonMessage中提取消息并放入上述对象中。
    //响应式如何处理？
    for(let i = 0; i < jsonMessage['content'].length; i++){
      let targetObject = {};
      targetObject["scenarioName"] = jsonMessage['content'][i]["scenarioName"];
      targetObject["steps"] = [];
      //需要控制step的顺序性。先一个个copy吧。
      for(let j = 0; j < jsonMessage['content'][i]["steps"].length; j++){
        let num = jsonMessage['content'][i]["steps"][j]["num"];
        targetObject["steps"][num]["stepText"] = jsonMessage['content'][i]["steps"][j]["stepText"];
        //暂时初始化。
        targetObject["steps"][num]["executeInfo"] = "";
        targetObject["steps"][num]["status"] = "";
      }
      scenariosData[jsonMessage['content'][i]["num"]] = targetObject;
    }
  }else if(jsonMessage['type'] === 'executeInfo'){
    let scenarioNum = jsonMessage['content']["scenarioNum"];
    let stepNum = jsonMessage['content']["stepNum"];
    let status = jsonMessage['content']["status"];
    let executeInfo = jsonMessage['content']["executeInfo"];
    scenariosData[scenarioNum]["steps"][stepNum]["status"] = status;
    scenariosData[scenarioNum]["steps"][stepNum]["executeInfo"] = executeInfo; //预计是在这里触发更新。
  }
  else {
    console.log("other type hasn't complete yet");
  }
}

// ----- ----- ----- ----- -----
function scenario_start(){

}
function scenario_palse(){

}
function scenario_stop(){

}
function scenario_replay(){

}
</script>