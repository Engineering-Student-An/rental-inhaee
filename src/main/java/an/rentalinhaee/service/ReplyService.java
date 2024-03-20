package an.rentalinhaee.service;

import an.rentalinhaee.domain.*;
import an.rentalinhaee.repository.BoardRepository;
import an.rentalinhaee.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Reply reply(String stuId, String name, String content, Long boardId) {
        Board board = boardRepository.findBoardById(boardId);
        Reply reply = Reply.createReply(stuId, name, content, board);
        return replyRepository.save(reply);
    }

    @Transactional
    public void like(Long id, String stuId){
        Reply reply = replyRepository.findReplyById(id);
        reply.like(stuId);
    }

    @Transactional
    public void delete(Long id) {
        replyRepository.delete(replyRepository.findReplyById(id));
    }
}
