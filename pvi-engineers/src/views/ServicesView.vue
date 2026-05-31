<script setup>
const serviceBlocks = [
  {
    icon: 'mdi-highway',
    title: 'Road Design & Mobility Corridors',
    summary:
      'End-to-end roadway engineering focused on safety, operational flow, and future traffic demand.',
    offerings: [
      'Geometric design for highways and urban roads',
      'Pavement design and rehabilitation strategies',
      'Junction and interchange optimization',
      'Road safety audits and accessibility planning',
    ],
  },
  {
    icon: 'mdi-water-pump',
    title: 'Integrated Water Management',
    summary:
      'Comprehensive planning and system design to secure dependable, efficient, and sustainable water networks.',
    offerings: [
      'Water supply network planning',
      'Reservoir and pumping system modeling',
      'Demand forecasting and balancing studies',
      'Water conservation and recharge integration',
    ],
  },
  {
    icon: 'mdi-weather-rainy',
    title: 'Drainage & Stormwater Engineering',
    summary:
      'Data-driven stormwater and flood mitigation solutions for urban and peri-urban developments.',
    offerings: [
      'Stormwater drainage layout design',
      'Hydrologic and hydraulic simulations',
      'Flood risk zoning and resilience planning',
      'Outfall and retention system design',
    ],
  },
]

const differentiators = [
  {
    icon: 'mdi-brain',
    title: 'AI-Assisted Design Intelligence',
    detail:
      'We evaluate alternatives rapidly through simulation-ready workflows to deliver faster and stronger decisions.',
  },
  {
    icon: 'mdi-map-search-outline',
    title: 'GIS-Centric Planning',
    detail:
      'Spatial analytics and terrain-informed planning help us design systems that fit real-world conditions.',
  },
  {
    icon: 'mdi-file-document-multiple-outline',
    title: 'Execution-Ready Documentation',
    detail:
      'From concept notes to final drawings and quantities, every output is prepared for practical deployment.',
  },
]

const sectors = [
  'Urban Infrastructure',
  'Industrial Corridors',
  'Smart City Programs',
  'Municipal Upgrades',
  'Regional Development Authorities',
  'Transit Support Networks',
]
</script>

<template>
  <v-container class="page-shell">
    <section class="hero-block">
      <p class="section-kicker">Services</p>
      <h1 class="page-title">Engineering Services Built for High-Impact Infrastructure</h1>
      <p class="page-copy">
        PVI ENGINEERS delivers specialized civil engineering services in roads, water systems, and
        drainage infrastructure. Our approach combines technical depth with digital modeling to
        produce resilient, future-ready outcomes.
      </p>
    </section>

    <v-row class="mt-4" dense>
      <v-col v-for="item in serviceBlocks" :key="item.title" cols="12" md="4">
        <v-card class="glass-card pa-6 h-100">
          <v-icon :icon="item.icon" color="primary" size="34" class="mb-4" />
          <h2 class="card-title">{{ item.title }}</h2>
          <p class="card-copy">{{ item.summary }}</p>
          <v-divider class="my-4" />
          <v-list bg-color="transparent" class="px-0">
            <v-list-item
              v-for="entry in item.offerings"
              :key="entry"
              prepend-icon="mdi-check-circle-outline"
              class="px-0"
            >
              <v-list-item-title class="list-copy">{{ entry }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
    </v-row>

    <section class="mt-16">
      <p class="section-kicker">Why clients choose us</p>
      <h2 class="section-title">A modern delivery model for complex civil systems</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="item in differentiators" :key="item.title" cols="12" md="4">
          <v-card class="glass-card pa-6 h-100">
            <v-icon :icon="item.icon" color="secondary" size="32" class="mb-4" />
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-copy">{{ item.detail }}</p>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="mt-16">
      <p class="section-kicker">Focus sectors</p>
      <h2 class="section-title">Supporting public and private infrastructure ecosystems</h2>
      <v-card class="glass-card pa-6 mt-4">
        <div class="d-flex flex-wrap ga-2">
          <v-chip
            v-for="sector in sectors"
            :key="sector"
            color="info"
            variant="tonal"
            size="large"
          >
            {{ sector }}
          </v-chip>
        </div>
      </v-card>
    </section>
  </v-container>
</template>

<style scoped>
.page-shell {
  position: relative;
  z-index: 1;
  padding-top: 44px;
  padding-bottom: 44px;
}

.hero-block {
  max-width: 80ch;
}

.section-kicker {
  margin: 0;
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.14em;
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
  font-size: clamp(2rem, 4vw, 3.1rem);
  max-width: 20ch;
}

.section-title {
  font-size: clamp(1.5rem, 3.1vw, 2.2rem);
  max-width: 25ch;
}

.page-copy {
  margin: 18px 0 0;
  color: var(--text-copy);
  line-height: 1.78;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
  backdrop-filter: blur(8px);
}

.card-title {
  margin: 0;
  font-size: 1.2rem;
  color: var(--text-card-title);
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.7;
}

.list-copy {
  color: var(--text-card-copy);
  white-space: normal;
  line-height: 1.5;
}
</style>
