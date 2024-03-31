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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;


@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RentalRepository rentalRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 학번 중복 검증
    public boolean checkStuIdDuplicate(String stuId){
        return studentRepository.existsByStuId(stuId);
    }

    // 회원가입
    public void join(JoinRequest joinRequest, String email){
        joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));

        studentRepository.save(joinRequest.toEntity(email));
    }

    public boolean passwordCheck(String stuId, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.matches(password, studentRepository.findByStuId(stuId).getPassword());
    }

    // 비밀번호 변경
    public void changePassword(String stuId, ChangePasswordRequest request) {
        Student student = studentRepository.findByStuId(stuId);
        student.editPassword(request.getChangePassword());
    }

    public void changeEmail(String stuId, String email) {
        Student student = studentRepository.findByStuId(stuId);
        student.editEmail(email);
    }

    private final BoardService boardService;
    private final ReplyService replyService;

    // 회원 탈퇴
    @Transactional
    public boolean delete(String stuId) {
        Student findStudent = studentRepository.findByStuId(stuId);

        Collection<RentalStatus> statuses = new ArrayList<>();
        statuses.add(RentalStatus.FINISH);
        statuses.add(RentalStatus.FINISH_OVERDUE);

        if(!rentalRepository.existsByStudent_IdAndStatusNotIn(findStudent.getId(), statuses)) {

            // 대여 기록 삭제
            for (Rental rental : rentalRepository.findRentalsByStudent_IdAndStatusIn(findStudent.getId(), statuses)) {
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
