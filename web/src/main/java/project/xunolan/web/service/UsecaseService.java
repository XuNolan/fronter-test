package project.xunolan.web.service;

import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;
import project.xunolan.database.repository.entity.DisplayListVo;
import project.xunolan.web.amisRespVo.UsecaseFilterParam;

import java.util.List;


public interface UsecaseService {
    void save(Usecase usecase, Script script);
    Usecase update(Usecase usecase, Script script);
    List<DisplayListVo> queryList(UsecaseFilterParam usecaseFilterParam);
    Usecase queryById(Long id);
    void deleteById(Long id);
}
