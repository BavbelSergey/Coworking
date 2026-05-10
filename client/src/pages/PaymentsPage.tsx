import { useState } from 'react'
import useSWR, { mutate } from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { Badge } from '../components/ui/Badge'
import { Select } from '../components/ui/Select'
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '../components/ui/Table'
import { payments, bookings } from '../lib/api'
import { Plus, Trash2, ChevronLeft, ChevronRight, CreditCard } from 'lucide-react'
import { format } from 'date-fns'
import { ru } from 'date-fns/locale'
import type { Payment, PaymentCreate, Booking, Page } from '../types'

const methodLabels: Record<string, string> = {
  CARD: 'Карта',
  CASH: 'Наличные',
  TRANSFER: 'Перевод',
}

const methodVariants: Record<string, 'default' | 'success' | 'warning' | 'destructive' | 'secondary'> = {
  CARD: 'default',
  CASH: 'success',
  TRANSFER: 'secondary',
}

export function PaymentsPage() {
  const [page, setPage] = useState(0)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [formData, setFormData] = useState<PaymentCreate>({
    amount: 0,
    paymentMethod: 'CARD',
    bookingId: 0,
  })

  const { data, isLoading } = useSWR<Page<Payment>>(
    ['payments', page],
    () => payments.getAll(page, 10)
  )

  const { data: bookingsData } = useSWR<Page<Booking>>(
    'bookings-list',
    () => bookings.getAll(0, 100)
  )

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await payments.create(formData)
      mutate(['payments', page])
      closeModal()
    } catch (error) {
      console.error('Error creating payment:', error)
    }
  }

  const handleDelete = async (id: number) => {
    if (confirm('Вы уверены, что хотите удалить платёж?')) {
      try {
        await payments.delete(id)
        mutate(['payments', page])
      } catch (error) {
        console.error('Error deleting payment:', error)
      }
    }
  }

  const openModal = () => {
    setFormData({
      amount: 0,
      paymentMethod: 'CARD',
      bookingId: bookingsData?.content[0]?.id || 0,
    })
    setIsModalOpen(true)
  }

  const closeModal = () => {
    setIsModalOpen(false)
    setFormData({ amount: 0, paymentMethod: 'CARD', bookingId: 0 })
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
          <h1 className="text-2xl font-bold">Платежи</h1>
          <p className="text-[hsl(var(--muted-foreground))]">
            Управление платежами за бронирования
          </p>
        </div>
        <Button onClick={openModal}>
          <Plus className="h-4 w-4" />
          Добавить платёж
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Список платежей
          </CardTitle>
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
                    <TableHead>Сумма</TableHead>
                    <TableHead>Метод</TableHead>
                    <TableHead>Дата</TableHead>
                    <TableHead>Пользователь</TableHead>
                    <TableHead>Бронирование</TableHead>
                    <TableHead className="text-right">Действия</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {data?.content.map((payment) => (
                    <TableRow key={payment.id}>
                      <TableCell className="font-medium">{payment.id}</TableCell>
                      <TableCell>
                        <span className="font-semibold text-[hsl(var(--primary))]">
                          {payment.amount} руб
                        </span>
                      </TableCell>
                      <TableCell>
                        <Badge variant={methodVariants[payment.paymentMethod]}>
                          {methodLabels[payment.paymentMethod] || payment.paymentMethod}
                        </Badge>
                      </TableCell>
                      <TableCell>{formatDateTime(payment.date)}</TableCell>
                      <TableCell>{payment.userName}</TableCell>
                      <TableCell>
                        <span className="text-sm text-[hsl(var(--muted-foreground))]">
                          #{payment.bookingId}
                        </span>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(payment.id)}
                        >
                          <Trash2 className="h-4 w-4 text-[hsl(var(--destructive))]" />
                        </Button>
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
        title="Новый платёж"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select
            label="Бронирование"
            value={String(formData.bookingId)}
            onChange={(e) => setFormData({ ...formData, bookingId: parseInt(e.target.value) })}
            options={bookingsData?.content.map(b => ({
              value: String(b.id),
              label: `#${b.id} - ${b.userName} (${b.workspaceName})`
            })) || []}
          />
          <Input
            label="Сумма (руб)"
            type="number"
            min={0}
            step={0.01}
            value={formData.amount}
            onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) })}
            required
          />
          <Select
            label="Метод оплаты"
            value={formData.paymentMethod}
            onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value })}
            options={[
              { value: 'CARD', label: 'Карта' },
              { value: 'CASH', label: 'Наличные' },
              { value: 'TRANSFER', label: 'Перевод' },
            ]}
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
