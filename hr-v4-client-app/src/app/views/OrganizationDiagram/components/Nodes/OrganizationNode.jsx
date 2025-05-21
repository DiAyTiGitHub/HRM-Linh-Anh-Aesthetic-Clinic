import React from 'react'
import { Handle, Position } from "@xyflow/react";

export const OrganizationNodeSkeleton = ({children, ...otherProps}) => {
  return (
    <div className="px-2 py-1 bg-white shadow-md rounded-md border-2 border-rose-700" {...otherProps}>
      <h5 className="m-0 text-lg font-bold text-center">{children}</h5>
    </div>
  )
}

export const OrganizationNodeType = {
  type: "organizationNode",
  emoji: "🏢",
  Skeleton: OrganizationNodeSkeleton,
  label: "Đơn vị",
}

// const 

export default function OrganizationNode({ data, isConnectable }) {

  return (
    <div className="px-12 py-8 bg-white shadow-md rounded-md border-2 border-rose-400 max-w-56">
      <h5 className="m-0 text-md font-bold">{data.name}</h5>
      <div className="flex">
        {/* <div className="flex justify-center items-center">
          {OrganizationNodeType.emoji}
        </div> */}
        <div className="ml-2">
          {/* <div className='text-gray-500 text-xs'>
            <span>Mô tả: </span>
            <strong>{data?.comments}</strong>
          </div> */}
          {/* <div className='text-gray-500 text-xs'>
            <span>Trạng thái: </span>
            <strong>{data?.status}</strong>
          </div> */}
        </div>
      </div>

      <Handle
        type="source"
        position={Position.Top}
        className="w-16 !bg-rose-700"
        id={"inputTop"}
      />
      <Handle
        type="source"
        position={Position.Bottom}
        className="w-16 !bg-rose-700"
        id={"inputBottom"}
      />
      <Handle
        type="source"
        position={Position.Left}
        className="h-8 !bg-rose-700"
        id={"inputLeft"}
      />
      <Handle
        type="source"
        position={Position.Right}
        className="h-8 !bg-rose-700"
        id={"inputRight"}
      />
    </div>
  )
}
