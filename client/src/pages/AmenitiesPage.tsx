import { useState } from 'react'
import useSWR, { mutate } from 'swr'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '../components/ui/Table'
import { amenities } from '../lib/api'
import { Plus, Pencil, Trash2, Search, ChevronLeft, ChevronRight, Wifi } from 'lucide-react'
import type { Amenity, AmenityCreate, Page } from '../types'

export function AmenitiesPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingAmenity, setEditingAmenity] = useState<Amenity | null>(null)
  const [formData, setFormData] = useState<AmenityCreate>({
    name: '',
    description: '',
  })

  const { data, isLoading } = useSWR<Page<Amenity>>(
    ['amenities', page],
    () => amenities.getAll(page, 10)
  )

  const { data: searchResults } = useSWR<Amenity[]>(
    search ? ['amenities-search', search] : null,
    () => amenities.search(search)
  )

  const displayAmenities = search ? searchResults : data?.content

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      if (editingAmenity) {
        await amenities.update(editingAmenity.id, formData)
      } else {
        await amenities.create(formData)
      }
      mutate(['amenities', page])
      closeModal()
    } catch (error) {
      console.error('Error saving amenity:', error)
    }
  }

  const handleDelete = async (id: number) => {
    if (confirm('Вы уверены, что хотите удалить удобство?')) {
      try {
        await amenities.delete(id)
        mutate(['amenities', page])
      } catch (error) {
        console.error('Error deleting amenity:', error)
      }
    }
  }

  const openModal = (amenity?: Amenity) => {
    if (amenity) {
      setEditingAmenity(amenity)
      setFormData({ name: amenity.name, description: amenity.description })
    } else {
      setEditingAmenity(null)
      setFormData({ name: '', description: '' })
    }
    setIsModalOpen(true)
  }

  const closeModal = () => {
    setIsModalOpen(false)
    setEditingAmenity(null)
    setFormData({ name: '', description: '' })
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold">Удобства</h1>
          <p className="text-[hsl(var(--muted-foreground))]">
            Управление удобствами коворкинга
          </p>
        </div>
        <Button onClick={() => openModal()}>
          <Plus className="h-4 w-4" />
          Добавить
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <CardTitle className="flex items-center gap-2">
              <Wifi className="h-5 w-5" />
              Список удобств
            </CardTitle>
            <div className="relative w-full sm:w-64">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted-foreground))]" />
              <Input
                placeholder="Поиск по названию..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9"
              />
            </div>
          </div>
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
                    <TableHead>Название</TableHead>
                    <TableHead>Описание</TableHead>
                    <TableHead className="text-right">Действия</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {displayAmenities?.map((amenity) => (
                    <TableRow key={amenity.id}>
                      <TableCell className="font-medium">{amenity.id}</TableCell>
                      <TableCell>
                        <span className="font-medium">{amenity.name}</span>
                      </TableCell>
                      <TableCell>
                        <span className="text-[hsl(var(--muted-foreground))]">
                          {amenity.description || '—'}
                        </span>
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => openModal(amenity)}
                          >
                            <Pencil className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleDelete(amenity.id)}
                          >
                            <Trash2 className="h-4 w-4 text-[hsl(var(--destructive))]" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              {!search && data && (
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
        title={editingAmenity ? 'Редактировать удобство' : 'Новое удобство'}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Название"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="Wi-Fi"
            required
          />
          <Input
            label="Описание"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            placeholder="Высокоскоростной интернет до 100 Мбит/с"
          />
          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={closeModal}>
              Отмена
            </Button>
            <Button type="submit">
              {editingAmenity ? 'Сохранить' : 'Создать'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
