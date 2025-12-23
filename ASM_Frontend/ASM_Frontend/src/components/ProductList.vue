<template>
  <section>
    <div class="container my-4">

      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><router-link to="/">Trang chủ</router-link></li>
          <li class="breadcrumb-item active" aria-current="page">Danh mục sản phẩm</li>
        </ol>
      </nav>

      <div class="row">
        <div class="col-md-3 mb-4">
          <div class="border p-3 mb-3 bg-light">
            <form @submit.prevent="applyFilter" class="d-flex flex-column gap-2">

              <h6 class="fw-bold mb-3">Danh mục</h6>

              <div class="form-check mb-2">
                <input class="form-check-input" type="radio" name="maLoai" 
                       id="cat-all" :value="null" v-model="selectedCategory">
                <label class="form-check-label" for="cat-all">Tất cả</label>
              </div>

              <div v-for="cat in categories" :key="cat.maLoai" class="form-check mb-2">
                <input class="form-check-input" type="radio" name="maLoai"
                       :id="'cat-' + cat.maLoai"
                       :value="cat.maLoai"
                       v-model="selectedCategory">
                <label class="form-check-label" :for="'cat-' + cat.maLoai">
                  {{ cat.tenLoai }}
                </label>
              </div>

              <hr>
              <h6 class="fw-bold mb-3">Lọc theo giá</h6>
              <div class="input-group input-group-sm">
                <span class="input-group-text">Từ</span>
                <input type="number" class="form-control" v-model="minPrice" placeholder="0" min="0">
              </div>
              <div class="input-group input-group-sm">
                <span class="input-group-text">Đến</span>
                <input type="number" class="form-control" v-model="maxPrice" placeholder="50000000" min="0">
              </div>

              <button type="submit" class="btn btn-primary btn-sm mt-2">Lọc</button>
            </form>
          </div>
        </div>

        <div class="col-md-9">
          <div class="d-flex justify-content-between align-items-center border p-2 mb-3 bg-light flex-wrap gap-2">
            <span>Hiển thị kết quả</span>
            <div class="d-flex align-items-center flex-wrap gap-2">
              <div class="d-flex align-items-center">
                <span class="me-2">Sắp xếp theo</span>
                <select class="form-select form-select-sm">
                  <option>Sắp xếp theo giá: thấp đến cao</option>
                  <option>Sắp xếp theo giá: cao đến thấp</option>
                  <option>Sản phẩm mới</option>
                </select>
              </div>

              <form class="d-flex" role="search" @submit.prevent>
                <input class="form-control form-control-sm" type="search" placeholder="Tìm sản phẩm..." aria-label="Search">
                <button class="btn btn-sm btn-primary ms-1" type="button">Tìm</button>
              </form>
            </div>
          </div>

          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="mt-2">Đang tải dữ liệu...</p>
          </div>

          <div v-else-if="products.length === 0" class="alert alert-warning text-center">
            Không tìm thấy sản phẩm nào phù hợp.
          </div>

          <div v-else class="row g-3">
            <div class="col-md-4 col-sm-6" v-for="p in products" :key="p.maSP">
              <div class="card h-100 product-card-hover">
                <router-link :to="'/product/' + p.maSP">
                  <div class="img-container">
                    <img :src="p.imgUrl" class="card-img-top p-3" :alt="p.tenSP">
                  </div>
                </router-link>
                
                <div class="card-body d-flex flex-column">
                  <span class="badge bg-warning text-dark mb-2 align-self-start">
                    {{ p.loaiSanPham?.tenLoai || 'Khuyến mãi' }}
                  </span>
                  
                  <router-link :to="'/product/' + p.maSP" class="text-decoration-none text-dark flex-grow-1">
                    <h6 class="product-title">{{ p.tenSP }}</h6>
                  </router-link>
                  
                  <p class="text-danger fw-bold mb-2 fs-5">
                    {{ formatCurrency(p.donGiaBan) }}
                  </p>

                  <button @click="addToCart(p)" class="btn btn-outline-primary btn-sm w-100 mt-auto">
                    <i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ hàng
                  </button>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

// --- STATE (Dữ liệu) ---
const products = ref([]);
const categories = ref([]);
const loading = ref(false);

// Biến bộ lọc
const selectedCategory = ref(null);
const minPrice = ref('');
const maxPrice = ref('');

// --- API URL (Cấu hình base URL nếu cần) ---
const API_URL = 'http://localhost:8080/api';

// --- FUNCTIONS (Hàm xử lý) ---

// 1. Format tiền tệ VNĐ
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

// 2. Lấy danh mục sản phẩm (Cho Sidebar)
const fetchCategories = async () => {
  try {
    const response = await axios.get(`${API_URL}/products/categories`);
    categories.value = response.data;
  } catch (error) {
    console.error("Lỗi lấy danh mục:", error);
  }
};

// 3. Lấy sản phẩm (Có hỗ trợ lọc)
const fetchProducts = async () => {
  loading.value = true;
  try {
    // Tạo param gửi lên backend
    const params = {};
    if (selectedCategory.value) params.maLoai = selectedCategory.value;
    if (minPrice.value) params.minPrice = minPrice.value;
    if (maxPrice.value) params.maxPrice = maxPrice.value;

    // Nếu không có param lọc thì gọi API all, có thì gọi API filter
    // (Hoặc dùng chung 1 API /filter nếu backend bạn xử lý null tốt)
    const endpoint = Object.keys(params).length === 0 
                     ? `${API_URL}/products` 
                     : `${API_URL}/products/filter`;

    const response = await axios.get(endpoint, { params });
    products.value = response.data;
  } catch (error) {
    console.error("Lỗi lấy sản phẩm:", error);
  } finally {
    loading.value = false;
  }
};

// 4. Sự kiện nút "Lọc"
const applyFilter = () => {
  fetchProducts();
};

// 5. Thêm vào giỏ hàng
const addToCart = async (product) => {
  const token = localStorage.getItem('jwt_token');
  
  if (!token) {
    alert("Vui lòng đăng nhập để mua hàng!");
    router.push('/login');
    return;
  }

  try {
    await axios.post(`${API_URL}/cart/add`, {
      maSP: product.maSP,
      soLuong: 1
    }, {
      headers: { Authorization: `Bearer ${token}` }
    });
    
    alert(`Đã thêm "${product.tenSP}" vào giỏ hàng!`);
    window.location.reload(); // Reload nhẹ để cập nhật số lượng trên Header
  } catch (error) {
    console.error("Lỗi thêm giỏ hàng:", error);
    alert("Có lỗi xảy ra khi thêm vào giỏ hàng.");
  }
};

// --- HOOK (Chạy khi trang vừa load) ---
onMounted(() => {
  fetchCategories();
  fetchProducts();
});
</script>

<style scoped>
/* CSS Tùy chỉnh thêm cho đẹp hơn Bootstrap mặc định */

.img-container {
  height: 200px; /* Cố định chiều cao ảnh để card đều nhau */
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.img-container img {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

/* Hiệu ứng hover card */
.product-card-hover:hover {
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  border-color: #0d6efd;
}

.product-card-hover:hover img {
  transform: scale(1.05);
}


</style>