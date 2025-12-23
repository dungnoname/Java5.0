<template>
  <div class="container my-4">
    <h3 class="mb-4 text-center">Quản lý Sản Phẩm</h3>

    <div v-if="alert.message" :class="['alert alert-dismissible fade show', alert.type === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      <span>{{ alert.message }}</span>
      <button type="button" class="btn-close" @click="closeAlert"></button>
    </div>

    <div class="d-flex justify-content-end mb-3">
      <button class="btn btn-primary" @click="openModal('add')">
        + Thêm sản phẩm mới
      </button>
    </div>

    <div class="table-responsive">
      <table class="table table-bordered table-striped align-middle text-center">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Hình ảnh</th>
            <th>Tên SP</th>
            <th>Đơn giá</th>
            <th>Tồn kho</th>
            <th>Loại</th>
            <th>Hãng</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(sp, index) in listProducts" :key="sp.maSP">
            <td>{{ sp.maSP }}</td>
            <td>
              <img :src="sp.imgUrl || 'https://via.placeholder.com/50'" alt="Img" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
            </td>
            <td class="text-start">{{ sp.tenSP }}</td>
            <td class="text-end">{{ formatCurrency(sp.donGiaBan) }}</td>
            <td>{{ sp.soLuongTon }}</td>
            <td>{{ sp.loaiSanPham ? sp.loaiSanPham.tenLoai : '-' }}</td>
            <td>{{ sp.hang ? sp.hang.tenHang : '-' }}</td>
            <td>
              <span :class="['badge', sp.trangThai ? 'bg-success' : 'bg-secondary']">
                {{ sp.trangThai ? 'Kích hoạt' : 'Ẩn' }}
              </span>
            </td>
            <td>
              <div class="d-flex justify-content-center gap-2">
                <button class="btn btn-warning btn-sm" @click="openModal('edit', sp)">Sửa</button>
                <button class="btn btn-danger btn-sm" @click="confirmDelete(sp)">Xóa</button>
              </div>
            </td>
          </tr>
          <tr v-if="listProducts.length === 0">
            <td colspan="9">Không có sản phẩm nào.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" id="productModal" tabindex="-1" aria-hidden="true" ref="modalElement">
      <div class="modal-dialog modal-lg"> <div class="modal-content">
          <form @submit.prevent="saveProduct">
            <div class="modal-header bg-primary text-white">
              <h5 class="modal-title">{{ isEditMode ? 'Cập nhật Sản Phẩm' : 'Thêm Sản Phẩm Mới' }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Tên Sản Phẩm <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" v-model="form.tenSP" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Đơn Giá Bán <span class="text-danger">*</span></label>
                  <input type="number" class="form-control" v-model="form.donGiaBan" required min="0">
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Số Lượng Tồn <span class="text-danger">*</span></label>
                  <input type="number" class="form-control" v-model="form.soLuongTon" required min="0">
                </div>
                <div class="col-md-6">
                  <label class="form-label">URL Hình Ảnh</label>
                  <input type="text" class="form-control" v-model="form.imgUrl" placeholder="https://...">
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">Mô Tả</label>
                <textarea class="form-control" v-model="form.moTa" rows="3"></textarea>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Loại Sản Phẩm <span class="text-danger">*</span></label>
                  <select class="form-select" v-model="form.loaiSanPham.maLoai" required>
                    <option :value="null" disabled>-- Chọn loại --</option>
                    <option v-for="loai in listCategories" :key="loai.maLoai" :value="loai.maLoai">
                      {{ loai.tenLoai }}
                    </option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Hãng <span class="text-danger">*</span></label>
                  <select class="form-select" v-model="form.hang.maHang" required>
                    <option :value="null" disabled>-- Chọn hãng --</option>
                    <option v-for="h in listBrands" :key="h.maHang" :value="h.maHang">
                      {{ h.tenHang }}
                    </option>
                  </select>
                </div>
              </div>

              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="trangThaiCheck" v-model="form.trangThai">
                <label class="form-check-label" for="trangThaiCheck">Kích hoạt (Hiển thị trên web)</label>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
              <button type="submit" class="btn btn-primary">Lưu thông tin</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true" ref="deleteModalElement">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Cảnh báo xóa</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            Bạn có chắc chắn muốn xóa sản phẩm <strong>{{ form.tenSP }}</strong> không?<br>
            <small class="text-muted">Hành động này không thể hoàn tác.</small>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-danger" @click="deleteProduct">Xóa ngay</button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
// 🔥 1. IMPORT MODAL CHÍNH CHỦ
import { Modal } from 'bootstrap'; 

// --- CẤU HÌNH ---
const BASE_URL = 'http://localhost:8080/api/admin/products'; 

// --- STATE ---
const listProducts = ref([]);
const listCategories = ref([]); 
const listBrands = ref([]); 

// 🔥 2. KHAI BÁO BIẾN MODAL TOÀN CỤC (Để các hàm bên dưới nhìn thấy)
let productModalObj = null;
let deleteModalObj = null;

const isEditMode = ref(false);
const form = ref({
  maSP: null,
  tenSP: '',
  donGiaBan: 0,
  soLuongTon: 0,
  moTa: '',
  imgUrl: '',
  trangThai: true,
  loaiSanPham: { maLoai: null }, 
  hang: { maHang: null }        
});
const alert = ref({ message: '', type: '' });

// --- AUTH TOKEN ---
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token'); 
  return { headers: { Authorization: `Bearer ${token}` } };
};

// --- LIFECYCLE ---
onMounted(() => {
  loadAllData();
  
  // 🔥 3. KHỞI TẠO MODAL CHUẨN (Sau khi DOM render)
  setTimeout(() => {
      const modalEl = document.getElementById('productModal');
      const deleteEl = document.getElementById('deleteModal');
      
      if (modalEl) productModalObj = new Modal(modalEl);
      if (deleteEl) deleteModalObj = new Modal(deleteEl);
  }, 200);
});

// --- METHODS ---

// 1. Tải dữ liệu
const loadAllData = async () => {
  try {
    const config = getAuthHeader();
    const [productsRes, catsRes, brandsRes] = await Promise.all([
      axios.get(BASE_URL, config),
      axios.get(`${BASE_URL}/categories`, config),
      axios.get(`${BASE_URL}/brands`, config)
    ]);

    listProducts.value = productsRes.data;
    listCategories.value = catsRes.data;
    listBrands.value = brandsRes.data;

  } catch (error) {
    console.error(error);
    showAlert('Lỗi tải dữ liệu!', 'error');
  }
};

// 2. Mở Modal Thêm/Sửa
const openModal = (mode, item = null) => {
  isEditMode.value = mode === 'edit';
  if (mode === 'edit' && item) {
    form.value = JSON.parse(JSON.stringify(item));
    if (!form.value.loaiSanPham) form.value.loaiSanPham = { maLoai: null };
    if (!form.value.hang) form.value.hang = { maHang: null };
  } else {
    form.value = {
      maSP: null,
      tenSP: '',
      donGiaBan: 0,
      soLuongTon: 0,
      moTa: '',
      imgUrl: '',
      trangThai: true,
      loaiSanPham: { maLoai: null },
      hang: { maHang: null }
    };
  }
  
  // 🔥 GỌI BIẾN TOÀN CỤC ĐÃ KHAI BÁO
  if (productModalObj) productModalObj.show();
};

// 3. Lưu Sản Phẩm
const saveProduct = async () => {
  try {
    const config = getAuthHeader();
    // Validate sơ bộ
    if (!form.value.loaiSanPham.maLoai || !form.value.hang.maHang) {
        showAlert('Vui lòng chọn Loại và Hãng!', 'error');
        return;
    }

    if (isEditMode.value) {
      await axios.put(`${BASE_URL}/${form.value.maSP}`, form.value, config);
      showAlert('Cập nhật thành công!', 'success');
    } else {
      await axios.post(BASE_URL, form.value, config);
      showAlert('Thêm mới thành công!', 'success');
    }
    
    // Refresh list
    const res = await axios.get(BASE_URL, config);
    listProducts.value = res.data;
    
    // Đóng Modal
    if (productModalObj) productModalObj.hide();

  } catch (error) {
    console.error(error);
    const msg = error.response?.data || error.message;
    showAlert('Lỗi: ' + msg, 'error');
  }
};

// 4. Xóa Sản Phẩm
const confirmDelete = (item) => {
  form.value = { ...item };
  if (deleteModalObj) deleteModalObj.show();
};

const deleteProduct = async () => {
  try {
    const config = getAuthHeader();
    await axios.delete(`${BASE_URL}/${form.value.maSP}`, config);
    showAlert('Xóa thành công!', 'success');
    
    const res = await axios.get(BASE_URL, config);
    listProducts.value = res.data;
    
    if (deleteModalObj) deleteModalObj.hide();
  } catch (error) {
    const msg = error.response?.data || error.message;
    showAlert('Không thể xóa: ' + msg, 'error');
    if (deleteModalObj) deleteModalObj.hide();
  }
};

// --- UTILS ---
const showAlert = (msg, type) => {
  alert.value = { message: msg, type: type };
  setTimeout(() => { alert.value.message = ''; }, 3000);
};

const closeAlert = () => { alert.value.message = ''; };

const formatCurrency = (value) => {
  if (!value) return '0 đ';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
</script>

<style scoped>
/* Không cần CSS thêm vì dùng Bootstrap */
</style>