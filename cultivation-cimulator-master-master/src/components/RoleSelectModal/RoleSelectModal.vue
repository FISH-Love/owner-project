<template>
  <div class="role-select-modal" v-if="visible">
    <!-- 遮罩层 -->
    <div class="modal-mask" @click="cancel"></div>
    
    <!-- 弹窗内容 -->
    <div class="modal-content">
      <div class="modal-header">
        <h3>请选择您的角色</h3>
        <button class="close-btn" @click="cancel">&times;</button>
      </div>
      
      <div class="modal-body">
        <!-- 加载状态提示 -->
        <div v-if="loading" class="loading-indicator">
          <div class="spinner"></div>
          <p>加载角色数据中...</p>
        </div>
        
        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-message">
          <p>{{ errorMessage }}</p>
        </div>
        
        <!-- 老虎机区域 - 仅在数据加载完成后显示 -->
        <div v-if="allRoles.length > 0" class="slot-machine-container">
          <div class="slot-machine">
            <div class="slot-window">
              <div class="slot-viewport" ref="slotViewport">
                <div v-for="(role, index) in allRoles" :key="index" class="slot-item">
                  <div class="role-card">
                    <div class="role-name">{{ role.name }}</div>
                    <div class="role-cultivation">境界: {{ getCultivationText(role.cultivation) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 控制按钮 -->
          <div class="control-buttons">
            <button class="spin-btn" @click="startSpin" :disabled="isSpinning">开始</button>
            <button class="stop-btn" @click="stopSpin" :disabled="!isSpinning">停止</button>
          </div>
          
          <!-- 选中角色信息 -->
          <div class="selected-role-info" v-if="selectedRole">
            <h4>已选择角色: {{ selectedRole.name }}</h4>
            <div class="role-stats">
              <div class="stat-item">
                <span class="stat-label">生命值:</span>
                <span class="stat-value">{{ selectedRole.hp }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">攻击力:</span>
                <span class="stat-value">{{ selectedRole.atk }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">防御力:</span>
                <span class="stat-value">{{ selectedRole.def }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">经验值:</span>
                <span class="stat-value">{{ selectedRole.exp }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">寿命:</span>
                <span class="stat-value">{{ selectedRole.lifeSpan }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 无角色数据提示 -->
        <div v-if="!loading && !errorMessage && allRoles.length === 0" class="no-roles-tip">
          <p>未获取到角色数据，请检查网络连接</p>
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="confirm-btn" @click="confirm" :disabled="!selectedRole">确认选择</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RoleSelectModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      allRoles: [],
      isSpinning: false,
      spinInterval: null,
      spinSpeed: 50,
      maxSpeed: 20,
      acceleration: 2,
      currentPosition: 0,
      selectedRole: null,
      spinCount: 0,
      totalSpins: 20,
      loading: true,         
      errorMessage: '',      
      apiLog: ''             
    };
  },
  methods: {
    getCultivationText(level) {
      const cultivationLevels = {
        1: '练气期',
        2: '大帝期',
        3: '上神期'
      };
      return cultivationLevels[level] || `未知境界 (${level})`;
    },
    
    startSpin() {
      if (this.isSpinning || !this.allRoles.length) return;
      
      this.isSpinning = true;
      this.spinCount = 0;
      this.spinSpeed = 50;
      this.currentPosition = 0;
      
      this.spinInterval = setInterval(() => {
        this.spinCount++;
        
        if (this.spinSpeed > this.maxSpeed) {
          this.spinSpeed -= this.acceleration;
        }
        
        this.currentPosition += 1;
        if (this.currentPosition >= this.allRoles.length) {
          this.currentPosition = 0;
        }
        
        const viewport = this.$refs.slotViewport;
        const itemHeight = 60;
        viewport.style.transform = `translateY(-${this.currentPosition * itemHeight}px)`;
        
        if (this.spinCount > this.totalSpins) {
          this.spinSpeed += this.acceleration / 2;
          if (this.spinSpeed > 100) {
            this.stopSpin();
          }
        }
      }, this.spinSpeed);
    },
    
    stopSpin() {
      if (!this.isSpinning) return;
      
      clearInterval(this.spinInterval);
      this.isSpinning = false;
      this.selectedRole = this.allRoles[this.currentPosition];
    },
    
    confirm() {
      if (!this.selectedRole) return;
      this.$emit('confirm', this.selectedRole);
    },
    
    cancel() {
      if (this.isSpinning) {
        this.stopSpin();
      }
      this.$emit('cancel');
    },
    
    async fetchAllRoles() {
      this.loading = true;
      this.errorMessage = '';
      
      try {
        const response = await this.$axios.post('/roles');
        this.apiLog = `roles接口响应: ${JSON.stringify(response.data)}`;
        console.log('角色API响应:', response.data);
        
        this.allRoles = response.data || [];
        
        if (this.allRoles.length > 0) {
          this.allRoles = [...this.allRoles, ...this.allRoles, ...this.allRoles];
        }
        
        console.log('角色数据加载成功，共', this.allRoles.length, '个角色');
      } catch (error) {
        this.errorMessage = '获取角色列表失败，请检查网络连接';
        console.error('获取角色列表失败:', error);
        
        if (error.response) {
          this.errorMessage = `请求错误: ${error.response.status} - ${error.response.statusText}`;
        } else if (error.request) {
          this.errorMessage = '网络错误，服务器未响应';
        } else {
          this.errorMessage = '请求处理失败: ' + error.message;
        }
      } finally {
        this.loading = false;
      }
    }
  },
  mounted() {
    console.log('RoleSelectModal 组件已挂载');
    console.log(this.visible)
    this.fetchAllRoles();
  },
  beforeUnmount() {
    if (this.spinInterval) {
      clearInterval(this.spinInterval);
    }
    console.log('RoleSelectModal 组件已卸载');
  }
};
</script>

<style scoped>
/* 基础液态玻璃样式 */
.role-select-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  width: 400px;
  max-width: 90%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.18);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.modal-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.2);
}

/* 加载状态样式 */
.loading-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 180px;
  color: #606266;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(64, 158, 255, 0.1);
  border-left-color: #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

.error-message {
  padding: 15px;
  background: rgba(255, 240, 240, 0.3);
  backdrop-filter: blur(5px);
  border-left: 4px solid rgba(245, 108, 108, 0.5);
  color: #909399;
  margin-bottom: 20px;
  border-radius: 0 4px 4px 0;
}

.no-roles-tip {
  padding: 20px;
  text-align: center;
  color: #909399;
  background: rgba(250, 250, 250, 0.2);
  backdrop-filter: blur(5px);
  border-radius: 8px;
  margin-bottom: 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 头部样式 */
.modal-header {
  padding: 16px 20px;
  background: rgba(245, 247, 250, 0.3);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #909399;
  cursor: pointer;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #303133;
}

/* 主体内容样式 */
.modal-body {
  padding: 20px;
}

.slot-machine {
  position: relative;
  height: 180px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.slot-window {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(6px);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
}

.slot-viewport {
  transition: transform 0.05s ease-out;
}

.slot-item {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(235, 238, 245, 0.3);
  background: rgba(255, 255, 255, 0.1);
}

.role-card {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(6px);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  width: 80%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.role-name {
  font-weight: 600;
  color: #303133;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.role-cultivation {
  color: #606266;
  font-size: 14px;
}

/* 按钮样式 */
.control-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.spin-btn, .stop-btn {
  padding: 8px 20px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.3), rgba(64, 158, 255, 0.1));
  backdrop-filter: blur(5px);
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.2);
}

.spin-btn {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.4), rgba(64, 158, 255, 0.2));
}

.stop-btn {
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.4), rgba(245, 108, 108, 0.2));
}

.spin-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
}

.stop-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(245, 108, 108, 0.3);
}

.spin-btn:disabled, .stop-btn:disabled {
  background: rgba(192, 196, 204, 0.3);
  box-shadow: none;
  cursor: not-allowed;
}

/* 选中角色信息样式 */
/* 选中角色信息样式 */
.selected-role-info {
  margin-top: 20px;
  padding: 12px;
  background: rgba(245, 247, 250, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.selected-role-info:hover {
  transform: translateY(-2px);
}

.selected-role-info h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 8px;
}

.role-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 10px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(4px);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.stat-label {
  color: #606266;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
}

.stat-value {
  color: #303133;
  font-weight: 500;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
}

/* 底部按钮样式 */
.modal-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: center;
  background: rgba(245, 247, 250, 0.1);
  backdrop-filter: blur(8px);
}

.confirm-btn {
  padding: 10px 30px;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.4), rgba(103, 194, 58, 0.2));
  backdrop-filter: blur(5px);
  color: white;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 10px rgba(103, 194, 58, 0.2);
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(103, 194, 58, 0.3);
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.5), rgba(103, 194, 58, 0.3));
}

.confirm-btn:disabled {
  background: rgba(192, 196, 204, 0.3);
  box-shadow: none;
  cursor: not-allowed;
}

/* 液态玻璃动态效果 */
@keyframes liquid-wave {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.modal-content {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.2) 0%, 
    rgba(255, 255, 255, 0.1) 50%, 
    rgba(255, 255, 255, 0.2) 100%);
  background-size: 200% 200%;
  animation: liquid-wave 15s ease infinite;
}

.slot-machine {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.25) 0%, 
    rgba(255, 255, 255, 0.15) 50%, 
    rgba(255, 255, 255, 0.25) 100%);
  background-size: 200% 200%;
  animation: liquid-wave 10s ease infinite;
}

.role-card {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.3) 0%, 
    rgba(255, 255, 255, 0.2) 50%, 
    rgba(255, 255, 255, 0.3) 100%);
  background-size: 200% 200%;
  animation: liquid-wave 8s ease infinite;
}

/* 选中角色高亮效果 */
.slot-item.active {
  background: rgba(64, 158, 255, 0.2);
}

.slot-item.active .role-card {
  border-color: rgba(64, 158, 255, 0.5);
  box-shadow: 0 0 15px rgba(64, 158, 255, 0.3);
  transform: scale(1.02);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modal-content {
    width: 90%;
  }
  
  .slot-machine {
    height: 150px;
  }
  
  .slot-item {
    height: 50px;
  }
  
  .role-card {
    padding: 6px 10px;
  }
  
  .role-name {
    font-size: 15px;
  }
  
  .role-cultivation {
    font-size: 12px;
  }
  
  .role-stats {
    grid-template-columns: 1fr;
  }
}
</style>