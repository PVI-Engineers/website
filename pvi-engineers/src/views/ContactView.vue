<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { submitContactInquiry } from '../services/contactApi'

const route = useRoute()
const formRef = ref(null)
const snackbar = ref(false)
const submitting = ref(false)
const submissionError = ref('')
const submittedRef = ref('')

const inquiry = reactive({
  name: '',
  email: '',
  phone: '',
  company: '',
  inquiryType: '',
  message: '',
})

const inquiryTypeOptions = [
  'General Inquiries',
  'Business Proposals',
  'Vendor Registration',
  'Technical Consultation',
  'Other',
]

const presetInquiryTypeByPath = {
  '/contact/general-inquiries': 'General Inquiries',
  '/contact/business-proposals': 'Business Proposals',
  '/contact/vendor-registration': 'Vendor Registration',
}

const contextByPath = {
  '/contact': {
    kicker: 'Contact',
    title: 'Let’s Engineer Your Next Infrastructure Project',
    copy: 'Share your requirements and our team will connect with a tailored plan for road design, water management, or drainage engineering services.',
  },
  '/contact/general-inquiries': {
    kicker: 'Contact • General Inquiries',
    title: 'Talk to Our Team',
    copy: 'Need service details, capability clarification, or project consultation? Submit your inquiry and we will route it to the right team.',
  },
  '/contact/business-proposals': {
    kicker: 'Contact • Business Proposals',
    title: 'Submit Your Business Proposal',
    copy: 'Share your proposal scope, timelines, and partnership intent. Our team will review and respond with the next steps.',
  },
  '/contact/vendor-registration': {
    kicker: 'Contact • Vendor Registration',
    title: 'Vendor Registration Inquiry',
    copy: 'Suppliers and partners can submit profile details and onboarding questions to start the vendor evaluation process.',
  },
}

const pageContext = computed(() => contextByPath[route.path] ?? contextByPath['/contact'])

const required = (value) => !!value || 'This field is required'
const emailRule = (value) => /.+@.+\..+/.test(value) || 'Enter a valid email address'
const phoneRule = (value) =>
  /^[0-9+\-\s()]{8,18}$/.test(value) || 'Enter a valid contact number'

function applyInquiryTypePreset() {
  const preset = presetInquiryTypeByPath[route.path]
  inquiry.inquiryType = preset || ''
}

function resetInquiryForm() {
  inquiry.name = ''
  inquiry.email = ''
  inquiry.phone = ''
  inquiry.company = ''
  inquiry.message = ''
  inquiry.inquiryType = ''
  applyInquiryTypePreset()
}

watch(
  () => route.path,
  () => {
    submissionError.value = ''
    submittedRef.value = ''
    applyInquiryTypePreset()
  },
  { immediate: true },
)

async function submitForm() {
  if (!formRef.value) return

  submissionError.value = ''
  const { valid } = await formRef.value.validate()
  if (!valid) return

  submitting.value = true

  try {
    const response = await submitContactInquiry(inquiry)
    submittedRef.value = response.inquiryRef || ''
    snackbar.value = true
    resetInquiryForm()
    formRef.value.resetValidation()
  } catch (error) {
    submissionError.value =
      error instanceof Error ? error.message : 'Failed to submit inquiry. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <v-container class="page-shell">
    <section>
      <p class="section-kicker">{{ pageContext.kicker }}</p>
      <h1 class="page-title">{{ pageContext.title }}</h1>
      <p class="page-copy">{{ pageContext.copy }}</p>
    </section>

    <v-row class="mt-8" dense>
      <v-col cols="12" md="4">
        <v-card class="glass-card pa-6 h-100">
          <h2 class="card-title">Contact Information</h2>
          <div class="info-group mt-6">
            <p class="info-label">Office</p>
            <p class="info-value">PVI ENGINEERS</p>
          </div>
          <div class="info-group mt-4">
            <p class="info-label">Email</p>
            <p class="info-value">contact@pviengineers.com</p>
          </div>
          <div class="info-group mt-4">
            <p class="info-label">Phone</p>
            <p class="info-value">+1 6055920819</p>
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
          <p class="card-copy mt-0 mb-4">
            Submitted details are securely stored and shared with our response team for follow-up.
          </p>
          <v-alert
            v-if="submissionError"
            class="mb-4"
            type="error"
            variant="tonal"
            border="start"
          >
            {{ submissionError }}
          </v-alert>
          <v-alert
            v-if="submittedRef"
            class="mb-4"
            type="success"
            variant="tonal"
            border="start"
          >
            Inquiry submitted successfully. Reference ID: {{ submittedRef }}
          </v-alert>
          <v-form ref="formRef" @submit.prevent="submitForm">
            <v-row dense>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="inquiry.name"
                  label="Your Name *"
                  :rules="[required]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="inquiry.email"
                  label="Work Email *"
                  :rules="[required, emailRule]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="inquiry.phone"
                  label="Phone Number *"
                  :rules="[required, phoneRule]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="inquiry.company"
                  label="Company / Organization"
                  color="primary"
                />
              </v-col>
              <v-col cols="12">
                <v-select
                  v-model="inquiry.inquiryType"
                  :items="inquiryTypeOptions"
                  label="Inquiry Type *"
                  :rules="[required]"
                  color="primary"
                />
              </v-col>
              <v-col cols="12">
                <v-textarea
                  v-model="inquiry.message"
                  label="How can we help you? *"
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
      Thank you! Your inquiry has been captured and sent to our team.
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
