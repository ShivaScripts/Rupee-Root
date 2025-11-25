package in.shivam.rupeeroot.repository;

import in.shivam.rupeeroot.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // -------------------------------------------------------
    // EXISTING METHODS (Keep these for backward compatibility)
    // -------------------------------------------------------

    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    // -------------------------------------------------------
    // NEW METHODS (Added for Collaborative/Group Features)
    // -------------------------------------------------------

    // 1. Fetch incomes for multiple users (The Group) between dates
    List<IncomeEntity> findByProfileIdInAndDateBetween(List<Long> profileIds, LocalDate startDate, LocalDate endDate);

    // 2. Fetch top 5 incomes for the whole group
    List<IncomeEntity> findTop5ByProfileIdInOrderByDateDesc(List<Long> profileIds);

    // 3. Calculate total income for the whole group
    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id IN :profileIds")
    BigDecimal findTotalIncomeByProfileIds(@Param("profileIds") List<Long> profileIds);

    // 4. Search/Filter incomes for the whole group
    List<IncomeEntity> findByProfileIdInAndDateBetweenAndNameContainingIgnoreCase(
            List<Long> profileIds,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );
}