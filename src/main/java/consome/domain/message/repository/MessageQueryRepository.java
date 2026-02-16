package consome.domain.message.repository;

import consome.domain.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageQueryRepository {

    Page<Message> findReceivedMessages(Long receiverId, Pageable pageable);

    Page<Message> findSentMessages(Long senderId, Pageable pageable);
}
