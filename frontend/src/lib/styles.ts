/**
 * Centralized style definitions for consistent UI
 * Using semantic class names for better readability
 */

export const colors = {
  primary: 'hsl(var(--primary))',
  primaryForeground: 'hsl(var(--primary-foreground))',
  secondary: 'hsl(var(--secondary))',
  foreground: 'hsl(var(--foreground))',
  mutedForeground: 'hsl(var(--muted-foreground))',
  card: 'hsl(var(--card))',
  destructive: 'hsl(var(--destructive))',
} as const

export const layout = {
  page: 'space-y-6',
  pageHeader: 'mb-6',
  pageTitle: 'text-2xl font-bold',
  pageDescription: 'text-muted-foreground',
  gridCols2: 'grid gap-4 sm:grid-cols-2',
  gridCols3: 'grid gap-4 sm:grid-cols-2 lg:grid-cols-3',
  gridCols4: 'grid gap-4 sm:grid-cols-2 lg:grid-cols-4',
  flexBetween: 'flex items-center justify-between',
  flexCenter: 'flex items-center justify-center',
  flexGap2: 'flex items-center gap-2',
  flexGap3: 'flex items-center gap-3',
  flexGap4: 'flex items-center gap-4',
} as const

export const card = {
  base: 'rounded-xl border bg-card shadow-sm',
  header: 'border-b p-4',
  headerWithTitle: 'flex items-center justify-between border-b p-4',
  title: 'text-lg font-semibold',
  content: 'p-4',
  contentPadded: 'p-6',
} as const

export const form = {
  group: 'space-y-4',
  label: 'block text-sm font-medium text-foreground mb-1.5',
  input: [
    'w-full rounded-lg border border-input bg-background px-4 py-2.5',
    'text-sm placeholder:text-muted-foreground',
    'focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-1',
    'disabled:cursor-not-allowed disabled:opacity-50',
  ].join(' '),
  select: [
    'w-full rounded-lg border border-input bg-background px-4 py-2.5',
    'text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-1',
  ].join(' '),
  error: 'text-sm text-destructive mt-1',
} as const

export const button = {
  base: [
    'inline-flex items-center justify-center gap-2',
    'rounded-lg font-medium transition-colors',
    'focus:outline-none focus:ring-2 focus:ring-offset-1',
    'disabled:pointer-events-none disabled:opacity-50',
  ].join(' '),
  sizes: {
    sm: 'h-8 px-3 text-xs',
    md: 'h-10 px-4 text-sm',
    lg: 'h-12 px-6 text-base',
  },
  variants: {
    primary: 'bg-primary text-primary-foreground hover:bg-primary/90 focus:ring-primary',
    secondary: 'bg-secondary text-foreground hover:bg-secondary/80 focus:ring-secondary',
    outline: 'border border-input bg-transparent hover:bg-secondary focus:ring-primary',
    ghost: 'bg-transparent hover:bg-secondary focus:ring-primary',
    destructive: 'bg-destructive text-destructive-foreground hover:bg-destructive/90 focus:ring-destructive',
  },
} as const

export const badge = {
  base: 'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium',
  variants: {
    default: 'bg-secondary text-foreground',
    primary: 'bg-primary/10 text-primary',
    success: 'bg-green-100 text-green-800',
    warning: 'bg-amber-100 text-amber-800',
    danger: 'bg-red-100 text-red-800',
    info: 'bg-blue-100 text-blue-800',
  },
} as const

export const table = {
  container: 'w-full overflow-auto',
  base: 'w-full caption-bottom text-sm',
  header: 'border-b bg-secondary/50',
  headerCell: 'h-12 px-4 text-left font-medium text-muted-foreground',
  body: '[&_tr:last-child]:border-0',
  row: 'border-b transition-colors hover:bg-secondary/50',
  cell: 'p-4 align-middle',
} as const

export const modal = {
  overlay: 'fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4',
  content: 'bg-card rounded-xl shadow-xl w-full max-w-md max-h-[90vh] overflow-auto',
  header: 'flex items-center justify-between border-b p-4',
  title: 'text-lg font-semibold',
  body: 'p-4',
  footer: 'flex items-center justify-end gap-2 border-t p-4',
} as const

export const nav = {
  sidebar: 'fixed inset-y-0 left-0 z-50 w-64 bg-card shadow-lg',
  sidebarHeader: 'flex h-16 items-center justify-between border-b px-6',
  sidebarNav: 'flex flex-col gap-1 p-4',
  navLink: [
    'flex items-center gap-3 rounded-lg px-4 py-3',
    'text-sm font-medium transition-colors',
  ].join(' '),
  navLinkActive: 'bg-primary text-primary-foreground',
  navLinkInactive: 'text-muted-foreground hover:bg-secondary hover:text-foreground',
} as const

export const roleBadge = {
  admin: 'inline-flex items-center gap-2 rounded-full px-3 py-1 text-xs font-medium bg-amber-100 text-amber-800',
  user: 'inline-flex items-center gap-2 rounded-full px-3 py-1 text-xs font-medium bg-blue-100 text-blue-800',
} as const

export const statusBadge = {
  PENDING: badge.variants.warning,
  CONFIRMED: badge.variants.success,
  CANCELLED: badge.variants.danger,
  COMPLETED: badge.variants.info,
} as const

export const statusText = {
  PENDING: 'Ожидает',
  CONFIRMED: 'Подтверждено',
  CANCELLED: 'Отменено',
  COMPLETED: 'Завершено',
} as const

// Helper to combine class names
export function cn(...classes: (string | boolean | undefined | null)[]): string {
  return classes.filter(Boolean).join(' ')
}
