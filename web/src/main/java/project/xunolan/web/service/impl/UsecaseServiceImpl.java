package project.xunolan.web.service.impl;

import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import project.xunolan.database.entity.ExecuteLog;
import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.database.repository.ExecuteLogRepository;
import project.xunolan.database.repository.ScriptRepository;
import project.xunolan.database.repository.UsecaseRepository;
import project.xunolan.database.repository.entity.DisplayListVo;
import project.xunolan.web.service.UsecaseService;
import project.xunolan.web.amisRespVo.UsecaseFilterParam;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class UsecaseServiceImpl implements UsecaseService {

    @Autowired
    private UsecaseRepository usecaseRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private ExecuteLogRepository executeLogRepository;

    @Override
    public void save(Usecase usecase,  Script script) {
        //新增script和usecase；
        usecase.setCreated(Math.toIntExact(DateUtil.currentSeconds()));
        usecase.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        Usecase insertedUsecase = usecaseRepository.save(usecase);

        script.setId(insertedUsecase.getId());
        script.setCreated(Math.toIntExact(DateUtil.currentSeconds()));
        script.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        script.setUsecaseId(insertedUsecase.getId());
        script.setActive(true);//提交之后默认启用。
        scriptRepository.save(script);
    }

    @Override
    public Usecase update(Usecase usecase,  Script script) {
        return null;
    }

    @Override
    public Usecase queryById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        //todo:未来执行组中，无执行组信息才允许删除。
        List<Script> scripts = scriptRepository.findAllByUsecaseId(id);
        for(Script script : scripts){
            List<ExecuteLog> executeLogs = executeLogRepository.findAllByScriptId(script.getId());
            executeLogRepository.deleteAll(executeLogs);
            scriptRepository.delete(script);
        }
        usecaseRepository.deleteById(id);
    }

    @Override
    public List<DisplayListVo> queryList(UsecaseFilterParam param) {

        //需要根据usecaseFilterParam筛选相关方法
        PageRequest pageRequest = PageRequest.of(param.getPage() - 1, param.getPerPage());
        List<DisplayListVo> result = new ArrayList<>();
        //先找usecase；
        List<Usecase> usecases;
        if(param.getKeyword()!=null){
            usecases = usecaseRepository.findAllByNameContains(param.getKeyword());
        } else {
            usecases = usecaseRepository.findAll();
        }
        for(Usecase usecase : usecases){
            Long usecaseId = usecase.getId();
            Script activeScript = scriptRepository.findOneByUsecaseIdAndIsActive(usecase.getId(), true);
            ExecuteLog executeLog = null;
            if(activeScript != null){
                executeLog = executeLogRepository.findFirstByScriptIdOrderByExecuteTimeDesc(activeScript.getId());
            }
            result.add(DisplayListVo.builder()
                    .id(String.valueOf(usecaseId))
                    .usecaseName(usecase.getName())
                    .usecaseDescription(usecase.getDescription())
                    .scriptName(activeScript == null? null:activeScript.getName())
                    .scriptDescription(activeScript == null? null:activeScript.getDescription())
                    .version(activeScript == null? null:activeScript.getVersion())
                    .lastExecuteTime(executeLog == null? null: LocalDateTime.ofInstant(Instant.ofEpochSecond(executeLog.getExecuteTime()), ZoneId.systemDefault()).toString())
                    .status(executeLog == null? 0 : executeLog.getStatus()).build());
        }
        return result;
    }
}
