package in.shivam.rupeeroot.controller;

import in.shivam.rupeeroot.entity.ProfileEntity; // Import ProfileEntity
import in.shivam.rupeeroot.service.DashboardService;
import in.shivam.rupeeroot.service.ProfileService; // Import ProfileService
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProfileService profileService; // 1. Inject ProfileService

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        // 2. Get the current user's ID
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        Long userId = currentProfile.getId();

        // 3. Pass the userId to the service (which will use it as the cache key)
        Map<String, Object> dashboardData = dashboardService.getDashboardData(userId);

        return ResponseEntity.ok(dashboardData);
    }
}