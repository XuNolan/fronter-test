package project.xunolan.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.xunolan.database.entity.Usecase;

import java.util.List;

public interface UsecaseRepository extends JpaRepository<Usecase, Long> {

    List<Usecase> findAllByNameContains(String name);
    Page<Usecase> findByNameContainsOrderByIdAsc(String keywords, Pageable pageable);
    Page<Usecase> findAllByOrderByIdAsc(Pageable pageable);
}
