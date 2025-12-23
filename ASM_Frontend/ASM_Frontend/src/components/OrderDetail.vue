<template>
  <div class="container my-5">
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">Đang tải chi tiết đơn hàng...</p>
    </div>

    <div v-else-if="!order" class="alert alert-danger text-center">
      Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này.
      <br>
      <router-link to="/orders" class="btn btn-sm btn-outline-danger mt-2">Quay lại danh sách</router-link>
    </div>

    <div v-else class="card shadow p-4">
      <h3 class="text-center mb-4 text-primary fw-bold">Chi tiết đơn hàng</h3>

      <div class="row">
        <div class="col-md-6 mb-4">
          <h5 class="text-secondary border-bottom pb-2">Thông tin đơn hàng</h5>
          <p><strong>Mã đơn hàng:</strong> #{{ order.maHD }}</p>
          <p><strong>Ngày lập:</strong> {{ formatDate(order.ngayLap) }}</p>
          <p><strong>Trạng thái:</strong> 
            <span :class="getStatusClass(order.trangThaiDonHang?.maTT)">
              {{ order.trangThaiDonHang?.tenTT }}
            </span>
          </p>
        </div>

        <div class="col-md-6 mb-4">
          <h5 class="text-secondary border-bottom pb-2">Thông tin nhận hàng</h5>
          <p><strong>Họ tên:</strong> {{ order.nguoiDung?.hoTen || order.nguoiDung?.username }}</p>
          <p><strong>Email:</strong> {{ order.nguoiDung?.email }}</p>
          <p><strong>Số điện thoại:</strong> {{ order.nguoiDung?.soDienThoai }}</p>
          <p><strong>Địa chỉ:</strong> {{ order.nguoiDung?.diaChi }}</p>
        </div>
      </div>

      <div class="table-responsive mb-4">
        <h5 class="text-secondary mb-3">Danh sách sản phẩm</h5>
        <table class="table table-bordered align-middle table-hover">
          <thead class="table-primary text-center">
            <tr>
              <th>STT</th>
              <th>Tên sản phẩm</th>
              <th>Hình ảnh</th>
              <th>Số lượng</th>
              <th>Đơn giá</th>
              <th>Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in order.chiTietHoaDonList" :key="item.maCTHD">
              <td class="text-center">{{ index + 1 }}</td>
              <td>
                <router-link :to="'/product/' + item.sanPham?.maSP" class="text-decoration-none fw-semibold">
                  {{ item.sanPham?.tenSP }}
                </router-link>
              </td>
              <td class="text-center">
                <img :src="item.sanPham?.imgUrl || '/placeholder.jpg'" alt="Sản phẩm"
                     class="rounded border"
                     style="width: 80px; height: 80px; object-fit: cover;">
              </td>
              <td class="text-center">{{ item.soLuongBan }}</td>
              <td class="text-end">{{ formatCurrency(item.donGia) }}</td>
              <td class="text-end fw-bold">{{ formatCurrency(item.soLuongBan * item.donGia) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="text-end mb-4">
        <h5>
          <strong>Tổng tiền:</strong>
          <span class="text-danger fw-bold fs-4 ms-2">
            {{ formatCurrency(totalAmount) }}
          </span>
        </h5>
      </div>

      <div class="d-flex justify-content-center gap-3 mt-4">
        <router-link to="/orders" class="btn btn-secondary px-4">
          <i class="bi bi-arrow-left"></i> Quay lại danh sách
        </router-link>

        <button 
          v-if="order.trangThaiDonHang?.maTT === 4"
          @click="handleReOrder"
          class="btn btn-success px-4"
        >
          <i class="bi bi-arrow-repeat"></i> Đặt lại đơn hàng
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const order = ref(null);
const loading = ref(true);

// Lấy ID từ URL (ví dụ: /order/123 -> id = 123)
const orderId = route.params.id;

// Helpers
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token');
  return { headers: { Authorization: `Bearer ${token}` } };
};

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
const formatDate = (d) => new Date(d).toLocaleString('vi-VN');

// Màu trạng thái
const getStatusClass = (statusId) => {
    switch(statusId) {
        case 0: return 'badge bg-warning text-dark';
        case 1: return 'badge bg-info text-dark';
        case 2: return 'badge bg-primary';
        case 3: return 'badge bg-success';
        case 4: return 'badge bg-danger';
        default: return 'badge bg-secondary';
    }
};

// Tính tổng tiền (Computed để tự động tính lại nếu dữ liệu thay đổi)
const totalAmount = computed(() => {
  if (!order.value || !order.value.chiTietHoaDons) return 0;
  return order.value.chiTietHoaDons.reduce((sum, item) => sum + (item.donGia * item.soLuong), 0);
});

// --- API ---

// 1. Lấy chi tiết đơn hàng
const fetchOrderDetail = async () => {
  loading.value = true;
  try {
    // Gọi API UserOrderRestController (cần đảm bảo backend có API này)
    const res = await axios.get(`http://localhost:8080/api/orders/${orderId}`, getAuthHeader());
    order.value = res.data;
  } catch (error) {
    console.error("Lỗi tải đơn hàng:", error);
    if(error.response && error.response.status === 403) {
      alert("Bạn không có quyền xem đơn hàng này!");
      router.push('/orders');
    }
  } finally {
    loading.value = false;
  }
};

// 2. Xử lý Đặt lại (Re-order)
const handleReOrder = async () => {
  if(!confirm("Bạn có muốn thêm các sản phẩm trong đơn hàng này vào giỏ để đặt lại không?")) return;
  
  try {
    // Cách 1: Gọi API Backend re-order riêng (nếu bạn đã viết)
    // await axios.post(`http://localhost:8080/api/orders/reorder/${orderId}`, {}, getAuthHeader());
    
    // Cách 2: (Đơn giản hơn cho Frontend) Duyệt qua từng món và thêm vào giỏ
    for (const item of order.value.chiTietHoaDons) {
       await axios.post('http://localhost:8080/api/cart/add', {
          maSP: item.sanPham.maSP,
          soLuong: item.soLuong
       }, getAuthHeader());
    }

    alert("Đã thêm sản phẩm vào giỏ hàng! Chuyển đến giỏ hàng ngay.");
    router.push('/cart');

  } catch (error) {
    console.error(error);
    alert("Có lỗi khi đặt lại đơn hàng.");
  }
};

onMounted(() => {
  fetchOrderDetail();
});
</script>