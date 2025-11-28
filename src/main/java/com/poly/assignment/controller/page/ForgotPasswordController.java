package com.poly.assignment.controller.page;

import com.poly.assignment.entity.User;
import com.poly.assignment.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    // ===== Hiển thị form quên mật khẩu =====
    @GetMapping("/quen-mat-khau")
    public String showForgotPasswordForm() {
        return "home/quen-mat-khau";
    }

    // ===== Xử lý yêu cầu gửi mail đặt lại mật khẩu =====
    @PostMapping("/quen-mat-khau")
    public String processForgotPassword(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // --- Kiểm tra dữ liệu rỗng ---
        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ tên đăng nhập và email.");
            return "redirect:/quen-mat-khau";
        }

        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty() || !userOptional.get().getTenDangNhap().equalsIgnoreCase(username)) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập và email không khớp.");
            return "redirect:/quen-mat-khau";
        }

        // --- Sinh token reset thủ công ---
        String token = UUID.randomUUID().toString();

        try {
            userService.updateResetPasswordToken(token, email);

            // Tạo link reset tuyệt đối (VD: http://localhost:8080/dat-lai-mat-khau?token=abc)
            String resetLink = getSiteURL(request) + "/dat-lai-mat-khau?token=" + token;
            sendEmail(email, resetLink);

            redirectAttributes.addFlashAttribute("message",
                    "✅ Liên kết đặt lại mật khẩu đã được gửi đến email của bạn!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Không thể gửi email: " + ex.getMessage());
        }

        return "redirect:/quen-mat-khau";
    }

    // ===== Hiển thị form đặt lại mật khẩu =====
    @GetMapping("/dat-lai-mat-khau")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<User> userOptional = userService.getByResetPasswordToken(token);

        if (userOptional.isEmpty()) {
            model.addAttribute("error", "❌ Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "home/dangnhap";
        }

        model.addAttribute("token", token);
        return "home/dat-lai-mat-khau";
    }

    // ===== Xử lý đặt lại mật khẩu =====
    @PostMapping("/dat-lai-mat-khau")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "❌ Mật khẩu nhập lại không khớp!");
            model.addAttribute("token", token);
            return "home/dat-lai-mat-khau";
        }

        Optional<User> userOptional = userService.getByResetPasswordToken(token);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ Link không hợp lệ!");
            return "redirect:/dang-nhap";
        }

        userService.updatePassword(userOptional.get(), password);
        redirectAttributes.addFlashAttribute("message", "✅ Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
        return "redirect:/dang-nhap";
    }

    // ===== Hàm hỗ trợ tạo URL tuyệt đối =====
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    // ===== Gửi email đặt lại mật khẩu =====
    private void sendEmail(String recipientEmail, String link) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // ⚙️ Nếu dùng Gmail App Password, chỉ nên để email
        helper.setFrom("dungntts00667@fpt.edu.vn");
        helper.setTo(recipientEmail);
        helper.setSubject("🔐 Đặt lại mật khẩu của bạn");

        String content = """
            <div style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden;">
                    
                    <div style="background-color: #007bff; color: white; text-align: center; padding: 20px;">
                        <h2 style="margin: 0;">Hỗ trợ khách hàng</h2>
                    </div>
                    
                    <div style="padding: 25px; color: #333;">
                        <p>Chào bạn,</p>
                        <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>
                        <p>Vui lòng nhấn vào nút bên dưới để thay đổi mật khẩu:</p>

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
                        <p>Trân trọng,<br><strong>Đội ngũ hỗ trợ khách hàng</strong></p>
                    </div>

                    <div style="background-color: #f1f1f1; text-align: center; padding: 10px; font-size: 12px; color: #777;">
                        © 2025 Công ty bạn. Mọi quyền được bảo lưu.
                    </div>
                </div>
            </div>
        """.formatted(link);

        helper.setText(content, true);
        mailSender.send(message);

        System.out.println("✅ Email đặt lại mật khẩu đã được gửi tới: " + recipientEmail);
    }
}
