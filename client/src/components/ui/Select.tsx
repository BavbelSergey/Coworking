import { type SelectHTMLAttributes, forwardRef } from 'react'

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  options: { value: string; label: string }[]
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ className = '', label, error, id, options, ...props }, ref) => {
    const selectId = id || label?.toLowerCase().replace(/\s+/g, '-')
    
    return (
      <div className="space-y-1.5">
        {label && (
          <label
            htmlFor={selectId}
            className="block text-sm font-medium text-[hsl(var(--foreground))]"
          >
            {label}
          </label>
        )}
        <select
          ref={ref}
          id={selectId}
          className={`flex h-10 w-full rounded-lg border border-[hsl(var(--input))] bg-[hsl(var(--background))] px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[hsl(var(--ring))] disabled:cursor-not-allowed disabled:opacity-50 ${
            error ? 'border-[hsl(var(--destructive))]' : ''
          } ${className}`}
          {...props}
        >
          {options.map(({ value, label }) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
        {error && (
          <p className="text-sm text-[hsl(var(--destructive))]">{error}</p>
        )}
      </div>
    )
  }
)

Select.displayName = 'Select'
