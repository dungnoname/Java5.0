package com.poly.assignment.service;

import com.poly.assignment.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    Optional<User> findById(Integer id);
    User save(User user);
    void deleteById(Integer id);
    Optional<User> findByTenDangNhap(String tenDangNhap);
    Optional<User> findByEmail(String email);
    void updateResetPasswordToken(String token, String email) throws Exception;
    Optional<User> getByResetPasswordToken(String token);
    void updatePassword(User user, String newPassword);
}
