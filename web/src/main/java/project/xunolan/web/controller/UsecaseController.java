package project.xunolan.web.controller;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import project.xunolan.database.entity.ExecuteLog;
import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.service.ExecuteLogService;
import project.xunolan.service.ScriptService;
import project.xunolan.service.UsecaseService;
import project.xunolan.web.amisEntity.entity.AmisCrudListVo;
import project.xunolan.web.amisEntity.entity.usecaseVo.UsecaseDisplayListVo;
import project.xunolan.web.amisEntity.entity.params.UsecaseFilterParam;
import project.xunolan.web.amisEntity.entity.usecaseVo.UsecaseInsertVo;
import project.xunolan.web.amisEntity.aspect.AmisResult;
import project.xunolan.web.amisEntity.utils.Convert4Amis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/usecase")
public class UsecaseController {

    @Autowired
    private UsecaseService usecaseService;
    @Autowired
    private ScriptService scriptService;
    @Autowired
    private ExecuteLogService executeLogService;

    @AmisResult
    @PostMapping("/save")
    public void save(@RequestBody UsecaseInsertVo usecaseInsertVo) {
        Usecase usecase = Usecase.builder()
                .name(usecaseInsertVo.getUsecaseName())
                .description(usecaseInsertVo.getUsecaseDescription())
                .build();
        Script script = Script.builder()
                .name(usecaseInsertVo.getScriptName())
                .data(usecaseInsertVo.getScriptData())
                .description(StrUtil.isEmpty(usecaseInsertVo.getScriptDescription())?"":usecaseInsertVo.getScriptDescription())
                .version(usecaseInsertVo.getScriptVersion()).build();
        usecaseService.save(usecase, script);
    }

    @AmisResult
    @PostMapping("/update/{id}")
    public Usecase update(@RequestBody Usecase usecase, @PathVariable("id") Long id){
        return usecaseService.update(usecase, id);
    }

    @AmisResult
    @GetMapping("/list")
    public AmisCrudListVo queryDisplayList(UsecaseFilterParam usecaseFilterParam) {

        List<UsecaseDisplayListVo> result = new ArrayList<>();
        //先找usecase；
        Page<Usecase> usecases = usecaseService.findAllByKeywordAndPageParam(usecaseFilterParam.getKeywords(), usecaseFilterParam.getPage()-1, usecaseFilterParam.getPerPage());
        for(Usecase usecase : usecases){
            Long usecaseId = usecase.getId();
            Script activeScript = scriptService.findOneByUsecaseIdAndIsActive(usecase.getId(), true);
            ExecuteLog executeLog = null;
            if(activeScript != null){
                executeLog = executeLogService.findFirstByScriptIdOrderByExecuteTimeDesc(activeScript.getId());
            }
            result.add(UsecaseDisplayListVo.builder()
                    .id(String.valueOf(usecaseId))
                    .usecaseName(usecase.getName())
                    .usecaseDescription(usecase.getDescription())
                    .scriptName(activeScript == null? null:activeScript.getName())
                    .scriptDescription(activeScript == null? null:activeScript.getDescription())
                    .version(activeScript == null? null:activeScript.getVersion())
                    .lastExecuteTime(String.valueOf(executeLog == null? null: executeLog.getExecuteTime()))
                    .status(executeLog == null? 0 : executeLog.getStatus()).build());
        }

        List<Map<String, Object>> items = Convert4Amis.createItemsMap(result);
        return AmisCrudListVo.builder()
                .total((int) usecases.getTotalElements())
                .items(items).page(usecaseFilterParam.getPage()).build();
    }

    @AmisResult
    @GetMapping("/query/{id}")
    public Usecase queryById(@PathVariable("id") Long id){
        return usecaseService.queryById(id);

    }

    @AmisResult
    @DeleteMapping("/delete")
    public void deleteById(@RequestParam("id") Long id){
        usecaseService.deleteById(id);
    }


}
