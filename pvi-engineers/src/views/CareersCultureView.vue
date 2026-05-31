<script setup>
import { cultureHighlights } from '../data/careersData'
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">Careers • Work Culture & Benefits</p>
      <h1 class="page-title">What It’s Like to Work at PVI ENGINEERS</h1>
      <p class="page-copy">
        We combine high ownership, technical depth, and supportive team culture to help engineers
        do their best work.
      </p>
    </section>

    <section class="mt-10">
      <v-row dense>
        <v-col v-for="item in cultureHighlights" :key="item.title" cols="12" md="6">
          <v-card class="glass-card pa-6 h-100">
            <v-icon :icon="item.icon" color="primary" size="30" class="mb-4" />
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-copy">{{ item.detail }}</p>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <v-card class="glass-card pa-8 mt-12 mb-8">
      <h2 class="card-title">Employee support highlights</h2>
      <v-row class="mt-3" dense>
        <v-col cols="12" md="4">
          <div class="support-tile">
            <p class="tile-title">Mentorship</p>
            <p class="tile-copy">Role-based technical guidance and review support from experienced leads.</p>
          </div>
        </v-col>
        <v-col cols="12" md="4">
          <div class="support-tile">
            <p class="tile-title">Learning</p>
            <p class="tile-copy">Continuous upskilling across design tools, standards, and delivery methods.</p>
          </div>
        </v-col>
        <v-col cols="12" md="4">
          <div class="support-tile">
            <p class="tile-title">Growth</p>
            <p class="tile-copy">Clear progression expectations aligned to performance and responsibility.</p>
          </div>
        </v-col>
      </v-row>
      <v-btn to="/careers/open-positions" color="primary" class="mt-5">Explore openings</v-btn>
    </v-card>
  </v-container>
</template>

<style scoped>
.page-shell {
  position: relative;
  z-index: 1;
  padding-top: 44px;
  padding-bottom: 44px;
}

.section-kicker {
  margin: 0;
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.72rem;
  font-weight: 700;
}

.page-title {
  margin: 10px 0 0;
  color: var(--text-title);
  font-size: clamp(2rem, 4vw, 3.1rem);
  line-height: 1.2;
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
  font-size: 1.16rem;
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.72;
}

.support-tile {
  border: 1px solid var(--surface-card-border);
  border-radius: 12px;
  padding: 12px;
}

.tile-title {
  margin: 0;
  color: var(--text-card-title);
  font-weight: 600;
}

.tile-copy {
  margin: 8px 0 0;
  color: var(--text-card-copy);
  line-height: 1.55;
}
</style>
