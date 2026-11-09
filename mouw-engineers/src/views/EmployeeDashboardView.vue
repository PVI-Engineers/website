<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  clearSession,
  fetchCurrentUser,
  fetchEmployeeHome,
  getStoredUser,
} from '../services/portalApi'

const router = useRouter()
const loading = ref(true)
const errorMessage = ref('')
const currentUser = ref(getStoredUser())
const employeeStatus = ref(null)

async function loadPortalData() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [profile, employeeHome] = await Promise.all([fetchCurrentUser(), fetchEmployeeHome()])
    currentUser.value = profile
    employeeStatus.value = employeeHome
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to load employee portal.'
  } finally {
    loading.value = false
  }
}

function logout() {
  clearSession()
  router.replace('/portal/login')
}

onMounted(loadPortalData)
</script>

<template>
  <v-container class="page-shell">
    <section class="d-flex justify-space-between align-start flex-wrap ga-4">
      <div>
        <p class="section-kicker">Employee Portal</p>
        <h1 class="page-title">Welcome {{ currentUser?.fullName || currentUser?.username }}</h1>
        <p class="page-copy">
          Your account is active. This area can be extended for future employee workflows.
        </p>
      </div>
      <v-btn color="error" variant="text" @click="logout">Logout</v-btn>
    </section>

    <v-alert v-if="errorMessage" class="mt-6" type="error" variant="tonal" border="start">
      {{ errorMessage }}
    </v-alert>

    <v-card v-if="loading" class="glass-card pa-8 mt-6 text-center">
      <v-progress-circular color="primary" indeterminate />
      <p class="page-copy mt-4">Loading employee workspace...</p>
    </v-card>

    <v-card v-else class="glass-card pa-6 mt-6">
      <p class="mini-kicker">Account Summary</p>
      <v-row dense class="mt-2">
        <v-col cols="12" md="4">
          <p class="meta-label">Username</p>
          <p class="meta-value">{{ currentUser?.username }}</p>
        </v-col>
        <v-col cols="12" md="4">
          <p class="meta-label">Email</p>
          <p class="meta-value">{{ currentUser?.email }}</p>
        </v-col>
        <v-col cols="12" md="4">
          <p class="meta-label">Role</p>
          <p class="meta-value">
            {{ Array.isArray(currentUser?.roles) ? currentUser.roles.join(', ') : '-' }}
          </p>
        </v-col>
      </v-row>

      <v-alert class="mt-5" type="success" variant="tonal" border="start">
        {{ employeeStatus?.message || 'Employee portal login successful.' }}
      </v-alert>
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
}
</style>
