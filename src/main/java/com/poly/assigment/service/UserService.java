package com.poly.assigment.service;

import java.util.List;
import java.util.Optional;

import com.poly.assigment.entity.User;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Integer id);
    User save(User user);
    void deleteById(Integer id);
    Optional<User> findByTenDangNhap(String tenDangNhap);
    Optional<User> findByEmail(String email);
    void updateResetPasswordToken(String token, String email) throws Exception;
    Optional<User> getByResetPasswordToken(String token);
    void updatePassword(User user, String newPassword);
}

