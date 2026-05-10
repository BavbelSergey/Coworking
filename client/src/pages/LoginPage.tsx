import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { Building2 } from 'lucide-react'

export function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setIsLoading(true)

    try {
      await login(email, password)
      navigate('/')
    } catch {
      setError('Неверный email или пароль')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[hsl(var(--secondary))] p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-[hsl(var(--primary))]">
            <Building2 className="h-8 w-8 text-[hsl(var(--primary-foreground))]" />
          </div>
          <CardTitle className="text-2xl">Coworking Space</CardTitle>
          <p className="text-sm text-[hsl(var(--muted-foreground))]">
            Войдите в систему управления коворкингом
          </p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="rounded-lg bg-[hsl(var(--destructive))]/10 p-3 text-sm text-[hsl(var(--destructive))]">
                {error}
              </div>
            )}
            <Input
              label="Email"
              type="email"
              placeholder="admin@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <Input
              label="Пароль"
              type="password"
              placeholder="Введите пароль"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <Button
              type="submit"
              className="w-full"
              disabled={isLoading}
            >
              {isLoading ? 'Вход...' : 'Войти'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
