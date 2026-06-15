<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  clearSession,
  downloadApplicationFile,
  downloadResume,
  fetchAdminUsers,
  fetchHrApplications,
  fetchHrContactInquiries,
  getStoredUser,
} from '../services/portalApi'

const router = useRouter()
const loading = ref(true)
const errorMessage = ref('')
const adminUser = ref(getStoredUser())
const users = ref([])
const applications = ref([])
const contactInquiries = ref([])
const downloadLoadingKey = ref('')

const userCount = computed(() => users.value.length)
const applicationCount = computed(() => applications.value.length)
const contactInquiryCount = computed(() => contactInquiries.value.length)

function formatDateTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [internalUsers, hrApplications, hrContactInquiries] = await Promise.all([
      fetchAdminUsers(),
      fetchHrApplications(),
      fetchHrContactInquiries(),
    ])
    users.value = internalUsers
    applications.value = hrApplications
    contactInquiries.value = hrContactInquiries
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Failed to load admin dashboard.'
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

onMounted(loadDashboard)
</script>

<template>
  <v-container class="page-shell">
    <section class="d-flex justify-space-between align-start flex-wrap ga-4">
      <div>
        <p class="section-kicker">Admin Dashboard</p>
        <h1 class="page-title">Full Access Control Center</h1>
        <p class="page-copy">
          Admin can manage users, review HR application data, and monitor website contact inquiries.
        </p>
      </div>
      <div class="d-flex ga-2 flex-wrap">
        <v-btn color="primary" variant="tonal" @click="loadDashboard">Refresh</v-btn>
        <v-btn color="secondary" variant="tonal" to="/portal/hr">Open HR Dashboard</v-btn>
        <v-btn color="error" variant="text" @click="logout">Logout</v-btn>
      </div>
    </section>

    <v-alert v-if="errorMessage" class="mt-6" type="error" variant="tonal" border="start">
      {{ errorMessage }}
    </v-alert>

    <v-row class="mt-2" dense>
      <v-col cols="12" md="3">
        <v-card class="glass-card pa-5">
          <p class="mini-kicker">Current Login</p>
          <p class="metric mt-2">{{ adminUser?.fullName || adminUser?.username }}</p>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="glass-card pa-5">
          <p class="mini-kicker">Internal Users</p>
          <p class="metric mt-2">{{ userCount }}</p>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="glass-card pa-5">
          <p class="mini-kicker">Applications</p>
          <p class="metric mt-2">{{ applicationCount }}</p>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="glass-card pa-5">
          <p class="mini-kicker">Contact Inquiries</p>
          <p class="metric mt-2">{{ contactInquiryCount }}</p>
        </v-card>
      </v-col>
    </v-row>

    <v-card v-if="loading" class="glass-card pa-8 mt-6 text-center">
      <v-progress-circular color="primary" indeterminate />
      <p class="page-copy mt-4">Loading admin dashboard...</p>
    </v-card>

    <template v-else>
      <v-card class="glass-card pa-6 mt-6">
        <p class="mini-kicker mb-3">Portal Users</p>
        <v-table class="transparent-table">
          <thead>
            <tr>
              <th>User</th>
              <th>Email</th>
              <th>Roles</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.fullName }} ({{ user.username }})</td>
              <td>{{ user.email }}</td>
              <td>{{ user.roles.join(', ') }}</td>
              <td>{{ formatDateTime(user.createdAt) }}</td>
            </tr>
          </tbody>
        </v-table>
      </v-card>

      <v-card class="glass-card pa-6 mt-6">
        <p class="mini-kicker mb-3">Latest Applications</p>
        <v-table class="transparent-table">
          <thead>
            <tr>
              <th>Candidate</th>
              <th>Job Role</th>
              <th>Applied On</th>
              <th>Files</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="application in applications.slice(0, 8)" :key="application.id">
              <td>{{ application.firstName }} {{ application.lastName }}</td>
              <td>{{ application.jobRole }}</td>
              <td>{{ formatDateTime(application.createdAt) }}</td>
              <td>
                <div class="d-flex flex-wrap ga-2">
                  <v-btn
                    v-for="file in getApplicationFiles(application)"
                    :key="`${application.id}-${file.id || file.fileName}`"
                    :loading="downloadLoadingKey === `${application.id}:${file.id ?? 'legacy-resume'}`"
                    color="primary"
                    density="comfortable"
                    size="small"
                    variant="tonal"
                    @click="onDownloadFile(application, file)"
                  >
                    {{ file.category === 'resume' ? 'Resume' : file.fileName }}
                  </v-btn>
                </div>
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card>

      <v-card class="glass-card pa-6 mt-6">
        <p class="mini-kicker mb-3">Latest Contact Inquiries</p>
        <v-table class="transparent-table">
          <thead>
            <tr>
              <th>Reference</th>
              <th>Name</th>
              <th>Inquiry Type</th>
              <th>Email / Phone</th>
              <th>Company</th>
              <th>Message</th>
              <th>Submitted On</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="inquiry in contactInquiries.slice(0, 8)" :key="inquiry.id">
              <td>{{ inquiry.inquiryRef }}</td>
              <td>{{ inquiry.name }}</td>
              <td>{{ inquiry.inquiryType }}</td>
              <td>{{ inquiry.email }}<br />{{ inquiry.phone }}</td>
              <td>{{ inquiry.company || '-' }}</td>
              <td class="message-cell">{{ inquiry.message }}</td>
              <td>{{ formatDateTime(inquiry.createdAt) }}</td>
            </tr>
          </tbody>
        </v-table>
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
  line-height: 1.2;
  font-size: clamp(1.8rem, 3.2vw, 2.6rem);
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

.metric {
  margin: 0;
  color: var(--text-title);
  font-size: 1.7rem;
  font-weight: 600;
}

.transparent-table {
  background: transparent;
}

.transparent-table :deep(th),
.transparent-table :deep(td) {
  color: var(--text-card-copy);
  vertical-align: top;
}

.message-cell {
  max-width: 340px;
  white-space: pre-line;
}
</style>
