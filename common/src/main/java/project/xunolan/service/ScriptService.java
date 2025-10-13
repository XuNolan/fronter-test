package project.xunolan.service;


import project.xunolan.database.entity.Script;

import java.util.List;

public interface ScriptService {
    /**
     * 根据用例ID查询脚本列表
     */
    List<Script> queryListByUsecaseId(Long usecaseId);
    
    /**
     * 根据脚本ID查询单个脚本
     */
    Script queryScriptByScriptId(Long scriptId);

    /**
     * 创建新脚本
     */
    void newScript(Script script, Long usecaseId);
    
    /**
     * 校验脚本版本号
     */
    boolean ScriptVersionValidate(Long usecaseId, String scriptVersion);

    /**
     * 激活脚本
     */
    void activeScript(Long usecaseId, Long scriptId);
    
    /**
     * 取消激活脚本
     */
    void deActiveScript(Long scriptId);

    /**
     * 根据用例ID和激活状态查询单个脚本
     */
    Script findOneByUsecaseIdAndIsActive(Long usecaseId, Boolean isActive);
    
    /**
     * 更新脚本
     */
    Script updateScript(Script script, Long scriptId);

    /**
     * 删除脚本
     */
    void deleteScript(long scriptId);
    
    /**
     * 查询所有脚本
     */
    List<Script> findAll();
    
    /**
     * 查询所有启用的脚本
     */
    List<Script> findAllActiveScripts();
    
    /**
     * 查询所有启用的脚本（按用例ID排序）
     */
    List<Script> findAllActiveScriptsOrdered();
}
