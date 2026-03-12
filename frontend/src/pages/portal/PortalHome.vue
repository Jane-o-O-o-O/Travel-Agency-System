<template>
  <div class="portal-home">
    <section class="hero">
      <h1 class="hero-title">探索你的下一站</h1>
      <p class="hero-desc">时拾纪旅行 · 非遗、亲子、摄影主题定制，专业行程规划</p>
      <router-link to="/portal/submit-ticket" class="hero-cta">提交出行意向</router-link>
    </section>

    <section class="section">
      <h2 class="section-title">
        <span class="section-icon">📢</span>
        近期推荐
      </h2>
      <div class="ad-grid">
        <div v-for="(ad, i) in ads" :key="i" class="ad-card">
          <div class="ad-badge">{{ ad.badge }}</div>
          <div class="ad-image" :style="{ background: ad.bg }">{{ ad.emoji }}</div>
          <div class="ad-body">
            <h3 class="ad-title">{{ ad.title }}</h3>
            <p class="ad-desc">{{ ad.desc }}</p>
            <div class="ad-meta">
              <span>{{ ad.days }}</span>
              <span>{{ ad.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section tips">
      <h2 class="section-title">
        <span class="section-icon">💡</span>
        使用说明
      </h2>
      <div class="tips-list">
        <p>登录后可在「提交工单」中填写意向主题、出行人数与天数、期望日期及特殊要求，我们会尽快与您联系。</p>
        <p>在「我的工单」中可查看进度与客服回复。</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const ads = ref([
  {
    badge: '非遗',
    title: '闽南非遗文化体验之旅',
    desc: '漳州土楼、泉州提线木偶、德化白瓷手作，5日深度体验',
    days: '5天4晚',
    price: '¥3,680起',
    emoji: '🏮',
    bg: 'linear-gradient(135deg, #d4a574 0%, #e07a5f 100%)'
  },
  {
    badge: '亲子',
    title: '海南亲子海岛度假',
    desc: '沙滩、海洋馆、热带植物园，适合 3–12 岁家庭',
    days: '4天3晚',
    price: '¥2,980起',
    emoji: '🏖️',
    bg: 'linear-gradient(135deg, #4299e1 0%, #48bb78 100%)'
  },
  {
    badge: '摄影',
    title: '云南元阳梯田摄影采风',
    desc: '日出云海、哈尼村落、多依树与坝达经典机位',
    days: '6天5晚',
    price: '¥4,200起',
    emoji: '📷',
    bg: 'linear-gradient(135deg, #1a365d 0%, #2d3748 100%)'
  },
  {
    badge: '热卖',
    title: '西安历史文化研学',
    desc: '兵马俑、陕西历史博物馆、城墙骑行、美食街区',
    days: '4天3晚',
    price: '¥2,580起',
    emoji: '🏛️',
    bg: 'linear-gradient(135deg, #805ad5 0%, #e07a5f 100%)'
  }
])
</script>

<style scoped>
.portal-home { padding: 0 0 40px; }
.hero {
  text-align: center;
  padding: 48px 24px;
  background: linear-gradient(135deg, var(--voyage-navy) 0%, #0f2744 100%);
  color: var(--voyage-pearl);
  border-radius: 12px;
  margin-bottom: 40px;
  border: 2px solid var(--voyage-sand);
}
.hero-title {
  font-family: 'Playfair Display', serif;
  font-size: 32px;
  margin: 0 0 12px;
}
.hero-desc { font-size: 15px; opacity: 0.9; margin: 0 0 24px; }
.hero-cta {
  display: inline-block;
  padding: 12px 28px;
  background: var(--voyage-sunset);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: transform 0.2s;
}
.hero-cta:hover { transform: translateY(-2px); }
.section { margin-bottom: 40px; }
.section-title {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  color: var(--voyage-navy);
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.section-icon { font-size: 26px; }
.ad-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.ad-card {
  background: white;
  border: 2px solid var(--voyage-sand);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px var(--voyage-shadow);
  transition: transform 0.2s;
}
.ad-card:hover { transform: translateY(-4px); }
.ad-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: var(--voyage-sunset);
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  z-index: 1;
}
.ad-card { position: relative; }
.ad-image {
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 56px;
}
.ad-body { padding: 16px; }
.ad-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--voyage-navy);
  margin: 0 0 8px;
}
.ad-desc { font-size: 13px; color: var(--voyage-ink); margin: 0 0 12px; line-height: 1.5; }
.ad-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--voyage-sunset);
  font-weight: 600;
}
.tips-list {
  background: white;
  border: 2px solid var(--voyage-sand);
  border-radius: 12px;
  padding: 24px;
  color: var(--voyage-ink);
  line-height: 1.8;
}
.tips-list p { margin: 0 0 12px; }
.tips-list p:last-child { margin-bottom: 0; }
</style>
