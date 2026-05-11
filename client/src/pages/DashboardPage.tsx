import useSWR from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { useAuth } from '../context/AuthContext'
import { users, workspaces, bookings, payments, auth } from '../lib/api'
import { layout, badge, cn, statusText } from '../lib/styles'
import { Users, Building2, Calendar, CreditCard, TrendingUp, Clock, User } from 'lucide-react'
import type { Page, User as UserType, Workspace, Booking, Payment, BookingStatus } from '../types'

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
        <div className={layout.flexBetween}>
          <div>
            <p className="text-sm font-medium text-muted-foreground">{title}</p>
            <p className="mt-1 text-3xl font-bold">{value}</p>
            {description && (
              <p className="mt-1 text-xs text-muted-foreground">{description}</p>
            )}
          </div>
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
            {icon}
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

function getStatusBadgeClass(status: BookingStatus): string {
  const variants: Record<BookingStatus, string> = {
    CONFIRMED: badge.variants.success,
    PENDING: badge.variants.warning,
    CANCELLED: badge.variants.danger,
    COMPLETED: badge.variants.info,
  }
  return cn(badge.base, variants[status])
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
      <div className={layout.gridCols4}>
        <StatCard
          title="Пользователи"
          value={usersData?.totalElements || 0}
          icon={<Users className="h-6 w-6 text-primary" />}
          description="Зарегистрировано"
        />
        <StatCard
          title="Рабочие места"
          value={workspacesData?.totalElements || 0}
          icon={<Building2 className="h-6 w-6 text-primary" />}
          description="Доступно"
        />
        <StatCard
          title="Бронирования"
          value={bookingsData?.totalElements || 0}
          icon={<Calendar className="h-6 w-6 text-primary" />}
          description={`${pendingBookings} ожидают, ${confirmedBookings} подтверждено`}
        />
        <StatCard
          title="Платежи"
          value={paymentsData?.totalElements || 0}
          icon={<CreditCard className="h-6 w-6 text-primary" />}
          description="Всего операций"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className={layout.flexGap2}>
              <Clock className="h-5 w-5" />
              Последние бронирования
            </CardTitle>
          </CardHeader>
          <CardContent>
            {bookingsData?.content.slice(0, 5).map((booking) => (
              <div
                key={booking.id}
                className={cn(layout.flexBetween, 'border-b py-3 last:border-0')}
              >
                <div>
                  <p className="font-medium">{booking.userName}</p>
                  <p className="text-sm text-muted-foreground">
                    {booking.workspaceName}
                  </p>
                </div>
                <span className={getStatusBadgeClass(booking.status)}>
                  {statusText[booking.status]}
                </span>
              </div>
            )) || (
              <p className="text-muted-foreground">Нет данных</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className={layout.flexGap2}>
              <TrendingUp className="h-5 w-5" />
              Популярные рабочие места
            </CardTitle>
          </CardHeader>
          <CardContent>
            {workspacesData?.content.slice(0, 5).map((workspace) => (
              <div
                key={workspace.id}
                className={cn(layout.flexBetween, 'border-b py-3 last:border-0')}
              >
                <div>
                  <p className="font-medium">{workspace.name}</p>
                  <p className="text-sm text-muted-foreground">
                    Вместимость: {workspace.capacity} чел.
                  </p>
                </div>
                <span className="font-semibold text-primary">
                  {workspace.pricePerHour} руб/час
                </span>
              </div>
            )) || (
              <p className="text-muted-foreground">Нет данных</p>
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

  const { data: userInfo } = useSWR<UserType>(
    userEmail ? `user-${userEmail}` : null,
    () => users.getByEmail(userEmail)
  )

  const { data: userBookings } = useSWR<Booking[]>(
    userInfo?.id ? `user-bookings-${userInfo.id}` : null,
    () => bookings.getUserActive(userInfo!.id)
  )

  const { data: workspacesData } = useSWR<Page<Workspace>>('workspaces', () => workspaces.getAll())

  const activeBookings = userBookings?.filter(
    b => b.status === 'CONFIRMED' || b.status === 'PENDING'
  ).length || 0
  const pendingBookings = userBookings?.filter(b => b.status === 'PENDING').length || 0

  return (
    <>
      <div className={layout.gridCols3}>
        <StatCard
          title="Мои бронирования"
          value={userBookings?.length || 0}
          icon={<Calendar className="h-6 w-6 text-primary" />}
          description={`${activeBookings} активных`}
        />
        <StatCard
          title="Ожидают подтверждения"
          value={pendingBookings}
          icon={<Clock className="h-6 w-6 text-primary" />}
          description="Бронирований"
        />
        <StatCard
          title="Доступные места"
          value={workspacesData?.totalElements || 0}
          icon={<Building2 className="h-6 w-6 text-primary" />}
          description="Для бронирования"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className={layout.flexGap2}>
              <User className="h-5 w-5" />
              Профиль
            </CardTitle>
          </CardHeader>
          <CardContent>
            {userInfo ? (
              <div className="space-y-3">
                <ProfileRow label="Имя" value={userInfo.name} />
                <ProfileRow label="Email" value={userInfo.email} />
                <ProfileRow label="Телефон" value={userInfo.phone || 'Не указан'} isLast />
              </div>
            ) : (
              <p className="text-muted-foreground">Загрузка...</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className={layout.flexGap2}>
              <Calendar className="h-5 w-5" />
              Мои активные бронирования
            </CardTitle>
          </CardHeader>
          <CardContent>
            {userBookings && userBookings.length > 0 ? (
              userBookings.slice(0, 5).map((booking) => (
                <div
                  key={booking.id}
                  className={cn(layout.flexBetween, 'border-b py-3 last:border-0')}
                >
                  <div>
                    <p className="font-medium">{booking.workspaceName}</p>
                    <p className="text-sm text-muted-foreground">
                      {new Date(booking.startTime).toLocaleDateString('ru-RU')}
                    </p>
                  </div>
                  <span className={getStatusBadgeClass(booking.status)}>
                    {statusText[booking.status]}
                  </span>
                </div>
              ))
            ) : (
              <p className="text-muted-foreground">У вас нет активных бронирований</p>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  )
}

interface ProfileRowProps {
  label: string
  value: string
  isLast?: boolean
}

function ProfileRow({ label, value, isLast = false }: ProfileRowProps) {
  return (
    <div className={cn(layout.flexBetween, !isLast && 'border-b', 'py-2')}>
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium">{value}</span>
    </div>
  )
}

export function DashboardPage() {
  const { isAdmin } = useAuth()

  return (
    <div className={layout.page}>
      <div>
        <h1 className={layout.pageTitle}>
          {isAdmin ? 'Панель управления' : 'Личный кабинет'}
        </h1>
        <p className="text-muted-foreground">
          {isAdmin
            ? 'Обзор системы управления коворкингом'
            : 'Управление бронированиями и профилем'}
        </p>
      </div>

      {isAdmin ? <AdminDashboard /> : <UserDashboard />}
    </div>
  )
}
