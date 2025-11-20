    package com.poly.assignment.service;

    import com.poly.assignment.dao.HangDAO;
    import com.poly.assignment.entity.Hang;
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
    }//
