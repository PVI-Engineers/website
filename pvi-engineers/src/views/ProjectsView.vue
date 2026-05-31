<script setup>
const projectPortfolio = [
  {
    category: 'Road Design',
    title: 'Eastern Mobility Corridor Upgrade',
    location: 'Bengaluru Metropolitan Region',
    challenge:
      'Heavy peak-hour congestion and inconsistent lane geometry were causing safety bottlenecks across key junctions.',
    outcome:
      'Delivered a redesigned 38 km corridor package with optimized intersections, improved turning radii, and safer pedestrian zones.',
    metrics: ['31% better corridor throughput', '22% fewer conflict points', '24-month phased rollout plan'],
  },
  {
    category: 'Water Management',
    title: 'Integrated Water Balance Program',
    location: 'Coastal Industrial Township',
    challenge:
      'Rapid growth created unstable demand patterns, pressure loss, and strain on existing storage and pumping infrastructure.',
    outcome:
      'Engineered a digital water network model with demand zoning, dynamic storage planning, and pumping optimization.',
    metrics: ['18% reduced network losses', '27% improved distribution reliability', '15-year demand alignment'],
  },
  {
    category: 'Drainage',
    title: 'Urban Stormwater Resilience Masterplan',
    location: 'Tier-1 Smart City Expansion Zone',
    challenge:
      'Recurring monsoon flooding due to undersized drainage paths, poor outfall connectivity, and uncoordinated urban development.',
    outcome:
      'Designed a climate-responsive drainage strategy with retention systems, upgraded channels, and flood-priority corridors.',
    metrics: ['40% lower flood exposure area', '3x faster peak runoff clearance', 'City-wide drainage design standards'],
  },
]

const deliveryHighlights = [
  { value: '260+', label: 'Technical packages delivered' },
  { value: '14 states', label: 'Regional experience footprint' },
  { value: '96%', label: 'Design approval confidence' },
  { value: '1.8M+', label: 'Population impact across projects' },
]
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">Projects</p>
      <h1 class="page-title">Infrastructure Programs We Have Engineered</h1>
      <p class="page-copy">
        From multi-lane road corridors to city-scale drainage systems, our projects reflect a
        design-first mindset focused on resilience, performance, and practical implementation.
      </p>
    </section>

    <v-row class="mt-6" dense>
      <v-col v-for="item in projectPortfolio" :key="item.title" cols="12">
        <v-card class="glass-card pa-6">
          <v-row>
            <v-col cols="12" md="3">
              <v-chip color="primary" variant="tonal" class="mb-4">{{ item.category }}</v-chip>
              <h2 class="card-title">{{ item.title }}</h2>
              <p class="location mt-3">
                <v-icon icon="mdi-map-marker-radius-outline" size="18" class="mr-1" />
                {{ item.location }}
              </p>
            </v-col>

            <v-col cols="12" md="5">
              <p class="mini-kicker">Challenge</p>
              <p class="card-copy">{{ item.challenge }}</p>
              <p class="mini-kicker mt-5">Outcome</p>
              <p class="card-copy">{{ item.outcome }}</p>
            </v-col>

            <v-col cols="12" md="4">
              <p class="mini-kicker">Impact Metrics</p>
              <div class="metric-stack">
                <div v-for="result in item.metrics" :key="result" class="metric-pill">
                  <v-icon icon="mdi-trending-up" size="16" color="secondary" />
                  <span>{{ result }}</span>
                </div>
              </div>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <section class="mt-16">
      <p class="section-kicker">Delivery strength</p>
      <h2 class="section-title">Performance indicators across our engineering portfolio</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="item in deliveryHighlights" :key="item.label" cols="12" md="3" sm="6">
          <v-card class="glass-card pa-5 h-100 text-center">
            <p class="highlight-value">{{ item.value }}</p>
            <p class="highlight-label">{{ item.label }}</p>
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
  padding-top: 44px;
  padding-bottom: 44px;
}

.section-kicker,
.mini-kicker {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.72rem;
  font-weight: 700;
}

.section-kicker {
  color: var(--text-kicker);
}

.mini-kicker {
  color: var(--text-copy);
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
  max-width: 80ch;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
}

.card-title {
  margin: 0;
  color: var(--text-card-title);
  font-size: 1.35rem;
}

.location {
  margin: 0;
  color: var(--text-path);
  display: inline-flex;
  align-items: center;
}

.card-copy {
  margin: 10px 0 0;
  color: var(--text-card-copy);
  line-height: 1.7;
}

.metric-stack {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.metric-pill {
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid var(--surface-card-border);
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-card-copy);
}

.highlight-value {
  margin: 0;
  font-size: 1.8rem;
  color: var(--text-kicker);
  font-weight: 700;
}

.highlight-label {
  margin: 10px 0 0;
  color: var(--text-card-copy);
}
</style>
