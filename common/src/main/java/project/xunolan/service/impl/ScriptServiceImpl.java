package project.xunolan.service.impl;

import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.database.repository.ScriptRepository;
import project.xunolan.service.ScriptService;

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

    public Script findOneByUsecaseIdAndIsActive(Long usecaseId, Boolean isActive) {
        return scriptRepository.findOneByUsecaseIdAndIsActive(usecaseId, isActive);
    }

    @Override
    public Script updateScript(Script script, Long scriptId) {
        if(scriptId == null){
            return null;
        }
        Script newScript = scriptRepository.findById(scriptId).orElse(null);
        if(newScript == null){
            return null;
        }
        newScript.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        newScript.setName(script.getName().isEmpty()? newScript.getName() : script.getName())
                .setVersion(script.getVersion().isEmpty()? newScript.getVersion() : script.getVersion())
                .setDescription(script.getDescription().isEmpty()? newScript.getDescription() : script.getDescription())
                .setData(script.getData().isEmpty()? newScript.getData() : script.getData());
        return scriptRepository.save(newScript);
    }

    @Override
    public void deleteScript(long scriptId) {
        scriptRepository.deleteById(scriptId);
    }
}
