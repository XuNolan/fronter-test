package project.xunolan.service;

import org.springframework.data.domain.Page;
import project.xunolan.database.entity.Script;
import project.xunolan.database.entity.Usecase;

import java.util.List;

public interface UsecaseService {
    void save(Usecase usecase, Script script);
    Usecase update(Usecase usecase, Long id);
    Usecase queryById(Long id);
    void deleteById(Long id);
    Page<Usecase> findAllByKeywordAndPageParam(String keyword, int curPage, int pageSize);
}
