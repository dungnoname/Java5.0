package com.poly.assigment.service;

import com.poly.assigment.entity.Hang;
import java.util.List;
import java.util.Optional;

public interface HangService {
    List<Hang> findAll();
    Optional<Hang> findById(Integer id);
    Hang save(Hang hang);
    void deleteById(Integer id);
}
