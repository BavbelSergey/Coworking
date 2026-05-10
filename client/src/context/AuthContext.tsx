import { createContext, useContext, useState, useEffect, type ReactNode } from 'react'
import { auth } from '../lib/api'

interface AuthContextType {
  isAuthenticated: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const token = auth.getToken()
    setIsAuthenticated(!!token)
    setIsLoading(false)
  }, [])

  const login = async (email: string, password: string) => {
    const response = await auth.login({ email, password })
    auth.setToken(response.token)
    setIsAuthenticated(true)
  }

  const logout = () => {
    auth.logout()
    setIsAuthenticated(false)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout, isLoading }}>
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
