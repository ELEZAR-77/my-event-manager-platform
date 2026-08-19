package dev.sorokin.eventmanager.user.serivce;

import dev.sorokin.eventmanager.user.entity.User;
import dev.sorokin.eventmanager.user.exceptions.UserLoginAlreadyExistsException;
import dev.sorokin.eventmanager.user.mapper.UserMapper;
import dev.sorokin.eventmanager.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public User createUser(User userRequest) {
        if (userRepository.existsByLogin(userRequest.login())) {
            throw new UserLoginAlreadyExistsException("User login %s is already exists".formatted(userRequest.login()));
        }

        var userEntity = userMapper.toEntity(userRequest);

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        userRepository.save(userEntity);

        return userMapper.toDomainFromEntity(userEntity);
    }

    public List<User> findAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toDomainFromEntity)
                .toList();
    }

    @Transactional
    public User updateUser(
            Long id,
            User user
    ) {

        var userEntity = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found")
        );

        if (user.login() != null) {
            userEntity.setLogin(user.login());
        }

        if (user.password() != null) {
            userEntity.setPassword(passwordEncoder.encode(user.password()));
        }

        if (user.age() != null) {
            userEntity.setAge(user.age());
        }

        userEntity.setRole(user.role());

        userRepository.save(userEntity);

        return userMapper.toDomainFromEntity(userEntity);
    }

    public User findUserById(Long id) {
        var userEntity = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found")
        );

        return userMapper.toDomainFromEntity(userEntity);
    }


    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else  {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
    }
}
