package com.poly.assigment.controller;

import com.poly.assigment.entity.User;
import com.poly.assigment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/quen-mat-khau")
    public String showForgotPasswordForm() {
        return "home/quen-mat-khau";
    }

    @PostMapping("/quen-mat-khau")
    public String processForgotPassword(@RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        // Tìm người dùng bằng email trước
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty() || !userOptional.get().getTenDangNhap().equals(username)) {
            // Nếu không tìm thấy email hoặc tên đăng nhập không khớp
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập và email không khớp.");
            return "redirect:/quen-mat-khau";
        }

        // Nếu thông tin chính xác, tiếp tục xử lý
        String token = UUID.randomUUID().toString();
        try {
            userService.updateResetPasswordToken(token, email);
            String resetLink = getSiteURL(request) + "/dat-lai-mat-khau?token=" + token;
            sendEmail(email, resetLink);
            redirectAttributes.addFlashAttribute("message", "Chúng tôi đã gửi một link đặt lại mật khẩu đến email của bạn.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quen-mat-khau";
    }

    @GetMapping("/dat-lai-mat-khau")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<User> userOptional = userService.getByResetPasswordToken(token);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn!");
            return "home/dangnhap";
        }
        model.addAttribute("token", token);
        return "home/dat-lai-mat-khau";
    }

    @PostMapping("/dat-lai-mat-khau")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp!");
            model.addAttribute("token", token);
            return "home/dat-lai-mat-khau";
        }

        Optional<User> userOptional = userService.getByResetPasswordToken(token);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Link không hợp lệ!");
            return "redirect:/dang-nhap";
        }

        userService.updatePassword(userOptional.get(), password);
        redirectAttributes.addFlashAttribute("message", "Bạn đã đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
        return "redirect:/dang-nhap";
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    private void sendEmail(String recipientEmail, String link) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("your-email@gmail.com", "Hỗ trợ khách hàng"); // Thay email của bạn vào đây
        helper.setTo(recipientEmail);
        String subject = "Link đặt lại mật khẩu của bạn";
        String content = "<p>Chào bạn,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu.</p>"
                + "<p>Bấm vào link dưới đây để thay đổi mật khẩu:</p>"
                + "<p><a href=\"" + link + "\">Đổi mật khẩu</a></p>"
                + "<br>"
                + "<p>Bỏ qua email này nếu bạn không thực hiện yêu cầu này.</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
}