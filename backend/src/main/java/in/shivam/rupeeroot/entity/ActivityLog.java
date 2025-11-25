package in.shivam.rupeeroot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_activity_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // e.g., "EXPENSE_ADDED", "BUDGET_UPDATED"

    private String description; // e.g., "Added expense: Pizza (₹500)"

    private String userEmail; // Who performed the action

    private String groupId; // Which group does this belong to? (Optional, for filtering)

    @CreationTimestamp
    private LocalDateTime timestamp;
}