package project.xunolan.service;

import javax.websocket.Session;

public enum SessionKeyEnum {
    SCRIPT_ID("scriptId"),
    USECASE_ID("usecaseId"),
    EXECUTE_GROUP_ID("executeGroupId"),

    RECORD_ID("recordId"),
    RECORD_FAIL("record_fail"),
    RECORD_START_TIME("recordStartTime"),
    RECORD_FILE_NAME("recordFileName"),

    EXECUTE_LOG_ID("executeLogId"),
    EXECUTE_START_TIME("executeStartTime"),

    FEATURE_STATUS("featureStatus"),

    ACC_EXECUTE_LOG("__acc_execute_log__");

    private final String key;

    SessionKeyEnum(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public void set(Session session, Object value) {
        session.getUserProperties().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Session session) {
        return (T) session.getUserProperties().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(Session session, T defaultValue) {
        Object v = session.getUserProperties().get(key);
        return v == null ? defaultValue : (T) v;
    }
}
