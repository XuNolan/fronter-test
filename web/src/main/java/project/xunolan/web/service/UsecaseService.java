package project.xunolan.web.service;

import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.common.entity.UsecaseDisplayListVo;
import project.xunolan.web.amisRespVo.UsecaseFilterParam;

import java.util.List;


public interface UsecaseService {
    void save(Usecase usecase, Script script);
    Usecase update(Usecase usecase, Script script);
    List<UsecaseDisplayListVo> queryList(UsecaseFilterParam usecaseFilterParam);
    Usecase queryById(Long id);
    void deleteById(Long id);
}
