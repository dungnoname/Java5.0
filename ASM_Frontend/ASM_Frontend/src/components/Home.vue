<template>
  <div class="home-page">
    
    <div id="homeCarousel" class="carousel slide">
      <div class="carousel-indicators">
        <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="0" class="active"></button>
        <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="1"></button>
        <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="2"></button>
        <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="3"></button>
        <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="4"></button>
      </div>
      
      <div class="carousel-inner">
        <div class="carousel-item active">
          <img src="/photos/index/Banner1.jpg" class="d-block w-100 banner-img" alt="Banner 1">
        </div>
        <div class="carousel-item">
          <img src="/photos/index/Banner2.jpg" class="d-block w-100 banner-img" alt="Banner 2">
        </div>
        <div class="carousel-item">
          <img src="/photos/index/Banner3.jpg" class="d-block w-100 banner-img" alt="Banner 3">
        </div>
        <div class="carousel-item">
          <img src="/photos/index/Banner4.jpg" class="d-block w-100 banner-img" alt="Banner 4">
        </div>
        <div class="carousel-item">
          <img src="/photos/index/Banner5.jpg" class="d-block w-100 banner-img" alt="Banner 5">
        </div>
        </div>

      <button class="carousel-control-prev" type="button" data-bs-target="#homeCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon"></span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#homeCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon"></span>
      </button>
    </div>

    <div class="container my-4">

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else>
        
        <section class="mb-5">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="fw-bold text-uppercase border-start border-4 border-primary ps-2">Laptop Dell</h4>
            <router-link to="/products?hang=2" class="text-decoration-none">Xem thêm &raquo;</router-link>
          </div>
          <div class="row g-3">
            <div class="col-md-3 col-6" v-for="p in dellList" :key="p.maSP">
              <ProductCard :product="p" />
            </div>
          </div>
        </section>

        <section class="mb-5">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="fw-bold text-uppercase border-start border-4 border-info ps-2">Laptop HP</h4>
            <router-link to="/products?hang=5" class="text-decoration-none">Xem thêm &raquo;</router-link>
          </div>
          <div class="row g-3">
            <div class="col-md-3 col-6" v-for="p in hpList" :key="p.maSP">
              <ProductCard :product="p" />
            </div>
          </div>
        </section>

        <div class="row mb-5">
            <div class="col-md-6">
                <img src="https://surfaceviet.vn/wp-content/uploads/2023/03/Banner-web-3.png" class="img-fluid w-100 rounded" alt="QC1">
            </div>
            <div class="col-md-6">
                <img src="https://surfaceviet.vn/wp-content/uploads/2023/03/Banner-web-3b-600x189.png" class="img-fluid w-100 rounded" alt="QC2">
            </div>
        </div>

        <section class="mb-5">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="fw-bold text-uppercase border-start border-4 border-danger ps-2">Laptop Lenovo</h4>
            <router-link to="/products?hang=6" class="text-decoration-none">Xem thêm &raquo;</router-link>
          </div>
          <div class="row g-3">
            <div class="col-md-3 col-6" v-for="p in lenovoList" :key="p.maSP">
              <ProductCard :product="p" />
            </div>
          </div>
        </section>

        <section class="mb-5">
           <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="fw-bold text-uppercase border-start border-4 border-warning ps-2">Phụ kiện Gaming</h4>
            <router-link to="/products?loai=2" class="text-decoration-none">Xem thêm &raquo;</router-link>
          </div>
          <div class="row g-3">
             <div class="col-md-3 col-6" v-for="p in accessoryList" :key="p.maSP">
              <ProductCard :product="p" />
            </div>
          </div>
        </section>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import ProductCard from './ProductCard.vue'; // Tách Card ra component con cho gọn

import { Carousel } from 'bootstrap';

const dellList = ref([]);
const hpList = ref([]);
const lenovoList = ref([]);
const accessoryList = ref([]);
const loading = ref(true);

const API_URL = 'http://localhost:8080/api/products';

// Hàm helper gọi API
const fetchByBrand = async (maLoai, maHang, limit = 4) => {
  try {
    const res = await axios.get(`${API_URL}/category/${maLoai}/brand/${maHang}/top?limit=${limit}`);
    return res.data;
  } catch (e) {
    console.error(e);
    return [];
  }
}

// Hàm lấy phụ kiện (Lấy theo loại, không cần hãng)
const fetchAccessories = async () => {
   try {
    // Gọi API lấy top sản phẩm theo loại 2 (Bàn phím) hoặc 3 (Chuột)
    // Ở đây tôi lấy loại 2 (Phím) làm ví dụ
    const res = await axios.get(`${API_URL}/category/2/top?limit=4`);
    return res.data;
  } catch (e) {
    return [];
  }
}

onMounted(async () => {
  // Gọi song song các API để tải nhanh hơn
  const [dell, hp, lenovo, acc] = await Promise.all([
     fetchByBrand(1, 2, 4), // Laptop (1) Dell (2)
     fetchByBrand(1, 5, 4), // Laptop (1) HP (5)
     fetchByBrand(1, 6, 4), // Laptop (1) Lenovo (6)
     fetchAccessories()
  ]);

  dellList.value = dell;
  hpList.value = hp;
  lenovoList.value = lenovo;
  accessoryList.value = acc;
  
  loading.value = false;

  setTimeout(() => {
    const carouselEl = document.getElementById('homeCarousel');
    if (carouselEl) {
        new Carousel(carouselEl, {
            interval: 3000,
            wrap: true
        });
    }
  }, 100);
});
</script>