import { useState } from 'react'
import useSWR, { mutate } from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { Badge } from '../components/ui/Badge'
import { workspaces, amenities } from '../lib/api'
import { Plus, Pencil, Trash2, Users, ChevronLeft, ChevronRight, Wifi } from 'lucide-react'
import type { Workspace, WorkspaceCreate, Amenity, Page } from '../types'

export function WorkspacesPage() {
  const [page, setPage] = useState(0)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingWorkspace, setEditingWorkspace] = useState<Workspace | null>(null)
  const [formData, setFormData] = useState<WorkspaceCreate>({
    name: '',
    phoneNumber: '',
    capacity: 1,
    pricePerHour: 0,
  })

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

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold">Рабочие места</h1>
          <p className="text-[hsl(var(--muted-foreground))]">
            Управление рабочими местами коворкинга
          </p>
        </div>
        <Button onClick={() => openModal()}>
          <Plus className="h-4 w-4" />
          Добавить
        </Button>
      </div>

      {isLoading ? (
        <div className="flex h-32 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-[hsl(var(--primary))] border-t-transparent" />
        </div>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {data?.content.map((workspace) => (
              <Card key={workspace.id}>
                <CardHeader className="pb-2">
                  <div className="flex items-start justify-between">
                    <CardTitle className="text-lg">{workspace.name}</CardTitle>
                    <div className="flex gap-1">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => openModal(workspace)}
                      >
                        <Pencil className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDelete(workspace.id)}
                      >
                        <Trash2 className="h-4 w-4 text-[hsl(var(--destructive))]" />
                      </Button>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    <div className="flex items-center justify-between text-sm">
                      <span className="flex items-center gap-2 text-[hsl(var(--muted-foreground))]">
                        <Users className="h-4 w-4" />
                        Вместимость
                      </span>
                      <span className="font-medium">{workspace.capacity} чел.</span>
                    </div>
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-[hsl(var(--muted-foreground))]">Цена</span>
                      <span className="font-semibold text-[hsl(var(--primary))]">
                        {workspace.pricePerHour} руб/час
                      </span>
                    </div>
                    {workspace.phoneNumber && (
                      <div className="flex items-center justify-between text-sm">
                        <span className="text-[hsl(var(--muted-foreground))]">Телефон</span>
                        <span>{workspace.phoneNumber}</span>
                      </div>
                    )}
                    <div className="pt-2">
                      <p className="mb-2 flex items-center gap-1 text-sm text-[hsl(var(--muted-foreground))]">
                        <Wifi className="h-4 w-4" />
                        Удобства
                      </p>
                      <div className="flex flex-wrap gap-1">
                        {workspace.amenities?.length > 0 ? (
                          workspace.amenities.map((amenity) => (
                            <Badge
                              key={amenity.id}
                              variant="secondary"
                              className="cursor-pointer hover:bg-[hsl(var(--destructive))]/10"
                              onClick={() => handleToggleAmenity(workspace.id, amenity.id, true)}
                            >
                              {amenity.name}
                            </Badge>
                          ))
                        ) : (
                          <span className="text-xs text-[hsl(var(--muted-foreground))]">
                            Нет удобств
                          </span>
                        )}
                      </div>
                      {amenitiesData && (
                        <div className="mt-2 flex flex-wrap gap-1">
                          {amenitiesData.content
                            .filter(a => !workspace.amenities?.some(wa => wa.id === a.id))
                            .slice(0, 3)
                            .map((amenity) => (
                              <Badge
                                key={amenity.id}
                                variant="secondary"
                                className="cursor-pointer opacity-50 hover:opacity-100"
                                onClick={() => handleToggleAmenity(workspace.id, amenity.id, false)}
                              >
                                + {amenity.name}
                              </Badge>
                            ))}
                        </div>
                      )}
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {data && (
            <div className="flex items-center justify-between">
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

      <Modal
        isOpen={isModalOpen}
        onClose={closeModal}
        title={editingWorkspace ? 'Редактировать рабочее место' : 'Новое рабочее место'}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
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
    </div>
  )
}
