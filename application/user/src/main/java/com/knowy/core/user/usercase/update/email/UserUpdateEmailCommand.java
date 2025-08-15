package com.knowy.core.user.usercase.update.email;

public record UserUpdateEmailCommand(int userId, String email, String password) {
}
