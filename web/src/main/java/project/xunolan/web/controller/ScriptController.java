package project.xunolan.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.xunolan.common.entity.BasicResultVO;
import project.xunolan.service.ScriptService;
import project.xunolan.web.amisEntity.entity.scriptVo.ScriptDisplayListVo;
import project.xunolan.common.enums.RespStatusEnum;
import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.web.amisEntity.entity.AmisCrudListVo;
import project.xunolan.web.amisEntity.entity.params.ScriptFilterParam;
import project.xunolan.web.amisEntity.aspect.AmisResult;
import project.xunolan.web.amisEntity.utils.Convert4Amis;
import project.xunolan.service.UsecaseService;
import project.xunolan.web.amisEntity.entity.scriptVo.ScriptTestInfoVo;

import java.util.List;
import java.util.Map;

@RestController
@AmisResult
@RequestMapping("/script")
public class ScriptController {
    //返回要flat的对象。flat本身在切面中完成。

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private UsecaseService usecaseService;

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

    @PostMapping("/new")
    public void newScript(@RequestBody Script script, @RequestParam("usecaseId") Long usecaseId) {
        scriptService.newScript(script, usecaseId);
    }

    @GetMapping("/versionValidate")
    public BasicResultVO<String> versionValidate(@RequestParam("version") String version, @RequestParam("usecaseId") Long usecaseId) {
        if(version == null) return new BasicResultVO<>(RespStatusEnum.FAIL, "version empty", "");
        boolean isValid = scriptService.ScriptVersionValidate(usecaseId, version);
        if (!isValid) {
            return new BasicResultVO<>(RespStatusEnum.FAIL, "version existed", "");
        } else {
            return new BasicResultVO<>(RespStatusEnum.SUCCESS, "valid", "");
        }
    }

    @PostMapping("/active")
    public void activeScript(@RequestParam("usecaseId") Long usecaseId, @RequestParam("scriptId") Long scriptId) {
        scriptService.activeScript(usecaseId, scriptId);
    }

    @PostMapping("/deActive")
    public void deActiveScript( @RequestParam("scriptId") Long scriptId) {
        scriptService.deActiveScript(scriptId);
    }


    @GetMapping("/testInfo")
    public Map<String, Object> getTestInfo(@RequestParam("scriptId") Long scriptId){
        Script script = scriptService.queryScriptByScriptId(scriptId);
        Usecase usecase = usecaseService.queryById(script.getUsecaseId());
        ScriptTestInfoVo scriptTestInfoVo = ScriptTestInfoVo.builder()
                .usecaseName(usecase.getName())
                .usecaseDescription(usecase.getDescription())
                .scriptName(script.getName())
                .scriptDescription(script.getDescription())
                .version(script.getVersion())
                .data(script.getData()).build();
        return Convert4Amis.flatSingleMap(scriptTestInfoVo);
    }

}
