package an.rentalinhaee.domain;

import an.rentalinhaee.domain.dto.BoardForm;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String stuId;
    private String name;

    private String title;

    @Column(length = 5000)
    private String content;

    private LocalDateTime writeTime;
    private LocalDateTime editTime;

    @ElementCollection
    private List<String> likeNumber = new ArrayList<>();

    private boolean notice;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Reply> replyList = new ArrayList<>();

    public void like(String stuId){
        if(this.likeNumber.contains(stuId)){
            this.likeNumber.remove(stuId);
        } else {
            this.likeNumber.add(stuId);
        }
    }

    public boolean isLike(String stuId) {
        if(this.likeNumber.contains(stuId)){
            return true;
        } else {
            return false;
        }
    }

    protected Board() { }
    public Board(String stuId, String name, String title, String content, LocalDateTime writeTime, boolean notice) {
        this.stuId = stuId;
        this.name = name;
        this.title = title;
        this.content = content;
        this.writeTime = writeTime;
        this.notice = notice;
    }

    public void edit(BoardForm boardForm){
        this.editTime = LocalDateTime.now();
        this.title = boardForm.getTitle();
        this.content = boardForm.getContent();
    }
}
