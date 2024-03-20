package an.rentalinhaee.service;

import an.rentalinhaee.domain.Board;
import an.rentalinhaee.domain.dto.BoardForm;
import an.rentalinhaee.repository.BoardQueryRepository;
import an.rentalinhaee.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;

    @Transactional
    public void saveBoard(Board board){
        boardRepository.save(board);
    }

    @Transactional
    public void like(Long boardId, String stuId){
        Board board = boardRepository.findBoardById(boardId);
        board.like(stuId);
    }

    @Transactional
    public void delete(Long id){
        boardRepository.delete(boardRepository.findBoardById(id));
    }

    @Transactional
    public void edit(Long id, BoardForm boardForm){
        Board board = boardRepository.findBoardById(id);
        board.edit(boardForm);
    }

    public Page<Board> findAll(Pageable pageable){
        return boardRepository.findAll(pageable);
    }

    public Page<Board> findByNotice(Pageable pageable, boolean notice) {
        return boardRepository.findBoardsByNoticeIs(pageable, notice);
    }

    public Page<Board> findByStuId(Pageable pageable, String stuId){
        return boardRepository.findBoardsByStuId(pageable, stuId);
    }

    public Board findOne(Long id) {
        return boardRepository.findBoardById(id);
    }

    public List<Board> findRecentBoard() {
        return boardQueryRepository.findRecentBoard();
    }

    public List<Board> findHotBoard() { return boardQueryRepository.findHotBoard(); }
}
