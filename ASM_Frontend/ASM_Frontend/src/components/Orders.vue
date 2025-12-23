<template>
  <section class="orders-page">
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">📦 Quản lý đơn hàng</h3>

      <ul class="nav nav-tabs mb-4">
        <li class="nav-item">
          <button class="nav-link" 
                  :class="{ active: activeTab === 'checkout' }"
                  @click="activeTab = 'checkout'">
            Đặt hàng
          </button>
        </li>
        <li class="nav-item">
          <button class="nav-link" 
                  :class="{ active: activeTab === 'history' }"
                  @click="activeTab = 'history'">
            Đơn hàng của tôi
          </button>
        </li>
      </ul>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else class="tab-content">
        
        <div v-if="activeTab === 'checkout'" class="fade show active">
          
          <div v-if="cartItems.length === 0" class="alert alert-warning text-center">
            Giỏ hàng trống! Vui lòng chọn sản phẩm trước khi đặt hàng.
            <br>
            <router-link to="/" class="btn btn-primary btn-sm mt-2">Mua sắm ngay</router-link>
          </div>

          <div v-else class="row">
            <div class="col-lg-6 mb-4">
              <div class="card shadow-sm">
                <div class="card-header bg-white fw-bold">Thông tin giao hàng</div>
                <div class="card-body">
                  <div class="mb-3">
                    <label class="form-label">Họ tên</label>
                    <input type="text" class="form-control bg-light" :value="userInfo.hoTen" readonly>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <input type="text" class="form-control bg-light" :value="userInfo.soDienThoai || 'Chưa cập nhật'" readonly>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Địa chỉ nhận hàng</label>
                    <textarea class="form-control bg-light" rows="3" readonly>{{ userInfo.diaChi || 'Chưa cập nhật địa chỉ' }}</textarea>
                    <small class="text-muted">
                      * Để thay đổi địa chỉ, vui lòng vào mục <router-link to="/profile">Tài khoản</router-link>
                    </small>
                  </div>
                  
                  <button @click="processCheckout" class="btn btn-success w-100 py-2 fw-bold">
                    XÁC NHẬN ĐẶT HÀNG
                  </button>
                </div>
              </div>
            </div>

            <div class="col-lg-6">
              <div class="card shadow-sm">
                <div class="card-header bg-white fw-bold">Sản phẩm trong đơn</div>
                <ul class="list-group list-group-flush">
                  <li v-for="item in cartItems" :key="item.maGH" 
                      class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                      <strong>{{ item.sanPham.tenSP }}</strong>
                      <div class="small text-muted">SL: {{ item.soLuong }}</div>
                    </div>
                    <span class="fw-bold">{{ formatCurrency(item.sanPham.donGiaBan * item.soLuong) }}</span>
                  </li>
                  
                  <li class="list-group-item d-flex justify-content-between align-items-center bg-light">
                    <h5 class="mb-0">Tổng cộng:</h5>
                    <h5 class="text-danger fw-bold mb-0">{{ formatCurrency(totalAmount) }}</h5>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'history'" class="fade show active">
          
          <div v-if="orders.length === 0" class="text-center text-muted py-5">
            <i class="bi bi-inbox fs-1"></i>
            <p class="mt-2">Bạn chưa có đơn hàng nào.</p>
          </div>

          <div v-else class="table-responsive bg-white shadow-sm rounded">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th>Mã đơn</th>
                  <th>Ngày đặt</th>
                  <th>Tổng tiền</th> <th>Trạng thái</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="hd in orders" :key="hd.maHD">
                  <td class="fw-bold text-primary">#HD{{ hd.maHD }}</td>
                  <td>{{ formatDate(hd.ngayLap) }}</td>
                  
                  <td>---</td> 

                  <td>
                    <span class="badge" :class="getStatusBadge(hd.trangThaiDonHang?.maTT)">
                      {{ hd.trangThaiDonHang?.tenTT }}
                    </span>
                  </td>
                  
                  <td>
                    <router-link :to="'/order/' + hd.maHD" class="btn btn-outline-primary btn-sm me-2">
                      Chi tiết
                    </router-link>

                    <button v-if="hd.trangThaiDonHang?.maTT === 0"
                            @click="cancelOrder(hd.maHD)" 
                            class="btn btn-outline-danger btn-sm">
                      Hủy
                    </button>

                    <button v-if="hd.trangThaiDonHang?.maTT === 3"
                            class="btn btn-outline-success btn-sm">
                      Đánh giá
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const activeTab = ref('checkout'); // Tab mặc định
const loading = ref(true);

// Dữ liệu
const userInfo = ref({});
const cartItems = ref([]);
const orders = ref([]);

// --- API HELPER ---
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token');
  return { headers: { Authorization: `Bearer ${token}` } };
};

// --- LOAD DATA ---
const loadData = async () => {
  loading.value = true;
  try {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      router.push('/login');
      return;
    }

    // Gọi song song 3 API để tiết kiệm thời gian
    const [profileRes, cartRes, historyRes] = await Promise.all([
      axios.get('http://localhost:8080/api/profile', getAuthHeader()),
      axios.get('http://localhost:8080/api/cart', getAuthHeader()),
      axios.get('http://localhost:8080/api/orders/history', getAuthHeader())
    ]);

    userInfo.value = profileRes.data;
    cartItems.value = cartRes.data;
    orders.value = historyRes.data;

    // Nếu giỏ hàng trống thì tự động chuyển sang tab lịch sử
    if (cartItems.value.length === 0) {
      activeTab.value = 'history';
    }

  } catch (error) {
    console.error("Lỗi tải dữ liệu:", error);
    if (error.response && error.response.status === 401) {
      alert("Phiên đăng nhập hết hạn!");
      router.push('/login');
    }
  } finally {
    loading.value = false;
  }
};

// --- COMPUTED ---
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.sanPham.donGiaBan * item.soLuong), 0);
});

// --- ACTIONS ---

// 1. Xử lý Đặt hàng
const processCheckout = async () => {
  if (!confirm("Xác nhận đặt hàng với thông tin trên?")) return;

  try {
    await axios.post('http://localhost:8080/api/orders/checkout', {}, getAuthHeader());
    
    alert("🎉 Đặt hàng thành công!");
    
    // Reload lại dữ liệu: Giỏ hàng sẽ trống, Lịch sử sẽ thêm đơn mới
    await loadData(); 
    
    // Chuyển sang tab lịch sử
    activeTab.value = 'history';
    
    // Update header cart count
    window.dispatchEvent(new Event('auth-change')); // Trick để update header nếu cần

  } catch (error) {
    console.error(error);
    alert("Đặt hàng thất bại! Vui lòng thử lại.");
  }
};

// 2. Hủy đơn hàng
const cancelOrder = async (id) => {
  if (!confirm("Bạn chắc chắn muốn hủy đơn hàng này?")) return;

  try {
    await axios.put(`http://localhost:8080/api/orders/cancel/${id}`, {}, getAuthHeader());
    alert("Đã hủy đơn hàng.");
    loadData(); // Load lại danh sách
  } catch (error) {
    console.error(error);
    alert("Không thể hủy đơn hàng này.");
  }
};

// --- HELPERS ---
const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString('vi-VN');
};

// Màu sắc Badge trạng thái
const getStatusBadge = (statusId) => {
  switch (statusId) {
    case 0: return 'bg-warning text-dark'; // Chờ xác nhận
    case 1: return 'bg-info text-dark';    // Đã xác nhận
    case 2: return 'bg-primary';           // Đang giao
    case 3: return 'bg-success';           // Hoàn thành
    case 4: return 'bg-danger';            // Hủy
    default: return 'bg-secondary';
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.nav-link {
  cursor: pointer;
  font-weight: 600;
  color: #6c757d;
}
.nav-link.active {
  color: #0d6efd;
  border-bottom: 2px solid #0d6efd;
}
</style>