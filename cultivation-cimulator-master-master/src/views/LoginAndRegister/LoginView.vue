<template>
  <div class="login-container">
    <!-- 视频背景 -->
    <video class="background-video" autoplay muted loop playsinline>
      <source src="@/assets/login-video.mp4" type="video/mp4">
      <!-- 视频加载失败时的备用背景 -->
      <div class="fallback-background"></div>
    </video>
    
    <div class="login-box">
      <h2 class="title">修仙模拟器</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">账号(手机号)</label>
          <input 
            type="text" 
            id="username" 
            v-model="form.username" 
            placeholder="请输入手机号"
            :class="{ 'is-invalid': errors.username }"
          />
          <div class="invalid-feedback" v-if="errors.username">{{ errors.username }}</div>
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="form.password" 
            placeholder="请输入密码"
            :class="{ 'is-invalid': errors.password }"
          />
          <div class="invalid-feedback" v-if="errors.password">{{ errors.password }}</div>
        </div>
        
        <!-- 验证码组件 -->
        <div class="form-group captcha-group">
          <label for="captcha">验证码</label>
          <div class="captcha-container">
            <input 
              type="text" 
              id="captcha" 
              v-model="form.captcha" 
              placeholder="请输入验证码"
              :class="{ 'is-invalid': errors.captcha }"
            />
            <img 
              :src="captchaUrl" 
              alt="验证码" 
              @click="refreshCaptcha"
              class="captcha-img"
            />
          </div>
          <div class="invalid-feedback" v-if="errors.captcha">{{ errors.captcha }}</div>
        </div>

        
        <button type="submit" class="btn-login">登录</button>
      </form>
      
      <div class="register-link">
        <span>没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus'; 

export default {
  data() {
    return {
      form: {
        username: '',
        password: '',
        captcha: ''
      },
      errors: {},
      captchaUrl: '',
      isLoading: false 
    };
  },
  mounted() {
    this.refreshCaptcha();
    const video = document.querySelector('.background-video');
    video.onerror = () => {
      console.log('视频加载失败，显示备用背景');
    };
  },
  methods: {
    refreshCaptcha() {
      this.isLoading = true;
      const timestamp = new Date().getTime();
      
      // 清空当前验证码输入
      this.form.captcha = '';
      
      // 更新验证码URL
      this.captchaUrl = `http://localhost:8080/api/captcha?timestamp=${timestamp}`;
      
      // 仅发送请求获取新验证码，不处理响应数据
      axios.get(`http://localhost:8080/api/captcha?timestamp=${timestamp}`, { responseType: 'blob' })
        .catch(error => {
          console.error('验证码加载失败', error);
          ElMessage.error('验证码加载失败，请重试');
        })
        .finally(() => {
          this.isLoading = false;
        });
    },
    
    validateForm() {
      const errors = {};
      
      if (!this.form.username) {
        errors.username = '请输入手机号';
      } else if (!/^1[3-9]\d{9}$/.test(this.form.username)) {
        errors.username = '请输入有效的手机号';
      }
      
      if (!this.form.password) {
        errors.password = '请输入密码';
      } else if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,16}$/.test(this.form.password)) {
        errors.password = '密码必须包含字母和数字，长度为6-16位';
      }
      
      if (!this.form.captcha) {
        errors.captcha = '请输入验证码';
      }
      
      this.errors = errors;
      return Object.keys(errors).length === 0;
    },
    
    async handleLogin() {
      if (!this.validateForm()) return;
      
      try {
        const response = await axios.post('http://localhost:8080/api/login', {
          username: this.form.username,
          password: this.form.password,
          captcha: this.form.captcha
        });
        
        console.log('登录成功返回数据:', response.data);
        
        // 存储token（用于权限验证）
        localStorage.setItem('token', response.data.token);
        
        // 通过路由参数传递用户名
        this.$router.push({
          path: "/home",
          query: {
            user_name: response.data.username || this.form.username // 备用方案：使用表单中的用户名
          }
        });
      } catch (error) {
        console.error('登录失败:', error);
        
        // 处理服务器返回的错误
        if (error.response && error.response.data) {
          const errorData = error.response.data;
          ElMessage.error(errorData.msg || '登录失败，请重试');
          
          // 特别处理验证码错误
          if (errorData.code === '400') {
            this.form.captcha = ''; // 清空验证码输入
            this.refreshCaptcha();  // 刷新验证码
          }
        } else {
          ElMessage.error('登录失败，请检查网络连接');
        }
      }
    }
  }
};
</script>

<style scoped>
/* 原有登录页样式保持不变 */
html, body {
  height: 100%;
  overflow: hidden; 
}

.login-container {
  position: fixed; 
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  align-items: center; 
  justify-content: center; 
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.fallback-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f0f2f5;
  background-image: url('@/assets/bg-login.png');
  background-size: cover;
  background-position: center;
  z-index: 0;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(1px);
  -webkit-backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
  position: relative;
  z-index: 2;
}

.title {
  margin-bottom: 30px;
  text-align: center;
  color: #333;
  font-size: 24px;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: rgba(255,255,255,0.5);
}

input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.is-invalid {
  border-color: #f5222d;
  box-shadow: 0 0 0 2px rgba(245, 34, 45, 0.2);
}

.invalid-feedback {
  margin-top: 4px;
  color: #f5222d;
  font-size: 12px;
}

.captcha-group {
  position: relative;
}

.captcha-container {
  display: flex;
  gap: 10px;
}

.captcha-img {
  width: 120px;
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.2);
  transition: all 0.3s ease;
}

.captcha-img:hover {
  transform: scale(1.02);
  box-shadow: 0 0 10px rgba(255,255,255,0.5);
}

.btn-login {
  width: 100%;
  padding: 12px;
  background: rgba(255, 215, 0, 0.15); 
  backdrop-filter: blur(12px); 
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 215, 0, 0.3); 
  border-radius: 12px;
  color: #555; 
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(255, 215, 0, 0.1); 
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-login:hover {
  background: rgba(255, 215, 0, 0.25); 
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(255, 215, 0, 0.15);
}

.register-link {
  margin-top: 25px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.register-link a {
  color: #FFD700; 
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.register-link a:hover {
  text-decoration: underline;
  color: #FFA500; 
}
</style>