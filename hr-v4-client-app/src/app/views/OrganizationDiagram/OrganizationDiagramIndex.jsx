import {
  addEdge,
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
import React, { useCallback, useEffect, useState } from "react";

import { Icon, IconButton } from "@material-ui/core";
import "@xyflow/react/dist/base.css";
import appConfig from "app/appConfig";
import { useStore } from "app/stores";
import { Formik, useFormikContext } from "formik";
import history from "history.js";
import { observer } from "mobx-react";
import { connect } from "react-redux";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import { toast } from "react-toastify";
import DepartmentForm from "../Department/DepartmentForm";
import PositionCUForm from "../Position/PositionCUForm";
import { DnDProvider, useDnD } from "./components/Context/DnDContext";
import { DepartmentNodeType } from "./components/Nodes/DepartmentNode";
import { OrganizationNodeType } from "./components/Nodes/OrganizationNode";
import { PositionNodeType } from "./components/Nodes/PositionNode";
import { StaffNodeType } from "./components/Nodes/StaffNode";
import EditNodePopup from "./components/Popup/EditNodePopup";
import ViewListStaffPopup from "./components/Popup/ViewListStaffPopup";
import ContextMenu from "./components/Toolbox/ContextMenu";
import Sidebar, { handleSaveChartData } from "./components/Toolbox/Sidebar";
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
  getOrgChartData,
  linkOrgChart,
  pagingOrgChartData,
  refreshOrgChartData,
  saveOrganizationChart,
} from "./OrganizationDiagramService";
import "./styles.css";
import ViewListPositionTitlePopup from "./components/Popup/ViewListPositionTitlePopup";

const initBgColor = "#e1f0fb";

const snapGrid = [100, 100];

const defaultViewport = {x:0, y:0, zoom:1.2};

const DnDFlow = observer (({settings}) => {
  const {id} = useParams ();
  const [nodes, setNodes, onNodesChange] = useNodesState ([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState ([]);
  const {screenToFlowPosition, addNodes, getNode, getNodes, getEdges} = useReactFlow ();
  const {setFieldValue, values} = useFormikContext ();
  const [showSidebar, setShowSidebar] = React.useState (true);
  // handle layout1 menu
  const mode = settings?.layout1Settings?.leftSidebar?.mode;

  const {departmentStore} = useStore ();
  const {shouldOpenEditorDialog} = departmentStore;

  const {positionStore} = useStore ();

  const {
    openCreateEditPopup:openPositionForm,
  } = positionStore;

  const saveChartData = () => {
    setTimeout (() => {
      let x = getEdges ();
      handleSaveChartData (values?.orgChartData, getNodes (), getEdges (), setFieldValue);
    }, 300);
  };

  const updateChartData = (chartData) => {
    if (chartData) {
      const nodes = convertNodes (chartData?.nodes);
      const edges = convertEdges (chartData?.edges);
      const {nodes:newNodes, edges:newEdges} = getLayoutedElements (nodes, edges, "TB");
      setNodes (newNodes);
      setEdges (newEdges);
    } else {
      setEdges ([]);
      setNodes ([]);
    }
  };

  const extractChartData = (chartData) => {
    if (chartData) {
      const edgesString = atob (chartData.base64Edges);
      const edges = JSON.parse (edgesString);
      setEdges (edges || []);
      const nodesString = decodeURIComponent (escape (window.atob (chartData.base64Nodes)));
      const nodes = JSON.parse (nodesString);
      setNodes (nodes || []);
    } else {
      setEdges ([]);
      setNodes ([]);
    }
  };

  const handleSyncData = (id) => {
    console.log ("🔥🔥🔥 ~ handleSyncData ~ id", id);
  };

  useEffect (() => {
    if (id) {
      getOrgChartData (id)
          .then (({data}) => {
            setFieldValue ("orgChartData", data);
            // updateChartData(data);
            extractChartData (data);
          })
          .catch ((err) => {
            console.log (err);
          });
    } else {
      // neu khong co id mac dinh 1 so do dau tien trong du lieu
      pagingOrgChartData ({pageIndex:1, pageSize:1})
          .then (({data}) => {
            const defaultId = data?.content?.[0]?.id;
            if (defaultId) {
              history.push (appConfig.ROOT_PATH + "organization/diagram/" + defaultId);
            }
          })
          .catch ((err) => {
            console.error (err);
          });
    }
  }, [id]);

  const handleRefresh = (id) => {
    refreshOrgChartData (id)
        .then (({data}) => {
          console.log (data);
          if (data) {
            const nodes = convertNodes (data?.nodes);
            const edges = convertEdges (data?.edges);
            const {nodes:newNodes, edges:newEdges} = getLayoutedElements (nodes, edges, "TB");
            setNodes (newNodes);
            setEdges (newEdges);
          }
        })
        .catch ((err) => {
          console.error (err);
          toast.error ("Có lỗi xảy ra khi tải lại dữ liệu");
        });
  };

  const [editData, setEditData] = useState (null);
  const [viewListStaff, setViewListStaff] = useState (null);
  const [viewListPositionTitle, setViewListPositionTitle] = useState (null);

  const [type] = useDnD ();

  const onConnect = async (params) => {
    const sourceId = params?.source;
    const targetId = params?.target;
    const source = getNode (sourceId);
    const target = getNode (targetId);

    const edgeDto = {
      relationType:RelationType.direct,
      sourceOrgId:source?.data?.id,
      targetOrgId:target?.data?.id,
    };
    let edgeDataId = null;
    await linkOrgChart (edgeDto)
        .then (({data}) => {
          edgeDataId = data?.id;
          // toast.success("Thêm liên kết thành công")
        })
        .then (() => {
          handleRefresh (id);
        })
        .then (() => {
          saveChartData ();
        })
        .catch ((err) => {
          console.error (err);
          toast.error ("Có lỗi xảy ra, vui lòng thử lại");
        });

    return setEdges ((eds) => {
      return addEdge (
          {
            ... params,
            id:edgeDataId, // lưu id server để call khi xoá
            type:"bidirectionalEdge",
            markerEnd:{
              type:MarkerType.ArrowClosed,
              width:12,
              height:12,
            },
            style:{
              strokeWidth:2,
            },
          },
          eds
      );
    });
  };

  function createEdges (edgeList) {
    // Kiểm tra nếu danh sách null hoặc không phải là một mảng
    if (!Array.isArray (edgeList) || edgeList.length === 0) {
      toast.warn ("Danh sách edges rỗng hoặc không hợp lệ.");
      return [];
    }

    // Duyệt qua danh sách và tạo danh sách edges hợp lệ
    const validEdges = edgeList
        .filter ((edge) => edge && edge.sourceOrg && edge.targetOrg && edge.sourceOrg.id && edge.targetOrg.id)
        .map ((edge) => ({
          id:edge.id,
          sourceId:edge.sourceOrg.id,
          targetId:edge.targetOrg.id,
          relationType:edge.relationType,
          animated:edge?.relationType === 0,
        }));

    if (validEdges.length === 0) {
      console.warn ("Không có edges hợp lệ sau khi lọc.");
    }

    return validEdges;
  }

  const handlePushNewEdgeFromServer = (edgeList) => {
    const listNewEdges = convertEdges (edgeList);

    setEdges ((eds) => {
      return [... listNewEdges];
    });
  };

  const onDragOver = useCallback ((event) => {
    event.preventDefault ();
    event.dataTransfer.dropEffect = "move";
  }, []);

  const onDrop = useCallback (
      async (event) => {
        event.preventDefault ();
        let x = event.clientX;
        let y = event.clientY;
        if (!type) {
          return;
        }

        if (!id) {
          toast.error ("Cần chọn sơ đồ trước khi thêm đối tượng");
          return;
        }

        const position = screenToFlowPosition ({
          x:event.clientX,
          y:event.clientY,
        });

        const data = {
          label:`${type} node`,
          orgChartDataId:id,
        };

        if (type === DepartmentNodeType.type) {
          const length = nodes?.filter ((f) => f?.type === type)?.length || 0;
          data.emoji = DepartmentNodeType.emoji;
          data.name = DepartmentNodeType.label + " " + (length + 1);
        } else if (type === StaffNodeType.type) {
          const length = nodes?.filter ((f) => f?.type === type)?.length || 0;
          data.emoji = StaffNodeType.emoji;
          data.name = StaffNodeType.label + " " + (length + 1);
        } else if (type === OrganizationNodeType.type) {
          const length = nodes?.filter ((f) => f?.type === type)?.length || 0;
          data.emoji = OrganizationNodeType.emoji;
          data.name = OrganizationNodeType.label + " " + (length + 1);
        } else if (type === PositionNodeType.type) {
          const length = nodes?.filter ((f) => f?.type === type)?.length || 0;
          data.emoji = PositionNodeType.emoji;
          data.name = PositionNodeType.label + " " + (length + 1);
        }

        const newNode = {
          id:crypto.randomUUID (),
          type,
          position,
          data,
        };
        if (type === StaffNodeType.type) {
          setEditData ({
            open:true,
            data:newNode?.data,
            node:newNode,
            handleFormSubmit:async (values) => {
              let dto = {
                ... values,
                x:x,
                y:y,
                orgType:EdgeTypeEnum.staffNode,
              };
              let error = false;
              await saveOrganizationChart (dto)
                  .then (({data}) => {
                    let node = data?.node;
                    if (!node?.id && node?.code && node?.description) {
                      error = true;
                      toast.error (node?.description);
                      return;
                    }
                    toast.success ("Lưu nhân viên thành công");
                    dto = {
                      ... dto,
                      ... node,
                    };
                    let edges = data?.edges;
                    if (edges?.length > 0) {
                      handlePushNewEdgeFromServer (edges);
                    }
                  })
                  .catch ((err) => {
                    console.error (err);
                    toast.error ("Có lỗi xảy ra vui lòng thử lại");
                  });
              if (error) return;
              addNodes ({... newNode, data:dto, id:dto?.id});
              saveChartData ();
              setEditData (null);
            },
          });
        } else if (type === DepartmentNodeType.type) {
          setEditData ({
            open:true,
            data:newNode?.data,
            node:newNode,
            handleFormSubmit:async (values) => {
              let dto = {
                ... values,
                x:x,
                y:y,
                orgType:EdgeTypeEnum.departmentNode,
              };
              let error = false;
              await saveOrganizationChart (dto)
                  .then (({data}) => {
                    let node = data?.node;
                    if (!node?.id && node?.code && node?.description) {
                      error = true;
                      toast.error (node?.description);
                      return;
                    }
                    toast.success ("Lưu phòng ban thành công");
                    dto = {
                      ... dto,
                      ... node,
                    };

                    let edges = data?.edges;
                    if (edges?.length > 0) {
                      handlePushNewEdgeFromServer (edges);
                    }
                  })
                  .catch ((err) => {
                    console.error (err);
                    toast.error ("Có lỗi xảy ra vui lòng thử lại");
                  });
              if (error) return;
              addNodes ({... newNode, data:dto, id:dto?.id});
              saveChartData ();
              setEditData (null);
            },
          });
        } else if (type === OrganizationNodeType.type) {
          setEditData ({
            open:true,
            data:newNode?.data,
            node:newNode,
            handleFormSubmit:async (values) => {
              let dto = {
                ... values,
                x:x,
                y:y,
                orgType:EdgeTypeEnum.organizationNode,
              };
              let error = false;
              await saveOrganizationChart (dto)
                  .then (({data}) => {
                    let node = data?.node;
                    if (!node?.id && node?.code && node?.description) {
                      error = true;
                      toast.error (node?.description);
                      return;
                    }
                    toast.success ("Lưu đơn vị thành công");
                    dto = {
                      ... dto,
                      ... node,
                    };

                    let edges = data?.edges;
                    if (edges?.length > 0) {
                      handlePushNewEdgeFromServer (edges);
                    }
                  })
                  .catch ((err) => {
                    console.error (err);
                    toast.error ("Có lỗi xảy ra vui lòng thử lại");
                  });
              if (error) return;
              addNodes ({... newNode, data:dto, id:dto?.id});
              saveChartData ();
              setEditData (null);
            },
          });
        } else if (type === PositionNodeType.type) {
          setEditData ({
            open:true,
            data:newNode?.data,
            node:newNode,
            handleFormSubmit:async (values) => {
              let dto = {
                ... values,
                x:x,
                y:y,
                orgType:EdgeTypeEnum.positionNode,
              };
              let error = false;
              await saveOrganizationChart (dto)
                  .then (({data}) => {
                    let node = data?.node;
                    if (!node?.id && node?.code && node?.description) {
                      error = true;
                      toast.error (node?.description);
                      return;
                    }
                    toast.success ("Lưu Vị trí thành công");
                    dto = {
                      ... dto,
                      ... node,
                    };
                    let edges = data?.edges;
                    if (edges?.length > 0) {
                      handlePushNewEdgeFromServer (edges);
                    }
                  })
                  .catch ((err) => {
                    console.error (err);
                    toast.error ("Có lỗi xảy ra vui lòng thử lại");
                  });
              if (error) return;
              addNodes ({... newNode, data:dto, id:dto?.id});
              saveChartData ();
              setEditData (null);
            },
          });
        } else {
          setNodes ((nds) => nds.concat (newNode));
        }
      },
      [screenToFlowPosition, type, nodes, id]
  );

  const [menu, setMenu] = useState (null);
  const ref = React.useRef (null);

  const onNodeContextMenu = useCallback (
      (event, node) => {
        // Prevent native context menu from showing
        event.preventDefault ();

        let widthSidebar = 0;
        if (!showSidebar) widthSidebar = 0;
        if (mode !== "full") widthSidebar += 160;
        // Calculate position of the context menu. We want to make sure it
        // doesn't get positioned off-screen.
        const pane = ref.current.getBoundingClientRect ();
        setMenu ({
          id:node.id,
          top:event.clientY - 40,
          left:event.clientX - (240 - widthSidebar),
        });
      },
      [setMenu, showSidebar, mode]
  );

  const onEdgeContextMenu = useCallback (
      (event, edge) => {
        // console.log("🔥🔥🔥 ~ DnDFlow ~ edge:", edge)
        // Prevent native context menu from showing
        event.preventDefault ();
        let widthSidebar = 0;
        if (!showSidebar) widthSidebar = 0;
        if (mode !== "full") widthSidebar += 160;

        // Calculate position of the context menu. We want to make sure it
        // doesn't get positioned off-screen.
        setMenu ({
          type:"edge",
          id:edge.id,
          top:event.clientY - 40,
          left:event.clientX - (240 - widthSidebar),
        });
      },
      [setMenu, showSidebar, mode]
  );

  // Close the context menu if it's open whenever the window is clicked.
  const onPaneClick = useCallback (() => setMenu (null), [setMenu]);

  // console.log("nodes", noeds)

  return (
      <div className='dndflow'>
        <div className='reactflow-wrapper'>
          <ReactFlow
              ref={ref}
              nodes={nodes}
              edges={edges}
              onNodesChange={onNodesChange}
              onEdgesChange={onEdgesChange}
              onConnect={onConnect}
              style={{background:initBgColor}}
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
              proOptions={{hideAttribution:true}}
              connectionMode={ConnectionMode.Loose}
              onEdgesDelete={async (eds) => {
                if (eds?.length > 0) {
                  for (const edge of eds) {
                    if (edge?.id) {
                      await disconnectOrgChart (edge?.id)
                          .then (({data}) => {
                          })
                          .catch ((err) => {
                            console.error (err);
                            // toast.error("Có lỗi xảy ra, vui lòng thử lại")
                          });
                    }
                  }
                }
                saveChartData ();
              }}
              onNodesDelete={async (nodes) => {
                if (nodes?.length > 0) {
                  for (const node of nodes) {
                    if (node?.data?.id) {
                      await deleteOrganizationChart (node?.data?.id)
                          .then (({data}) => {
                            toast.success ("Xoá thành công");
                          })
                          .catch ((err) => {
                            console.error (err);
                            toast.error ("Có lỗi xảy ra, vui lòng thử lại");
                          });
                    }
                  }
                }
                saveChartData ();
              }}>
            <IconButton
                className={`p-4 !bg-[#fcfcfc] !rounded-sm 
                        ${showSidebar? "" : "!bg-[mistyrose]"}`}
                style={{
                  position:"absolute",
                  // left: "0",
                  right:"0",
                  top:"0",
                  zIndex:"99",
                }}
                onClick={() => setShowSidebar ((s) => !s)}
                size='small'>
              <Icon title='Menu'>list</Icon>
            </IconButton>
            {/* <Controls /> */}
            <Background/>

            {menu && (
                <ContextMenu
                    {... menu}
                    orgCharDataId={id}
                    onClick={onPaneClick}
                    setEditData={setEditData}
                    setViewListStaff={setViewListStaff}
                    setViewListPositionTitle={setViewListPositionTitle}
                />
            )}

            {editData?.open && (
                <EditNodePopup
                    open={editData?.open}
                    handleClose={() => setEditData (null)}
                    data={editData?.data}
                    node={editData?.node}
                    handleFormSubmit={editData?.handleFormSubmit}
                />
            )}

            {/* Chỉnh sửa phòng ban */}
            {shouldOpenEditorDialog && <DepartmentForm open={shouldOpenEditorDialog}/>}

            {openPositionForm && <PositionCUForm/>}
            {/* Xem ds nhân viên */}
            {viewListStaff?.open && <ViewListStaffPopup {... viewListStaff} />}
            {viewListPositionTitle?.open && <ViewListPositionTitlePopup{... viewListPositionTitle} />}
            <Controls showFitView/>
          </ReactFlow>
        </div>

        <Sidebar
            updateChartData={updateChartData}
            showSidebar={showSidebar}
            setShowSidebar={setShowSidebar}
            handleSyncData={handleSyncData}
        />
      </div>
  );
});

const OrganizationDiagramIndex = ({settings}) => {
  return (
      <Formik enableReinitialize initialValues={{}}>
        <ReactFlowProvider>
          <DnDProvider>
            <DnDFlow settings={settings}/>
          </DnDProvider>
        </ReactFlowProvider>
      </Formik>
  );
};

const mapStateToProps = (state) => ({
  settings:state.layout.settings,
});

export default connect (mapStateToProps) (OrganizationDiagramIndex);
