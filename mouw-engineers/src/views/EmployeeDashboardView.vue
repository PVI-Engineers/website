<script setup>
import { computed, onMounted, ref } from 'vue'
import PortalShell from '../components/PortalShell.vue'
import {
  changePortalPassword,
  fetchEmployeeDashboard,
  submitEmployeeTimesheet,
  updateEmployeeProfile,
} from '../services/portalApi'

const loading = ref(true)
const saving = ref(false)
const profileSaving = ref(false)
const passwordSaving = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const dashboard = ref(null)
const section = ref('dashboard')
const phone = ref('')
const passwordForm = ref({ currentPassword: '', newPassword: '', confirmPassword: '' })
const form = ref(emptyForm())

const navItems = [
  { id: 'dashboard', label: 'Dashboard', icon: 'mdi-view-dashboard-outline' },
  { id: 'profile', label: 'My profile', icon: 'mdi-account-outline' },
  { id: 'projects', label: 'Projects', icon: 'mdi-folder-outline' },
  { id: 'timesheet', label: 'Submit time', icon: 'mdi-clock-outline' },
  { id: 'history', label: 'Timesheets', icon: 'mdi-format-list-bulleted' },
]

const projects = computed(() => dashboard.value?.projects || [])
const timesheets = computed(() => dashboard.value?.timesheets || [])
const summary = computed(() => dashboard.value?.summary || {})
const profile = computed(() => dashboard.value?.profile || {})
const today = new Date().toISOString().slice(0, 10)

function emptyForm() {
  return {
    projectId: null,
    workDate: new Date().toISOString().slice(0, 10),
    hours: '',
    workDescription: '',
  }
}

function display(value) {
  return value ? value : 'Not assigned'
}

function formatDate(value) {
  if (!value) return '-'
  const [year, month, day] = String(value).split('-')
  if (!year || !month || !day) return value
  return new Date(Number(year), Number(month) - 1, Number(day)).toLocaleDateString()
}

function formatDateTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

function hoursLabel(value) {
  const amount = Number(value || 0)
  return `${amount.toFixed(2)} hrs`
}

function statusColor(status) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'error'
  return 'warning'
}

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''
  try {
    dashboard.value = await fetchEmployeeDashboard()
    phone.value = dashboard.value?.profile?.phone || ''
    if (!form.value.projectId && projects.value.length === 1) {
      form.value.projectId = projects.value[0].id
    }
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to load the employee portal.'
  } finally {
    loading.value = false
  }
}

async function onSubmit() {
  successMessage.value = ''
  errorMessage.value = ''
  const hours = Number(form.value.hours)
  if (!form.value.projectId) {
    errorMessage.value = 'Select one of your assigned projects.'
    return
  }
  if (!form.value.workDate || form.value.workDate > today) {
    errorMessage.value = 'Choose today or an earlier work date.'
    return
  }
  if (!Number.isFinite(hours) || hours < 0.25 || hours > 24) {
    errorMessage.value = 'Enter hours between 0.25 and 24.'
    return
  }
  if (form.value.workDescription.trim().length < 10) {
    errorMessage.value = 'Describe the work in at least 10 characters.'
    return
  }

  saving.value = true
  try {
    await submitEmployeeTimesheet({
      projectId: form.value.projectId,
      workDate: form.value.workDate,
      hours,
      workDescription: form.value.workDescription.trim(),
    })
    successMessage.value = 'Timesheet submitted for review.'
    form.value = emptyForm()
    await loadDashboard()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to submit the timesheet.'
  } finally {
    saving.value = false
  }
}

async function saveProfile() {
  successMessage.value = ''
  errorMessage.value = ''
  profileSaving.value = true
  try {
    await updateEmployeeProfile({ phone: phone.value.trim() })
    successMessage.value = 'Your contact number was updated.'
    await loadDashboard()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to update your profile.'
  } finally {
    profileSaving.value = false
  }
}

async function savePassword() {
  successMessage.value = ''
  errorMessage.value = ''
  if (passwordForm.value.newPassword.length < 8) {
    errorMessage.value = 'New password must be at least 8 characters.'
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    errorMessage.value = 'New password and confirmation do not match.'
    return
  }
  passwordSaving.value = true
  try {
    await changePortalPassword({
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword,
    })
    passwordForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
    successMessage.value = 'Password changed. This session is still signed in.'
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to change your password.'
  } finally {
    passwordSaving.value = false
  }
}

onMounted(loadDashboard)
</script>

<template>
  <PortalShell v-model:section="section" eyebrow="Employee Portal" :items="navItems">
    <v-alert v-if="errorMessage" class="mb-4" type="error" variant="tonal" border="start">
      {{ errorMessage }}
    </v-alert>
    <v-alert v-if="successMessage" class="mb-4" type="success" variant="tonal" border="start">
      {{ successMessage }}
    </v-alert>

    <v-card v-if="loading" class="glass-card pa-8 text-center">
      <v-progress-circular color="primary" indeterminate />
      <p class="page-copy mt-4">Loading employee portal...</p>
    </v-card>

    <template v-else>
      <div v-show="section === 'dashboard'">
        <v-row dense>
          <v-col cols="12" sm="6" lg="3">
            <v-card class="glass-card pa-5">
              <p class="mini-kicker">Total hours</p>
              <p class="metric mt-2">{{ hoursLabel(summary.totalHours) }}</p>
            </v-card>
          </v-col>
          <v-col cols="12" sm="6" lg="3">
            <v-card class="glass-card pa-5">
              <p class="mini-kicker">Pending review</p>
              <p class="metric mt-2">{{ hoursLabel(summary.pendingHours) }}</p>
            </v-card>
          </v-col>
          <v-col cols="12" sm="6" lg="3">
            <v-card class="glass-card pa-5">
              <p class="mini-kicker">Approved</p>
              <p class="metric mt-2">{{ hoursLabel(summary.approvedHours) }}</p>
            </v-card>
          </v-col>
          <v-col cols="12" sm="6" lg="3">
            <v-card class="glass-card pa-5">
              <p class="mini-kicker">Assigned projects</p>
              <p class="metric mt-2">{{ projects.length }}</p>
            </v-card>
          </v-col>
        </v-row>
        <v-card class="glass-card pa-6 mt-4">
          <p class="mini-kicker">Recent activity</p>
          <p v-if="!(dashboard?.recentActivity || []).length" class="page-copy">
            Submitted and reviewed timesheets will appear here.
          </p>
          <div v-else class="d-flex flex-column ga-3 mt-4">
            <div v-for="item in dashboard.recentActivity" :key="item.id" class="project-row">
              <p class="meta-value">{{ item.message }}</p>
              <p class="meta-label">{{ formatDateTime(item.occurredAt) }}</p>
            </div>
          </div>
        </v-card>
      </div>

      <div v-show="section === 'profile'">
        <v-row dense>
          <v-col cols="12" lg="6">
            <v-card class="glass-card pa-6">
              <p class="mini-kicker">Employment details</p>
              <p class="page-copy">Employee ID, role, department, and email are managed by an administrator.</p>
              <v-row class="mt-2" dense>
                <v-col cols="12" sm="6">
                  <p class="meta-label">Employee ID</p>
                  <p class="meta-value">{{ display(profile.employeeCode) }}</p>
                </v-col>
                <v-col cols="12" sm="6">
                  <p class="meta-label">Name</p>
                  <p class="meta-value">{{ display(profile.fullName) }}</p>
                </v-col>
                <v-col cols="12" sm="6">
                  <p class="meta-label">Designation</p>
                  <p class="meta-value">{{ display(profile.designation) }}</p>
                </v-col>
                <v-col cols="12" sm="6">
                  <p class="meta-label">Department</p>
                  <p class="meta-value">{{ display(profile.department) }}</p>
                </v-col>
                <v-col cols="12">
                  <p class="meta-label">Email</p>
                  <p class="meta-value">{{ display(profile.email) }}</p>
                </v-col>
              </v-row>
            </v-card>
          </v-col>
          <v-col cols="12" lg="6">
            <v-card class="glass-card pa-6 mb-4">
              <p class="mini-kicker">Contact number</p>
              <v-form class="mt-4" @submit.prevent="saveProfile">
                <v-text-field v-model="phone" label="Phone number" placeholder="+1 605 592 0819" />
                <v-btn :loading="profileSaving" color="primary" type="submit">Save contact number</v-btn>
              </v-form>
            </v-card>
            <v-card class="glass-card pa-6">
              <p class="mini-kicker">Change password</p>
              <v-form class="mt-4" @submit.prevent="savePassword">
                <v-text-field v-model="passwordForm.currentPassword" class="mb-3" label="Current password" type="password" />
                <v-text-field v-model="passwordForm.newPassword" class="mb-3" label="New password" type="password" />
                <v-text-field v-model="passwordForm.confirmPassword" class="mb-4" label="Confirm new password" type="password" />
                <v-btn :loading="passwordSaving" color="primary" type="submit">Update password</v-btn>
              </v-form>
            </v-card>
          </v-col>
        </v-row>
      </div>

      <v-card v-show="section === 'projects'" class="glass-card pa-6">
        <p class="mini-kicker">Assigned projects</p>
        <p v-if="projects.length === 0" class="page-copy">
          No projects are assigned yet. An administrator needs to assign your projects before you can submit time.
        </p>
        <div v-else class="d-flex flex-column ga-3 mt-4">
          <div v-for="project in projects" :key="project.id" class="project-row">
            <p class="meta-value">{{ project.name }}</p>
            <p class="meta-label mt-1">{{ project.code }}</p>
            <p class="project-copy">{{ project.description || 'Active project assignment' }}</p>
          </div>
        </div>
      </v-card>

      <v-card v-show="section === 'timesheet'" class="glass-card pa-6">
        <p class="mini-kicker">Submit timesheet</p>
        <v-form class="mt-4" @submit.prevent="onSubmit">
          <v-select
            v-model="form.projectId"
            class="mb-3"
            :items="projects"
            item-title="name"
            item-value="id"
            label="Assigned project"
            :disabled="projects.length === 0"
            no-data-text="No assigned projects"
          />
          <v-text-field v-model="form.workDate" class="mb-3" label="Work date" type="date" :max="today" />
          <v-text-field v-model="form.hours" class="mb-3" label="Hours worked" type="number" min="0.25" max="24" step="0.25" />
          <v-textarea
            v-model="form.workDescription"
            class="mb-4"
            label="Work status"
            rows="4"
            counter="2000"
            placeholder="Describe the tasks completed on this project."
          />
          <v-btn :loading="saving" color="primary" type="submit" :disabled="projects.length === 0">Submit timesheet</v-btn>
        </v-form>
      </v-card>

      <v-card v-show="section === 'history'" class="glass-card pa-6">
        <p class="mini-kicker mb-3">Timesheet history</p>
        <p v-if="timesheets.length === 0" class="page-copy">No timesheets have been submitted yet.</p>
        <div v-else class="table-scroll">
          <v-table class="transparent-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Project</th>
                <th>Hours</th>
                <th>Work status</th>
                <th>Review</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="entry in timesheets" :key="entry.id">
                <td>{{ formatDate(entry.workDate) }}</td>
                <td>{{ entry.projectName }}<br /><span class="meta-label">{{ entry.projectCode }}</span></td>
                <td>{{ hoursLabel(entry.hours) }}</td>
                <td class="message-cell">{{ entry.workDescription }}</td>
                <td>
                  <v-chip :color="statusColor(entry.status)" size="small" variant="tonal">{{ entry.status }}</v-chip>
                  <p v-if="entry.reviewNote" class="meta-label mt-2">{{ entry.reviewNote }}</p>
                </td>
              </tr>
            </tbody>
          </v-table>
        </div>
      </v-card>
    </template>
  </PortalShell>
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

.page-copy,
.project-copy {
  margin: 14px 0 0;
  color: var(--text-copy);
  line-height: 1.7;
}

.project-copy {
  margin: 6px 0 0;
  font-size: 0.92rem;
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

.meta-label {
  margin: 0;
  color: var(--text-kicker);
  font-size: 0.75rem;
  letter-spacing: 0.04em;
}

.meta-value {
  margin: 4px 0 0;
  color: var(--text-card-title);
  font-weight: 600;
}

.project-row {
  padding-bottom: 12px;
  border-bottom: 1px solid var(--surface-card-border);
}

.table-scroll {
  overflow-x: auto;
}

.transparent-table {
  background: transparent;
  min-width: 720px;
}

.transparent-table :deep(th),
.transparent-table :deep(td) {
  color: var(--text-card-copy);
  vertical-align: top;
}

.message-cell {
  max-width: 320px;
  white-space: pre-line;
}
</style>
