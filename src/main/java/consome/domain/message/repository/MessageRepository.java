package consome.domain.message.repository;

import consome.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false AND m.isDeletedByReceiver = false")
    long countUnreadByReceiverId(@Param("receiverId") Long receiverId);
}
