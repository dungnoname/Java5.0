<template>
  <div class="container my-4">
    <h3 class="mb-4 text-center fw-bold">Tổng hợp – Thống kê kết quả kinh doanh</h3>

    <div v-if="alert.message" :class="['alert alert-dismissible fade show', alert.type === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      <span>{{ alert.message }}</span>
      <button type="button" class="btn-close" @click="closeAlert"></button>
    </div>

    <div class="mb-5">
      <h5 class="mb-3 text-primary fw-bold">1️⃣ Thống kê doanh thu theo loại hàng</h5>
      <div class="table-responsive">
        <table class="table table-bordered table-hover text-center align-middle">
          <thead class="table-dark">
            <tr>
              <th>STT</th>
              <th>Tên loại</th>
              <th>Tổng doanh thu</th>
              <th>Tổng số lượng</th>
              <th>Giá cao nhất</th>
              <th>Giá thấp nhất</th>
              <th>Giá trung bình</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in revenueData" :key="index">
              <td>{{ index + 1 }}</td>
              <td class="text-start">{{ item.tenLoai }}</td>
              <td class="text-end fw-bold text-success">{{ formatCurrency(item.tongDoanhThu) }}</td>
              <td>{{ item.tongSoLuong }}</td>
              <td class="text-end">{{ formatCurrency(item.giaCaoNhat) }}</td>
              <td class="text-end">{{ formatCurrency(item.giaThapNhat) }}</td>
              <td class="text-end">{{ formatCurrency(item.giaTrungBinh) }}</td>
            </tr>
            <tr v-if="revenueData.length === 0">
              <td colspan="7">Chưa có dữ liệu thống kê.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div>
      <h5 class="mb-3 text-success fw-bold">2️⃣ Top 10 khách hàng VIP</h5>
      <div class="table-responsive">
        <table class="table table-bordered table-striped text-center align-middle">
          <thead class="table-dark">
            <tr>
              <th>STT</th>
              <th>Tên khách hàng</th>
              <th>Tổng tiền đã mua</th>
              <th>Ngày mua đầu tiên</th>
              <th>Ngày mua sau cùng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(cus, index) in topCustomers" :key="index">
              <td>{{ index + 1 }}</td>
              <td class="text-start fw-bold">{{ cus.tenKhachHang }}</td>
              <td class="text-end fw-bold text-danger">{{ formatCurrency(cus.tongTienDaMua) }}</td>
              <td>{{ formatDate(cus.ngayMuaDauTien) }}</td>
              <td>{{ formatDate(cus.ngayMuaSauCung) }}</td>
            </tr>
            <tr v-if="topCustomers.length === 0">
              <td colspan="5">Chưa có dữ liệu khách hàng VIP.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// --- CẤU HÌNH API ---
const BASE_URL = 'http://localhost:8080/api/admin/statistics';

// --- STATE (Biến chứa dữ liệu) ---
const revenueData = ref([]);   // Dữ liệu bảng 1
const topCustomers = ref([]);  // Dữ liệu bảng 2
const alert = ref({ message: '', type: '' });

// --- AUTH HEADER (Bắt buộc) ---
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token');
  return { headers: { Authorization: `Bearer ${token}` } };
};

// --- LIFECYCLE ---
onMounted(() => {
  loadStatistics();
});

// --- METHODS ---

// Hàm tải dữ liệu từ API
const loadStatistics = async () => {
  try {
    const config = getAuthHeader();

    // Gọi song song 2 API cùng lúc cho nhanh
    const [revenueRes, customerRes] = await Promise.all([
      axios.get(`${BASE_URL}/revenue-by-category`, config), // API 1
      axios.get(`${BASE_URL}/top-customers`, config)        // API 2
    ]);

    revenueData.value = revenueRes.data;
    topCustomers.value = customerRes.data;

  } catch (error) {
    console.error(error);
    if (error.response && error.response.status === 403) {
      showAlert('Bạn không có quyền xem thống kê!', 'error');
    } else {
      showAlert('Lỗi tải dữ liệu thống kê!', 'error');
    }
  }
};

// Hàm hiển thị thông báo
const showAlert = (msg, type) => {
  alert.value = { message: msg, type: type };
};
const closeAlert = () => alert.value.message = '';

// --- UTILS (Hàm định dạng) ---

// Format tiền VND (Ví dụ: 1000000 -> 1.000.000 ₫)
const formatCurrency = (value) => {
  if (!value) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

// Format ngày tháng (Ví dụ: 2024-01-05 -> 05/01/2024)
const formatDate = (dateString) => {
  if (!dateString) return '';
  // Nếu dateString là mảng [yyyy, mm, dd] (do Jackson serialize LocalDate mặc định)
  if (Array.isArray(dateString)) {
      const [year, month, day] = dateString;
      return `${day < 10 ? '0' + day : day}/${month < 10 ? '0' + month : month}/${year}`;
  }
  // Nếu là chuỗi "yyyy-mm-dd"
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};
</script>

<style scoped>
/* CSS bổ sung nếu cần */
.table th {
  vertical-align: middle;
}
</style>