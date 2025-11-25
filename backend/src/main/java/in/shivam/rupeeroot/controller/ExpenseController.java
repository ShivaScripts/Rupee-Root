package in.shivam.rupeeroot.controller;

import in.shivam.rupeeroot.annotation.LogActivity;
import in.shivam.rupeeroot.dto.ExpenseDTO;
import in.shivam.rupeeroot.entity.ProfileEntity; // Imported ProfileEntity
import in.shivam.rupeeroot.service.ExpenseService;
import in.shivam.rupeeroot.service.ProfileService; // Imported ProfileService
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ProfileService profileService; // 1. Injected ProfileService
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @LogActivity(value = "EXPENSE_ADDED", description = "Added a new expense")
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto) {
        ExpenseDTO saved = expenseService.addExpense(dto);

        // 2. Get Current User Profile
        ProfileEntity currentUser = profileService.getCurrentProfile();

        // 3. Determine Destination based on Group Status
        String destination;
        if (currentUser.getGroupId() != null && !currentUser.getGroupId().isEmpty()) {
            // If user is in a group, send to the group topic
            destination = "/topic/groups/" + currentUser.getGroupId() + "/expenses";
        } else {
            // If user is independent, send to their specific user topic
            destination = "/topic/user/" + currentUser.getId() + "/expenses";
        }

        // 4. Send the message to the dynamic destination
        messagingTemplate.convertAndSend(destination, "Expense Added");

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PostMapping("/settle")
    @LogActivity(value = "DEBT_SETTLED", description = "Settled a debt")
    public ResponseEntity<ExpenseDTO> settleDebt(@RequestBody ExpenseDTO dto) {
        // 1. Process the settlement
        ExpenseDTO saved = expenseService.settleDebt(dto);

        // 2. Notify Group Members (So their graph updates instantly)
        ProfileEntity currentUser = profileService.getCurrentProfile();
        String destination;

        if (currentUser.getGroupId() != null && !currentUser.getGroupId().isEmpty()) {
            destination = "/topic/groups/" + currentUser.getGroupId() + "/expenses";
        } else {
            destination = "/topic/user/" + currentUser.getId() + "/expenses";
        }

        // Send message to trigger frontend refresh
        messagingTemplate.convertAndSend(destination, "Debt Settled");

        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses() {
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    @LogActivity(value = "EXPENSE_DELETED", description = "Deleted an expense")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);

        // 5. Replicate logic for delete to ensure real-time removal updates
        ProfileEntity currentUser = profileService.getCurrentProfile();
        String destination;
        if (currentUser.getGroupId() != null && !currentUser.getGroupId().isEmpty()) {
            destination = "/topic/groups/" + currentUser.getGroupId() + "/expenses";
        } else {
            destination = "/topic/user/" + currentUser.getId() + "/expenses";
        }

        messagingTemplate.convertAndSend(destination, "Expense Deleted");

        return ResponseEntity.noContent().build();
    }
}