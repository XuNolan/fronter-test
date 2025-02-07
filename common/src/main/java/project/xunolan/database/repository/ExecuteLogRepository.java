package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.xunolan.database.entity.ExecuteLog;

import java.util.List;

public interface ExecuteLogRepository extends JpaRepository<ExecuteLog, Long> {
    List<ExecuteLog> findAllByScriptId(Long scriptId);
    ExecuteLog findFirstByScriptIdOrderByExecuteTimeDesc(Long scriptId);
}
