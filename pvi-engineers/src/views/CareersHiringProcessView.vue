<script setup>
import { hiringFlow } from '../data/careersData'
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">Careers • Hiring Process</p>
      <h1 class="page-title">How Our Hiring Process Works</h1>
      <p class="page-copy">
        We keep recruitment transparent, quick, and role-focused so candidates clearly understand
        every stage.
      </p>
    </section>

    <section class="mt-10">
      <v-row dense>
        <v-col v-for="step in hiringFlow" :key="step.title" cols="12" md="6">
          <v-card class="glass-card pa-6 h-100">
            <p class="step-label">{{ step.step }}</p>
            <h3 class="card-title mt-2">{{ step.title }}</h3>
            <p class="card-copy">{{ step.detail }}</p>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <v-card class="glass-card pa-8 mt-12 mb-8">
      <h2 class="card-title">What to prepare before applying</h2>
      <v-list bg-color="transparent" class="px-0 mt-3">
        <v-list-item prepend-icon="mdi-file-account-outline" class="px-0">
          <v-list-item-title class="list-copy">Updated CV with relevant project experience</v-list-item-title>
        </v-list-item>
        <v-list-item prepend-icon="mdi-briefcase-check-outline" class="px-0">
          <v-list-item-title class="list-copy">Role-fit summary and expected notice period</v-list-item-title>
        </v-list-item>
        <v-list-item prepend-icon="mdi-comment-question-outline" class="px-0">
          <v-list-item-title class="list-copy">Questions about team, growth path, or work model</v-list-item-title>
        </v-list-item>
      </v-list>

      <div class="mt-4 d-flex ga-3 flex-wrap">
        <v-btn color="primary" to="/careers/open-positions">Go to open positions</v-btn>
        <v-btn color="secondary" variant="tonal" href="mailto:careers@pviengineers.com?subject=Hiring%20Process%20Query">
          Ask a question
        </v-btn>
      </div>
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

.step-label {
  margin: 0;
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.11em;
  font-size: 0.74rem;
  font-weight: 700;
}

.card-title {
  margin: 0;
  color: var(--text-card-title);
  font-size: 1.18rem;
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.72;
}

.list-copy {
  color: var(--text-card-copy);
  white-space: normal;
}
</style>
