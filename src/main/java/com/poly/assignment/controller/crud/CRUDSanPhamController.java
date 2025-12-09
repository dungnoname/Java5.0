package com.poly.assignment.controller.crud;

import com.poly.assignment.entity.Hang;
import com.poly.assignment.entity.LoaiSanPham;
import com.poly.assignment.entity.SanPham;
import com.poly.assignment.service.HangService;
import com.poly.assignment.service.LoaiSanPhamService;
import com.poly.assignment.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid; // Đảm bảo đúng package cho Jakarta Validation

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class CRUDSanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    @Autowired
    private HangService hangService;

    // Đảm bảo các danh sách này luôn có sẵn cho form
    @ModelAttribute("loaiSanPhams")
    public List<LoaiSanPham> getLoaiSanPhams() {
        return loaiSanPhamService.findAll();
    }

    @ModelAttribute("hangs")
    public List<Hang> getHangs() {
        return hangService.findAll();
    }

    @GetMapping
    public String index(Model model) {
        List<SanPham> sanPhams = sanPhamService.findAll();
        model.addAttribute("sanPhams", sanPhams);

        // Khởi tạo sanPham cho form thêm mới.
        // Đảm bảo loaiSanPham và hang không null nếu cần bind trực tiếp đến maLoai/maHang
        // Mặc dù th:selected đã có kiểm tra null, việc này tăng cường độ an toàn
        SanPham newSanPham = new SanPham();
        newSanPham.setLoaiSanPham(new LoaiSanPham()); // Khởi tạo đối tượng LoaiSanPham rỗng
        newSanPham.setHang(new Hang()); // Khởi tạo đối tượng Hang rỗng
        newSanPham.setTrangThai(true); // Đặt trạng thái mặc định cho sản phẩm mới
        model.addAttribute("sanPham", newSanPham);

        return "CRUD/QuanLySanPham";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Optional<SanPham> sanPhamOpt = sanPhamService.findById(id);
        if (sanPhamOpt.isPresent()) {
            SanPham sanPhamToEdit = sanPhamOpt.get();
            // Đảm bảo loaiSanPham và hang không null nếu chúng bị null trong DB
            // Mặc dù @ManyToOne thường fetch EAGER hoặc proxy, nhưng đây là cách phòng hờ
            if (sanPhamToEdit.getLoaiSanPham() == null) {
                sanPhamToEdit.setLoaiSanPham(new LoaiSanPham());
            }
            if (sanPhamToEdit.getHang() == null) {
                sanPhamToEdit.setHang(new Hang());
            }
            model.addAttribute("sanPham", sanPhamToEdit);
            List<SanPham> sanPhams = sanPhamService.findAll(); // Vẫn giữ để hiển thị bảng
            model.addAttribute("sanPhams", sanPhams);
            return "CRUD/QuanLySanPham";
        } else {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm có ID: " + id);
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("sanPham") SanPham sanPham,
                       BindingResult result,
                       RedirectAttributes ra,
                       Model model) {
        if (result.hasErrors()) {
            // Khi có lỗi, cần tải lại danh sách sản phẩm và các danh mục cho form
            model.addAttribute("sanPhams", sanPhamService.findAll());
            // Đảm bảo loaiSanPham và hang của đối tượng sanPham hiện tại không null khi trả về form lỗi
            if (sanPham.getLoaiSanPham() == null) {
                sanPham.setLoaiSanPham(new LoaiSanPham());
            }
            if (sanPham.getHang() == null) {
                sanPham.setHang(new Hang());
            }
            ra.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin sản phẩm.");
            return "CRUD/QuanLySanPham"; // Trả về trang form để hiển thị lỗi
        }

        try {
            // Đảm bảo rằng chỉ ID của LoaiSanPham và Hang được sử dụng để lưu
            // Nếu bạn bind trực tiếp đến loaiSanPham.maLoai, Spring sẽ tự map.
            // Nếu không, bạn cần tìm và gán đối tượng LoaiSanPham/Hang đầy đủ.
            if (sanPham.getLoaiSanPham() != null && sanPham.getLoaiSanPham().getMaLoai() != null) {
                loaiSanPhamService.findById(sanPham.getLoaiSanPham().getMaLoai())
                        .ifPresent(sanPham::setLoaiSanPham);
            } else {
                sanPham.setLoaiSanPham(null); // Hoặc xử lý lỗi nếu loại SP là bắt buộc
            }

            if (sanPham.getHang() != null && sanPham.getHang().getMaHang() != null) {
                hangService.findById(sanPham.getHang().getMaHang())
                        .ifPresent(sanPham::setHang);
            } else {
                sanPham.setHang(null); // Hoặc xử lý lỗi nếu hãng là bắt buộc
            }

            sanPhamService.save(sanPham);
            ra.addFlashAttribute("successMessage", "Lưu sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lưu sản phẩm thất bại: " + e.getMessage());
            // Khi có lỗi, cần tải lại danh sách sản phẩm và các danh mục cho form
            model.addAttribute("sanPhams", sanPhamService.findAll());
            // Đảm bảo loaiSanPham và hang của đối tượng sanPham hiện tại không null khi trả về form lỗi
            if (sanPham.getLoaiSanPham() == null) {
                sanPham.setLoaiSanPham(new LoaiSanPham());
            }
            if (sanPham.getHang() == null) {
                sanPham.setHang(new Hang());
            }
            return "CRUD/QuanLySanPham"; // Trả về trang form để hiển thị lỗi nếu save lỗi
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            sanPhamService.deleteById(id);
            ra.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Xóa sản phẩm thất bại: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}//