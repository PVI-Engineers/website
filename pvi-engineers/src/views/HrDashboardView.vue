<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  clearSession,
  downloadApplicationFile,
  downloadResume,
  fetchHrApplications,
  getStoredUser,
} from '../services/portalApi'

const router = useRouter()
const loading = ref(true)
const downloadLoadingKey = ref('')
const errorMessage = ref('')
const applications = ref([])
const portalUser = ref(getStoredUser())

const applicationCount = computed(() => applications.value.length)

function formatDateTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

async function loadApplications() {
  loading.value = true
  errorMessage.value = ''
  try {
    applications.value = await fetchHrApplications()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Failed to load applications.'
  } finally {
    loading.value = false
  }
}

function getApplicationFiles(application) {
  if (Array.isArray(application.files) && application.files.length > 0) {
    return application.files
  }

  if (application.resumeFileName) {
    return [
      {
        id: null,
        category: 'resume',
        fileName: application.resumeFileName,
      },
    ]
  }

  return []
}

async function onDownloadFile(application, file) {
  const loadingKey = `${application.id}:${file.id ?? 'legacy-resume'}`
  downloadLoadingKey.value = loadingKey
  try {
    const downloadResult = file.id
      ? await downloadApplicationFile(application.id, file.id)
      : await downloadResume(application.id)
    const { blob, filename } = downloadResult
    const objectUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = objectUrl
    link.download = filename || file.fileName || `${application.applicationRef}-document`
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(objectUrl)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Failed to download file.'
  } finally {
    downloadLoadingKey.value = ''
  }
}

function logout() {
  clearSession()
  router.replace('/portal/login')
}

onMounted(loadApplications)
</script>

<template>
  <v-container class="page-shell">
    <section class="d-flex justify-space-between align-start flex-wrap ga-4">
      <div>
        <p class="section-kicker">HR Dashboard</p>
        <h1 class="page-title">Applications Management</h1>
        <p class="page-copy">
          View submitted applications and download resumes and supporting files shared by candidates.
        </p>
      </div>
      <div class="d-flex ga-2 flex-wrap">
        <v-btn color="primary" variant="tonal" @click="loadApplications">Refresh</v-btn>
        <v-btn color="secondary" variant="tonal" to="/portal/admin">Admin View</v-btn>
        <v-btn color="error" variant="text" @click="logout">Logout</v-btn>
      </div>
    </section>

    <v-card class="glass-card pa-4 mt-6">
      <div class="d-flex flex-wrap ga-3 align-center justify-space-between">
        <p class="meta-line">
          Logged in as <strong>{{ portalUser?.fullName || portalUser?.username }}</strong>
        </p>
        <v-chip color="primary" variant="tonal">Total applications: {{ applicationCount }}</v-chip>
      </div>
    </v-card>

    <v-alert v-if="errorMessage" class="mt-6" type="error" variant="tonal" border="start">
      {{ errorMessage }}
    </v-alert>

    <v-card v-if="loading" class="glass-card pa-8 mt-6 text-center">
      <v-progress-circular color="primary" indeterminate />
      <p class="page-copy mt-4">Loading applications...</p>
    </v-card>

    <v-card v-else-if="applications.length === 0" class="glass-card pa-8 mt-6">
      <p class="section-kicker">No data yet</p>
      <h2 class="section-title mt-2">No submitted applications found</h2>
      <p class="page-copy mt-3">
        Once candidates submit forms from the careers page, records will appear here.
      </p>
    </v-card>

    <v-expansion-panels v-else class="mt-6">
      <v-expansion-panel v-for="application in applications" :key="application.id" rounded="xl">
        <v-expansion-panel-title>
          <div class="title-wrap">
            <p class="job-title">{{ application.firstName }} {{ application.lastName }}</p>
            <p class="job-meta">
              {{ application.jobRole }} • {{ application.applicationRef }} •
              {{ formatDateTime(application.createdAt) }}
            </p>
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row dense>
            <v-col cols="12" md="4">
              <p class="meta-label">Email</p>
              <p class="meta-value">{{ application.email }}</p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Phone</p>
              <p class="meta-value">{{ application.phone }}</p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Location</p>
              <p class="meta-value">{{ application.currentLocation }}</p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Current Company</p>
              <p class="meta-value">{{ application.currentCompany }}</p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Designation</p>
              <p class="meta-value">{{ application.currentDesignation }}</p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Experience</p>
              <p class="meta-value">
                {{ application.totalExperience }} yrs (Relevant: {{ application.relevantExperience }} yrs)
              </p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Expected CTC</p>
              <p class="meta-value">{{ application.expectedCtc }}</p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Notice Period</p>
              <p class="meta-value">{{ application.noticePeriod }}</p>
            </v-col>
            <v-col cols="12" md="4">
              <p class="meta-label">Available From</p>
              <p class="meta-value">{{ application.availableFrom }}</p>
            </v-col>
          </v-row>

          <p class="meta-label mt-2">Key Skills</p>
          <p class="meta-value">{{ application.keySkills }}</p>

          <p class="meta-label mt-2">Why Join PVI</p>
          <p class="meta-value">{{ application.whyJoin }}</p>

          <p class="meta-label mt-2">Additional Information</p>
          <p class="meta-value">{{ application.additionalInfo || '-' }}</p>

          <p class="meta-label mt-4">Uploaded Files</p>
          <div class="d-flex ga-3 mt-2 flex-wrap">
            <template v-for="file in getApplicationFiles(application)" :key="`${application.id}-${file.id || file.fileName}`">
              <v-btn
                :loading="downloadLoadingKey === `${application.id}:${file.id ?? 'legacy-resume'}`"
                color="primary"
                variant="flat"
                @click="onDownloadFile(application, file)"
              >
                Download {{ file.fileName }}
              </v-btn>
              <v-chip color="secondary" variant="tonal">
                {{ file.category || 'supporting' }}
              </v-chip>
            </template>
          </div>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
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
  font-size: clamp(1.8rem, 3.2vw, 2.6rem);
}

.section-title {
  font-size: clamp(1.3rem, 2.5vw, 1.8rem);
}

.page-copy {
  margin: 14px 0 0;
  color: var(--text-copy);
  line-height: 1.7;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
}

.meta-line {
  margin: 0;
  color: var(--text-copy);
}

.title-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.job-title {
  margin: 0;
  color: var(--text-title);
  font-weight: 600;
}

.job-meta {
  margin: 0;
  color: var(--text-copy);
  font-size: 0.9rem;
}

.meta-label {
  margin: 0;
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.7rem;
}

.meta-value {
  margin: 6px 0 0;
  color: var(--text-card-copy);
  white-space: pre-line;
}
</style>
