package an.rentalinhaee.controller;

import an.rentalinhaee.domain.Board;
import an.rentalinhaee.domain.Reply;
import an.rentalinhaee.domain.dto.BoardForm;
import an.rentalinhaee.domain.dto.ReplyForm;
import an.rentalinhaee.service.BoardService;
import an.rentalinhaee.service.ReplyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;

    @GetMapping("/board/list")
    public String list(Model model, @RequestParam(required = false, value = "noticePage", defaultValue = "1") int noticePage,
                       @RequestParam(required = false, value = "boardPage", defaultValue = "1") int boardPage) {

        PageRequest noticePageRequest = PageRequest.of(noticePage - 1, 5, Sort.by("writeTime").descending());
        Page<Board> notices = boardService.findByNotice(noticePageRequest, true);
        model.addAttribute("notices", notices);

        PageRequest boardPageRequest = PageRequest.of(boardPage - 1, 5, Sort.by("writeTime").descending());
        Page<Board> boards = boardService.findByNotice(boardPageRequest, false);
        model.addAttribute("boards", boards);

        model.addAttribute("noticePage", noticePage);
        model.addAttribute("boardPage", boardPage);

        return "board/list";
    }

    @GetMapping("/board/new")
    public String createBoardForm(Model model) {

        model.addAttribute("boardForm", new BoardForm());
        return "board/createBoardForm";
    }

    @PostMapping("/board/new")
    public String createBoard(@ModelAttribute BoardForm boardForm,
                              HttpSession httpSession, BindingResult bindingResult, Model model) {

        if (boardForm.getTitle().isEmpty()) {
            bindingResult.addError(new FieldError("boardForm", "title", "제목을 입력하세요"));
        }
        if (boardForm.getTitle().length() > 20) {
            bindingResult.addError(new FieldError("boardForm", "title", "20자 이내로 입력해주세요"));
        }
        if (boardForm.getContent().isEmpty()) {
            bindingResult.addError(new FieldError("boardForm", "content", "내용을 입력하세요"));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("boardForm", boardForm);
            return "board/createBoardForm";
        }

        if(boardForm.isNotice()){
            boardForm.setTitle("[공지] " + boardForm.getTitle() );
        }
        Board board = new Board((String) httpSession.getAttribute("loginStuId"), (String) httpSession.getAttribute("loginName"),
                 boardForm.getTitle(), boardForm.getContent(), LocalDateTime.now(),  boardForm.isNotice());

        boardService.saveBoard(board);
        return "redirect:/board/list";
    }

    @GetMapping("/board/{id}")
    public String showOneBoard(@PathVariable("id") Long id, Model model) {

        Board board = boardService.findOne(id);

        model.addAttribute("board", board);
        model.addAttribute("form", new ReplyForm());

        return "board/showOne";

    }

    @GetMapping("/board/{id}/like")
    public String likeBoard(@PathVariable("id") Long id, HttpSession httpSession) {
        String stuId = (String) httpSession.getAttribute("loginStuId");

        boardService.like(id, stuId);

        return "redirect:/board/" + id;
    }

    @GetMapping("/board/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id) {
        boardService.delete(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/{id}/edit")
    public String editBoardForm(@PathVariable("id") Long id, Model model) {
        Board board = boardService.findOne(id);

        BoardForm boardForm = new BoardForm();
        boardForm.setStuId(board.getStuId());
        boardForm.setName(board.getName());
        boardForm.setTitle(board.getTitle());
        boardForm.setContent(board.getContent());
        boardForm.setLikeNumber(board.getLikeNumber().size());

        model.addAttribute("boardForm", boardForm);


        return "board/updateBoardForm";
    }

    @PostMapping("/board/{id}/edit")
    public String editBoard(@PathVariable("id") Long id, BoardForm boardForm) {
        boardService.edit(id, boardForm);
        return "redirect:/board/" + id;
    }

    @PostMapping("/board/{id}/reply/new")
    public String createReply(@PathVariable("id") Long boardId, RedirectAttributes redirectAttributes, ReplyForm form, HttpSession httpSession, BindingResult bindingResult) {

        if(!StringUtils.hasText(form.getContent())) {
            bindingResult.addError(new FieldError("form", "content", "내용을 입력하세요"));
        }
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/board/" + boardId;
        }
        Reply reply = replyService.reply((String) httpSession.getAttribute("loginStuId"), (String) httpSession.getAttribute("loginName"),
                form.getContent(), boardId);
        reply.setBoard(boardService.findOne(boardId));

        return "redirect:/board/" + boardId;
    }

    @GetMapping("/board/{id}/reply/{replyId}/delete")
    public String deleteReply(@PathVariable("id") Long boardId,
                              @PathVariable("replyId") Long replyId) {

        replyService.delete(replyId);
        return "redirect:/board/" + boardId;
    }

    @GetMapping("/reply/{id}/like")
    public String likeReply(@RequestParam("boardId") Long boardId,
                            @PathVariable("id") Long replyId, HttpSession httpSession) {

        replyService.like(replyId, (String) httpSession.getAttribute("loginStuId"));
        return "redirect:/board/" + boardId;
    }

    @GetMapping("/board/myList")
    public String myBoardList(HttpSession httpSession, Model model,
                              @RequestParam(required = false, defaultValue = "1", value = "page") int page) {

        PageRequest pageRequest;
        pageRequest = PageRequest.of(page - 1, 10, Sort.by("writeTime").descending());

        String stuId = (String) httpSession.getAttribute("loginStuId");
        Page<Board> boards = boardService.findByStuId(pageRequest, stuId);
        model.addAttribute("boards", boards);

        return "board/myList";
    }

    @ModelAttribute("loginStuId")
    public String loginStuId(HttpSession httpSession) {

        if(httpSession.getAttribute("loginStuId") != null) return httpSession.getAttribute("loginStuId").toString();
        return null;

    }

    @ModelAttribute("loginName")
    public String loginName(HttpSession httpSession) {
        if(httpSession.getAttribute("loginStuId") != null) return httpSession.getAttribute("loginName").toString();
        return null;
    }
}
