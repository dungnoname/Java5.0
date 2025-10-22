package com.poly.assigment.service;

import com.poly.assigment.dao.GioHangDAO;
import com.poly.assigment.entity.GioHang;
import com.poly.assigment.entity.SanPham;
import com.poly.assigment.entity.User;
import jakarta.transaction.Transactional;
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
    public void deleteById(Integer id) {
        gioHangDAO.deleteById(id);
    }

    @Override
    public List<GioHang> getGioHangByUser(User user) {
        return gioHangDAO.findByUser(user);
    }

    @Override
    public GioHang findByUserAndSanPham(User user, SanPham sanPham) {
        return gioHangDAO.findByUserAndSanPham(user, sanPham);
    }

    @Override
    public GioHang save(GioHang gioHang) {
        return gioHangDAO.save(gioHang);
    }

    @Override
    public void delete(Integer maGH) {
        gioHangDAO.deleteById(maGH);
    }

    @Override
    @Transactional
    public void deleteAll() {
        gioHangDAO.deleteAll();
    }

    @Override
    @Transactional
    public void deleteAllByUser(User user) {
        gioHangDAO.deleteAllByUser(user);
    }


}
