import React from 'react'

export default function DnDNode({
  className,
  onDragStart,
  children,
  Skeleton,
  ...otherProps
}) {
  if (Skeleton) {
    return (
      <Skeleton
        onDragStart={onDragStart} 
        draggable
        {...otherProps}
      >
        {children}
      </Skeleton>
    )
  }
  return (
    <div 
      className={className}
      onDragStart={onDragStart} 
      draggable
      {...otherProps}
    >
      {children}
    </div>
  )
}
