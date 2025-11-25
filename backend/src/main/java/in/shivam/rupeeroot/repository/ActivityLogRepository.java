package in.shivam.rupeeroot.repository;

import in.shivam.rupeeroot.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Find logs for a specific user (if single)
    List<ActivityLog> findByUserEmailOrderByTimestampDesc(String userEmail);

    // Find logs for a specific group (if in a family)
    List<ActivityLog> findByGroupIdOrderByTimestampDesc(String groupId);

    // Find top 10 recent logs (for dashboard widget)
    List<ActivityLog> findTop10ByUserEmailOrGroupIdOrderByTimestampDesc(String userEmail, String groupId);
}