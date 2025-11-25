package in.shivam.rupeeroot.service;

import in.shivam.rupeeroot.dto.ChatMessageDTO;
import in.shivam.rupeeroot.entity.ChatMessage;
import in.shivam.rupeeroot.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageDTO saveMessage(ChatMessageDTO dto) {
        ChatMessage entity = ChatMessage.builder()
                .content(dto.getContent())
                .senderName(dto.getSenderName())
                .senderEmail(dto.getSenderEmail())
                .groupId(dto.getGroupId())
                .build();

        ChatMessage saved = chatMessageRepository.save(entity);

        // Return DTO with the generated timestamp
        return ChatMessageDTO.builder()
                .content(saved.getContent())
                .senderName(saved.getSenderName())
                .senderEmail(saved.getSenderEmail())
                .groupId(saved.getGroupId())
                .timestamp(saved.getTimestamp())
                .build();
    }

    public List<ChatMessageDTO> getChatHistory(String groupId) {
        return chatMessageRepository.findByGroupIdOrderByTimestampAsc(groupId)
                .stream()
                .map(msg -> ChatMessageDTO.builder()
                        .content(msg.getContent())
                        .senderName(msg.getSenderName())
                        .senderEmail(msg.getSenderEmail())
                        .groupId(msg.getGroupId())
                        .timestamp(msg.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}