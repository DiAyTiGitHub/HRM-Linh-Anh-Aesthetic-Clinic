import React from "react";
import { Handle, Position } from "@xyflow/react";

export const PositionNodeSkeleton = ({ children, ...otherProps }) => {
    return (
        <div className='px-2 py-1 bg-white shadow-md rounded-md border-2 border-orange-400' {...otherProps}>
            <h5 className='m-0 text-lg font-bold text-center'>{children}</h5>
        </div>
    );
};

export const PositionNodeType = {
    type: "positionNode",
    emoji: "🚹",
    Skeleton: PositionNodeSkeleton,
    label: "Vị trí",
};

// CSS tùy chỉnh cho hiệu ứng viền chạy xung quanh
const blinkingBorderStyle = `
    .blinking-border {
        position: relative;
        border: 2px solid #f97316; /* orange-500 */
        border-radius: 10px;
        animation: blinkBorder 1s infinite;
    }

    @keyframes blinkBorder {
        0%, 100% {
            border-color: #f97316;
            box-shadow: 0 0 0 0 rgba(249, 115, 22, 0.3);
        }
        50% {
            border-color: #fff;
            box-shadow: 0 0 10px 4px rgba(249, 115, 22, 0.5);
        }
    }
`;



// Thêm style vào document
const styleSheet = document.createElement("style");
styleSheet.textContent = blinkingBorderStyle;
document.head.appendChild(styleSheet);

export default function PositionNode({ data, isConnectable = true }) {
    // Style cơ bản
    const baseStyle = "px-12 py-8 shadow-md rounded-md bg-white max-w-56";
    // Chọn className dựa trên data.highlight
    const nodeClass = data?.highlight
        ? `${baseStyle} blinking-border` // Áp dụng hiệu ứng viền chạy xung quanh
        : `${baseStyle} border-2 border-red-400`; // Style mặc định

    return (
        <div className={nodeClass}>
            <div className="flex items-center">
                {isConnectable && (
                    <div className="rounded-full w-12 h-12 flex justify-center items-center bg-gray-100">
                        🚹
                    </div>
                )}
                <div className="ml-4">
                    <h5 className="m-0 text-md font-bold">{data?.title}</h5>
                    <div className="text-gray-500 text-xs">
                        <strong>{data?.name}</strong>
                    </div>
                </div>
            </div>

            {isConnectable && (
                <>
                    <Handle
                        type="source"
                        position={Position.Top}
                        className="w-16 !bg-orange-500"
                        id="inputTop"
                    />
                    <Handle
                        type="source"
                        position={Position.Bottom}
                        className="w-16 !bg-orange-500"
                        id="inputBottom"
                    />
                    <Handle
                        type="source"
                        position={Position.Left}
                        className="h-8 !bg-orange-500"
                        id="inputLeft"
                    />
                    <Handle
                        type="source"
                        position={Position.Right}
                        className="h-8 !bg-orange-500"
                        id="inputRight"
                    />
                </>
            )}
        </div>
    );
}