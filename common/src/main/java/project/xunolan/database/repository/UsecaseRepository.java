package project.xunolan.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.xunolan.database.entity.Usecase;

import java.util.List;

public interface UsecaseRepository extends JpaRepository<Usecase, Long> {

//    @Query(value = "SELECT new project.xunolan.database.repository.entity.DisplayListVo(A.name, A.description, B.name, B.description, C.executeTime, C.status) " +
//            "FROM Usecase as A " +
//            "   left join Script as B on A.id = B.usecaseId and B.isActive = true " +
//            "   left join ExecuteLog as C on B.id = C.scriptId " +
//            "    AND C.executeTime = (" +
//            "        SELECT MAX(executeTime)" +
//            "        FROM C" +
//            "        WHERE scriptId = B.id" +
//            ")" +
//            "order by A.scriptId desc "
//    )
//    List<DisplayListVo> queryDisplayListVo();

    List<Usecase> findAllByNameContains(String name);
}
