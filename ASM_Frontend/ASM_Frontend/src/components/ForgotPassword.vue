<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow-sm p-4 border-0">
          <div class="card-body">
            <h4 class="card-title text-center mb-4">Quên mật khẩu</h4>
            <p class="text-center text-muted small">
              Vui lòng nhập <b>email đã đăng ký</b>. Hệ thống sẽ gửi liên kết đặt lại mật khẩu cho bạn.
            </p>

            <div v-if="successMessage" class="alert alert-success">
              {{ successMessage }}
            </div>

            <div v-if="errorMessage" class="alert alert-danger">
              {{ errorMessage }}
            </div>

            <form @submit.prevent="handleSubmit" class="mt-4">
              
              <div class="mb-3">
                <label for="email" class="form-label">Địa chỉ Email</label>
                <input 
                  type="email" 
                  id="email" 
                  v-model="formData.email" 
                  class="form-control" 
                  required
                  autofocus
                  placeholder="Nhập email của bạn"
                >
              </div>

              <button type="submit" class="btn btn-primary w-100 mt-3" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-1"></span>
                {{ isLoading ? 'Đang gửi...' : 'Gửi Link Đặt Lại' }}
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
import { ref, reactive } from 'vue';
import axios from 'axios';

const isLoading = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

// Dữ liệu form gửi đi: CHỈ CẦN EMAIL
const formData = reactive({
  email: ''
});

const handleSubmit = async () => {
  // 1. Reset thông báo cũ
  isLoading.value = true;
  successMessage.value = '';
  errorMessage.value = '';

  try {
    // 2. Gọi API Backend
    // Backend sẽ nhận JSON: { "email": "..." }
    const response = await axios.post('http://localhost:8080/api/auth/forgot-password', formData);

    // 3. Xử lý thành công
    successMessage.value = response.data || 'Link đặt lại mật khẩu đã được gửi vào email của bạn!';
    
    // Xóa trắng form
    formData.email = '';

  } catch (error) {
    // 4. Xử lý lỗi
    console.error(error);
    if (error.response && error.response.data) {
      errorMessage.value = error.response.data; // Lỗi từ server (VD: Email không tồn tại)
    } else {
      errorMessage.value = 'Lỗi hệ thống. Vui lòng thử lại sau.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>