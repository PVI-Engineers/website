<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  clearSession,
  fetchCurrentUser,
  fetchEmployeeHome,
  getStoredUser,
} from '../services/portalApi'

const days = ['MON 14', 'TUE 15', 'WED 16', 'THU 17', 'FRI 18', 'SAT 19', 'SUN 20']

const seedRows = [
  {
    id: 1,
    project: 'Riverside Bridge Rehabilitation',
    phase: 'Final design · Structural',
    code: '24-104 / 320',
    billable: true,
    hours: [7.5, 6, 4, 7, 5.5, 0, 0],
  },
  {
    id: 2,
    project: 'Northpoint Stormwater Master Plan',
    phase: 'Hydrologic modeling',
    code: '25-018 / 210',
    billable: true,
    hours: [0, 2, 3.5, 1, 2, 0, 0],
  },
  {
    id: 3,
    project: 'Cedar Avenue Streetscape',
    phase: 'Utility coordination',
    code: '24-087 / 450',
    billable: true,
    hours: [0, 0, 0, 0, 1.5, 0, 0],
  },
  {
    id: 4,
    project: 'Business development',
    phase: 'River County SOQ',
    code: 'OH-100 / BD',
    billable: false,
    hours: [0.5, 0, 0, 0, 0, 0, 0],
  },
]

const router = useRouter()
const loading = ref(true)
const errorMessage = ref('')
const currentUser = ref(getStoredUser())
const rows = ref(seedRows.map((row) => ({ ...row, hours: [...row.hours] })))
const status = ref('draft')
const note = ref('')
const toast = ref('')
const activePage = ref('timesheet')
let toastTimer = 0
let previousTitle = ''

const totals = computed(() => {
  const byDay = days.map((_, day) => rows.value.reduce((sum, row) => sum + (row.hours[day] || 0), 0))
  const total = byDay.reduce((sum, value) => sum + value, 0)
  const billable = rows.value
    .filter((row) => row.billable)
    .reduce((sum, row) => sum + row.hours.reduce((a, b) => a + b, 0), 0)
  return { byDay, total, billable }
})

const displayName = computed(() => currentUser.value?.fullName || currentUser.value?.username || 'Employee')

const initials = computed(() => {
  const parts = displayName.value.split(/\s+/).filter(Boolean)
  const letters = parts.slice(0, 2).map((part) => part[0])
  return (letters.join('') || 'E').toUpperCase()
})

const roleLabel = computed(() => {
  const roles = Array.isArray(currentUser.value?.roles) ? currentUser.value.roles : []
  if (roles.includes('ADMIN')) return 'Administrator'
  if (roles.includes('HR')) return 'Human Resources'
  return 'Employee'
})

const pages = [
  { id: 'timesheet', icon: '▦', label: 'My timesheet' },
  { id: 'projects', icon: '⌂', label: 'Projects' },
  { id: 'approvals', icon: '✓', label: 'Approvals', badge: '3' },
  { id: 'reports', icon: '↗', label: 'Reports' },
]

function showToast(message) {
  toast.value = message
  window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => {
    toast.value = ''
  }, 2600)
}

function setHours(rowId, day, value) {
  const parsed = Math.max(0, Math.min(24, Number(value) || 0))
  rows.value = rows.value.map((row) =>
    row.id === rowId
      ? { ...row, hours: row.hours.map((hour, index) => (index === day ? parsed : hour)) }
      : row,
  )
  status.value = 'draft'
}

function addRow() {
  rows.value = [
    ...rows.value,
    {
      id: Date.now(),
      project: 'General overhead',
      phase: 'Select a phase',
      code: 'OH-100 / GEN',
      billable: false,
      hours: [0, 0, 0, 0, 0, 0, 0],
    },
  ]
}

function rowTotal(row) {
  return row.hours.reduce((sum, hour) => sum + hour, 0)
}

function openPage(pageId) {
  activePage.value = pageId
}

function logout() {
  clearSession()
  router.replace('/portal/login')
}

async function loadPortalData() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [profile] = await Promise.all([fetchCurrentUser(), fetchEmployeeHome()])
    currentUser.value = profile
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to load employee portal.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  previousTitle = document.title
  document.title = 'MOUW Engineering | Time & Expense'
  loadPortalData()
})

onUnmounted(() => {
  window.clearTimeout(toastTimer)
  if (previousTitle) document.title = previousTitle
})
</script>

<template>
  <main class="timesheet-app">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark"><span>M</span></span>
        <span>MOUW ENGINEERING<small>CIVIL ENGINEERING</small></span>
      </div>

      <nav aria-label="Primary navigation">
        <button
          v-for="page in pages"
          :key="page.id"
          class="nav-item"
          :class="{ active: activePage === page.id }"
          @click="openPage(page.id)"
        >
          <span>{{ page.icon }}</span>
          {{ page.label }}
          <b v-if="page.badge">{{ page.badge }}</b>
        </button>
      </nav>

      <div class="sidebar-bottom">
        <button class="nav-item" :class="{ active: activePage === 'settings' }" @click="openPage('settings')">
          <span>⚙</span>
          Settings
        </button>
        <div class="profile">
          <div class="avatar">{{ initials }}</div>
          <div>
            <strong>{{ displayName }}</strong>
            <small>{{ roleLabel }}</small>
          </div>
          <button aria-label="Log out" title="Log out" @click="logout">⎋</button>
        </div>
      </div>
    </aside>

    <section class="workspace">
      <header class="topbar">
        <div>
          <span class="eyebrow">TIME &amp; EXPENSE</span>
          <h1>{{ activePage === 'timesheet' ? 'Weekly timesheet' : pages.find((page) => page.id === activePage)?.label || 'Settings' }}</h1>
        </div>
        <div class="header-actions">
          <button class="icon-button" aria-label="Notifications" @click="showToast('No new notifications.')">
            ♢
            <i />
          </button>
          <button class="help" aria-label="Help" @click="showToast('Enter time in quarter-hour increments, then submit the week.')">?</button>
        </div>
      </header>

      <div v-if="loading" class="content">
        <section class="sheet-card">
          <div class="sheet-head">
            <div>
              <h2>Loading workspace</h2>
              <p>Checking your employee session.</p>
            </div>
          </div>
        </section>
      </div>

      <div v-else-if="errorMessage" class="content">
        <section class="sheet-card">
          <div class="sheet-head">
            <div>
              <h2>Unable to open timesheet</h2>
              <p>{{ errorMessage }}</p>
            </div>
            <button class="ghost" @click="logout">Log out</button>
          </div>
        </section>
      </div>

      <div v-else-if="activePage === 'timesheet'" class="content">
        <section class="week-strip">
          <button class="week-arrow" aria-label="Previous week" @click="showToast('Earlier weeks will be available when timesheets are saved.')">‹</button>
          <div>
            <span class="eyebrow">WEEK 29</span>
            <strong>July 14 – 20, 2026</strong>
            <small>Current week</small>
          </div>
          <button class="week-arrow" aria-label="Next week" @click="showToast('You are on the current week.')">›</button>
          <button class="today" @click="showToast('Already viewing the current week.')">Today</button>
          <div class="status" :class="status">
            <span />
            {{ status === 'draft' ? 'Draft' : 'Submitted' }}
          </div>
        </section>

        <section class="metric-grid" aria-label="Timesheet summary">
          <div class="metric primary">
            <span>WEEK TOTAL</span>
            <strong>{{ totals.total.toFixed(1) }}<small> / 40 hrs</small></strong>
            <div class="progress"><i :style="{ width: `${Math.min(100, (totals.total / 40) * 100)}%` }" /></div>
            <p>{{ Math.max(0, 40 - totals.total).toFixed(1) }} hours remaining</p>
          </div>
          <div class="metric">
            <span>BILLABLE</span>
            <strong>{{ totals.billable.toFixed(1) }}<small> hrs</small></strong>
            <p><em>▲ 4%</em> from last week</p>
          </div>
          <div class="metric">
            <span>UTILIZATION</span>
            <strong>{{ totals.total ? Math.round((totals.billable / totals.total) * 100) : 0 }}<small>%</small></strong>
            <p>Target <b>82%</b></p>
          </div>
          <div class="metric">
            <span>PROJECTS</span>
            <strong>{{ rows.filter((row) => row.billable).length }}</strong>
            <p>Across 3 disciplines</p>
          </div>
        </section>

        <section class="sheet-card">
          <div class="sheet-head">
            <div>
              <h2>Time entries</h2>
              <p>Enter time in quarter-hour increments.</p>
            </div>
            <button class="ghost" @click="addRow">＋ Add project</button>
          </div>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>PROJECT / PHASE</th>
                  <th v-for="(day, index) in days" :key="day" :class="{ 'today-col': index === 4 }">{{ day }}</th>
                  <th>TOTAL</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in rows" :key="row.id">
                  <td>
                    <div class="project-dot" :class="{ overhead: !row.billable }" />
                    <div class="project-name">
                      <strong>{{ row.project }}</strong>
                      <span>{{ row.phase }}</span>
                      <small>{{ row.code }} · {{ row.billable ? 'Billable' : 'Non-billable' }}</small>
                    </div>
                  </td>
                  <td v-for="(hour, day) in row.hours" :key="day" :class="{ 'today-col': day === 4 }">
                    <input
                      :aria-label="`${row.project} ${days[day]} hours`"
                      type="number"
                      min="0"
                      max="24"
                      step="0.25"
                      :value="hour || ''"
                      placeholder="—"
                      @input="setHours(row.id, day, $event.target.value)"
                    />
                  </td>
                  <td class="row-total">{{ rowTotal(row).toFixed(1) }}</td>
                  <td>
                    <button class="more" :aria-label="`Options for ${row.project}`" @click="showToast(`${row.project} stays on this timesheet.`)">•••</button>
                  </td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td>DAILY TOTAL</td>
                  <td v-for="(total, index) in totals.byDay" :key="index" :class="{ 'today-col': index === 4 }">
                    <strong>{{ total ? total.toFixed(1) : '—' }}</strong>
                    <small>{{ total >= 8 ? '✓' : total ? `${(8 - total).toFixed(1)} short` : '' }}</small>
                  </td>
                  <td>{{ totals.total.toFixed(1) }}</td>
                  <td />
                </tr>
              </tfoot>
            </table>
          </div>
          <div class="sheet-footer">
            <label>
              <span>WEEKLY NOTE <small>OPTIONAL</small></span>
              <textarea v-model="note" placeholder="Add context for your project managers or approver…" />
            </label>
            <div class="submit-zone">
              <p>By submitting, you confirm these hours are accurate.</p>
              <button class="save" @click="showToast('Draft saved locally.')">Save draft</button>
              <button
                class="submit"
                @click="status = 'submitted'; showToast('Timesheet submitted for approval.')"
              >
                Submit timesheet <span>→</span>
              </button>
            </div>
          </div>
        </section>

        <section class="budget-note">
          <div class="budget-icon">⌁</div>
          <div>
            <strong>Budget watch</strong>
            <p>Riverside Bridge Rehabilitation is at <b>76% of phase budget</b> with 64 design hours remaining.</p>
          </div>
          <button @click="openPage('projects')">View project →</button>
        </section>
      </div>

      <div v-else-if="activePage === 'projects'" class="content">
        <section class="sheet-card">
          <div class="sheet-head">
            <div>
              <h2>Assigned projects</h2>
              <p>Projects currently on your timesheet.</p>
            </div>
          </div>
          <div class="panel-list">
            <article v-for="row in rows" :key="row.id" class="panel-row">
              <div class="project-dot" :class="{ overhead: !row.billable }" />
              <div class="project-name">
                <strong>{{ row.project }}</strong>
                <span>{{ row.phase }}</span>
                <small>{{ row.code }} · {{ row.billable ? 'Billable' : 'Non-billable' }} · {{ rowTotal(row).toFixed(1) }} hrs</small>
              </div>
            </article>
          </div>
        </section>
      </div>

      <div v-else-if="activePage === 'approvals'" class="content">
        <section class="sheet-card">
          <div class="sheet-head">
            <div>
              <h2>Approvals</h2>
              <p>This week is {{ status === 'submitted' ? 'waiting for approval' : 'still a draft' }}.</p>
            </div>
          </div>
          <div class="panel-list">
            <article class="panel-row">
              <div>
                <strong>Week of July 14 – 20, 2026</strong>
                <span>{{ totals.total.toFixed(1) }} hours · {{ status === 'submitted' ? 'Submitted' : 'Not submitted' }}</span>
              </div>
            </article>
            <article class="panel-row">
              <div>
                <strong>Open approval items</strong>
                <span>3 items are queued for a project manager.</span>
              </div>
            </article>
          </div>
        </section>
      </div>

      <div v-else-if="activePage === 'reports'" class="content">
        <section class="metric-grid" aria-label="Report summary">
          <div class="metric">
            <span>WEEK TOTAL</span>
            <strong>{{ totals.total.toFixed(1) }}<small> hrs</small></strong>
          </div>
          <div class="metric">
            <span>BILLABLE</span>
            <strong>{{ totals.billable.toFixed(1) }}<small> hrs</small></strong>
          </div>
          <div class="metric">
            <span>NON-BILLABLE</span>
            <strong>{{ (totals.total - totals.billable).toFixed(1) }}<small> hrs</small></strong>
          </div>
          <div class="metric">
            <span>UTILIZATION</span>
            <strong>{{ totals.total ? Math.round((totals.billable / totals.total) * 100) : 0 }}<small>%</small></strong>
          </div>
        </section>
      </div>

      <div v-else class="content">
        <section class="sheet-card">
          <div class="sheet-head">
            <div>
              <h2>Account</h2>
              <p>Signed in to the employee portal.</p>
            </div>
            <button class="ghost" @click="logout">Log out</button>
          </div>
          <div class="panel-list">
            <article class="panel-row"><div><strong>Name</strong><span>{{ displayName }}</span></div></article>
            <article class="panel-row"><div><strong>Username</strong><span>{{ currentUser?.username || '—' }}</span></div></article>
            <article class="panel-row"><div><strong>Email</strong><span>{{ currentUser?.email || '—' }}</span></div></article>
            <article class="panel-row"><div><strong>Role</strong><span>{{ roleLabel }}</span></div></article>
          </div>
        </section>
      </div>
    </section>

    <div v-if="toast" class="toast" role="status">✓ {{ toast }}</div>
  </main>
</template>

<style scoped>
.timesheet-app {
  --ink: #152724;
  --green: #16664c;
  --green2: #208461;
  --line: #dce5e1;
  min-height: 100vh;
  display: flex;
  background: #f6f7f5;
  color: var(--ink);
  font-family: Arial, sans-serif;
}

.timesheet-app button,
.timesheet-app input,
.timesheet-app textarea {
  font-family: inherit;
}

.timesheet-app button {
  cursor: pointer;
}

.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  width: 236px;
  background: #113c31;
  color: #eaf5f1;
  padding: 28px 18px 20px;
  display: flex;
  flex-direction: column;
  z-index: 4;
  overflow: hidden;
}

.brand {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 0 8px 28px;
  font-size: 14px;
  font-weight: 750;
  letter-spacing: 0.12em;
}

.brand-mark {
  width: 34px;
  height: 34px;
  min-width: 34px;
  border: 1px solid #94b8aa;
  display: grid;
  place-items: center;
  font-family: Georgia, serif;
  font-size: 21px;
  transform: rotate(45deg);
}

.brand-mark span {
  display: block;
  transform: rotate(-45deg);
}

.brand small,
.profile small {
  display: block;
  font-size: 8px;
  letter-spacing: 0.12em;
  opacity: 0.62;
  margin-top: 3px;
  font-weight: 500;
}

nav {
  display: grid;
  gap: 6px;
}

.nav-item {
  border: 0;
  color: #c2d5ce;
  background: transparent;
  padding: 11px 12px;
  border-radius: 7px;
  display: flex;
  align-items: center;
  gap: 12px;
  text-align: left;
  width: 100%;
  font-size: 13px;
}

.nav-item span {
  width: 18px;
  text-align: center;
  font-size: 16px;
}

.nav-item b {
  margin-left: auto;
  background: #d88355;
  color: white;
  border-radius: 12px;
  padding: 2px 7px;
  font-size: 10px;
}

.nav-item.active {
  background: #e9f3ef;
  color: #164c3b;
  font-weight: 700;
}

.sidebar-bottom {
  margin-top: auto;
}

.profile {
  border-top: 1px solid #31564d;
  margin-top: 15px;
  padding: 19px 5px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.profile strong {
  font-size: 11px;
}

.profile small {
  font-size: 8px;
  letter-spacing: 0;
}

.profile button {
  color: #adc3bb;
  background: none;
  border: 0;
  margin-left: auto;
}

.avatar {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #c9875d;
  color: white;
  font-size: 10px;
  font-weight: 800;
}

.workspace {
  margin-left: 236px;
  width: calc(100% - 236px);
}

.topbar {
  height: 86px;
  background: white;
  border-bottom: 1px solid var(--line);
  padding: 18px 34px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.eyebrow {
  color: #7c8b86;
  font-size: 9px;
  font-weight: 800;
  letter-spacing: 0.14em;
}

.topbar h1 {
  margin: 4px 0 0;
  font-family: Georgia, serif;
  font-size: 24px;
  font-weight: 500;
  color: var(--ink);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.icon-button,
.help {
  width: 34px;
  height: 34px;
  border: 1px solid var(--line);
  background: white;
  border-radius: 50%;
  position: relative;
  color: #48615a;
}

.icon-button i {
  width: 5px;
  height: 5px;
  background: #d87844;
  border-radius: 50%;
  position: absolute;
  top: 4px;
  right: 4px;
}

.content {
  max-width: 1320px;
  padding: 25px 34px 55px;
  margin: auto;
}

.week-strip {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
}

.week-strip > div:nth-child(2) {
  min-width: 245px;
}

.week-strip strong {
  display: block;
  font-family: Georgia, serif;
  font-size: 17px;
  margin-top: 3px;
}

.week-strip small {
  color: var(--green2);
  font-size: 10px;
}

.week-arrow,
.today,
.ghost {
  background: white;
  border: 1px solid var(--line);
  border-radius: 5px;
  color: #50645e;
}

.week-arrow {
  width: 30px;
  height: 30px;
  font-size: 22px;
}

.today {
  padding: 7px 14px;
  font-size: 11px;
}

.status {
  margin-left: auto;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 9px;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 7px;
}

.status span {
  width: 7px;
  height: 7px;
  background: #d58a62;
  border-radius: 50%;
}

.status.submitted {
  color: var(--green);
}

.status.submitted span {
  background: var(--green2);
}

.metric-grid {
  display: grid;
  grid-template-columns: 1.45fr repeat(3, 1fr);
  gap: 1px;
  background: var(--line);
  border: 1px solid var(--line);
  border-radius: 7px;
  overflow: hidden;
  margin-bottom: 18px;
}

.metric {
  background: white;
  min-height: 113px;
  padding: 18px 21px;
}

.metric span {
  font-size: 9px;
  color: #76857f;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.metric strong {
  display: block;
  font-family: Georgia, serif;
  font-size: 26px;
  margin: 9px 0 7px;
  font-weight: 500;
}

.metric strong small {
  font-family: Arial, sans-serif;
  font-size: 11px;
  color: #788984;
}

.metric p {
  font-size: 10px;
  color: #87918e;
  margin: 4px 0;
}

.metric em {
  color: var(--green2);
  font-style: normal;
}

.progress {
  width: 140px;
  height: 4px;
  border-radius: 3px;
  background: #e0e7e4;
  overflow: hidden;
}

.progress i {
  display: block;
  height: 100%;
  background: var(--green2);
}

.sheet-card {
  background: white;
  border: 1px solid var(--line);
  border-radius: 7px;
  overflow: hidden;
}

.sheet-head {
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--line);
}

.sheet-head h2 {
  font-family: Georgia, serif;
  font-size: 17px;
  margin: 0 0 3px;
}

.sheet-head p {
  margin: 0;
  color: #84908d;
  font-size: 10px;
}

.ghost {
  color: var(--green);
  font-weight: 700;
  padding: 8px 13px;
  font-size: 10px;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

th {
  padding: 11px 5px;
  background: #fafbf9;
  border-bottom: 1px solid var(--line);
  color: #7a8984;
  font-size: 8px;
  letter-spacing: 0.07em;
}

th:first-child {
  width: 310px;
  text-align: left;
  padding-left: 20px;
}

th:not(:first-child) {
  width: 73px;
}

th:nth-last-child(2) {
  width: 62px;
}

th:last-child {
  width: 34px;
}

td {
  border-bottom: 1px solid #e8eeeb;
  padding: 10px 5px;
  text-align: center;
  height: 70px;
}

td:first-child {
  text-align: left;
  padding-left: 20px;
  position: relative;
}

.project-dot {
  display: inline-block;
  width: 7px;
  height: 38px;
  background: #2a8566;
  border-radius: 4px;
  margin-right: 12px;
  vertical-align: top;
}

.project-dot.overhead {
  background: #c2a76a;
}

.project-name {
  display: inline-flex;
  flex-direction: column;
  gap: 3px;
  max-width: 255px;
}

.project-name strong {
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.project-name span {
  font-size: 9px;
  color: #697873;
}

.project-name small {
  font-size: 8px;
  color: #a0aaa7;
  letter-spacing: 0.04em;
}

td input {
  width: 48px;
  height: 34px;
  border: 1px solid transparent;
  border-radius: 4px;
  text-align: center;
  color: #29423b;
  background: #f8faf9;
  font-size: 11px;
}

td input:hover,
td input:focus {
  outline: none;
  border-color: #75a993;
  background: white;
}

td input::placeholder {
  color: #c4ceca;
}

.today-col {
  background: #f1f7f4 !important;
}

.row-total {
  font-weight: 800;
  font-size: 11px;
}

.more {
  background: none;
  border: 0;
  color: #92a09b;
  letter-spacing: 1px;
}

tfoot td {
  height: 56px;
  background: #fafbf9;
  border: 0;
  font-size: 9px;
  font-weight: 800;
  color: #536661;
}

tfoot td strong {
  display: block;
  font-size: 11px;
}

tfoot td small {
  display: block;
  color: #cc784c;
  font-size: 7px;
  margin-top: 3px;
  font-weight: 600;
}

.sheet-footer {
  padding: 18px 20px;
  display: grid;
  grid-template-columns: minmax(260px, 1fr) auto;
  align-items: end;
  gap: 30px;
  border-top: 1px solid var(--line);
}

.sheet-footer label span {
  display: block;
  font-size: 8px;
  font-weight: 800;
  letter-spacing: 0.1em;
  margin-bottom: 7px;
}

.sheet-footer label small {
  font-size: 7px;
  color: #a1aaa7;
  margin-left: 5px;
}

.sheet-footer textarea {
  resize: none;
  height: 42px;
  width: 100%;
  border: 1px solid var(--line);
  border-radius: 5px;
  padding: 11px;
  font-size: 10px;
}

.submit-zone {
  display: flex;
  align-items: center;
  gap: 10px;
}

.submit-zone p {
  font-size: 8px;
  color: #929c99;
  width: 125px;
  text-align: right;
}

.save,
.submit {
  border-radius: 5px;
  padding: 11px 16px;
  font-size: 10px;
  font-weight: 750;
}

.save {
  border: 1px solid var(--line);
  background: white;
  color: #4e625c;
}

.submit {
  border: 0;
  background: var(--green);
  color: white;
}

.submit span {
  margin-left: 10px;
}

.budget-note {
  margin-top: 16px;
  border: 1px solid #e6ddd0;
  background: #fbf8f1;
  border-radius: 6px;
  padding: 14px 18px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.budget-icon {
  width: 31px;
  height: 31px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #f0dfc7;
  color: #9a6b31;
}

.budget-note strong {
  font-size: 10px;
}

.budget-note p {
  margin: 3px 0 0;
  font-size: 9px;
  color: #7c817c;
}

.budget-note button {
  margin-left: auto;
  border: 0;
  background: none;
  color: #946b36;
  font-size: 9px;
  font-weight: 800;
}

.panel-list {
  display: grid;
}

.panel-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #e8eeeb;
}

.panel-row strong,
.panel-row span {
  display: block;
}

.panel-row span {
  margin-top: 3px;
  color: #697873;
  font-size: 12px;
}

.toast {
  position: fixed;
  right: 24px;
  bottom: 24px;
  background: #153f34;
  color: white;
  border-radius: 7px;
  padding: 13px 18px;
  font-size: 11px;
  box-shadow: 0 10px 30px #102f2455;
  z-index: 6;
}

@media (max-width: 900px) {
  .sidebar {
    width: 72px;
    padding-inline: 10px;
  }

  .timesheet-app .brand > span:last-child,
  .timesheet-app .nav-item {
    font-size: 0;
  }

  .brand {
    padding-inline: 8px;
  }

  .nav-item span {
    font-size: 18px;
  }

  .nav-item b {
    display: none;
  }

  .profile > div:not(.avatar),
  .profile button {
    display: none;
  }

  .workspace {
    margin-left: 72px;
    width: calc(100% - 72px);
  }

  .metric-grid {
    grid-template-columns: 1fr 1fr;
  }

  .sheet-footer {
    grid-template-columns: 1fr;
  }

  .submit-zone {
    justify-content: flex-end;
  }
}

@media (max-width: 620px) {
  .content,
  .topbar {
    padding-left: 16px;
    padding-right: 16px;
  }

  .metric-grid {
    grid-template-columns: 1fr;
  }

  .metric {
    min-height: auto;
  }

  .week-strip > div:nth-child(2) {
    min-width: 150px;
  }

  .week-strip .today {
    display: none;
  }

  .sheet-footer {
    padding: 14px;
  }

  .submit-zone {
    flex-wrap: wrap;
  }

  .submit-zone p {
    width: 100%;
    text-align: left;
  }

  .budget-note button {
    display: none;
  }
}

@media (prefers-reduced-motion: no-preference) {
  .toast {
    animation: toast-in 0.25s ease-out;
  }

  @keyframes toast-in {
    from {
      transform: translateY(8px);
      opacity: 0;
    }
  }
}
</style>
