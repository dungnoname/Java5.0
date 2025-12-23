<template>
  <div class="home-page bg-light">
    
    <div class="container-fluid p-0 mb-4">
      <div class="bg-white border-bottom d-flex align-items-center justify-content-center text-muted" style="height: 300px;">
          <h3>BANNER QUẢNG CÁO</h3>
      </div>
    </div>

    <div class="container pb-5">

      <div class="d-flex justify-content-end mb-4">
        <div class="d-flex align-items-center bg-white p-2 rounded shadow-sm">
          <span class="fw-bold me-2"><i class="bi bi-funnel"></i> Lọc giá:</span>
          <select class="form-select form-select-sm" style="width: 200px;" v-model="selectedPrice">
            <option value="all">Tất cả mức giá</option>
            <option value="duoi10">Dưới 10 triệu</option>
            <option value="10den20">Từ 10 - 20 triệu</option>
            <option value="tren20">Trên 20 triệu</option>
          </select>
        </div>
      </div>

      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else>
          <div class="section-product my-5" v-if="filterList(products.banPhim).length > 0">
              <h4 class="fw-bold text-uppercase border-start border-4 border-primary ps-3 mb-3">1. Bàn phím</h4>
              
              <div class="slider-container position-relative">
                  <button class="btn-scroll btn-prev" @click="scrollList('listBanPhim', -1)">
                      <i class="bi bi-chevron-left"></i>
                  </button>

                  <div class="product-list-scroll" ref="listBanPhim">
                      <div class="product-item" v-for="sp in filterList(products.banPhim)" :key="sp.maSP">
                          <div class="card h-100 shadow-sm border-0 product-card" @click="goToDetail(sp.maSP)">
                              <div class="img-wrapper p-3">
                                  <img :src="getImageUrl(sp.imgUrl)" class="mw-100 mh-100" :alt="sp.tenSP">
                              </div>
                              <div class="card-body">
                                  <h6 class="card-title text-truncate" :title="sp.tenSP">{{ sp.tenSP }}</h6>
                                  <p class="text-danger fw-bold fs-5 mb-0">{{ formatPrice(sp.donGiaBan) }}</p>
                              </div>
                          </div>
                      </div>
                  </div>

                  <button class="btn-scroll btn-next" @click="scrollList('listBanPhim', 1)">
                      <i class="bi bi-chevron-right"></i>
                  </button>
              </div>
          </div>

          <div class="section-product my-5" v-if="filterList(products.laptop).length > 0">
              <h4 class="fw-bold text-uppercase border-start border-4 border-danger ps-3 mb-3">2. Laptop</h4>
              
              <div class="slider-container position-relative">
                  <button class="btn-scroll btn-prev" @click="scrollList('listLaptop', -1)">
                      <i class="bi bi-chevron-left"></i>
                  </button>

                  <div class="product-list-scroll" ref="listLaptop">
                      <div class="product-item" v-for="sp in filterList(products.laptop)" :key="sp.maSP">
                          <div class="card h-100 shadow-sm border-0 product-card" @click="goToDetail(sp.maSP)">
                              <div class="img-wrapper p-3">
                                  <img :src="getImageUrl(sp.imgUrl)" class="mw-100 mh-100" :alt="sp.tenSP">
                              </div>
                              <div class="card-body">
                                  <h6 class="card-title text-truncate">{{ sp.tenSP }}</h6>
                                  <p class="text-danger fw-bold fs-5 mb-0">{{ formatPrice(sp.donGiaBan) }}</p>
                              </div>
                          </div>
                      </div>
                  </div>

                  <button class="btn-scroll btn-next" @click="scrollList('listLaptop', 1)">
                      <i class="bi bi-chevron-right"></i>
                  </button>
              </div>
          </div>
          
          <div class="section-product my-5" v-if="filterList(products.chuot).length > 0">
              <h4 class="fw-bold text-uppercase border-start border-4 border-success ps-3 mb-3">3. Chuột</h4>
              
              <div class="slider-container position-relative">
                  <button class="btn-scroll btn-prev" @click="scrollList('listChuot', -1)">
                      <i class="bi bi-chevron-left"></i>
                  </button>

                  <div class="product-list-scroll" ref="listChuot">
                      <div class="product-item" v-for="sp in filterList(products.chuot)" :key="sp.maSP">
                          <div class="card h-100 shadow-sm border-0 product-card" @click="goToDetail(sp.maSP)">
                              <div class="img-wrapper p-3">
                                  <img :src="getImageUrl(sp.imgUrl)" class="mw-100 mh-100" :alt="sp.tenSP">
                              </div>
                              <div class="card-body">
                                  <h6 class="card-title text-truncate">{{ sp.tenSP }}</h6>
                                  <p class="text-danger fw-bold fs-5 mb-0">{{ formatPrice(sp.donGiaBan) }}</p>
                              </div>
                          </div>
                      </div>
                  </div>

                  <button class="btn-scroll btn-next" @click="scrollList('listChuot', 1)">
                      <i class="bi bi-chevron-right"></i>
                  </button>
              </div>
          </div>
      </div>

    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Home',
  data() {
    return {
      products: { banPhim: [], laptop: [], chuot: [] },
      isLoading: true,
      selectedPrice: 'all'
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      axios.get('http://localhost:8080/api/home')
        .then(res => {
          this.products = res.data;
          this.isLoading = false;
        })
        .catch(err => {
          console.error(err);
          this.isLoading = false;
        });
    },

    // HÀM XỬ LÝ CLICK NÚT TRƯỢT
    scrollList(refName, direction) {
        // Tìm element danh sách dựa vào ref
        const container = this.$refs[refName];
        if (container) {
            // Tính khoảng cách trượt (chiều rộng 1 thẻ + gap) * số thẻ muốn trượt
            // Trượt khoảng 300px mỗi lần bấm
            const scrollAmount = 300; 
            container.scrollBy({ 
                left: scrollAmount * direction, // direction: 1 là phải, -1 là trái
                behavior: 'smooth' 
            });
        }
    },
    
    filterList(list) {
        if (!list) return [];
        if (this.selectedPrice === 'all') return list;
        return list.filter(sp => {
            const gia = sp.donGiaBan;
            if (this.selectedPrice === 'duoi10') return gia < 10000000;
            if (this.selectedPrice === '10den20') return gia >= 10000000 && gia <= 20000000;
            if (this.selectedPrice === 'tren20') return gia > 20000000;
            return true;
        });
    },

    goToDetail(productId) {
        this.$router.push({ name: 'ProductDetail', params: { id: productId } });
    },
    getImageUrl(imageName) {
      if (!imageName) return 'https://via.placeholder.com/300x300';
      if (imageName.startsWith('http')) return imageName;
      return `/images/${imageName}`;
    },
    formatPrice(value) {
      if (!value) return '0 ₫';
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    }
  }
};
</script>

<style scoped>
/* --- 1. CSS CHO DANH SÁCH TRƯỢT NGANG --- */
.product-list-scroll {
    display: flex;             /* Xếp ngang */
    overflow-x: auto;          /* Cho phép cuộn ngang */
    scroll-behavior: smooth;   /* Hiệu ứng trượt mượt */
    gap: 15px;                 /* Khoảng cách giữa các thẻ */
    padding: 10px 5px;         /* Khoảng hở trên dưới */
    
    /* Ẩn thanh cuộn đi để nhìn cho đẹp (nhưng vẫn cuộn được) */
    scrollbar-width: none;      /* Firefox */
    -ms-overflow-style: none;   /* IE/Edge */
}
.product-list-scroll::-webkit-scrollbar {
    display: none;              /* Chrome/Safari */
}

/* --- 2. CSS CHO TỪNG THẺ SẢN PHẨM --- */
.product-item {
    flex: 0 0 auto;            /* Không co giãn */
    width: 240px;              /* ĐỘ RỘNG CỐ ĐỊNH: Chỉnh số này để hiện 4 hay 5 cái */
                               /* Ví dụ: container 1200px / 5 cái = ~240px */
}

/* --- 3. CSS CHO NÚT BẤM TRƯỢT --- */
.btn-scroll {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: none;
    background-color: rgba(255, 255, 255, 0.9);
    box-shadow: 0 2px 5px rgba(0,0,0,0.2);
    z-index: 10;
    color: #333;
    font-size: 1.2rem;
    transition: all 0.3s;
    
    /* Căn giữa icon trong nút */
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-scroll:hover {
    background-color: #0d6efd; /* Màu xanh khi di chuột vào */
    color: white;
}

.btn-prev {
    left: -20px; /* Đẩy nút sang trái ra ngoài khung một chút */
}

.btn-next {
    right: -20px; /* Đẩy nút sang phải ra ngoài khung một chút */
}

/* --- 4. CSS CARD SẢN PHẨM (GIỮ NGUYÊN) --- */
.img-wrapper {
    height: 180px;
    display: flex;
    align-items: center;
    justify-content: center;
}
.product-card {
    cursor: pointer;
    transition: transform 0.2s;
}
.product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}
</style>