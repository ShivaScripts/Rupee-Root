package in.shivam.rupeeroot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebtSettlementDTO {
    private String fromUserName;
    private Long fromUserId; // --- NEW FIELD: Identifies the Debtor ---

    private String toUserName;
    private Long toUserId;

    private BigDecimal amount;
    private String avatarUrl;
}