<script setup>
</script>

<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup>
// Không cần import Header/Footer ở đây nữa
</script>

<style>
/* Định dạng để Footer luôn nằm dưới cùng */
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* Chiều cao bằng màn hình */
}

.main-content {
  flex: 1; /* Chiếm hết khoảng trống còn lại */
  padding-bottom: 20px;
}
</style>
