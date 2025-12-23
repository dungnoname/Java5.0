<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card shadow-sm p-4">
          <h4 class="text-center mb-4">Đăng nhập</h4>

          <div v-if="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="alert alert-success">
            {{ successMessage }}
          </div>

          <form @submit.prevent="handleLogin">
            <div class="mb-3">
              <label class="form-label">Tên đăng nhập</label>
              <input 
                type="text" 
                v-model="loginData.username" 
                class="form-control" 
                placeholder="Nhập tên đăng nhập" 
                required
              >
            </div>
            <div class="mb-3">
              <label class="form-label">Mật khẩu</label>
              <input 
                type="password" 
                v-model="loginData.password" 
                class="form-control" 
                placeholder="Nhập mật khẩu" 
                required
              >
            </div>

            <div class="text-end mb-3">
              <router-link to="/quen-mat-khau" class="text-decoration-none small">
                Quên mật khẩu?
              </router-link>
            </div>

            <button type="submit" class="btn btn-primary w-100" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-border spinner-border-sm me-1"></span>
              {{ isLoading ? 'Đang xử lý...' : 'Đăng nhập' }}
            </button>
          </form>

          <div class="text-center my-3">
            <span class="text-muted"></span>
          </div>

          <div class="text-center">
            <small>Chưa có tài khoản?
              <router-link to="/register">Đăng ký ngay</router-link>
            </small>
          </div>

            <div class="text-center mt-3">
                <p>Hoặc đăng nhập bằng</p>
                <GoogleLogin :callback="callbackGoogle" />
            </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { GoogleLogin } from 'vue3-google-login';

// Khởi tạo router để chuyển trang sau khi login
const router = useRouter();

// Biến trạng thái
const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

// Dữ liệu form khớp với class LoginRequest trong Java
const loginData = reactive({
  username: '',
  password: ''
});

const handleLogin = async () => {
  // Reset thông báo
  errorMessage.value = '';
  isLoading.value = true;

  try {
    // Gọi API Spring Boot
    // Lưu ý: Đảm bảo port 8080 đúng với server Java của bạn
    const response = await axios.post('http://localhost:8080/api/auth/login', loginData);

    // Xử lý khi thành công (Status 200)
    const { token, username, roles } = response.data;

    // 1. Lưu Token và thông tin User vào LocalStorage
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('user_info', username);
    localStorage.setItem('user_role', JSON.stringify(roles));

    // 2. Thông báo (hoặc chuyển trang ngay lập tức)
    window.dispatchEvent(new Event('auth-change'));
    successMessage.value = 'Đăng nhập thành công! Đang chuyển hướng...';

    // 3. Chuyển hướng sang trang chủ (Dashboard/Home) sau 1s
    setTimeout(() => {
      router.push('/'); 
    }, 1000);

  } catch (error) {
    // Xử lý lỗi
    console.error(error);
    if (error.response && error.response.status === 401 || error.response.status === 403) {
      errorMessage.value = 'Tên đăng nhập hoặc mật khẩu không chính xác.';
    } else {
      errorMessage.value = 'Lỗi kết nối đến máy chủ. Vui lòng thử lại sau.';
    }
  } finally {
    isLoading.value = false;
  }
};

const callbackGoogle = async (response) => {
  try {
    // 1. Lấy token từ Google (credential)
    const googleToken = response.credential;
    
    // 2. Gửi token này xuống Backend để đổi lấy JWT của hệ thống
    const res = await axios.post('http://localhost:8080/api/auth/google', {
      idToken: googleToken
    });

    // 3. Xử lý lưu JWT (Giống hệt login thường)
    const { token, username, roles } = res.data;
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('user_info', username);
    localStorage.setItem('roles', JSON.stringify(roles));

    // 4. Thông báo & Chuyển trang
    alert("Đăng nhập Google thành công!");
    window.location.href = '/';

  } catch (error) {
    console.error("Lỗi login Google:", error);
    alert("Đăng nhập Google thất bại.");
  }
};
</script>

<style scoped>
/* Thêm CSS riêng nếu cần, hiện tại đang dùng Bootstrap */
</style>