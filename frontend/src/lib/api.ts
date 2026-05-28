import type {
  User,
  UserCreate,
  UserRole,
  Workspace,
  WorkspaceCreate,
  Booking,
  BookingCreate,
  Payment,
  PaymentCreate,
  Amenity,
  AmenityCreate,
  Page,
  AuthRequest,
  AuthResponse,
  RegisterRequest,
  DecodedToken,
} from '../types'

const API_BASE = (import.meta.env.VITE_API_URL || '/api').replace(/\/$/, '')

class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message)
    this.name = 'ApiError'
  }
}

async function fetchApi<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = localStorage.getItem('token')
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  
  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new ApiError(
      response.status,
      errorData.message || `HTTP error! status: ${response.status}`
    )
  }

  if (response.status === 204) {
    return undefined as T
  }

  const data = await response.json()

  // Normalize page responses from backend
  // Backend may return: { content, totalElements, totalPages, ... } (flat)
  // Or: { content, page: { totalElements, totalPages, ... } } (nested)
  // We always normalize to flat structure: { content, totalElements, totalPages, ... }

  if (data && typeof data === 'object' && data.content) {
    if (data.page && typeof data.page === 'object') {
      // Nested structure: extract page metadata
      return {
        content: data.content,
        totalElements: data.page.totalElements ?? 0,
        totalPages: data.page.totalPages ?? 0,
        size: data.page.size ?? data.content.length,
        number: data.page.number ?? 0,
        first: data.page.first !== false,
        last: data.page.last !== false,
      } as T
    } else if (typeof data.totalElements !== 'undefined' || typeof data.totalPages !== 'undefined') {
      // Already flat structure, just ensure all fields exist
      return {
        content: data.content,
        totalElements: data.totalElements ?? 0,
        totalPages: data.totalPages ?? 0,
        size: data.size ?? data.content.length,
        number: data.number ?? 0,
        first: data.first !== false,
        last: data.last !== false,
      } as T
    }
  }

  return data
}

// Helper to decode JWT
function decodeToken(token: string): DecodedToken | null {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

// Auth
export const auth = {
  login: (data: AuthRequest) =>
    fetchApi<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  register: (data: RegisterRequest) =>
    fetchApi<User>('/users', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  logout: () => {
    localStorage.removeItem('token')
  },
  getToken: () => localStorage.getItem('token'),
  setToken: (token: string) => localStorage.setItem('token', token),
  getDecodedToken: (): DecodedToken | null => {
    const token = localStorage.getItem('token')
    if (!token) return null
    return decodeToken(token)
  },
  getRole: (): UserRole | null => {
    const decoded = auth.getDecodedToken()
    return decoded?.role || null
  },
  isAdmin: (): boolean => {
    return auth.getRole() === 'ADMIN'
  },
}

// Users
export const users = {
  getAll: (page = 0, size = 10) =>
    fetchApi<Page<User>>(`/users?page=${page}&size=${size}`),
  getById: (id: number) => fetchApi<User>(`/users/${id}`),
  getByEmail: (email: string) => fetchApi<User>(`/users/email/${email}`),
  create: (data: UserCreate) =>
    fetchApi<User>('/users', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  update: (id: number, data: Partial<User>) =>
    fetchApi<User>(`/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/users/${id}`, { method: 'DELETE' }),
  search: (name: string) => fetchApi<User[]>(`/users/search?name=${name}`),
  withActiveBookings: () => fetchApi<User[]>('/users/active-bookings'),
  withoutBookings: () => fetchApi<User[]>('/users/without-bookings'),
}

// Workspaces
export const workspaces = {
  getAll: (page = 0, size = 10) =>
    fetchApi<Page<Workspace>>(`/workspaces?page=${page}&size=${size}`),
  getById: (id: number) => fetchApi<Workspace>(`/workspaces/${id}`),
  create: (data: WorkspaceCreate) =>
    fetchApi<Workspace>('/workspaces', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  update: (id: number, data: Partial<WorkspaceCreate>) =>
    fetchApi<Workspace>(`/workspaces/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/workspaces/${id}`, { method: 'DELETE' }),
  getAvailable: (minCapacity?: number, maxPrice?: number, amenityIds?: number[]) => {
    const params = new URLSearchParams()
    if (minCapacity) params.append('minCapacity', String(minCapacity))
    if (maxPrice) params.append('maxPrice', String(maxPrice))
    if (amenityIds?.length) amenityIds.forEach(id => params.append('amenityIds', String(id)))
    return fetchApi<Workspace[]>(`/workspaces/available?${params}`)
  },
  addAmenity: (workspaceId: number, amenityId: number) =>
    fetchApi<Workspace>(`/workspaces/${workspaceId}/amenities/${amenityId}`, {
      method: 'POST',
    }),
  removeAmenity: (workspaceId: number, amenityId: number) =>
    fetchApi<Workspace>(`/workspaces/${workspaceId}/amenities/${amenityId}`, {
      method: 'DELETE',
    }),
}

// Bookings
export const bookings = {
  getAll: (page = 0, size = 10) =>
    fetchApi<Page<Booking>>(`/bookings?page=${page}&size=${size}`),
  getById: (id: number) => fetchApi<Booking>(`/bookings/${id}`),
  create: (data: BookingCreate) =>
    fetchApi<Booking>('/bookings', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  update: (id: number, data: Partial<Booking>) =>
    fetchApi<Booking>(`/bookings/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/bookings/${id}`, { method: 'DELETE' }),
  cancel: (id: number) =>
    fetchApi<Booking>(`/bookings/${id}/cancel`, { method: 'POST' }),
  confirm: (id: number) =>
    fetchApi<Booking>(`/bookings/${id}/confirm`, { method: 'POST' }),
  getByWorkspace: (workspaceId: number) =>
    fetchApi<Booking[]>(`/bookings/workspace/${workspaceId}`),
  getUserActive: (userId: number) =>
    fetchApi<Booking[]>(`/bookings/user/${userId}/active`),
}

// Payments
export const payments = {
  getAll: (page = 0, size = 20) =>
    fetchApi<Page<Payment>>(`/payments?page=${page}&size=${size}`),
  getById: (id: number) => fetchApi<Payment>(`/payments/${id}`),
  getByBooking: (bookingId: number) =>
    fetchApi<Payment>(`/payments/booking/${bookingId}`),
  create: (data: PaymentCreate) =>
    fetchApi<Payment>('/payments', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  update: (id: number, data: Partial<Payment>) =>
    fetchApi<Payment>(`/payments/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/payments/${id}`, { method: 'DELETE' }),
  getUserPayments: (userId: number) =>
    fetchApi<Payment[]>(`/payments/user/${userId}`),
  isBookingPaid: (bookingId: number) =>
    fetchApi<boolean>(`/payments/check/booking/${bookingId}`),
}

// Amenities
export const amenities = {
  getAll: (page = 0, size = 50) =>
    fetchApi<Page<Amenity>>(`/amenities?page=${page}&size=${size}`),
  getById: (id: number) => fetchApi<Amenity>(`/amenities/${id}`),
  getByName: (name: string) => fetchApi<Amenity>(`/amenities/name/${name}`),
  create: (data: AmenityCreate) =>
    fetchApi<Amenity>('/amenities', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  update: (id: number, data: Partial<Amenity>) =>
    fetchApi<Amenity>(`/amenities/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    fetchApi<void>(`/amenities/${id}`, { method: 'DELETE' }),
  search: (name: string) =>
    fetchApi<Amenity[]>(`/amenities/search/name?name=${name}`),
  getNames: () => fetchApi<string[]>('/amenities/names'),
}

export { ApiError }
