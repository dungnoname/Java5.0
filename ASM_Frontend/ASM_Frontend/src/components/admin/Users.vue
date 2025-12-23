<template>
  <div class="container my-4">
    <h3 class="mb-4 text-center">Quản lý Người Dùng</h3>

    <div v-if="alert.message" :class="['alert alert-dismissible fade show', alert.type === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      <span>{{ alert.message }}</span>
      <button type="button" class="btn-close" @click="closeAlert"></button>
    </div>

    <div class="d-flex justify-content-end mb-3">
      <button class="btn btn-primary" @click="openModal('add')">
        + Thêm người dùng
      </button>
    </div>

    <div class="table-responsive">
      <table class="table table-bordered table-striped align-middle text-center">
        <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Họ Tên</th>
            <th>Ngày Sinh</th>
            <th>Giới Tính</th>
            <th>SĐT</th>
            <th>Email</th>
            <th>Tên Đăng Nhập</th>
            <th>Vai Trò</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in listUsers" :key="user.userId">
            <td>{{ user.userId }}</td>
            <td class="text-start">{{ user.hoTen }}</td>
            <td>{{ formatDate(user.ngaySinh) }}</td>
            <td>{{ user.gioiTinh ? 'Nam' : 'Nữ' }}</td>
            <td>{{ user.soDienThoai }}</td>
            <td class="text-start">{{ user.email }}</td>
            <td>{{ user.tenDangNhap }}</td>
            <td>
              <span :class="getRoleBadgeClass(user.role?.roleName)">
                {{ user.role ? user.role.roleName : 'Chưa gán' }}
              </span>
            </td>
            <td>
              <div class="d-flex justify-content-center gap-2">
                <button class="btn btn-warning btn-sm" @click="openModal('edit', user)">Sửa</button>
                <button v-if="user.userId !== currentUserId" class="btn btn-danger btn-sm" @click="confirmDelete(user)">Xóa</button>
              </div>
            </td>
          </tr>
          <tr v-if="listUsers.length === 0">
            <td colspan="9">Không có dữ liệu người dùng.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <nav v-if="totalPages > 0" aria-label="Page navigation" class="mt-3">
      <ul class="pagination justify-content-center">
        <li :class="['page-item', { disabled: currentPage === 0 }]">
          <button class="page-link" @click="changePage(currentPage - 1)">Previous</button>
        </li>
        <li v-for="page in totalPages" :key="page" :class="['page-item', { active: currentPage === page - 1 }]">
          <button class="page-link" @click="changePage(page - 1)">{{ page }}</button>
        </li>
        <li :class="['page-item', { disabled: currentPage === totalPages - 1 }]">
          <button class="page-link" @click="changePage(currentPage + 1)">Next</button>
        </li>
      </ul>
    </nav>

    <div class="modal fade" id="userModal" tabindex="-1" aria-hidden="true" ref="modalElement">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <form @submit.prevent="saveUser">
            <div class="modal-header bg-primary text-white">
              <h5 class="modal-title">{{ isEditMode ? 'Cập nhật Người Dùng' : 'Thêm Người Dùng Mới' }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Họ Tên <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" v-model="form.hoTen" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Ngày Sinh</label>
                  <input type="date" class="form-control" v-model="form.ngaySinh">
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Giới Tính</label>
                  <select class="form-select" v-model="form.gioiTinh">
                    <option :value="true">Nam</option>
                    <option :value="false">Nữ</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Số Điện Thoại</label>
                  <input type="text" class="form-control" v-model="form.soDienThoai">
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Email <span class="text-danger">*</span></label>
                  <input type="email" class="form-control" v-model="form.email" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Địa Chỉ</label>
                  <input type="text" class="form-control" v-model="form.diaChi">
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label class="form-label">Tên Đăng Nhập <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" v-model="form.tenDangNhap" required :disabled="isEditMode">
                  <small v-if="isEditMode" class="text-muted">Không thể sửa tên đăng nhập.</small>
                </div>
                <div class="col-md-6">
                  <label class="form-label">Mật Khẩu <span v-if="!isEditMode" class="text-danger">*</span></label>
                  <input type="password" class="form-control" v-model="form.matKhau" 
                         :placeholder="isEditMode ? 'Để trống nếu không đổi' : 'Nhập mật khẩu'" 
                         :required="!isEditMode">
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">Vai Trò <span class="text-danger">*</span></label>
                <select class="form-select" v-model="form.role.roleId" required>
                  <option :value="null" disabled>-- Chọn vai trò --</option>
                  <option v-for="r in listRoles" :key="r.roleId" :value="r.roleId">
                    {{ r.roleName }}
                  </option>
                </select>
              </div>

            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
              <button type="submit" class="btn btn-primary">Lưu thông tin</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true" ref="deleteModalElement">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Cảnh báo xóa</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            Bạn có chắc chắn muốn xóa người dùng <strong>{{ form.hoTen }}</strong> không?<br>
            <small class="text-muted">Hành động này không thể hoàn tác.</small>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-danger" @click="deleteUser">Xóa ngay</button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { jwtDecode } from "jwt-decode"; // Nếu chưa cài thì chạy: npm install jwt-decode
import { Modal } from 'bootstrap';

// --- CẤU HÌNH ---
const BASE_URL = 'http://localhost:8080/api/admin/users';

// --- STATE ---
const listUsers = ref([]);
const listRoles = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = 5; // Số dòng mỗi trang

let userModalObj = null;
let deleteModalObj = null;

const isEditMode = ref(false);
const form = ref({
  userId: null,
  hoTen: '',
  ngaySinh: '',
  gioiTinh: true,
  soDienThoai: '',
  email: '',
  diaChi: '',
  tenDangNhap: '',
  matKhau: '',
  role: { roleId: null }
});
const alert = ref({ message: '', type: '' });

let modalInstance = null;
let deleteModalInstance = null;

// Lấy ID người dùng hiện tại từ Token để chặn xóa chính mình
const currentUserId = computed(() => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    try {
      // Vì không cài thư viện ngoài được ngay, ta dùng hàm parse thủ công hoặc thư viện có sẵn
      // Ở đây giả định backend trả về userId trong payload token
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      const decoded = JSON.parse(jsonPayload);
      return decoded.userId || decoded.sub; // Tùy cấu trúc token của bạn
    } catch (e) {
      return null;
    }
  }
  return null;
});


// --- AUTH TOKEN ---
const getAuthHeader = () => {
  const token = localStorage.getItem('jwt_token');
  return { headers: { Authorization: `Bearer ${token}` } };
};

// --- LIFECYCLE ---
onMounted(() => {
  loadData(0);
  loadRoles();

  // Init Bootstrap Modal
  setTimeout(() => {
    const modalEl = document.getElementById('userModal');
    const deleteEl = document.getElementById('deleteModal');
    
    if (modalEl) userModalObj = new Modal(modalEl);
    if (deleteEl) deleteModalObj = new Modal(deleteEl);
  }, 100);
});

// --- METHODS ---

// 1. Tải danh sách User (Có phân trang)
const loadData = async (page) => {
  try {
    const config = getAuthHeader();
    // Gọi API với tham số page và size
    const response = await axios.get(`${BASE_URL}?page=${page}&size=${pageSize}`, config);
    
    listUsers.value = response.data.content; // Spring Data trả về .content
    totalPages.value = response.data.totalPages;
    currentPage.value = response.data.number; // Trang hiện tại

  } catch (error) {
    console.error(error);
    if(error.response && error.response.status === 403) {
       showAlert('Bạn không có quyền quản lý người dùng!', 'error');
    } else {
       showAlert('Lỗi tải danh sách người dùng!', 'error');
    }
  }
};

// 2. Tải danh sách Roles
const loadRoles = async () => {
  try {
    const config = getAuthHeader();
    const response = await axios.get(`${BASE_URL}/roles`, config);
    listRoles.value = response.data;
  } catch (error) {
    console.error("Lỗi tải roles:", error);
  }
};

// 3. Chuyển trang
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    loadData(page);
  }
};

// 4. Mở Modal Thêm/Sửa
const openModal = (mode, item = null) => {
  isEditMode.value = mode === 'edit';
  
  if (mode === 'edit' && item) {
    // Clone dữ liệu để tránh sửa trực tiếp trên bảng khi chưa lưu
    form.value = JSON.parse(JSON.stringify(item));
    form.value.matKhau = ''; // Xóa mật khẩu cũ để tránh lộ hash, chỉ nhập khi muốn đổi
    
    // Fix lỗi nếu role bị null
    if (!form.value.role) {
        form.value.role = { roleId: null };
    }
  } else {
    // Reset form về trạng thái rỗng để thêm mới
    form.value = {
      userId: null,
      hoTen: '',
      ngaySinh: '',
      gioiTinh: true,
      soDienThoai: '',
      email: '',
      diaChi: '',
      tenDangNhap: '',
      matKhau: '',
      role: { roleId: null }
    };
  }
  
  // Gọi show() từ biến đã khởi tạo
  if (userModalObj) {
    userModalObj.show();
  } else {
    console.error("Modal chưa được khởi tạo!");
  }
};

// Sửa hàm saveUser (đóng modal)
// 5. Lưu User
const saveUser = async () => {
  try {
    const config = getAuthHeader();
    
    // Validate cơ bản (nếu cần)
    if (!form.value.hoTen || !form.value.tenDangNhap) {
        showAlert('Vui lòng điền đủ thông tin!', 'error');
        return;
    }

    if (isEditMode.value) {
      // Cập nhật (PUT)
      await axios.put(`${BASE_URL}/${form.value.userId}`, form.value, config);
      showAlert('Cập nhật thành công!', 'success');
    } else {
      // Thêm mới (POST)
      await axios.post(BASE_URL, form.value, config);
      showAlert('Thêm mới thành công!', 'success');
    }
    
    loadData(currentPage.value); // Tải lại trang hiện tại để thấy dữ liệu mới
    
    // Đóng modal sau khi thành công
    if (userModalObj) userModalObj.hide();
    
  } catch (error) {
    console.error(error);
    const msg = error.response?.data || error.message;
    showAlert('Lỗi: ' + msg, 'error');
  }
};

// Sửa hàm confirmDelete
const confirmDelete = (item) => {
  form.value = { ...item };
  if (deleteModalObj) deleteModalObj.show();
};

// Sửa hàm deleteUser (đóng modal)
const deleteUser = async () => {
  try {
    const config = getAuthHeader();
    await axios.delete(`${BASE_URL}/${form.value.userId}`, config);
    showAlert('Xóa thành công!', 'success');
    loadData(0); // Về trang đầu sau khi xóa
  } catch (error) {
    const msg = error.response?.data || error.message;
    showAlert('Không thể xóa: ' + msg, 'error');
  } finally {
    // Luôn đóng modal dù thành công hay thất bại
    if (deleteModalObj) deleteModalObj.hide();
  }
};

// --- UTILS ---
const showAlert = (msg, type) => {
  alert.value = { message: msg, type: type };
  setTimeout(() => { alert.value.message = ''; }, 3000);
};

const closeAlert = () => { alert.value.message = ''; };

const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('vi-VN');
};

const getRoleBadgeClass = (roleName) => {
  if (roleName === 'Admin' || roleName === 'ROLE_ADMIN') return 'badge bg-danger'; // Đỏ
  if (roleName === 'Staff' || roleName === 'ROLE_STAFF') return 'badge bg-warning text-dark'; // Vàng
  return 'badge bg-secondary'; // Xám (User)
};

</script>

<style scoped>
</style>