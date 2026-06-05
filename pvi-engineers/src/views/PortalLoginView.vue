<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getStoredToken, getStoredUser, loginPortal, resolvePortalRoute } from '../services/portalApi'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  username: '',
  password: '',
})

const required = (value) => !!value || 'This field is required'

function redirectByRole(roles = []) {
  const requestedPath = typeof route.query.redirect === 'string' ? route.query.redirect : ''
  const fallbackPath = resolvePortalRoute(roles)

  if (requestedPath && requestedPath.startsWith('/portal/')) {
    router.replace(requestedPath)
    return
  }

  router.replace(fallbackPath)
}

onMounted(() => {
  const token = getStoredToken()
  const user = getStoredUser()
  if (!token || !user) return

  redirectByRole(user.roles || [])
})

async function submitLogin() {
  if (!formRef.value) return

  errorMessage.value = ''
  const { valid } = await formRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const data = await loginPortal(form.username.trim(), form.password)
    redirectByRole(data.roles || [])
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Unable to login right now.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">Internal Portal</p>
      <h1 class="page-title">Employee and HR Login</h1>
      <p class="page-copy">
        Login with your portal credentials to access role-specific dashboards for Employee, HR, and
        Admin operations.
      </p>
    </section>

    <v-card class="glass-card pa-6 mt-8">
      <p class="mini-kicker mb-3">Portal Access</p>
      <v-form ref="formRef" @submit.prevent="submitLogin">
        <v-row dense>
          <v-col cols="12">
            <v-text-field v-model="form.username" :rules="[required]" label="Username or Email *" />
          </v-col>
          <v-col cols="12">
            <v-text-field
              v-model="form.password"
              :rules="[required]"
              label="Password *"
              type="password"
            />
          </v-col>
        </v-row>

        <v-alert v-if="errorMessage" class="mt-2" type="error" variant="tonal" border="start">
          {{ errorMessage }}
        </v-alert>

        <div class="d-flex ga-3 mt-5 flex-wrap">
          <v-btn :loading="loading" color="primary" type="submit">Login</v-btn>
          <v-btn to="/careers/open-positions" variant="tonal">Back to Careers</v-btn>
        </div>
      </v-form>
    </v-card>

    <v-card class="glass-card pa-6 mt-6">
      <p class="mini-kicker mb-3">Default Accounts (Change in environment)</p>
      <ul class="quick-list">
        <li>Admin: <code>admin@pvi.local</code> / <code>Admin@123</code></li>
        <li>HR: <code>hr@pvi.local</code> / <code>Hr@123</code></li>
        <li>Employee: <code>employee@pvi.local</code> / <code>Employee@123</code></li>
      </ul>
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
  font-size: clamp(2rem, 4vw, 3rem);
}

.page-copy {
  margin: 18px 0 0;
  color: var(--text-copy);
  line-height: 1.75;
  max-width: 85ch;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
}

.quick-list {
  margin: 0;
  padding-left: 18px;
  color: var(--text-copy);
  line-height: 1.8;
}
</style>
