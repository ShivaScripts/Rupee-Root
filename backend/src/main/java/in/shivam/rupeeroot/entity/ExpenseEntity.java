package in.shivam.rupeeroot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String icon;
    private LocalDate date;
    private BigDecimal amount;

    // Existing field
    @Builder.Default
    private Boolean isSplittable = true;

    // --- NEW FIELDS FOR SETTLEMENT ---
    @Builder.Default
    @Column(name = "is_settlement")
    private Boolean isSettlement = false;

    // The person receiving the payment (Creditor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settled_to_id")
    private ProfileEntity settledTo;
    // ---------------------------------

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
        if (this.isSplittable == null) {
            this.isSplittable = true;
        }
        // Ensure isSettlement is never null
        if (this.isSettlement == null) {
            this.isSettlement = false;
        }
    }
}