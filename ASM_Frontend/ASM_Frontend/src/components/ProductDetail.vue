<template>
  <section>
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p>Đang tải thông tin sản phẩm...</p>
    </div>

    <div v-else-if="product" class="container py-4">
      
      <div class="row g-4">

        <div class="col-md-6">
          <div class="card border-0 shadow-sm">
            <div class="img-container p-3">
              <img :src="product.imgUrl || '/placeholder-image.jpg'" 
                   class="card-img-top main-image" 
                   :alt="product.tenSP">
            </div>
            <div class="d-flex justify-content-center flex-wrap p-2 bg-light rounded-bottom">
               <img :src="product.imgUrl" class="img-thumbnail m-1 border-primary" style="width: 70px; height: 70px; object-fit: cover; cursor: pointer;">
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <h2 class="fw-bold text-dark">{{ product.tenSP }}</h2>
          
          <div class="mb-3 d-flex align-items-center">
            <div class="text-warning me-2">
              <i v-for="n in 5" :key="n" class="bi" 
                 :class="n <= Math.round(avgRating) ? 'bi-star-fill' : 'bi-star'"></i>
            </div>
            <span class="text-muted">({{ reviews.length }} đánh giá của khách hàng)</span>
          </div>

          <h3 class="text-danger fw-bold mb-3">
            {{ formatCurrency(product.donGiaBan) }}
            <small class="text-muted text-decoration-line-through fs-6 ms-2">
              {{ formatCurrency(product.donGiaBan * 1.2) }}
            </small>
          </h3>

          <ul class="list-unstyled bg-light p-3 rounded border">
            <li class="mb-2"><i class="bi bi-gift-fill text-danger me-2"></i> Tặng nâng cấp thêm 8GB RAM MIỄN PHÍ</li>
            <li class="mb-2"><i class="bi bi-bag-fill text-danger me-2"></i> Tặng túi chống sốc trị giá 300.000 VNĐ</li>
            <li class="mb-2"><i class="bi bi-shield-check text-success me-2"></i> Bảo hành 12 tháng máy Fullbox</li>
            <li class="mb-2"><i class="bi bi-lightning-fill text-warning me-2"></i> Cài đặt miễn phí trọn đời</li>
            <li class="mb-2"><i class="bi bi-truck text-primary me-2"></i> Miễn phí vận chuyển toàn quốc</li>
          </ul>

          <div class="d-flex align-items-center my-3">
            <span class="fw-bold me-3">Số lượng:</span>
            <div class="input-group" style="width: 140px;">
              <button class="btn btn-outline-secondary" type="button" @click="quantity > 1 ? quantity-- : 1">-</button>
              <input type="text" class="form-control text-center" v-model="quantity" readonly>
              <button class="btn btn-outline-secondary" type="button" @click="quantity++">+</button>
            </div>
          </div>

          <div class="d-grid gap-2 my-3 d-md-flex">
            <button class="btn btn-warning btn-lg flex-grow-1 fw-bold text-white">MUA NGAY</button>
            <button @click="addToCart" class="btn btn-primary btn-lg flex-grow-1">
              <i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ hàng
            </button>
          </div>

          <div class="p-3 border rounded bg-white mt-4 shadow-sm">
            <h6 class="fw-bold border-bottom pb-2"><i class="bi bi-headset me-1"></i> Hotline hỗ trợ:</h6>
            <div class="row mt-2 text-muted small">
              <div class="col-6">Hà Nội: <strong class="text-dark">0834.82.1988</strong></div>
              <div class="col-6">TP HCM: <strong class="text-dark">0911.71.2468</strong></div>
              <div class="col-6">Đà Nẵng: <strong class="text-dark">088.602.9669</strong></div>
              <div class="col-6">Cần Thơ: <strong class="text-dark">088.639.3737</strong></div>
            </div>
          </div>
        </div>
      </div>

      <div class="row mt-5">
        <div class="col-12">
          <ul class="nav nav-tabs" id="myTab" role="tablist">
            <li class="nav-item" role="presentation">
              <button class="nav-link fw-bold" 
                      :class="{ active: activeTab === 'desc' }" 
                      @click="activeTab = 'desc'">
                Mô tả sản phẩm
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link fw-bold" 
                      :class="{ active: activeTab === 'review' }" 
                      @click="activeTab = 'review'">
                Đánh giá ({{ reviews.length }})
              </button>
            </li>
          </ul>

          <div class="tab-content border border-top-0 p-4 bg-white shadow-sm rounded-bottom">
            
            <div v-if="activeTab === 'desc'" class="fade show active">
              <h5 class="fw-bold">{{ product.tenSP }}</h5>
              <p class="text-muted">{{ product.moTa || 'Đang cập nhật mô tả...' }}</p>
              
              <table class="table table-bordered mt-3 w-50">
                <tbody>
                  <tr>
                    <th class="bg-light" width="30%">Giá bán</th>
                    <td class="text-danger fw-bold">{{ formatCurrency(product.donGiaBan) }}</td>
                  </tr>
                  <tr>
                    <th class="bg-light">Kho</th>
                    <td>{{ product.soLuongTon }} sản phẩm</td>
                  </tr>
                  <tr>
                    <th class="bg-light">Loại</th>
                    <td>{{ product.loaiSanPham?.tenLoai }}</td>
                  </tr>
                  <tr>
                    <th class="bg-light">Hãng</th>
                    <td>{{ product.hang?.tenHang }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div v-if="activeTab === 'review'" class="fade show active">
              <h5 class="mb-3">Đánh giá trung bình: <span class="text-warning fw-bold">{{ avgRating }} / 5</span> ⭐</h5>

              <div v-if="reviews.length > 0">
                <div v-for="rv in reviews" :key="rv.maDG" class="border-bottom pb-3 mb-3">
                  <div class="d-flex justify-content-between">
                    <strong>{{ rv.user?.hoTen || rv.user?.tenDangNhap || 'Khách hàng' }}</strong>
                    <small class="text-muted">{{ formatDate(rv.ngayDanhGia) }}</small>
                  </div>
                  <div class="text-warning mb-1">
                    <i v-for="n in 5" :key="n" class="bi" :class="n <= rv.soSao ? 'bi-star-fill' : 'bi-star'"></i>
                  </div>
                  <p class="mb-0 text-secondary">{{ rv.noiDung }}</p>
                </div>
              </div>

              <div v-else class="text-center py-4 text-muted">
                <i class="bi bi-chat-square-text fs-1"></i>
                <p class="mt-2">Chưa có đánh giá nào cho sản phẩm này.</p>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center py-5">
       <h3>Không tìm thấy sản phẩm!</h3>
       <router-link to="/" class="btn btn-primary mt-3">Quay lại trang chủ</router-link>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

// --- STATE ---
const product = ref(null);
const reviews = ref([]); // Danh sách đánh giá
const loading = ref(true);
const activeTab = ref('desc'); // Tab mặc định
const quantity = ref(1);

// --- COMPUTED ---
// Tính điểm trung bình
const avgRating = computed(() => {
  if (reviews.value.length === 0) return 5; // Mặc định 5 sao nếu chưa có đánh giá
  const total = reviews.value.reduce((sum, rv) => sum + rv.soSao, 0);
  return (total / reviews.value.length).toFixed(1);
});

// --- API CALLS ---

// 1. Lấy chi tiết sản phẩm
const fetchProduct = async () => {
  try {
    const id = route.params.id; // Lấy ID từ URL
    const res = await axios.get(`http://localhost:8080/api/products/${id}`);
    product.value = res.data;
    
    

  } catch (error) {
    console.error("Lỗi tải sản phẩm:", error);
  } finally {
    loading.value = false;
  }
};

const fetchReviews = async () => {
      try {
        const id = route.params.id;
        // Gọi API Backend vừa viết
        const res = await axios.get(`http://localhost:8080/api/reviews/product/${id}`);
        reviews.value = res.data;
      } catch (error) {
        console.error("Lỗi lấy đánh giá:", error);
        reviews.value = [];
      }
    };

// 2. Thêm vào giỏ hàng
const addToCart = async () => {
  const token = localStorage.getItem('jwt_token');
  if (!token) {
    alert("Vui lòng đăng nhập để mua hàng!");
    router.push('/login');
    return;
  }

  try {
    await axios.post('http://localhost:8080/api/cart/add', {
      maSP: product.value.maSP,
      soLuong: quantity.value
    }, {
      headers: { Authorization: `Bearer ${token}` }
    });

    alert(`Đã thêm ${quantity.value} sản phẩm vào giỏ!`);
    // Reload để cập nhật giỏ hàng trên Header
    window.location.reload(); 
  } catch (error) {
    console.error("Lỗi thêm giỏ:", error);
    alert("Lỗi khi thêm vào giỏ hàng.");
  }
};

// --- FORMATTERS ---
const formatCurrency = (value) => {
  if (!value) return '0 đ';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('vi-VN') + ' ' + new Date(dateString).toLocaleTimeString('vi-VN');
};

// --- INIT ---
onMounted(() => {
  fetchProduct();

  fetchReviews();
});
</script>

<style scoped>
.img-container {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
}

.main-image {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain;
}

.nav-link {
  color: #555;
  cursor: pointer;
}
.nav-link.active {
  color: #0d6efd !important;
  background-color: #fff;
  border-color: #dee2e6 #dee2e6 #fff;
}
</style>