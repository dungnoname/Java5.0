<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card shadow-sm p-4">
          <h4 class="text-center mb-4">Đăng ký tài khoản</h4>

          <div v-if="generalError" class="alert alert-danger">
            {{ generalError }}
          </div>
          <div v-if="successMessage" class="alert alert-success">
            {{ successMessage }}
          </div>

          <form @submit.prevent="handleRegister">
            
            <div class="mb-3">
              <label class="form-label">Họ và tên</label>
              <input 
                type="text" 
                v-model="registerData.fullname" 
                class="form-control" 
                placeholder="Nhập họ và tên"
                required
              >
            </div>

            <div class="mb-3">
              <label class="form-label">Email</label>
              <input 
                type="email" 
                v-model="registerData.email" 
                class="form-control" 
                placeholder="Nhập địa chỉ email"
                required
              >
            </div>

            <div class="mb-3">
              <label class="form-label">Tên đăng nhập</label>
              <input 
                type="text" 
                v-model="registerData.username" 
                class="form-control" 
                placeholder="Nhập tên đăng nhập"
                required
              >
            </div>

            <div class="mb-3">
              <label class="form-label">Mật khẩu</label>
              <input 
                type="password" 
                v-model="registerData.password" 
                class="form-control" 
                placeholder="Nhập mật khẩu"
                required
              >
            </div>

            <div class="mb-3">
              <label class="form-label">Nhập lại mật khẩu</label>
              <input 
                type="password" 
                v-model="registerData.confirmPassword" 
                class="form-control" 
                placeholder="Nhập lại mật khẩu"
                required
              >
              <div v-if="passwordMismatch" class="text-danger small mt-1">
                Mật khẩu xác nhận không khớp!
              </div>
            </div>

            <button type="submit" class="btn btn-success w-100" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-1"></span>
              {{ isLoading ? 'Đang xử lý...' : 'Đăng ký' }}
            </button>
          </form>

          <div class="text-center my-3">
            <span class="text-muted">hoặc</span>
          </div>

          <button class="btn btn-outline-danger w-100 mb-3">
            <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/google/google-original.svg"
                 alt="" width="20" class="me-2">
            Đăng ký bằng Google
          </button>

          <div class="text-center mt-3">
            <small>Đã có tài khoản?
              <router-link to="/login">Đăng nhập</router-link>
            </small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const isLoading = ref(false);
const generalError = ref('');
const successMessage = ref('');

// Dữ liệu form khớp hoàn toàn với DTO "RegisterRequest" trong Java Controller
const registerData = reactive({
  fullname: '',        // Bên Java là fullname
  email: '',           // Bên Java là email
  username: '',        // Bên Java là username
  password: '',        // Bên Java là password
  confirmPassword: ''  // Bên Java là confirmPassword
});

// Kiểm tra nhanh xem mật khẩu có khớp không
const passwordMismatch = computed(() => {
  return registerData.confirmPassword && registerData.password !== registerData.confirmPassword;
});

const handleRegister = async () => {
  // 1. Reset thông báo cũ
  generalError.value = '';
  successMessage.value = '';

  // 2. Validate Client cơ bản
  if (passwordMismatch.value) {
    generalError.value = "Vui lòng kiểm tra lại mật khẩu!";
    return;
  }

  isLoading.value = true;

  try {
    // 3. Gọi API Java Spring Boot
    // Đảm bảo Backend đang chạy ở cổng 8080
    const response = await axios.post('http://localhost:8080/api/auth/register', registerData);

    // 4. Thành công
    successMessage.value = response.data || "Đăng ký thành công!";
    
    // Reset Form
    Object.keys(registerData).forEach(key => registerData[key] = '');

    // Chuyển hướng sang trang đăng nhập sau 1.5 giây
    setTimeout(() => {
      router.push('/login');
    }, 1500);

  } catch (error) {
    // 5. Xử lý lỗi từ Backend trả về (ví dụ: Trùng username, email...)
    console.error(error);
    if (error.response && error.response.data) {
      // Backend trả về String lỗi trực tiếp (như bạn code trong Controller)
      generalError.value = error.response.data; 
    } else {
      generalError.value = "Có lỗi xảy ra khi kết nối server.";
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* CSS riêng cho trang đăng ký nếu cần */
</style>