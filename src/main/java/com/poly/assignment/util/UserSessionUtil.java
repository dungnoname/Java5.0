
package com.poly.assignment.util;

import com.poly.assignment.entity.User;
import com.poly.assignment.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionUtil {
    private final UserService userService;

    public User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("userSession");
        if (user == null) {
            user = userService.findById(1)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user tạm thời"));
            session.setAttribute("userSession", user);
        }
        return user;
    }
}
