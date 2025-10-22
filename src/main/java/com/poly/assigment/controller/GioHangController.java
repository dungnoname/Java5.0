package com.poly.assigment.controller;

import com.poly.assigment.dao.SanPhamDAO;
import com.poly.assigment.dao.UserDAO;
import com.poly.assigment.entity.GioHang;
import com.poly.assigment.entity.SanPham;
import com.poly.assigment.entity.User;
import com.poly.assigment.service.GioHangService;
import jakarta.servlet.http.HttpServletRequest;
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
    private UserDAO userDAO;

    @Autowired
    private SanPhamDAO spDAO;



    // ✅ User tạm thời (vì chưa có đăng nhập)
    private User getTempUser(HttpSession session) {
        User user = (User) session.getAttribute("userSession");
        if (user == null) {
            user = userDAO.findById(1)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user tạm thời"));
            session.setAttribute("userSession", user);
        }
        return user;
    }

    // ✅ Lấy giỏ hàng từ DB mỗi lần vào trang (đảm bảo dữ liệu thật)
    private List<GioHang> getCart(User user) {
        return new ArrayList<>(gioHangService.getGioHangByUser(user));
    }

    // ✅ Trang giỏ hàng
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User user = getTempUser(session);
        List<GioHang> cart = getCart(user);
        session.setAttribute("sessionCart", cart);

        // Danh sách tạm tính cho từng sản phẩm
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
        User user = getTempUser(session);
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

        // Nếu không có redirectUrl thì mặc định về danh mục
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
        User user = getTempUser(session);
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
        User user = getTempUser(session);
        SanPham sp = spDAO.findById(maSP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        GioHang gh = gioHangService.findByUserAndSanPham(user, sp);
        if (gh != null) {
            gioHangService.delete(gh.getMaGH());
        }

        return "redirect:/gio-hang";
    }









}
