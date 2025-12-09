package com.poly.assignment.service;

import com.poly.assignment.dao.ChiTietHoaDonDAO;
import com.poly.assignment.dao.ThongKeDAO;
import com.poly.assignment.dao.UserDAO;
import com.poly.assignment.dto.ThongKeDoanhThuLoaiHangDTO;
import com.poly.assignment.dto.TopKhachHangVipDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    @Autowired
    private ThongKeDAO thongKeDAO;

    public List<ThongKeDoanhThuLoaiHangDTO> getDoanhThuByLoaiSanPham() {
        return thongKeDAO.getDoanhThuTheoLoai();
    }

    public List<TopKhachHangVipDTO> getTop10KhachHangVip() {
        return thongKeDAO.getTopKhachHang();
    }
}//