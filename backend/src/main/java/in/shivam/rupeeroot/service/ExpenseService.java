package in.shivam.rupeeroot.service;

import in.shivam.rupeeroot.dto.ExpenseDTO;
import in.shivam.rupeeroot.entity.CategoryEntity;
import in.shivam.rupeeroot.entity.ExpenseEntity;
import in.shivam.rupeeroot.entity.ProfileEntity;
import in.shivam.rupeeroot.repository.CategoryRepository;
import in.shivam.rupeeroot.repository.ExpenseRepository;
import in.shivam.rupeeroot.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final CacheManager cacheManager;

    private List<Long> getFamilyProfileIds(ProfileEntity currentProfile) {
        if (currentProfile.getGroupId() == null || currentProfile.getGroupId().isEmpty()) {
            return List.of(currentProfile.getId());
        } else {
            List<ProfileEntity> familyMembers = profileRepository.findByGroupId(currentProfile.getGroupId());
            return familyMembers.stream()
                    .map(ProfileEntity::getId)
                    .collect(Collectors.toList());
        }
    }

    private void clearDashboardCacheForGroup(ProfileEntity currentProfile) {
        List<Long> familyIds = getFamilyProfileIds(currentProfile);
        Cache dashboardCache = cacheManager.getCache("dashboard");
        if (dashboardCache != null) {
            for (Long id : familyIds) {
                dashboardCache.evict(id);
            }
        }
    }

    public ExpenseDTO addExpense(ExpenseDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ExpenseEntity entity = toEntity(dto, profile, category);
        ExpenseEntity saved = expenseRepository.save(entity);

        // Check and notify for personal budget
        checkAndNotifyPersonalBudget(profile);

        clearDashboardCacheForGroup(profile);

        return toDTO(saved);
    }

    private void checkAndNotifyPersonalBudget(ProfileEntity profile) {
        if (profile.getBudgetLimit() == null || profile.getBudgetLimit().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        List<ExpenseEntity> personalExpenses = expenseRepository.findByProfileIdInAndDateBetween(
                List.of(profile.getId()), start, end
        );

        BigDecimal totalPersonalExpense = personalExpenses.stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPersonalExpense.compareTo(profile.getBudgetLimit()) > 0) {
            String subject = "Budget Alert: Limit Exceeded";
            String body = "Hello " + profile.getFullName() + ",\n\n" +
                    "You have exceeded your monthly personal budget limit.\n\n" +
                    "Budget Limit: " + profile.getBudgetLimit() + "\n" +
                    "Current Month Expenses: " + totalPersonalExpense + "\n\n" +
                    "Please manage your expenses accordingly.\n\n" +
                    "Best,\nRupeeRoot Team";

            emailService.sendEmail(profile.getEmail(), subject, body);
        }
    }
    // --- NEW METHOD: Settle Debt ---
    public ExpenseDTO settleDebt(ExpenseDTO dto) {
        ProfileEntity payer = profileService.getCurrentProfile();

        // Find the receiver (The person getting paid)
        ProfileEntity receiver = profileRepository.findById(dto.getSettledToId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // We need a dummy category for Settlements (Database constraint)
        // Try to find "Settlement" or "Others", fallback to the first available category
        CategoryEntity category = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase("Settlement") || c.getName().equalsIgnoreCase("Others"))
                .findFirst()
                .orElseGet(() -> categoryRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No categories available")));

        ExpenseEntity settlement = ExpenseEntity.builder()
                .name("Settlement: " + payer.getFullName() + " -> " + receiver.getFullName())
                .amount(dto.getAmount())
                .date(LocalDate.now())
                .profile(payer)          // Who paid
                .settledTo(receiver)     // Who received
                .category(category)      // Required field
                .isSettlement(true)      // FLAG: It's a settlement
                .isSplittable(false)     // FLAG: Don't split this, it's direct
                .icon("🤝")              // Handshake icon
                .build();

        ExpenseEntity saved = expenseRepository.save(settlement);
        return toDTO(saved);
    }
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Long> familyIds = getFamilyProfileIds(profile);
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<ExpenseEntity> list = expenseRepository.findByProfileIdInAndDateBetween(familyIds, startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalExpenseForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Long> familyIds = getFamilyProfileIds(profile);
        BigDecimal total = expenseRepository.findTotalExpenseByProfileIds(familyIds);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Long> familyIds = getFamilyProfileIds(profile);
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdInOrderByDateDesc(familyIds);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteExpense(Long id) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(entity);

        clearDashboardCacheForGroup(profile);
    }

    public List<ExpenseDTO> getExpensesForUser(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        List<Long> familyIds;
        if (profileId == null) {
            ProfileEntity profile = profileService.getCurrentProfile();
            familyIds = getFamilyProfileIds(profile);
        } else {
            familyIds = List.of(profileId);
        }

        List<ExpenseEntity> list = expenseRepository.findByProfileIdInAndDateBetweenAndNameContainingIgnoreCase(familyIds, startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDTO).toList();
    }

    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                // --- MAP NEW FIELD ---
                .isSplittable(dto.getIsSplittable() != null ? dto.getIsSplittable() : true)
                // ---------------------
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity entity) {
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .creatorName(entity.getProfile().getFullName())
                .isSplittable(entity.getIsSplittable())
                .isSettlement(entity.getIsSettlement())
                .build();
    }
}