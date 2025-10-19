package com.poly.assigment.service;

import com.poly.assigment.dao.TrangThaiDonHangDAO;
import com.poly.assigment.entity.TrangThaiDonHang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TrangThaiDonHangServiceImpl implements TrangThaiDonHangService {

    @Autowired
    private TrangThaiDonHangDAO trangThaiDonHangDAO;

    @Override
    public List<TrangThaiDonHang> findAll() {
        return trangThaiDonHangDAO.findAll();
    }

    @Override
    public Optional<TrangThaiDonHang> findById(Integer id) {
        return trangThaiDonHangDAO.findById(id);
    }

    @Override
    public TrangThaiDonHang save(TrangThaiDonHang tt) {
        return trangThaiDonHangDAO.save(tt);
    }

    @Override
    public void deleteById(Integer id) {
        trangThaiDonHangDAO.deleteById(id);
    }
}
