export type UserRole = 'ADMIN' | 'USER'

export interface User {
  id: number
  name: string
  email: string
  phone: string
  role: UserRole
}

export interface UserCreate {
  name: string
  email: string
  phone: string
  password: string
  role?: UserRole
}

export interface Amenity {
  id: number
  name: string
  description: string
}

export interface AmenityCreate {
  name: string
  description: string
}

export interface Workspace {
  id: number
  name: string
  phoneNumber: string
  capacity: number
  pricePerHour: number
  amenities: Amenity[]
}

export interface WorkspaceCreate {
  name: string
  phoneNumber?: string
  capacity: number
  pricePerHour: number
  amenityIds?: number[]
}

export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED'

export interface Booking {
  id: number
  startTime: string
  endTime: string
  createdAt: string
  status: BookingStatus
  userId: number
  workspaceId: number
  paymentId?: number
  userName: string
  userEmail: string
  workspaceName: string
}

export interface BookingCreate {
  startTime: string
  endTime: string
  userId: number
  workspaceId: number
}

export interface Payment {
  id: number
  amount: number
  date: string
  paymentMethod: 'CARD' | 'CASH' | 'TRANSFER'
  bookingId: number
  bookingStartTime: string
  bookingEndTime: string
  userId: number
  userName: string
  workspaceId: number
}

export interface PaymentCreate {
  amount: number
  paymentMethod: string
  bookingId: number
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface AuthRequest {
  email: string
  password: string
}

export interface AuthResponse {
  token: string
}
