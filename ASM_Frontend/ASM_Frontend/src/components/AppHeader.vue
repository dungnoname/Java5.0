<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light shadow-sm">
    <div class="container">
      <router-link class="navbar-brand fw-bold" to="/">TechShop</router-link>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item"><router-link class="nav-link" to="/">Trang chủ</router-link></li>
          <li class="nav-item"><router-link class="nav-link" to="/products">Sản phẩm</router-link></li>
          </ul>

        <ul class="navbar-nav ms-auto align-items-center">
          
          <li class="nav-item dropdown cart-dropdown me-3" v-if="isLoggedIn">
            <router-link to="/cart" class="nav-link position-relative">
              <i class="bi bi-cart3 fs-4"></i>
              <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                {{ cartCount }}
              </span>
            </router-link>

            <div class="cart-content">
              <div v-if="cartItems.length === 0" class="text-center py-3 text-muted">
                Giỏ hàng trống 😢
              </div>

              <div v-else>
                <div v-for="item in cartItems" :key="item.maGH" class="cart-item">
                  <img :src="item.sanPham.imgUrl" alt="SP">
                  <div class="flex-grow-1">
                    <div class="fw-semibold text-truncate" style="max-width: 180px;">{{ item.sanPham.tenSP }}</div>
                    <div class="small text-muted">SL: {{ item.soLuong }}</div>
                    <div class="text-danger fw-bold">{{ formatCurrency(item.sanPham.donGiaBan * item.soLuong) }}</div>
                  </div>
                </div>
                
                <div class="cart-footer">
                  <router-link to="/cart" class="btn btn-primary btn-sm w-100">Xem giỏ hàng</router-link>
                </div>
              </div>
            </div>
          </li>

          <li class="nav-item" v-if="!isLoggedIn">
            <router-link class="nav-link" to="/login">Đăng nhập</router-link>
          </li>
          <li class="nav-item" v-if="!isLoggedIn">
            <router-link class="nav-link" to="/register">Đăng ký</router-link>
          </li>

          <li class="nav-item dropdown" v-if="isLoggedIn">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
              Xin chào, {{ userInfo }}
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
                <li v-if="isAdmin">
                    <router-link class="dropdown-item fw-bold text-primary" to="/admin">
                    Trang quản trị
                    </router-link>
                </li>
                <li><router-link class="dropdown-item" to="/profile">Tài khoản</router-link></li>
                <li><router-link class="dropdown-item" to="/orders">Đơn hàng</router-link></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="#" @click.prevent="handleLogout">Đăng xuất</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const isLoggedIn = ref(false);
const userInfo = ref('');
const cartItems = ref([]);

const isAdmin = ref(false);

// 1. Kiểm tra trạng thái đăng nhập
const checkLoginStatus = () => {
  const token = localStorage.getItem('jwt_token');
  // Lấy chuỗi role từ LocalStorage
  const userRolesString = localStorage.getItem('user_role'); 

  if (token) {
    isLoggedIn.value = true;
    userInfo.value = localStorage.getItem('user_info') || 'User';
    
    // 🔥 BỔ SUNG LOGIC CHECK ADMIN TẠI ĐÂY 🔥
    if (userRolesString) {
      try {
        const roles = JSON.parse(userRolesString);
        // Kiểm tra xem mảng role có chứa 'ROLE_ADMIN' không
        isAdmin.value = roles.includes('ROLE_ADMIN');
      } catch (e) {
        console.error("Lỗi đọc quyền user:", e);
        isAdmin.value = false;
      }
    } else {
      isAdmin.value = false;
    }

    fetchMiniCart(); 
  } else {
    // Reset trạng thái khi chưa đăng nhập / logout
    isLoggedIn.value = false;
    userInfo.value = '';
    isAdmin.value = false; // <--- Nhớ reset biến này
    cartItems.value = [];
  }
};

// 2. Gọi API lấy giỏ hàng (Mini Cart)
const fetchMiniCart = async () => {
  try {
    const token = localStorage.getItem('jwt_token');
    // Cấu hình header Authorization
    const response = await axios.get('http://localhost:8080/api/cart', {
      headers: { Authorization: `Bearer ${token}` }
    });
    cartItems.value = response.data;
  } catch (error) {
    console.error("Lỗi tải mini cart:", error);
  }
};

// 3. Tính tổng số lượng (Cart Count)
const cartCount = computed(() => {
  return cartItems.value.length;
});

// 4. Xử lý Đăng xuất
const handleLogout = () => {
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user_role');
  localStorage.removeItem('user_info');
  isLoggedIn.value = false;
  cartItems.value = []; // Xóa giỏ hàng trên view
  window.dispatchEvent(new Event('auth-change'));
  router.push('/login');
};

// 5. Format tiền tệ
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

onMounted(() => {
  checkLoginStatus();
  window.addEventListener('auth-change', checkLoginStatus);
});

onUnmounted(() => {
  window.removeEventListener('auth-change', checkLoginStatus);
});

//logic kiểm tra admin
// const isAdmin = computed(() => {
//   const rolesString = localStorage.getItem('user_role');
//   if (!rolesString) return false;
  
//   // Parse chuỗi JSON thành mảng
//   try {
//     const roles = JSON.parse(rolesString);
//     // Kiểm tra xem trong mảng roles có 'ROLE_ADMIN' không
//     return roles.includes('ROLE_ADMIN');
//   } catch (e) {
//     return false;
//   }
// });

</script>

<style scoped>
/* CSS cho Mini Cart Dropdown */
.cart-dropdown {
  position: relative;
}

.cart-content {
  display: none; /* Mặc định ẩn */
  position: absolute;
  right: 0;
  top: 100%;
  width: 320px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
  padding: 10px;
  z-index: 1000;
}

/* Hiển thị khi hover */
.cart-dropdown:hover .cart-content {
  display: block;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.cart-item img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  margin-right: 10px;
  border-radius: 4px;
}

.cart-footer {
  margin-top: 10px;
}
</style>