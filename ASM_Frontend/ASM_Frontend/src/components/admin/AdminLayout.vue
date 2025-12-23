<template>
  <div class="admin-layout">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container-fluid">
        <router-link class="navbar-brand" to="/admin">Hệ thống CRUD</router-link>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNav">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="adminNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/users">Người dùng</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/products">Sản phẩm</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/categories">Loại hàng</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/orders">Đơn hàng</router-link>
            </li>
            
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/stats">Thống kê</router-link>
            </li>

            <li class="nav-item ms-3">
              <a class="nav-link text-warning" href="#" @click.prevent="handleLogout">
                Đăng xuất
              </a>
            </li>
            
            <li class="nav-item ms-3">
               <router-link class="nav-link text-info" to="/">
                 Về trang bán hàng
               </router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <div class="container my-4">
       <router-view></router-view> 
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const handleLogout = () => {
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user_info');
  localStorage.removeItem('user_role');
  window.location.href = '/login';
};
</script>