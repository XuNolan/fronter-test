package project.xunolan.web.service;

import org.springframework.stereotype.Service;
import project.xunolan.database.entity.Script;

import java.util.List;

public interface ScriptService {
    List<Script> queryListByUsecaseId(Long usecaseId);
    Script queryScriptByScriptId(Long scriptId);

    void newScript(Script script, Long usecaseId);
    boolean ScriptVersionValidate(Long usecaseId, String scriptVersion);

    void activeScript(Long usecaseId, Long scriptId);
    void deActiveScript(Long scriptId);
}
