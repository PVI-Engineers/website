import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'

const pviLight = {
  dark: false,
  colors: {
    background: '#f3f9ff',
    surface: '#ffffff',
    primary: '#1f88e5',
    secondary: '#3fb5ff',
    accent: '#67c9ff',
    info: '#2b9af2',
    success: '#22b881',
    warning: '#ffba55',
    error: '#df5f70',
  },
}

const pviDark = {
  dark: true,
  colors: {
    background: '#050816',
    surface: '#0c1630',
    primary: '#5dd6ff',
    secondary: '#90f0de',
    accent: '#8c6bff',
    info: '#3aa7ff',
    success: '#3ad9a8',
    warning: '#ffc067',
    error: '#ff6486',
  },
}

export default createVuetify({
  theme: {
    defaultTheme: 'pviLight',
    themes: {
      pviLight,
      pviDark,
    },
  },
  defaults: {
    VBtn: {
      rounded: 'pill',
      class: 'text-none',
    },
    VCard: {
      rounded: 'xl',
      elevation: 0,
    },
    VChip: {
      rounded: 'pill',
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VTextarea: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable',
    },
  },
})
