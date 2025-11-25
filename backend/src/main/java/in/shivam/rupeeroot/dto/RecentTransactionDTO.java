package in.shivam.rupeeroot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable; // Import this
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentTransactionDTO implements Serializable { // Implement Serializable

    private static final long serialVersionUID = 1L; // Best practice

    private Long id;
    private Long profileId;
    private String icon;
    private String name;
    private BigDecimal amount;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String type;
    private String creatorName;
}