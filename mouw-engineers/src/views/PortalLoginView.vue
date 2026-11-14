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
  <v-container class="login-shell">
    <v-row justify="center">
      <v-col cols="12" sm="10" md="7" lg="5">
        <v-card class="glass-card pa-6 pa-sm-8">
          <p class="section-kicker">MOUW Engineering</p>
          <h1 class="page-title">Portal sign in</h1>
          <p class="page-copy">
            Use your employee, HR, or admin account. After sign in you stay in your portal until you log out.
          </p>
          <v-form ref="formRef" class="mt-6" @submit.prevent="submitLogin">
            <v-text-field v-model="form.username" :rules="[required]" label="Username or email" autocomplete="username" />
            <v-text-field
              v-model="form.password"
              :rules="[required]"
              label="Password"
              type="password"
              autocomplete="current-password"
            />
            <v-alert v-if="errorMessage" class="mb-4" type="error" variant="tonal" border="start">
              {{ errorMessage }}
            </v-alert>
            <v-btn :loading="loading" block color="primary" size="large" type="submit">Sign in</v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.login-shell {
  position: relative;
  z-index: 1;
  padding-top: 48px;
  padding-bottom: 48px;
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
