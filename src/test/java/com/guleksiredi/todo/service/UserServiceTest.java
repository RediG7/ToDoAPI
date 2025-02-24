package com.guleksiredi.todo.service;

import com.guleksiredi.todo.dto.request.UserRequest;
import com.guleksiredi.todo.dto.response.UserResponse;
import com.guleksiredi.todo.entity.User;
import com.guleksiredi.todo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password123");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void shouldSaveUserSuccessfully() {
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse savedUser = userService.registerUser(userRequest);

        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void shouldGetUsernameById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String username = userService.getUsernameById(1L);

        assertNotNull(username);
        assertEquals("testUser", username);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUsernameNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUsernameById(1L));
    }
}
