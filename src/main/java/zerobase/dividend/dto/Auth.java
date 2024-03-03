package zerobase.dividend.dto;

import lombok.*;

import java.util.List;

public class Auth {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignIn {
        private String userName;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUp {
        private String userName;
        private String password;
        private List<String> roles;
    }
}
