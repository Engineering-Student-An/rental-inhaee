package an.rentalinhaee.repository;

import an.rentalinhaee.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findBoardById(Long id);

    Page<Board> findBoardsByStuId(Pageable pageable, String stuId);

    Page<Board> findBoardsByNoticeIs(Pageable pageable, boolean notice);
}
