import useSWR from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { users, workspaces, bookings, payments } from '../lib/api'
import { Users, Building2, Calendar, CreditCard, TrendingUp, Clock } from 'lucide-react'
import type { Page, User, Workspace, Booking, Payment } from '../types'

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

export function DashboardPage() {
  const { data: usersData } = useSWR<Page<User>>('users', () => users.getAll())
  const { data: workspacesData } = useSWR<Page<Workspace>>('workspaces', () => workspaces.getAll())
  const { data: bookingsData } = useSWR<Page<Booking>>('bookings', () => bookings.getAll())
  const { data: paymentsData } = useSWR<Page<Payment>>('payments', () => payments.getAll())

  const pendingBookings = bookingsData?.content.filter(b => b.status === 'PENDING').length || 0
  const confirmedBookings = bookingsData?.content.filter(b => b.status === 'CONFIRMED').length || 0

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Панель управления</h1>
        <p className="text-[hsl(var(--muted-foreground))]">
          Обзор системы управления коворкингом
        </p>
      </div>

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
                      ? 'bg-[hsl(var(--success))]/10 text-[hsl(var(--success))]'
                      : booking.status === 'PENDING'
                      ? 'bg-[hsl(var(--warning))]/10 text-[hsl(var(--warning))]'
                      : 'bg-[hsl(var(--destructive))]/10 text-[hsl(var(--destructive))]'
                  }`}
                >
                  {booking.status}
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
    </div>
  )
}
