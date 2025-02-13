package project.xunolan.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.database.entity.Script;
import project.xunolan.database.repository.ScriptRepository;
import project.xunolan.web.service.ScriptService;

import java.util.List;

@Service
public class ScriptServiceImpl implements ScriptService {
    @Autowired
    private ScriptRepository scriptRepository;

    @Override
    public List<Script> queryListByUsecaseId(Long usecaseId) {
        return scriptRepository.findAllByUsecaseId(usecaseId);
    }

    @Override
    public Script queryScriptByScriptId(Long scriptId) {
        return scriptRepository.findById(scriptId).orElse(null);
    }
}
