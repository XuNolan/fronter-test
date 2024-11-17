<script>
export default {
  props:['step'],
}
</script>

<template>
  <div class="step">
    <div class="text">
      <p>stepId : {{step.stepId}}</p>
      <p>step text: {{step.prefix}}  {{step.stepText}}</p>
    </div>
    <div class="status">
      <p>status: {{step.status}}</p>
      <p>executeInfo: {{step.executeInfo}}</p>
      <p>startTime: {{step.startTime}}</p>
      <p>endTime: {{step.endTime}}</p>
    </div>
  </div>
</template>

<style scoped>

.step{
  border: 4px solid green;
  display:flex;
  justify-content: space-between;
}
.text{
  display:inline-block
}
.status{
  display:inline-block;
  border: 3px solid darkgreen;
  margin-right: 100px;
}


</style>