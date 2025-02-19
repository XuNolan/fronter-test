package project.xunolan.websocket.entity.send.impl;

import com.intuit.karate.core.Result;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.core.StepResult;
import project.xunolan.websocket.entity.send.SendMsgBase;


public class ExecuteResultInfo extends SendMsgBase {
/*
private final String status;
    private final long durationNanos;
    private final boolean aborted;
    private final Throwable error;
    private final boolean skipped;
    private final StepRuntime.MethodMatch matchingMethod;

    private final long startTime;
    private final long endTime;
 */
    private final int scenarioIndex;
    private final int stepIndex;
    private final long durationNanos;
    private final boolean aborted;
    private final String error;
    private final long startTime;
    private final long endTime;

    public ExecuteResultInfo(int scenarioIndex, int stepIndex, long durationNanos, boolean aborted, String error,long startTime, long endTime) {
        this.scenarioIndex = scenarioIndex;
        this.stepIndex = stepIndex;

        this.durationNanos = durationNanos;
        this.aborted = aborted;
        this.error = error;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ExecuteResultInfo fromResult(int scenarioIndex, int stepIndex, StepResult stepResult) {
        Result result = stepResult.getResult();
        return new ExecuteResultInfo(
                scenarioIndex,
                stepIndex,
                result.getDurationNanos(),
                result.isAborted(),
                result.getErrorMessage(),
                result.getStartTime(),
                result.getEndTime());
    }
}