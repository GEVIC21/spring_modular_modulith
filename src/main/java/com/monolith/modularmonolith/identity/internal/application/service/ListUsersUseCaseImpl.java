package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.ListUsersUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.response.UserSummaryResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;
    private final UserProfileMapper mapper;

    @Override
    public Page<UserSummaryResponse> execute(Pageable pageable, ProfileType profileType,
                                             String search, Boolean active) {
        Page<User> users = userRepository.findAllWithFilters(profileType, active, search, pageable);
        return users.map(mapper::toUserSummaryResponse);
    }
}