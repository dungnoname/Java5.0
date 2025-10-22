package com.poly.assigment.service;

import com.poly.assigment.dao.DanhGiaDAO;
import com.poly.assigment.entity.DanhGia;
import com.poly.assigment.entity.HoaDon;
import com.poly.assigment.entity.SanPham;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DanhGiaServiceImpl implements DanhGiaService {

    @Autowired
    private DanhGiaDAO danhGiaDAO;

    @Override
    public List<DanhGia> findBySanPham(SanPham sanPham) {
        return danhGiaDAO.findBySanPham(sanPham);
    }

    @Override
    public List<DanhGia> findAll() {
        return danhGiaDAO.findAll();
    }

    @Override
    public Optional<DanhGia> findById(Integer id) {
        return danhGiaDAO.findById(id);
    }

    @Override
    public DanhGia save(DanhGia dg) {
        return danhGiaDAO.save(dg);
    }

    @Override
    public void deleteById(Integer id) {
        danhGiaDAO.deleteById(id);
    }
    @Override
    public List<DanhGia> findByHoaDon(HoaDon hoaDon) {
        return danhGiaDAO.findByHoaDon(hoaDon);
    }

    @Override
    public List<DanhGia> findBySanPhamMaSP(Integer maSP) {
        return danhGiaDAO.findBySanPhamMaSP(maSP);
    }
}
