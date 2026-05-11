import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { cn, layout, form } from '../lib/styles'
import { Building2, UserPlus, LogIn } from 'lucide-react'

type AuthMode = 'login' | 'register'

const tabStyles = {
  container: 'mb-6 flex rounded-lg bg-secondary p-1',
  button: 'flex flex-1 items-center justify-center gap-2 rounded-md py-2 text-sm font-medium transition-colors',
  active: 'bg-card text-foreground shadow-sm',
  inactive: 'text-muted-foreground hover:text-foreground',
}

const alertStyles = {
  error: 'mb-4 rounded-lg bg-destructive/10 p-3 text-sm text-destructive',
  success: 'mb-4 rounded-lg bg-green-500/10 p-3 text-sm text-green-600',
}

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
      const message = err instanceof Error ? err.message : 'Ошибка при регистрации'
      setError(message)
    } finally {
      setIsLoading(false)
    }
  }

  const switchMode = (newMode: AuthMode) => {
    resetForm()
    setMode(newMode)
  }

  const isLoginMode = mode === 'login'
  const isRegisterMode = mode === 'register'

  return (
    <div className={cn(layout.flexCenter, 'min-h-screen bg-secondary p-4')}>
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className={cn(layout.flexCenter, 'mx-auto mb-4 h-16 w-16 rounded-full bg-primary')}>
            <Building2 className="h-8 w-8 text-primary-foreground" />
          </div>
          <CardTitle className="text-2xl">Coworking Space</CardTitle>
          <p className="text-sm text-muted-foreground">
            {isLoginMode
              ? 'Войдите в систему управления коворкингом'
              : 'Создайте аккаунт для бронирования'}
          </p>
        </CardHeader>

        <CardContent>
          {/* Mode Tabs */}
          <div className={tabStyles.container}>
            <button
              type="button"
              onClick={() => switchMode('login')}
              className={cn(
                tabStyles.button,
                isLoginMode ? tabStyles.active : tabStyles.inactive
              )}
            >
              <LogIn className="h-4 w-4" />
              Вход
            </button>
            <button
              type="button"
              onClick={() => switchMode('register')}
              className={cn(
                tabStyles.button,
                isRegisterMode ? tabStyles.active : tabStyles.inactive
              )}
            >
              <UserPlus className="h-4 w-4" />
              Регистрация
            </button>
          </div>

          {error && <div className={alertStyles.error}>{error}</div>}
          {success && <div className={alertStyles.success}>{success}</div>}

          {isLoginMode ? (
            <form onSubmit={handleLogin} className={form.group}>
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
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? 'Вход...' : 'Войти'}
              </Button>
            </form>
          ) : (
            <form onSubmit={handleRegister} className={form.group}>
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
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
