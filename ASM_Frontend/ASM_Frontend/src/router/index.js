import { createRouter, createWebHistory } from 'vue-router'

// --- 1. IMPORT LAYOUTS ---
import UserLayout from '../layouts/UserLayout.vue' // Layout cho khách
// Lưu ý: Kiểm tra lại đường dẫn AdminLayout của bạn, 
// nếu bạn để trong 'views/admin' thì sửa lại, còn đây là theo code bạn gửi:
import AdminLayout from '../components/admin/AdminLayout.vue' 

// --- 2. IMPORT COMPONENTS (USER) ---
import ProductList from '../components/ProductList.vue' 
import ProductDetail from '../components/ProductDetail.vue'
import Cart from '../components/Cart.vue'
import Login from '../components/Login.vue'
import Orders from '../components/Orders.vue'
import Register from '../components/Register.vue'
import ForgotPassword from '../components/ForgotPassword.vue'
import ResetPassword from '../components/ResetPassword.vue'
import Profile from '../components/Profile.vue'
import OrderDetail from '../components/OrderDetail.vue'

import OrderReview from '../components/OrderReview.vue'

import Home from '../components/Home.vue'

// --- 3. IMPORT COMPONENTS (ADMIN) ---
import AdminDashboard from '../components/admin/Dashboard.vue'
import AdminOrders from '../components/admin/AdminOrders.vue'

//Hung
import AdminBrands from '../components/admin/Brands.vue'
import AdminCategories from '../components/admin/Categories.vue'
import AdminProducts from '../components/admin/Products.vue'
import AdminStatistics from '../components/admin/Statistics.vue'
import AdminUsers from '../components/admin/Users.vue'



const routes = [
  // ====================================================
  //  NHÓM 1: GIAO DIỆN KHÁCH HÀNG (UserLayout)
  // ====================================================
  {
    path: '/',
    component: UserLayout, // Áp dụng Header/Footer cho tất cả các trang con bên dưới
    children: [
      { 
        path: '', // Đường dẫn gốc: /
        name: 'Home', 
        component: Home 
      },
      {
        path: 'products', // Đường dẫn: /products
        name: 'ProductList',
        component: ProductList
      },
      {
        path: 'product/:id', 
        name: 'ProductDetail',
        component: ProductDetail
      },
      {
        path: 'cart',
        name: 'Cart',
        component: Cart,
        meta: { requiresAuth: true } // Cần đăng nhập
      },
      {
        path: 'orders',
        name: 'Orders',
        component: Orders,
        meta: { requiresAuth: true } // Cần đăng nhập
      },

      {
        path: 'order/:id',
        name: 'UserOrderDetail',
        // Trỏ đến file OrderDetail dành cho User mà bạn đã tạo từ Thymeleaf
        component: OrderDetail, 
        meta: { requiresAuth: true }
      },

      {
        path: 'login',
        name: 'Login',
        component: Login
      },
      { 
        path: 'register', 
        name: 'Register', 
        component: Register 
      },
      {
        path: 'quen-mat-khau',
        name: 'ForgotPassword',
        component: ForgotPassword
      },
      {
        path: 'dat-lai-mat-khau', 
        name: 'ResetPassword',
        component: ResetPassword
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { requiresAuth: true }
      },
      {
        path: 'order/review/:id', // URL: /order/review/5
        name: 'OrderReview',
        component: OrderReview,
        meta: { requiresAuth: true }
      },
    ]
  },

  // ====================================================
  //  NHÓM 2: GIAO DIỆN QUẢN TRỊ (AdminLayout)
  // ====================================================
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true }, // Bảo vệ nghiêm ngặt
    children: [
      {
        path: '', // Đường dẫn: /admin
        name: 'AdminDashboard',
        component: AdminDashboard
      },

      {
        path: 'orders', // URL sẽ là /admin/orders
        name: 'AdminOrders',
        component: AdminOrders
      },

      // {
      //   path: 'order/:id',
      //   name: 'OrderDetail',
      //   component: OrderDetail,
      //   meta: { requiresAuth: true }
      // },

      // Sau này thêm các trang quản lý user, product vào đây...
      {
        path: 'brands', // Đường dẫn sẽ là /admin/brands
        name: 'AdminBrands',
        component: AdminBrands
      },
      {
        path: 'categories', // Đường dẫn: /admin/categories
        name: 'AdminCategories',
        component: AdminCategories
      },
      {
        path: 'products', // Đường dẫn: /admin/products
        name: 'AdminProducts',
        component: AdminProducts
      },
      {
        path: 'stats', // Đường dẫn: /admin/statistics
        name: 'AdminStatistics',
        component: AdminStatistics
      },
      {
        path: 'users', // Đường dẫn: /admin/users
        name: 'AdminUsers',
        component: AdminUsers
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ====================================================
//  VỆ SĨ BẢO VỆ ROUTER (GỘP CHUNG 1 HÀM)
// ====================================================
router.beforeEach((to, from, next) => {
  // 1. Lấy thông tin từ LocalStorage (Dùng đúng tên khóa 'jwt_token')
  const token = localStorage.getItem('jwt_token');
  const userRoles = localStorage.getItem('user_role');
  const roles = userRoles ? JSON.parse(userRoles) : [];

  // 2. Kiểm tra Login (cho các trang có meta: requiresAuth)
  if (to.meta.requiresAdmin) {
    //Cho phép cả ADMIN và STAFF
    const hasAccess = roles.includes('ROLE_ADMIN') || roles.includes('ROLE_STAFF');
    
    if (!hasAccess) {
      alert("Bạn không có quyền truy cập trang quản trị!");
      return next('/'); // Đá về trang chủ
    }
  }

  // 3. (Optional) Nếu Staff cố vào trang Thống kê (/admin/stats) -> Chặn
  if (to.path === '/admin/stats' && !roles.includes('ROLE_ADMIN')) {
      alert("Chỉ Admin mới được xem thống kê!");
      return next('/admin'); // Đá về Dashboard chung
  }

  // 4. Nếu hợp lệ thì cho đi tiếp
  next();
});

export default router;