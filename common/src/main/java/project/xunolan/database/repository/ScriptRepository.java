package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.xunolan.database.entity.Script;

import java.util.List;

public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findAllByUsecaseId(Long scriptId);
    Script findOneByUsecaseIdAndIsActive(Long scriptId, Boolean isActive);
}
