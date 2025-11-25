package in.shivam.rupeeroot.repository;

import in.shivam.rupeeroot.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Fetch all messages for a group, oldest first
    List<ChatMessage> findByGroupIdOrderByTimestampAsc(String groupId);
}