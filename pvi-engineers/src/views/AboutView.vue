<script setup>
const principles = [
  {
    icon: 'mdi-compass-outline',
    title: 'Engineering Integrity',
    detail: 'Every design decision is grounded in standards, technical rigor, and real-world constructability.',
  },
  {
    icon: 'mdi-leaf-circle-outline',
    title: 'Sustainable Impact',
    detail: 'We integrate climate resilience and lifecycle thinking into road, water, and drainage systems.',
  },
  {
    icon: 'mdi-account-supervisor-circle-outline',
    title: 'Client Partnership',
    detail: 'We operate as an extension of your team with transparent communication and proactive solutions.',
  },
]

const milestones = [
  { year: '2018', title: 'PVI ENGINEERS founded', detail: 'Started with a mission to modernize civil design workflows.' },
  { year: '2020', title: 'Expanded into water systems', detail: 'Launched integrated planning services for municipal water infrastructure.' },
  { year: '2023', title: 'Digital modeling practice scaled', detail: 'Adopted simulation-first project delivery for complex city programs.' },
  { year: '2026', title: 'Pan-regional project footprint', detail: 'Serving public and private infrastructure clients across multiple states.' },
]

const leadership = [
  {
    name: 'Er. Priya Varma',
    role: 'Founder & Managing Director',
    focus: 'Infrastructure strategy, quality assurance, and stakeholder delivery.',
  },
  {
    name: 'Er. Nikhil Iyer',
    role: 'Head of Road Engineering',
    focus: 'Corridor design optimization and mobility-focused project execution.',
  },
  {
    name: 'Er. Kavya Menon',
    role: 'Head of Water & Drainage',
    focus: 'Hydraulic design leadership and resilience-driven system planning.',
  },
]
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">About us</p>
      <h1 class="page-title">Designing Essential Infrastructure That Lasts for Generations</h1>
      <p class="page-copy">
        PVI ENGINEERS is a civil engineering company focused on road design, water management, and
        drainage systems. We combine practical engineering expertise with digital modeling to help
        cities build resilient and future-ready infrastructure.
      </p>
    </section>

    <v-row class="mt-10" dense>
      <v-col cols="12" md="6">
        <v-card class="glass-card pa-6 h-100">
          <p class="mini-kicker">Our mission</p>
          <h2 class="card-title mt-2">Enable safe, efficient, and climate-ready infrastructure systems</h2>
          <p class="card-copy">
            We deliver precision engineering solutions that improve mobility, secure water resources,
            and reduce flood risk for communities and businesses.
          </p>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card class="glass-card pa-6 h-100">
          <p class="mini-kicker">Our vision</p>
          <h2 class="card-title mt-2">Be a trusted design partner for next-generation civil infrastructure</h2>
          <p class="card-copy">
            We envision infrastructure planning that is smarter, faster, and more adaptive through
            integrated technology and collaborative engineering.
          </p>
        </v-card>
      </v-col>
    </v-row>

    <section class="mt-16">
      <p class="section-kicker">Core principles</p>
      <h2 class="section-title">The values guiding every engagement</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="item in principles" :key="item.title" cols="12" md="4">
          <v-card class="glass-card pa-6 h-100">
            <v-icon :icon="item.icon" color="primary" size="32" class="mb-4" />
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-copy">{{ item.detail }}</p>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="mt-16">
      <p class="section-kicker">Journey</p>
      <h2 class="section-title">Company milestones</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="item in milestones" :key="item.year" cols="12" md="3" sm="6">
          <v-card class="glass-card pa-5 h-100">
            <p class="milestone-year">{{ item.year }}</p>
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-copy">{{ item.detail }}</p>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="mt-16 mb-8">
      <p class="section-kicker">Leadership</p>
      <h2 class="section-title">Experienced professionals, focused execution</h2>
      <v-row class="mt-3" dense>
        <v-col v-for="member in leadership" :key="member.name" cols="12" md="4">
          <v-card class="glass-card pa-6 h-100">
            <v-avatar color="primary" size="56" class="mb-4">
              <v-icon icon="mdi-account" class="leader-icon" />
            </v-avatar>
            <h3 class="card-title">{{ member.name }}</h3>
            <p class="role">{{ member.role }}</p>
            <p class="card-copy">{{ member.focus }}</p>
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
  max-width: 22ch;
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
  font-size: 1.18rem;
}

.role {
  margin: 6px 0 0;
  color: var(--text-path);
  font-size: 0.88rem;
}

.leader-icon {
  color: var(--visual-icon-color);
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.7;
}

.milestone-year {
  margin: 0 0 12px;
  color: var(--text-kicker);
  font-size: 1.5rem;
  font-weight: 700;
}
</style>
