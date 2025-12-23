<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card shadow-sm p-4 border-0">
          <div class="card-body">
            <h4 class="card-title text-center mb-4">Đặt lại mật khẩu</h4>

            <div v-if="errorMessage" class="alert alert-danger">
              {{ errorMessage }}
            </div>

            <div v-if="successMessage" class="alert alert-success">
              {{ successMessage }}
            </div>

            <form v-if="!successMessage" @submit.prevent="handleReset">
              
              <div class="mb-3">
                <label class="form-label">Mật khẩu mới</label>
                <input 
                  type="password" 
                  v-model="password" 
                  class="form-control" 
                  required 
                  placeholder="Nhập mật khẩu mới"
                >
              </div>

              <div class="mb-3">
                <label class="form-label">Xác nhận mật khẩu</label>
                <input 
                  type="password" 
                  v-model="confirmPassword" 
                  class="form-control" 
                  required 
                  placeholder="Nhập lại mật khẩu mới"
                >
              </div>

              <button type="submit" class="btn btn-success w-100 mt-2" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-1"></span>
                {{ isLoading ? 'Đang xử lý...' : 'Đổi Mật Khẩu' }}
              </button>
            </form>

            <div class="text-center mt-3">
              <router-link to="/login" class="text-decoration-none small">
                Quay lại đăng nhập
              </router-link>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();   // Dùng để lấy tham số trên URL
const router = useRouter(); // Dùng để chuyển trang

const password = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

// Lấy token từ URL (ví dụ: .../dat-lai-mat-khau?token=abc-xyz...)
const token = route.query.token;

// Kiểm tra ngay khi vào trang: Nếu không có token thì báo lỗi luôn
onMounted(() => {
  if (!token) {
    errorMessage.value = "Đường dẫn không hợp lệ! Vui lòng kiểm tra lại email.";
  }
});

const handleReset = async () => {
  // 1. Kiểm tra mật khẩu khớp nhau
  if (password.value !== confirmPassword.value) {
    errorMessage.value = "Mật khẩu xác nhận không khớp!";
    return;
  }

  // 2. Kiểm tra token lần nữa
  if (!token) {
    errorMessage.value = "Thiếu mã xác thực (Token). Vui lòng click lại link trong email.";
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  try {
    // 3. Gọi API Backend
    // Gửi đúng object khớp với ResetPasswordRequest trong Java
    await axios.post('http://localhost:8080/api/auth/reset-password', {
      token: token,
      newPassword: password.value
    });

    // 4. Thành công
    successMessage.value = "Đổi mật khẩu thành công! Đang chuyển hướng về trang đăng nhập...";
    
    // Tự động chuyển về trang Login sau 2 giây
    setTimeout(() => {
      router.push('/login');
    }, 2000);

  } catch (error) {
    console.error(error);
    if (error.response && error.response.data) {
      errorMessage.value = error.response.data; // Lỗi server trả về (Token hết hạn, v.v...)
    } else {
      errorMessage.value = "Lỗi kết nối server. Vui lòng thử lại.";
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* CSS riêng nếu cần */
</style>