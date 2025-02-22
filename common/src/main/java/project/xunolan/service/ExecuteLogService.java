package project.xunolan.service;

import project.xunolan.database.entity.ExecuteLog;

public interface ExecuteLogService {
     ExecuteLog findFirstByScriptIdOrderByExecuteTimeDesc(Long scriptId);
}
