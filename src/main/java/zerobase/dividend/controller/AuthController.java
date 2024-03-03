package zerobase.dividend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.dividend.dto.Auth;
import zerobase.dividend.dto.MemberDto;
import zerobase.dividend.security.TokenProvider;
import zerobase.dividend.service.MemberService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(
            @RequestBody Auth.SignUp request
    ) {
        // 회원가입을 위한 API
        return ResponseEntity.ok(
                memberService.register(request)
        );
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(
            @RequestBody Auth.SignIn request
    ) {
        // 로그인용 API
        MemberDto memberDto = memberService.authenticate(request);
        String token = tokenProvider.generateToken(memberDto.getUserName(), memberDto.getRoles());
        log.info("user login -> " + request.getUserName());

        return ResponseEntity.ok(token);
    }

}
