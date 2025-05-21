import React from "react";
import { getBezierPath, useStore, BaseEdge, getSmoothStepPath } from "@xyflow/react";
export const getSpecialPath = ({ sourceX, sourceY, targetX, targetY }, offset) => {
    const centerX = (sourceX + targetX) / 2;
    const centerY = (sourceY + targetY) / 2;

    return `M ${sourceX} ${sourceY} Q ${centerX} ${centerY + offset} ${targetX} ${targetY}`;
};

export default function BidirectionalEdge({
    source,
    target,
    sourceX,
    sourceY,
    targetX,
    targetY,
    sourcePosition,
    targetPosition,
    markerEnd,
    sourceHandleId,
    targetHandleId,
    ...otherProps
}) {
    // console.log("🔥🔥🔥 ~ otherProps:", otherProps)
    const isBiDirectionEdge = useStore((s) => {
        const edgeExists = s.edges.some((e) => {
            if (e.target === source && e.source === target) {
                // console.log("🔥🔥🔥 ~ isBiDirectionEdge ~ e:", e)
                // console.log("🔥🔥🔥 ~ isBiDirectionEdge ~ e?.sourceHandleId:", e?.sourceHandleId)
                // console.log("🔥🔥🔥 ~ isBiDirectionEdge ~ targetHandleId:", targetHandleId)
                if (e?.sourceHandle === targetHandleId && e?.targetHandle === sourceHandleId) {
                    return true;
                }
            }
            return false;
        });

        return edgeExists;
    });

    const edgePathParams = {
        sourceX,
        sourceY,
        sourcePosition,
        targetX,
        targetY,
        targetPosition,
    };

    let path = "";

    if (isBiDirectionEdge) {
        path = getSpecialPath(edgePathParams, sourceX < targetX ? 75 : -75);
    } else {
        [path] = getSmoothStepPath(edgePathParams);
    }

    return <BaseEdge path={path} markerEnd={markerEnd} {...otherProps} />;
}
