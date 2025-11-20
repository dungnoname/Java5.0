package com.poly.assignment.security;

import com.poly.assignment.dao.UserDAO;
import com.poly.assignment.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        // Lấy tên quyền từ bảng Role (User, Staff, Admin)
        String roleNameFromDB = user.getRole().getRoleName();

        // Spring Security cần tiền tố "ROLE_", nên ta cộng chuỗi vào
        // Ví dụ DB là "Admin" -> Spring cần "ROLE_ADMIN" (thường viết hoa)
        String springRole = "ROLE_" + roleNameFromDB.toUpperCase();

        return new org.springframework.security.core.userdetails.User(
                user.getTenDangNhap(),
                user.getMatKhau(),
                Collections.singletonList(new SimpleGrantedAuthority(springRole))
        );
    }
}