package com.poly.assigment.controller.crud;

import com.poly.assigment.dao.UserDAO;
import com.poly.assigment.entity.HoaDon;
import com.poly.assigment.entity.TrangThaiDonHang;
import com.poly.assigment.entity.User;
import com.poly.assigment.service.HoaDonService;
import com.poly.assigment.service.TrangThaiDonHangService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/CRUD/quan-ly-don-hang")
public class CRUDDonHangController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private TrangThaiDonHangService trangThaiDonHangService;

    @Autowired
    private UserDAO userDAO;

    // ✅ Tạo nhân viên tạm (chưa có đăng nhập)
    private User getTempStaff(HttpSession session) {
        User staff = (User) session.getAttribute("staffSession");
        if (staff == null) {
            staff = userDAO.findById(6) // lấy Nguyễn Văn Nhân (staff1)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên tạm"));
            session.setAttribute("staffSession", staff);
        }
        return staff;
    }

    // ✅ [GET] Hiển thị danh sách đơn hàng
    @GetMapping
    public String list(Model model) {
        List<HoaDon> hoaDonList = hoaDonService.findAll();
        List<TrangThaiDonHang> trangThaiList = trangThaiDonHangService.findAll();

        model.addAttribute("hoaDonList", hoaDonList);
        model.addAttribute("trangThaiList", trangThaiList);
        return "CRUD/QuanLyDonHang";
    }

    // ✅ [POST] Cập nhật đơn hàng
    @PostMapping("/update")
    public String update(
            @RequestParam("id") Integer id,
            @RequestParam("trangThaiId") Integer trangThaiId,
            HttpSession session
    ) {
        HoaDon hoaDon = hoaDonService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn để cập nhật!"));

        // Lấy nhân viên tạm
        User nhanVien = getTempStaff(session);

        // Cập nhật trạng thái và nhân viên
        hoaDon.setTrangThaiDonHang(trangThaiDonHangService.findById(trangThaiId).orElse(null));
        hoaDon.setNhanVien(nhanVien);

        hoaDonService.save(hoaDon);
        return "redirect:/CRUD/quan-ly-don-hang";
    }

    // ✅ [POST] Xóa đơn hàng
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        hoaDonService.deleteById(id);
        return "redirect:/CRUD/quan-ly-don-hang";
    }
}
