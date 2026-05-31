<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useTheme } from 'vuetify'
import { companyButtons, serviceButtons } from './data/siteNavigation'

const drawer = ref(false)
const openedGroups = ref([])
const route = useRoute()
const theme = useTheme()

const activePath = computed(() => route.path)
const desktopRows = [serviceButtons, companyButtons]
const desktopLabels = ['Service Domains', 'Company Sections']
const year = new Date().getFullYear()
const isDarkTheme = computed(() => theme.global.name.value === 'pviDark')
const themeToggleIcon = computed(() =>
  isDarkTheme.value ? 'mdi-white-balance-sunny' : 'mdi-weather-night',
)
const themeToggleLabel = computed(() =>
  isDarkTheme.value ? 'Switch to light theme' : 'Switch to dark theme',
)

function isGroupActive(item) {
  return activePath.value === item.path || activePath.value.startsWith(`${item.path}/`)
}

function closeDrawer() {
  drawer.value = false
}

function previewText(text, max = 106) {
  if (!text) return ''
  return text.length > max ? `${text.slice(0, max - 1)}…` : text
}

function applyTheme(themeName) {
  const selected = themeName === 'pviDark' ? 'pviDark' : 'pviLight'
  const mode = selected === 'pviDark' ? 'dark' : 'light'

  theme.global.name.value = selected
  document.body.setAttribute('data-site-theme', mode)
  document.documentElement.style.colorScheme = mode
  window.localStorage.setItem('pvi-theme', selected)
}

function toggleTheme() {
  applyTheme(isDarkTheme.value ? 'pviLight' : 'pviDark')
}

watch(
  () => route.path,
  () => {
    drawer.value = false
  },
)

onMounted(() => {
  const saved = window.localStorage.getItem('pvi-theme')
  applyTheme(saved === 'pviDark' || saved === 'pviLight' ? saved : 'pviLight')
})
</script>

<template>
  <v-app class="pvi-app">
    <div class="ambient-lights" aria-hidden="true">
      <span class="light-orb orb-one"></span>
      <span class="light-orb orb-two"></span>
      <span class="light-orb orb-three"></span>
    </div>

    <v-navigation-drawer
      v-model="drawer"
      class="mobile-drawer d-lg-none"
      location="right"
      temporary
    >
      <v-list v-model:opened="openedGroups" density="comfortable" nav>
        <v-list-item
          prepend-icon="mdi-home-city-outline"
          title="Home"
          to="/"
          @click="closeDrawer"
        />
        <v-list-item
          prepend-icon="mdi-account-key-outline"
          title="Portal Login"
          to="/portal/login"
          @click="closeDrawer"
        />
        <v-list-item
          :prepend-icon="themeToggleIcon"
          :title="themeToggleLabel"
          @click="toggleTheme"
        />

        <v-divider class="my-2" />
        <v-list-subheader>Service Domains</v-list-subheader>

        <v-list-group v-for="item in serviceButtons" :key="item.path" :value="item.path">
          <template #activator="{ props }">
            <v-list-item
              v-bind="props"
              :active="isGroupActive(item)"
              :prepend-icon="item.icon"
              :title="item.label"
            />
          </template>

          <v-list-item
            prepend-icon="mdi-view-dashboard-outline"
            title="Overview"
            :to="item.path"
            @click="closeDrawer"
          />
          <v-list-item
            v-for="child in item.children"
            :key="child.path"
            :prepend-icon="child.icon"
            :title="child.label"
            :to="child.path"
            @click="closeDrawer"
          />
        </v-list-group>

        <v-divider class="my-2" />
        <v-list-subheader>Company</v-list-subheader>

        <v-list-group v-for="item in companyButtons" :key="item.path" :value="item.path">
          <template #activator="{ props }">
            <v-list-item
              v-bind="props"
              :active="isGroupActive(item)"
              :prepend-icon="item.icon"
              :title="item.label"
            />
          </template>

          <v-list-item
            prepend-icon="mdi-view-dashboard-outline"
            title="Overview"
            :to="item.path"
            @click="closeDrawer"
          />
          <v-list-item
            v-for="child in item.children"
            :key="child.path"
            :prepend-icon="child.icon"
            :title="child.label"
            :to="child.path"
            @click="closeDrawer"
          />
        </v-list-group>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar class="top-bar" flat height="110">
      <v-container class="top-inner">
        <router-link class="brand-link" to="/">
          <span class="brand-mark">PVI</span>
          <span class="brand-copy">
            <strong>PVI ENGINEERS</strong>
            <small>Road, Water, Drainage & Civil Systems</small>
          </span>
        </router-link>

        <div class="desktop-nav d-none d-lg-flex">
          <div v-for="(row, rowIndex) in desktopRows" :key="rowIndex" class="nav-row">
            <div
              v-for="item in row"
              :key="item.path"
              class="menu-trigger"
              :class="{ 'menu-trigger-active': isGroupActive(item) }"
            >
              <v-btn
                class="menu-btn"
                :color="isGroupActive(item) ? 'primary' : undefined"
                :to="item.path"
                :variant="isGroupActive(item) ? 'flat' : 'text'"
              >
                {{ item.label }}
                <v-icon icon="mdi-chevron-down" size="16" class="ml-1" />
              </v-btn>

              <v-menu
                activator="parent"
                :close-delay="170"
                location="bottom start"
                :open-delay="90"
                :offset="8"
                open-on-hover
              >
                <v-card class="mega-menu-card" rounded="xl" width="780">
                  <v-row no-gutters>
                    <v-col cols="5" class="mega-overview pa-5">
                      <v-chip
                        :color="rowIndex === 0 ? 'primary' : 'secondary'"
                        size="small"
                        variant="outlined"
                      >
                        {{ desktopLabels[rowIndex] }}
                      </v-chip>

                      <div class="mega-icon-shell mt-4">
                        <v-icon :icon="item.icon" size="38" color="primary" />
                      </div>

                      <h4 class="mega-title mt-4">{{ item.label }}</h4>
                      <p class="mega-copy">{{ previewText(item.intro, 172) }}</p>

                      <v-divider class="my-4" />
                      <p class="mega-mini">Focus Snapshot</p>
                      <ul class="mega-bullets">
                        <li v-for="point in item.focusAreas.slice(0, 2)" :key="point">
                          {{ previewText(point, 68) }}
                        </li>
                      </ul>

                      <v-btn
                        :color="rowIndex === 0 ? 'primary' : 'secondary'"
                        :to="item.path"
                        variant="flat"
                      >
                        Open {{ item.label }} Overview
                      </v-btn>
                    </v-col>

                    <v-col cols="7" class="mega-links-col pa-4">
                      <p class="mega-mini mb-2">Dropdown Pages</p>
                      <div class="mega-link-grid">
                        <router-link
                          v-for="child in item.children"
                          :key="child.path"
                          :to="child.path"
                          class="mega-link-card"
                        >
                          <span class="mega-link-icon">
                            <v-icon :icon="child.icon" size="18" color="secondary" />
                          </span>
                          <span class="mega-link-block">
                            <span class="mega-link-title">
                              {{ child.label }}
                            </span>
                            <span class="mega-link-copy">{{ previewText(child.intro, 94) }}</span>
                          </span>
                        </router-link>
                      </div>
                    </v-col>
                  </v-row>
                </v-card>
              </v-menu>
            </div>
          </div>
        </div>

        <div class="top-actions">
          <v-btn
            class="portal-entry-btn d-none d-md-inline-flex"
            prepend-icon="mdi-account-key-outline"
            to="/portal/login"
            variant="tonal"
          >
            Portal Login
          </v-btn>
          <v-btn
            :icon="themeToggleIcon"
            :title="themeToggleLabel"
            aria-label="Toggle light and dark theme"
            class="theme-toggle-btn"
            variant="tonal"
            @click="toggleTheme"
          />
          <v-btn class="d-lg-none" icon="mdi-menu" variant="text" @click="drawer = true" />
        </div>
      </v-container>
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>

    <v-footer class="site-footer">
      <v-container class="py-8">
        <v-row>
          <v-col cols="12" md="4">
            <h3 class="footer-title">PVI ENGINEERS</h3>
            <p class="footer-copy">
              Futuristic civil engineering website with dedicated pages for every menu and dropdown
              option.
            </p>
            <p class="footer-copy mt-3">Email: hello@pviengineers.com</p>
            <p class="footer-copy">Phone: +91 80 4567 2200</p>
          </v-col>

          <v-col cols="12" md="4">
            <h4 class="footer-subtitle">Service Buttons</h4>
            <div class="footer-links">
              <router-link
                v-for="link in serviceButtons"
                :key="`footer-service-${link.path}`"
                :to="link.path"
                class="footer-link"
              >
                {{ link.label }}
              </router-link>
            </div>
          </v-col>

          <v-col cols="12" md="4">
            <h4 class="footer-subtitle">Company Buttons</h4>
            <div class="footer-links">
              <router-link
                v-for="link in companyButtons"
                :key="`footer-company-${link.path}`"
                :to="link.path"
                class="footer-link"
              >
                {{ link.label }}
              </router-link>
            </div>
          </v-col>
        </v-row>

        <v-divider class="my-6" />
        <div class="footer-bottom">
          <p class="footer-meta">
            © {{ year }} PVI ENGINEERS. All rights reserved.
          </p>
          <router-link class="footer-policy" to="/privacy-statement">Privacy Statement</router-link>
        </div>
      </v-container>
    </v-footer>
  </v-app>
</template>

<style scoped>
.pvi-app {
  background: transparent;
}

.ambient-lights {
  pointer-events: none;
  position: fixed;
  inset: 0;
  overflow: hidden;
  z-index: 0;
}

.light-orb {
  position: absolute;
  width: 40vw;
  height: 40vw;
  border-radius: 50%;
  filter: blur(80px);
  opacity: var(--ambient-orb-opacity);
  animation: float 12s ease-in-out infinite;
}

.orb-one {
  background: var(--ambient-orb-one);
  top: -12%;
  left: -10%;
}

.orb-two {
  background: var(--ambient-orb-two);
  right: -10%;
  top: 30%;
  animation-delay: -4s;
}

.orb-three {
  background: var(--ambient-orb-three);
  bottom: -20%;
  left: 28%;
  animation-delay: -8s;
}

.top-bar {
  backdrop-filter: blur(12px);
  background: var(--top-bar-bg) !important;
  border-bottom: 1px solid var(--top-bar-border);
}

.top-inner {
  display: flex;
  align-items: center;
  gap: 20px;
}

.brand-link {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  min-width: fit-content;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--brand-mark-text);
  background: var(--brand-mark-gradient);
}

.brand-copy {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.brand-copy strong {
  color: var(--brand-title);
  font-size: 0.95rem;
  letter-spacing: 0.07em;
}

.brand-copy small {
  color: var(--brand-subtitle);
  font-size: 0.7rem;
}

.desktop-nav {
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.theme-toggle-btn {
  border: 1px solid var(--top-bar-border);
}

.portal-entry-btn {
  border: 1px solid var(--top-bar-border);
}

.nav-row {
  display: flex;
  gap: 4px;
  overflow-x: auto;
  padding-bottom: 2px;
  scrollbar-width: none;
}

.nav-row::-webkit-scrollbar {
  display: none;
}

.menu-trigger {
  position: relative;
}

.menu-btn {
  min-width: max-content;
  font-size: 0.78rem;
  letter-spacing: 0.02em;
}

.menu-trigger-active .menu-btn {
  font-weight: 600;
}

.mega-menu-card {
  background: var(--mega-bg);
  border: 1px solid var(--mega-border);
  overflow: hidden;
}

.mega-overview {
  background: var(--mega-overview-bg);
  border-right: 1px solid var(--mega-overview-border);
}

.mega-icon-shell {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  border: 1px solid var(--mega-border);
  background: var(--mega-overview-bg);
}

.mega-title {
  margin: 0;
  color: var(--mega-title);
  font-size: 1.06rem;
  line-height: 1.3;
}

.mega-copy {
  margin: 12px 0 0;
  color: var(--mega-copy);
  line-height: 1.6;
  font-size: 0.9rem;
}

.mega-mini {
  margin: 0;
  color: var(--mega-mini);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.68rem;
  font-weight: 700;
}

.mega-bullets {
  margin: 10px 0 18px;
  padding: 0 0 0 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mega-bullets li {
  color: var(--mega-bullet);
  font-size: 0.88rem;
  line-height: 1.45;
}

.mega-links-col {
  background: var(--mega-links-bg);
}

.mega-link-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
}

.mega-link-card {
  display: grid;
  grid-template-columns: 32px 1fr;
  gap: 10px;
  align-items: flex-start;
  text-decoration: none;
  border: 1px solid var(--mega-link-card-border);
  border-radius: 12px;
  padding: 10px;
  background: var(--mega-link-card-bg);
  transition: border-color 0.2s ease, transform 0.2s ease;
}

.mega-link-card:hover {
  border-color: var(--mega-link-card-hover-border);
  transform: translateY(-1px);
}

.mega-link-icon {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border-radius: 9px;
  background: var(--mega-link-icon-bg);
}

.mega-link-block {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.mega-link-title {
  color: var(--mega-link-title);
  font-size: 0.9rem;
  line-height: 1.35;
  display: inline-flex;
  align-items: center;
}

.mega-link-copy {
  color: var(--mega-link-copy);
  font-size: 0.79rem;
  line-height: 1.42;
}

.mobile-drawer {
  background: var(--drawer-bg);
}

@media (max-width: 1420px) {
  .mega-menu-card {
    width: 690px !important;
  }
}

.site-footer {
  position: relative;
  z-index: 1;
  background: var(--footer-bg);
  border-top: 1px solid var(--footer-border);
}

.footer-title {
  margin: 0 0 12px;
  color: var(--footer-title);
  font-size: 1.2rem;
  letter-spacing: 0.06em;
}

.footer-subtitle {
  margin: 0 0 10px;
  color: var(--footer-subtitle);
  font-size: 1rem;
}

.footer-copy {
  margin: 0;
  color: var(--footer-copy);
  line-height: 1.6;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.footer-link {
  color: var(--footer-link);
  text-decoration: none;
  font-size: 0.95rem;
}

.footer-link:hover {
  color: var(--footer-link-hover);
}

.footer-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.footer-meta {
  margin: 0;
  color: var(--footer-meta);
  font-size: 0.88rem;
}

.footer-policy {
  color: var(--footer-policy);
  text-decoration: none;
  font-size: 0.9rem;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-18px);
  }
}
</style>
