package in.shivam.rupeeroot.repository;

import in.shivam.rupeeroot.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // --- 1. Modified Dashboard Methods (Ignore Settlements) ---

    // Fetch total for single user (Dashboard) - Exclude settlements
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId AND (e.isSettlement IS NULL OR e.isSettlement = false)")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    // Fetch list for single user - Exclude settlements
    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id = :profileId AND e.date BETWEEN :startDate AND :endDate AND (e.isSettlement IS NULL OR e.isSettlement = false)")
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    // Fetch list for single user with Search - Exclude settlements
    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id = :profileId AND e.date BETWEEN :startDate AND :endDate AND LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND (e.isSettlement IS NULL OR e.isSettlement = false)")
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    // Fetch latest 5 - Exclude settlements
    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id = :profileId AND (e.isSettlement IS NULL OR e.isSettlement = false) ORDER BY e.date DESC")
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // --- 2. Modified Group Methods (Ignore Settlements in Dashboard) ---

    // Total expense for group - Exclude settlements
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id IN :profileIds AND (e.isSettlement IS NULL OR e.isSettlement = false)")
    BigDecimal findTotalExpenseByProfileIds(@Param("profileIds") List<Long> profileIds);

    // List for group - Exclude settlements
    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id IN :profileIds AND e.date BETWEEN :startDate AND :endDate AND (e.isSettlement IS NULL OR e.isSettlement = false)")
    List<ExpenseEntity> findByProfileIdInAndDateBetween(List<Long> profileIds, LocalDate startDate, LocalDate endDate);

    // Search for group - Exclude settlements
    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id IN :profileIds AND e.date BETWEEN :startDate AND :endDate AND LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND (e.isSettlement IS NULL OR e.isSettlement = false)")
    List<ExpenseEntity> findByProfileIdInAndDateBetweenAndNameContainingIgnoreCase(List<Long> profileIds, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    // Latest 5 for group - Exclude settlements
    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id IN :profileIds AND (e.isSettlement IS NULL OR e.isSettlement = false) ORDER BY e.date DESC")
    List<ExpenseEntity> findTop5ByProfileIdInOrderByDateDesc(List<Long> profileIds);


    // --- 3. Methods kept as-is or Helper Methods ---

    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT e FROM ExpenseEntity e WHERE e.profile.id = :profileId AND e.date = :date AND (e.isSettlement IS NULL OR e.isSettlement = false)")
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

    // --- 4. NEW METHOD FOR DEBT SERVICE (Must Include Settlements) ---
    // This fetches EVERYTHING so the debt algorithm can see the repayments
    List<ExpenseEntity> findByProfileIdIn(List<Long> profileIds);
}