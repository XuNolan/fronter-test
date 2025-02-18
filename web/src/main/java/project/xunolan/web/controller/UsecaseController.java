package project.xunolan.web.controller;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.web.amisRespVo.UsecaseDisplayListVo;
import project.xunolan.web.amisRespVo.UsecaseInsertVo;
import project.xunolan.web.amisRespVo.aspect.AmisResult;
import project.xunolan.web.amisRespVo.utils.Convert4Amis;
import project.xunolan.web.service.UsecaseService;
import project.xunolan.web.amisRespVo.UsecaseFilterParam;
import project.xunolan.web.amisRespVo.AmisCrudListVo;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/usecase")
@AmisResult
public class UsecaseController {
    @Autowired
    private UsecaseService usecaseService;


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

    @PostMapping("/update")
    public Usecase update(){
        return null;
    }

    @GetMapping("/list")
    public AmisCrudListVo queryDisplayList(UsecaseFilterParam usecaseFilterParam) {

        List<UsecaseDisplayListVo> usecaseDisplayListVos = usecaseService.queryList(usecaseFilterParam);

        List<Map<String, Object>> items = Convert4Amis.createItemsMap(usecaseDisplayListVos);
        return AmisCrudListVo.builder()
                .total(usecaseDisplayListVos.size())
                .items(items).page(usecaseFilterParam.getPage()).build();
    }

    @GetMapping("/query/{id}")
    public Map<String, Object> queryById(@PathVariable("id") Long id){
        return Convert4Amis.flatSingleMap(usecaseService.queryById(id));

    }


    @DeleteMapping("/delete")
    public void deleteById(@RequestParam("id") Long id){
        usecaseService.deleteById(id);
    }

    //todo:testOnce。
//
//    @GetMapping("/version_validation")
//    public void insertValidation(String version, String usecaseId){
//
//    }

}
