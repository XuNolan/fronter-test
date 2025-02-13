package project.xunolan.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.xunolan.common.entity.ScriptDisplayListVo;
import project.xunolan.database.entity.Script;
import project.xunolan.web.amisRespVo.AmisCrudListVo;
import project.xunolan.web.amisRespVo.ScriptFilterParam;
import project.xunolan.web.amisRespVo.aspect.AmisResult;
import project.xunolan.web.amisRespVo.utils.Convert4Amis;
import project.xunolan.web.service.ScriptService;
import project.xunolan.web.service.UsecaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AmisResult
@RequestMapping("/script")
public class ScriptController {
    @Autowired
    private ScriptService scriptService;

    @GetMapping("/list")
    public AmisCrudListVo queryDisplayList(ScriptFilterParam scriptFilterParam) {
        List<Script> scriptList = scriptService.queryListByUsecaseId(scriptFilterParam.getUsecaseId());
        List<ScriptDisplayListVo> scriptDisplayListVos = scriptList.stream()
                .map(ScriptDisplayListVo::fromScript)
                .toList();
        List<Map<String, Object>> items = Convert4Amis.createItemsMap(scriptDisplayListVos);
        return AmisCrudListVo.builder()
                .total(scriptDisplayListVos.size())
                .items(items).page(scriptFilterParam.getPage()).build();
    }

    @GetMapping("/info/{scriptId}")
    public Map<String, Object> queryScript(@PathVariable Long scriptId) {
        Script script = scriptService.queryScriptByScriptId(scriptId);
        return Convert4Amis.flatSingleMapWithPrefix("script", script);
    }

}
