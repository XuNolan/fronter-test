package project.xunolan.service.impl;

import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.xunolan.database.entity.ExecuteLog;
import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.database.repository.ExecuteLogRepository;
import project.xunolan.database.repository.ScriptRepository;
import project.xunolan.database.repository.UsecaseRepository;
import project.xunolan.service.UsecaseService;


import java.util.*;

//todo: 搬移至common模块。
@Service
public class UsecaseServiceImpl implements UsecaseService {

    @Autowired
    private UsecaseRepository usecaseRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private ExecuteLogRepository executeLogRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Usecase usecase,  Script script) {
        //新增script和usecase；
        usecase.setCreated(Math.toIntExact(DateUtil.currentSeconds()));
        usecase.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        Usecase insertedUsecase = usecaseRepository.save(usecase);


        script.setCreated(Math.toIntExact(DateUtil.currentSeconds()));
        script.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        script.setUsecaseId(insertedUsecase.getId());
        script.setIsActive(false);//提交之后默认不启用。
        scriptRepository.save(script);
    }

    @Override
    public Usecase update(Usecase usecase, Long id) {
        if(id == null){
            return null;
        }
        Usecase updateUsecase = usecaseRepository.findById(id).orElse(null);
        if(updateUsecase == null){
            return null;
        }
        //更新
        updateUsecase.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        if(usecase.getName() != null){
            updateUsecase.setName(usecase.getName());
        }
        if(usecase.getDescription() != null){
            updateUsecase.setDescription(usecase.getDescription());
        }
        return usecaseRepository.save(updateUsecase);
    }

    @Override
    public Usecase queryById(Long id) {
        return usecaseRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    public Page<Usecase> findAllByKeywordAndPageParam(String keyword, int curPage, int pageSize) {
        if(keyword != null) {
            return usecaseRepository.findByNameContainsOrderByIdAsc(keyword, PageRequest.of(curPage, pageSize));
        } else {
            return usecaseRepository.findAllByOrderByIdAsc(PageRequest.of(curPage, pageSize));
        }
    }

}
