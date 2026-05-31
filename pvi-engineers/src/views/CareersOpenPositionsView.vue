<script setup>
import { computed, ref } from 'vue'
import { openings } from '../data/careersData'

const selectedDepartment = ref('All Departments')
const selectedLocation = ref('All Locations')
const searchQuery = ref('')

const departmentOptions = computed(() => [
  'All Departments',
  ...Array.from(new Set(openings.map((item) => item.department))),
])

const locationOptions = ['All Locations', 'INDIA', 'USA']

const locationChipLabel = computed(() =>
  selectedLocation.value === 'All Locations' ? 'USA, INDIA' : selectedLocation.value,
)

const filteredOpenings = computed(() =>
  openings.filter((job) => {
    const matchesDepartment =
      selectedDepartment.value === 'All Departments' ||
      job.department === selectedDepartment.value

    const matchesLocation =
      selectedLocation.value === 'All Locations' ||
      job.country === selectedLocation.value

    const query = searchQuery.value.trim().toLowerCase()
    const matchesQuery =
      query.length === 0 ||
      job.role.toLowerCase().includes(query) ||
      job.id.toLowerCase().includes(query) ||
      job.summary.toLowerCase().includes(query) ||
      job.location.toLowerCase().includes(query)

    return matchesDepartment && matchesLocation && matchesQuery
  }),
)
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">Careers • Open Positions</p>
      <h1 class="page-title">Current Openings at PVI ENGINEERS</h1>
      <p class="page-copy">
        Explore role details in a clear MNC-style format including job overview, responsibilities,
        qualifications, preferred skills, tools, and benefits.
      </p>
    </section>

    <v-card class="glass-panel pa-5 mt-8">
      <v-row dense>
        <v-col cols="12" md="4">
          <v-select
            v-model="selectedDepartment"
            :items="departmentOptions"
            density="comfortable"
            hide-details
            label="Filter by Department"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-select
            v-model="selectedLocation"
            :items="locationOptions"
            density="comfortable"
            hide-details
            label="Filter by Country"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="searchQuery"
            clearable
            density="comfortable"
            hide-details
            label="Search by role, job ID, or keyword"
            prepend-inner-icon="mdi-magnify"
          />
        </v-col>
      </v-row>
      <div class="mt-4 d-flex ga-2 flex-wrap">
        <v-chip color="info" variant="tonal">Open Roles: {{ filteredOpenings.length }}</v-chip>
        <v-chip color="primary" variant="outlined">Locations: {{ locationChipLabel }}</v-chip>
      </div>
    </v-card>

    <section class="mt-8 mb-8">
      <v-expansion-panels variant="accordion">
        <v-expansion-panel v-for="job in filteredOpenings" :key="job.id" class="glass-panel">
          <v-expansion-panel-title>
            <div class="job-head">
              <div>
                <h3 class="job-title">{{ job.role }}</h3>
                <p class="job-meta">
                  {{ job.department }} • {{ job.location }} • {{ job.workModel }} • {{ job.type }}
                </p>
                <p class="job-id">Job ID: {{ job.id }}</p>
              </div>
              <v-chip color="info" variant="tonal">Now Hiring</v-chip>
            </div>
          </v-expansion-panel-title>
          <v-expansion-panel-text>
            <p class="card-copy mt-0">{{ job.summary }}</p>

            <p class="mini-kicker mt-5">Job overview</p>
            <v-row class="mt-2" dense>
              <v-col cols="12" md="4">
                <div class="overview-tile">
                  <p class="tile-label">Experience</p>
                  <p class="tile-value">{{ job.experience }}</p>
                </div>
              </v-col>
              <v-col cols="12" md="4">
                <div class="overview-tile">
                  <p class="tile-label">Reporting To</p>
                  <p class="tile-value">{{ job.reportingTo }}</p>
                </div>
              </v-col>
              <v-col cols="12" md="4">
                <div class="overview-tile">
                  <p class="tile-label">Openings</p>
                  <p class="tile-value">{{ job.openingCount }}</p>
                </div>
              </v-col>
              <v-col cols="12" md="6">
                <div class="overview-tile">
                  <p class="tile-label">Compensation</p>
                  <p class="tile-value">{{ job.compensation }}</p>
                </div>
              </v-col>
              <v-col cols="12" md="3">
                <div class="overview-tile">
                  <p class="tile-label">Posted On</p>
                  <p class="tile-value">{{ job.postedOn }}</p>
                </div>
              </v-col>
              <v-col cols="12" md="3">
                <div class="overview-tile">
                  <p class="tile-label">Notice Period</p>
                  <p class="tile-value">{{ job.noticePeriod }}</p>
                </div>
              </v-col>
            </v-row>

            <p class="mini-kicker mt-5">Key responsibilities</p>
            <v-list bg-color="transparent" class="px-0">
              <v-list-item
                v-for="entry in job.responsibilities"
                :key="entry"
                prepend-icon="mdi-chevron-right-circle-outline"
                class="px-0"
              >
                <v-list-item-title class="list-copy">{{ entry }}</v-list-item-title>
              </v-list-item>
            </v-list>

            <p class="mini-kicker mt-4">Basic qualifications</p>
            <v-list bg-color="transparent" class="px-0">
              <v-list-item
                v-for="entry in job.basicQualifications"
                :key="entry"
                prepend-icon="mdi-check-circle-outline"
                class="px-0"
              >
                <v-list-item-title class="list-copy">{{ entry }}</v-list-item-title>
              </v-list-item>
            </v-list>

            <p class="mini-kicker mt-4">Preferred qualifications</p>
            <v-list bg-color="transparent" class="px-0">
              <v-list-item
                v-for="entry in job.preferredQualifications"
                :key="entry"
                prepend-icon="mdi-star-outline"
                class="px-0"
              >
                <v-list-item-title class="list-copy">{{ entry }}</v-list-item-title>
              </v-list-item>
            </v-list>

            <p class="mini-kicker mt-4">Tools and technologies</p>
            <div class="mt-2 d-flex ga-2 flex-wrap">
              <v-chip v-for="tool in job.tools" :key="tool" color="secondary" variant="tonal">
                {{ tool }}
              </v-chip>
            </div>

            <p class="mini-kicker mt-5">What we offer</p>
            <v-list bg-color="transparent" class="px-0">
              <v-list-item
                v-for="entry in job.benefits"
                :key="entry"
                prepend-icon="mdi-hand-coin-outline"
                class="px-0"
              >
                <v-list-item-title class="list-copy">{{ entry }}</v-list-item-title>
              </v-list-item>
            </v-list>

            <v-alert class="mt-4" type="info" variant="tonal" border="start">
              PVI ENGINEERS is an equal opportunity employer. Selection is based on role fit,
              capability, and alignment with project requirements.
            </v-alert>

            <div class="mt-4 d-flex ga-3 flex-wrap">
              <v-btn
                :to="{ name: 'CareersApplyForm', params: { jobId: job.id } }"
                color="primary"
              >
                Apply for this role
              </v-btn>
              <v-btn color="secondary" variant="tonal" to="/careers/hiring-process">
                View hiring process
              </v-btn>
            </div>
          </v-expansion-panel-text>
        </v-expansion-panel>
      </v-expansion-panels>

      <v-card v-if="filteredOpenings.length === 0" class="glass-panel pa-6 mt-6">
        <h3 class="job-title">No matching openings found</h3>
        <p class="card-copy">
          Try changing department filter or search keywords. You can also email
          careers@pviengineers.com for upcoming opportunities.
        </p>
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

.section-kicker,
.mini-kicker {
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

.glass-panel {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
}

.job-head {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.job-title {
  margin: 0;
  color: var(--text-card-title);
  font-size: 1.08rem;
}

.job-meta {
  margin: 6px 0 0;
  color: var(--text-path);
  font-size: 0.86rem;
}

.job-id {
  margin: 4px 0 0;
  color: var(--text-path);
  font-size: 0.8rem;
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.7;
}

.list-copy {
  color: var(--text-card-copy);
  white-space: normal;
  line-height: 1.55;
}

.overview-tile {
  border: 1px solid var(--surface-card-border);
  border-radius: 12px;
  padding: 10px;
}

.tile-label {
  margin: 0;
  color: var(--text-path);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.72rem;
}

.tile-value {
  margin: 7px 0 0;
  color: var(--text-card-title);
  line-height: 1.5;
  font-size: 0.93rem;
}
</style>
