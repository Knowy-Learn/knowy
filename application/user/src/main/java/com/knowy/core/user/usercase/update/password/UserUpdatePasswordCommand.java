package com.knowy.core.user.usercase.update.password;

public record UserUpdatePasswordCommand(String token, String password, String confirmPassword) {
}
