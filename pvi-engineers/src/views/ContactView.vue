<script setup>
import { reactive, ref } from 'vue'

const formRef = ref(null)
const snackbar = ref(false)
const submitting = ref(false)

const inquiry = reactive({
  name: '',
  email: '',
  company: '',
  service: '',
  message: '',
})

const services = [
  'Road Design Engineering',
  'Water Management Planning',
  'Drainage & Flood Resilience',
  'Integrated Infrastructure Consulting',
]

const required = (value) => !!value || 'This field is required'
const emailRule = (value) => /.+@.+\..+/.test(value) || 'Enter a valid email address'

async function submitForm() {
  if (!formRef.value) return

  const { valid } = await formRef.value.validate()
  if (!valid) return

  submitting.value = true

  setTimeout(() => {
    submitting.value = false
    snackbar.value = true

    inquiry.name = ''
    inquiry.email = ''
    inquiry.company = ''
    inquiry.service = ''
    inquiry.message = ''

    formRef.value.resetValidation()
  }, 800)
}
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">Contact</p>
      <h1 class="page-title">Let’s Engineer Your Next Infrastructure Project</h1>
      <p class="page-copy">
        Share your requirements and our team will connect with a tailored plan for road design,
        water management, or drainage engineering services.
      </p>
    </section>

    <v-row class="mt-8" dense>
      <v-col cols="12" md="4">
        <v-card class="glass-card pa-6 h-100">
          <h2 class="card-title">Contact Information</h2>
          <div class="info-group mt-6">
            <p class="info-label">Head Office</p>
            <p class="info-value">PVI ENGINEERS, Bengaluru, Karnataka, India</p>
          </div>
          <div class="info-group mt-4">
            <p class="info-label">Email</p>
            <p class="info-value">hello@pviengineers.com</p>
          </div>
          <div class="info-group mt-4">
            <p class="info-label">Phone</p>
            <p class="info-value">+91 80 4567 2200</p>
          </div>
          <div class="info-group mt-4">
            <p class="info-label">Working Hours</p>
            <p class="info-value">Mon - Sat | 9:00 AM - 7:00 PM</p>
          </div>
          <v-divider class="my-6" />
          <p class="mini-kicker mb-3">Response commitment</p>
          <p class="card-copy mt-0">
            We typically respond within one business day with next steps and project alignment
            details.
          </p>
        </v-card>
      </v-col>

      <v-col cols="12" md="8">
        <v-card class="glass-card pa-6">
          <h2 class="card-title mb-5">Project Inquiry Form</h2>
          <v-form ref="formRef" @submit.prevent="submitForm">
            <v-row dense>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="inquiry.name"
                  label="Your Name"
                  :rules="[required]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="inquiry.email"
                  label="Work Email"
                  :rules="[required, emailRule]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="inquiry.company"
                  label="Company / Organization"
                  :rules="[required]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12">
                <v-select
                  v-model="inquiry.service"
                  :items="services"
                  label="Service Needed"
                  :rules="[required]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12">
                <v-textarea
                  v-model="inquiry.message"
                  label="Project Details"
                  :rules="[required]"
                  rows="5"
                  auto-grow
                  color="primary"
                />
              </v-col>
            </v-row>
            <div class="mt-4">
              <v-btn :loading="submitting" color="primary" size="large" type="submit">
                Send Inquiry
              </v-btn>
            </div>
          </v-form>
        </v-card>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar" color="success" timeout="3000">
      Thank you! Your inquiry has been captured. Our team will contact you shortly.
    </v-snackbar>
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
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.72rem;
  font-weight: 700;
}

.section-kicker {
  color: var(--text-kicker);
}

.mini-kicker {
  color: var(--text-copy);
}

.page-title {
  margin: 10px 0 0;
  color: var(--text-title);
  font-size: clamp(2rem, 4vw, 3.1rem);
  line-height: 1.2;
  max-width: 20ch;
}

.page-copy {
  margin: 18px 0 0;
  color: var(--text-copy);
  line-height: 1.78;
  max-width: 80ch;
}

.glass-card {
  background: var(--surface-card-bg);
  border: 1px solid var(--surface-card-border);
}

.card-title {
  margin: 0;
  color: var(--text-card-title);
  font-size: 1.2rem;
}

.card-copy {
  margin: 12px 0 0;
  color: var(--text-card-copy);
  line-height: 1.7;
}

.info-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  margin: 0;
  color: var(--text-path);
  font-size: 0.78rem;
  letter-spacing: 0.09em;
  text-transform: uppercase;
}

.info-value {
  margin: 0;
  color: var(--text-card-copy);
  line-height: 1.5;
}
</style>
