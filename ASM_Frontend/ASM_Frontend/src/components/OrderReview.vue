<template>
  <div class="container my-5">
    <div class="card shadow-sm">
      <div class="card-header bg-white">
        <h4 class="mb-0 fw-bold text-primary">
          <i class="bi bi-star-fill text-warning"></i> Đánh giá đơn hàng #{{ orderId }}
        </h4>
      </div>
      
      <div class="card-body">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary"></div>
          <p class="mt-2">Đang tải sản phẩm...</p>
        </div>

        <form v-else @submit.prevent="submitReview">
          
          <div v-for="(item, index) in reviewItems" :key="item.sanPham.maSP" class="card mb-3 border-0 shadow-sm bg-light">
            <div class="card-body">
              <div class="row align-items-center">
                
                <div class="col-md-2 col-3 text-center">
                  <img :src="item.sanPham.imgUrl || '/placeholder.jpg'" 
                       class="img-fluid rounded border bg-white" 
                       style="max-height: 100px; object-fit: contain;"
                       alt="Sản phẩm">
                </div>

                <div class="col-md-10 col-9">
                  <h6 class="fw-bold text-dark">{{ item.sanPham.tenSP }}</h6>

                  <div class="row g-3">
                    <div class="col-md-4">
                      <label class="form-label small text-muted">Mức độ hài lòng:</label>
                      <select class="form-select text-warning fw-bold" v-model="item.rating" required>
                        <option value="5">5 ★★★★★ (Tuyệt vời)</option>
                        <option value="4">4 ★★★★ (Hài lòng)</option>
                        <option value="3">3 ★★★ (Bình thường)</option>
                        <option value="2">2 ★★ (Tệ)</option>
                        <option value="1">1 ★ (Rất tệ)</option>
                      </select>
                    </div>

                    <div class="col-md-8">
                      <label class="form-label small text-muted">Bình luận:</label>
                      <textarea class="form-control" 
                                v-model="item.comment" 
                                rows="2"
                                placeholder="Bạn cảm thấy sản phẩm thế nào?"></textarea>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>

          <div class="d-flex justify-content-end gap-2 mt-4">
            <router-link to="/orders" class="btn btn-secondary">
              Quay lại
            </router-link>
            <button type="submit" class="btn btn-success px-4 fw-bold">
              <i class="bi bi-send"></i> Gửi đánh giá
            </button>
          </div>

        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const orderId = route.params.id; // Lấy ID từ URL
const loading = ref(true);
const reviewItems = ref([]); // Danh sách sản phẩm kèm dữ liệu đánh giá

// API Helper
const getHeader = () => {
  const token = localStorage.getItem('jwt_token');
  return { headers: { Authorization: `Bearer ${token}` } };
};

// 1. Lấy danh sách sản phẩm cần đánh giá
const fetchReviewForm = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/reviews/form/${orderId}`, getHeader());
    
    // Map dữ liệu từ API sang cấu trúc frontend cần dùng (thêm rating và comment mặc định)
    reviewItems.value = res.data.map(cthd => ({
      ...cthd,       // Giữ lại thông tin sản phẩm (tên, ảnh...)
      rating: 5,     // Mặc định 5 sao
      comment: ''    // Comment rỗng
    }));

  } catch (error) {
    console.error("Lỗi tải form:", error);
    alert("Không thể đánh giá đơn hàng này (Có thể do chưa hoàn thành hoặc không tồn tại).");
    router.push('/orders'); // Đá về trang danh sách
  } finally {
    loading.value = false;
  }
};

// 2. Gửi đánh giá (Submit)
const submitReview = async () => {
  if (!confirm("Bạn có chắc muốn gửi những đánh giá này không?")) return;

  try {
    // Chuẩn bị dữ liệu theo đúng DTO Backend yêu cầu (ReviewRequestDTO)
    const payload = reviewItems.value.map(item => ({
      productId: item.sanPham.maSP,
      rating: Number(item.rating), // Đảm bảo là số
      comment: item.comment
    }));

    // Gọi API POST
    await axios.post(`http://localhost:8080/api/reviews/submit/${orderId}`, payload, getHeader());

    alert("🎉 Cảm ơn bạn đã đánh giá!");
    router.push('/orders'); // Quay về danh sách đơn hàng

  } catch (error) {
    console.error(error);
    alert("Gửi đánh giá thất bại!");
  }
};

onMounted(() => {
  fetchReviewForm();
});
</script>

<style scoped>
/* Style tùy chỉnh cho select box sao */
.form-select option {
  color: #333;
}
</style>