<template>
  <div class="main-content">
    <!-- 顶部导航栏 -->
    <div class="navbar">
      <span class="navbar-title">遇袭</span>
    </div>

    <!-- 播报区，紧跟导航栏下方 -->
    <div class="broadcast-log" v-if="battleLog.length">
      <div v-for="(msg, idx) in battleLog" :key="idx">{{ msg }}</div>
    </div>

    <!-- 战斗音乐 -->
    <audio
        ref="bgm"
        :src="require('@/assets/battle-bgm.mp3')"
        loop
        autoplay
        preload="auto"
        style="display: none;"
    ></audio>

    <!-- 视频背景 -->
    <video
        class="background-video"
        autoplay
        loop
        muted
        playsinline
    >
      <source src="@/assets/bg-battle.mp4" type="video/mp4" />
      您的浏览器不支持视频播放。
    </video>

    <!-- 战斗主区域 -->
    <div class="battle-arena content-overlay">
      <!-- 左侧：我方角色 -->
      <div class="side left">
        <div v-if="userRole" class="role-card" :class="{ 'shake-effect': isHeroHit }">
          <div class="avatar">
            <img src="@/assets/role_head.png" alt="角色头像" />
          </div>
          <!-- 血条 -->
          <div class="hp-bar">
            <div
                class="hp-bar-inner"
                :style="{ width: ((heroHp / roleHp) * 100) + '%'}"
            ></div>
          </div>
          <div class="role-info">
            <p>角色名称: {{ userRole.name }}</p>
            <p>等级: {{ userRole.level ?? userRole.cultivation }}</p>
            <p>生命: {{ heroHp }} / {{ roleHp }}</p>
            <p>攻击: {{ roleAtk }}</p>
            <p>防御: {{ roleDef }}</p>
          </div>
        </div>
      </div>
      <!-- 右侧：怪物 -->
      <div class="side right">
        <div v-if="monster" class="monster-card" :class="{ 'shake-effect': isMonsterHit }">
          <div class="avatar">
            <img src="@/assets/monster_head.png" alt="怪物头像" />
          </div>
          <!-- 血条 -->
          <div class="hp-bar">
            <div
                class="hp-bar-inner monster"
                :style="{ width: ((monsterHp / (monster ? Math.max(monster.maxHp, monsterHp) : 1)) * 100) + '%'}"
            ></div>
          </div>
          <div class="monster-info">
            <p>怪物名称: {{ monster.name }}</p>
            <p>怪物类型:
              <span v-if="monster.type === 1">普通怪</span>
              <span v-else-if="monster.type === 2">精英怪</span>
              <span v-else-if="monster.type === 3">Boss</span>
            </p>
            <p>生命: {{ monsterHp }} / {{ monster ? monster.maxHp : '' }}</p>
            <p>攻击: {{ monsterAtk }}</p>
            <p>防御: {{ monsterDef }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 结果弹窗 TODO-->
    <div class="result-overlay" v-if="battleResult">
      <div class="result-box">
        <h2>{{ battleResult }}</h2>
        <button @click="restartBattle">再来一次</button>
        <!-- TODO-->
        <button @click="restartBattle">领取奖励</button>
      </div>
    </div>

    <!-- Dock 栏 -->
    <div class="dock-bar" v-if="!battleResult && isMyTurn">
      <button class="dock-btn" @click="doAttack('normal')" :disabled="!canOperate">⚔️<span class="label">普攻</span></button>
      <button class="dock-btn" @click="doAttack('skill')" :disabled="!canOperate">✨<span class="label">元素战技</span></button>
      <button class="dock-btn" @click="doAttack('burst')" :disabled="!canOperate">💥<span class="label">元素爆发</span></button>
      <button class="dock-btn" @click="escapeBattle()" :disabled="!canOperate">💨<span class="label">逃离战斗</span></button>
    </div>
    <!-- 逃跑弹窗 -->
    <div class="escape-popup" :class="{ 'active': showEscapePopup }">
      <h2>你逃跑了</h2>
      <button @click="goToHome">返回主页</button>
    </div>
    <!-- 奖励弹窗 TODO-->
    <div class="rewards-popup" :class="{ 'active': showRewardsPopup }">
      <div class="popup-content">
        <h2>战斗胜利！</h2>
        <div class="rewards-list">
          <div
              v-for="relic in currentRewards"
              :key="relic.id"
              class="relic-item"
              :class="getRarityClass(relic.type)"
          >
            <div class="relic-header">
              <div class="relic-name">{{ relic.name }}</div>
              <div class="relic-rarity">{{ getRarityText(relic.type) }}</div>
            </div>
            <div class="relic-stats">
              <div class="stat-row" v-if="relic.hp !== 0">
                <span class="stat-label">HP:</span>
                <span class="stat-value {{ relic.hp > 0 ? 'positive' : 'negative' }}">
              {{ relic.hp > 0 ? '+' + relic.hp : relic.hp }}
            </span>
              </div>
              <div class="stat-row" v-if="relic.atk !== 0">
                <span class="stat-label">ATK:</span>
                <span class="stat-value {{ relic.atk > 0 ? 'positive' : 'negative' }}">
              {{ relic.atk > 0 ? '+' + relic.atk : relic.atk }}
            </span>
              </div>
              <div class="stat-row" v-if="relic.def !== 0">
                <span class="stat-label">DEF:</span>
                <span class="stat-value {{ relic.def > 0 ? 'positive' : 'negative' }}">
              {{ relic.def > 0 ? '+' + relic.def : relic.def }}
            </span>
              </div>
            </div>
            <div class="relic-desc">{{ relic.description }}</div>
          </div>
        </div>
        <button @click="closeRewardsPopup" class="btn-claim">
          <i class="fa fa-check"></i> 领取奖励
        </button>
      </div>
    </div>
  </div>

</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      // 我方属性
      userRole: null,
      roleAtk: 0,
      roleDef: 0,
      roleHp: 0,
      heroHp: 0, // 当前血量
      // 敌方属性
      monster: null,
      monsterHp: 0,
      monsterDef: 0,
      monsterAtk: 0,
      // 战斗机制
      isMyTurn: true,
      canOperate: true,
      battleLog: [],
      battleResult: "",
      // 受击判断
      isHeroHit: false,
      isMonsterHit: false,
      // 控制弹窗显示
      showEscapePopup: false,
      // 显示奖励
      showRewardsPopup: false,
      currentRewards: [],
      // 战斗音效 预加载
      sounds:{
        normal: require('@/assets/sound/normal.mp3'),
        skill: require('@/assets/sound/skill.mp3'),
        burst: require('@/assets/sound/burst.mp3')
      }
    }
  },
  created() {
    // 本地角色参数
    try {
      const userRoleStr = this.$route.query.userRole;
      if (userRoleStr) {
        const userRoleObj = JSON.parse(userRoleStr);
        this.roleAtk = userRoleObj.atk ?? 0;
        this.roleDef = userRoleObj.def ?? 0;
        this.roleHp = userRoleObj.hp ?? 0;
        this.heroHp = this.roleHp;
        this.userRole = userRoleObj;
      } else {
        console.error('未传递角色数据');
      }
    } catch (error) {
      console.error('解析角色数据失败:', error);
    }
    this.fetchMonster();
  },
  mounted() {
    // 自动播放音乐（部分浏览器需用户交互后才可播放）
    const bgm = this.$refs.bgm;
    if (bgm && typeof bgm.play === 'function') {
      bgm.volume = 0.6;
      // 若页面已交互可自动播放，否则需手动触发
      bgm.play().catch(() => {
        // 某些浏览器策略不允许自动播放，可在用户交互时触发
        const unlock = () => {
          bgm.play();
          window.removeEventListener('click', unlock);
        };
        window.addEventListener('click', unlock);
      });
    }
  },
  methods: {
    fetchMonster() {
      axios.get('http://localhost:8080/api/monsters/random')
          .then(res => {
            const data = res.data.data; // 注意这里取 data 里的 data
            console.log('怪物原始数据:', data);

            // 按实际字段赋值
            const minHp = Number(data.minHp);
            const maxHp = Number(data.maxHp);
            const minDef = Number(data.minDef);
            const maxDef = Number(data.maxDef);
            const minAtk = Number(data.minAtk);
            const maxAtk = Number(data.maxAtk);

            if (
                isNaN(minHp) || isNaN(maxHp) ||
                isNaN(minDef) || isNaN(maxDef) ||
                isNaN(minAtk) || isNaN(maxAtk)
            ) {
              console.error('怪物属性区间有问题:', minHp, maxHp, minDef, maxDef, minAtk, maxAtk);
              return;
            }

            this.monster = data;
            this.monsterHp = this.getRandomInt(minHp, maxHp);
            this.monsterDef = this.getRandomInt(minDef, maxDef);
            this.monsterAtk = this.getRandomInt(minAtk, maxAtk);

            // 其余初始化
            this.isMyTurn = true;
            this.canOperate = true;
            this.battleLog = [];
            this.battleResult = "";
            this.heroHp = this.roleHp;
          })
          .catch(err => {
            console.error('怪物信息获取失败:', err);
          });
    },
    getRandomInt(min, max) {
      if (isNaN(min) || isNaN(max)) {
        console.error('getRandomInt参数异常:', min, max);
        return 1;
      }
      if (min > max) [min, max] = [max, min];
      return Math.floor(Math.random() * (max - min + 1)) + min;
    },
    // 获取奖励
    getBattleRewards() {
      this.battleLog.push("正在获取战斗奖励...");

      // 修改请求参数为monsterTypeId
      axios.post('http://localhost:8080/api/battle/rewards', this.monster.typeId)
          .then(res => {
            const rewards = res.data;
            this.openRewardsPopup(rewards);
          })
          .catch(err => {
            console.error('获取奖励失败:', err);
            this.battleLog.push("奖励获取失败，请稍后重试");
          });
    },

// 显示奖励
    openRewardsPopup(rewards) {
      this.currentRewards = rewards;

      // 更新战斗日志格式
      rewards.forEach(relic => {
        this.battleLog.push(`获得遗物: ${relic.name} (${this.getRarityText(relic.type)})`);
      });

      // 延迟显示弹窗，确保日志先加载
      setTimeout(() => {
        this.showRewardsPopup = true;
      }, 500);
    },

// 关闭奖励弹窗
    closeRewardsPopup() {
      this.showRewardsPopup = false;

      // 可以在这里添加奖励应用逻辑

      setTimeout(() => {
        this.$router.push({ name: 'Home' });
      }, 300);
    },

// 辅助方法：获取遗物稀有度文本
    getRarityText(type) {
      const rarityMap = {
        1: '普通',
        2: '罕见',
        3: '稀有',
        4: 'BOSS'
      };
      return rarityMap[type] || '未知';
    },

// 辅助方法：获取遗物稀有度CSS类
    getRarityClass(type) {
      const classMap = {
        1: 'relic-common',
        2: 'relic-uncommon',
        3: 'relic-rare',
        4: 'relic-boss'
      };
      return classMap[type] || 'relic-common';
    },
    // 逃跑方法
    escapeBattle() {
      // 播放逃跑音效
      // new Audio(require('@/assets/sound/escape.mp3')).play();
      // 禁用操作按钮
      this.canOperate = false;
      // 添加逃跑日志
      this.battleLog.push("你选择了逃跑！");
      // 显示弹窗（添加动画延迟）
      setTimeout(() => {
        this.showEscapePopup = true;
      }, 300);
    },
    goToHome() {
      // 关闭弹窗
      this.showEscapePopup = false;
      // 延迟后跳转主页（与动画同步）
      setTimeout(() => {
        this.$router.push({ name: 'Home' });
      }, 300);
    },
    // 添加受击动画
    triggerHitEffect(target) {
      if (target === 'hero') {
        this.isHeroHit = true;
        setTimeout(() => this.isHeroHit = false, 300); // 0.3秒后恢复
      } else if (target === 'monster') {
        this.isMonsterHit = true;
        setTimeout(() => this.isMonsterHit = false, 300);
      }
    },
    doAttack(type) {
      if (!this.isMyTurn || this.battleResult) return;
      let damage = 0;
      let desc = '';
      if (type === 'normal') {
        damage = this.roleAtk;
        desc = `你对怪物发动了普攻，造成 ${damage} 点伤害！`;

        new Audio(this.sounds.normal).play();
      } else if (type === 'skill') {
        damage = this.roleAtk * 2;
        desc = `你释放了元素战技，造成 ${damage} 点伤害！`;
        new Audio(this.sounds.skill).play();
      } else if (type === 'burst') {
        damage = this.roleAtk * 3;
        desc = `你释放了元素爆发，造成 ${damage} 点伤害！`;
        new Audio(this.sounds.burst).play();
      }
      this.triggerHitEffect('monster');
      // 结算怪物受到的伤害（考虑怪物防御）
      let actualDamage = Math.max(1, damage - this.monsterDef); // 至少1伤害
      this.monsterHp = Math.max(0, this.monsterHp - actualDamage);
      this.battleLog.push(desc + `（怪物实际损失 ${actualDamage} 点生命）`);
      this.checkBattleEnd();
      if (!this.battleResult) {
        this.isMyTurn = false;
        this.canOperate = false;
        // 怪物回合（延迟模拟）
        setTimeout(() => this.monsterAttack(), 1000);
      }
    },
    monsterAttack() {
      if (this.battleResult) return;
      // 怪物攻击
      let damage = this.monsterAtk;
      let actualDamage = Math.max(1, damage - this.roleDef); // 至少1伤害
      this.heroHp = Math.max(0, this.heroHp - actualDamage);
      this.battleLog.push(`怪物攻击了你，造成 ${actualDamage} 点伤害！`);
      this.triggerHitEffect('hero');
      this.checkBattleEnd();
      if (!this.battleResult) {
        this.isMyTurn = true;
        this.canOperate = true;
      }
    },
    checkBattleEnd() {
      if (this.monsterHp <= 0 && this.heroHp > 0) {
        this.battleResult = "胜利！你击败了怪物！";
        this.canOperate = false;
        this.syncResultToServer(); // 预留同步接口
      } else if (this.heroHp <= 0 && this.monsterHp > 0) {
        this.battleResult = "失败！你被怪物击败了！";
        this.canOperate = false;
        this.syncResultToServer(); // 预留同步接口
      } else if (this.monsterHp <= 0 && this.heroHp <= 0) {
        this.battleResult = "平局！你和怪物同归于尽！";
        this.canOperate = false;
        this.syncResultToServer(); // 预留同步接口
      }
    },
    restartBattle() {
      this.fetchMonster();
      // 重新设置血量
      this.heroHp = this.roleHp;
    },
    syncResultToServer() {
      // 这里预留同步接口，如根据 this.userRole.id 更新属性等
      // axios.post('http://localhost:8080/api/roles/update', { ... })
      //   .then(res => { ... })
      //   .catch(err => { ... });
      // 此处留空
      console.log('同步战斗结果到服务器（预留接口）');
    }
  }
}
</script>

<style scoped>
/* 角色受击振动动画 */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-8px) rotate(-2deg); }
  75% { transform: translateX(8px) rotate(2deg); }
}
/* 苹果风格液态玻璃奖励弹窗 */
.rewards-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0.9);
  width: 85%;
  max-width: 420px;
  padding: 32px 28px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.15);
  z-index: 1000;
  transition: all 0.3s ease;
  opacity: 0;
  visibility: hidden;
}

.rewards-popup.active {
  opacity: 1;
  visibility: visible;
  transform: translate(-50%, -50%) scale(1);
}

.popup-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.popup-content h2 {
  font-size: 1.8rem;
  font-weight: 600;
  color: #222;
  margin-bottom: 24px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 奖励弹窗样式 */
.rewards-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s, visibility 0.3s;
}

.rewards-popup.active {
  opacity: 1;
  visibility: visible;
}

.popup-content {
  background-color: #1a1a1a;
  border: 2px solid #4a4a4a;
  border-radius: 10px;
  padding: 20px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
  transform: scale(0.9);
  transition: transform 0.3s;
}

.rewards-popup.active .popup-content {
  transform: scale(1);
}

.rewards-list {
  margin: 20px 0;
  max-height: 300px;
  overflow-y: auto;
}

.relic-item {
  background-color: #2a2a2a;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  border-left: 5px solid;
  transition: transform 0.2s;
}

.relic-item:hover {
  transform: translateX(5px);
}

.relic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.relic-name {
  font-size: 1.2em;
  font-weight: bold;
  color: #fff;
}

.relic-rarity {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.8em;
  font-weight: bold;
}

.relic-common .relic-rarity { background-color: #888; }
.relic-uncommon .relic-rarity { background-color: #4caf50; }
.relic-rare .relic-rarity { background-color: #2196f3; }
.relic-boss .relic-rarity { background-color: #f44336; }

.relic-common { border-left-color: #888; }
.relic-uncommon { border-left-color: #4caf50; }
.relic-rare { border-left-color: #2196f3; }
.relic-boss { border-left-color: #f44336; }

.relic-stats {
  margin-bottom: 10px;
}

.stat-row {
  display: flex;
  margin-bottom: 5px;
}

.stat-label {
  width: 40px;
  color: #aaa;
}

.stat-value {
  margin-left: 10px;
}

.positive { color: #4caf50; }
.negative { color: #f44336; }

.relic-desc {
  color: #ccc;
  font-size: 0.9em;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #444;
}

.btn-claim {
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  font-size: 1em;
  cursor: pointer;
  width: 100%;
  transition: background-color 0.2s;
}

.btn-claim:hover {
  background-color: #0d8bf2;
}

.btn-claim i {
  margin-right: 5px;
}

/* 苹果风格液态玻璃效果 */
.escape-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  max-width: 400px;
  padding: 32px 28px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.15);
  z-index: 1000;
  transition: all 0.3s ease;
  opacity: 0;
  visibility: hidden;
  transform: translate(-50%, -50%) scale(0.9);
}

.escape-popup.active {
  opacity: 1;
  visibility: visible;
  transform: translate(-50%, -50%) scale(1);
  transform: translate(-50%, -50%) scale(1);
}

.escape-popup h2 {
  font-size: 1.8rem;
  font-weight: 600;
  color: #222;
  text-align: center;
  margin-bottom: 24px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.escape-popup button {
  width: 100%;
  padding: 12px 16px;
  margin-top: 16px;
  font-size: 1.1rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #222;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(10px);
}

.escape-popup button:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.shake-effect {
  animation: shake 0.3s ease-in-out;
}
.main-content {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.27);
  box-shadow: 0 2px 16px 0 rgba(0,0,0,0.10);
  backdrop-filter: blur(18px) saturate(160%);
  -webkit-backdrop-filter: blur(18px) saturate(160%);
  z-index: 20;
  border-bottom: 1.5px solid rgba(255,255,255,0.18);
}
.navbar-title {
  font-size: 1.38rem;
  font-weight: 600;
  color: #222;
  letter-spacing: 0.04em;
  text-shadow: 0 2px 8px rgba(0,0,0,0.10);
}
.broadcast-log {
  position: fixed;
  top: 56px;
  left: 50%;
  transform: translateX(-50%);
  width: 420px;
  max-width: 94vw;
  background: rgba(18,18,18,0.56);
  color: #fff;
  border-radius: 12px;
  padding: 12px 16px 6px 18px;
  font-size: 1.08rem;
  z-index: 25;
  box-shadow: 0 3px 16px 2px rgba(0,0,0,0.10);
  max-height: 100px;
  overflow-y: auto;
  margin-bottom: 8px;
  text-align: left;
}
.background-video {
  position: absolute;
  top: 0;
  left: 0;
  min-width: 100%;
  min-height: 100%;
  width: auto;
  height: auto;
  z-index: 0;
  object-fit: cover;
}
.battle-arena {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding-top: 92px;
  padding-bottom: 140px;
  height: 60vh;
  min-height: 420px;
}
.side {
  width: 40%;
  min-width: 220px;
  display: flex;
  justify-content: center;
  align-items: flex-end;
}
.left .role-card,
.right .monster-card {
  background: rgba(255,255,255,0.18);
  border-radius: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.08);
  padding: 22px 16px 14px 16px;
  color: #222;
  min-width: 170px;
  text-align: left;
  backdrop-filter: blur(10px) saturate(140%);
  -webkit-backdrop-filter: blur(10px) saturate(140%);
  position: relative;
}
.avatar {
  width: 68px;
  height: 68px;
  background: #e7e7e9;
  border-radius: 50%;
  margin-bottom: 8px;
  border: 2px solid #fff;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.hp-bar {
  width: 100%;
  height: 15px;
  background: #e3e3e3;
  border-radius: 8px;
  margin-bottom: 8px;
  overflow: hidden;
  border: 1.5px solid #bdbdbd;
  box-sizing: border-box;
}
.hp-bar-inner {
  height: 100%;
  background: linear-gradient(90deg, #ff6868, #f6dd52 90%);
  transition: width 0.3s;
}
.hp-bar-inner.monster {
  background: linear-gradient(90deg, #61d6b7, #5cafff 90%);
}
.role-info, .monster-info {
  font-size: 1rem;
  line-height: 1.7;
}
.result-overlay {
  position: fixed;
  left: 0; right: 0; top: 0; bottom: 0;
  background: rgba(0,0,0,0.45);
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
}
.result-box {
  background: #fff;
  border-radius: 18px;
  padding: 40px 32px 32px 32px;
  box-shadow: 0 2px 24px 2px rgba(0,0,0,0.16);
  text-align: center;
  min-width: 240px;
}
.result-box h2 {
  color: #222;
  font-size: 2rem;
  margin-bottom: 24px;
}
.result-box button {
  padding: 9px 28px;
  font-size: 1.13rem;
  border-radius: 10px;
  background: #3bb87f;
  color: #fff;
  border: none;
  cursor: pointer;
  margin-top: 8px;
}
.dock-bar {
  position: fixed;
  left: 50%;
  bottom: 32px;
  transform: translateX(-50%);
  display: flex;
  gap: 32px;
  padding: 18px 36px;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.27);
  box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.12),
  0 1.5px 8px 0 rgba(0, 0, 0, 0.07);
  backdrop-filter: blur(18px) saturate(160%);
  -webkit-backdrop-filter: blur(18px) saturate(160%);
  z-index: 10;
  border: 1.5px solid rgba(255,255,255,0.3);
}
.dock-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(255,255,255,0.60);
  border: none;
  border-radius: 18px;
  padding: 10px 18px;
  font-size: 1.14rem;
  color: #222;
  cursor: pointer;
  box-shadow: 0 2px 8px 0 rgba(0,0,0,0.06);
  transition:
      transform 0.16s cubic-bezier(.4,2.3,.3,1),
      background 0.2s;
  backdrop-filter: blur(8px) saturate(120%);
  -webkit-backdrop-filter: blur(8px) saturate(120%);
  outline: none;
}
.dock-btn:active {
  transform: scale(0.96);
  background: rgba(255,255,255,0.86);
}
.dock-btn .icon {
  font-size: 1.6rem;
  margin-bottom: 4px;
}
.dock-btn .label {
  font-size: 0.96rem;
  font-weight: 500;
  letter-spacing: 0.02em;
}
@media (max-width: 600px) {
  .navbar {
    height: 44px;
  }
  .navbar-title {
    font-size: 1.08rem;
  }
  .broadcast-log {
    width: 98vw;
    min-width: unset;
    left: 1vw;
    padding: 8px 8px 4px 10px;
    font-size: 0.95rem;
    border-radius: 7px;
  }
  .battle-arena {
    flex-direction: column;
    align-items: center;
    padding-top: 68px;
    min-height: 300px;
    height: 48vh;
    padding-bottom: 96px;
  }
  .side {
    width: 100%;
    margin-bottom: 16px;
    min-width: 120px;
  }
  .result-box {
    min-width: 128px;
    padding: 20px 10px 14px 10px;
  }
  .dock-bar {
    gap: 12px;
    padding: 10px 3px;
    border-radius: 16px;
  }
  .dock-btn {
    font-size: 0.98rem;
    padding: 7px 7px;
    border-radius: 10px;
  }
  .dock-btn .icon {
    font-size: 1.2rem;
  }
  .hp-bar {
    height: 11px;
  }
}
</style>