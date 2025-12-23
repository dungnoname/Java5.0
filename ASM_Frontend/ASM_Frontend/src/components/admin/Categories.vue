<template>
  <div class="container my-4">
    <h3 class="mb-4 text-center">Quản lý Loại Sản Phẩm</h3>

    <div v-if="alert.message" :class="['alert alert-dismissible fade show', alert.type === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      <span>{{ alert.message }}</span>
      <button type="button" class="btn-close" @click="closeAlert"></button>
    </div>

    <div class="d-flex justify-content-end mb-3">
      <button class="btn btn-primary" @click="openModal('add')">
        + Thêm loại sản phẩm
      </button>
    </div>

    <table class="table table-bordered table-hover text-center align-middle">
      <thead class="table-dark">
        <tr>
          <th>STT</th>
          <th>Mã Loại</th>
          <th>Tên Loại</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in listCategories" :key="item.maLoai">
          <td>{{ index + 1 }}</td>
          <td>{{ item.maLoai }}</td>
          <td>{{ item.tenLoai }}</td>
          <td>
            <button class="btn btn-warning btn-sm me-2" @click="openModal('edit', item)">
              Sửa
            </button>
            <button class="btn btn-danger btn-sm" @click="confirmDelete(item)">
              Xóa
            </button>
          </td>
        </tr>
        <tr v-if="listCategories.length === 0">
          <td colspan="4">Không có loại sản phẩm nào.</td>
        </tr>
      </tbody>
    </table>

    <div class="modal fade" id="addEditModal" tabindex="-1" aria-hidden="true" ref="modalElement">
      <div class="modal-dialog">
        <div class="modal-content">
          <form @submit.prevent="saveCategory">
            <div class="modal-header bg-primary text-white">
              <h5 class="modal-title">{{ isEditMode ? 'Chỉnh sửa loại sản phẩm' : 'Thêm loại sản phẩm' }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
              <div class="mb-3">
                <label for="maLoai" class="form-label">Mã loại</label>
                <input type="text" class="form-control" id="maLoai" v-model="form.maLoai" readonly placeholder="Tự động tạo khi thêm mới">
              </div>
              <div class="mb-3">
                <label for="tenLoai" class="form-label">Tên loại <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="tenLoai" v-model="form.tenLoai" required>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
              <button type="submit" class="btn btn-success">Lưu</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true" ref="deleteModalElement">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Xác nhận xóa</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            Bạn có chắc chắn muốn xóa loại sản phẩm <strong>{{ form.tenLoai }}</strong> không?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-danger" @click="deleteCategory">Xóa</button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
// 🔥 1. IMPORT MODAL TỪ BOOTSTRAP
import { Modal } from 'bootstrap'; 

// --- CẤU HÌNH ---
const API_URL = 'http://localhost:8080/api/admin/categories'; 

// --- STATE ---
const listCategories = ref([]);
const isEditMode = ref(false);
const form = ref({
  maLoai: null,
  tenLoai: ''
});
const alert = ref({
  message: '',
  type: ''
});

// 🔥 2. KHAI BÁO BIẾN TOÀN CỤC
let addEditModalObj = null;
let deleteModalObj = null;

// --- AUTH TOKEN ---
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token'); 
  return { headers: { Authorization: `Bearer ${token}` } };
};

// --- LIFECYCLE HOOK ---
onMounted(() => {
  loadData();
  
  // 🔥 3. KHỞI TẠO MODAL SAU KHI DOM RENDER
  setTimeout(() => {
      const modalEl = document.getElementById('addEditModal');
      const deleteModalEl = document.getElementById('deleteModal');
      
      if (modalEl) addEditModalObj = new Modal(modalEl);
      if (deleteModalEl) deleteModalObj = new Modal(deleteModalEl);
  }, 200);
});

// --- METHODS ---

// 1. Tải danh sách
const loadData = async () => {
  try {
    const response = await axios.get(API_URL, getAuthHeader());
    listCategories.value = response.data;
  } catch (error) {
    console.error("Lỗi tải dữ liệu:", error);
    showAlert('Lỗi tải danh sách!', 'error');
  }
};

// 2. Mở Modal
const openModal = (mode, item = null) => {
  isEditMode.value = mode === 'edit';
  if (mode === 'edit' && item) {
    form.value = { ...item };
  } else {
    form.value = { maLoai: null, tenLoai: '' };
  }
  
  // 🔥 GỌI SHOW() TỪ BIẾN TOÀN CỤC
  if (addEditModalObj) addEditModalObj.show();
};

// 3. Lưu
const saveCategory = async () => {
  try {
    // Validate
    if (!form.value.tenLoai.trim()) {
        showAlert('Tên loại không được để trống!', 'error');
        return;
    }

    if (isEditMode.value) {
      await axios.put(`${API_URL}/${form.value.maLoai}`, form.value, getAuthHeader());
      showAlert('Cập nhật thành công!', 'success');
    } else {
      await axios.post(API_URL, form.value, getAuthHeader());
      showAlert('Thêm mới thành công!', 'success');
    }
    loadData();
    
    // Đóng Modal
    if (addEditModalObj) addEditModalObj.hide();
    
  } catch (error) {
    const msg = error.response?.data || error.message;
    showAlert('Lỗi: ' + msg, 'error');
  }
};

// 4. Mở Modal Xóa
const confirmDelete = (item) => {
  form.value = { ...item };
  if (deleteModalObj) deleteModalObj.show();
};

// 5. Xóa
const deleteCategory = async () => {
  try {
    await axios.delete(`${API_URL}/${form.value.maLoai}`, getAuthHeader());
    showAlert('Xóa thành công!', 'success');
    loadData();
    if (deleteModalObj) deleteModalObj.hide();
  } catch (error) {
    const msg = error.response?.data || error.message;
    showAlert('Lỗi xóa: ' + msg, 'error'); // Thường do ràng buộc khóa ngoại (đang có SP dùng loại này)
    if (deleteModalObj) deleteModalObj.hide();
  }
};

// --- TIỆN ÍCH ---
const showAlert = (msg, type) => {
  alert.value = { message: msg, type: type };
  setTimeout(() => { alert.value.message = ''; }, 3000);
};

const closeAlert = () => { alert.value.message = ''; };
</script>

<style scoped>
/* CSS tùy chỉnh (nếu cần), phần lớn đã dùng Bootstrap */
</style>