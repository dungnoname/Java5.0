package com.poly.assignment.controller.page;

import com.poly.assignment.entity.*;
import com.poly.assignment.service.ChiTietHoaDonService;
import com.poly.assignment.service.DanhGiaService;
import com.poly.assignment.service.HoaDonService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/danh-gia")
public class DanhGiaController {

    @Autowired
    private HttpSession session;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private DanhGiaService danhGiaService;

    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService; // dùng query theo hoaDon

    // Hiển thị form đánh giá
    @GetMapping("/form/{hoaDonId}")
    public String formDanhGia(@PathVariable("hoaDonId") Integer hoaDonId, Model model) {
        HoaDon hd = hoaDonService.findById(hoaDonId).orElse(null);
        if (hd == null || hd.getTrangThaiDonHang().getMaTT() != 3) {
            return "redirect:/home/donHang"; // nếu đơn không tồn tại hoặc chưa hoàn thành
        }

        // Lấy danh sách sản phẩm trong đơn
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonService.findByHoaDon(hd);
        model.addAttribute("hoaDon", hd);
        model.addAttribute("chiTietList", chiTietList);

        return "home/formDanhGia"; // file Thymeleaf form đánh giá
    }

    // Xử lý submit đánh giá
    @PostMapping("/submit/{hoaDonId}")
    public String submitDanhGia(@PathVariable("hoaDonId") Integer hoaDonId,
                                @RequestParam Map<String, String> params,
                                RedirectAttributes redirectAttrs) {

        HoaDon hd = hoaDonService.findById(hoaDonId).orElse(null);
        if (hd == null) {
            redirectAttrs.addFlashAttribute("msgError", "Đơn hàng không tồn tại!");
            return "redirect:/don-hang";
        }

        // Lấy danh sách sản phẩm trong đơn
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonService.findByHoaDon(hd);

        for (ChiTietHoaDon cthd : chiTietList) {
            SanPham sp = cthd.getSanPham();

            String ratingKey = "rating_" + sp.getMaSP();
            String commentKey = "comment_" + sp.getMaSP();

            int rating = Integer.parseInt(params.getOrDefault(ratingKey, "0"));
            String comment = params.get(commentKey);
            User currentUser = (User) session.getAttribute("currentUser");
            if (rating > 0) { // chỉ lưu nếu có đánh giá
                DanhGia dg = new DanhGia();
                dg.setSanPham(sp);
                dg.setSoSao(rating);
                dg.setNoiDung(comment);
                dg.setUser(currentUser);
                dg.setNgayDanhGia(LocalDateTime.now());

                danhGiaService.save(dg);
            }
        }

        redirectAttrs.addFlashAttribute("msgSuccess", "Đánh giá sản phẩm thành công!");
        return "redirect:/don-hang";
    }
}
