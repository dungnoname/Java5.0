package com.poly.assigment.controller;

import com.poly.assigment.entity.GioHang;
import com.poly.assigment.entity.User;
import com.poly.assigment.service.GioHangService;
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
        // Lấy user tạm
        User user = (User) session.getAttribute("userSession");
        if (user == null) return;

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
