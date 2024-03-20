package an.rentalinhaee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Reply {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String stuId;
    private String name;

    private List<String> likeNumber = new ArrayList<>();

    @Column(length = 5000)
    private String content;
    private LocalDateTime writeTime;

    public void setBoard(Board board){
        this.board = board;
        board.getReplyList().add(this);
    }

    public void like(String stuId){
        if(this.likeNumber.contains(stuId)){
            this.likeNumber.remove(stuId);
        } else {
            this.likeNumber.add(stuId);
        }
    }

    public boolean isLike(String stuId){
        if(this.likeNumber.contains(stuId)) {
            return true;
        } else {
            return false;
        }
    }

    public static Reply createReply(String stuId, String name, String content, Board board) {
        Reply reply = new Reply();

        reply.setStuId(stuId);
        reply.setName(name);
        reply.setContent(content);
        reply.setWriteTime(LocalDateTime.now());
        reply.setBoard(board);

        return reply;
    }


}
