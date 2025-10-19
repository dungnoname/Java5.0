package com.poly.assigment.service;

import com.poly.assigment.dao.GioHangDAO;
import com.poly.assigment.entity.GioHang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GioHangServiceImpl implements GioHangService {

    @Autowired
    private GioHangDAO gioHangDAO;

    @Override
    public List<GioHang> findAll() {
        return gioHangDAO.findAll();
    }

    @Override
    public Optional<GioHang> findById(Integer id) {
        return gioHangDAO.findById(id);
    }

    @Override
    public GioHang save(GioHang gh) {
        return gioHangDAO.save(gh);
    }

    @Override
    public void deleteById(Integer id) {
        gioHangDAO.deleteById(id);
    }
}
