package com.knowy.server.application.usecase.update.password;

public record UserUpdatePasswordCommand(String token, String password, String confirmPassword) {
}
