package com.knowy.server.application.usecase.update.email;

public record UserUpdateEmailCommand(int userId, String email, String password) {
}
