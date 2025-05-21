import { Icon, IconButton } from "@material-ui/core";
import {
    Background,
    ConnectionMode,
    Controls,
    MarkerType,
    ReactFlow,
    ReactFlowProvider,
    useEdgesState,
    useNodesState,
    useReactFlow,
} from "@xyflow/react";
import "@xyflow/react/dist/base.css";
import { Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { useCallback, useEffect, useState } from "react";
import { connect } from "react-redux";
import { toast } from "react-toastify";
import { DnDProvider, useDnD } from "./components/Context/DnDContext";
import { DepartmentNodeType } from "./components/Nodes/DepartmentNode";
import { OrganizationNodeType } from "./components/Nodes/OrganizationNode";
import { StaffNodeType } from "./components/Nodes/StaffNode";
import ContextMenuPosition from "./components/Toolbox/ContextMenuPosition";
import { handleSaveChartData } from "./components/Toolbox/Sidebar";
import {
    convertEdges,
    convertNodes,
    EdgeTypeEnum,
    edgeTypes,
    getLayoutedElements,
    nodeTypes,
    RelationType,
} from "./components/utils";
import {
    deleteOrganizationChart,
    disconnectOrgChart,
    getPositionChart,
    saveOrganizationChart,
    saveRelaytion,
} from "./OrganizationDiagramService";
import "./styles.css";
import PositionNodeForm from "./components/Popup/PositionNodeForm";
import TopBar from "./components/Toolbox/TopBar";

const initBgColor = "#e1f0fb";

const snapGrid = [100, 100];

const defaultViewport = { x: 0, y: 0, zoom: 1.2 };

const DnDFlow = observer(({ settings }) => {
    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);
    const { screenToFlowPosition, addNodes, getNode, getNodes, getEdges } = useReactFlow();
    const { setFieldValue, values } = useFormikContext();
    const [showSidebar, setShowSidebar] = React.useState(true);
    // handle layout1 menu
    const mode = settings?.layout1Settings?.leftSidebar?.mode;

    const saveChartData = () => {
        setTimeout(() => {
            let x = getEdges();
            handleSaveChartData(values?.orgChartData, getNodes(), getEdges(), setFieldValue);
        }, 300);
    };

    const handleRefresh = useCallback(async () => {
        try {
            const { data } = await getPositionChart();
            updateChartData(data);
        } catch (err) {
            console.error(err);
            toast.error("Có lỗi xảy ra, vui lòng thử lại");
        }
    }, []);

    const updateChartData = (chartData) => {
        if (chartData) {
            const nodes = convertNodes(chartData?.nodes);
            const edges = convertEdges(chartData?.edges);
            const { nodes: newNodes, edges: newEdges } = getLayoutedElements(nodes, edges, "TB");
            setNodes(newNodes);
            setEdges(newEdges);
        } else {
            setEdges([]);
            setNodes([]);
        }
    };

    useEffect(() => {
        handleRefresh();
    }, []);

    const [editData, setEditData] = useState(null);

    const [type] = useDnD();

    const onConnect = async (params) => {
        const sourceId = params?.source;
        const targetId = params?.target;
        const source = getNode(sourceId);
        const target = getNode(targetId);
        let edgeDataId = crypto.randomUUID();

        const edgeDto = {
            id: edgeDataId,
            relationType: RelationType.direct,
            sourceOrgId: source?.data?.id,
            sourceOrg: source?.data,
            targetOrg: target?.data,
            targetOrgId: target?.data?.id,
        };

        await saveRelaytion(edgeDto)
            .then(({ data }) => {
                edgeDataId = data?.id;
                edgeDto.id = edgeDataId;
                toast.success("Thêm liên kết thành công");
                handlePushNewEdgeFromServer(edgeDto);
            })
            .catch((err) => {
                console.error(err);
                toast.error("Có lỗi xảy ra, vui lòng thử lại");
                handleRefresh();
            });
    };

    const handlePushNewEdgeFromServer = (edge) => {
        const newEdge = {
            id: edge.id,
            source: edge.sourceOrgId,
            sourceHandle: "inputBottom",
            target: edge.targetOrgId,
            targetHandle: "inputTop",
            type: "bidirectionalEdge",
            markerEnd: {
                type: MarkerType.ArrowClosed,
                width: 12,
                height: 12,
            },
            label: edge.relationType,
            labelStyle: {
                fill: "red",
                fontWeight: "bold",
            },
            arrowHeadType: "arrowclosed",
            animated: edge.relationType === 1,
            relationType: edge.relationType,
        };
        setEdges((prevEdges) => [...prevEdges, newEdge]);
    };

    const onDragOver = useCallback((event) => {
        event.preventDefault();
        event.dataTransfer.dropEffect = "move";
    }, []);

    const onDrop = useCallback(
        async (event) => {
            event.preventDefault();
            let x = event.clientX;
            let y = event.clientY;
            if (!type) {
                return;
            }
            const position = screenToFlowPosition({
                x: event.clientX,
                y: event.clientY,
            });

            const data = {
                label: `${type} node`,
                orgChartDataId: null,
            };

            if (type === DepartmentNodeType.type) {
                const length = nodes?.filter((f) => f?.type === type)?.length || 0;
                data.emoji = DepartmentNodeType.emoji;
                data.name = DepartmentNodeType.label + " " + (length + 1);
            } else if (type === StaffNodeType.type) {
                const length = nodes?.filter((f) => f?.type === type)?.length || 0;
                data.emoji = StaffNodeType.emoji;
                data.name = StaffNodeType.label + " " + (length + 1);
            } else if (type === OrganizationNodeType.type) {
                const length = nodes?.filter((f) => f?.type === type)?.length || 0;
                data.emoji = OrganizationNodeType.emoji;
                data.name = OrganizationNodeType.label + " " + (length + 1);
            }

            const newNode = {
                id: crypto.randomUUID(),
                type,
                position,
                data,
            };

            if (type === StaffNodeType.type) {
                setEditData({
                    open: true,
                    data: newNode?.data,
                    node: newNode,
                    handleFormSubmit: async (values) => {
                        let dto = {
                            ...values,
                            x: x,
                            y: y,
                            orgType: EdgeTypeEnum.staffNode,
                        };
                        let error = false;
                        await saveOrganizationChart(dto)
                            .then(({ data }) => {
                                let node = data?.node;
                                if (!node?.id && node?.code && node?.description) {
                                    error = true;
                                    toast.error(node?.description);
                                    return;
                                }
                                toast.success("Lưu nhân viên thành công");
                                dto = {
                                    ...dto,
                                    ...node,
                                };
                                let edges = data?.edges;
                                if (edges?.length > 0) {
                                    handlePushNewEdgeFromServer(edges);
                                }
                            })
                            .catch((err) => {
                                console.error(err);
                                toast.error("Có lỗi xảy ra vui lòng thử lại");
                            });
                        if (error) return;
                        addNodes({ ...newNode, data: dto, id: dto?.id });
                        saveChartData();
                        setEditData(null);
                    },
                });
            }
        },
        [screenToFlowPosition, type, nodes]
    );

    const [menu, setMenu] = useState(null);
    const ref = React.useRef(null);

    const onNodeContextMenu = useCallback(
        (event, node) => {
            // Prevent native context menu from showing
            event.preventDefault();

            let widthSidebar = 0;
            if (!showSidebar) widthSidebar = 0;
            if (mode !== "full") widthSidebar += 160;
            // Calculate position of the context menu. We want to make sure it
            // doesn't get positioned off-screen.
            setMenu({
                id: node.id,
                top: event.clientY - 40,
                left: event.clientX - (240 - widthSidebar),
            });
        },
        [setMenu, showSidebar, mode]
    );

    const onEdgeContextMenu = useCallback(
        (event, edge) => {
            // console.log("🔥🔥🔥 ~ DnDFlow ~ edge:", edge)
            // Prevent native context menu from showing
            event.preventDefault();
            let widthSidebar = 0;
            if (!showSidebar) widthSidebar = 0;
            if (mode !== "full") widthSidebar += 160;

            // Calculate position of the context menu. We want to make sure it
            // doesn't get positioned off-screen.
            setMenu({
                type: "edge",
                id: edge.id,
                top: event.clientY - 40,
                left: event.clientX - (240 - widthSidebar),
            });
        },
        [setMenu, showSidebar, mode]
    );

    // Close the context menu if it's open whenever the window is clicked.
    const onPaneClick = useCallback(() => setMenu(null), [setMenu]);

    // console.log("nodes", noeds)

    const reactFlowInstance = useReactFlow();
    let objectHandle = {
        handleRefresh,
        handleSearch: (searchTerm) => {
            const nodes = getNodes();
            const foundNode = nodes.find((node) => {
                const isIncludeName = node.data?.name?.toLowerCase().includes(searchTerm.toLowerCase());
                const isIncludeTitle = node.data?.title?.toLowerCase().includes(searchTerm.toLowerCase());
                return isIncludeName || isIncludeTitle;
            });

            if (foundNode) {
                reactFlowInstance.setCenter(foundNode[0].position.x, foundNode[0].position.y, {
                    zoom: 1.5, // Zoom vào node tìm thấy
                    duration: 800, // Animation mượt
                });
            }
        },
    };

    return (
        <div className='dndflow'>
            {console.log("nodes", nodes)}
            {console.log("edges", edges)}

            <div className='reactflow-wrapper'>
                <ReactFlow
                    ref={ref}
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    style={{ background: initBgColor }}
                    nodeTypes={nodeTypes}
                    edgeTypes={edgeTypes}
                    snapToGrid={true}
                    snapGrid={snapGrid}
                    defaultViewport={defaultViewport}
                    fitView
                    attributionPosition='bottom-left'
                    onDrop={onDrop}
                    onDragOver={onDragOver}
                    onPaneClick={onPaneClick}
                    onNodeContextMenu={onNodeContextMenu}
                    onEdgeContextMenu={onEdgeContextMenu}
                    proOptions={{ hideAttribution: true }}
                    connectionMode={ConnectionMode.Loose}
                    onEdgesDelete={async (eds) => {
                        if (eds?.length > 0) {
                            for (const edge of eds) {
                                if (edge?.id) {
                                    await disconnectOrgChart(edge?.id)
                                        .then(({ data }) => {})
                                        .catch((err) => {
                                            console.error(err);
                                            // toast.error("Có lỗi xảy ra, vui lòng thử lại")
                                        });
                                }
                            }
                        }
                        saveChartData();
                    }}
                    onNodesDelete={async (nodes) => {
                        if (nodes?.length > 0) {
                            for (const node of nodes) {
                                if (node?.data?.id) {
                                    await deleteOrganizationChart(node?.data?.id)
                                        .then(({ data }) => {
                                            toast.success("Xoá thành công");
                                        })
                                        .catch((err) => {
                                            console.error(err);
                                            toast.error("Có lỗi xảy ra, vui lòng thử lại");
                                        });
                                }
                            }
                        }
                        saveChartData();
                    }}>
                    <IconButton
                        className={`p-4 !bg-[#fcfcfc] !rounded-sm 
            ${showSidebar ? "" : "!bg-[mistyrose]"}`}
                        style={{
                            position: "absolute",
                            // left: "0",
                            right: "0",
                            top: "0",
                            zIndex: "99",
                        }}
                        onClick={() => setShowSidebar((s) => !s)}
                        size='small'>
                        <Icon title='Menu'>list</Icon>
                    </IconButton>
                    <Controls showFitView style={{ top: "10px", right: "10px", position: "absolute" }} />
                    <Background />
                </ReactFlow>
            </div>
            <TopBar showSidebar={showSidebar} packetFunction={objectHandle} />
            {menu && (
                <ContextMenuPosition
                    {...menu}
                    onClick={onPaneClick}
                    setEditData={setEditData}
                    handleRefresh={handleRefresh}
                />
            )}

            {editData?.open && (
                <PositionNodeForm
                    isDisabled={true}
                    open={editData?.open}
                    handleClose={() => setEditData(null)}
                    positionId={editData?.data?.objectId}
                    handleFormSubmit={editData?.handleFormSubmit}
                />
            )}
        </div>
    );
});

const PositionDiagramIndex = ({ settings }) => {
    return (
        <Formik enableReinitialize initialValues={{}}>
            <ReactFlowProvider>
                <DnDProvider>
                    <DnDFlow settings={settings} />
                </DnDProvider>
            </ReactFlowProvider>
        </Formik>
    );
};

const mapStateToProps = (state) => ({
    settings: state.layout.settings,
});

export default connect(mapStateToProps)(PositionDiagramIndex);
