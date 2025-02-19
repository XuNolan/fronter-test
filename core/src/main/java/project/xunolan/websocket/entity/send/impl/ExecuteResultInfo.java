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
    private String scenarioName;
    private int stepLine;

    private final long durationNanos;
    private final boolean aborted;
    private final String error;
    private final long startTime;
    private final long endTime;

    public ExecuteResultInfo(String scenarioName, int stepLine, long durationNanos, boolean aborted, String error,long startTime, long endTime) {
        this.scenarioName = scenarioName;
        this.stepLine = stepLine;
        this.durationNanos = durationNanos;
        this.aborted = aborted;
        this.error = error;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ExecuteResultInfo fromResult(StepResult stepResult, ScenarioRuntime sr) {
        Result result = stepResult.getResult();
        return new ExecuteResultInfo(
                sr.scenario.getName(),
                stepResult.getStep().getLine(),
                result.getDurationNanos(),
                result.isAborted(),
                result.getErrorMessage(),
                result.getStartTime(),
                result.getEndTime());
    }
}