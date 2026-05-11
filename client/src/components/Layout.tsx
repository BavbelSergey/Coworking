import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { cn, nav, roleBadge } from '../lib/styles'
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
  Shield,
  User,
} from 'lucide-react'
import { useState } from 'react'

interface NavItem {
  to: string
  icon: React.ElementType
  label: string
  adminOnly?: boolean
}

const navItems: NavItem[] = [
  { to: '/', icon: LayoutDashboard, label: 'Панель управления' },
  { to: '/workspaces', icon: Building2, label: 'Рабочие места' },
  { to: '/bookings', icon: Calendar, label: 'Бронирования' },
  { to: '/users', icon: Users, label: 'Пользователи', adminOnly: true },
  { to: '/payments', icon: CreditCard, label: 'Платежи', adminOnly: true },
  { to: '/amenities', icon: Wifi, label: 'Удобства', adminOnly: true },
]

export function Layout() {
  const { logout, isAdmin } = useAuth()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const filteredNavItems = navItems.filter(item => !item.adminOnly || isAdmin)

  const closeSidebar = () => setSidebarOpen(false)
  const openSidebar = () => setSidebarOpen(true)

  return (
    <div className="flex min-h-screen bg-secondary">
      {/* Mobile sidebar backdrop */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={closeSidebar}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          nav.sidebar,
          'transform transition-transform duration-200 lg:static lg:translate-x-0',
          sidebarOpen ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <div className={nav.sidebarHeader}>
          <h1 className="text-xl font-bold text-primary">Coworking</h1>
          <button className="lg:hidden" onClick={closeSidebar}>
            <X className="h-6 w-6" />
          </button>
        </div>

        {/* Role Badge */}
        <div className="border-b px-6 py-3">
          <div className={isAdmin ? roleBadge.admin : roleBadge.user}>
            {isAdmin ? (
              <Shield className="h-3 w-3" />
            ) : (
              <User className="h-3 w-3" />
            )}
            {isAdmin ? 'Администратор' : 'Пользователь'}
          </div>
        </div>

        <nav className={nav.sidebarNav}>
          {filteredNavItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              onClick={closeSidebar}
              className={({ isActive }) =>
                cn(
                  nav.navLink,
                  isActive ? nav.navLinkActive : nav.navLinkInactive
                )
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
            className={cn(
              nav.navLink,
              'w-full text-destructive hover:bg-destructive/10'
            )}
          >
            <LogOut className="h-5 w-5" />
            Выйти
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex flex-1 flex-col">
        {/* Mobile header */}
        <header className="flex h-16 items-center gap-4 border-b bg-card px-6 lg:hidden">
          <button onClick={openSidebar}>
            <Menu className="h-6 w-6" />
          </button>
          <h1 className="text-lg font-semibold">Coworking</h1>
          <div className={cn('ml-auto', isAdmin ? roleBadge.admin : roleBadge.user)}>
            {isAdmin ? (
              <Shield className="h-3 w-3" />
            ) : (
              <User className="h-3 w-3" />
            )}
            {isAdmin ? 'Admin' : 'User'}
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
