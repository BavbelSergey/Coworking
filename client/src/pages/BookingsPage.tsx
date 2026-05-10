import { useState } from 'react'
import useSWR, { mutate } from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { Badge } from '../components/ui/Badge'
import { Select } from '../components/ui/Select'
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '../components/ui/Table'
import { bookings, users, workspaces } from '../lib/api'
import { Plus, Trash2, Check, X, ChevronLeft, ChevronRight } from 'lucide-react'
import { format } from 'date-fns'
import { ru } from 'date-fns/locale'
import type { Booking, BookingCreate, User, Workspace, Page, BookingStatus } from '../types'

const statusVariants: Record<BookingStatus, 'default' | 'success' | 'warning' | 'destructive' | 'secondary'> = {
  PENDING: 'warning',
  CONFIRMED: 'success',
  CANCELLED: 'destructive',
  COMPLETED: 'secondary',
}

const statusLabels: Record<BookingStatus, string> = {
  PENDING: 'Ожидает',
  CONFIRMED: 'Подтверждено',
  CANCELLED: 'Отменено',
  COMPLETED: 'Завершено',
}

export function BookingsPage() {
  const [page, setPage] = useState(0)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [formData, setFormData] = useState<BookingCreate>({
    startTime: '',
    endTime: '',
    userId: 0,
    workspaceId: 0,
  })

  const { data, isLoading } = useSWR<Page<Booking>>(
    ['bookings', page],
    () => bookings.getAll(page, 10)
  )

  const { data: usersData } = useSWR<Page<User>>('users-list', () => users.getAll(0, 100))
  const { data: workspacesData } = useSWR<Page<Workspace>>('workspaces-list', () => workspaces.getAll(0, 100))

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const payload = {
        ...formData,
        startTime: formData.startTime.replace('T', ' ') + ':00',
        endTime: formData.endTime.replace('T', ' ') + ':00',
      }
      await bookings.create(payload)
      mutate(['bookings', page])
      closeModal()
    } catch (error) {
      console.error('Error creating booking:', error)
    }
  }

  const handleConfirm = async (id: number) => {
    try {
      await bookings.confirm(id)
      mutate(['bookings', page])
    } catch (error) {
      console.error('Error confirming booking:', error)
    }
  }

  const handleCancel = async (id: number) => {
    try {
      await bookings.cancel(id)
      mutate(['bookings', page])
    } catch (error) {
      console.error('Error cancelling booking:', error)
    }
  }

  const handleDelete = async (id: number) => {
    if (confirm('Вы уверены, что хотите удалить бронирование?')) {
      try {
        await bookings.delete(id)
        mutate(['bookings', page])
      } catch (error) {
        console.error('Error deleting booking:', error)
      }
    }
  }

  const openModal = () => {
    setFormData({
      startTime: '',
      endTime: '',
      userId: usersData?.content[0]?.id || 0,
      workspaceId: workspacesData?.content[0]?.id || 0,
    })
    setIsModalOpen(true)
  }

  const closeModal = () => {
    setIsModalOpen(false)
    setFormData({ startTime: '', endTime: '', userId: 0, workspaceId: 0 })
  }

  const formatDateTime = (dateStr: string) => {
    try {
      return format(new Date(dateStr.replace(' ', 'T')), 'dd MMM yyyy, HH:mm', { locale: ru })
    } catch {
      return dateStr
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold">Бронирования</h1>
          <p className="text-[hsl(var(--muted-foreground))]">
            Управление бронированиями рабочих мест
          </p>
        </div>
        <Button onClick={openModal}>
          <Plus className="h-4 w-4" />
          Создать бронирование
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Список бронирований</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex h-32 items-center justify-center">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-[hsl(var(--primary))] border-t-transparent" />
            </div>
          ) : (
            <>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Пользователь</TableHead>
                    <TableHead>Рабочее место</TableHead>
                    <TableHead>Начало</TableHead>
                    <TableHead>Окончание</TableHead>
                    <TableHead>Статус</TableHead>
                    <TableHead className="text-right">Действия</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {data?.content.map((booking) => (
                    <TableRow key={booking.id}>
                      <TableCell className="font-medium">{booking.id}</TableCell>
                      <TableCell>
                        <div>
                          <p className="font-medium">{booking.userName}</p>
                          <p className="text-xs text-[hsl(var(--muted-foreground))]">
                            {booking.userEmail}
                          </p>
                        </div>
                      </TableCell>
                      <TableCell>{booking.workspaceName}</TableCell>
                      <TableCell>{formatDateTime(booking.startTime)}</TableCell>
                      <TableCell>{formatDateTime(booking.endTime)}</TableCell>
                      <TableCell>
                        <Badge variant={statusVariants[booking.status]}>
                          {statusLabels[booking.status]}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-1">
                          {booking.status === 'PENDING' && (
                            <>
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => handleConfirm(booking.id)}
                                title="Подтвердить"
                              >
                                <Check className="h-4 w-4 text-[hsl(var(--success))]" />
                              </Button>
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => handleCancel(booking.id)}
                                title="Отменить"
                              >
                                <X className="h-4 w-4 text-[hsl(var(--warning))]" />
                              </Button>
                            </>
                          )}
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleDelete(booking.id)}
                          >
                            <Trash2 className="h-4 w-4 text-[hsl(var(--destructive))]" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              {data && (
                <div className="mt-4 flex items-center justify-between">
                  <p className="text-sm text-[hsl(var(--muted-foreground))]">
                    Показано {data.content.length} из {data.totalElements}
                  </p>
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(p => p - 1)}
                      disabled={data.first}
                    >
                      <ChevronLeft className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(p => p + 1)}
                      disabled={data.last}
                    >
                      <ChevronRight className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={closeModal}
        title="Новое бронирование"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select
            label="Пользователь"
            value={String(formData.userId)}
            onChange={(e) => setFormData({ ...formData, userId: parseInt(e.target.value) })}
            options={usersData?.content.map(u => ({ value: String(u.id), label: u.name })) || []}
          />
          <Select
            label="Рабочее место"
            value={String(formData.workspaceId)}
            onChange={(e) => setFormData({ ...formData, workspaceId: parseInt(e.target.value) })}
            options={workspacesData?.content.map(w => ({ value: String(w.id), label: `${w.name} (${w.pricePerHour} руб/час)` })) || []}
          />
          <Input
            label="Начало"
            type="datetime-local"
            value={formData.startTime}
            onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
            required
          />
          <Input
            label="Окончание"
            type="datetime-local"
            value={formData.endTime}
            onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
            required
          />
          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={closeModal}>
              Отмена
            </Button>
            <Button type="submit">Создать</Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
