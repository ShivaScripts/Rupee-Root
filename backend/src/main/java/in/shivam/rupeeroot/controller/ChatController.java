package in.shivam.rupeeroot.controller;

import in.shivam.rupeeroot.dto.ChatMessageDTO;
import in.shivam.rupeeroot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // --- WEBSOCKET HANDLING ---
    // Frontend sends to: /app/chat/{groupId}
    @MessageMapping("/chat/{groupId}")
    public void sendMessage(@DestinationVariable String groupId, @Payload ChatMessageDTO chatMessage) {

        // 1. Save the message to MySQL
        chatMessage.setGroupId(groupId);
        ChatMessageDTO savedMsg = chatService.saveMessage(chatMessage);

        // 2. Broadcast to everyone in the group
        // Topic: /topic/groups/{groupId}/chat
        messagingTemplate.convertAndSend("/topic/groups/" + groupId + "/chat", savedMsg);
    }

    // --- REST API HANDLING ---
    // Get history: GET /api/v1.0/chat/history/{groupId}
    @GetMapping("/chat/history/{groupId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable String groupId) {
        return ResponseEntity.ok(chatService.getChatHistory(groupId));
    }
}