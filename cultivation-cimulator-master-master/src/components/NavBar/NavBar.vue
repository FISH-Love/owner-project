<!-- NavBar.vue (压缩版) -->
<template>
  <div class="user-info-card" :class="{ 'collapsed': isCollapsed }">
    <!-- 卡片头部 -->
    <div class="card-header" @click="toggleCard">
      <div class="header-left">
        <div class="logo">修仙模拟器</div>
        <div class="user-welcome" v-if="u_n">欢迎游玩！{{ u_n }}</div>
      </div>

      <div class="header-right">
        <div class="role-summary" v-if="selectedRole">
          <span class="role-name">{{ selectedRole.name }}</span>
          <span class="role-cultivation">{{ getCultivationText(selectedRole.cultivation) }}</span>
        </div>
        <button class="toggle-btn">
          <i class="fa" :class="isCollapsed ? 'fa-chevron-down' : 'fa-chevron-up'"></i>
        </button>
      </div>
    </div>

    <!-- 卡片主体 (折叠时隐藏) -->
    <div class="card-body" v-if="!isCollapsed">
      <!-- 角色详细信息 -->
      <div class="role-details" v-if="selectedRole">
        <div class="role-header">
          <div class="role-name">{{ selectedRole.name }}</div>
          <div class="role-cultivation">境界: {{ getCultivationText(selectedRole.cultivation) }}</div>
        </div>

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

      <!-- 操作按钮 -->
      <div class="card-actions">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "NavBar",
  props: {
    u_n: {
      type: String,
      default: ''
    },
    selectedRole: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      isCollapsed: true // 默认折叠状态
    };
  },
  methods: {
    toggleCard() {
      this.isCollapsed = !this.isCollapsed;
    },
    handleLogout() {
      this.$emit('logout');
    },
    getCultivationText(level) {
      const cultivationLevels = {
        1: '练气期',
        2: '大帝期',
        3: '上神期'
      };
      return cultivationLevels[level] || '未知境界';
    }
  }
};
</script>

<style scoped>
/* 液态玻璃风格的用户信息卡片 */
.user-info-card {
  position: fixed;
  top: 20px;
  left: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  z-index: 100;
  transition: all 0.3s ease;
  overflow: hidden;
}

/* 折叠状态样式 */
.collapsed {
  max-height: 70px;
}

.collapsed .card-body {
  display: none;
}

/* 卡片头部 */
.card-header {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.logo {
  font-size: 18px;
  font-weight: 600;
  color: #F9D79F ;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.user-welcome {
  color: #F9D79F ;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.role-summary {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-name {
  font-weight: 600;
  color: #F9D79F ;
  font-size: 14px;
}

.role-cultivation {
  color: #F9D79F ;
  font-size: 12px;
}

.toggle-btn {
  background: none;
  border: none;
  padding: 8px;
  color: #F9D79F ;
  cursor: pointer;
  transition: all 0.3s;
}

.toggle-btn:hover {
  color: #F9D79F ;
}

/* 卡片主体 */
.card-body {
  padding: 15px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.role-details {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(100px);
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 15px;
}

.role-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.role-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  font-size: 13px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
}

.stat-label {
  color: #F9D79F ;
}

.stat-value {
  color: #F9D79F ;
  font-weight: 500;
}

.card-actions {
  display: flex;
  justify-content: center;
}

.logout-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.4), rgba(245, 108, 108, 0.2));
  backdrop-filter: blur(5px);
  color: white;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 10px rgba(245, 108, 108, 0.2);
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(245, 108, 108, 0.3);
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.5), rgba(245, 108, 108, 0.3));
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-info-card {
    top: 10px;
    left: 10px;
    right: 10px;
  }

  .card-header {
    padding: 12px 15px;
  }

  .logo {
    font-size: 16px;
  }

  .user-welcome {
    font-size: 12px;
  }

  .role-summary {
    flex-direction: column;
    align-items: flex-end;
    gap: 2px;
  }

  .role-name, .role-cultivation {
    font-size: 12px;
  }

  .card-body {
    padding: 12px 15px;
  }

  .role-stats {
    grid-template-columns: 1fr;
  }

  .logout-btn {
    padding: 6px 15px;
    font-size: 12px;
  }
}
</style>