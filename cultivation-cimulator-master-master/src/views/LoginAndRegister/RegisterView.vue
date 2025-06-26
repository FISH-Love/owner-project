<template>
    <div class="register-container">
      <!-- 视频背景 -->
      <video class="background-video" autoplay muted loop playsinline>
        <source src="@/assets/register-video.mp4" type="video/mp4">
        <!-- 视频加载失败时的备用背景 -->
        <div class="fallback-background"></div>
      </video>
      
      <div class="register-box">
        <h2 class="title">修仙模拟器</h2>
        <p class="subtitle">创建您的修仙账号</p>
        
        <form @submit.prevent="handleRegister">
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
          
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input 
              type="password" 
              id="confirmPassword" 
              v-model="form.confirmPassword" 
              placeholder="请再次输入密码"
              :class="{ 'is-invalid': errors.confirmPassword }"
            />
            <div class="invalid-feedback" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</div>
          </div>
          
          <button type="submit" class="btn-register">立即注册</button>
        </form>
        
        <div class="login-link">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        form: {
          username: '',
          password: '',
          confirmPassword: ''
        },
        errors: {}
      };
    },
    methods: {
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
        
        if (!this.form.confirmPassword) {
          errors.confirmPassword = '请确认密码';
        } else if (this.form.confirmPassword !== this.form.password) {
          errors.confirmPassword = '两次输入的密码不一致';
        }
        
        this.errors = errors;
        return Object.keys(errors).length === 0;
      },
      
      async handleRegister() {
        if (!this.validateForm()) return;
        
        try {
          await axios.post('http://localhost:8080/api/register', {
            username: this.form.username,
            password: this.form.password
          });
          
          // 使用原生alert替代this.$message
          alert('注册成功，请登录');
          this.$router.push('/login');
        } catch (error) {
          if (error.response) {
            // 使用原生alert替代this.$message
            alert(error.response.data.message || '注册失败');
          } else {
            alert('网络连接失败');
          }
        }
      }
    }
  };
  </script>
  
  <style scoped>
  html, body {
    height: 100%;
    overflow: hidden; /* 禁止整个页面滚动 */
  }
  
  .register-container {
    position: fixed; /* 使用fixed定位，确保不会滚动 */
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    display: flex;
    justify-content: center; /* 水平居中 */
    align-items: center; /* 垂直居中 */
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
    background-image: url('@/assets/bg-register.png');
    background-size: cover;
    background-position: center;
    z-index: 0;
  }
  
  .register-box {
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
    margin-bottom: 10px;
    text-align: center;
    color: #333;
    font-size: 24px;
    font-weight: 700;
    text-shadow: 0 1px 2px rgba(0,0,0,0.1);
  }
  
  .subtitle {
    margin-bottom: 30px;
    text-align: center;
    color: #666;
    font-size: 14px;
    text-shadow: 0 1px 2px rgba(255,255,255,0.3);
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
  
  .btn-register {
  width: 100%;
  padding: 12px;
  background: rgba(255, 215, 0, 0.15); /* 淡金色半透明背景 */
  backdrop-filter: blur(12px); /* 高度模糊效果 */
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 215, 0, 0.3); /* 淡金色半透明边框 */
  border-radius: 12px;
  color: #555; /* 深灰色文字 */
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(255, 215, 0, 0.1); /* 淡金色微妙阴影 */
}

.btn-register:hover {
  background: rgba(255, 215, 0, 0.25); /* 悬停时增加透明度 */
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(255, 215, 0, 0.15);
}
  
  .login-link {
    margin-top: 25px;
    text-align: center;
    color: #666;
    font-size: 14px;
  }
  
  .login-link a {
    color: #FFD700; /* 淡金色文本颜色 */
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s ease;
  }
  
  .login-link a:hover {
    text-decoration: underline;
    color: #FFA500; /* 悬停时加深为金色 */
  }
  </style>
