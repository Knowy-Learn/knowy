package com.knowy.server.application.usecase.register;

public record UserSingUpCommand(String nickname, String email, String password) {
}
