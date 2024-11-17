<template>
  <text v-for="feature in features">
    <button id="start_btn" v-on:click="feature_start($(feature.featureId))">start</button>

<!--    <button id="palse" v-on:click="scenario_palse">palse</button>-->

<!--    <button id="stop" v-on:click="scenario_stop">stop</button>-->

<!--    <button id="replay" v-on:click="scenario_replay">replay</button>-->
    <text>{{feature.featureId}}</text>
    <text>{{feature.featureName}}</text>

    <text v-for="scenario in feature.scenarios">
      <text>{{scenario.scenarioId}}</text>
      <text>{{scenario.scenarioName}}</text>

      <text v-for="step in scenario.steps">
        <text>{{step.stepId}}</text>
        <text>{{step.prefix}}</text>
        <text>{{step.stepText}}</text>
        <text>{{step.status}}</text>
        <text>{{step.executeInfo}}</text>
        <text>{{step.startTime}}</text>
        <text>{{step.endTime}}</text>
      </text>

    </text>

  </text>

</template>
<script>
import {reactive} from "vue";
import {initWebSocket, isConnect, sendMessage, webSocket} from "./websocket.js";
let featureInfoReplies = [];
let features;
export default {
  setup(){
    let getScenarioReq = {
      msgType:"request",
      contentType:"request_feature",
      content:{}
    };
    initWebSocket("ws://127.0.0.1:8888/websocket", messageHandler);
    webSocket.addEventListener('open', function () {
      sendMessage(getScenarioReq);
    });
    features = reactive(featureInfoReplies);
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
  let jsonObject = JSON.parse(jsonMessage["content"]);
  console.log(msgType);
  console.log(jsonObject);
  if(msgType === 'featureInfos') {
    for(let featureNum = 0; featureNum < jsonObject.length; featureNum++) {
      let featureInfo = {};
      featureInfo['featureId'] = jsonObject[featureNum]["featureId"];
      featureInfo['featureName'] = jsonObject[featureNum]["featureName"];
      featureInfo['fileName'] = jsonObject[featureNum]["fileName"];
      featureInfo['scenarios'] = [];
      features.push(featureInfo);
      console.log('\n' + featureNum);
      console.log('\n' + featureInfo);
      for (let scenarioNum = 0; scenarioNum < jsonObject[featureNum]["scenarios"].length; scenarioNum++) {
        let scenario = {};
        scenario["scenarioId"] = jsonObject[featureNum]["scenarios"][scenarioNum]["scenarioId"];
        scenario["scenarioName"] = jsonObject[featureNum]["scenarios"][scenarioNum]["scenarioName"];
        scenario["steps"] = [];
        featureInfo["scenarios"].push(scenario);
        console.log('\n' + scenarioNum);
        console.log('\n' + featureInfo.name);
        for (let stepNum = 0; stepNum < jsonObject[featureNum]["scenarios"][scenarioNum]["steps"].length; stepNum++) {
          let step = {};
          step["stepId"] = jsonObject[featureNum]["scenarios"][scenarioNum]["steps"][stepNum]["stepId"];
          step["prefix"] = jsonObject[featureNum]["scenarios"][scenarioNum]["steps"][stepNum]["prefix"];
          step["stepText"] = jsonObject[featureNum]["scenarios"][scenarioNum]["steps"][stepNum]["stepText"];
          scenario["steps"].push(step);
          console.log('\n' + stepNum);
          console.log('\n' + step.stepText + '\n');
        }
      }
    }
  } else if(msgType === 'executeInfos') {
    let featureId = jsonObject["featureId"];
    let scenarioId = jsonObject["scenarioId"];
    let stepId = jsonObject["stepId"];
    let status = jsonObject["status"];
    let errorMsg = jsonObject["errorMsg"];
    let startTime = jsonObject["startTime"];
    let endTime = jsonObject["endTime"];

    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).executeInfo = errorMsg;
    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).status = status;
    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).startTime = startTime;
    featureInfoReplies.at(featureId).scenarios.at(scenarioId).steps.at(stepId).endTime = endTime;
  }
  // } else if(msgType === 'heartbeat'){
  //
  // }
  else {
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