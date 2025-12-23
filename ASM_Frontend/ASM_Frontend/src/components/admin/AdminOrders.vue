<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h3>Quản lý đơn hàng</h3>
      <button class="btn btn-sm btn-outline-primary" @click="fetchOrders">
        <i class="bi bi-arrow-clockwise"></i> Tải lại
      </button>
    </div>

    <div v-if="loading" class="text-center py-4">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <table v-else class="table table-bordered table-striped align-middle text-center">
      <thead class="table-dark">
        <tr>
          <th>STT</th>
          <th>Mã HĐ</th>
          <th>Ngày lập</th>
          <th>Mã người dùng</th>
          <th>Nhân viên</th>
          <th>Trạng thái</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(hd, index) in orders" :key="hd.maHD">
          <td>{{ index + 1 }}</td>
          <td>{{ hd.maHD }}</td>
          <td>{{ formatDate(hd.ngayLap) }}</td>
          <td>{{ hd.nguoiDung?.userId || '—' }}</td> 
          <td>{{ hd.nhanVien?.hoTen || 'Nhân viên tạm thời' }}</td>
          <td>
            <span :class="getStatusBadgeClass(hd.trangThaiDonHang?.maTT)">
              {{ hd.trangThaiDonHang?.tenTT || '—' }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-danger me-2" @click="deleteOrder(hd.maHD)">
              Xóa
            </button>

            <button class="btn btn-sm btn-warning" @click="openEditModal(hd)">
              Sửa
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="modal fade" id="editModal" tabindex="-1" ref="editModalRef">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-warning text-dark">
            <h5 class="modal-title">Sửa thông tin đơn hàng #{{ editData.maHD }}</h5>
            <button type="button" class="btn-close" @click="hideModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveOrder">
              <div class="mb-3">
                <label class="form-label">Trạng thái</label>
                <select class="form-select" v-model="editData.trangThaiId" required>
                  <option value="" disabled>-- Chọn trạng thái --</option>
                  <option v-for="tt in statuses" :key="tt.maTT" :value="tt.maTT">
                    {{ tt.tenTT }}
                  </option>
                </select>
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="hideModal">Hủy</button>
                <button type="submit" class="btn btn-warning">Lưu thay đổi</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import axios from 'axios';
// 🔥 1. IMPORT MODAL TỪ BOOTSTRAP
import { Modal } from 'bootstrap'; 

// --- STATE ---
const orders = ref([]);
const statuses = ref([]);
const loading = ref(false);

// 🔥 2. KHAI BÁO BIẾN TOÀN CỤC
let editModalObj = null;

// Dữ liệu tạm để sửa
const editData = reactive({
  maHD: null,
  trangThaiId: ''
});

// --- API HELPER ---
const API_URL = 'http://localhost:8080/api/admin/orders';
const getHeader = () => {
  const token = localStorage.getItem('jwt_token');
  return { headers: { Authorization: `Bearer ${token}` } };
};

// --- FORMAT HELPER ---
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('vi-VN', { 
    year: 'numeric', month: '2-digit', day: '2-digit', 
    hour: '2-digit', minute: '2-digit' 
  });
};

const getStatusBadgeClass = (statusId) => {
    switch(statusId) {
        case 0: return 'badge bg-warning text-dark';
        case 1: return 'badge bg-info text-dark';
        case 2: return 'badge bg-primary';
        case 3: return 'badge bg-success';
        case 4: return 'badge bg-danger';
        default: return '';
    }
}

// --- MAIN FUNCTIONS ---

// 1. Lấy danh sách Trạng thái
const fetchStatuses = async () => {
  try {
    const res = await axios.get(`${API_URL}/statuses`, getHeader());
    statuses.value = res.data;
  } catch (e) {
    console.error("Lỗi lấy trạng thái:", e);
  }
};

// 2. Lấy danh sách Đơn hàng
const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await axios.get(API_URL, getHeader());
    orders.value = res.data;
  } catch (e) {
    console.error("Lỗi lấy đơn hàng:", e);
    if(e.response && e.response.status === 403) alert("Bạn không có quyền truy cập!");
  } finally {
    loading.value = false;
  }
};

// 3. Mở Modal Sửa
const openEditModal = (order) => {
  // Gán dữ liệu vào biến tạm
  editData.maHD = order.maHD;
  editData.trangThaiId = order.trangThaiDonHang?.maTT;

  // 🔥 GỌI BIẾN TOÀN CỤC
  if (editModalObj) editModalObj.show();
};

// 4. Đóng Modal
const hideModal = () => {
  if (editModalObj) editModalObj.hide();
};

// 5. Lưu thay đổi (Gọi API Update)
const saveOrder = async () => {
  try {
    await axios.put(`${API_URL}/${editData.maHD}/status`, 
      { trangThaiId: editData.trangThaiId }, 
      getHeader()
    );
    alert("Cập nhật thành công!");
    hideModal();
    fetchOrders(); // Tải lại danh sách
  } catch (e) {
    console.error(e);
    alert("Cập nhật thất bại!");
  }
};

// 6. Xóa đơn hàng
const deleteOrder = async (id) => {
  if (!confirm(`Bạn có chắc muốn xóa đơn hàng #${id} không?`)) return;

  try {
    await axios.delete(`${API_URL}/${id}`, getHeader());
    alert("Đã xóa thành công!");
    fetchOrders();
  } catch (e) {
    alert("Không thể xóa đơn hàng này (Có thể do lỗi ràng buộc dữ liệu).");
  }
};

// --- LIFECYCLE ---
onMounted(() => {
  fetchStatuses();
  fetchOrders();
  
  // 🔥 3. KHỞI TẠO MODAL SAU KHI DOM RENDER
  setTimeout(() => {
      const modalEl = document.getElementById('editModal');
      if (modalEl) editModalObj = new Modal(modalEl);
  }, 200);
});
</script>

<style scoped>
/* Giữ nguyên style bootstrap */
</style>