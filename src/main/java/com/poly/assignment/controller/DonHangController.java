package com.poly.assignment.controller;

import com.poly.assignment.entity.*;
import com.poly.assignment.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/don-hang")
public class DonHangController {

    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService;
    @Autowired
    private TrangThaiDonHangService trangThaiDonHangService;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private UserService userService;

    // ✅ GET /don-hang - hiển thị đơn hàng & giỏ hàng
    @GetMapping
    public String viewDonHang(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        List<GioHang> gioHangList = gioHangService.getGioHangByUser(user);

        BigDecimal tongTien = gioHangList.stream()
                .map(item -> item.getSanPham().getDonGiaBan()
                        .multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<HoaDon> hoaDonList = hoaDonService.findAll().stream()
                .filter(hd -> hd.getNguoiDung() != null
                        && hd.getNguoiDung().getUserId().equals(user.getUserId()))
                .toList();

        model.addAttribute("gioHangList", gioHangList);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("hoaDonList", hoaDonList);
        model.addAttribute("user", user);
        return "home/donHang";
    }

    // ✅ POST /don-hang/xac-nhan
    @PostMapping("/xac-nhan")
    public String xacNhanDonHang(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        List<GioHang> gioHangList = gioHangService.getGioHangByUser(user);

        if (gioHangList.isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống!");
            model.addAttribute("gioHangList", gioHangList);
            model.addAttribute("tongTien", BigDecimal.ZERO);
            return "home/donHang";
        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setNguoiDung(user);
        hoaDon.setNgayLap(LocalDateTime.now());
        hoaDon.setTrangThaiDonHang(trangThaiDonHangService.findById(0).orElse(null)); // Chờ xác nhận

        HoaDon savedHoaDon = hoaDonService.save(hoaDon);

        for (GioHang item : gioHangList) {
            ChiTietHoaDon cthd = new ChiTietHoaDon();
            cthd.setHoaDon(savedHoaDon);
            cthd.setSanPham(item.getSanPham());
            cthd.setSoLuongBan(item.getSoLuong());
            cthd.setDonGia(item.getSanPham().getDonGiaBan());
            chiTietHoaDonService.save(cthd);
        }

        gioHangService.deleteAllByUser(user);

        model.addAttribute("hoaDon", savedHoaDon);
        model.addAttribute("message", "Đặt hàng thành công! Đơn của bạn đang chờ xác nhận.");
        return "home/datHangThanhCong";
    }

    // ✅ POST /don-hang/huy/{id}
    @PostMapping("/huy/{id}")
    public String huyDonHang(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        HoaDon hoaDon = hoaDonService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!hoaDon.getNguoiDung().getUserId().equals(user.getUserId()) ||
                hoaDon.getTrangThaiDonHang().getMaTT() != 0) {
            return "redirect:/don-hang";
        }

        hoaDon.setTrangThaiDonHang(trangThaiDonHangService.findById(4).orElse(null)); // Hủy
        hoaDonService.save(hoaDon);

        return "redirect:/don-hang";
    }

    // ✅ GET /don-hang/chi-tiet/{id}
    @GetMapping("/chi-tiet/{id}")
    public String chiTietDonHang(@PathVariable("id") Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        HoaDon hoaDon = hoaDonService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (!hoaDon.getNguoiDung().getUserId().equals(user.getUserId())) {
            return "redirect:/don-hang";
        }

        List<ChiTietHoaDon> chiTietList = chiTietHoaDonService.findByHoaDon(hoaDon);
        BigDecimal tongTien = chiTietList.stream()
                .map(ct -> ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuongBan())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("chiTietList", chiTietList);
        return "home/chiTietDonHang";
    }

    // ✅ POST /don-hang/dat-lai/{id}
    @PostMapping("/dat-lai/{id}")
    public String datLaiDonHang(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        HoaDon hoaDon = hoaDonService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!hoaDon.getNguoiDung().getUserId().equals(user.getUserId()) ||
                hoaDon.getTrangThaiDonHang().getMaTT() != 4) {
            return "redirect:/don-hang";
        }

        hoaDon.setTrangThaiDonHang(trangThaiDonHangService.findById(0).orElse(null));
        hoaDon.setNgayLap(LocalDateTime.now());
        hoaDonService.save(hoaDon);

        return "redirect:/don-hang/chi-tiet/" + id;
    }
}

