import { createContext, useContext, useState, useEffect, type ReactNode } from 'react'
import { auth } from '../lib/api'
import type { UserRole, RegisterRequest } from '../types'

interface AuthContextType {
  isAuthenticated: boolean
  role: UserRole | null
  isAdmin: boolean
  login: (email: string, password: string) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [role, setRole] = useState<UserRole | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const token = auth.getToken()
    if (token) {
      const decoded = auth.getDecodedToken()
      if (decoded && decoded.exp * 1000 > Date.now()) {
        setIsAuthenticated(true)
        setRole(decoded.role)
      } else {
        auth.logout()
      }
    }
    setIsLoading(false)
  }, [])

  const login = async (email: string, password: string) => {
    const response = await auth.login({ email, password })
    auth.setToken(response.token)
    const decoded = auth.getDecodedToken()
    setRole(decoded?.role || null)
    setIsAuthenticated(true)
  }

  const register = async (data: RegisterRequest) => {
    await auth.register(data)
  }

  const logout = () => {
    auth.logout()
    setIsAuthenticated(false)
    setRole(null)
  }

  const isAdmin = role === 'admin'

  return (
    <AuthContext.Provider value={{ isAuthenticated, role, isAdmin, login, register, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
