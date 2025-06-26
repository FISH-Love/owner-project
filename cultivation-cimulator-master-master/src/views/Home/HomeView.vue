<template>
  <div class="home-container">
    <!-- 视频背景容器 -->
    <div class="video-background">
      <video
          autoplay
          muted
          loop
          playsinline
          class="background-video"
          @error="handleVideoError"
          @loadeddata="handleVideoLoad"
      >
        <source src="@/assets/background-video.mp4" type="video/mp4">
        <img src="@/assets/bg-home.png" alt="背景图片" class="fallback-image">
      </video>
      <div class="video-overlay"></div>
    </div>

    <!-- 背景音乐 -->
    <audio
        ref="bgm"
        :src="require('@/assets/home-music.mp3')"
        autoplay
        loop
        preload="auto"
        @error="handleAudioError"
    />


<!--    &lt;!&ndash; 音乐开关按钮 &ndash;&gt;-->
<!--    <button class="music-toggle" @click="toggleMusic">-->
<!--      {{ isMusicPlaying ? '暂停音乐' : '播放音乐' }}-->
<!--    </button>-->

    <!-- 导航栏 -->
    <nav-bar
        :u_n="user_name"
        :selectedRole="userRole"
        @logout="logout"
    ></nav-bar>

    <!-- 欢迎语 -->
    <div class="main-content">
      <div class="welcome-message">
        <h2>{{ currentQuote.content }}</h2>
        <p>{{ currentQuote.source }}</p>
      </div>
    </div>

    <!-- 底部导航 -->
    <div class="dock-nav">
      <div class="dock-container">
        <div
            class="dock-item"
            :class="{ 'active': $route.name === 'Adventure' }"
            @click="goToAdventure"
            :style="{ opacity: !userRole ? '0.5' : '1', cursor: !userRole ? 'not-allowed' : 'pointer' }"
        >
          <div class="text">冒险</div>
        </div>

        <div
            class="dock-item"
            :class="{ 'active': $route.name === 'Cultivation' }"
            @click="goToCultivation"
            :style="{ opacity: !userRole ? '0.5' : '1', cursor: !userRole ? 'not-allowed' : 'pointer' }"
        >
          <div class="text">修炼</div>
        </div>
      </div>
    </div>

    <!-- 角色选择 -->
    <role-select-modal
        v-if="showRoleModal"
        @confirm="confirmRoleSelection"
        @cancel="closeRoleModal"
        :visible="visible"
    ></role-select-modal>
  </div>
</template>

<script>
import RoleSelectModal from '@/components/RoleSelectModal/RoleSelectModal.vue';
import NavBar from '@/components/NavBar/NavBar.vue';

export default {
  name: 'HomeView',
  components: {
    RoleSelectModal,
    NavBar
  },
  data() {
    return {
      user_name: '',
      showRoleModal: false,
      userRole: null,
      apiLog: '',
      visible: true,
      videoLoaded: false,
      videoError: false,
      isMusicPlaying: true, // 音乐播放状态
      quotes: [
        { content: '天纵羽翔临九霄，万域同辉耀今朝', source: '《神机问道院》-羽翔大帝' },
        { content: '凡柯挥剑破苍冥，诸敌皆陨道途平', source: '《神机问道院》-凡柯大帝' },
        { content: '豪嘉踏碎仙穹路，一令诸神尽伏诛', source: '《神机问道院》-豪嘉大帝' },
        { content: '岐嘉掌灭万古劫，帝威碾碎九重天', source: '《神机问道院》-岐嘉大帝' },
        { content: '洋琉破浪开天阙，帝影横空镇万邪', source: '《神机问道院》-洋琉大帝' },
        { content: '豪璃神辉照八荒，一念众生尽归藏', source: '《神机问道院》-豪璃上神' },
        { content: '超澎神力裂星河，神谕一出天地合', source: '《神机问道院》-超澎上神' },
        { content: '昂邪掌毒覆幽冥，道途染血逆苍生', source: '《神机问道院》-昂邪道徒' },
        { content: '展峥剑指仙途巅，道心峥嵘破万难', source: '《神机问道院》-展峥道徒' },
        { content: '辰桓御道踏苍澜，道威碾世天地颤', source: '《神机问道院》-辰桓道徒' }
      ],
      currentQuote: {}
    };
  },
  created() {
    this.user_name = this.$route.query.user_name || '修仙者';
    const token = localStorage.getItem('token');
    if (!token) {
      this.$router.push('/login');
      return;
    }
    this.checkRoleBinding();
    this.currentQuote = this.quotes[0];
    setInterval(() => this.switchQuote(), 10000);
  },
  mounted() {
    // 尝试自动播放背景音乐
    document.addEventListener('click', this.tryPlayMusicOnce, { once: true });
  },
  methods: {
    switchQuote() {
      const currentIndex = this.quotes.findIndex(q => q.content === this.currentQuote.content);
      this.currentQuote = this.quotes[(currentIndex + 1) % this.quotes.length];
    },
    async checkRoleBinding() {
      try {
        const response = await this.$axios.get(`/${this.user_name}/hasRole`);
        this.visible = !response.data;
        if (!response.data) {
          this.showRoleModal = true;
        } else {
          await this.fetchUserRole();
        }
      } catch (e) {
        alert('获取角色信息失败，请重试');
      }
    },
    async fetchUserRole() {
      try {
        const user = (await this.$axios.post('http://localhost:8080/api/findUserByUname', null, {
          params: { username: this.user_name }
        })).data;
        if (!user?.roleId) throw new Error('用户未绑定角色');
        const role = (await this.$axios.post('http://localhost:8080/api/queryRoleById', null, {
          params: { id: user.roleId }
        })).data;
        this.userRole = role;
      } catch (e) {
        if (e.message.includes('未绑定角色')) this.showRoleModal = true;
        else alert('获取角色信息失败');
      }
    },
    confirmRoleSelection(role) {
      this.userRole = role;
      localStorage.setItem('userRole', JSON.stringify(role));
      this.showRoleModal = false;
      this.bindRoleToUser(role.id);
    },
    closeRoleModal() {
      this.showRoleModal = false;
    },
    async bindRoleToUser(roleId) {
      try {
        const res = await this.$axios.post(
            `http://localhost:8080/api/${this.user_name}/bindRole`,
            { roleId },
            { headers: { 'Content-Type': 'application/json' } }
        );
        if (res.data.code !== 200) alert(res.data.message);
        else alert('角色绑定成功');
      } catch (e) {
        alert('角色绑定失败: ' + (e.response?.data?.message || e.message));
      }
    },
    goToCultivation() {
      if (!this.userRole) return alert('请先选择角色');
      this.$router.push({ name: 'Cultivation', query: { userRole: JSON.stringify(this.userRole) } });
    },
    goToAdventure() {
      if (!this.userRole) return alert('请先选择角色');
      this.$router.push({ name: 'Adventure', query: { userRole: JSON.stringify(this.userRole) } });
    },
    logout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
        this.$router.push('/login');
      }
    },
    handleVideoError() {
      this.videoError = true;
      document.querySelector('.video-background').classList.add('fallback');
    },
    handleVideoLoad() {
      this.videoLoaded = true;
      document.querySelector('.video-background').classList.remove('fallback');
    },
    handleAudioError() {
      console.warn('背景音乐加载失败');
    },
    tryPlayMusicOnce() {
      const bgm = this.$refs.bgm;
      if (bgm && bgm.paused) {
        bgm.play().catch(err => {
          console.warn('自动播放失败：', err);
        });
      }
    },
    toggleMusic() {
      const bgm = this.$refs.bgm;
      if (bgm) {
        this.isMusicPlaying ? bgm.pause() : bgm.play();
        this.isMusicPlaying = !this.isMusicPlaying;
      }
    }
  }
};
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
  z-index: 1;
}
.video-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  overflow: hidden;
  transition: opacity 0.5s ease;
}
.background-video {
  position: absolute;
  top: 0;
  left: 0;
  min-width: 100%;
  min-height: 100%;
  object-fit: cover;
}
.fallback-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.5s ease;
}
.video-background.fallback .fallback-image {
  opacity: 1;
}
.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.7));
  z-index: 1;
}
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 20px 40px;
  max-width: 1200px;
  margin: auto;
  text-align: center;
  z-index: 2;
}
.welcome-message {
  color: white;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}
.welcome-message h2 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}
.welcome-message p {
  font-size: 1.2rem;
  opacity: 0.9;
}
.dock-nav {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  max-width: 600px;
  z-index: 100;
}
.dock-container {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border-radius: 40px;
  padding: 15px 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: space-around;
}
.dock-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
}
.dock-item.active {
  background: rgba(78, 205, 196, 0.2);
  box-shadow: 0 0 15px rgba(78, 205, 196, 0.4);
}
.dock-item.active .text {
  color: #4ecdc4;
  text-shadow: 0 0 10px rgba(78, 205, 196, 0.7);
}
.text {
  font-size: 1.1rem;
  font-weight: 500;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}
.music-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 200;
  background: rgba(0, 0, 0, 0.4);
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}
@media (max-width: 768px) {
  .welcome-message h2 { font-size: 2rem; }
  .welcome-message p { font-size: 1rem; }
  .dock-nav { bottom: 20px; width: 95%; }
  .text { font-size: 1rem; }
}
</style>
