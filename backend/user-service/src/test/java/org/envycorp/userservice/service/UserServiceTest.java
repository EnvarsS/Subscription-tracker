package org.envycorp.userservice.service;

import org.envycorp.userservice.exception.NoSuchUserWithIdSpecified;
import org.envycorp.userservice.model.DTO.UserRequestDTO;
import org.envycorp.userservice.model.DTO.UserResponseDTO;
import org.envycorp.userservice.model.entity.User;
import org.envycorp.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDisplayName("Old Name");
        existingUser.setPreferredCurrency("EUR");
        existingUser.setCreatedAt(Instant.now());
        existingUser.setUpdatedAt(Instant.now());
    }

    @Test
    void getUser_returnsUser_whenFound() {
        UserResponseDTO expectedDto = new UserResponseDTO();
        expectedDto.setId(userId);
        expectedDto.setDisplayName("Old Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(mapper.map(existingUser, UserResponseDTO.class)).thenReturn(expectedDto);

        UserResponseDTO result = userService.getUser(userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getDisplayName()).isEqualTo("Old Name");
    }

    @Test
    void getUser_throwsNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(NoSuchUserWithIdSpecified.class);
    }

    @Test
    void createOrUpdateUser_updatesExistingUser_whenAlreadyExists() {
        UserRequestDTO requestDto = new UserRequestDTO();
        requestDto.setDisplayName("New Name");
        requestDto.setPreferredCurrency("USD");

        UserResponseDTO expectedDto = new UserResponseDTO();
        expectedDto.setId(userId);
        expectedDto.setDisplayName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doAnswer(invocation -> {
            existingUser.setDisplayName("New Name");
            existingUser.setPreferredCurrency("USD");
            return null;
        }).when(mapper).map(requestDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(mapper.map(existingUser, UserResponseDTO.class)).thenReturn(expectedDto);

        UserResponseDTO result = userService.createOrUpdateUser(userId, requestDto);

        assertThat(result.getDisplayName()).isEqualTo("New Name");
        verify(userRepository).save(existingUser);
    }

    @Test
    void createOrUpdateUser_createsNewUser_withCorrectId_whenNoneExists() {
        UserRequestDTO requestDto = new UserRequestDTO();
        requestDto.setDisplayName("Brand New");
        requestDto.setPreferredCurrency("EUR");

        User mappedUser = new User();
        mappedUser.setDisplayName("Brand New");
        mappedUser.setPreferredCurrency("EUR");

        UserResponseDTO expectedDto = new UserResponseDTO();
        expectedDto.setId(userId);
        expectedDto.setDisplayName("Brand New");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(mapper.map(requestDto, User.class)).thenReturn(mappedUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(expectedDto);

        userService.createOrUpdateUser(userId, requestDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(userCaptor.getValue().getId()).isEqualTo(userId);
        assertThat(userCaptor.getValue().getDisplayName()).isEqualTo("Brand New");
    }

    @Test
    void deleteUser_deletesUser_whenFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(existingUser);

        userService.deleteUser(userId);

        verify(userRepository).delete(existingUser);
    }

    @Test
    void deleteUser_throwsNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(NoSuchUserWithIdSpecified.class);
    }
}
