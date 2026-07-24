package com.monolith.modularmonolith.authentication.internal.application.port.inbound;

import com.monolith.modularmonolith.authentication.internal.dto.PasswordChangeRequest;

public interface ChangePasswordUseCase {

    void execute(String email, PasswordChangeRequest request);
}