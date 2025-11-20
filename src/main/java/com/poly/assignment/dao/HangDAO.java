package com.poly.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assignment.entity.Hang;

@Repository
public interface HangDAO extends JpaRepository<Hang, Integer> {
}
