package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.xunolan.database.entity.ExecuteGroupId;

import java.util.Optional;

@Repository
public interface ExecuteGroupIdRepository extends JpaRepository<ExecuteGroupId, Long> {
    
    /**
     * 根据组名查询
     */
    Optional<ExecuteGroupId> findByGroupName(String groupName);
    
    /**
     * 检查组名是否存在
     */
    boolean existsByGroupName(String groupName);
}

