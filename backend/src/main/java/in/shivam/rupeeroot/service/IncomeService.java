package in.shivam.rupeeroot.service;

import in.shivam.rupeeroot.dto.IncomeDTO;
import in.shivam.rupeeroot.entity.CategoryEntity;
import in.shivam.rupeeroot.entity.IncomeEntity;
import in.shivam.rupeeroot.entity.ProfileEntity;
import in.shivam.rupeeroot.repository.CategoryRepository;
import in.shivam.rupeeroot.repository.IncomeRepository;
import in.shivam.rupeeroot.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache; // Import Cache
import org.springframework.cache.CacheManager; // Import CacheManager
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    // 1. Inject CacheManager
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

    // 2. Helper method to manually clear cache for EVERYONE in the group
    private void clearDashboardCacheForGroup(ProfileEntity currentProfile) {
        List<Long> familyIds = getFamilyProfileIds(currentProfile);
        Cache dashboardCache = cacheManager.getCache("dashboard");
        if (dashboardCache != null) {
            for (Long id : familyIds) {
                dashboardCache.evict(id); // This deletes the cache entry for specific user ID
            }
        }
    }

    // 3. Removed @CacheEvict annotation
    public IncomeDTO addIncome(IncomeDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        IncomeEntity entity = toEntity(dto, profile, category);
        IncomeEntity saved = incomeRepository.save(entity);

        // 4. Trigger manual eviction
        clearDashboardCacheForGroup(profile);

        return toDTO(saved);
    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Long> familyIds = getFamilyProfileIds(profile);
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<IncomeEntity> list = incomeRepository.findByProfileIdInAndDateBetween(familyIds, startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Long> familyIds = getFamilyProfileIds(profile);
        BigDecimal total = incomeRepository.findTotalIncomeByProfileIds(familyIds);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Long> familyIds = getFamilyProfileIds(profile);
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdInOrderByDateDesc(familyIds);
        return list.stream().map(this::toDTO).toList();
    }

    public List<IncomeDTO> getIncomesForUser(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        List<Long> familyIds;
        if (profileId == null) {
            ProfileEntity profile = profileService.getCurrentProfile();
            familyIds = getFamilyProfileIds(profile);
        } else {
            familyIds = List.of(profileId);
        }

        List<IncomeEntity> list = incomeRepository.findByProfileIdInAndDateBetweenAndNameContainingIgnoreCase(familyIds, startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    // 3. Removed @CacheEvict annotation
    public void deleteIncome(Long id) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this income (created by another member)");
        }
        incomeRepository.delete(entity);

        // 4. Trigger manual eviction
        clearDashboardCacheForGroup(profile);
    }

    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity entity) {
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .creatorName(entity.getProfile().getFullName())
                .build();
    }
}