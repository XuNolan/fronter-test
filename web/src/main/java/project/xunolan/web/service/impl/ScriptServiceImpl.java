package project.xunolan.web.service.impl;

import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    public void newScript(Script script, Long usecaseId) {
        script.setUsecaseId(usecaseId)
                .setActive(false)
                .setCreated(Math.toIntExact(DateUtil.currentSeconds()))
                .setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        scriptRepository.save(script);
    }

    @Override
    public boolean ScriptVersionValidate(Long usecaseId, String scriptVersion) {
        List<String> versions = scriptRepository.findAllVersionsByUsecaseId(usecaseId);
        for(String version : versions){
            if(version.equals(scriptVersion)){
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void activeScript(Long usecaseId, Long scriptId) {
        scriptRepository.deactivePre(usecaseId);
        scriptRepository.updateIsActiveByUsecaseId(usecaseId, scriptId);
    }

    @Override
    @Transactional
    public void deActiveScript(Long scriptId) {
        scriptRepository.deactive(scriptId);
    }
}
