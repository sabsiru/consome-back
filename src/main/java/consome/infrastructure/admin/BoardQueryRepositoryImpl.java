package consome.infrastructure.admin;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import consome.application.admin.BoardSearchCommand;
import consome.application.admin.BoardSearchResult;
import consome.application.board.UserBoardSearchResult;
import consome.domain.admin.repository.BoardQueryRepository;
import consome.domain.admin.QBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QBoard board = QBoard.board;

    @Override
    public Page<BoardSearchResult> findBoards(Pageable pageable) {
        List<BoardSearchResult> content = queryFactory
                .select(Projections.constructor(BoardSearchResult.class,
                        board.id,
                        board.name,
                        board.description,
                        board.displayOrder
                ))
                .from(board)
                .where(board.deleted.isFalse())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.displayOrder.asc())
                .fetch();

        Long total = queryFactory
                .select(board.count())
                .from(board)
                .where(board.deleted.isFalse())
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<BoardSearchResult> search(BoardSearchCommand command, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(board.deleted.isFalse());

        // 통합검색
        if (StringUtils.hasText(command.keyword())) {
            builder.and(
                    board.name.containsIgnoreCase(command.keyword())
                            .or(board.description.containsIgnoreCase(command.keyword()))
                            .or(board.id.stringValue().contains(command.keyword()))
            );
        }

        // 개별검색
        if (command.id() != null) {
            builder.and(board.id.eq(command.id()));
        }
        if (StringUtils.hasText(command.name())) {
            builder.and(board.name.containsIgnoreCase(command.name()));
        }

        List<BoardSearchResult> content = queryFactory
                .select(Projections.constructor(BoardSearchResult.class,
                        board.id,
                        board.name,
                        board.description,
                        board.displayOrder
                ))
                .from(board)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.displayOrder.asc())
                .fetch();

        Long total = queryFactory
                .select(board.count())
                .from(board)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public List<UserBoardSearchResult> searchByKeyword(String keyword, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(board.deleted.isFalse());

        if (StringUtils.hasText(keyword)) {
            builder.and(board.name.containsIgnoreCase(keyword));
        }

        return queryFactory
                .select(Projections.constructor(UserBoardSearchResult.class,
                        board.id,
                        board.name
                ))
                .from(board)
                .where(builder)
                .orderBy(board.name.asc())
                .limit(limit)
                .fetch();
    }
}
