package com.poly.assigment.controller;

import com.poly.assigment.entity.User;
import com.poly.assigment.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
public class TaiKhoanController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Hiển thị trang tài khoản + lấy user từ session
    @GetMapping("/tai-khoan")
    public String hienThiTrangTaiKhoan(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        // Truyền user ra view để cả 2 tab đều có thể sử dụng
        model.addAttribute("user", user);
        return "home/TaiKhoan";
    }

    // ================== PHẦN ĐÃ THÊM MỚI ==================
    /**
     * ✅ XỬ LÝ CẬP NHẬT THÔNG TIN CÁ NHÂN (TAB 1)
     * Phương thức này nhận dữ liệu từ form "Cập nhật thông tin"
     */
    @PostMapping("/tai-khoan/cap-nhat-thong-tin")
    public String xuLyCapNhatThongTin(
            @ModelAttribute("user") User userTuForm, // Đây là đối tượng CHỈ chứa dữ liệu từ form
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. Lấy đối tượng User GỐC (đầy đủ) từ session
        User userTrongSession = (User) session.getAttribute("currentUser");
        if (userTrongSession == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!");
            return "redirect:/dang-nhap";
        }

        try {
            // 2. Cập nhật CHỌN LỌC các trường từ form vào đối tượng GỐC
            // (Đảm bảo tên các trường này khớp với file User.java của bạn)
            userTrongSession.setHoTen(userTuForm.getHoTen());
            userTrongSession.setEmail(userTuForm.getEmail());
            userTrongSession.setSoDienThoai(userTuForm.getSoDienThoai());
            userTrongSession.setDiaChi(userTuForm.getDiaChi());

            // 3. LƯU ĐỐI TƯỢNG GỐC (userTrongSession)
            // Đối tượng này vẫn giữ nguyên RoleId, tenDangNhap, matKhau...
            User userDaCapNhat = userService.save(userTrongSession);

            session.setAttribute("currentUser", userDaCapNhat);

            redirectAttributes.addFlashAttribute("message", "✅ Cập nhật thông tin thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Lỗi khi cập nhật: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/tai-khoan";
    }


    // ✅ Gửi mail xác nhận đổi mật khẩu (TAB 2)
    @PostMapping("/tai-khoan/gui-xac-nhan-doi-mat-khau")
    public String guiXacNhanDoiMatKhau(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       HttpServletRequest request,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập trước!");
            return "redirect:/dang-nhap";
        }

        // 1️⃣ Kiểm tra mật khẩu hiện tại
        if (!user.getMatKhau().equals(oldPassword)) {
            redirectAttributes.addFlashAttribute("error", "❌ Mật khẩu hiện tại không đúng!");
            return "redirect:/tai-khoan";
        }

        // 2️⃣ Kiểm tra nhập lại mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "❌ Mật khẩu nhập lại không khớp!");
            return "redirect:/tai-khoan";
        }

        // 3️⃣ Sinh token xác nhận
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userService.save(user); // Lưu token vào DB

        // 4️⃣ Gửi mail xác nhận
        try {
            String link = getSiteURL(request) + "/xac-nhan-doi-mat-khau?token=" + token + "&newPassword=" + newPassword;
            sendEmail(user.getEmail(), link);
            redirectAttributes.addFlashAttribute("message", "✅ Link xác nhận đổi mật khẩu đã gửi tới email của bạn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi email: " + e.getMessage());
        }

        return "redirect:/tai-khoan";
    }

    // ✅ Xác nhận đổi mật khẩu qua email
    @GetMapping("/xac-nhan-doi-mat-khau")
    public String xacNhanDoiMatKhau(@RequestParam("token") String token,
                                    @RequestParam("newPassword") String newPassword,
                                    RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getByResetPasswordToken(token);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ Token không hợp lệ hoặc đã hết hạn!");
            return "redirect:/dang-nhap";
        }

        User user = userOptional.get();
        user.setMatKhau(newPassword);
        user.setResetPasswordToken(null); // Xóa token sau khi dùng
        userService.save(user); // Lưu mật khẩu mới

        redirectAttributes.addFlashAttribute("message", "✅ Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        return "redirect:/dang-nhap";
    }

    // Phụ trợ
    private String getSiteURL(HttpServletRequest request) {
        return request.getRequestURL().toString().replace(request.getServletPath(), "");
    }

    private void sendEmail(String recipientEmail, String link) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("dungntts00667@fpt.edu.vn");
        helper.setTo(recipientEmail);
        helper.setSubject("🔐 Xác nhận đổi mật khẩu của bạn");

        String content = """
            <div style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden;">
                    <div style="background-color: #007bff; color: white; text-align: center; padding: 20px;">
                        <h2 style="margin: 0;">Xác nhận đổi mật khẩu</h2>
                    </div>
                    <div style="padding: 25px; color: #333;">
                        <p>Chào bạn,</p>
                        <p>Bạn vừa yêu cầu đổi mật khẩu. Hãy nhấn vào nút bên dưới để xác nhận:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #007bff; color: white; padding: 14px 28px; 
                                      text-decoration: none; border-radius: 8px; font-size: 16px;">
                                ✅ Xác nhận đổi mật khẩu
                            </a>
                        </div>
                        <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>
                        <br>
                        <p>Trân trọng,<br><strong>Đội ngũ hỗ trợ khách hàng</strong></p>
                    </div>
                </div>
            </div>
        """.formatted(link);

        helper.setText(content, true);
        mailSender.send(message);
    }
}