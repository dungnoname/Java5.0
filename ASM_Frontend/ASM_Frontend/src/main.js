import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router' // <-- Import router
import vue3GoogleLogin from 'vue3-google-login'

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

const app = createApp(App)

app.use(router)

app.use(vue3GoogleLogin, {
  clientId: '428471090484-5m40753a3e0n0cfeq38qaqmcnj5vq6ku.apps.googleusercontent.com'
})

app.mount('#app')