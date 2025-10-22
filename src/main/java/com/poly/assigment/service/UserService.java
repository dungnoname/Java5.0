package com.poly.assigment.service;

import java.util.List;
import java.util.Optional;

import com.poly.assigment.entity.User;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable

public interface UserService {
    List<User> findAll();
    Page<User> findAll(Pageable pageable); // Thêm phương thức phân trang
    Optional<User> findById(Integer id);
    User save(User user);
    void deleteById(Integer id);
    Optional<User> findByTenDangNhap(String tenDangNhap);
    Optional<User> findByEmail(String email);
    void updateResetPasswordToken(String token, String email) throws Exception;
    Optional<User> getByResetPasswordToken(String token);
    void updatePassword(User user, String newPassword);
}//