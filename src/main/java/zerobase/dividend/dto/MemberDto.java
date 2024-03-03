package zerobase.dividend.dto;

import lombok.*;
import zerobase.dividend.domain.Member;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private String userName;

    private String password;

    private List<String> roles;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .userName(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRoles())
                .build();
    }
}
