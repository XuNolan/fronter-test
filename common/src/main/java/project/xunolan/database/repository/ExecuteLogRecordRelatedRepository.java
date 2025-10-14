package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.xunolan.database.entity.ExecuteLogRecordRelated;

@Repository
public interface ExecuteLogRecordRelatedRepository extends JpaRepository<ExecuteLogRecordRelated, Long> {
}


