package in.shivam.rupeeroot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String profileImageUrl;
    private String groupId;

    // --- NEW FIELD ---
    private BigDecimal budgetLimit;
    // -----------------

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}