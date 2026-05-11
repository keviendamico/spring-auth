package com.kevien.damico.auth.service;

import com.kevien.damico.auth.model.User;
import com.kevien.damico.auth.model.dto.UserDto;
import com.kevien.damico.auth.model.dto.UserRequest;
import com.kevien.damico.auth.repository.UsersRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UsersRepository repository;
    private final PasswordEncoder encoder;

    public UsersService(UsersRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public UserDto createUser(UserRequest request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .username(request.username())
                .password(encoder.encode(request.password()))
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .activated(request.activated())
                .verified(request.verified())
                .build();
        repository.save(user);
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername(),
                user.getAddress(), user.getPhoneNumber(), user.isVerified(), user.isActivated());
    }

    public UserDto findByUsername(String username) {
        User user = repository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername(),
                user.getAddress(), user.getPhoneNumber(), user.isVerified(), user.isActivated());
    }
}
