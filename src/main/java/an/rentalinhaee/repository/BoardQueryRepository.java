//package an.rentalinhaee.repository;
//
//import an.rentalinhaee.domain.Board;
//import an.rentalinhaee.domain.QBoard;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class BoardQueryRepository {
//
//    private final EntityManager em;
//
//
//    // 최근 게시글 쿼리
//    public List<Board> findRecentBoard() {
//        JPAQueryFactory query = new JPAQueryFactory(em);
//        QBoard board = QBoard.board;
//
//        long totalBoardCount = query.selectFrom(board).fetchCount();
//
//        if(totalBoardCount == 0) {
//            totalBoardCount = 1;
//        }
//        long limit = totalBoardCount < 5 ? totalBoardCount : 5;
//
//        return query.selectFrom(board)
//                .orderBy(board.writeTime.desc())
//                .limit(limit).fetch();
//    }
//
//    // hot 게시글 쿼리
//    public List<Board> findHotBoard() {
//        JPAQueryFactory query = new JPAQueryFactory(em);
//        QBoard board = QBoard.board;
//
//        long totalBoardCount = query.selectFrom(board).fetchCount();
//
//        if(totalBoardCount == 0) {
//            totalBoardCount = 1;
//        }
//        long limit = totalBoardCount < 5 ? totalBoardCount : 5;
//
//        return query.selectFrom(board)
//                .orderBy(board.likeNumber.size().desc(), board.writeTime.desc())
//                .where(board.writeTime.between(LocalDateTime.now().minusDays(7), LocalDateTime.now()))
//                .where(board.likeNumber.isNotEmpty())
//                .limit(limit).fetch();
//    }
//
//}
