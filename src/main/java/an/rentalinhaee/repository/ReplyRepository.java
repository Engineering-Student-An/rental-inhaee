package an.rentalinhaee.repository;

import an.rentalinhaee.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository  extends JpaRepository<Reply, Long> {
    Reply findReplyById(Long id);

    List<Reply> findRepliesByStuId(String stuId);
}
