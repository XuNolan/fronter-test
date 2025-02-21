package project.xunolan.service;

import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.web.amisEntity.UsecaseDisplayListVo;
import project.xunolan.web.amisEntity.UsecaseFilterParam;

import java.util.List;

//todo: 搬移至common模块。返回信息和VO与DTO解耦。
public interface UsecaseService {
    void save(Usecase usecase, Script script);
    Usecase update(Usecase usecase, Script script);
    List<UsecaseDisplayListVo> queryList(UsecaseFilterParam usecaseFilterParam);
    Usecase queryById(Long id);
    void deleteById(Long id);
}
