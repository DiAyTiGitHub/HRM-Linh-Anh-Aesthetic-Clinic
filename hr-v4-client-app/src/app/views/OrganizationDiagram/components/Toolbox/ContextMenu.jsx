import React, { useCallback } from "react";
import { MarkerType, useReactFlow } from "@xyflow/react";
// import { addUpdateDepartment, addUpdateStep, deleteStep, deleteStepDepartment, disconnectStepDepartment } from "../../ChartDataService";
import { toast } from "react-toastify";
import { useFormikContext } from "formik";
import { convertEdges, convertNodes, EdgeTypeEnum, getLayoutedElements, RelationType } from "../utils";
import { handleSaveChartData } from "./Sidebar";
import { DepartmentNodeType } from "../Nodes/DepartmentNode";
import {
  deleteOrganizationChart,
  disconnectOrgChart,
  linkOrgChart,
  refreshOrgChartData,
  saveOrganizationChart,
} from "../../OrganizationDiagramService";
import { observer } from "mobx-react";
import { useStore } from "app/stores";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import { PositionNodeType } from "../Nodes/PositionNode";

function ContextMenu ({
                        type = "node", // mặc định menu của node
                        id,
                        top,
                        left,
                        right,
                        bottom,
                        setEditData,
                        setViewListStaff,
                        orgCharDataId,
                        setViewListPositionTitle,
                        ... props
                      }) {
  const {values, setFieldValue} = useFormikContext ();
  const {departmentStore, positionStore} = useStore ();
  const {handleEditDepartment} = departmentStore;
  const {handleOpenCreateEdit:handleOpenPositionForm} = positionStore;

  const {getNode, setNodes, addNodes, setEdges, updateNode, getEdge, updateEdge, getNodes, getEdges} =
      useReactFlow ();

  const saveChartData = () => {
    setTimeout (() => {
      handleSaveChartData (values?.orgChartData, getNodes (), getEdges (), setFieldValue);
    }, 300);
  };

  // const duplicateNode = useCallback(() => {
  //   const node = getNode(id);
  //   const position = {
  //     x: node.position.x + 150,
  //     y: node.position.y,
  //   };

  //   addNodes({
  //     ...node,
  //     selected: false,
  //     dragging: false,
  //     id: `${node.id}-copy`,
  //     position,
  //   });
  // }, [id, getNode, addNodes]);

  const deleteNode = useCallback (() => {
    const node = getNode (id);
    if (node?.data?.id) {
      deleteOrganizationChart (node?.data?.id)
          .then (({data}) => {
            toast.success ("Xoá thành công");
          })
          .catch ((err) => {
            console.error (err);
            toast.error ("Có lỗi xảy ra, vui lòng thử lại");
          });
    }

    setNodes ((nodes) => nodes.filter ((node) => node.id !== id));
    setEdges ((edges) => edges.filter ((edge) => edge.source !== id));

    saveChartData ();
  }, [id, setNodes, setEdges]);

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

  const openPopupEdit = () => {
    const node = getNode (id);
    console.log ("🔥🔥🔥 ~ openPopupEdit ~ node", node);
    setEditData ({
      open:true,
      data:node?.data,
      node:node,
      handleFormSubmit:async (values) => {
        if (node?.type === PositionNodeType.type) {
          let dto = {
            ... values,
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
              })
              .catch ((err) => {
                console.error (err);
                toast.error ("Có lỗi xảy ra vui lòng thử lại");
              });
          if (error) return;
          updateNode (id, {... node, data:dto});
        } else if (node?.type === DepartmentNodeType.type) {
          let dto = {
            ... values,
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
              })
              .catch ((err) => {
                console.error (err);
                toast.error ("Có lỗi xảy ra vui lòng thử lại");
              });
          if (error) return;
          updateNode (id, {... node, data:dto});
        } else {
          updateNode (id, {... node, data:values});
        }
        saveChartData ();
        setEditData (null);
      },
    });
  };

  // thêm department
  const addDepartment = () => {
    const node = getNode (id);
    const nodes = getNodes ();
    const type = DepartmentNodeType.type;

    const position = {
      x:node.position.x,
      y:node.position.y + 200,
    };
    const data = {
      label:`${type} node`,
      orgChartDataId:values?.orgChartData?.id,
    };
    const length = nodes?.filter ((f) => f?.type === type)?.length || 0;
    data.emoji = DepartmentNodeType.emoji;
    data.name = DepartmentNodeType.label + " " + (length + 1);
    const newNode = {
      id:crypto.randomUUID (),
      type,
      position,
      data,
    };

    setEditData ({
      open:true,
      data:newNode?.data,
      node:newNode,
      handleFormSubmit:async (values) => {
        let dto = {
          ... values,
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
            })
            .catch ((err) => {
              console.error (err);
              toast.error ("Có lỗi xảy ra vui lòng thử lại");
            });
        if (error) return;
        addNodes ({... newNode, data:dto});

        // call hàm lưu cái relation trên db
        const edgeDto = {
          relationType:0,
          sourceOrgId:node?.data?.id,
          targetOrgId:dto?.id,
        };
        let edgeDataId = null;
        await linkOrgChart (edgeDto)
            .then (({data}) => {
              edgeDataId = data?.id;
              handleRefresh (orgCharDataId);
              // toast.success("Thêm liên kết thành công")
            })
            .catch ((err) => {
              console.error (err);
              toast.error ("Có lỗi xảy ra, vui lòng thử lại");
            });

        setEdges ((edges) => [
          ... edges,
          {
            source:id,
            sourceHandle:"inputBottom",
            target:newNode?.id,
            targetHandle:"inputTop",
            id:edgeDataId,
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
        ]);
        saveChartData ();
        setEditData (null);
      },
    });
  };

  // thêm staff
  const addPosition = () => {
    const node = getNode (id);
    const nodes = getNodes ();
    const type = PositionNodeType.type;

    const position = {
      x:node.position.x,
      y:node.position.y + 200,
    };
    const data = {
      label:`${type} node`,
      orgChartDataId:values?.orgChartData?.id,
    };
    const length = nodes?.filter ((f) => f?.type === type)?.length || 0;
    data.emoji = PositionNodeType.emoji;
    data.name = PositionNodeType.label + " " + (length + 1);
    data.stepIndex = length + 1;
    const newNode = {
      id:crypto.randomUUID (),
      type,
      position,
      data,
    };

    setEditData ({
      open:true,
      data:newNode?.data,
      node:newNode,
      handleFormSubmit:async (values) => {
        let dto = {
          ... values,
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
              toast.success ("Lưu nhân viên thành công");
              dto = {
                ... dto,
                ... node,
              };
            })
            .catch ((err) => {
              console.error (err);
              toast.error ("Có lỗi xảy ra vui lòng thử lại");
            });
        if (error) return false;
        addNodes ({... newNode, data:dto});

        // TODO call hàm lưu cái relation trên db
        const edgeDto = {
          relationType:0,
          sourceOrgId:node?.data?.id,
          targetOrgId:dto?.id,
        };
        let edgeDataId = null;
        await linkOrgChart (edgeDto)
            .then (({data}) => {
              edgeDataId = data?.id;
              handleRefresh (orgCharDataId);
              // toast.success("Thêm liên kết thành công")
            })
            .catch ((err) => {
              console.error (err);
              toast.error ("Có lỗi xảy ra, vui lòng thử lại");
            });

        setEdges ((edges) => [
          ... edges,
          {
            id:edgeDataId,
            source:id,
            sourceHandle:"inputBottom",
            target:newNode?.id,
            targetHandle:"inputTop",
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
        ]);
        saveChartData ();
        setEditData (null);
      },
    });
  };

  const deleteEdge = useCallback (() => {
    const edge = getEdge (id);
    console.log ("🔥🔥🔥 ~ deleteEdge ~ edge:", edge);
    if (edge?.id) {
      disconnectOrgChart (edge?.id)
          .then (({data}) => {
            // toast.success("Thêm liên kết thành công")

            setEdges ((edges) => edges.filter ((edge) => edge.id !== id));
            handleRefresh (orgCharDataId);
          })
          .then (() => {
            saveChartData ();
          })
          .catch ((err) => {
            console.error (err);
            toast.error ("Có lỗi xảy ra, vui lòng thử lại");
          });
    }
  }, [id, setEdges]);

  const setAnimated = async (animated) => {
    const edge = getEdge (id);
    let edgeDataId = edge?.edgeDataId;
    const source = getNode (edge?.source);
    const target = getNode (edge?.target);

    const edgeDto = {
      id:edgeDataId,
      relationType:animated? RelationType.indirect : RelationType.direct,
      sourceOrgId:source?.data?.id,
      targetOrgId:target?.data?.id,
    };
    await linkOrgChart (edgeDto)
        .then (({data}) => {
          edgeDataId = data?.id;
          handleRefresh (orgCharDataId);
          // toast.success("Thêm liên kết thành công")
        })
        .catch ((err) => {
          console.error (err);
          toast.error ("Có lỗi xảy ra, vui lòng thử lại");
        });

    saveChartData ();

    updateEdge (id, {... edge, id:edgeDataId, animated});
  };

  if (type === "edge") {
    return (
        <>
          <div style={{top, left, right, bottom}} className='context-menu' {... props}>
            <button className='p-1' onClick={() => setAnimated (false)}>
              Quản lý trực tiếp
            </button>
            <button className='p-1' onClick={() => setAnimated (true)}>
              Quản lý gián tiếp
            </button>
            <button className='p-1' onClick={deleteEdge}>
              Xoá
            </button>
          </div>
        </>
    );
  }

  const handleViewListStaff = (node) => {
    setViewListStaff ({
      open:true,
      handleClose:() => setViewListStaff (null),
      node,
    });
  };
  const handleViewListPositionTitle = (node) => {
    setViewListPositionTitle ({
      open:true,
      handleClose:() => setViewListPositionTitle (null),
      node,
    });
  };


  const node = getNode (id);
  return (
      <>
        <div style={{top, left, right, bottom}} className='context-menu' {... props}>
          {/* <button className='p-1' onClick={openPopupEdit}>
                    Cập nhật sơ đồ
                </button> */}
          {node?.type === PositionNodeType.type && node?.data?.objectId && (
              <button className='p-1' onClick={() => handleOpenPositionForm (node?.data?.objectId)}>
                Cập nhật vị trí
              </button>
          )}
          {node?.type === DepartmentNodeType.type && node?.data?.objectId && (
              <button
                  className='p-1'
                  onClick={() => {
                    handleEditDepartment (node?.data?.objectId);
                  }}>
                Cập nhật phòng ban
              </button>
          )}
          {node?.type === DepartmentNodeType.type && node?.data?.objectId && (
              <button
                  className='p-1'
                  onClick={() => {
                    handleViewListStaff (node);
                  }}>
                Danh sách vị trí
              </button>
          )}
          {node?.type === DepartmentNodeType.type && node?.data?.objectId && (
              <button
                  className='p-1'
                  onClick={() => {
                    handleViewListPositionTitle (node);
                  }}>
                Danh sách chức danh
              </button>
          )}
          <button className='p-1' onClick={addDepartment}>
            Thêm phòng ban
          </button>
          <button className='p-1' onClick={addPosition}>
            Thêm vị trí
          </button>
          <button className='p-1' onClick={deleteNode}>
            Xoá
          </button>
        </div>
      </>
  );
}

export default observer (ContextMenu);
