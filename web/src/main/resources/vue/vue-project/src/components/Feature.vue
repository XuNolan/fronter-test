<template>
  <div class="feature_style">
    <div class="feature_container">
      <h2>feature {{feature.featureId}} : {{feature.featureName}}</h2>

      <div class="feature_btns">
        <button id="start_btn" v-on:click="feature_start(feature)" class="feature_btn">start</button>

        <button id="palse" >palse</button>

        <button id="stop" >stop</button>

        <button id="replay">replay</button>

        <input type="checkbox" id="checkbox" v-model="needRecord">
        <label for="checkbox">{{ needRecord }}</label>
      </div>
    </div>

    <div v-for="scenario in feature.scenarios">
      <h3 >scenarioId: {{scenario.scenarioId}} name : {{scenario.scenarioName}}</h3>
      <Step
          v-for="step in scenario.steps"
          :step="step"  />
    </div>

  </div>
</template>

<script>
import {sendMessage} from "./websocket.js";
import Step from "./Step.vue";
import {ref} from "vue";

let needRecord = ref(false);

export default {
  components: {Step},
  props: ['feature'],
  data() {
    return {needRecord};
  },
  methods: {
    feature_start,
  }
}
function feature_start(feature){
  let startFeatureRequest = {
    msgType:"process",
    contentType:"feature_start",
    content:{
      featureId:feature.featureId,
      needRecord: needRecord.value,
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
.feature_style{
  border: 4px solid red;
  margin: 5px;
}
.feature_container{
  display:flex;
  justify-content: space-between;
}
.feature_btns {
  display: flex;
  justify-content: space-between;
  margin-right: 100px;

}
.feature_btn{
  display: inline-block;
}
</style>