package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.ListUsersUseCase;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.response.UserSummaryResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> execute(int page, int size, String profileType, String search, Boolean active) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAllWithFilters(profileType, search, active, pageable)
                .map(userProfileMapper::toUserSummaryResponse);
    }
}