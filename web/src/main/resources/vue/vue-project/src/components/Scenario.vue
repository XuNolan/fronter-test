<template>
  <div>
    <p>{{feature.featureId}}</p>
    <p>{{feature.featureName}}</p>

    <div v-for="scenario in feature.scenarios">
      <p>{{scenario.scenarioId}}</p>
      <p>{{scenario.scenarioName}}</p>

      <div v-for="step in scenario.steps">
        <p>stepId : {{step.stepId}}</p>
        <p>step texts: {{step.prefix}}  {{step.stepText}}</p>
        <div class="status">
          <p>status: {{step.status}}</p>
          <p>executeInfo: {{step.executeInfo}}</p>
          <p>startTime: {{step.startTime}}</p>
          <p>endTime: {{step.endTime}}</p>
        </div>

      </div>

    </div>
    <div>
      <button id="start_btn" v-on:click="feature_start($(feature.featureId))">start</button>
    </div>
  </div>



<!--    <button id="palse" v-on:click="scenario_palse">palse</button>-->

<!--    <button id="stop" v-on:click="scenario_stop">stop</button>-->

<!--    <button id="replay" v-on:click="scenario_replay">replay</button>-->

</template>

<script>
import {sendMessage} from "./websocket.js";

export default {
  props: ['feature'],
  methods: {
    feature_start,
  }
}
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

<style scoped>
.status{
  border: 4px solid green;
}

</style>