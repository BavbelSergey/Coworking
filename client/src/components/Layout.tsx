import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  LayoutDashboard,
  Users,
  Building2,
  Calendar,
  CreditCard,
  Wifi,
  LogOut,
  Menu,
  X,
} from 'lucide-react'
import { useState } from 'react'

const navItems = [
  { to: '/', icon: LayoutDashboard, label: 'Панель управления' },
  { to: '/users', icon: Users, label: 'Пользователи' },
  { to: '/workspaces', icon: Building2, label: 'Рабочие места' },
  { to: '/bookings', icon: Calendar, label: 'Бронирования' },
  { to: '/payments', icon: CreditCard, label: 'Платежи' },
  { to: '/amenities', icon: Wifi, label: 'Удобства' },
]

export function Layout() {
  const { logout } = useAuth()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <div className="flex min-h-screen bg-[hsl(var(--secondary))]">
      {/* Mobile sidebar backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 w-64 transform bg-[hsl(var(--card))] shadow-lg transition-transform duration-200 lg:static lg:translate-x-0 ${
          sidebarOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-16 items-center justify-between border-b px-6">
          <h1 className="text-xl font-bold text-[hsl(var(--primary))]">Coworking</h1>
          <button
            className="lg:hidden"
            onClick={() => setSidebarOpen(false)}
          >
            <X className="h-6 w-6" />
          </button>
        </div>
        <nav className="flex flex-col gap-1 p-4">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              onClick={() => setSidebarOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-[hsl(var(--primary))] text-[hsl(var(--primary-foreground))]'
                    : 'text-[hsl(var(--muted-foreground))] hover:bg-[hsl(var(--secondary))] hover:text-[hsl(var(--foreground))]'
                }`
              }
            >
              <Icon className="h-5 w-5" />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="absolute bottom-0 left-0 right-0 border-t p-4">
          <button
            onClick={logout}
            className="flex w-full items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium text-[hsl(var(--destructive))] transition-colors hover:bg-[hsl(var(--destructive))]/10"
          >
            <LogOut className="h-5 w-5" />
            Выйти
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex flex-1 flex-col">
        {/* Mobile header */}
        <header className="flex h-16 items-center gap-4 border-b bg-[hsl(var(--card))] px-6 lg:hidden">
          <button onClick={() => setSidebarOpen(true)}>
            <Menu className="h-6 w-6" />
          </button>
          <h1 className="text-lg font-semibold">Coworking</h1>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
