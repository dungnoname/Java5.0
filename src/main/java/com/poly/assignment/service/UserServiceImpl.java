package com.poly.assignment.service;

import com.poly.assignment.dao.UserDAO;
import com.poly.assignment.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    // ===== LẤY TOÀN BỘ NGƯỜI DÙNG =====
    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    // ===== PHÂN TRANG NGƯỜI DÙNG =====
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userDAO.findAll(pageable);
    }

    // ===== TÌM NGƯỜI DÙNG THEO ID =====
    @Override
    public Optional<User> findById(Integer id) {
        return userDAO.findById(id);
    }

    // ===== LƯU HOẶC CẬP NHẬT NGƯỜI DÙNG =====
    @Override
    public User save(User user) {
        return userDAO.save(user);
    }

    // ===== XÓA NGƯỜI DÙNG THEO ID =====
    @Override
    public void deleteById(Integer id) {
        userDAO.deleteById(id);
    }

    // ===== TÌM NGƯỜI DÙNG THEO TÊN ĐĂNG NHẬP =====
    @Override
    public Optional<User> findByTenDangNhap(String tenDangNhap) {
        return userDAO.findByTenDangNhap(tenDangNhap);
    }

    // ===== TÌM NGƯỜI DÙNG THEO EMAIL =====
    @Override
    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    // ===== CẬP NHẬT TOKEN RESET PASSWORD =====
    @Override
    public void updateResetPasswordToken(String token, String email) throws Exception {
        Optional<User> optionalUser = userDAO.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setResetPasswordToken(token);
            userDAO.save(user);
        } else {
            throw new Exception("Không tìm thấy người dùng với email: " + email);
        }
    }

    @Override
    public Optional<User> getByResetPasswordToken(String token) {
        return userDAO.findByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setMatKhau(newPassword);
        user.setResetPasswordToken(null);
        userDAO.save(user);
    }
}
