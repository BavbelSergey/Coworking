import { useState } from 'react'
import useSWR, { mutate } from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { Badge } from '../components/ui/Badge'
import { useAuth } from '../context/AuthContext'
import { workspaces, amenities, bookings, users, auth } from '../lib/api'
import { layout, form, cn } from '../lib/styles'
import { Plus, Pencil, Trash2, Users, ChevronLeft, ChevronRight, Wifi, Calendar } from 'lucide-react'
import type { Workspace, WorkspaceCreate, Amenity, Page, User, BookingCreate } from '../types'

const cardStyles = {
  amenityBadge: 'cursor-pointer hover:bg-destructive/10',
  addAmenityBadge: 'cursor-pointer opacity-50 hover:opacity-100',
}

const alertStyles = {
  error: 'rounded-lg bg-destructive/10 p-3 text-sm text-destructive',
  success: 'rounded-lg bg-green-500/10 p-3 text-sm text-green-600',
}

const infoBoxStyles = 'rounded-lg bg-secondary p-3'

export function WorkspacesPage() {
  const { isAdmin } = useAuth()
  const [page, setPage] = useState(0)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isBookingModalOpen, setIsBookingModalOpen] = useState(false)
  const [editingWorkspace, setEditingWorkspace] = useState<Workspace | null>(null)
  const [bookingWorkspace, setBookingWorkspace] = useState<Workspace | null>(null)
  const [formData, setFormData] = useState<WorkspaceCreate>({
    name: '',
    phoneNumber: '',
    capacity: 1,
    pricePerHour: 0,
  })
  const [bookingData, setBookingData] = useState({
    startTime: '',
    endTime: '',
  })
  const [bookingError, setBookingError] = useState('')
  const [bookingSuccess, setBookingSuccess] = useState('')

  const decoded = auth.getDecodedToken()
  const userEmail = decoded?.sub || ''

  const { data: currentUser } = useSWR<User>(
    userEmail ? `user-${userEmail}` : null,
    () => users.getByEmail(userEmail)
  )

  const { data, isLoading } = useSWR<Page<Workspace>>(
    ['workspaces', page],
    () => workspaces.getAll(page, 10)
  )

  const { data: amenitiesData } = useSWR<Page<Amenity>>(
    'amenities',
    () => amenities.getAll(0, 100)
  )

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      if (editingWorkspace) {
        await workspaces.update(editingWorkspace.id, formData)
      } else {
        await workspaces.create(formData)
      }
      mutate(['workspaces', page])
      closeModal()
    } catch (error) {
      console.error('Error saving workspace:', error)
    }
  }

  const handleDelete = async (id: number) => {
    if (confirm('Вы уверены, что хотите удалить рабочее место?')) {
      try {
        await workspaces.delete(id)
        mutate(['workspaces', page])
      } catch (error) {
        console.error('Error deleting workspace:', error)
      }
    }
  }

  const handleToggleAmenity = async (workspaceId: number, amenityId: number, hasAmenity: boolean) => {
    try {
      if (hasAmenity) {
        await workspaces.removeAmenity(workspaceId, amenityId)
      } else {
        await workspaces.addAmenity(workspaceId, amenityId)
      }
      mutate(['workspaces', page])
    } catch (error) {
      console.error('Error toggling amenity:', error)
    }
  }

  const handleBookingSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setBookingError('')
    setBookingSuccess('')

    if (!currentUser || !bookingWorkspace) {
      setBookingError('Не удалось получить данные пользователя')
      return
    }

    try {
      const bookingPayload: BookingCreate = {
        startTime: new Date(bookingData.startTime).toISOString(),
        endTime: new Date(bookingData.endTime).toISOString(),
        userId: currentUser.id,
        workspaceId: bookingWorkspace.id,
      }
      await bookings.create(bookingPayload)
      setBookingSuccess('Бронирование успешно создано!')
      setTimeout(() => {
        closeBookingModal()
      }, 1500)
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Ошибка при создании бронирования'
      setBookingError(message)
    }
  }

  const openModal = (workspace?: Workspace) => {
    if (workspace) {
      setEditingWorkspace(workspace)
      setFormData({
        name: workspace.name,
        phoneNumber: workspace.phoneNumber,
        capacity: workspace.capacity,
        pricePerHour: workspace.pricePerHour,
      })
    } else {
      setEditingWorkspace(null)
      setFormData({ name: '', phoneNumber: '', capacity: 1, pricePerHour: 0 })
    }
    setIsModalOpen(true)
  }

  const closeModal = () => {
    setIsModalOpen(false)
    setEditingWorkspace(null)
    setFormData({ name: '', phoneNumber: '', capacity: 1, pricePerHour: 0 })
  }

  const openBookingModal = (workspace: Workspace) => {
    setBookingWorkspace(workspace)
    setBookingData({ startTime: '', endTime: '' })
    setBookingError('')
    setBookingSuccess('')
    setIsBookingModalOpen(true)
  }

  const closeBookingModal = () => {
    setIsBookingModalOpen(false)
    setBookingWorkspace(null)
    setBookingData({ startTime: '', endTime: '' })
    setBookingError('')
    setBookingSuccess('')
  }

  const pageDescription = isAdmin
    ? 'Управление рабочими местами коворкинга'
    : 'Выберите рабочее место для бронирования'

  return (
    <div className={layout.page}>
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className={layout.pageTitle}>Рабочие места</h1>
          <p className="text-muted-foreground">{pageDescription}</p>
        </div>
        {isAdmin && (
          <Button onClick={() => openModal()}>
            <Plus className="h-4 w-4" />
            Добавить
          </Button>
        )}
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : (
        <>
          <div className={layout.gridCols3}>
            {data?.content.map((workspace) => (
              <WorkspaceCard
                key={workspace.id}
                workspace={workspace}
                isAdmin={isAdmin}
                amenitiesData={amenitiesData}
                onEdit={() => openModal(workspace)}
                onDelete={() => handleDelete(workspace.id)}
                onToggleAmenity={handleToggleAmenity}
                onBook={() => openBookingModal(workspace)}
              />
            ))}
          </div>

          {data && (
            <Pagination
              current={data.content.length}
              total={data.totalElements}
              isFirst={data.first}
              isLast={data.last}
              onPrev={() => setPage(p => p - 1)}
              onNext={() => setPage(p => p + 1)}
            />
          )}
        </>
      )}

      {/* Admin: Create/Edit Workspace Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={closeModal}
        title={editingWorkspace ? 'Редактировать рабочее место' : 'Новое рабочее место'}
      >
        <form onSubmit={handleSubmit} className={form.group}>
          <Input
            label="Название"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            required
          />
          <Input
            label="Телефон"
            value={formData.phoneNumber || ''}
            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
            placeholder="+79161234567"
          />
          <Input
            label="Вместимость"
            type="number"
            min={1}
            value={formData.capacity}
            onChange={(e) => setFormData({ ...formData, capacity: parseInt(e.target.value) })}
            required
          />
          <Input
            label="Цена за час (руб)"
            type="number"
            min={0}
            step={0.01}
            value={formData.pricePerHour}
            onChange={(e) => setFormData({ ...formData, pricePerHour: parseFloat(e.target.value) })}
            required
          />
          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={closeModal}>
              Отмена
            </Button>
            <Button type="submit">
              {editingWorkspace ? 'Сохранить' : 'Создать'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* User: Booking Modal */}
      <Modal
        isOpen={isBookingModalOpen}
        onClose={closeBookingModal}
        title={`Бронирование: ${bookingWorkspace?.name || ''}`}
      >
        <form onSubmit={handleBookingSubmit} className={form.group}>
          {bookingError && <div className={alertStyles.error}>{bookingError}</div>}
          {bookingSuccess && <div className={alertStyles.success}>{bookingSuccess}</div>}

          {bookingWorkspace && (
            <div className={infoBoxStyles}>
              <p className="text-sm">
                <span className="text-muted-foreground">Цена:</span>{' '}
                <span className="font-semibold">{bookingWorkspace.pricePerHour} руб/час</span>
              </p>
              <p className="text-sm">
                <span className="text-muted-foreground">Вместимость:</span>{' '}
                <span className="font-semibold">{bookingWorkspace.capacity} чел.</span>
              </p>
            </div>
          )}

          <Input
            label="Дата и время начала"
            type="datetime-local"
            value={bookingData.startTime}
            onChange={(e) => setBookingData({ ...bookingData, startTime: e.target.value })}
            required
          />
          <Input
            label="Дата и время окончания"
            type="datetime-local"
            value={bookingData.endTime}
            onChange={(e) => setBookingData({ ...bookingData, endTime: e.target.value })}
            required
          />
          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={closeBookingModal}>
              Отмена
            </Button>
            <Button type="submit">Забронировать</Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}

/* Sub-components */

function LoadingSpinner() {
  return (
    <div className="flex h-32 items-center justify-center">
      <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
    </div>
  )
}

interface WorkspaceCardProps {
  workspace: Workspace
  isAdmin: boolean
  amenitiesData?: Page<Amenity>
  onEdit: () => void
  onDelete: () => void
  onToggleAmenity: (workspaceId: number, amenityId: number, hasAmenity: boolean) => void
  onBook: () => void
}

function WorkspaceCard({
  workspace,
  isAdmin,
  amenitiesData,
  onEdit,
  onDelete,
  onToggleAmenity,
  onBook,
}: WorkspaceCardProps) {
  const availableAmenities = amenitiesData?.content.filter(
    a => !workspace.amenities?.some(wa => wa.id === a.id)
  ).slice(0, 3) || []

  return (
    <Card>
      <CardHeader className="pb-2">
        <div className={layout.flexBetween}>
          <CardTitle className="text-lg">{workspace.name}</CardTitle>
          {isAdmin && (
            <div className={layout.flexGap2}>
              <Button variant="ghost" size="sm" onClick={onEdit}>
                <Pencil className="h-4 w-4" />
              </Button>
              <Button variant="ghost" size="sm" onClick={onDelete}>
                <Trash2 className="h-4 w-4 text-destructive" />
              </Button>
            </div>
          )}
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          <InfoRow
            icon={<Users className="h-4 w-4" />}
            label="Вместимость"
            value={`${workspace.capacity} чел.`}
          />
          <InfoRow
            label="Цена"
            value={`${workspace.pricePerHour} руб/час`}
            valueClassName="font-semibold text-primary"
          />
          {workspace.phoneNumber && (
            <InfoRow label="Телефон" value={workspace.phoneNumber} />
          )}

          <div className="pt-2">
            <p className={cn(layout.flexGap2, 'mb-2 text-sm text-muted-foreground')}>
              <Wifi className="h-4 w-4" />
              Удобства
            </p>
            <div className="flex flex-wrap gap-1">
              {workspace.amenities?.length > 0 ? (
                workspace.amenities.map((amenity) => (
                  <Badge
                    key={amenity.id}
                    variant="secondary"
                    className={isAdmin ? cardStyles.amenityBadge : ''}
                    onClick={() => isAdmin && onToggleAmenity(workspace.id, amenity.id, true)}
                  >
                    {amenity.name}
                  </Badge>
                ))
              ) : (
                <span className="text-xs text-muted-foreground">Нет удобств</span>
              )}
            </div>
            {isAdmin && availableAmenities.length > 0 && (
              <div className="mt-2 flex flex-wrap gap-1">
                {availableAmenities.map((amenity) => (
                  <Badge
                    key={amenity.id}
                    variant="secondary"
                    className={cardStyles.addAmenityBadge}
                    onClick={() => onToggleAmenity(workspace.id, amenity.id, false)}
                  >
                    + {amenity.name}
                  </Badge>
                ))}
              </div>
            )}
          </div>

          {!isAdmin && (
            <div className="pt-3">
              <Button className="w-full" onClick={onBook}>
                <Calendar className="h-4 w-4" />
                Забронировать
              </Button>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}

interface InfoRowProps {
  icon?: React.ReactNode
  label: string
  value: string
  valueClassName?: string
}

function InfoRow({ icon, label, value, valueClassName = 'font-medium' }: InfoRowProps) {
  return (
    <div className={cn(layout.flexBetween, 'text-sm')}>
      <span className={cn(layout.flexGap2, 'text-muted-foreground')}>
        {icon}
        {label}
      </span>
      <span className={valueClassName}>{value}</span>
    </div>
  )
}

interface PaginationProps {
  current: number
  total: number
  isFirst: boolean
  isLast: boolean
  onPrev: () => void
  onNext: () => void
}

function Pagination({ current, total, isFirst, isLast, onPrev, onNext }: PaginationProps) {
  return (
    <div className={layout.flexBetween}>
      <p className="text-sm text-muted-foreground">
        Показано {current} из {total}
      </p>
      <div className={layout.flexGap2}>
        <Button variant="outline" size="sm" onClick={onPrev} disabled={isFirst}>
          <ChevronLeft className="h-4 w-4" />
        </Button>
        <Button variant="outline" size="sm" onClick={onNext} disabled={isLast}>
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}
