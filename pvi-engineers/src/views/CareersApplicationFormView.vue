<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getOpeningById } from '../data/careersData'
import { submitCareerApplication } from '../services/careersApi'

const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const submitted = ref(false)
const snackbar = ref(false)
const applicationRefId = ref('')
const resumeFile = ref(null)
const supportingFiles = ref([])
const submissionError = ref('')
const MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024
const MAX_TOTAL_UPLOAD_BYTES = 9 * 1024 * 1024
const MAX_SUPPORTING_FILE_COUNT = 3
const ALLOWED_UPLOAD_EXTENSIONS = ['pdf', 'doc', 'docx', 'png', 'jpg', 'jpeg']

const job = computed(() => getOpeningById(route.params.jobId))

const application = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  currentLocation: '',
  willingToRelocate: '',
  workAuthorization: '',
  currentCompany: '',
  currentDesignation: '',
  totalExperience: '',
  relevantExperience: '',
  highestQualification: '',
  specialization: '',
  graduationYear: '',
  currentCtc: '',
  expectedCtc: '',
  noticePeriod: '',
  availableFrom: '',
  linkedin: '',
  portfolio: '',
  keySkills: '',
  whyJoin: '',
  additionalInfo: '',
  consentPrivacy: false,
  consentBackground: false,
})

const relocateOptions = ['Yes', 'No', 'Open to discuss']
const authorizationOptions = ['Indian Citizen', 'Valid Work Permit', 'Require Sponsorship']

const required = (value) => (!!value || value === 0) || 'This field is required'
const requiredTrue = (value) => value || 'This consent is required'
const emailRule = (value) => /.+@.+\..+/.test(value) || 'Enter a valid email address'
const phoneRule = (value) =>
  /^[0-9+\-\s()]{8,18}$/.test(value) || 'Enter a valid contact number'
const numberRule = (value) =>
  value === '' || Number(value) >= 0 || 'Enter a valid non-negative value'
const requiredHttpsUrlRule = (value) =>
  /^https?:\/\/[\w.-]+(?:\/\S*)?$/.test(value) || 'Enter a valid URL starting with http:// or https://'
const optionalHttpsUrlRule = (value) =>
  value === '' ||
  /^https?:\/\/[\w.-]+(?:\/\S*)?$/.test(value) ||
  'Enter a valid URL starting with http:// or https://'

function normalizeResumeFile(value) {
  if (Array.isArray(value)) {
    return value[0] ?? null
  }

  return value ?? null
}

function normalizeFileList(value) {
  if (Array.isArray(value)) {
    return value.filter(Boolean)
  }

  return value ? [value] : []
}

function hasAllowedExtension(file) {
  const lowerName = (file?.name || '').toLowerCase()
  return ALLOWED_UPLOAD_EXTENSIONS.some((extension) => lowerName.endsWith(`.${extension}`))
}

function getTotalUploadSizeBytes() {
  const resume = normalizeResumeFile(resumeFile.value)
  const extras = normalizeFileList(supportingFiles.value)
  return [resume, ...extras].filter(Boolean).reduce((sum, file) => sum + file.size, 0)
}

const resumeRule = (value) =>
  !!normalizeResumeFile(value) || 'Resume file is required'
const resumeSizeRule = (value) => {
  const file = normalizeResumeFile(value)
  return !file || file.size <= MAX_FILE_SIZE_BYTES || 'Resume file must be less than 5 MB'
}
const resumeExtensionRule = (value) => {
  const file = normalizeResumeFile(value)
  return !file || hasAllowedExtension(file) || 'Resume must be PDF, DOC, DOCX, PNG, JPG, or JPEG'
}
const totalUploadRule = () =>
  getTotalUploadSizeBytes() <= MAX_TOTAL_UPLOAD_BYTES || 'Total upload size must be less than 9 MB'
const supportingFileCountRule = (value) =>
  normalizeFileList(value).length <= MAX_SUPPORTING_FILE_COUNT ||
  `You can upload up to ${MAX_SUPPORTING_FILE_COUNT} supporting files`
const supportingFileSizeRule = (value) => {
  const files = normalizeFileList(value)
  return files.every((file) => file.size <= MAX_FILE_SIZE_BYTES) || 'Each file must be less than 5 MB'
}
const supportingFileExtensionRule = (value) => {
  const files = normalizeFileList(value)
  return (
    files.every((file) => hasAllowedExtension(file)) ||
    'Supporting files must be PDF, DOC, DOCX, PNG, JPG, or JPEG'
  )
}

async function submitApplication() {
  if (!formRef.value || !job.value) return

  submissionError.value = ''
  const { valid } = await formRef.value.validate()
  if (!valid) return

  const selectedResumeFile = normalizeResumeFile(resumeFile.value)
  if (!selectedResumeFile) {
    submissionError.value = 'Resume file is required.'
    return
  }

  const selectedSupportingFiles = normalizeFileList(supportingFiles.value)
  const filesToUpload = [
    {
      category: 'resume',
      file: selectedResumeFile,
    },
    ...selectedSupportingFiles.map((file) => ({
      category: 'supporting',
      file,
    })),
  ]

  submitting.value = true

  try {
    const response = await submitCareerApplication(
      {
        id: job.value.id,
        role: job.value.role,
      },
      application,
      filesToUpload,
    )

    applicationRefId.value = response.applicationRef
    submitted.value = true
    snackbar.value = true
  } catch (error) {
    submissionError.value =
      error instanceof Error
        ? error.message
        : 'Failed to submit application. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <v-container class="page-shell">
    <template v-if="job">
      <section>
        <p class="section-kicker">Careers • Application Form</p>
        <h1 class="page-title">Apply for {{ job.role }}</h1>
        <p class="page-copy">
          Complete the required fields below. This form follows enterprise-style hiring workflows,
          similar to major MNC job application portals.
        </p>
      </section>

      <v-card class="glass-card pa-6 mt-8">
        <p class="mini-kicker mb-3">Position Summary</p>
        <v-row dense>
          <v-col cols="12" md="3">
            <p class="meta-label">Job ID</p>
            <p class="meta-value">{{ job.id }}</p>
          </v-col>
          <v-col cols="12" md="3">
            <p class="meta-label">Department</p>
            <p class="meta-value">{{ job.department }}</p>
          </v-col>
          <v-col cols="12" md="3">
            <p class="meta-label">Location</p>
            <p class="meta-value">{{ job.location }}</p>
          </v-col>
          <v-col cols="12" md="3">
            <p class="meta-label">Work Model</p>
            <p class="meta-value">{{ job.workModel }}</p>
          </v-col>
        </v-row>
      </v-card>

      <v-card v-if="submitted" class="glass-card pa-6 mt-6">
        <p class="mini-kicker">Application Submitted</p>
        <h2 class="section-title mt-2">Thank you for applying</h2>
        <p class="page-copy mt-2">
          We have received your application for {{ job.role }}. Our team will review your profile
          and contact you if shortlisted.
        </p>
        <v-chip color="success" variant="tonal" class="mt-4">
          Reference ID: {{ applicationRefId }}
        </v-chip>
      </v-card>

      <v-alert
        v-if="submissionError"
        class="error-alert mt-6"
        type="error"
        variant="tonal"
        border="start"
      >
        {{ submissionError }}
      </v-alert>

      <v-form
        v-if="!submitted"
        ref="formRef"
        class="mt-8 mb-8"
        @submit.prevent="submitApplication"
      >
        <v-card class="glass-card pa-6">
          <p class="mini-kicker">1. Personal Information</p>
          <v-row class="mt-1" dense>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.firstName"
                :rules="[required]"
                label="First Name *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.lastName"
                :rules="[required]"
                label="Last Name *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.email"
                :rules="[required, emailRule]"
                label="Email Address *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.phone"
                :rules="[required, phoneRule]"
                label="Mobile Number *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.currentLocation"
                :rules="[required]"
                label="Current Location *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="application.willingToRelocate"
                :items="relocateOptions"
                :rules="[required]"
                label="Willing to Relocate? *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="application.workAuthorization"
                :items="authorizationOptions"
                :rules="[required]"
                label="Work Authorization Status *"
              />
            </v-col>
          </v-row>
        </v-card>

        <v-card class="glass-card pa-6 mt-6">
          <p class="mini-kicker">2. Professional Details</p>
          <v-row class="mt-1" dense>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.currentCompany"
                :rules="[required]"
                label="Current Company *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.currentDesignation"
                :rules="[required]"
                label="Current Designation *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.totalExperience"
                :rules="[required, numberRule]"
                label="Total Experience (Years) *"
                type="number"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.relevantExperience"
                :rules="[required, numberRule]"
                label="Relevant Experience (Years) *"
                type="number"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.highestQualification"
                :rules="[required]"
                label="Highest Qualification *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.specialization"
                :rules="[required]"
                label="Specialization *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.graduationYear"
                :rules="[required]"
                label="Graduation Year *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-textarea
                v-model="application.keySkills"
                :rules="[required]"
                auto-grow
                label="Key Skills (comma separated) *"
                rows="2"
              />
            </v-col>
          </v-row>
        </v-card>

        <v-card class="glass-card pa-6 mt-6">
          <p class="mini-kicker">3. Compensation & Availability</p>
          <v-row class="mt-1" dense>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="application.currentCtc"
                :rules="[required]"
                label="Current CTC *"
              />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="application.expectedCtc"
                :rules="[required]"
                label="Expected CTC *"
              />
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="application.noticePeriod"
                :rules="[required]"
                label="Notice Period *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.availableFrom"
                :rules="[required]"
                label="Earliest Available Joining Date *"
                type="date"
              />
            </v-col>
          </v-row>
        </v-card>

        <v-card class="glass-card pa-6 mt-6">
          <p class="mini-kicker">4. Links, Documents & Statements</p>
          <v-row class="mt-1" dense>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.linkedin"
                :rules="[required, requiredHttpsUrlRule]"
                label="LinkedIn Profile URL *"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="application.portfolio"
                :rules="[optionalHttpsUrlRule]"
                label="Portfolio / Other Professional Link"
              />
            </v-col>
            <v-col cols="12">
              <v-file-input
                v-model="resumeFile"
                :rules="[resumeRule, resumeSizeRule, resumeExtensionRule, totalUploadRule]"
                accept=".pdf,.doc,.docx,.png,.jpg,.jpeg"
                label="Upload Resume * (PDF/DOC/DOCX/PNG/JPG/JPEG, max 5 MB)"
                show-size
              />
            </v-col>
            <v-col cols="12">
              <v-file-input
                v-model="supportingFiles"
                :rules="[
                  supportingFileCountRule,
                  supportingFileSizeRule,
                  supportingFileExtensionRule,
                  totalUploadRule,
                ]"
                accept=".pdf,.doc,.docx,.png,.jpg,.jpeg"
                chips
                clearable
                label="Supporting Documents (optional, up to 3 files)"
                multiple
                show-size
              />
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="application.whyJoin"
                :rules="[required]"
                auto-grow
                label="Why do you want to join PVI ENGINEERS? *"
                rows="3"
              />
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="application.additionalInfo"
                auto-grow
                label="Additional Information (optional)"
                rows="3"
              />
            </v-col>
          </v-row>
        </v-card>

        <v-card class="glass-card pa-6 mt-6">
          <p class="mini-kicker">5. Declarations</p>
          <v-checkbox
            v-model="application.consentPrivacy"
            :rules="[requiredTrue]"
            color="primary"
            label="I agree to PVI ENGINEERS processing my application data. *"
          />
          <v-checkbox
            v-model="application.consentBackground"
            :rules="[requiredTrue]"
            color="primary"
            label="I confirm that the details provided are accurate and I consent to background verification. *"
          />
        </v-card>

        <div class="mt-6 d-flex ga-3 flex-wrap">
          <v-btn :loading="submitting" color="primary" size="large" type="submit">
            Submit Application
          </v-btn>
          <v-btn color="secondary" to="/careers/open-positions" variant="tonal">
            Back to Open Positions
          </v-btn>
        </div>
      </v-form>
    </template>

    <v-card v-else class="glass-card pa-8 mt-8">
      <p class="section-kicker">Invalid Job Link</p>
      <h1 class="page-title">This job opening could not be found</h1>
      <p class="page-copy">
        The role may have been closed or the link is incorrect. Please browse current openings and
        apply using the active listing.
      </p>
      <v-btn class="mt-6" color="primary" to="/careers/open-positions">Go to Open Positions</v-btn>
    </v-card>

    <v-snackbar v-model="snackbar" color="success" timeout="3000">
      Application submitted successfully. Our hiring team will review and contact you.
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
  color: var(--text-kicker);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.72rem;
  font-weight: 700;
}

.page-title,
.section-title {
  margin: 10px 0 0;
  color: var(--text-title);
  line-height: 1.2;
}

.page-title {
  font-size: clamp(2rem, 4vw, 3.1rem);
  max-width: 24ch;
}

.section-title {
  font-size: clamp(1.4rem, 2.8vw, 2rem);
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

.error-alert {
  white-space: pre-line;
}

.meta-label {
  margin: 0;
  color: var(--text-path);
  text-transform: uppercase;
  letter-spacing: 0.09em;
  font-size: 0.72rem;
}

.meta-value {
  margin: 7px 0 0;
  color: var(--text-card-title);
}
</style>
