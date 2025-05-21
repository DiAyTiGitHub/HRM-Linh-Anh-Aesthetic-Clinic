import BidirectionalEdge from "./Edges/BidirectionalEdge";
import DepartmentNode from "./Nodes/DepartmentNode";
import OrganizationNode from "./Nodes/OrganizationNode";
import StaffNode from "./Nodes/StaffNode";
import PositionNode from "./Nodes/PositionNode";
import dagre from "dagre";
import { MarkerType } from "@xyflow/react";

export const nodeTypes = {
    staffNode: StaffNode,
    departmentNode: DepartmentNode,
    organizationNode: OrganizationNode,
    positionNode: PositionNode,
};

export const edgeTypes = {
    bidirectionalEdge: BidirectionalEdge,
};

export const EdgeTypeEnum = {
    staffNode: 0,
    departmentNode: 1,
    organizationNode: 2,
    positionNode: 3,
};

export const RelationType = {
    direct: 0, // trực tiếp
    indirect: 1, // gián tiếp
};

export // Hàm tự động sắp xếp bằng Dagre
const getLayoutedElements = (nodes, edges, direction = "TB") => {
    const dagreGraph = new dagre.graphlib.Graph();
    dagreGraph.setDefaultEdgeLabel(() => ({}));
    dagreGraph.setGraph({ rankdir: direction, nodesep: 100, ranksep: 100 });

    nodes.forEach((node) => {
        dagreGraph.setNode(node.id, { width: 150, height: 50 });
    });

    edges.forEach((edge) => {
        dagreGraph.setEdge(edge.source, edge.target);
    });

    // console.log("Dagre Graph edges:", edges); // Debug
    dagre.layout(dagreGraph);

    const layoutedNodes = nodes.map((node) => ({
        ...node,
        position: {
            x: dagreGraph.node(node.id).x - 75, // Căn giữa node theo chiều ngang
            y: dagreGraph.node(node.id).y - 25, // Căn giữa node theo chiều dọc
        },
    }));

    return { nodes: layoutedNodes, edges };
};

export const convertNodes = (nodes) => {
    if (!nodes || !Array.isArray(nodes)) return [];

    return nodes.map((node) => {
        let type = "organizationNode";
        if (node?.orgType === 0) {
            type = "staffNode";
        } else if (node?.orgType === 1) {
            type = "departmentNode";
        } else if (node?.orgType === 2) {
            type = "organizationNode";
        } else if (node?.orgType === 3) {
            type = "positionNode";
        }
        return {
            id: node.id,
            type: type,
            position: { x: node?.x || 0, y: node?.y || 0 },
            data: {
                id: node?.id,
                name: node?.name || "Vacant",
                title: node?.title || "",
                objectId: node?.objectId,
                highlight: node?.highlight
            },
        };
    });
};

export const convertEdges = (edges, direction = "TB") => {
    if (!edges || !Array.isArray(edges)) return [];

    return edges.map((edge) => {
        const isTopBottom = direction === "TB";
        return {
            id: edge?.id,
            source: edge.sourceOrgId,
            target: edge.targetOrgId,
            type: "bidirectionalEdge",
            label: edge?.label || "",
            targetHandle: isTopBottom ? "inputTop" : "inputLeft",
            sourceHandle: isTopBottom ? "inputBottom" : "inputRight",
            markerEnd: {
                type: MarkerType.ArrowClosed,
                width: 12,
                height: 12,
            },
            style: {
                strokeWidth: 2,
            },
            labelStyle: {
                fill: "red",
                fontWeight: "bold",
            },
            arrowHeadType: "arrowclosed",
            animated: edge?.relationType === 1,
        };
    });
};

export const sanitizeData = (data) => {
    console.log(data);
    return data && typeof data === "string" && data.trim() === "" ? null : data;
};
