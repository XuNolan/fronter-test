package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.xunolan.database.entity.Script;

import java.util.List;

public interface ScriptRepository extends JpaRepository<Script, Long> {
    /**
     * 根据用例ID查询所有脚本
     */
    List<Script> findAllByUsecaseId(Long scriptId);
    
    /**
     * 根据用例ID和激活状态查询单个脚本
     */
    Script findOneByUsecaseIdAndIsActive(Long scriptId, Boolean isActive);

    /**
     * 查询所有启用的脚本
     */
    List<Script> findAllByIsActive(Boolean isActive);
    
    /**
     * 查询所有启用的脚本（按用例ID排序）
     */
    @Query("SELECT s FROM Script s WHERE s.isActive = true ORDER BY s.usecaseId ASC, s.id ASC")
    List<Script> findAllActiveScriptsOrdered();

    /**
     * 根据用例ID查询所有版本号
     */
    @Query("SELECT s.version FROM Script s where s.usecaseId = :usecaseId")
    List<String> findAllVersionsByUsecaseId(@Param("usecaseId") Long usecaseId);

    /**
     * 激活指定脚本
     */
    @Modifying
    @Query("UPDATE Script s set s.isActive = true where s.usecaseId = :usecaseId and s.id = :scriptId ")
    void updateIsActiveByUsecaseId(@Param("usecaseId") Long usecaseId, @Param("scriptId") Long scriptId);

    /**
     * 取消激活用例下的所有脚本
     */
    @Modifying
    @Query("UPDATE Script s set s.isActive = false where s.usecaseId = :usecaseId and s.isActive = true")
    void deactivePre(@Param("usecaseId") Long usecaseId);

    /**
     * 取消激活指定脚本
     */
    @Modifying
    @Query("UPDATE Script s set s.isActive = false where s.id = :scriptId")
    void deactive(@Param("scriptId") Long scriptId);
}
