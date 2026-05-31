<script setup>
import { companyButtons, serviceButtons } from '../data/siteNavigation'
import { homeHeroImage, homeWorkGallery } from '../data/siteMedia'
</script>

<template>
  <v-container class="page-shell">
    <v-row class="hero align-center" dense>
      <v-col cols="12" md="7">
        <v-chip color="primary" variant="outlined" class="mb-5">Futuristic Civil Engineering Website</v-chip>
        <h1 class="page-title">
          PVI ENGINEERS
          <span class="gradient-text">Road, Water, Drainage, and Infrastructure Specialists</span>
        </h1>
        <p class="page-copy">
          Explore every practice area and company section directly from the dropdown navigation.
          Every main button and dropdown option now includes a dedicated page with rich visual
          content.
        </p>
        <div class="mt-7 d-flex ga-3 flex-wrap">
          <v-btn color="primary" size="large" to="/contact">Start a Project Discussion</v-btn>
          <v-btn color="secondary" size="large" variant="tonal" to="/our-work">See Our Work</v-btn>
        </div>
      </v-col>

      <v-col cols="12" md="5">
        <v-card class="glass-card hero-media-card pa-3">
          <v-img :src="homeHeroImage" class="hero-media-image" cover height="290">
            <div class="hero-image-overlay">
              <p class="hero-image-kicker">Engineering Visual</p>
              <p class="hero-image-copy">Designing infrastructure that supports growth and resilience.</p>
            </div>
          </v-img>
        </v-card>
      </v-col>
    </v-row>

    <section class="mt-16">
      <p class="section-kicker">Main service buttons</p>
      <h2 class="section-title">Engineering service domains</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="item in serviceButtons" :key="item.path" cols="12" md="4">
          <v-card class="glass-card pa-6 h-100">
            <v-icon :icon="item.icon" color="primary" size="30" class="mb-4" />
            <h3 class="card-title">{{ item.label }}</h3>
            <p class="card-copy">{{ item.intro }}</p>
            <div class="mt-5 d-flex flex-wrap ga-2">
              <v-chip
                v-for="entry in item.children"
                :key="entry.path"
                :to="entry.path"
                color="info"
                variant="tonal"
                size="small"
              >
                {{ entry.label }}
              </v-chip>
            </div>
            <v-btn :to="item.path" class="mt-5" color="primary" variant="flat">Open Main Page</v-btn>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="mt-16">
      <p class="section-kicker">Featured visual projects</p>
      <h2 class="section-title">Infrastructure snapshots inspired by your project domains</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="work in homeWorkGallery" :key="work.title" cols="12" md="4">
          <v-card class="glass-card h-100">
            <v-img :src="work.image" cover height="198" />
            <div class="pa-5">
              <h3 class="card-title">{{ work.title }}</h3>
              <p class="card-copy">{{ work.location }}</p>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="mt-16 mb-8">
      <p class="section-kicker">Main company buttons</p>
      <h2 class="section-title">Company and support sections</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="item in companyButtons" :key="item.path" cols="12" md="4">
          <v-card class="glass-card pa-6 h-100">
            <v-icon :icon="item.icon" color="secondary" size="30" class="mb-4" />
            <h3 class="card-title">{{ item.label }}</h3>
            <p class="card-copy">{{ item.intro }}</p>
            <div class="mt-5 d-flex flex-wrap ga-2">
              <v-chip
                v-for="entry in item.children"
                :key="entry.path"
                :to="entry.path"
                color="secondary"
                variant="tonal"
                size="small"
              >
                {{ entry.label }}
              </v-chip>
            </div>
            <v-btn :to="item.path" class="mt-5" color="secondary" variant="flat">Open Main Page</v-btn>
          </v-card>
        </v-col>
      </v-row>
    </section>
  </v-container>
</template>

<style scoped>
.page-shell {
  position: relative;
  z-index: 1;
  padding-top: 42px;
  padding-bottom: 42px;
}

.hero {
  min-height: 320px;
}

.section-kicker {
  margin: 0;
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.13em;
  font-size: 0.72rem;
  font-weight: 700;
}

.page-title,
.section-title {
  margin: 10px 0 0;
  color: var(--text-title);
  line-height: 1.16;
}

.page-title {
  font-size: clamp(2.1rem, 4.8vw, 3.5rem);
  max-width: 17ch;
}

.gradient-text {
  display: block;
  margin-top: 8px;
  background: var(--home-gradient-text);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.section-title {
  font-size: clamp(1.5rem, 3.2vw, 2.2rem);
  max-width: 28ch;
}

.page-copy {
  margin: 18px 0 0;
  color: var(--text-copy);
  line-height: 1.75;
  max-width: 72ch;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
  overflow: hidden;
}

.hero-media-card {
  backdrop-filter: blur(7px);
}

.hero-media-image {
  border-radius: 14px;
  overflow: hidden;
}

.hero-image-overlay {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 16px;
  background: linear-gradient(180deg, transparent 45%, rgba(0, 0, 0, 0.45) 100%);
}

.hero-image-kicker {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.7rem;
  color: rgba(214, 237, 255, 0.92);
}

.hero-image-copy {
  margin: 6px 0 0;
  color: #eaf5ff;
  font-size: 0.95rem;
  line-height: 1.4;
}

.card-title {
  margin: 0;
  color: var(--text-card-title);
  font-size: 1.15rem;
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.68;
}
</style>
