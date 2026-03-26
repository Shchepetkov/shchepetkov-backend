package ru.shchepetkov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.shchepetkov.dto.UpdateProfileRequest;
import ru.shchepetkov.models.Role;
import ru.shchepetkov.models.User;
import ru.shchepetkov.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = normalizeUsername(username);
        return userRepository.findByUsernameIgnoreCase(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalizedUsername));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        String normalizedUsername = normalizeUsername(username);
        try {
            return userRepository.findByUsernameIgnoreCase(normalizedUsername);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error finding user: " + normalizedUsername, e);
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        if (user.getPassword() != null && !user.isPasswordEncrypted()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPasswordEncrypted(true);
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordEncrypted(true);
        userRepository.save(user);
    }

    public boolean addUser(User user) {
        String normalizedUsername = normalizeUsername(user.getUsername());
        user.setUsername(normalizedUsername);

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPasswordEncrypted(false);
        save(user);
        return true;
    }

    public boolean saveUser(User user) {
        String normalizedUsername = normalizeUsername(user.getUsername());
        user.setUsername(normalizedUsername);

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPasswordEncrypted(false);
        save(user);
        return true;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(normalizeUsername(username));
    }

    public User findByUsernameOrThrow(String username) {
        String normalizedUsername = normalizeUsername(username);
        return userRepository.findByUsernameIgnoreCase(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalizedUsername));
    }

    public void updateProfile(User user, String username, String password) {
        String normalizedUsername = normalizeUsername(username);
        if (!normalizedUsername.isEmpty() && !normalizedUsername.equalsIgnoreCase(user.getUsername())) {
            if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
                throw new RuntimeException("Username already exists: " + normalizedUsername);
            }
            user.setUsername(normalizedUsername);
        }
        
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
            user.setPasswordEncrypted(true);
        }
        
        userRepository.save(user);
    }

    public void updateAvatar(User user, String avatarPath) {
        user.setAvatarPath(avatarPath);
        userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(false);
            userRepository.save(user);
        }
    }

    public void activateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            userRepository.save(user);
        }
    }

    public User updateProfileDetails(User user, UpdateProfileRequest request) {
        user.setFullName(normalizeNullableValue(request.getFullName()));
        user.setEmail(normalizeNullableValue(request.getEmail()));
        user.setLocation(normalizeNullableValue(request.getLocation()));
        user.setBio(normalizeNullableValue(request.getBio()));
        return userRepository.save(user);
    }

    private String normalizeNullableValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
} 