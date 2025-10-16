package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.xunolan.database.entity.ExecuteGroupLogRelated;

import java.util.List;

@Repository
public interface ExecuteGroupLogRelatedRepository extends JpaRepository<ExecuteGroupLogRelated, Long> {

    List<ExecuteGroupLogRelated> findByExecuteGroupId(Long executeGroupId);

    @Query("select distinct e.executeTermId from ExecuteGroupLogRelated e where e.executeGroupId = ?1 order by min(e.created) asc")
    List<String> findDistinctTermsByGroupIdOrderByCreatedAsc(Long executeGroupId);

    List<ExecuteGroupLogRelated> findByExecuteGroupIdAndExecuteTermId(Long groupId, String termId);
}


