package in.shivam.rupeeroot.aspect;

import in.shivam.rupeeroot.annotation.LogActivity;
import in.shivam.rupeeroot.entity.ActivityLog;
import in.shivam.rupeeroot.entity.ProfileEntity;
import in.shivam.rupeeroot.repository.ActivityLogRepository;
import in.shivam.rupeeroot.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLoggingAspect {

    private final ActivityLogRepository activityLogRepository;
    private final ProfileService profileService;

    @AfterReturning(value = "@annotation(logActivity)", returning = "result")
    public void logActivity(JoinPoint joinPoint, LogActivity logActivity, Object result) {
        try {
            // 1. Get the current user
            ProfileEntity user = profileService.getCurrentProfile();

            // 2. Create the log entry
            ActivityLog log = ActivityLog.builder()
                    .action(logActivity.value())
                    .userEmail(user.getEmail())
                    .groupId(user.getGroupId())
                    .description(generateDescription(logActivity.description(), result))
                    .build();

            // 3. Save to DB
            activityLogRepository.save(log);

        } catch (Exception e) {
            // Silently fail logging so it doesn't stop the main transaction
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }

    // Helper to make the description dynamic based on what was returned
    private String generateDescription(String template, Object result) {
        // Simple logic: If we just saved something (DTO), try to append its name/amount if possible
        // For now, we will keep it simple and return the template.
        // You can expand this later to read 'result' and say "Added Expense: Pizza"
        return template;
    }
}