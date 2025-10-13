package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.xunolan.database.entity.RecordInfo;

import java.util.Optional;

@Repository
public interface RecordInfoRepository extends JpaRepository<RecordInfo, Long> {
    
    /**
     * 根据日志ID查询录像信息
     */
    Optional<RecordInfo> findByLogId(Long logId);
    
    /**
     * 检查日志是否有录像
     */
    boolean existsByLogId(Long logId);

    /**
     * 获取最近一次保存的全局配置（复用 RecordInfo 表，record_type = 'config'）
     */
    Optional<RecordInfo> findTopByRecordTypeOrderByRecordIdDesc(String recordType);
}

