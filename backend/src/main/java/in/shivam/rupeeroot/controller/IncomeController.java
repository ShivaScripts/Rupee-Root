package in.shivam.rupeeroot.controller;

import in.shivam.rupeeroot.annotation.LogActivity;
import in.shivam.rupeeroot.dto.IncomeDTO;
import in.shivam.rupeeroot.entity.ProfileEntity; // Import this
import in.shivam.rupeeroot.service.IncomeService;
import in.shivam.rupeeroot.service.ProfileService; // Import this
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;
    private final ProfileService profileService; // 1. Inject ProfileService
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @LogActivity(value = "INCOME_ADDED", description = "Added a new income source")
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto) {
        IncomeDTO saved = incomeService.addIncome(dto);

        // 2. Get Current User Profile
        ProfileEntity currentUser = profileService.getCurrentProfile();

        // 3. Determine Destination based on Group Status (Matches Frontend AppContext.jsx logic)
        String destination;
        if (currentUser.getGroupId() != null && !currentUser.getGroupId().isEmpty()) {
            // If user is in a group, send to the group topic
            destination = "/topic/groups/" + currentUser.getGroupId() + "/incomes";
        } else {
            // If user is independent, send to their specific user topic
            destination = "/topic/user/" + currentUser.getId() + "/incomes";
        }

        // 4. Send the message to the specific destination
        messagingTemplate.convertAndSend(destination, "Income Added");

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getExpenses() {
        List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/{id}")
    @LogActivity(value = "INCOME_DELETED", description = "Deleted an income source")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);

        // 5. Replicate logic for delete (Optional but recommended for real-time updates on delete)
        ProfileEntity currentUser = profileService.getCurrentProfile();
        String destination;
        if (currentUser.getGroupId() != null && !currentUser.getGroupId().isEmpty()) {
            destination = "/topic/groups/" + currentUser.getGroupId() + "/incomes";
        } else {
            destination = "/topic/user/" + currentUser.getId() + "/incomes";
        }
        messagingTemplate.convertAndSend(destination, "Income Deleted");

        return ResponseEntity.noContent().build();
    }
}