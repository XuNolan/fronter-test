<template>
  <dev v-for="feature in features">
    <button id="start_btn" v-on:click="feature_start($(feature.featureId))">start</button>

<!--    <button id="palse" v-on:click="scenario_palse">palse</button>-->

<!--    <button id="stop" v-on:click="scenario_stop">stop</button>-->

<!--    <button id="replay" v-on:click="scenario_replay">replay</button>-->
    <text>{{feature.featureId}}}</text>
    <text>{{feature.featureName}}}</text>

    <dev v-for="scenario in feature.scenarios">
      <text>{{scenario.scenarioId}}}</text>
      <text>{{scenario.scenarioName}}}</text>

      <dev v-for="step in scenario.steps">
        <text>{{step.stepId}}}</text>
        <text>{{step.prefix}}}</text>
        <text>{{step.stepText}}</text>
        <text>{{step.status}}</text>
        <text>{{step.executeInfo}}</text>
        <text>{{step.startTime}}</text>
        <text>{{step.endTime}}</text>
      </dev>

    </dev>

  </dev>

</template>
<script>
import {reactive} from "vue";
import {initWebSocket, sendMessage} from "./websocket.js";
let featureInfoReplies = [];
export default {
  setup(){
    let getScenarioReq = {
      msgType:"request",
      contentType:"request_feature",
      content:{}
    };
    initWebSocket("ws://127.0.0.1:8888/websocket", messageHandler);
    sendMessage(getScenarioReq);
    let features = reactive(featureInfoReplies);
    return {
      features,
    }
  },
  methods:{
    feature_start,
  }
}

//可不可以直接反序列化？
function messageHandler(jsonMessage){
  let msgType = jsonMessage['msgType'];
  if(msgType === 'featureInfos') {
    for(let featureNum = 0; featureNum < jsonMessage['content'].length; featureNum++) {
      let featureInfo = {};
      featureInfo['featureId'] = jsonMessage['content'][featureNum]["featureId"];
      featureInfo['featureName'] = jsonMessage['content'][featureNum]["featureName"];
      featureInfo['fileName'] = jsonMessage['content'][featureNum]["fileName"];
      featureInfo['scenarios'] = [];
      featureInfoReplies.push(featureInfo);
      for (let scenarioNum = 0; scenarioNum < jsonMessage['content'][featureNum]["scenarios"].length; scenarioNum++) {
        let scenario = {};
        scenario["scenarioId"] = jsonMessage['content'][featureNum]["scenarios"][scenarioNum]["scenarioId"];
        scenario["scenarioName"] = jsonMessage['content'][featureNum]["scenarios"][scenarioNum]["scenarioName"];
        scenario["steps"] = [];
        featureInfo["scenarios"].push(scenario);
        for (let stepNum = 0; stepNum < jsonMessage['content'][featureNum]["scenarios"][scenarioNum]["steps"]; stepNum++) {
          let step = {};
          step["stepId"] = jsonMessage['content'][featureNum]["scenarios"][scenarioNum]["steps"][stepNum]["stepId"];
          step["prefix"] = jsonMessage['content'][featureNum]["scenarios"][scenarioNum]["steps"][stepNum]["prefix"];
          step["stepText"] = jsonMessage['content'][featureNum]["scenarios"][scenarioNum]["steps"][stepNum]["stepText"];
          scenario["steps"].push(step);
        }
      }
    }
  } else if(msgType === 'executeInfos') {
    let featureId = jsonMessage['content']["featureId"];
    let scenarioId = jsonMessage['content']["scenarioId"];
    let stepId = jsonMessage['content']["stepId"];
    let status = jsonMessage['content']["status"];
    let errorMsg = jsonMessage['content']["errorMsg"];
    let startTime = jsonMessage['content']["startTime"];
    let endTime = jsonMessage['content']["endTime"];

    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).executeInfo = errorMsg;
    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).status = status;
    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).startTime = startTime;
    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).endTime = endTime;
  }
  else {
    console.log(msgType);
    console.log("other type hasn't complete yet");
  }
}

// ----- ----- ----- ----- -----
function feature_start(featureId){
  let startFeatureRequest = {
    msgType:"process",
    contentType:"feature_start",
    content:{
      featureId:featureId,
    }
  };
  sendMessage(startFeatureRequest);
}
// function scenario_palse(){
//
// }
// function scenario_stop(){
//
// }
// function scenario_replay(){
//
// }
</script>