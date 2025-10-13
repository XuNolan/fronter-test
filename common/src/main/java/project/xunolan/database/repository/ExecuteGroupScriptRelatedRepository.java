package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.xunolan.database.entity.ExecuteGroupScriptRelated;

import java.util.List;

@Repository
public interface ExecuteGroupScriptRelatedRepository extends JpaRepository<ExecuteGroupScriptRelated, Long> {
    
    /**
     * 根据执行组ID查询所有关联的脚本（按顺序）
     */
    List<ExecuteGroupScriptRelated> findByExecuteGroupIdOrderByIndexAsc(Long executeGroupId);
    
    /**
     * 根据执行组ID删除所有关联
     */
    void deleteByExecuteGroupId(Long executeGroupId);
    
    /**
     * 统计执行组中的脚本数量
     */
    long countByExecuteGroupId(Long executeGroupId);
}

