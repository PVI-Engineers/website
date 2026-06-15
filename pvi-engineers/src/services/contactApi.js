const configuredBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? '').trim()
const API_BASE_URL =
  configuredBaseUrl.length === 0 || configuredBaseUrl === '/'
    ? ''
    : configuredBaseUrl.replace(/\/$/, '')

function extractErrorMessage(payload, fallback) {
  if (!payload) return fallback
  if (typeof payload === 'string') return payload
  if (Array.isArray(payload.details) && payload.details.length > 0) return payload.details.join('\n')
  if (typeof payload.message === 'string') return payload.message
  if (typeof payload.error === 'string') return payload.error
  return fallback
}

async function parseErrorPayload(response) {
  try {
    return await response.json()
  } catch {
    return null
  }
}

async function submitContactInquiry(inquiry) {
  const payload = {
    name: inquiry.name.trim(),
    email: inquiry.email.trim(),
    phone: inquiry.phone.trim(),
    company: inquiry.company ? inquiry.company.trim() : '',
    inquiryType: inquiry.inquiryType.trim(),
    message: inquiry.message.trim(),
  }

  const response = await fetch(`${API_BASE_URL}/api/contact/inquiries`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const errorPayload = await parseErrorPayload(response)
    throw new Error(extractErrorMessage(errorPayload, 'Failed to submit inquiry. Please try again.'))
  }

  return response.json()
}

export { submitContactInquiry }
