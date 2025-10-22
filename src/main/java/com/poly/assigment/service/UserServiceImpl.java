package com.poly.assigment.service;

import com.poly.assigment.dao.UserDAO;
import com.poly.assigment.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) { // Triển khai phương thức phân trang
        return userDAO.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userDAO.findById(id);
    }

    @Override
    public User save(User user) {
        return userDAO.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        userDAO.deleteById(id);
    }

    /// update by dung for form đăng kí
    @Override
    public Optional<User> findByTenDangNhap(String tenDangNhap) {
        return userDAO.findByTenDangNhap(tenDangNhap);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) throws Exception {
        Optional<User> userOptional = userDAO.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setResetPasswordToken(token);
            userDAO.save(user);
        } else {
            throw new Exception("Không tìm thấy người dùng nào với email: " + email);
        }
    }

    @Override
    public Optional<User> getByResetPasswordToken(String token) {
        return userDAO.findByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        // TODO: MÃ HÓA MẬT KHẨU TRƯỚC KHI LƯU VÀO DATABASE
        // user.setMatKhau(passwordEncoder.encode(newPassword));
        user.setMatKhau(newPassword); // Tạm thời để nguyên, bạn cần thêm BCryptPasswordEncoder
        user.setResetPasswordToken(null);
        userDAO.save(user);
    }
}