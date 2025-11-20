package com.poly.assignment.controller;

import com.poly.assignment.dao.SanPhamDAO;
import com.poly.assignment.entity.GioHang;
import com.poly.assignment.entity.SanPham;
import com.poly.assignment.entity.User;
import com.poly.assignment.service.GioHangService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gio-hang")
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private SanPhamDAO spDAO;

    // ✅ Lấy giỏ hàng từ DB
    private List<GioHang> getCart(User user) {
        return new ArrayList<>(gioHangService.getGioHangByUser(user));
    }

    // ✅ Trang giỏ hàng
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap"; // chưa đăng nhập → chuyển đến login
        }

        List<GioHang> cart = getCart(user);
        session.setAttribute("sessionCart", cart);

        List<BigDecimal> tamTinhs = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (GioHang item : cart) {
            BigDecimal tamTinh = item.getSanPham().getDonGiaBan()
                    .multiply(BigDecimal.valueOf(item.getSoLuong()));
            tamTinhs.add(tamTinh);
            total = total.add(tamTinh);
        }

        model.addAttribute("items", cart);
        model.addAttribute("tamTinhs", tamTinhs);
        model.addAttribute("total", total);
        return "home/gioHang";
    }

    // ✅ Thêm sản phẩm
    @PostMapping("/add/{maSP}")
    public String addToCart(@PathVariable("maSP") Integer maSP,
                            @RequestParam("soLuong") Integer soLuong,
                            @RequestParam(value = "redirectUrl", required = false) String redirectUrl,
                            HttpSession session) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap"; // chưa đăng nhập → login
        }

        SanPham sp = spDAO.findById(maSP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        GioHang existing = gioHangService.findByUserAndSanPham(user, sp);

        if (existing != null) {
            existing.setSoLuong(existing.getSoLuong() + soLuong);
            existing.setNgayTao(LocalDateTime.now());
            gioHangService.save(existing);
        } else {
            GioHang gh = new GioHang();
            gh.setUser(user);
            gh.setSanPham(sp);
            gh.setSoLuong(soLuong);
            gh.setNgayTao(LocalDateTime.now());
            gioHangService.save(gh);
        }

        if (redirectUrl == null || redirectUrl.isEmpty()) {
            redirectUrl = "/SanPham/";
        }
        return "redirect:" + redirectUrl;
    }

    // ✅ Cập nhật số lượng
    @PostMapping("/update/{maSP}")
    public String updateQuantity(@PathVariable("maSP") Integer maSP,
                                 @RequestParam("soLuong") Integer soLuong,
                                 HttpSession session) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        SanPham sp = spDAO.findById(maSP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        GioHang gh = gioHangService.findByUserAndSanPham(user, sp);
        if (gh != null) {
            gh.setSoLuong(Math.max(1, soLuong));
            gh.setNgayTao(LocalDateTime.now());
            gioHangService.save(gh);
        }

        return "redirect:/gio-hang";
    }

    // ✅ Xóa sản phẩm
    @GetMapping("/remove/{maSP}")
    public String removeItem(@PathVariable("maSP") Integer maSP, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        SanPham sp = spDAO.findById(maSP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        GioHang gh = gioHangService.findByUserAndSanPham(user, sp);
        if (gh != null) {
            gioHangService.delete(gh.getMaGH());
        }

        return "redirect:/gio-hang";
    }









}
