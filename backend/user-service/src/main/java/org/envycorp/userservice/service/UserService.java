package org.envycorp.userservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.exception.NoSuchUserWithIdSpecified;
import org.envycorp.userservice.model.DTO.UserRequestDTO;
import org.envycorp.userservice.model.DTO.UserResponseDTO;
import org.envycorp.userservice.model.entity.User;
import org.envycorp.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public UserResponseDTO getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserWithIdSpecified("No such user with id " + userId + " specified"));
        return mapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO createOrUpdateUser(UUID userId, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(userId)
                .map(existingUser -> {
                    mapper.map(userRequestDTO, existingUser);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = mapper.map(userRequestDTO, User.class);
                    newUser.setId(userId);
                    return userRepository.save(newUser);
                });

        return mapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    public void deleteUser(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserWithIdSpecified("No such user with id " + userId + " specified"));
        userRepository.delete(user);
    }
}
