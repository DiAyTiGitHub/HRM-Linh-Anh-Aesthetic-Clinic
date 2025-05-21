import React, { useCallback, useState } from "react";
import { useReactFlow } from "@xyflow/react";
import { useFormikContext } from "formik";
import history from "history.js";
import { Grid, Button, IconButton, TextField, List, ListItem, ListItemText, Divider } from "@material-ui/core";
import { AddRounded } from "@material-ui/icons";

import appConfig from "app/appConfig";
import { useDnD } from "../Context/DnDContext";
import DnDNode from "./DnDNode";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingOrgChartData, refreshOrgChartData, saveOrgChartData } from "../../OrganizationDiagramService";
import AddOrgChartPopup from "../Popup/AddOrgChartPopup";

import { OrganizationNodeType } from "../Nodes/OrganizationNode";
import { DepartmentNodeType } from "../Nodes/DepartmentNode";
import { PositionNodeType } from "../Nodes/PositionNode";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import { convertEdges, convertNodes, getLayoutedElements } from "../utils";
import { toast } from "react-toastify";

const listDnDObject = [
    {
        type: OrganizationNodeType.type,
        children: OrganizationNodeType.label,
        Skeleton: OrganizationNodeType.Skeleton,
    },
    {
        type: DepartmentNodeType.type,
        children: DepartmentNodeType.label,
        Skeleton: DepartmentNodeType.Skeleton,
    },
    {
        type: PositionNodeType.type,
        children: PositionNodeType.label,
        Skeleton: PositionNodeType.Skeleton,
    },
];

export const handleSaveChartData = async (orgChartData, nodes, edges, setFieldValue) => {
    var nodeJson = JSON.stringify(nodes);
    var edgesJson = JSON.stringify(edges);
    const base64Nodes = btoa(unescape(encodeURIComponent(nodeJson)));
    const base64Edges = btoa(unescape(encodeURIComponent(edgesJson)));

    const chartData = {
        ...orgChartData,
        base64Nodes,
        base64Edges,
    };

    return saveOrgChartData(chartData)
        .then(({ data }) => {
            setFieldValue("orgChartData", data);
            // const edges = data?.edges ? convertEdges(data?.edges) : [];
            // setEdges(edges);
            return data;
        })
        .catch((err) => {
            console.error(err);
        });
};

export default function Sidebar({ updateChartData, showSidebar }) {
    const [_, setType] = useDnD();
    const { getNodes, getEdges, setNodes, setEdges } = useReactFlow();
    const { values, setFieldValue } = useFormikContext();
    const [openAdd, setOpenAdd] = React.useState(false);
    const { id } = useParams();

    const onDragStart = (event, nodeType) => {
        setType(nodeType);
        event.dataTransfer.effectAllowed = "move";
    };

    const handleCancelData = () => {
        updateChartData(values?.orgChartData);
    };

    const reactFlowInstance = useReactFlow();

    const [searchTerm, setSearchTerm] = useState("");
    const [searchResults, setSearchResults] = useState([]); // Lưu danh sách node tìm thấy

    const handleReset = () => {
        setSearchTerm("");
        setSearchResults([]);
        handleRefresh(id);
    };

    // 🔍 Hàm tìm kiếm node
    const handleSearch = useCallback(() => {
        if (!searchTerm) {
            setSearchResults([]);
            return;
        }
        const nodes = getNodes();
        const results = nodes.filter((node) => {
            const nameLike = node.data?.name?.toLowerCase().includes(searchTerm.toLowerCase());
            const titleLike = node.data?.title?.toLowerCase().includes(searchTerm.toLowerCase());

            return nameLike || titleLike;
        });
        console.log(results);
        if (results.length === 1) {
            handleZoomToNode(results[0].id);
        }
        setSearchResults(results);
    }, [searchTerm, getNodes]);

    // 🔍 Hàm tìm node theo id
    const findNodeById = (id) => {
        const nodes = getNodes();
        return nodes.find((node) => node.id === id);
    };

    // 🔍 Hàm zoom đến node khi click vào
    const handleZoomToNode = (id) => {
        const node = findNodeById(id);
        if (node) {
            reactFlowInstance.setCenter(node.position.x, node.position.y, {
                zoom: 1.5,
                duration: 800,
            });
        }
    };
    const handleRefresh = (id) => {
        refreshOrgChartData(id)
            .then(({ data }) => {
                console.log(data);
                if (data) {
                    const nodes = convertNodes(data?.nodes);
                    const edges = convertEdges(data?.edges);
                    const { nodes: newNodes, edges: newEdges } = getLayoutedElements(nodes, edges, "TB");
                    setNodes(newNodes);
                    setEdges(newEdges);
                }
            })
            .catch((err) => {
                console.error(err);
                toast.error("Có lỗi xảy ra khi tải lại dữ liệu");
            });
    };

    const handleAutoRange = () => {
        const { nodes, edges } = getLayoutedElements(getNodes(), getEdges(), "TB");
        setNodes(nodes);
        setEdges(edges);
    };

    return (
        <Grid
            container
            direction='column'
            style={{
                display: showSidebar ? "flex" : "none",
                height: "100%",
                padding: "16px",
                position: "relative",
                width: "210px",
            }}>
            <Grid item>
                <div className='title'>Menu</div>
                <hr />
            </Grid>

            <Grid item>
                <Grid container direction='column' spacing={2}>
                    <Grid item>
                        <div className='description'>Kéo thả để thêm đối tượng</div>
                    </Grid>

                    <Grid item>
                        {listDnDObject.map((obj, index) => (
                            <Grid item key={index} style={{ marginBottom: "8px" }}>
                                <DnDNode Skeleton={obj.Skeleton} onDragStart={(event) => onDragStart(event, obj.type)}>
                                    {obj.children}
                                </DnDNode>
                            </Grid>
                        ))}
                    </Grid>
                </Grid>
                <hr />
            </Grid>

            <Grid item style={{ marginTop: "16px" }}>
                <GlobitsPagingAutocomplete
                    label='Sơ đồ'
                    name='orgChartData'
                    api={pagingOrgChartData}
                    onChange={(_, value) => {
                        setFieldValue("orgChartData", value);
                        if (!id || value?.id !== id) {
                            history.push(appConfig.ROOT_PATH + "organization/diagram/" + value?.id);
                        }
                    }}
                    endAdornment={
                        <IconButton
                            style={{ marginTop: "-5px" }}
                            onClick={(event) => {
                                event.preventDefault();
                                event.stopPropagation();
                                setOpenAdd(true);
                            }}>
                            <AddRounded className='text-primary' />
                        </IconButton>
                    }
                />
                {openAdd && (
                    <AddOrgChartPopup
                        open={openAdd}
                        handleClose={() => setOpenAdd(false)}
                        handleFormSubmit={async (formData) => {
                            const value = await handleSaveChartData(formData, null, null, setFieldValue);
                            if (!id || value?.id !== id) {
                                history.push(appConfig.ROOT_PATH + "organization/diagram/" + value?.id);
                            }
                            setOpenAdd(false);
                        }}
                    />
                )}
            </Grid>

            <Grid
                item
                style={{
                    margin: "8px",
                }}>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <Button
                            className='btn btn-primary full-width'
                            style={{ width: "100%" }}
                            onClick={() =>
                                handleSaveChartData(values?.orgChartData, getNodes(), getEdges(), setFieldValue)
                            }>
                            Lưu
                        </Button>
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            className='btn btn-primary full-width'
                            style={{ width: "100%" }}
                            onClick={() => handleRefresh(id)}>
                            Làm mới
                        </Button>
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            className='btn btn-primary full-width'
                            style={{ width: "100%" }}
                            onClick={() => handleAutoRange()}>
                            Sắp xếp
                        </Button>
                    </Grid>
                    <Grid item xs={12}>
                        <Button
                            className='btn btn-secondary full-width'
                            style={{ width: "100%" }}
                            onClick={() => history.goBack()}>
                            Quay lại
                        </Button>
                    </Grid>
                    {/* 🔎 Ô tìm kiếm */}
                    <TextField
                        label='Tìm kiếm node...'
                        variant='outlined'
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleSearch()} // Tìm khi nhấn Enter
                        style={{ margin: "10px 0" }}
                        fullWidth
                    />
                    {/* <Button className='btn btn-primary' onClick={handleSearch} fullWidth>
                        Tìm kiếm
                    </Button> */}

                    {/* Danh sách kết quả tìm kiếm */}
                    {searchResults.length > 0 && (
                        <List
                            component='nav'
                            style={{
                                background: "#fff",
                                border: "1px solid #ccc",
                                marginTop: "10px",
                                maxHeight: "200px",
                                width: "100%",
                                overflowY: "auto",
                            }}>
                            {searchResults.map((node, index) => (
                                <ListItem button padding={0} key={node.id} onClick={() => handleZoomToNode(node.id)}>
                                    <ListItemText primary={node.data.title} secondary={node.data.name} />
                                    {index !== searchResults.length - 1 && <Divider />}{" "}
                                    {/* Gạch chân dưới mỗi item, trừ item cuối */}
                                </ListItem>
                            ))}
                        </List>
                    )}
                </Grid>
            </Grid>
        </Grid>
    );
}
