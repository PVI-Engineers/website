const configuredBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? '').trim()
const API_BASE_URL =
  configuredBaseUrl.length === 0 || configuredBaseUrl === '/'
    ? ''
    : configuredBaseUrl.replace(/\/$/, '')
const TOKEN_KEY = 'pvi-portal-token'
const USER_KEY = 'pvi-portal-user'

function extractErrorMessage(payload, fallback) {
  if (!payload) return fallback
  if (typeof payload === 'string') return payload
  if (Array.isArray(payload.details) && payload.details.length > 0) return payload.details.join('\n')
  if (typeof payload.message === 'string') return payload.message
  if (typeof payload.error === 'string') return payload.error
  return fallback
}

function getStoredToken() {
  return window.localStorage.getItem(TOKEN_KEY) || ''
}

function getStoredUser() {
  const raw = window.localStorage.getItem(USER_KEY)
  if (!raw) return null

  try {
    const parsed = JSON.parse(raw)
    if (!parsed || typeof parsed !== 'object') return null
    return parsed
  } catch {
    return null
  }
}

function hasRole(role) {
  const user = getStoredUser()
  if (!user || !Array.isArray(user.roles)) return false
  return user.roles.includes(role)
}

function hasAnyRole(roles = []) {
  return roles.some((role) => hasRole(role))
}

function resolvePortalRoute(roles = []) {
  if (roles.includes('ADMIN')) return '/portal/admin'
  if (roles.includes('HR')) return '/portal/hr'
  if (roles.includes('EMPLOYEE')) return '/portal/employee'
  return '/portal/login'
}

function saveSession(data) {
  const normalized = {
    username: data.username,
    fullName: data.fullName,
    email: data.email,
    roles: Array.isArray(data.roles) ? data.roles : [],
  }

  window.localStorage.setItem(TOKEN_KEY, data.token)
  window.localStorage.setItem(USER_KEY, JSON.stringify(normalized))
}

function clearSession() {
  window.localStorage.removeItem(TOKEN_KEY)
  window.localStorage.removeItem(USER_KEY)
}

async function request(path, options = {}) {
  const { requiresAuth = true, responseType = 'json', headers = {}, ...restOptions } = options
  const mergedHeaders = { ...headers }

  if (requiresAuth) {
    const token = getStoredToken()
    if (!token) {
      throw new Error('Your session expired. Please login again.')
    }
    mergedHeaders.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...restOptions,
    headers: mergedHeaders,
  })

  if (!response.ok) {
    let payload = null
    try {
      payload = await response.json()
    } catch {
      payload = null
    }

    const fallback =
      response.status === 401
        ? 'Invalid or expired session. Please login again.'
        : 'Request failed. Please try again.'

    throw new Error(extractErrorMessage(payload, fallback))
  }

  if (responseType === 'blob') {
    return {
      blob: await response.blob(),
      contentDisposition: response.headers.get('Content-Disposition') || '',
    }
  }

  if (responseType === 'text') {
    return response.text()
  }

  if (response.status === 204) return null
  return response.json()
}

async function loginPortal(username, password) {
  const data = await request('/api/auth/login', {
    method: 'POST',
    requiresAuth: false,
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password }),
  })

  saveSession(data)
  return data
}

async function fetchCurrentUser() {
  return request('/api/auth/me')
}

async function fetchEmployeeHome() {
  return request('/api/employee/home')
}

async function fetchHrApplications() {
  return request('/api/hr/applications')
}

async function fetchAdminUsers() {
  return request('/api/admin/users')
}

function parseFilenameFromDisposition(contentDisposition, fallback = 'download') {
  if (!contentDisposition) return fallback

  const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) {
    try {
      return decodeURIComponent(utf8Match[1]).replace(/["]/g, '')
    } catch {
      return utf8Match[1].replace(/["]/g, '')
    }
  }

  const basicMatch = contentDisposition.match(/filename="?([^"]+)"?/i)
  if (basicMatch?.[1]) {
    return basicMatch[1]
  }

  return fallback
}

async function downloadResume(applicationId) {
  const { blob, contentDisposition } = await request(`/api/hr/applications/${applicationId}/resume`, {
    responseType: 'blob',
  })

  return {
    blob,
    filename: parseFilenameFromDisposition(contentDisposition, 'resume'),
  }
}

async function downloadApplicationFile(applicationId, fileId) {
  const { blob, contentDisposition } = await request(
    `/api/hr/applications/${applicationId}/files/${fileId}`,
    {
      responseType: 'blob',
    },
  )

  return {
    blob,
    filename: parseFilenameFromDisposition(contentDisposition, 'application-file'),
  }
}

export {
  clearSession,
  downloadApplicationFile,
  downloadResume,
  fetchAdminUsers,
  fetchCurrentUser,
  fetchEmployeeHome,
  fetchHrApplications,
  getStoredToken,
  getStoredUser,
  hasAnyRole,
  hasRole,
  loginPortal,
  resolvePortalRoute,
}
