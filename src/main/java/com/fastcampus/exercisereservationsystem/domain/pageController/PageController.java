package com.fastcampus.exercisereservationsystem.domain.pageController;

import com.fastcampus.exercisereservationsystem.common.exception.BizException;
import com.fastcampus.exercisereservationsystem.domain.user.dto.request.CreateUserRequest;
import com.fastcampus.exercisereservationsystem.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static com.fastcampus.exercisereservationsystem.domain.user.exception.UserErrorCode.*;

@EnableMethodSecurity(prePostEnabled = true)
@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserService userService;


    //<--------------------------홈페이지------------------------------------------------->
    @GetMapping("/")
    public String home() {

        return "index";
    }

    //회원가입폼으로 이동
    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("createUserForm")) {
            model.addAttribute("createUserForm", new CreateUserRequest(null, null, null, null, null, null));
        }
        return "signup";
    }

    //로그임 폼
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //<--------------------------공지사항------------------------------------------------>
    //공지사항 목록
    @GetMapping("/notices")
    public String noticeListPage() {
        return "notice";
    }

    // 공지 작성 페이지
    @GetMapping("/notices/create")
    public String noticeCreatePage() {
        return "notice-create"; // 👉 notice-create.html
    }

    // 공지 상세 페이지
    @GetMapping("/notices/{noticeId}")
    public String noticeDetailPage() {
        return "notice-detail"; // 👉 notice-detail.html
    }

    //<--------------------------수업 프로그램--------------------------------------------->
    @GetMapping("/program")
    public String programPage() {
        return "program";
    }

    //<-------예약--------->
    //예약 달력
    @GetMapping("/classSchedule-calendar")
    public String classScheduleCreate() {
        return "classSchedule-calendar";
    }
    //예약 생성폼 페이지
    @GetMapping("/classSchedule-createForm")
    public String classScheduleCreateForm() {
        return "classSchedule-createForm";
    }

    //예약 목록페이지
    @GetMapping("/classSchedule-list")
    public String classScheduleList() {
        return "classSchedule-list";
    }

    @GetMapping("/classSchedule-updateForm")
    public String classScheduleUpdateForm() {
        return "classSchedule-updateForm";
    }


    //<--------------------------내 정보--------------------------------------------->
    //내 정보
    @GetMapping("/my")
    public String myPage() {
        return "my";
    }

    /**
     * 서비스는 예외를 던진다. (throw new BizException(UserErrorCode.…))
     * 컨트롤러는 그 예외를 받아서 BindingResult에 에러를 매핑하고 폼을 다시 보여준다.
     * 그래서 try/catch로 BizException을 받아 BindingResult에 담았다.
     */
    @PostMapping("/signup")
    public String signupSubmit(
            @Valid @ModelAttribute("createUserForm") CreateUserRequest form,
            BindingResult bindingResult
    ) {
        // 1) 스프링 빈 검증(@NotBlank 등) 실패 시
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        // 2) 서비스 호출 -> 예외를 BindingResult로 변환
        try {
            userService.signup(form);
        } catch (BizException e) {
            switch (e.getErrorCode()) {
                case USER_ALREADY_EXISTED -> bindingResult.rejectValue("username", "duplicate", "이미 사용 중인 아이디입니다.");
                case USER_NICKNAME_ALREADY_EXISTED ->
                        bindingResult.rejectValue("nickname", "duplicate", "이미 사용 중인 닉네임입니다.");
                case USER_INVALID_DATE_RANGE -> bindingResult.reject("dates.invalid", "시작일은 종료일보다 늦을 수 없습니다.");
                default -> bindingResult.reject("signup.failed", "회원가입 처리에 실패했습니다.");
            }
            return "signup";
        }

        // 성공
        return "redirect:/";
    }
}
