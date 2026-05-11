import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { Building2, UserPlus, LogIn } from 'lucide-react'

type AuthMode = 'login' | 'register'

export function LoginPage() {
  const [mode, setMode] = useState<AuthMode>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const { login, register } = useAuth()
  const navigate = useNavigate()

  const resetForm = () => {
    setEmail('')
    setPassword('')
    setName('')
    setPhone('')
    setError('')
    setSuccess('')
  }

  const handleLogin = async (e: React.FormEvent) => {
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

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setIsLoading(true)

    if (password.length < 8) {
      setError('Пароль должен быть не менее 8 символов')
      setIsLoading(false)
      return
    }

    try {
      await register({ name, email, phone, password })
      setSuccess('Регистрация успешна! Теперь вы можете войти.')
      setMode('login')
      setPassword('')
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message || 'Ошибка при регистрации')
      } else {
        setError('Ошибка при регистрации')
      }
    } finally {
      setIsLoading(false)
    }
  }

  const switchMode = (newMode: AuthMode) => {
    resetForm()
    setMode(newMode)
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
            {mode === 'login' 
              ? 'Войдите в систему управления коворкингом'
              : 'Создайте аккаунт для бронирования'}
          </p>
        </CardHeader>
        <CardContent>
          {/* Mode Tabs */}
          <div className="mb-6 flex rounded-lg bg-[hsl(var(--secondary))] p-1">
            <button
              type="button"
              onClick={() => switchMode('login')}
              className={`flex flex-1 items-center justify-center gap-2 rounded-md py-2 text-sm font-medium transition-colors ${
                mode === 'login'
                  ? 'bg-[hsl(var(--card))] text-[hsl(var(--foreground))] shadow-sm'
                  : 'text-[hsl(var(--muted-foreground))] hover:text-[hsl(var(--foreground))]'
              }`}
            >
              <LogIn className="h-4 w-4" />
              Вход
            </button>
            <button
              type="button"
              onClick={() => switchMode('register')}
              className={`flex flex-1 items-center justify-center gap-2 rounded-md py-2 text-sm font-medium transition-colors ${
                mode === 'register'
                  ? 'bg-[hsl(var(--card))] text-[hsl(var(--foreground))] shadow-sm'
                  : 'text-[hsl(var(--muted-foreground))] hover:text-[hsl(var(--foreground))]'
              }`}
            >
              <UserPlus className="h-4 w-4" />
              Регистрация
            </button>
          </div>

          {error && (
            <div className="mb-4 rounded-lg bg-[hsl(var(--destructive))]/10 p-3 text-sm text-[hsl(var(--destructive))]">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 rounded-lg bg-green-500/10 p-3 text-sm text-green-600">
              {success}
            </div>
          )}

          {mode === 'login' ? (
            <form onSubmit={handleLogin} className="space-y-4">
              <Input
                label="Email"
                type="email"
                placeholder="user@example.com"
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
          ) : (
            <form onSubmit={handleRegister} className="space-y-4">
              <Input
                label="Имя"
                type="text"
                placeholder="Иван Петров"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
              <Input
                label="Email"
                type="email"
                placeholder="user@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
              <Input
                label="Телефон"
                type="tel"
                placeholder="+79161234567"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
              <Input
                label="Пароль"
                type="password"
                placeholder="Минимум 8 символов"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                minLength={8}
              />
              <Button
                type="submit"
                className="w-full"
                disabled={isLoading}
              >
                {isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
