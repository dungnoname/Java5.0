package com.poly.assignment.service;

import com.poly.assignment.dao.ChiTietHoaDonDAO;
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
    private ChiTietHoaDonDAO chiTietHoaDonDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public List<ThongKeDoanhThuLoaiHangDTO> getDoanhThuByLoaiSanPham() {
        return chiTietHoaDonDAO.getDoanhThuByLoaiSanPham();
    }

    @Override
    public List<TopKhachHangVipDTO> getTop10KhachHangVip() {
        // Sử dụng PageRequest để lấy Top 10
        return userDAO.getTopKhachHangVip(PageRequest.of(0, 10));
    }
}//