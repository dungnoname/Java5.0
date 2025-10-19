package com.poly.assigment.service;

import com.poly.assigment.dao.SanPhamDAO;
import com.poly.assigment.entity.SanPham;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    @Autowired
    private SanPhamDAO sanPhamDAO;

    @Override
    public List<SanPham> findAll() {
        return sanPhamDAO.findAll();
    }

    @Override
    public Optional<SanPham> findById(Integer id) {
        return sanPhamDAO.findById(id);
    }

    @Override
    public SanPham save(SanPham sp) {
        return sanPhamDAO.save(sp);
    }

    @Override
    public void deleteById(Integer id) {
        sanPhamDAO.deleteById(id);
    }
}
