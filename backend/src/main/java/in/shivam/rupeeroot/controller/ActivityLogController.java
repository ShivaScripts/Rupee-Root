package in.shivam.rupeeroot.controller;

import in.shivam.rupeeroot.entity.ActivityLog;
import in.shivam.rupeeroot.entity.ProfileEntity;
import in.shivam.rupeeroot.repository.ActivityLogRepository;
import in.shivam.rupeeroot.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getRecentActivities() {
        ProfileEntity user = profileService.getCurrentProfile();

        // Fetch logs for the user or their entire group
        List<ActivityLog> logs = activityLogRepository.findTop10ByUserEmailOrGroupIdOrderByTimestampDesc(
                user.getEmail(),
                user.getGroupId()
        );

        return ResponseEntity.ok(logs);
    }
}