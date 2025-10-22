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

    public void sendEmail(String recipientEmail, String link) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("dungntts00667@fpt.edu.vn", "Hỗ trợ khách hàng"); // ✅ Thay email thật của bạn vào đây
        helper.setTo(recipientEmail);

        String subject = "🔐 Đặt lại mật khẩu của bạn";

        String content = """
            <div style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden;">
                    
                    <div style="background-color: #007bff; color: white; text-align: center; padding: 20px;">
                        <h2 style="margin: 0;">Hỗ trợ khách hàng</h2>
                    </div>
                    
                    <div style="padding: 25px; color: #333;">
                        <p>Chào bạn,</p>
                        <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>
                        <p>Vui lòng nhấn vào nút bên dưới để tiến hành thay đổi mật khẩu:</p>

                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" 
                               style="background-color: #007bff; color: white; padding: 14px 28px; 
                                      text-decoration: none; border-radius: 8px; font-size: 16px; 
                                      font-weight: bold; display: inline-block;">
                                🔁 Đổi mật khẩu ngay
                            </a>
                        </div>

                        <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>
                        <br>
                        <p>Trân trọng,<br><strong>Đội ngũ Hỗ trợ khách hàng</strong></p>
                    </div>

                    <div style="background-color: #f1f1f1; text-align: center; padding: 10px; font-size: 12px; color: #777;">
                        © 2025 Công ty .... Mọi quyền được bảo lưu.
                    </div>
                </div>
            </div>
        """.formatted(link);

        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}