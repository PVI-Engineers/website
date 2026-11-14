<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { clearSession, getStoredUser } from '../services/portalApi'

const props = defineProps({
  eyebrow: {
    type: String,
    required: true,
  },
  items: {
    type: Array,
    default: () => [],
  },
  section: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:section'])
const router = useRouter()
const theme = useTheme()
const mobileNav = ref(false)
const currentUser = computed(() => getStoredUser())
const isDarkTheme = computed(() => theme.global.name.value === 'mouwDark')
const themeToggleIcon = computed(() => (isDarkTheme.value ? 'mdi-white-balance-sunny' : 'mdi-weather-night'))
const themeToggleLabel = computed(() => (isDarkTheme.value ? 'Switch to light theme' : 'Switch to dark theme'))

const activeLabel = computed(() => {
  const match = props.items.find((item) => item.id === props.section)
  return match?.label || props.eyebrow
})

const initials = computed(() => {
  const name = currentUser.value?.fullName || currentUser.value?.username || 'User'
  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase()
})

function choose(id) {
  emit('update:section', id)
  mobileNav.value = false
}

function applyTheme(themeName) {
  const selected = themeName === 'mouwDark' ? 'mouwDark' : 'mouwLight'
  const mode = selected === 'mouwDark' ? 'dark' : 'light'
  theme.global.name.value = selected
  document.body.setAttribute('data-site-theme', mode)
  document.documentElement.style.colorScheme = mode
  window.localStorage.setItem('mouw-theme', selected)
}

function toggleTheme() {
  applyTheme(isDarkTheme.value ? 'mouwLight' : 'mouwDark')
}

function logout() {
  clearSession()
  router.replace('/portal/login')
}
</script>

<template>
  <div class="portal-shell">
    <aside class="portal-side">
      <div class="portal-brand">
        <span class="portal-mark">MOUW</span>
        <div>
          <strong>MOUW Engineering</strong>
          <small>{{ eyebrow }}</small>
        </div>
      </div>

      <nav class="portal-nav" aria-label="Portal navigation">
        <button
          v-for="item in items"
          :key="item.id"
          type="button"
          class="portal-nav-btn"
          :class="{ active: section === item.id }"
          @click="choose(item.id)"
        >
          <v-icon :icon="item.icon" size="18" />
          <span>{{ item.label }}</span>
        </button>
      </nav>

      <div class="portal-side-footer">
        <div class="portal-user">
          <span class="portal-avatar">{{ initials }}</span>
          <div>
            <strong>{{ currentUser?.fullName || currentUser?.username }}</strong>
            <small>{{ currentUser?.email || currentUser?.username }}</small>
          </div>
        </div>
        <button type="button" class="portal-logout" @click="logout">
          <v-icon icon="mdi-logout" size="18" />
          <span>Logout</span>
        </button>
      </div>
    </aside>

    <section class="portal-workspace">
      <header class="portal-top">
        <v-btn class="d-md-none" icon="mdi-menu" variant="text" aria-label="Open portal menu" @click="mobileNav = true" />
        <div class="portal-heading">
          <p class="portal-eyebrow">{{ eyebrow }}</p>
          <h1>{{ activeLabel }}</h1>
        </div>
        <div class="portal-actions">
          <v-btn
            :icon="themeToggleIcon"
            :title="themeToggleLabel"
            :aria-label="themeToggleLabel"
            class="theme-toggle-btn"
            variant="tonal"
            @click="toggleTheme"
          />
          <button type="button" class="portal-logout" @click="logout">
            <v-icon icon="mdi-logout" size="18" />
            <span>Logout</span>
          </button>
        </div>
      </header>
      <div class="portal-content">
        <slot />
      </div>
    </section>

    <v-navigation-drawer v-model="mobileNav" class="portal-drawer d-md-none" temporary>
      <div class="pa-4">
        <p class="portal-eyebrow">{{ eyebrow }}</p>
        <button
          v-for="item in items"
          :key="`mobile-${item.id}`"
          type="button"
          class="portal-nav-btn"
          :class="{ active: section === item.id }"
          @click="choose(item.id)"
        >
          <v-icon :icon="item.icon" size="18" />
          <span>{{ item.label }}</span>
        </button>
        <button type="button" class="portal-logout mt-4" @click="logout">
          <v-icon icon="mdi-logout" size="18" />
          <span>Logout</span>
        </button>
      </div>
    </v-navigation-drawer>
  </div>
</template>

<style scoped>
.portal-shell {
  min-height: 100vh;
  display: flex;
  background: transparent;
}

.portal-side {
  display: none;
}

.portal-workspace {
  min-width: 0;
  flex: 1;
}

.portal-top {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 76px;
  padding: 14px 18px;
  background: var(--top-bar-bg);
  border-bottom: 1px solid var(--top-bar-border);
  backdrop-filter: blur(12px);
}

.portal-top h1 {
  margin: 2px 0 0;
  color: var(--text-title);
  font-size: 1.25rem;
  line-height: 1.2;
}

.portal-eyebrow {
  margin: 0;
  color: var(--text-kicker);
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.portal-top .portal-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.theme-toggle-btn {
  border: 1px solid var(--top-bar-border);
}

.portal-logout {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex: 0 0 auto;
  height: 40px;
  padding: 0 14px;
  border: 1px solid rgba(223, 95, 112, 0.35);
  border-radius: 999px;
  background: transparent;
  color: #df5f70;
  font: inherit;
  font-size: 0.86rem;
  font-weight: 700;
  cursor: pointer;
}

.portal-side .portal-logout {
  width: 100%;
}

.portal-content {
  padding: 8px 8px 28px;
}

.portal-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 6px 18px;
}

.portal-brand strong,
.portal-user strong {
  display: block;
  color: var(--text-title);
  font-size: 0.84rem;
}

.portal-brand small,
.portal-user small {
  display: block;
  margin-top: 2px;
  color: var(--text-copy);
  font-size: 0.72rem;
}

.portal-mark {
  display: grid;
  place-items: center;
  width: 46px;
  height: 46px;
  border-radius: 14px;
  color: var(--brand-mark-text);
  background: var(--brand-mark-gradient);
  font-size: 0.62rem;
  font-weight: 800;
}

.portal-nav {
  display: grid;
  gap: 6px;
}

.portal-nav-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  border: 0;
  border-radius: 12px;
  padding: 11px 12px;
  background: transparent;
  color: var(--text-copy);
  text-align: left;
  cursor: pointer;
}

.portal-nav-btn.active {
  background: rgba(31, 136, 229, 0.14);
  color: var(--text-title);
  font-weight: 700;
}

.portal-side-footer {
  margin-top: auto;
  padding-top: 16px;
}

.portal-user {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.portal-avatar {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #1f88e5;
  color: white;
  font-size: 0.72rem;
  font-weight: 800;
}

.portal-drawer {
  background: var(--drawer-bg);
}

@media (min-width: 960px) {
  .portal-side {
    position: sticky;
    top: 0;
    display: flex;
    flex-direction: column;
    width: 260px;
    height: 100vh;
    padding: 22px 16px 18px;
    background: var(--surface-card-bg);
    border-right: 1px solid var(--surface-card-border);
  }

  .portal-content {
    padding: 18px 22px 40px;
  }

  .portal-top {
    padding-inline: 28px;
  }
}
</style>
