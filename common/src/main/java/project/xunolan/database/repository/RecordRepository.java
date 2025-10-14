package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.xunolan.database.entity.Record;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
}


