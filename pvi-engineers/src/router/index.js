import { createRouter, createWebHistory } from 'vue-router'
import { pageCatalog } from '../data/siteNavigation'
import AdminDashboardView from '../views/AdminDashboardView.vue'
import CareersApplicationFormView from '../views/CareersApplicationFormView.vue'
import CareersCultureView from '../views/CareersCultureView.vue'
import CareersHiringProcessView from '../views/CareersHiringProcessView.vue'
import CareersOpenPositionsView from '../views/CareersOpenPositionsView.vue'
import CareersView from '../views/CareersView.vue'
import EmployeeDashboardView from '../views/EmployeeDashboardView.vue'
import HomeView from '../views/HomeView.vue'
import HrDashboardView from '../views/HrDashboardView.vue'
import PortalLoginView from '../views/PortalLoginView.vue'
import SectionPageView from '../views/SectionPageView.vue'
import { getStoredToken, getStoredUser, hasAnyRole, resolvePortalRoute } from '../services/portalApi'

const customCareersPaths = new Set([
  '/careers',
  '/careers/open-positions',
  '/careers/hiring-process',
  '/careers/work-culture-and-benefits',
])

const generatedRoutes = pageCatalog
  .filter((page) => !customCareersPaths.has(page.path))
  .map((page) => ({
  path: page.path,
  name: `page-${page.path.replace(/\//g, '-').replace(/^-/, '')}`,
  component: SectionPageView,
  }))

const routes = [
  { path: '/', name: 'Home', component: HomeView },
  { path: '/careers', name: 'Careers', component: CareersView },
  { path: '/careers/open-positions', name: 'CareersOpenings', component: CareersOpenPositionsView },
  { path: '/careers/apply/:jobId', name: 'CareersApplyForm', component: CareersApplicationFormView },
  { path: '/careers/apply', redirect: '/careers/open-positions' },
  { path: '/careers/hiring-process', name: 'CareersHiringProcess', component: CareersHiringProcessView },
  {
    path: '/careers/work-culture-and-benefits',
    name: 'CareersCulture',
    component: CareersCultureView,
  },
  { path: '/portal/login', name: 'PortalLogin', component: PortalLoginView },
  {
    path: '/portal/hr',
    name: 'PortalHrDashboard',
    component: HrDashboardView,
    meta: {
      requiresAuth: true,
      roles: ['HR', 'ADMIN'],
    },
  },
  {
    path: '/portal/employee',
    name: 'PortalEmployeeDashboard',
    component: EmployeeDashboardView,
    meta: {
      requiresAuth: true,
      roles: ['EMPLOYEE', 'ADMIN'],
    },
  },
  {
    path: '/portal/admin',
    name: 'PortalAdminDashboard',
    component: AdminDashboardView,
    meta: {
      requiresAuth: true,
      roles: ['ADMIN'],
    },
  },
  {
    path: '/portal',
    redirect: () => {
      const user = getStoredUser()
      return user ? resolvePortalRoute(user.roles || []) : '/portal/login'
    },
  },
  ...generatedRoutes,
  { path: '/careers/graduate-program', redirect: '/careers/open-positions' },
  { path: '/services', redirect: '/civil' },
  { path: '/projects', redirect: '/our-work' },
  { path: '/about', redirect: '/about-us' },
  { path: '/news-events', redirect: '/news-and-events' },
  { path: '/privacy', redirect: '/privacy-statement' },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  if (to.name === 'PortalLogin') {
    const token = getStoredToken()
    const user = getStoredUser()
    if (token && user) {
      return resolvePortalRoute(user.roles || [])
    }
    return true
  }

  if (!to.meta?.requiresAuth) {
    return true
  }

  const token = getStoredToken()
  const user = getStoredUser()

  if (!token || !user) {
    return {
      name: 'PortalLogin',
      query: { redirect: to.fullPath },
    }
  }

  const requiredRoles = Array.isArray(to.meta.roles) ? to.meta.roles : []
  if (requiredRoles.length > 0 && !hasAnyRole(requiredRoles)) {
    return resolvePortalRoute(user.roles || [])
  }

  return true
})

export default router
