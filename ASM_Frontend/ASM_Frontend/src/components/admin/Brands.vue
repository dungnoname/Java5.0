<template>
  <div class="container my-4">
    <h3 class="mb-4 text-center">Quản lý Hãng Sản Phẩm</h3>

    <div v-if="alert.message" :class="['alert alert-dismissible fade show', alert.type === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      <span>{{ alert.message }}</span>
      <button type="button" class="btn-close" @click="closeAlert"></button>
    </div>

    <div class="d-flex justify-content-end mb-3">
      <button class="btn btn-primary" @click="openModal('add')">
        + Thêm hãng mới
      </button>
    </div>

    <table class="table table-bordered table-hover text-center align-middle">
      <thead class="table-dark">
        <tr>
          <th>STT</th>
          <th>Mã Hãng</th>
          <th>Tên Hãng</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in listHang" :key="item.maHang">
          <td>{{ index + 1 }}</td>
          <td>{{ item.maHang }}</td>
          <td>{{ item.tenHang }}</td>
          <td>
            <button class="btn btn-warning btn-sm me-2" @click="openModal('edit', item)">
              Sửa
            </button>
            <button class="btn btn-danger btn-sm" @click="confirmDelete(item)">
              Xóa
            </button>
          </td>
        </tr>
        <tr v-if="listHang.length === 0">
          <td colspan="4">Không có dữ liệu hãng nào.</td>
        </tr>
      </tbody>
    </table>

    <div class="modal fade" id="addEditModal" tabindex="-1" aria-hidden="true" ref="modalElement">
      <div class="modal-dialog">
        <div class="modal-content">
          <form @submit.prevent="saveBrand">
            <div class="modal-header bg-primary text-white">
              <h5 class="modal-title">{{ isEditMode ? 'Cập nhật Hãng' : 'Thêm Hãng mới' }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Mã Hãng</label>
                <input type="text" class="form-control" v-model="form.maHang" readonly placeholder="Tự động sinh mã">
              </div>
              <div class="mb-3">
                <label class="form-label">Tên Hãng <span class="text-danger">*</span></label>
                <input type="text" class="form-control" v-model="form.tenHang" required>
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
            Bạn có chắc chắn muốn xóa hãng <strong>{{ form.tenHang }}</strong> không?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-danger" @click="deleteBrand">Xóa</button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// --- CẤU HÌNH ---
const API_URL = 'http://localhost:8080/api/admin/brands'; 
const listHang = ref([]);
const isEditMode = ref(false);
const form = ref({
  maHang: null,
  tenHang: ''
});
const alert = ref({
  message: '',
  type: ''
});

let modalInstance = null;
let deleteModalInstance = null;

// --- HÀM LẤY TOKEN (QUAN TRỌNG) ---
// Hàm này lấy token từ localStorage để kẹp vào mỗi lần gọi API
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token'); 
  // Lưu ý: Key 'jwt_token' phải trùng với lúc bạn lưu khi login
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
};

// --- LIFECYCLE ---
onMounted(() => {
  loadData();
  
  const modalEl = document.getElementById('addEditModal');
  const deleteModalEl = document.getElementById('deleteModal');
  if (window.bootstrap) {
      modalInstance = new window.bootstrap.Modal(modalEl);
      deleteModalInstance = new window.bootstrap.Modal(deleteModalEl);
  }
});

// --- METHODS ---
const loadData = async () => {
  try {
    // 👇 THÊM getAuthHeader() VÀO ĐÂY 👇
    const response = await axios.get(API_URL, getAuthHeader());
    listHang.value = response.data;
  } catch (error) {
    console.error("Lỗi tải dữ liệu:", error);
    if (error.response && error.response.status === 403) {
      showAlert('Bạn không có quyền truy cập (Cần quyền Admin)!', 'error');
    } else if (error.response && error.response.status === 401) {
      showAlert('Vui lòng đăng nhập lại!', 'error');
    }
  }
};

const openModal = (mode, item = null) => {
  isEditMode.value = mode === 'edit';
  if (mode === 'edit' && item) {
    form.value = { ...item };
  } else {
    form.value = { maHang: null, tenHang: '' };
  }
  if(modalInstance) modalInstance.show();
};

const saveBrand = async () => {
  try {
    if (isEditMode.value) {
      // 👇 THÊM getAuthHeader() VÀO ĐÂY 👇
      await axios.put(`${API_URL}/${form.value.maHang}`, form.value, getAuthHeader());
      showAlert('Cập nhật thành công!', 'success');
    } else {
      // 👇 THÊM getAuthHeader() VÀO ĐÂY 👇
      await axios.post(API_URL, form.value, getAuthHeader());
      showAlert('Thêm mới thành công!', 'success');
    }
    loadData();
    if(modalInstance) modalInstance.hide();
  } catch (error) {
    const msg = error.response?.data || error.message;
    showAlert('Lỗi: ' + msg, 'error');
  }
};

const confirmDelete = (item) => {
  form.value = { ...item };
  if(deleteModalInstance) deleteModalInstance.show();
};

const deleteBrand = async () => {
  try {
    // 👇 THÊM getAuthHeader() VÀO ĐÂY 👇
    await axios.delete(`${API_URL}/${form.value.maHang}`, getAuthHeader());
    showAlert('Xóa thành công!', 'success');
    loadData();
    if(deleteModalInstance) deleteModalInstance.hide();
  } catch (error) {
    const msg = error.response?.data || error.message;
    showAlert('Lỗi xóa: ' + msg, 'error');
    if(deleteModalInstance) deleteModalInstance.hide();
  }
};

const showAlert = (msg, type) => {
  alert.value = { message: msg, type: type };
  setTimeout(() => {
    alert.value.message = '';
  }, 3000);
};

const closeAlert = () => {
  alert.value.message = '';
};
</script>

<style scoped>
</style>