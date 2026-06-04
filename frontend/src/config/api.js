/**
 * Backend base URL. Set VITE_API_BASE_URL in production (e.g. Vercel env vars).
 * Local default: http://localhost:8081
 */
export const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081"

export const API_URL = `${API_BASE}/api`
export const WS_URL = `${API_BASE}/ws-auction`

/** Build full URL for uploaded images/documents returned by the backend */
export function assetUrl(path) {
  if (!path) return ""
  if (path.startsWith("http://") || path.startsWith("https://")) return path
  const normalized = path.startsWith("/") ? path : `/${path}`
  return `${API_BASE}${normalized}`
}
