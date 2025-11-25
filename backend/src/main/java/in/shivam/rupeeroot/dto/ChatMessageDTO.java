package in.shivam.rupeeroot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private String content;
    private String senderName;
    private String senderEmail;
    private String groupId;
    private LocalDateTime timestamp;
}