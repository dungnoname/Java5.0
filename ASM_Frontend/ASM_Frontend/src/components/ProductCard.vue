<template>
  <div class="card h-100 shadow-sm product-card">
    <div class="position-relative">
       <span v-if="product.soLuongTon < 5" class="badge bg-danger position-absolute top-0 start-0 m-2">Sắp hết</span>
       
       <router-link :to="'/product/' + product.maSP">
          <img :src="product.imgUrl || '/placeholder.jpg'" class="card-img-top p-3" alt="SP" 
               style="height: 200px; object-fit: contain;">
       </router-link>
    </div>
    
    <div class="card-body d-flex flex-column">
      <router-link :to="'/product/' + product.maSP" class="text-decoration-none text-dark">
         <h6 class="card-title text-truncate" :title="product.tenSP">{{ product.tenSP }}</h6>
      </router-link>
      
      <div class="mt-auto">
        <p class="text-danger fw-bold mb-1 fs-5">{{ formatCurrency(product.donGiaBan) }}</p>
        <small class="text-muted text-decoration-line-through">{{ formatCurrency(product.donGiaBan * 1.1) }}</small>
        
        <button class="btn btn-outline-primary w-100 mt-2" @click="addToCart(product)">
          <i class="bi bi-cart-plus"></i> Thêm vào giỏ
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';

const props = defineProps(['product']);

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const addToCart = async (product) => {
  const token = localStorage.getItem('jwt_token');
  if(!token) {
     alert("Vui lòng đăng nhập!");
     return;
  }
  try {
     await axios.post('http://localhost:8080/api/cart/add', {
        maSP: product.maSP,
        soLuong: 1
     }, { headers: { Authorization: `Bearer ${token}` } });
     alert("Đã thêm vào giỏ hàng!");
     window.location.reload();
  } catch(e) {
     alert("Lỗi thêm giỏ hàng");
  }
}
</script>

<style scoped>
.product-card:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
  border-color: #0d6efd;
  transition: 0.3s;
}
</style>