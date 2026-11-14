<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  clearSession,
  downloadApplicationFile,
  downloadResume,
  fetchAdminEmployees,
  fetchAdminProjects,
  fetchAdminTimesheets,
  fetchAdminUsers,
  fetchHrApplications,
  fetchHrContactInquiries,
  getStoredUser,
  reviewAdminTimesheet,
  saveAdminEmployee,
  saveAdminProject,
} from '../services/portalApi'

const router = useRouter()
const loading = ref(true)
const errorMessage = ref('')
const adminUser = ref(getStoredUser())
const users = ref([])
const applications = ref([])
const contactInquiries = ref([])
const employees = ref([])
const projects = ref([])
const timesheets = ref([])
const adminTab = ref('operations')
const downloadLoadingKey = ref('')
const employeeSaving = ref(false)
const projectSaving = ref(false)
const reviewSaving = ref(false)
const notice = ref('')
const employeeForm = ref(emptyEmployeeForm())
const projectForm = ref(emptyProjectForm())
const reviewDialog = ref(false)
const reviewTarget = ref(null)
const reviewNote = ref('')

const userCount = computed(() => users.value.length)
const applicationCount = computed(() => applications.value.length)
const contactInquiryCount = computed(() => contactInquiries.value.length)
const pendingTimesheets = computed(() => timesheets.value.filter((entry) => entry.status === 'SUBMITTED').length)

function emptyEmployeeForm() {
  return {
    id: null,
    fullName: '',
    employeeCode: '',
    email: '',
    designation: '',
    department: '',
    password: '',
    projectIds: [],
  }
}

function emptyProjectForm() {
  return {
    id: null,
    name: '',
    code: '',
    description: '',
    active: true,
  }
}

function formatDateTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [internalUsers, hrApplications, hrContactInquiries, workforce, projectList, timesheetList] = await Promise.all([
      fetchAdminUsers(),
      fetchHrApplications(),
      fetchHrContactInquiries(),
      fetchAdminEmployees(),
      fetchAdminProjects(),
      fetchAdminTimesheets(),
    ])
    users.value = internalUsers
    applications.value = hrApplications
    contactInquiries.value = hrContactInquiries
    employees.value = workforce
    projects.value = projectList
    timesheets.value = timesheetList
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

function statusColor(status) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'error'
  return 'warning'
}

function projectNames(employee) {
  return (employee.projects || []).map((project) => project.name).join(', ') || '-'
}

function editEmployee(employee) {
  employeeForm.value = {
    id: employee.id,
    fullName: employee.fullName || '',
    employeeCode: employee.employeeCode || '',
    email: employee.email || '',
    designation: employee.designation || '',
    department: employee.department || '',
    password: '',
    projectIds: (employee.projects || []).map((project) => project.id),
  }
  adminTab.value = 'employees'
}

function resetEmployeeForm() {
  employeeForm.value = emptyEmployeeForm()
}

function editProject(project) {
  projectForm.value = {
    id: project.id,
    name: project.name,
    code: project.code,
    description: project.description || '',
    active: project.active,
  }
  adminTab.value = 'projects'
}

function resetProjectForm() {
  projectForm.value = emptyProjectForm()
}

async function onSaveEmployee() {
  notice.value = ''
  errorMessage.value = ''
  if (!employeeForm.value.projectIds.length) {
    errorMessage.value = 'Assign at least one project before saving the employee.'
    return
  }
  if (!employeeForm.value.id && employeeForm.value.password.trim().length < 8) {
    errorMessage.value = 'A new employee password must be at least 8 characters.'
    return
  }
  employeeSaving.value = true
  try {
    await saveAdminEmployee(
      {
        fullName: employeeForm.value.fullName.trim(),
        employeeCode: employeeForm.value.employeeCode.trim(),
        email: employeeForm.value.email.trim(),
        designation: employeeForm.value.designation.trim(),
        department: employeeForm.value.department.trim(),
        password: employeeForm.value.password,
        projectIds: employeeForm.value.projectIds,
      },
      employeeForm.value.id,
    )
    notice.value = employeeForm.value.id ? 'Employee details updated.' : 'Employee account created.'
    resetEmployeeForm()
    await loadDashboard()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to save the employee.'
  } finally {
    employeeSaving.value = false
  }
}

async function onSaveProject() {
  notice.value = ''
  errorMessage.value = ''
  projectSaving.value = true
  try {
    await saveAdminProject(
      {
        name: projectForm.value.name.trim(),
        code: projectForm.value.code.trim(),
        description: projectForm.value.description.trim(),
        active: projectForm.value.active,
      },
      projectForm.value.id,
    )
    notice.value = projectForm.value.id ? 'Project updated.' : 'Project created.'
    resetProjectForm()
    await loadDashboard()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to save the project.'
  } finally {
    projectSaving.value = false
  }
}

function openReview(entry) {
  reviewTarget.value = entry
  reviewNote.value = ''
  reviewDialog.value = true
}

async function onReview(decision) {
  if (!reviewTarget.value) return
  if (decision === 'REJECTED' && !reviewNote.value.trim()) {
    errorMessage.value = 'Add a note when rejecting a timesheet.'
    return
  }
  reviewSaving.value = true
  errorMessage.value = ''
  try {
    await reviewAdminTimesheet(reviewTarget.value.id, {
      decision,
      note: reviewNote.value.trim(),
    })
    notice.value = decision === 'APPROVED' ? 'Timesheet approved.' : 'Timesheet rejected.'
    reviewDialog.value = false
    await loadDashboard()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to review the timesheet.'
  } finally {
    reviewSaving.value = false
  }
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
          Manage employees and projects, review timesheets, and monitor applications and contact inquiries.
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
    <v-alert v-if="notice" class="mt-6" type="success" variant="tonal" border="start">
      {{ notice }}
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
      <v-col cols="12" sm="6" md="3">
        <v-card class="glass-card pa-5">
          <p class="mini-kicker">Pending Timesheets</p>
          <p class="metric mt-2">{{ pendingTimesheets }}</p>
        </v-card>
      </v-col>
    </v-row>

    <v-card v-if="loading" class="glass-card pa-8 mt-6 text-center">
      <v-progress-circular color="primary" indeterminate />
      <p class="page-copy mt-4">Loading admin dashboard...</p>
    </v-card>

    <template v-else>
      <v-tabs v-model="adminTab" class="mt-6" color="primary" show-arrows>
        <v-tab value="operations">Operations</v-tab>
        <v-tab value="employees">Employees</v-tab>
        <v-tab value="projects">Projects</v-tab>
        <v-tab value="timesheets">Timesheets</v-tab>
      </v-tabs>

      <v-window v-model="adminTab">
        <v-window-item value="operations">
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
        </v-window-item>

        <v-window-item value="employees">
          <v-row class="mt-4" dense>
            <v-col cols="12" lg="5">
              <v-card class="glass-card pa-6">
                <p class="mini-kicker">{{ employeeForm.id ? 'Update employee' : 'Create employee' }}</p>
                <v-form class="mt-4" @submit.prevent="onSaveEmployee">
                  <v-text-field v-model="employeeForm.fullName" class="mb-3" label="Full name" />
                  <v-text-field v-model="employeeForm.employeeCode" class="mb-3" label="Employee ID" />
                  <v-text-field v-model="employeeForm.email" class="mb-3" label="Email" type="email" />
                  <v-text-field v-model="employeeForm.designation" class="mb-3" label="Designation" />
                  <v-text-field v-model="employeeForm.department" class="mb-3" label="Department" />
                  <v-text-field
                    v-model="employeeForm.password"
                    class="mb-3"
                    :label="employeeForm.id ? 'New password (optional)' : 'Password'"
                    type="password"
                  />
                  <v-select
                    v-model="employeeForm.projectIds"
                    class="mb-4"
                    :items="projects"
                    item-title="name"
                    item-value="id"
                    label="Assigned projects"
                    multiple
                    chips
                    closable-chips
                  />
                  <div class="d-flex ga-2 flex-wrap">
                    <v-btn :loading="employeeSaving" color="primary" type="submit">
                      {{ employeeForm.id ? 'Update employee' : 'Create employee' }}
                    </v-btn>
                    <v-btn v-if="employeeForm.id" variant="text" @click="resetEmployeeForm">Cancel</v-btn>
                  </div>
                </v-form>
              </v-card>
            </v-col>
            <v-col cols="12" lg="7">
              <v-card class="glass-card pa-6">
                <p class="mini-kicker mb-3">Employee accounts</p>
                <p v-if="employees.length === 0" class="page-copy">No employee accounts have been created yet.</p>
                <div v-else class="table-scroll">
                  <v-table class="transparent-table">
                    <thead>
                      <tr>
                        <th>Employee</th>
                        <th>Role</th>
                        <th>Projects</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="employee in employees" :key="employee.id">
                        <td>
                          {{ employee.fullName }}<br />
                          <span class="meta-label">{{ employee.employeeCode || 'No ID' }} · {{ employee.email }}</span>
                        </td>
                        <td>{{ employee.designation || '-' }}<br /><span class="meta-label">{{ employee.department || '-' }}</span></td>
                        <td>{{ projectNames(employee) }}</td>
                        <td><v-btn size="small" variant="tonal" @click="editEmployee(employee)">Edit</v-btn></td>
                      </tr>
                    </tbody>
                  </v-table>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-window-item>

        <v-window-item value="projects">
          <v-row class="mt-4" dense>
            <v-col cols="12" md="5">
              <v-card class="glass-card pa-6">
                <p class="mini-kicker">{{ projectForm.id ? 'Update project' : 'Create project' }}</p>
                <v-form class="mt-4" @submit.prevent="onSaveProject">
                  <v-text-field v-model="projectForm.name" class="mb-3" label="Project name" />
                  <v-text-field v-model="projectForm.code" class="mb-3" label="Project code" />
                  <v-textarea v-model="projectForm.description" class="mb-3" label="Description" rows="3" />
                  <v-switch v-model="projectForm.active" color="primary" label="Active" inset />
                  <div class="d-flex ga-2 flex-wrap">
                    <v-btn :loading="projectSaving" color="primary" type="submit">
                      {{ projectForm.id ? 'Update project' : 'Create project' }}
                    </v-btn>
                    <v-btn v-if="projectForm.id" variant="text" @click="resetProjectForm">Cancel</v-btn>
                  </div>
                </v-form>
              </v-card>
            </v-col>
            <v-col cols="12" md="7">
              <v-card class="glass-card pa-6">
                <p class="mini-kicker mb-3">Projects</p>
                <p v-if="projects.length === 0" class="page-copy">Create a project before assigning it to an employee.</p>
                <div v-else class="table-scroll">
                  <v-table class="transparent-table">
                    <thead>
                      <tr>
                        <th>Project</th>
                        <th>Status</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="project in projects" :key="project.id">
                        <td>{{ project.name }}<br /><span class="meta-label">{{ project.code }}</span></td>
                        <td>{{ project.active ? 'Active' : 'Inactive' }}</td>
                        <td><v-btn size="small" variant="tonal" @click="editProject(project)">Edit</v-btn></td>
                      </tr>
                    </tbody>
                  </v-table>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-window-item>

        <v-window-item value="timesheets">
          <v-card class="glass-card pa-6 mt-4">
            <p class="mini-kicker mb-3">Employee timesheets</p>
            <p v-if="timesheets.length === 0" class="page-copy">Submitted timesheets will appear here for approval.</p>
            <div v-else class="table-scroll">
              <v-table class="transparent-table">
                <thead>
                  <tr>
                    <th>Employee</th>
                    <th>Project</th>
                    <th>Date</th>
                    <th>Hours</th>
                    <th>Work status</th>
                    <th>Review</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="entry in timesheets" :key="entry.id">
                    <td>{{ entry.employeeName }}<br /><span class="meta-label">{{ entry.employeeCode || '-' }}</span></td>
                    <td>{{ entry.projectName }}</td>
                    <td>{{ entry.workDate }}</td>
                    <td>{{ Number(entry.hours).toFixed(2) }}</td>
                    <td class="message-cell">{{ entry.workDescription }}</td>
                    <td>
                      <v-chip :color="statusColor(entry.status)" size="small" variant="tonal">{{ entry.status }}</v-chip>
                      <p v-if="entry.reviewNote" class="meta-label mt-2">{{ entry.reviewNote }}</p>
                      <v-btn
                        v-if="entry.status === 'SUBMITTED'"
                        class="mt-2"
                        size="small"
                        color="primary"
                        variant="tonal"
                        @click="openReview(entry)"
                      >
                        Review
                      </v-btn>
                    </td>
                  </tr>
                </tbody>
              </v-table>
            </div>
          </v-card>
        </v-window-item>
      </v-window>

      <v-dialog v-model="reviewDialog" max-width="520">
        <v-card class="glass-card pa-6">
          <p class="mini-kicker">Review timesheet</p>
          <p class="page-copy">
            {{ reviewTarget?.employeeName }} · {{ reviewTarget?.projectName }} · {{ reviewTarget?.workDate }}
          </p>
          <v-textarea v-model="reviewNote" class="mt-4" label="Review note" rows="3" />
          <div class="d-flex ga-2 flex-wrap">
            <v-btn :loading="reviewSaving" color="success" @click="onReview('APPROVED')">Approve</v-btn>
            <v-btn :loading="reviewSaving" color="error" variant="tonal" @click="onReview('REJECTED')">Reject</v-btn>
            <v-btn variant="text" @click="reviewDialog = false">Cancel</v-btn>
          </div>
        </v-card>
      </v-dialog>
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

.table-scroll {
  overflow-x: auto;
}

.transparent-table :deep(th),
.transparent-table :deep(td) {
  color: var(--text-card-copy);
  vertical-align: top;
}

.meta-label {
  color: var(--text-kicker);
  font-size: 0.75rem;
}

.message-cell {
  max-width: 340px;
  white-space: pre-line;
}
</style>
