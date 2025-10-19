package com.poly.assigment.service;

import com.poly.assigment.dao.RoleDAO;
import com.poly.assigment.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Override
    public List<Role> findAll() {
        return roleDAO.findAll();
    }

    @Override
    public Optional<Role> findById(Integer id) {
        return roleDAO.findById(id);
    }

    @Override
    public Role save(Role role) {
        return roleDAO.save(role);
    }

    @Override
    public void deleteById(Integer id) {
        roleDAO.deleteById(id);
    }
}
