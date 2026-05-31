const configuredBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? '').trim()
const API_BASE_URL =
  configuredBaseUrl.length === 0 || configuredBaseUrl === '/'
    ? ''
    : configuredBaseUrl.replace(/\/$/, '')
const MAX_TOTAL_FILE_BYTES = 9 * 1024 * 1024

function extractErrorMessage(payload, fallback) {
  if (!payload) return fallback

  if (typeof payload === 'string') return payload

  if (Array.isArray(payload.details) && payload.details.length > 0) {
    return payload.details.join('\n')
  }

  if (typeof payload.message === 'string') {
    return payload.message
  }

  return fallback
}

async function parseErrorPayload(response) {
  try {
    return await response.json()
  } catch {
    return null
  }
}

async function requestPresignedUploads(fileUploads) {
  const response = await fetch(`${API_BASE_URL}/api/careers/applications/presign`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      files: fileUploads.map((fileUpload) => ({
        clientFileId: fileUpload.clientFileId,
        category: fileUpload.category,
        fileName: fileUpload.file.name,
        contentType: fileUpload.file.type || 'application/octet-stream',
        sizeBytes: fileUpload.file.size,
      })),
    }),
  })

  if (!response.ok) {
    const payload = await parseErrorPayload(response)
    throw new Error(extractErrorMessage(payload, 'Failed to prepare file upload. Please try again.'))
  }

  return response.json()
}

async function uploadToS3(fileUpload, uploadTarget) {
  const response = await fetch(uploadTarget.uploadUrl, {
    method: uploadTarget.method || 'PUT',
    headers: {
      'Content-Type': uploadTarget.contentType || fileUpload.file.type || 'application/octet-stream',
    },
    body: fileUpload.file,
  })

  if (!response.ok) {
    throw new Error(`Failed to upload ${fileUpload.file.name}. Please try again.`)
  }
}

function buildApplicationPayload(job, application, uploadedFiles) {
  return {
    jobId: job.id,
    jobRole: job.role,
    firstName: application.firstName,
    lastName: application.lastName,
    email: application.email,
    phone: application.phone,
    currentLocation: application.currentLocation,
    willingToRelocate: application.willingToRelocate,
    workAuthorization: application.workAuthorization,
    currentCompany: application.currentCompany,
    currentDesignation: application.currentDesignation,
    totalExperience: application.totalExperience,
    relevantExperience: application.relevantExperience,
    highestQualification: application.highestQualification,
    specialization: application.specialization,
    graduationYear: application.graduationYear,
    currentCtc: application.currentCtc,
    expectedCtc: application.expectedCtc,
    noticePeriod: application.noticePeriod,
    availableFrom: application.availableFrom,
    linkedin: application.linkedin,
    portfolio: application.portfolio || '',
    keySkills: application.keySkills,
    whyJoin: application.whyJoin,
    additionalInfo: application.additionalInfo || '',
    consentPrivacy: Boolean(application.consentPrivacy),
    consentBackground: Boolean(application.consentBackground),
    files: uploadedFiles.map((file) => ({
      clientFileId: file.clientFileId,
      category: file.category,
      fileName: file.fileName,
      contentType: file.contentType,
      sizeBytes: file.sizeBytes,
      storageKey: file.storageKey,
    })),
  }
}

async function submitCareerApplication(job, application, files) {
  if (!Array.isArray(files) || files.length === 0) {
    throw new Error('Please upload your resume before submitting.')
  }

  const totalBytes = files.reduce((sum, fileEntry) => sum + (fileEntry.file?.size || 0), 0)
  if (totalBytes > MAX_TOTAL_FILE_BYTES) {
    throw new Error('Total upload size must be less than 9 MB.')
  }

  const fileUploads = files.map((fileEntry, index) => ({
    clientFileId: `${Date.now()}-${index}-${Math.random().toString(36).slice(2, 10)}`,
    category: fileEntry.category,
    file: fileEntry.file,
  }))

  const presignResponse = await requestPresignedUploads(fileUploads)
  const presignedFiles = Array.isArray(presignResponse.files) ? presignResponse.files : []
  const uploadTargetByClientId = new Map(
    presignedFiles.map((uploadTarget) => [uploadTarget.clientFileId, uploadTarget]),
  )

  const uploadedFiles = []
  for (const fileUpload of fileUploads) {
    const uploadTarget = uploadTargetByClientId.get(fileUpload.clientFileId)
    if (!uploadTarget) {
      throw new Error(`Upload token missing for ${fileUpload.file.name}. Please retry.`)
    }
    await uploadToS3(fileUpload, uploadTarget)
    uploadedFiles.push(uploadTarget)
  }

  const payload = buildApplicationPayload(job, application, uploadedFiles)
  const response = await fetch(`${API_BASE_URL}/api/careers/applications`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const errorPayload = await parseErrorPayload(response)
    throw new Error(
      extractErrorMessage(errorPayload, 'Failed to submit application. Please try again later.'),
    )
  }

  return response.json()
}

export { submitCareerApplication }
