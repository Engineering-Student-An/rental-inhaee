package an.rentalinhaee.service;

import an.rentalinhaee.domain.*;
import an.rentalinhaee.domain.dto.ChangePasswordRequest;
import an.rentalinhaee.domain.dto.JoinRequest;
import an.rentalinhaee.domain.dto.LoginRequest;
import an.rentalinhaee.repository.BoardRepository;
import an.rentalinhaee.repository.RentalRepository;
import an.rentalinhaee.repository.ReplyRepository;
import an.rentalinhaee.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RentalRepository rentalRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    // 학번 중복 검증
    public boolean checkStuIdDuplicate(String stuId){
        return studentRepository.existsByStuId(stuId);
    }

    // 회원가입
    public void join(JoinRequest joinRequest, String email){
        studentRepository.save(joinRequest.toEntity(email));
    }

    // 비밀번호 변경
    public void changePassword(String stuId, ChangePasswordRequest request) {
        Student student = studentRepository.findByStuId(stuId);
        student.editPassword(request.getChangePassword());
    }


    private final BoardService boardService;
    private final ReplyService replyService;
    private final RentalService rentalService;

    // 회원 탈퇴
    @Transactional
    public boolean delete(String stuId) {
        Student findStudent = studentRepository.findByStuId(stuId);

        if(!rentalRepository.existsByStudent_IdAndStatusNot(findStudent.getId(), RentalStatus.FINISH)) {

            // 대여 기록 삭제
            for (Rental rental : rentalRepository.findRentalsByStudent_IdAndStatus(findStudent.getId(), RentalStatus.FINISH)) {
                rentalRepository.delete(rental);
            }

            // 학생 정보 삭제
            studentRepository.delete(findStudent);

            // 게시글의 학생 정보 수정
            for (Board board : boardService.findBoardsByStuId(stuId)) {
                boardRepository.delete(board);
            }

            // 댓글의 학생 정보 수정
            for (Reply reply : replyService.findRepliesByStuId(stuId)) {
                replyRepository.delete(reply);
            }

            // 게시글의 좋아요 리스트에서 삭제
            for (Board board : boardRepository.findAll()) {
                if(board.isLike(stuId)) {
                    board.like(stuId);
                }
            }

            // 댓글의 좋아요 리스트에서 삭제
            for(Reply reply : replyRepository.findAll()) {
                if(reply.isLike(stuId)) {
                    reply.like(stuId);
                }
            }
            return true;
        } else {
            return false;
        }

    }

    // 로그인
    public Student login(LoginRequest loginRequest) {
        Student student = studentRepository.findByStuId(loginRequest.getStuId());

        if (student == null) {
            return null;
        }

        if(!student.getPassword().equals(loginRequest.getPassword())){
            return null;
        }

        return student;
    }

    /**
     * 학생 단건 조회
     */
    public Student findStudent(String stuId){
        return studentRepository.findByStuId(stuId);

    }

    public Page<Student> findStudentContainingStuIdAndName(String stuId, String name, Pageable pageable){
        return studentRepository.findStudentByStuIdContainingAndNameContaining(stuId, name, pageable);
    }

    /**
     * 학생 전체 조회
     */
    public Page<Student> findAllStudent(Pageable pageable){
        return studentRepository.findAll(pageable);
    }
}
