import React, { useCallback, useState } from "react";
import { useReactFlow } from "@xyflow/react";
import { StaffNodeType } from "../Nodes/StaffNode";
import { toast } from "react-toastify";
import { useFormikContext } from "formik";
import { EdgeTypeEnum, RelationType } from "../utils";
import { handleSaveChartData } from "./Sidebar";
import { DepartmentNodeType } from "../Nodes/DepartmentNode";
import {
    deleteOrganizationChart,
    deleteRelaytion,
    saveOrganizationChart,
    saveRelaytion,
} from "../../OrganizationDiagramService";
import { observer } from "mobx-react";
import { useStore } from "app/stores";
import { Dialog, DialogActions, DialogContent, DialogTitle, Button, TextField } from "@material-ui/core";
import PositionNodeForm from "../Popup/PositionNodeForm";
import PositionNode, { PositionNodeType } from "../Nodes/PositionNode";

function ContextMenuPosition({
    type = "node", // mặc định menu của node
    id,
    top,
    left,
    right,
    bottom,
    setEditData,
    setViewListStaff,
    handleRange,
    handleRefresh,
    ...props
}) {
    const { values, setFieldValue } = useFormikContext();
    const { departmentStore } = useStore();
    const { handleEditDepartment } = departmentStore;

    const { getNode, setNodes, addNodes, setEdges, updateNode, getEdge, updateEdge, getNodes, getEdges } =
        useReactFlow();

    const [openAppointDialog, setOpenAppointDialog] = useState(false);
    const [positionId, setPositionId] = useState("");

    const saveChartData = () => {
        setTimeout(() => {
            handleSaveChartData(values?.orgChartData, getNodes(), getEdges(), setFieldValue);
        }, 300);
    };

    const hanldeClosePopup = () => {
        setPositionId("");
        setOpenAppointDialog(false);
    };

    const deleteNode = useCallback(() => {
        const node = getNode(id);
        setNodes((nodes) => nodes.filter((node) => node.id !== id));
        setEdges((edges) => edges.filter((edge) => edge.source !== id));
    }, [id, setNodes, setEdges]);

    const openPopupEdit = () => {
        const node = getNode(id);
        console.log("🔥🔥🔥 ~ openPopupEdit ~ node", node);
        setEditData({
            open: true,
            data: node?.data,
            node: node,
            handleFormSubmit: async (values) => {
                if (node?.type === PositionNodeType.type) {
                    const positionId = node?.data?.objectId;
                    console.log("🔥🔥🔥 ~ openPopupEdit ~ positionId", positionId);

                    let dto = {
                        id: values?.id,
                        name: values?.staff?.displayName || "Vacant",
                        title: values?.title?.name,
                        objectId: positionId,
                    };

                    updateNode(id, { ...node, data: { ...dto } });
                } else if (node?.type === StaffNodeType.type) {
                    let dto = {
                        ...values,
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
                        })
                        .catch((err) => {
                            console.error(err);
                            toast.error("Có lỗi xảy ra vui lòng thử lại");
                        });
                    if (error) return;
                    updateNode(id, { ...node, data: dto });
                } else if (node?.type === DepartmentNodeType.type) {
                    let dto = {
                        ...values,
                        orgType: EdgeTypeEnum.departmentNode,
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
                            toast.success("Lưu phòng ban thành công");
                            dto = {
                                ...dto,
                                ...node,
                            };
                        })
                        .catch((err) => {
                            console.error(err);
                            toast.error("Có lỗi xảy ra vui lòng thử lại");
                        });
                    if (error) return;
                    updateNode(id, { ...node, data: dto });
                } else {
                    updateNode(id, { ...node, data: values });
                }
            },
        });
    };

    const handleAppoint = () => {
        const node = getNode(id);
        console.log("🔥🔥🔥 ~ handleAppoint ~ node", node);
        if (!node) {
            toast.error("Không tìm thấy node");
            return;
        }
        setPositionId(node?.data?.objectId);
        setOpenAppointDialog(true);
    };

    const deleteEdge = useCallback(() => {
        const edge = getEdge(id);

        const sourceId = edge?.source;
        const targetId = edge?.target;
        const source = getNode(sourceId);
        const target = getNode(targetId);

        const edgeDto = {
            relationType: RelationType.direct,
            sourceOrgId: source?.data?.id,
            sourceOrg: source?.data,
            targetOrg: target?.data,
            targetOrgId: target?.data?.id,
        };

        deleteRelaytion(edgeDto)
            .then(({ data }) => {
                setEdges((edges) => edges.filter((edge) => edge.id !== id));
            })
            .catch((err) => {
                if (typeof handleRefresh === "function") {
                    handleRefresh();
                }
                toast.error("Có lỗi xảy ra, vui lòng thử lại");
            });
    }, [id, setEdges]);

    const setAnimated = async (animated) => {
        const edge = getEdge(id);

        const sourceId = edge?.source;
        const targetId = edge?.target;
        const source = getNode(sourceId);
        const target = getNode(targetId);

        const edgeDto = {
            relationType: animated ? RelationType.indirect : RelationType.direct,
            sourceOrgId: source?.data?.id,
            sourceOrg: source?.data,
            targetOrg: target?.data,
            targetOrgId: target?.data?.id,
        };
        let edgeDataId = null;
        await saveRelaytion(edgeDto)
            .then(({ data }) => {
                edgeDataId = data?.id;
                toast.success("Thêm liên kết thành công");
            })
            .catch((err) => {
                console.error(err);
                toast.error("Có lỗi xảy ra, vui lòng thử lại");
                if (typeof handleRefresh === "function") {
                    handleRefresh();
                }
            });

        updateEdge(id, { ...edge, id: id, animated });
    };

    return (
        <>
            {type === "edge" ? (
                <div style={{ top, left, right, bottom }} className='context-menu' {...props}>
                    <button className='p-1' style={{ padding: "10px" }} onClick={() => setAnimated(false)}>
                        Quản lý trực tiếp
                    </button>
                    <button className='p-1' style={{ padding: "10px" }} onClick={() => setAnimated(true)}>
                        Quản lý gián tiếp
                    </button>
                    <button className='p-1' style={{ padding: "10px" }} onClick={deleteEdge}>
                        Xóa
                    </button>
                </div>
            ) : (
                <div style={{ top, left, right, bottom }} className='context-menu' {...props}>
                    <button onClick={openPopupEdit} style={{ padding: "10px" }}>
                        Bổ nhiệm
                    </button>
                    <button onClick={handleRefresh} style={{ padding: "10px" }}>
                        Làm mới
                    </button>
                    <button onClick={deleteNode} style={{ padding: "10px" }}>
                        Ẩn
                    </button>
                </div>
            )}
            <PositionNodeForm
                isDisabled={false}
                open={openAppointDialog}
                hanldeClosePopup={hanldeClosePopup}
                positionId={positionId}
                handleRefresh={handleRefresh}
            />
        </>
    );
}

export default observer(ContextMenuPosition);
