package project.xunolan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.database.entity.ExecuteLog;
import project.xunolan.database.repository.ExecuteLogRepository;
import project.xunolan.service.ExecuteLogService;

@Service
public class ExecuteLogServiceImpl implements ExecuteLogService {

    @Autowired
    private ExecuteLogRepository executeLogRepository;

    public ExecuteLog findFirstByScriptIdOrderByExecuteTimeDesc(Long scriptId){
        return executeLogRepository.findFirstByScriptIdOrderByExecuteTimeDesc(scriptId);
    }
}
