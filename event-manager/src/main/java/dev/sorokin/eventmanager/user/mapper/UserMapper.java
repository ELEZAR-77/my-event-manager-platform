package dev.sorokin.eventmanager.user.mapper;

import dev.sorokin.eventmanager.user.dto.UserRegistrationRequest;
import dev.sorokin.eventmanager.user.dto.UserResponse;
import dev.sorokin.eventmanager.user.dto.UserUpdateRequest;
import dev.sorokin.eventmanager.user.entity.User;
import dev.sorokin.eventmanager.user.entity.UserEntity;
import dev.sorokin.eventmanager.user.entity.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomainFromRegRequest(UserRegistrationRequest request) {
        return new User(
                null,
                request.login(),
                request.age(),
                request.password(),
                UserRole.USER
        );
    }

    public UserEntity toEntity(User domain) {
        return new UserEntity(
                domain.id(),
                domain.login(),
                domain.age(),
                domain.password(),
                domain.role()
        );
    }

    public UserResponse toResponseFromDomain(User user) {
        return new UserResponse(
                user.id(),
                user.login(),
                user.age(),
                user.role().name()
        );
    }

    public User toDomainFromEntity(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getAge(),
                userEntity.getPassword(),
                userEntity.getRole()
        );
    }

    public User updateRequestToDomain(UserUpdateRequest  updateRequest) {
        UserRole role;


        return new User(
                null,
                updateRequest.login(),
                updateRequest.age(),
                updateRequest.password(),
                role = updateRequest.role() == null ?
                        UserRole.USER
                        : UserRole.valueOf(updateRequest.role())
        );
    }
}
