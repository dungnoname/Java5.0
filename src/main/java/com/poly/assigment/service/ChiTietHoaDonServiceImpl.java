package com.poly.assigment.service;

import com.poly.assigment.dao.ChiTietHoaDonDAO;
import com.poly.assigment.entity.ChiTietHoaDon;
import com.poly.assigment.entity.HoaDon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChiTietHoaDonServiceImpl implements ChiTietHoaDonService {

    @Autowired
    private ChiTietHoaDonDAO chiTietHoaDonDAO;

    @Override
    public List<ChiTietHoaDon> findAll() {
        return chiTietHoaDonDAO.findAll();
    }

    @Override
    public Optional<ChiTietHoaDon> findById(Integer id) {
        return chiTietHoaDonDAO.findById(id);
    }

    @Override
    public ChiTietHoaDon save(ChiTietHoaDon cthd) {
        return chiTietHoaDonDAO.save(cthd);
    }

    @Override
    public void deleteById(Integer id) {
        chiTietHoaDonDAO.deleteById(id);
    }
    @Override
    public List<ChiTietHoaDon> findByHoaDon(HoaDon hoaDon) {
        return chiTietHoaDonDAO.findByHoaDon(hoaDon);
    }

}//




