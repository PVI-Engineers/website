<script setup>
import { cultureHighlights, hiringFlow, openings } from '../data/careersData'

const quickLinks = [
  { label: 'Open Positions', to: '/careers/open-positions', icon: 'mdi-briefcase-search-outline' },
  { label: 'Hiring Process', to: '/careers/hiring-process', icon: 'mdi-timeline-check-outline' },
  { label: 'Work Culture & Benefits', to: '/careers/work-culture-and-benefits', icon: 'mdi-heart-multiple-outline' },
]
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">Careers</p>
      <h1 class="page-title">Build Your Career in High-Impact Infrastructure Engineering</h1>
      <p class="page-copy">
        PVI ENGINEERS is hiring experienced professionals across road design, water systems,
        drainage, and geospatial engineering. This careers section now provides clear pages for
        openings, hiring process, and culture.
      </p>
      <div class="mt-7 d-flex ga-3 flex-wrap">
        <v-btn color="primary" to="/careers/open-positions">View Open Positions</v-btn>
        <v-btn color="secondary" variant="tonal" to="/careers/hiring-process">Understand Hiring Process</v-btn>
      </div>
    </section>

    <section class="mt-14">
      <p class="section-kicker">Explore career pages</p>
      <h2 class="section-title">Everything candidates need, clearly structured</h2>
      <v-row class="mt-4" dense>
        <v-col v-for="link in quickLinks" :key="link.to" cols="12" md="4">
          <v-card class="glass-card pa-6 h-100 d-flex flex-column justify-space-between">
            <div>
              <v-icon :icon="link.icon" color="primary" size="30" class="mb-4" />
              <h3 class="card-title">{{ link.label }}</h3>
              <p class="card-copy">Open this dedicated page for complete details and next steps.</p>
            </div>
            <v-btn :to="link.to" color="primary" variant="tonal" class="mt-5">Open Page</v-btn>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="mt-16">
      <p class="section-kicker">Current opportunities snapshot</p>
      <h2 class="section-title">Roles we are actively hiring for</h2>
      <v-row class="mt-4" dense>
        <v-col v-for="job in openings.slice(0, 3)" :key="job.role" cols="12" md="4">
          <v-card class="glass-card pa-6 h-100">
            <h3 class="card-title">{{ job.role }}</h3>
            <p class="job-meta">{{ job.location }} • {{ job.type }}</p>
            <p class="card-copy">{{ job.summary }}</p>
          </v-card>
        </v-col>
      </v-row>
      <v-btn to="/careers/open-positions" color="primary" class="mt-6">See All Openings</v-btn>
    </section>

    <section class="mt-16 mb-8">
      <p class="section-kicker">Why join PVI</p>
      <h2 class="section-title">A culture built for engineering growth and project ownership</h2>
      <v-row class="mt-4" dense>
        <v-col v-for="item in cultureHighlights" :key="item.title" cols="12" md="6">
          <v-card class="glass-card pa-6 h-100">
            <v-icon :icon="item.icon" color="primary" size="30" class="mb-4" />
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-copy">{{ item.detail }}</p>
          </v-card>
        </v-col>
      </v-row>

      <v-card class="glass-card pa-6 mt-8">
        <p class="section-kicker mb-2">Hiring process at a glance</p>
        <v-row dense>
          <v-col v-for="step in hiringFlow" :key="step.title" cols="12" md="3">
            <div class="process-item">
              <p class="step-label">{{ step.step }}</p>
              <h4 class="step-title">{{ step.title }}</h4>
            </div>
          </v-col>
        </v-row>
        <v-btn to="/careers/hiring-process" color="secondary" variant="tonal" class="mt-4">
          View Full Hiring Steps
        </v-btn>
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

.section-kicker {
  margin: 0;
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.12em;
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
  max-width: 22ch;
}

.section-title {
  font-size: clamp(1.5rem, 3.1vw, 2.2rem);
  max-width: 28ch;
}

.page-copy {
  margin: 18px 0 0;
  color: var(--text-copy);
  line-height: 1.78;
  max-width: 82ch;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
}

.card-title {
  margin: 0;
  color: var(--text-card-title);
  font-size: 1.15rem;
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.7;
}

.job-meta {
  margin: 8px 0 0;
  color: var(--text-path);
  font-size: 0.88rem;
}

.process-item {
  border: 1px solid var(--surface-card-border);
  border-radius: 12px;
  padding: 12px;
  background: color-mix(in srgb, var(--surface-card-bg) 74%, transparent);
}

.step-label {
  margin: 0;
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.11em;
  font-size: 0.7rem;
}

.step-title {
  margin: 8px 0 0;
  color: var(--text-card-title);
  font-size: 0.98rem;
}
</style>
