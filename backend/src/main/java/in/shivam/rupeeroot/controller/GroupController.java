package in.shivam.rupeeroot.controller;

import in.shivam.rupeeroot.dto.DebtSettlementDTO; // Import DTO
import in.shivam.rupeeroot.dto.ProfileDTO;
import in.shivam.rupeeroot.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<ProfileDTO> createGroup() {
        return ResponseEntity.ok(groupService.createGroup());
    }

    @PostMapping("/join")
    public ResponseEntity<ProfileDTO> joinGroup(@RequestBody Map<String, String> payload) {
        String groupId = payload.get("groupId");
        return ResponseEntity.ok(groupService.joinGroup(groupId));
    }

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteToGroup(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        groupService.inviteMember(email);
        return ResponseEntity.ok().build();
    }

    // --- NEW ENDPOINT (Stage 2) ---
    @GetMapping("/debts")
    public ResponseEntity<List<DebtSettlementDTO>> getGroupDebts() {
        return ResponseEntity.ok(groupService.getGroupDebts());
    }
}