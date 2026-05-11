import useSWR from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { useAuth } from '../context/AuthContext'
import { users, workspaces, bookings, payments, auth } from '../lib/api'
import { Users, Building2, Calendar, CreditCard, TrendingUp, Clock, User } from 'lucide-react'
import type { Page, User as UserType, Workspace, Booking, Payment } from '../types'

interface StatCardProps {
  title: string
  value: string | number
  icon: React.ReactNode
  description?: string
}

function StatCard({ title, value, icon, description }: StatCardProps) {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-[hsl(var(--muted-foreground))]">{title}</p>
            <p className="mt-1 text-3xl font-bold">{value}</p>
            {description && (
              <p className="mt-1 text-xs text-[hsl(var(--muted-foreground))]">{description}</p>
            )}
          </div>
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[hsl(var(--primary))]/10">
            {icon}
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

function AdminDashboard() {
  const { data: usersData } = useSWR<Page<UserType>>('users', () => users.getAll())
  const { data: workspacesData } = useSWR<Page<Workspace>>('workspaces', () => workspaces.getAll())
  const { data: bookingsData } = useSWR<Page<Booking>>('bookings', () => bookings.getAll())
  const { data: paymentsData } = useSWR<Page<Payment>>('payments', () => payments.getAll())

  const pendingBookings = bookingsData?.content.filter(b => b.status === 'PENDING').length || 0
  const confirmedBookings = bookingsData?.content.filter(b => b.status === 'CONFIRMED').length || 0

  return (
    <>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Пользователи"
          value={usersData?.totalElements || 0}
          icon={<Users className="h-6 w-6 text-[hsl(var(--primary))]" />}
          description="Зарегистрировано"
        />
        <StatCard
          title="Рабочие места"
          value={workspacesData?.totalElements || 0}
          icon={<Building2 className="h-6 w-6 text-[hsl(var(--primary))]" />}
          description="Доступно"
        />
        <StatCard
          title="Бронирования"
          value={bookingsData?.totalElements || 0}
          icon={<Calendar className="h-6 w-6 text-[hsl(var(--primary))]" />}
          description={`${pendingBookings} ожидают, ${confirmedBookings} подтверждено`}
        />
        <StatCard
          title="Платежи"
          value={paymentsData?.totalElements || 0}
          icon={<CreditCard className="h-6 w-6 text-[hsl(var(--primary))]" />}
          description="Всего операций"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-5 w-5" />
              Последние бронирования
            </CardTitle>
          </CardHeader>
          <CardContent>
            {bookingsData?.content.slice(0, 5).map((booking) => (
              <div
                key={booking.id}
                className="flex items-center justify-between border-b py-3 last:border-0"
              >
                <div>
                  <p className="font-medium">{booking.userName}</p>
                  <p className="text-sm text-[hsl(var(--muted-foreground))]">
                    {booking.workspaceName}
                  </p>
                </div>
                <span
                  className={`rounded-full px-2 py-1 text-xs font-medium ${
                    booking.status === 'CONFIRMED'
                      ? 'bg-green-100 text-green-800'
                      : booking.status === 'PENDING'
                      ? 'bg-amber-100 text-amber-800'
                      : 'bg-red-100 text-red-800'
                  }`}
                >
                  {booking.status === 'CONFIRMED' ? 'Подтверждено' : 
                   booking.status === 'PENDING' ? 'Ожидает' : 
                   booking.status === 'CANCELLED' ? 'Отменено' : booking.status}
                </span>
              </div>
            )) || (
              <p className="text-[hsl(var(--muted-foreground))]">Нет данных</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5" />
              Популярные рабочие места
            </CardTitle>
          </CardHeader>
          <CardContent>
            {workspacesData?.content.slice(0, 5).map((workspace) => (
              <div
                key={workspace.id}
                className="flex items-center justify-between border-b py-3 last:border-0"
              >
                <div>
                  <p className="font-medium">{workspace.name}</p>
                  <p className="text-sm text-[hsl(var(--muted-foreground))]">
                    Вместимость: {workspace.capacity} чел.
                  </p>
                </div>
                <span className="font-semibold text-[hsl(var(--primary))]">
                  {workspace.pricePerHour} руб/час
                </span>
              </div>
            )) || (
              <p className="text-[hsl(var(--muted-foreground))]">Нет данных</p>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  )
}

function UserDashboard() {
  const decoded = auth.getDecodedToken()
  const userEmail = decoded?.sub || ''

  // User only sees their own bookings
  const { data: userInfo } = useSWR<UserType>(
    userEmail ? `user-${userEmail}` : null,
    () => users.getByEmail(userEmail)
  )

  const { data: userBookings } = useSWR<Booking[]>(
    userInfo?.id ? `user-bookings-${userInfo.id}` : null,
    () => bookings.getUserActive(userInfo!.id)
  )

  const { data: workspacesData } = useSWR<Page<Workspace>>('workspaces', () => workspaces.getAll())

  const activeBookings = userBookings?.filter(b => b.status === 'CONFIRMED' || b.status === 'PENDING').length || 0
  const pendingBookings = userBookings?.filter(b => b.status === 'PENDING').length || 0

  return (
    <>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard
          title="Мои бронирования"
          value={userBookings?.length || 0}
          icon={<Calendar className="h-6 w-6 text-[hsl(var(--primary))]" />}
          description={`${activeBookings} активных`}
        />
        <StatCard
          title="Ожидают подтверждения"
          value={pendingBookings}
          icon={<Clock className="h-6 w-6 text-[hsl(var(--primary))]" />}
          description="Бронирований"
        />
        <StatCard
          title="Доступные места"
          value={workspacesData?.totalElements || 0}
          icon={<Building2 className="h-6 w-6 text-[hsl(var(--primary))]" />}
          description="Для бронирования"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              Профиль
            </CardTitle>
          </CardHeader>
          <CardContent>
            {userInfo ? (
              <div className="space-y-3">
                <div className="flex justify-between border-b py-2">
                  <span className="text-[hsl(var(--muted-foreground))]">Имя</span>
                  <span className="font-medium">{userInfo.name}</span>
                </div>
                <div className="flex justify-between border-b py-2">
                  <span className="text-[hsl(var(--muted-foreground))]">Email</span>
                  <span className="font-medium">{userInfo.email}</span>
                </div>
                <div className="flex justify-between py-2">
                  <span className="text-[hsl(var(--muted-foreground))]">Телефон</span>
                  <span className="font-medium">{userInfo.phone || 'Не указан'}</span>
                </div>
              </div>
            ) : (
              <p className="text-[hsl(var(--muted-foreground))]">Загрузка...</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              Мои активные бронирования
            </CardTitle>
          </CardHeader>
          <CardContent>
            {userBookings && userBookings.length > 0 ? (
              userBookings.slice(0, 5).map((booking) => (
                <div
                  key={booking.id}
                  className="flex items-center justify-between border-b py-3 last:border-0"
                >
                  <div>
                    <p className="font-medium">{booking.workspaceName}</p>
                    <p className="text-sm text-[hsl(var(--muted-foreground))]">
                      {new Date(booking.startTime).toLocaleDateString('ru-RU')}
                    </p>
                  </div>
                  <span
                    className={`rounded-full px-2 py-1 text-xs font-medium ${
                      booking.status === 'CONFIRMED'
                        ? 'bg-green-100 text-green-800'
                        : booking.status === 'PENDING'
                        ? 'bg-amber-100 text-amber-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {booking.status === 'CONFIRMED' ? 'Подтверждено' : 
                     booking.status === 'PENDING' ? 'Ожидает' : 
                     booking.status === 'CANCELLED' ? 'Отменено' : booking.status}
                  </span>
                </div>
              ))
            ) : (
              <p className="text-[hsl(var(--muted-foreground))]">У вас нет активных бронирований</p>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  )
}

export function DashboardPage() {
  const { isAdmin } = useAuth()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">
          {isAdmin ? 'Панель управления' : 'Личный кабинет'}
        </h1>
        <p className="text-[hsl(var(--muted-foreground))]">
          {isAdmin 
            ? 'Обзор системы управления коворкингом'
            : 'Управление бронированиями и профилем'}
        </p>
      </div>

      {isAdmin ? <AdminDashboard /> : <UserDashboard />}
    </div>
  )
}
