<template>
  <section class="cart-page">
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">🛒 Giỏ hàng của bạn</h3>

      <!-- LOADING STATE (Trạng thái đang tải) -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2">Đang tải giỏ hàng...</p>
      </div>

      <div v-else>
        <!-- TRƯỜNG HỢP CÓ SẢN PHẨM -->
        <div v-if="cartItems.length > 0" class="row">
          
          <!-- 1. BẢNG GIỎ HÀNG -->
          <div class="col-lg-8">
            <div class="table-responsive bg-white shadow-sm rounded">
              <table class="table align-middle mb-0">
                <thead class="table-light">
                  <tr>
                    <th style="width:50px;"></th>
                    <th>Sản phẩm</th>
                    <th>Giá</th>
                    <th style="width:150px;">Số lượng</th>
                    <th>Tạm tính</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in cartItems" :key="item.maGH">
                    
                    <!-- Nút xóa -->
                    <td>
                      <button @click="removeItem(item.sanPham.maSP)" 
                              class="btn btn-outline-danger btn-sm border-0"
                              title="Xóa sản phẩm">
                        <i class="bi bi-x-lg"></i>
                      </button>
                    </td>

                    <!-- Thông tin sản phẩm -->
                    <td>
                      <div class="d-flex align-items-center">
                        <img :src="item.sanPham.imgUrl || '/placeholder.jpg'" 
                             width="70" height="70" 
                             class="me-3 rounded object-fit-cover">
                        <div>
                          <router-link :to="'/product/' + item.sanPham.maSP" 
                                       class="text-decoration-none text-dark fw-semibold">
                            {{ item.sanPham.tenSP }}
                          </router-link>
                          <div class="small text-muted">{{ item.sanPham.loaiSanPham?.tenLoai }}</div>
                        </div>
                      </div>
                    </td>

                    <!-- Giá -->
                    <td class="fw-bold text-secondary">
                      {{ formatCurrency(item.sanPham.donGiaBan) }}
                    </td>

                    <!-- Cập nhật số lượng -->
                    <td>
                      <div class="input-group input-group-sm" style="width: 120px;">
                        <button class="btn btn-outline-secondary" 
                                @click="updateQuantity(item, -1)"
                                :disabled="item.soLuong <= 1">-</button>
                        
                        <input type="text" class="form-control text-center" 
                               :value="item.soLuong" readonly>
                        
                        <button class="btn btn-outline-secondary" 
                                @click="updateQuantity(item, 1)">+</button>
                      </div>
                    </td>

                    <!-- Tạm tính (Subtotal) -->
                    <td class="fw-bold text-danger">
                      {{ formatCurrency(item.sanPham.donGiaBan * item.soLuong) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- 2. TÓM TẮT ĐƠN HÀNG (Summary) -->
          <div class="col-lg-4 mt-4 mt-lg-0">
            <div class="card shadow-sm border-0">
              <div class="card-body p-4">
                <h5 class="card-title fw-bold mb-4">Tóm tắt đơn hàng</h5>
                
                <ul class="list-group list-group-flush mb-3">
                  <li class="list-group-item d-flex justify-content-between bg-transparent px-0">
                    <span class="text-muted">Tạm tính</span>
                    <strong class="text-dark">{{ formatCurrency(totalAmount) }}</strong>
                  </li>
                  <li class="list-group-item bg-transparent px-0">
                    <span class="text-muted">Vận chuyển</span>
                    <p class="mb-0 small text-success">
                      <i class="bi bi-truck"></i> Miễn phí giao hàng
                    </p>
                  </li>
                  <li class="list-group-item d-flex justify-content-between bg-transparent px-0 py-3 border-top">
                    <span class="fs-5 fw-bold">Tổng cộng</span>
                    <span class="fs-5 fw-bold text-danger">{{ formatCurrency(totalAmount) }}</span>
                  </li>
                </ul>

                <button @click="handleCheckout" class="btn btn-primary w-100 py-2 fw-bold text-uppercase shadow-sm">
                  Tiến hành đặt hàng
                </button>
                
                <router-link to="/" class="btn btn-outline-secondary w-100 mt-2 border-0">
                  <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- TRƯỜNG HỢP GIỎ HÀNG TRỐNG -->
        <div v-else class="text-center py-5 bg-white rounded shadow-sm">
          <i class="bi bi-cart-x text-muted display-1"></i>
          <h4 class="mt-3">Giỏ hàng của bạn đang trống</h4>
          <p class="text-muted">Hãy dạo một vòng và chọn những sản phẩm yêu thích nhé!</p>
          <router-link to="/" class="btn btn-primary px-4 py-2 mt-2">
            Mua sắm ngay
          </router-link>
        </div>
      </div>

    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

// --- VARIABLES (Biến) ---
const router = useRouter();
const cartItems = ref([]);
const loading = ref(true);

// Hàm lấy Header chứa Token
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token');
  return { headers: { Authorization: `Bearer ${token}` } };
};

// --- COMPUTED PROPERTIES (Thuộc tính tính toán) ---
// Tính tổng tiền
const totalAmount = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return total + (item.sanPham.donGiaBan * item.soLuong);
  }, 0);
});

// --- FUNCTIONS (Hàm xử lý) ---

// 1. Format tiền tệ
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

// 2. Lấy danh sách giỏ hàng từ API
const fetchCart = async () => {
  loading.value = true;
  try {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      router.push('/login'); // Nếu chưa login thì đá về trang login
      return;
    }

    const response = await axios.get('http://localhost:8080/api/cart', getAuthHeader());
    cartItems.value = response.data;
  } catch (error) {
    console.error("Lỗi tải giỏ hàng:", error);
    if (error.response && error.response.status === 401) {
      alert("Phiên đăng nhập hết hạn!");
      router.push('/login');
    }
  } finally {
    loading.value = false;
  }
};

// 3. Cập nhật số lượng (Tăng/Giảm)
const updateQuantity = async (item, change) => {
  const newQty = item.soLuong + change;
  
  // Chặn không cho giảm xuống dưới 1
  if (newQty < 1) return;

  try {
    // Gọi API cập nhật
    await axios.put('http://localhost:8080/api/cart/update', {
      maSP: item.sanPham.maSP,
      soLuong: newQty
    }, getAuthHeader());
    
    // Cập nhật UI ngay lập tức (Optimistic update)
    item.soLuong = newQty;
  } catch (error) {
    console.error("Lỗi cập nhật:", error);
    alert("Không thể cập nhật số lượng!");
  }
};

// 4. Xóa sản phẩm
const removeItem = async (maSP) => {
  if (!confirm("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")) return;

  try {
    await axios.delete(`http://localhost:8080/api/cart/remove/${maSP}`, getAuthHeader());
    
    // Lọc bỏ sản phẩm khỏi danh sách trên UI
    cartItems.value = cartItems.value.filter(item => item.sanPham.maSP !== maSP);
    
    // Reload nhẹ để cập nhật số lượng trên Header (nếu có Mini Cart)
    window.location.reload();
  } catch (error) {
    console.error("Lỗi xóa:", error);
    alert("Xóa thất bại!");
  }
};

// 5. Đặt hàng (Checkout)
const handleCheckout = () => {
  router.push('/orders'); 
};

// --- LIFECYCLE HOOKS (Vòng đời component) ---
onMounted(() => {
  fetchCart();
});
</script>

<style scoped>
.object-fit-cover {
  object-fit: cover;
}
/* Làm đẹp thanh cuộn nếu bảng quá dài */
.table-responsive::-webkit-scrollbar {
  height: 6px;
}
.table-responsive::-webkit-scrollbar-thumb {
  background-color: #dee2e6;
  border-radius: 4px;
}
</style>