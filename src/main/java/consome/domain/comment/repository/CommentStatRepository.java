package consome.domain.comment.repository;

import consome.domain.comment.CommentStat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentStatRepository extends JpaRepository<CommentStat, Long> {
}
