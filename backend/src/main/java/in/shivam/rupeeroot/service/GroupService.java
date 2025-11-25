package in.shivam.rupeeroot.service;

import in.shivam.rupeeroot.dto.DebtSettlementDTO; // Import DTO
import in.shivam.rupeeroot.dto.ProfileDTO;
import in.shivam.rupeeroot.entity.ExpenseEntity;
import in.shivam.rupeeroot.entity.ProfileEntity;
import in.shivam.rupeeroot.repository.ExpenseRepository; // Import Repo
import in.shivam.rupeeroot.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ProfileRepository profileRepository;
    private final ExpenseRepository expenseRepository; // 1. Inject ExpenseRepo
    private final EmailService emailService;
    private final DebtSimplificationService debtService; // 2. Inject Algorithm Service

    private ProfileEntity getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // ... (Keep createGroup, joinGroup, inviteMember as they were) ...
    public ProfileDTO createGroup() {
        ProfileEntity user = getLoggedInUser();
        if (user.getGroupId() != null && !user.getGroupId().isEmpty()) {
            throw new RuntimeException("You are already in a group (ID: " + user.getGroupId() + ")");
        }
        String newGroupId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        user.setGroupId(newGroupId);
        ProfileEntity saved = profileRepository.save(user);
        return mapToDTO(saved);
    }

    public ProfileDTO joinGroup(String groupId) {
        ProfileEntity user = getLoggedInUser();
        if (user.getGroupId() != null && !user.getGroupId().isEmpty()) {
            throw new RuntimeException("You are already in a group. Leave it before joining another.");
        }
        String cleanGroupId = groupId.trim().toUpperCase();
        List<ProfileEntity> groupMembers = profileRepository.findByGroupId(cleanGroupId);
        if (groupMembers.isEmpty()) {
            throw new RuntimeException("Invalid Group ID. No such group exists.");
        }
        user.setGroupId(cleanGroupId);
        ProfileEntity saved = profileRepository.save(user);
        return mapToDTO(saved);
    }

    public void inviteMember(String emailToInvite) {
        ProfileEntity currentUser = getLoggedInUser();
        String groupCode = currentUser.getGroupId();
        if (groupCode == null || groupCode.isEmpty()) {
            throw new RuntimeException("You are not part of a group yet. Create one first!");
        }
        emailService.sendGroupInvitation(emailToInvite, currentUser.getFullName(), groupCode);
    }

    // --- NEW METHOD FOR STAGE 2 ---
    public List<DebtSettlementDTO> getGroupDebts() {
        ProfileEntity currentUser = getLoggedInUser();

        // 1. Check if user is in a group
        if (currentUser.getGroupId() == null || currentUser.getGroupId().isEmpty()) {
            return List.of(); // Return empty list if no group
        }

        // 2. Get all group members
        List<ProfileEntity> groupMembers = profileRepository.findByGroupId(currentUser.getGroupId());
        List<Long> memberIds = groupMembers.stream().map(ProfileEntity::getId).collect(Collectors.toList());

        // 3. Get ALL expenses for these members
        List<ExpenseEntity> groupExpenses = expenseRepository.findByProfileIdIn(memberIds);

        // 4. Run the Algorithm
        return debtService.simplifyDebts(groupExpenses, groupMembers);
    }
    // ------------------------------

    private ProfileDTO mapToDTO(ProfileEntity entity) {
        return ProfileDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .groupId(entity.getGroupId())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}