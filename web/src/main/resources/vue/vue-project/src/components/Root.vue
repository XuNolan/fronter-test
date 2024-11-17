
<!--根组件，整体界面。-->
<!--且在此处负责完成与后端的交互-->
<script>
import {reactive} from "vue";
import {initWebSocket, sendMessage, webSocket} from "./websocket.js";
import Scenario from "./Scenario.vue";
let featureInfoReplies = [];
let features;
export default {
  components: {Scenario},
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
      features.push(JSON.parse(JSON.stringify(jsonObject[featureNum])));
    }
  } else if(msgType === 'executeInfos') {
    let toChange = features.at(jsonObject["featureId"]).scenarios.at(jsonObject["scenarioId"]).steps.at(jsonObject["stepId"]);
    toChange.executeInfo = jsonObject["errorMsg"];
    toChange.status = jsonObject["status"];
    toChange.startTime = jsonObject["startTime"];
    toChange.endTime = jsonObject["endTime"];
  }
  else {
    console.log("other type hasn't complete yet");
  }
}
</script>

<template>
  <head>
    <title>前端浏览器自动化脚本管理</title>
  </head>
  <body>
    <main>
      <div >
        <Scenario
            v-for="feature in features"
            :feature="feature"
            class="scenario_style"/>
      </div>

    </main>
  </body>
</template>

<style scoped>
.scenario_style{
  border: 4px solid red;
  margin: 5px;
}

</style>