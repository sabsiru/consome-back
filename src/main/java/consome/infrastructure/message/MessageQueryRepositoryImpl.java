package consome.infrastructure.message;

import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.domain.message.entity.Message;
import consome.domain.message.repository.MessageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static consome.domain.message.entity.QMessage.message;

@Repository
@RequiredArgsConstructor
public class MessageQueryRepositoryImpl implements MessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Message> findReceivedMessages(Long receiverId, Pageable pageable) {
        List<Message> content = queryFactory
                .selectFrom(message)
                .where(
                        message.receiverId.eq(receiverId),
                        message.isDeletedByReceiver.eq(false)
                )
                .orderBy(message.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(message.count())
                .from(message)
                .where(
                        message.receiverId.eq(receiverId),
                        message.isDeletedByReceiver.eq(false)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<Message> findSentMessages(Long senderId, Pageable pageable) {
        List<Message> content = queryFactory
                .selectFrom(message)
                .where(
                        message.senderId.eq(senderId),
                        message.isDeletedBySender.eq(false)
                )
                .orderBy(message.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(message.count())
                .from(message)
                .where(
                        message.senderId.eq(senderId),
                        message.isDeletedBySender.eq(false)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
