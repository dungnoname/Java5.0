package com.poly.assigment.service;

import com.poly.assigment.dao.HangDAO;
import com.poly.assigment.entity.Hang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HangServiceImpl implements HangService {

    @Autowired
    private HangDAO hangDAO;

    @Override
    public List<Hang> findAll() {
        return hangDAO.findAll();
    }

    @Override
    public Optional<Hang> findById(Integer id) {
        return hangDAO.findById(id);
    }

    @Override
    public Hang save(Hang hang) {
        return hangDAO.save(hang);
    }

    @Override
    public void deleteById(Integer id) {
        hangDAO.deleteById(id);
    }
}
