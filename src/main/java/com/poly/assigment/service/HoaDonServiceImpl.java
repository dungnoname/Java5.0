package com.poly.assigment.service;

import com.poly.assigment.dao.HoaDonDAO;
import com.poly.assigment.entity.HoaDon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    @Autowired
    private HoaDonDAO hoaDonDAO;

    @Override
    public List<HoaDon> findAll() {
        return hoaDonDAO.findAll();
    }

    @Override
    public Optional<HoaDon> findById(Integer id) {
        return hoaDonDAO.findById(id);
    }

    @Override
    public HoaDon save(HoaDon hd) {
        return hoaDonDAO.save(hd);
    }

    @Override
    public void deleteById(Integer id) {
        hoaDonDAO.deleteById(id);
    }
}
