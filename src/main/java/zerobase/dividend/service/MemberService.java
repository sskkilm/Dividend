package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.dividend.domain.Member;
import zerobase.dividend.dto.Auth;
import zerobase.dividend.dto.MemberDto;
import zerobase.dividend.exception.impl.AlreadyExistUserException;
import zerobase.dividend.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return memberRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + userName));
    }

    public MemberDto register(Auth.SignUp signUpInfo) {
        if (memberRepository.existsByUserName(signUpInfo.getUserName())) {
            throw new AlreadyExistUserException();
        }

        signUpInfo.setPassword(passwordEncoder.encode(signUpInfo.getPassword()));

        return MemberDto.fromEntity(memberRepository.save(Member.from(signUpInfo)));
    }

    public MemberDto authenticate(Auth.SignIn signInInfo) {
        Member member = memberRepository.findByUserName(signInInfo.getUserName())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + signInInfo.getUserName()));

        if (!passwordEncoder.matches(signInInfo.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return MemberDto.fromEntity(member);
    }

}
