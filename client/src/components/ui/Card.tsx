import { type HTMLAttributes, forwardRef } from 'react'

interface CardProps extends HTMLAttributes<HTMLDivElement> {}

export const Card = forwardRef<HTMLDivElement, CardProps>(
  ({ className = '', ...props }, ref) => (
    <div
      ref={ref}
      className={`rounded-xl border border-[hsl(var(--border))] bg-[hsl(var(--card))] text-[hsl(var(--card-foreground))] shadow-sm ${className}`}
      {...props}
    />
  )
)

Card.displayName = 'Card'

export const CardHeader = forwardRef<HTMLDivElement, CardProps>(
  ({ className = '', ...props }, ref) => (
    <div
      ref={ref}
      className={`flex flex-col space-y-1.5 p-6 ${className}`}
      {...props}
    />
  )
)

CardHeader.displayName = 'CardHeader'

export const CardTitle = forwardRef<HTMLHeadingElement, HTMLAttributes<HTMLHeadingElement>>(
  ({ className = '', ...props }, ref) => (
    <h3
      ref={ref}
      className={`text-lg font-semibold leading-none tracking-tight ${className}`}
      {...props}
    />
  )
)

CardTitle.displayName = 'CardTitle'

export const CardContent = forwardRef<HTMLDivElement, CardProps>(
  ({ className = '', ...props }, ref) => (
    <div ref={ref} className={`p-6 pt-0 ${className}`} {...props} />
  )
)

CardContent.displayName = 'CardContent'
