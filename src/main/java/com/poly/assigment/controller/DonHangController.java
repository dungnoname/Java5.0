package com.poly.assigment.controller;

import com.poly.assigment.entity.*;
import com.poly.assigment.service.*;
import com.poly.assigment.util.UserSessionUtil;
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
    @Autowired
    private UserSessionUtil userSessionUtil;

    // ✅ Lấy user hiện tại (tạm thời là user id = 1 nếu chưa đăng nhập)
    private User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("userSession");
        if (user == null) {
            user = userService.findById(1)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user tạm thời"));
            session.setAttribute("userSession", user);
        }
        return user;
    }

    // ✅ GET /don-hang - hiển thị đơn hàng & giỏ hàng
    @GetMapping
    public String viewDonHang(Model model, HttpSession session) {
        User user = getCurrentUser(session);

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

        return "home/donHang";
    }

    // ✅ POST /don-hang/xac-nhan - xác nhận đặt hàng (trạng thái: Chờ xác nhận)
    @PostMapping("/xac-nhan")
    public String xacNhanDonHang(Model model, HttpSession session) {
        User user = getCurrentUser(session);
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
        hoaDon.setTrangThaiDonHang(trangThaiDonHangService.findById(0).orElse(null)); // 👉 Chờ xác nhận

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

    // ✅ POST /don-hang/huy/{id} - người dùng hủy đơn (chỉ khi đang chờ xác nhận)
    @PostMapping("/huy/{id}")
    public String huyDonHang(@PathVariable("id") Integer id, HttpSession session, Model model) {
        User user = getCurrentUser(session);
        HoaDon hoaDon = hoaDonService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!hoaDon.getNguoiDung().getUserId().equals(user.getUserId())) {
            model.addAttribute("error", "Bạn không có quyền hủy đơn hàng này!");
            return "redirect:/don-hang";
        }

        if (hoaDon.getTrangThaiDonHang().getMaTT() != 0) {
            model.addAttribute("error", "Đơn hàng này không thể hủy!");
            return "redirect:/don-hang";
        }

        hoaDon.setTrangThaiDonHang(trangThaiDonHangService.findById(4).orElse(null)); // 👉 Hủy
        hoaDonService.save(hoaDon);

        return "redirect:/don-hang";
    }

    // ✅ GET /don-hang/chi-tiet/{id}
    @GetMapping("/chi-tiet/{id}")
    public String chiTietDonHang(@PathVariable("id") Integer id, Model model, HttpSession session) {
        User user = getCurrentUser(session);
        HoaDon hoaDon = hoaDonService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (!hoaDon.getNguoiDung().getUserId().equals(user.getUserId())) {
            model.addAttribute("error", "Bạn không có quyền xem đơn hàng này!");
            return "home/donHang";
        }

        List<ChiTietHoaDon> chiTietList = chiTietHoaDonService.findByHoaDon(hoaDon);
        BigDecimal tongTien = chiTietList.stream()
                .map(ct -> ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuongBan())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("tongTien", tongTien);
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("chiTietList", chiTietList);
        return "home/chiTietDonHang";
    }

    @PostMapping("/dat-lai/{id}")
    public String datLaiDonHang(@PathVariable("id") Integer id, HttpSession session, Model model) {
        User user = getCurrentUser(session);
        HoaDon hoaDon = hoaDonService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền sở hữu
        if (!hoaDon.getNguoiDung().getUserId().equals(user.getUserId())) {
            model.addAttribute("error", "Bạn không có quyền thao tác đơn hàng này!");
            return "redirect:/don-hang";
        }

        // Chỉ cho phép đặt lại nếu đơn hàng đã hủy
        if (hoaDon.getTrangThaiDonHang().getMaTT() != 4) {
            model.addAttribute("error", "Chỉ có thể đặt lại đơn hàng đã bị hủy!");
            return "redirect:/don-hang";
        }

        // Cập nhật trạng thái lại thành "Chờ xác nhận"
        hoaDon.setTrangThaiDonHang(trangThaiDonHangService.findById(0).orElse(null));
        hoaDon.setNgayLap(LocalDateTime.now()); // cập nhật lại thời gian đặt
        hoaDonService.save(hoaDon);

        model.addAttribute("message", "Đơn hàng đã được đặt lại và đang chờ xác nhận.");
        return "redirect:/don-hang/chi-tiet/" + id;
    }
}
