package com.poly.assignment.service;

import com.poly.assignment.dao.LoaiSanPhamDAO;
import com.poly.assignment.entity.LoaiSanPham;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {

    @Autowired
    private LoaiSanPhamDAO loaiSanPhamDAO;

    @Override
    public List<LoaiSanPham> findAll() {
        return loaiSanPhamDAO.findAll();
    }

    @Override
    public Optional<LoaiSanPham> findById(Integer id) {
        return loaiSanPhamDAO.findById(id);
    }

    @Override
    public LoaiSanPham save(LoaiSanPham loai) {
        return loaiSanPhamDAO.save(loai);
    }

    @Override
    public void deleteById(Integer id) {
        loaiSanPhamDAO.deleteById(id);
    }
}
