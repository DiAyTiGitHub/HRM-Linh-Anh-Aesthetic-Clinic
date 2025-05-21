import React from 'react'
import { Handle, Position } from "@xyflow/react";

export const StaffNodeSkeleton = ({children, ...otherProps}) => {
  return (
    <div className="px-2 py-1 bg-white shadow-md rounded-md border-2 border-orange-400" {...otherProps}>
      <h5 className="m-0 text-lg font-bold text-center">{children}</h5>
    </div>
  )
}

export const StaffNodeType = {
  type: "staffNode",
  emoji: "🚹",
  Skeleton: StaffNodeSkeleton,
  label: "Nhân viên",
}

// const 

export default function StaffNode({ data, isConnectable }) {

  return (
    <div className="px-12 py-8 shadow-md rounded-md bg-white border-2 border-stone-400 max-w-56">
      <div className="flex flex-middle">
        <div className="rounded-full w-12 h-12 flex justify-center items-center bg-gray-100">
          🚹
        </div>
        <div className="ml-4">
          <h5 className="m-0 text-md font-bold">{data.name}</h5>
          <div className='text-gray-500 text-xs'>
            <span>Vị trí: </span>
            <strong>{data?.title}</strong>
          </div>
          {/* <div className='text-gray-500 text-xs'>
            <span>Mô tả: </span>
            <strong>{data?.comments}</strong>
          </div> */}
        </div>
      </div>

      <Handle
        type="source"
        position={Position.Top}
        className="w-16 !bg-orange-500"
        id={"inputTop"}
      />
      <Handle
        type="source"
        position={Position.Bottom}
        className="w-16 !bg-orange-500"
        id={"inputBottom"}
      />
      <Handle
        type="source"
        position={Position.Left}
        className="h-8 !bg-orange-500"
        id={"inputLeft"}
      />
      <Handle
        type="source"
        position={Position.Right}
        className="h-8 !bg-orange-500"
        id={"inputRight"}
      />
    </div>
  )
}
