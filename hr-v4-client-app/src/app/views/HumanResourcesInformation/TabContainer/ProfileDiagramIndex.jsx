import {Icon, IconButton} from "@material-ui/core";
import {
    Background,
    ConnectionMode,
    Controls,
    ReactFlow,
    ReactFlowProvider,
    useEdgesState,
    useNodesState,
    useReactFlow,
} from "@xyflow/react";
import "@xyflow/react/dist/base.css";
import {Formik, useFormikContext} from "formik";
import {observer} from "mobx-react";
import React, {useCallback, useEffect, useState} from "react";
import {connect} from "react-redux";
import {DnDProvider} from "app/views/OrganizationDiagram/components/Context/DnDContext";
import {
    convertEdges,
    convertNodes,
    edgeTypes,
    getLayoutedElements,
    nodeTypes,
} from "app/views/OrganizationDiagram/components/utils";
import "app/views/profile/styles.css";
const initBgColor = "#e1f0fb";

const snapGrid = [100, 100];

const defaultViewport = {x: 0, y: 0, zoom: 1.2};

const DnDFlow = observer(({settings, orgChartData}) => {
    // const { id } = useParams();
    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);
    const {screenToFlowPosition, addNodes, getNode, getNodes, getEdges} = useReactFlow();
    const [showSidebar, setShowSidebar] = React.useState(true);

    const {values, setFieldValue, isSubmitting} = useFormikContext();
    // handle layout1 menu
    const mode = settings?.layout1Settings?.leftSidebar?.mode;

    const updateChartData = (chartData) => {
        if (chartData) {
            const nodes = convertNodes(chartData?.nodes);
            const edges = convertEdges(chartData?.edges);
            const {nodes: newNodes, edges: newEdges} = getLayoutedElements(nodes, edges, "TB");
            setNodes(newNodes);
            setEdges(newEdges);
        } else {
            setEdges([]);
            setNodes([]);
        }
    };


    useEffect(() => {
        updateChartData(orgChartData);
    }, [orgChartData]);

    const onDragOver = useCallback((event) => {
        event.preventDefault();
        event.dataTransfer.dropEffect = "move";
    }, []);

    const reactFlowInstance = useReactFlow();
    const [menu, setMenu] = useState(null);
    const ref = React.useRef(null);

    // 🔍 Hàm tìm node theo id
    const findNode = () => {
        console.log(values);
        const nodes = getNodes();
        const currentPositionId = values?.position?.id;
        return nodes.find((node) => node?.data?.objectId === currentPositionId);
    };

    // 🔍 Hàm zoom đến node khi click vào
    const handleZoomToNode = () => {
        const node = findNode();
        if (node) {
            reactFlowInstance.setCenter(node.position.x, node.position.y, {
                zoom: 1.5,
                duration: 800,
            });
        }
    };

    const onNodeContextMenu = useCallback(
        (event, node) => {
            // Prevent native context menu from showing
            event.preventDefault();

            let widthSidebar = 0;
            if (!showSidebar) widthSidebar = 0;
            if (mode !== "full") widthSidebar += 160;
            // Calculate position of the context menu. We want to make sure it
            // doesn't get positioned off-screen.
            const pane = ref.current.getBoundingClientRect();
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

    return (
        <div className='dndflow' style={{height: "430px"}}>
            <div className='reactflow-wrapper'>
                <ReactFlow
                    ref={ref}
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    // onConnect={onConnect}
                    style={{background: initBgColor}}
                    nodeTypes={nodeTypes}
                    edgeTypes={edgeTypes}
                    snapToGrid={true}
                    snapGrid={snapGrid}
                    defaultViewport={defaultViewport}
                    fitView
                    attributionPosition='bottom-left'
                    // onDrop={onDrop}
                    onDragOver={onDragOver}
                    onPaneClick={onPaneClick}
                    onNodeContextMenu={onNodeContextMenu}
                    onEdgeContextMenu={onEdgeContextMenu}
                    proOptions={{hideAttribution: true}}
                    connectionMode={ConnectionMode.Loose}>
                    <IconButton
                        className={`p-4 !bg-[#fcfcfc] !rounded-sm 
                        ${showSidebar ? "" : "!bg-[mistyrose]"}`}
                        style={{
                            position: "absolute",
                            left: "0",
                            // right: "0",
                            top: "0",
                            zIndex: "99",
                        }}
                        onClick={() => handleZoomToNode()}
                        size='small'>
                        <Icon title='Menu'>search</Icon>
                        Vị trí
                    </IconButton>
                    {/* <Controls /> */}
                    <Background/>
                    <Controls showFitView/>
                </ReactFlow>
            </div>
        </div>
    );
});

const ProfileDiagramIndex = ({settings, orgChartData}) => {
    return (
        <ReactFlowProvider>
            <DnDProvider>
                <DnDFlow settings={settings} orgChartData={orgChartData}/>
            </DnDProvider>
        </ReactFlowProvider>
    );
};

const mapStateToProps = (state) => ({
    settings: state.layout.settings,
});

export default connect(mapStateToProps)(ProfileDiagramIndex);
