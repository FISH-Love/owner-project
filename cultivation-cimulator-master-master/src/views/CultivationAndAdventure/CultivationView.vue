<template>
  <div class="cultivation-container">
    <!-- 视频背景 -->
    <video 
      class="bg-video" 
      autoplay 
      muted 
      loop 
      src="@/assets/cultivation-video.mp4" 
    ></video>

    <!-- 角色信息卡片（液态玻璃风格） -->
    <div class="role-card" :style="glassStyle">
      <div class="role-info">
        <h2>角色信息</h2>
        <p>名称: {{ userRole.name }}</p>
        <p>ID: {{ userRole.id }}</p>
        <p>经验值: {{ userRole.exp }} (当前修炼收益: {{ pendingExp }})</p>
        <p>攻击: {{ userRole.atk }} (收益: {{ pendingAtk }})</p>
        <p>防御: {{ userRole.def }} (收益: {{ pendingDef }})</p>
        <p>生命值: {{ userRole.hp }} (收益: {{ pendingHp }})</p>
      </div>
    </div>

    <!-- 自动挂机播报（文字轮播） -->
    <div class="broadcast-box" :style="glassStyle">
      <div class="broadcast-wrapper">
        <div 
          class="broadcast-text" 
          :style="{ animationDuration: `${textScrollDuration}s` }"
        >
          {{ broadcastText }}
        </div>
      </div>
    </div>

    <!-- 挂机控制按钮 -->
    <div class="control-box" :style="glassStyle">
      <button 
        class="btn start-btn" 
        @click="startCultivate" 
        :disabled="isCultivating || autoCultivate"
      >
        开始修炼
      </button>
      <button 
        class="btn end-btn" 
        @click="endCultivate" 
        :disabled="!isCultivating && !autoCultivate"
      >
        结束修炼
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userRole: null,                 // 角色数据
      isCultivating: false,           // 手动修炼状态
      autoCultivate: false,           // 自动修炼状态
      cultivateTimer: null,           // 经验值定时器
      attrTimer: null,                // 属性加成定时器
      remainingTime: 0,               // 自动修炼剩余时间（秒）
      broadcastText: '等待开始修炼...', // 轮播文字
      textScrollDuration: 20,         // 文字滚动时长
      glassStyle: {                   // 液态玻璃样式
        backdropFilter: 'blur(10px)',
        backgroundColor: 'rgba(255, 255, 255, 0.2)',
        border: '1px solid rgba(255, 255, 255, 0.3)',
        borderRadius: '12px',
        padding: '20px',
        margin: '20px 0',
      },
      pendingExp: 0,                  // 暂存经验值收益
      pendingAtk: 0,                  // 暂存攻击收益
      pendingDef: 0,                  // 暂存防御收益
      pendingHp: 0,                   // 暂存生命值收益
    };
  },

  created() {
    this.parseRoleData();
    this.checkAutoCultivate();
  },

  methods: {
    // 解析角色数据
    parseRoleData() {
      try {
        const userRoleStr = this.$route.query.userRole;
        if (userRoleStr) {
          this.userRole = JSON.parse(userRoleStr);
        } else {
          console.error('未传递角色数据，使用默认值');
          this.userRole = {
            id: 1,
            name: '默认角色',
            exp: 0,
            atk: 10,
            def: 10,
            hp: 100,
          };
        }
      } catch (error) {
        console.error('解析角色数据失败:', error);
        this.userRole = {
          id: 1,
          name: '默认角色',
          exp: 0,
          atk: 10,
          def: 10,
          hp: 100,
        };
      }
    },

    // 检查URL参数是否包含自动修炼指令
    checkAutoCultivate() {
      const hours = parseInt(this.$route.query.hours) || 0;
      const minutes = parseInt(this.$route.query.minutes) || 0;
      
      if (hours > 0 || minutes > 0) {
        const totalSeconds = hours * 3600 + minutes * 60;
        if (totalSeconds > 0) {
          this.remainingTime = totalSeconds;
          this.autoCultivate = true;
          this.startCultivate();
          
          // 更新广播文本
          this.broadcastText = `自动修炼已启动！将持续 ${hours} 小时 ${minutes} 分钟`;
          
          // 添加倒计时定时器
          setInterval(() => {
            if (this.remainingTime > 0) {
              this.remainingTime--;
              
              // 剩余时间小于60秒时显示秒数
              if (this.remainingTime <= 60) {
                this.broadcastText = `自动修炼即将结束！剩余 ${this.remainingTime} 秒`;
              }
              
              // 时间到自动结束
              if (this.remainingTime === 0) {
                this.endCultivate();
              }
            }
          }, 1000);
        }
      }
    },

    // 开始修炼
    startCultivate() {
      this.isCultivating = true;
      
      // 每秒累加经验值
      this.cultivateTimer = setInterval(() => {
        this.pendingExp += 1;
      }, 1000);
      
      // 每200秒随机属性+5
      this.attrTimer = setInterval(() => {
        this.randomMarkAttr();
      }, 200000);
      
      // 更新广播文本
      if (this.autoCultivate) {
        this.broadcastText = `自动修炼中...剩余 ${this.formatDuration(this.remainingTime)}`;
      } else {
        this.broadcastText = '正在挂机修炼，收益将在结束时更新...';
      }
    },

    // 结束修炼
    endCultivate() {
      this.isCultivating = false;
      this.autoCultivate = false;
      this.broadcastText = '修炼结束，正在更新数据...';
      this.clearTimers();
      
      // 只有存在收益时才调用接口
      if (this.pendingExp > 0 || this.pendingAtk > 0 || this.pendingDef > 0 || this.pendingHp > 0) {
        this.updateAllStats();
      } else {
        this.broadcastText = '修炼结束，未获得收益';
      }
    },

    // 清除所有定时器
    clearTimers() {
      clearInterval(this.cultivateTimer);
      clearInterval(this.attrTimer);
      this.cultivateTimer = null;
      this.attrTimer = null;
    },

    // 随机标记属性加成
    randomMarkAttr() {
      const attrs = ['atk', 'def', 'hp'];
      const randomAttr = attrs[Math.floor(Math.random() * attrs.length)];
      this[`pending${randomAttr.charAt(0).toUpperCase() + randomAttr.slice(1)}`] += 5;
      
      // 更新广播文本
      this.broadcastText = `检测到属性增长！${this.getAttrName(randomAttr)}+5（结束时生效）`;
    },

    // 一次性更新所有收益到后端
    updateAllStats() {
      const baseUrl = 'http://localhost:8080/api';
      const promises = [];

      // 更新经验值
      if (this.pendingExp > 0) {
        const expUrl = `${baseUrl}/updateExpById?id=${this.userRole.id}&exp=${this.userRole.exp + this.pendingExp}`;
        promises.push(axios.post(expUrl));
      }

      // 更新攻击
      if (this.pendingAtk > 0) {
        const atkUrl = `${baseUrl}/updateAtkById?id=${this.userRole.id}&atk=${this.userRole.atk + this.pendingAtk}`;
        promises.push(axios.post(atkUrl));
      }

      // 更新防御
      if (this.pendingDef > 0) {
        const defUrl = `${baseUrl}/updateDefById?id=${this.userRole.id}&def=${this.userRole.def + this.pendingDef}`;
        promises.push(axios.post(defUrl));
      }

      // 更新生命值
      if (this.pendingHp > 0) {
        const hpUrl = `${baseUrl}/updateHpById?id=${this.userRole.id}&hp=${this.userRole.hp + this.pendingHp}`;
        promises.push(axios.post(hpUrl));
      }

      // 并发请求所有更新
      Promise.all(promises)
        .then(() => {
          // 更新前端数据
          this.userRole.exp += this.pendingExp;
          this.userRole.atk += this.pendingAtk;
          this.userRole.def += this.pendingDef;
          this.userRole.hp += this.pendingHp;
          
          this.broadcastText = `修炼结束！共获得 ${this.pendingExp} 经验，属性提升：
            ${this.pendingAtk > 0 ? `攻击+${this.pendingAtk}` : ''}
            ${this.pendingDef > 0 ? `防御+${this.pendingDef}` : ''}
            ${this.pendingHp > 0 ? `生命+${this.pendingHp}` : ''}`;
          
          // 重置收益变量
          this.resetPendingStats();
        })
        .catch(error => {
          console.error('更新数据失败:', error);
          this.broadcastText = '修炼结束，但更新数据时出错';
        });
    },

    // 重置收益变量
    resetPendingStats() {
      this.pendingExp = 0;
      this.pendingAtk = 0;
      this.pendingDef = 0;
      this.pendingHp = 0;
      this.remainingTime = 0;
    },

    // 属性英文转中文
    getAttrName(attr) {
      const nameMap = {
        atk: '攻击',
        def: '防御',
        hp: '生命值'
      };
      return nameMap[attr] || attr;
    },

    // 格式化持续时间
    formatDuration(seconds) {
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;
      
      if (hours > 0) {
        return `${hours}小时${minutes}分${secs}秒`;
      } else if (minutes > 0) {
        return `${minutes}分${secs}秒`;
      } else {
        return `${secs}秒`;
      }
    }
  },

  // 组件销毁时清除定时器
  beforeUnmount() {
    this.clearTimers();
  }
};
</script>

<style scoped>
/* 基础背景，确保玻璃效果可见 */
body {
  min-height: 100vh;
  margin: 0;
  padding: 20px;
  box-sizing: border-box;
  font-family: 'Arial', sans-serif;
  position: relative; /* 为视频定位做参考 */
}

/* 视频背景样式 */
.bg-video {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  object-fit: cover; /* 保持视频比例并覆盖整个容器 */
  z-index: -1; /* 让视频在内容下方 */
}

.cultivation-container {
  max-width: 600px;
  margin: 0 auto;
  color: #f0f0f0;
  position: relative; /* 让内容层级高于视频 */
  z-index: 1; 
}

/* 液态玻璃卡片公共样式 */
.role-card, .broadcast-box, .control-box {
  position: relative; /* 确保卡片在视频上方 */
  z-index: 1;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.role-card:hover, .broadcast-box:hover, .control-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 32px 0 rgba(0, 0, 0, 0.45);
}

.role-info h2 {
  margin-top: 0;
  color: #4cc9f0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.role-info p {
  margin: 8px 0;
  font-size: 16px;
}

/* 按钮样式 */
.control-box {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.btn {
  padding: 10px 20px;
  background: rgba(76, 201, 240, 0.2);
  border: 1px solid rgba(76, 201, 240, 0.5);
  color: #4cc9f0;
  cursor: pointer;
  border-radius: 8px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn:hover {
  background: rgba(76, 201, 240, 0.3);
  transform: translateY(-2px);
}

.start-btn {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.5);
  color: #3b82f6;
}

.end-btn {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.5);
  color: #ef4444;
}

/* 文字轮播样式 */
.broadcast-wrapper {
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
}

.broadcast-text {
  display: inline-block;
  animation: scroll-left linear infinite;
  color: #f0f0f0;
  font-size: 16px;
}

@keyframes scroll-left {
  0% {
    transform: translateX(100%);
  }
  100% {
    transform: translateX(-100%);
  }
}
</style>