package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.xunolan.database.entity.Script;

import java.util.List;

public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findAllByUsecaseId(Long scriptId);
    Script findOneByUsecaseIdAndIsActive(Long scriptId, Boolean isActive);

    @Query("SELECT s.version FROM Script s where s.usecaseId = :usecaseId")
    List<String> findAllVersionsByUsecaseId(@Param("usecaseId") Long usecaseId);

    @Modifying
    @Query("UPDATE Script s set s.isActive = true where s.usecaseId = :usecaseId and s.id = :scriptId ")
    void updateIsActiveByUsecaseId(@Param("usecaseId") Long usecaseId, @Param("scriptId") Long scriptId);

    @Modifying
    @Query("UPDATE Script s set s.isActive = false where s.usecaseId = :usecaseId and s.isActive = true")
    void deactivePre(@Param("usecaseId") Long usecaseId);

    @Modifying
    @Query("UPDATE Script s set s.isActive = false where s.id = :scriptId")
    void deactive(@Param("scriptId") Long scriptId);
}
