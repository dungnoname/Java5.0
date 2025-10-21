package com.poly.assigment.dao;

import com.poly.assigment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    Optional<User> findByTenDangNhap(String tenDangNhap);

    Optional<User> findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
}
