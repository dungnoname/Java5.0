<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <h3 class="mb-4">👤 Quản lý tài khoản</h3>

        <!-- Thông báo chung -->
        <div v-if="successMessage" class="alert alert-success alert-dismissible fade show">
          {{ successMessage }}
          <button type="button" class="btn-close" @click="successMessage = ''"></button>
        </div>
        <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show">
          {{ errorMessage }}
          <button type="button" class="btn-close" @click="errorMessage = ''"></button>
        </div>

        <!-- Tabs chuyển đổi -->
        <ul class="nav nav-tabs mb-3">
          <li class="nav-item">
            <button 
              class="nav-link" 
              :class="{ active: activeTab === 'info' }" 
              @click="activeTab = 'info'"
            >
              Cập nhật thông tin
            </button>
          </li>
          <li class="nav-item">
            <button 
              class="nav-link" 
              :class="{ active: activeTab === 'password' }" 
              @click="activeTab = 'password'"
            >
              Đổi mật khẩu
            </button>
          </li>
        </ul>

        <div class="tab-content">
          
          <!-- TAB 1: THÔNG TIN CÁ NHÂN -->
          <div v-if="activeTab === 'info'" class="tab-pane fade show active">
            <div class="card shadow-sm">
              <div class="card-body">
                <h5 class="card-title mb-3">Thông tin cá nhân</h5>
                <form @submit.prevent="updateProfile">
                  
                  <div class="row g-3">
                    <!-- Tên đăng nhập (Readonly) -->
                    <div class="col-12">
                      <label class="form-label">Tên đăng nhập</label>
                      <input type="text" class="form-control bg-light" :value="user.tenDangNhap" readonly disabled>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Họ tên</label>
                      <input type="text" class="form-control" v-model="user.hoTen" required>
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">Email</label>
                      <input type="email" class="form-control" v-model="user.email" required>
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">Số điện thoại</label>
                      <input type="text" class="form-control" v-model="user.soDienThoai">
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">Địa chỉ</label>
                      <input type="text" class="form-control" v-model="user.diaChi">
                    </div>
                  </div>

                  <div class="mt-4 text-end">
                    <button type="submit" class="btn btn-primary" :disabled="isLoading">
                      <span v-if="isLoading" class="spinner-border spinner-border-sm me-1"></span>
                      Lưu thay đổi
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>

          <!-- TAB 2: ĐỔI MẬT KHẨU -->
          <div v-if="activeTab === 'password'" class="tab-pane fade show active">
            <div class="card shadow-sm">
              <div class="card-body">
                <h5 class="card-title mb-3">Đổi mật khẩu</h5>
                <form @submit.prevent="changePassword">
                  
                  <div class="mb-3">
                    <label class="form-label">Mật khẩu hiện tại</label>
                    <input type="password" class="form-control" v-model="passwordData.oldPassword" placeholder="Nhập mật khẩu hiện tại" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Mật khẩu mới</label>
                    <input type="password" class="form-control" v-model="passwordData.newPassword" placeholder="Nhập mật khẩu mới" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label">Nhập lại mật khẩu mới</label>
                    <input type="password" class="form-control" v-model="passwordData.confirmPassword" placeholder="Nhập lại mật khẩu mới" required>
                  </div>

                  <div class="text-end">
                    <button type="submit" class="btn btn-success" :disabled="isLoading">
                      <span v-if="isLoading" class="spinner-border spinner-border-sm me-1"></span>
                      Đổi mật khẩu
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const activeTab = ref('info'); // Tab mặc định
const isLoading = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

// Dữ liệu Profile
const user = reactive({
  tenDangNhap: '',
  hoTen: '',
  email: '',
  soDienThoai: '',
  diaChi: ''
});

// Dữ liệu Đổi mật khẩu
const passwordData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// Hàm lấy Token từ LocalStorage và cấu hình Header
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token');
  if (!token) return {};
  return { Authorization: `Bearer ${token}` };
};

// 1. Lấy thông tin User khi vào trang
const fetchProfile = async () => {
  try {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      router.push('/login');
      return;
    }

    const response = await axios.get('http://localhost:8080/api/profile', {
      headers: getAuthHeader()
    });

    // Gán dữ liệu vào biến user
    Object.assign(user, response.data);

  } catch (error) {
    console.error(error);
    if (error.response && error.response.status === 401) {
      router.push('/login'); // Hết hạn token thì đá về login
    } else {
      errorMessage.value = "Không thể tải thông tin tài khoản.";
    }
  }
};

// 2. Cập nhật thông tin
const updateProfile = async () => {
  isLoading.value = true;
  successMessage.value = '';
  errorMessage.value = '';

  try {
    // Gửi JSON đúng theo UpdateProfileRequest trong Java
    await axios.put('http://localhost:8080/api/profile/update', {
      hoTen: user.hoTen,
      email: user.email,
      soDienThoai: user.soDienThoai,
      diaChi: user.diaChi
    }, {
      headers: getAuthHeader() // Bắt buộc phải có Header Token
    });

    successMessage.value = "Cập nhật thông tin thành công!";
  } catch (error) {
    console.error(error);
    errorMessage.value = "Lỗi cập nhật: " + (error.response?.data || "Vui lòng thử lại.");
  } finally {
    isLoading.value = false;
  }
};

// 3. Đổi mật khẩu
const changePassword = async () => {
  // Validate phía Client trước
  if (passwordData.newPassword !== passwordData.confirmPassword) {
    errorMessage.value = "Mật khẩu xác nhận không khớp!";
    return;
  }

  isLoading.value = true;
  successMessage.value = '';
  errorMessage.value = '';

  try {
    // Gửi JSON đúng theo ChangePasswordRequest trong Java
    const response = await axios.put('http://localhost:8080/api/profile/change-password', {
      oldPassword: passwordData.oldPassword,
      newPassword: passwordData.newPassword,
      confirmPassword: passwordData.confirmPassword
    }, {
      headers: getAuthHeader()
    });

    successMessage.value = response.data.message || "Đổi mật khẩu thành công!";
    
    // Reset form mật khẩu
    passwordData.oldPassword = '';
    passwordData.newPassword = '';
    passwordData.confirmPassword = '';

  } catch (error) {
    console.error(error);
    // Hiển thị lỗi từ Controller (VD: "Mật khẩu cũ không đúng!")
    errorMessage.value = error.response?.data || "Lỗi đổi mật khẩu.";
  } finally {
    isLoading.value = false;
  }
};

// Chạy khi component được load
onMounted(() => {
  fetchProfile();
});
</script>