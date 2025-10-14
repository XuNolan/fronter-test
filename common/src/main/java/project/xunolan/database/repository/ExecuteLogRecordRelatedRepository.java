package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.xunolan.database.entity.ExecuteLogRecordRelated;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExecuteLogRecordRelatedRepository extends JpaRepository<ExecuteLogRecordRelated, Long> {
    Optional<ExecuteLogRecordRelated> findByExecuteLogId(Long executeLogId);
    List<ExecuteLogRecordRelated> findByExecuteLogIdIn(List<Long> executeLogIds);
}


