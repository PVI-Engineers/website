<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { pageMap } from '../data/siteNavigation'

const route = useRoute()

const page = computed(() => pageMap[route.path] ?? null)

const colorPairs = [
  ['#53d8ff', '#89f2d7'],
  ['#6da2ff', '#77d4ff'],
  ['#9b88ff', '#66d8ff'],
  ['#53dfa8', '#79d9ff'],
  ['#8ee06a', '#5bc4ff'],
  ['#e9a862', '#73d0ff'],
]

function hashText(input) {
  if (!input) return 0

  let hash = 0
  for (let index = 0; index < input.length; index += 1) {
    hash = (hash << 5) - hash + input.charCodeAt(index)
    hash |= 0
  }

  return Math.abs(hash)
}

const visual = computed(() => {
  const source = page.value?.sectionPath ?? page.value?.path ?? 'pvi'
  const pair = colorPairs[hashText(source) % colorPairs.length]

  return {
    icon: page.value?.icon ?? 'mdi-compass-outline',
    accentA: pair[0],
    accentB: pair[1],
  }
})

const heroStyle = computed(() => ({
  '--hero-a': visual.value.accentA,
  '--hero-b': visual.value.accentB,
}))

function getRelatedPage(path) {
  return pageMap[path] ?? null
}
</script>

<template>
  <v-container class="page-shell">
    <v-card v-if="!page" class="glass-card pa-8">
      <p class="section-kicker">Page not found</p>
      <h1 class="page-title">This section is currently unavailable</h1>
      <p class="page-copy">
        The requested page may have moved. Please use the top navigation menu to continue
        exploring PVI ENGINEERS.
      </p>
      <v-btn class="mt-6" color="primary" to="/">Back to Home</v-btn>
    </v-card>

    <template v-else>
      <section>
        <v-row align="stretch" dense>
          <v-col cols="12" md="7">
            <p class="section-kicker">{{ page.category }}</p>
            <h1 class="page-title">{{ page.title }}</h1>
            <p class="page-copy">{{ page.intro }}</p>
            <div class="mt-5 d-flex ga-2 flex-wrap">
              <v-chip v-if="page.parent" color="secondary" variant="tonal">
                Parent: {{ page.parent.label }}
              </v-chip>
              <v-chip color="info" variant="outlined">PVI ENGINEERS</v-chip>
            </div>
          </v-col>

          <v-col cols="12" md="5">
            <v-card class="hero-visual h-100 pa-6" :style="heroStyle">
              <span class="visual-orb visual-orb-one" aria-hidden="true"></span>
              <span class="visual-orb visual-orb-two" aria-hidden="true"></span>
              <v-img :src="page.heroImage" class="visual-banner mb-4" cover height="168" />
              <div class="visual-icon-wrap">
                <v-icon :icon="visual.icon" class="visual-main-icon" size="44" />
              </div>
              <p class="visual-kicker">Section Visual</p>
              <h2 class="visual-title">{{ page.title }}</h2>
              <p class="visual-path">{{ page.path }}</p>
              <div class="mt-5 d-flex ga-2 flex-wrap">
                <v-chip
                  color="primary"
                  size="small"
                  variant="flat"
                >
                  {{ page.parent ? page.parent.label : 'Top-level section' }}
                </v-chip>
                <v-chip color="secondary" size="small" variant="outlined">Dedicated page</v-chip>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </section>

      <v-row class="mt-7" dense>
        <v-col cols="12" md="6">
          <v-card class="glass-card pa-6 h-100">
            <h2 class="card-title">Focus Areas</h2>
            <v-list bg-color="transparent" class="px-0 mt-2">
              <v-list-item
                v-for="item in page.focusAreas"
                :key="item"
                prepend-icon="mdi-check-circle-outline"
                class="px-0"
              >
                <v-list-item-title class="list-copy">{{ item }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>

        <v-col cols="12" md="6">
          <v-card class="glass-card pa-6 h-100">
            <h2 class="card-title">Typical Deliverables</h2>
            <v-list bg-color="transparent" class="px-0 mt-2">
              <v-list-item
                v-for="item in page.deliverables"
                :key="item"
                prepend-icon="mdi-file-check-outline"
                class="px-0"
              >
                <v-list-item-title class="list-copy">{{ item }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>
      </v-row>

      <section class="mt-16">
        <p class="section-kicker">Related pages</p>
        <h2 class="section-title">Explore connected sections</h2>
        <v-row class="mt-4" dense>
          <v-col v-for="link in page.relatedLinks" :key="link.path" cols="12" md="4">
            <v-card class="glass-card pa-5 h-100 d-flex flex-column justify-space-between">
              <div>
                <div class="d-flex align-start ga-2 mb-2">
                  <v-icon
                    :icon="getRelatedPage(link.path)?.icon ?? 'mdi-arrow-top-right-thin-circle-outline'"
                    color="primary"
                    size="20"
                  />
                  <h3 class="card-title mb-0">{{ link.label }}</h3>
                </div>
                <p class="card-copy mt-0">
                  {{ getRelatedPage(link.path)?.category ?? 'Dedicated section page' }}
                </p>
                <p class="link-path">{{ link.path }}</p>
              </div>
              <div class="mt-5">
                <v-btn :to="link.path" color="primary" variant="tonal">Open Page</v-btn>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </section>

      <v-card class="glass-card pa-8 mt-16 mb-8">
        <v-row align="center">
          <v-col cols="12" md="8">
            <p class="section-kicker mb-2">Need assistance?</p>
            <h2 class="section-title mb-2">Talk to our engineering team</h2>
            <p class="card-copy mt-0">
              Share your project requirements and we will guide you with the right service pathway.
            </p>
          </v-col>
          <v-col cols="12" md="4" class="d-flex justify-md-end">
            <v-btn color="primary" size="large" to="/contact">Contact PVI ENGINEERS</v-btn>
          </v-col>
        </v-row>
      </v-card>
    </template>
  </v-container>
</template>

<style scoped>
.page-shell {
  position: relative;
  z-index: 1;
  padding-top: 44px;
  padding-bottom: 44px;
}

.hero-visual {
  position: relative;
  overflow: hidden;
  background: var(--hero-visual-bg);
  border: 1px solid var(--hero-visual-border);
}

.visual-banner {
  position: relative;
  z-index: 1;
  border-radius: 14px;
  border: 1px solid var(--surface-card-border);
  overflow: hidden;
}

.visual-orb {
  position: absolute;
  border-radius: 999px;
  filter: blur(12px);
}

.visual-orb-one {
  width: 170px;
  height: 170px;
  top: -42px;
  left: -22px;
  background: radial-gradient(circle, var(--hero-a) 0%, transparent 68%);
  opacity: 0.38;
}

.visual-orb-two {
  width: 180px;
  height: 180px;
  right: -48px;
  bottom: -64px;
  background: radial-gradient(circle, var(--hero-b) 0%, transparent 70%);
  opacity: 0.33;
}

.visual-icon-wrap {
  position: relative;
  z-index: 1;
  width: 68px;
  height: 68px;
  border-radius: 20px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--hero-a), var(--hero-b));
  box-shadow: var(--visual-icon-shadow);
}

.visual-main-icon {
  color: var(--visual-icon-color);
}

.visual-kicker {
  position: relative;
  z-index: 1;
  margin: 16px 0 0;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.72rem;
  color: var(--text-copy);
}

.visual-title {
  position: relative;
  z-index: 1;
  margin: 8px 0 0;
  color: var(--text-title);
  font-size: 1.35rem;
  line-height: 1.28;
}

.visual-path {
  position: relative;
  z-index: 1;
  margin: 9px 0 0;
  color: var(--text-path);
  font-size: 0.83rem;
  letter-spacing: 0.04em;
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
  line-height: 1.2;
}

.page-title {
  font-size: clamp(2rem, 4vw, 3rem);
  max-width: 21ch;
}

.section-title {
  font-size: clamp(1.45rem, 3vw, 2.1rem);
  max-width: 26ch;
}

.page-copy {
  margin: 18px 0 0;
  color: var(--text-copy);
  line-height: 1.75;
  max-width: 80ch;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
}

.card-title {
  margin: 0;
  color: var(--text-card-title);
  font-size: 1.12rem;
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.68;
}

.link-path {
  margin: 10px 0 0;
  color: var(--text-path);
  font-size: 0.78rem;
}

.list-copy {
  color: var(--text-card-copy);
  white-space: normal;
  line-height: 1.5;
}
</style>
