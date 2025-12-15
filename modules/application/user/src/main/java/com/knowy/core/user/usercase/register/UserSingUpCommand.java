package com.knowy.core.user.usercase.register;

public record UserSingUpCommand(String nickname, String gender, String email, String password) {
}
