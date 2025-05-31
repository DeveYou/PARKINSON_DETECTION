package mobile_java_project.repository;

import mobile_java_project.entity.Session;
import mobile_java_project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    List<Session> findByUserOrderByTimestampDesc(User user);
    
    Page<Session> findByUser(User user, Pageable pageable);
    
    Page<Session> findByUserAndPrediction(User user, int prediction, Pageable pageable);
} 