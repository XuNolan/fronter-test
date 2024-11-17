
<!--根组件，整体界面。-->
<!--且在此处负责完成与后端的交互-->
<script>
import {reactive} from "vue";
import {initWebSocket, sendMessage, webSocket} from "./websocket.js";
import Feature from "./Feature.vue";
let featureInfoReplies = [];
let features;
export default {
  components: {Feature},
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
//需要根据id来注入到对应位置，避免出现乱序？
//todo。暂时没出现问题。之后需要根据id来以数组形式进行逐步放入。待修复。
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
    console.log(features);
    features[jsonObject["featureId"]].scenarios[jsonObject["scenarioId"]].steps[jsonObject["stepId"]].executeInfo = jsonObject["errorMsg"];
    features[jsonObject["featureId"]].scenarios[jsonObject["scenarioId"]].steps[jsonObject["stepId"]].status = jsonObject["status"];
    features[jsonObject["featureId"]].scenarios[jsonObject["scenarioId"]].steps[jsonObject["stepId"]].startTime = jsonObject["startTime"];
    features[jsonObject["featureId"]].scenarios[jsonObject["scenarioId"]].steps[jsonObject["stepId"]].endTime = jsonObject["endTime"];
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
      <div>
        <Feature
            v-for="feature in features"
            :feature="feature"
            />
      </div>

    </main>
  </body>
</template>

<style scoped>

</style>