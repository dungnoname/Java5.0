package com.poly.assignment.controller.page;

import com.poly.assignment.entity.GioHang;
import com.poly.assignment.entity.User;
import com.poly.assignment.service.GioHangService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final GioHangService gioHangService;

    @ModelAttribute
    public void addGlobalCartInfo(Model model, HttpSession session) {
        // Lấy user hiện tại từ session
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            // Nếu chưa đăng nhập, không hiển thị giỏ hàng mini
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("cartCount", 0);
            model.addAttribute("cartTotal", BigDecimal.ZERO);
            return;
        }

        // Lấy giỏ hàng thật từ DB
        List<GioHang> cart = gioHangService.getGioHangByUser(user);
        if (cart == null) cart = new ArrayList<>();

        int totalItems = cart.stream().mapToInt(GioHang::getSoLuong).sum();
        BigDecimal totalPrice = cart.stream()
                .map(i -> i.getSanPham().getDonGiaBan()
                        .multiply(BigDecimal.valueOf(i.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Thêm vào model để layout (nav bar) lấy
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartCount", totalItems);
        model.addAttribute("cartTotal", totalPrice);
    }
}
