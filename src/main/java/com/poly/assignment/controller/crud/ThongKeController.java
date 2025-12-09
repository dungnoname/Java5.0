package com.poly.assignment.controller.crud;

import com.poly.assignment.dto.ThongKeDoanhThuLoaiHangDTO;
import com.poly.assignment.dto.TopKhachHangVipDTO;
import com.poly.assignment.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/statistics")
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    @GetMapping
    public String showStatistics(Model model) {
        // Lấy dữ liệu thống kê doanh thu theo loại hàng
        List<ThongKeDoanhThuLoaiHangDTO> doanhThuLoaiHang = thongKeService.getDoanhThuByLoaiSanPham();
        model.addAttribute("doanhThuLoaiHang", doanhThuLoaiHang);

        // Lấy dữ liệu Top 10 khách hàng VIP
        List<TopKhachHangVipDTO> topKhachHangVip = thongKeService.getTop10KhachHangVip();
        model.addAttribute("topKhachHangVip", topKhachHangVip);

        return "CRUD/TongHopThongKe"; // Trả về file HTML của bạn
    }
}//