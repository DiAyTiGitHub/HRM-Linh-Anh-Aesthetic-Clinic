import { Button, Divider, Grid, List, ListItem, ListItemText, TextField } from "@material-ui/core";
import { useReactFlow } from "@xyflow/react";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import localStorageService from "app/services/localStorageService";
import { pagingAllDepartments } from "app/views/Department/DepartmentService";
import { useFormikContext } from "formik";
import React, { useCallback, useMemo, useState } from "react";
import { useHistory } from "react-router-dom/cjs/react-router-dom.min";
import { useDnD } from "../Context/DnDContext";

export default function DepartmentSidebar({ showSidebar }) {
    const [_, setType] = useDnD();
    const { getNodes, getEdges, setNodes, setEdges } = useReactFlow();
    const { values, setFieldValue } = useFormikContext();
    const isAdmin = useMemo(() => {
        let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);
    const history = useHistory(); // Hook để điều hướng
    const reactFlowInstance = useReactFlow();
    const [searchTerm, setSearchTerm] = useState("");
    const [searchResults, setSearchResults] = useState([]);

    // 🔍 Hàm tìm kiếm node
    const handleSearch = useCallback(() => {
        if (!searchTerm) {
            setSearchResults([]);
            return;
        }
        const nodes = getNodes();
        const results = nodes.filter((node) =>
            [node.data?.name, node.data?.title].some((text) => text?.toLowerCase().includes(searchTerm.toLowerCase()))
        );
        if (results.length === 1) {
            handleZoomToNode(results[0].id);
        }
        setSearchResults(results);
    }, [searchTerm, getNodes]);

    // 🔍 Hàm tìm node theo id
    const findNodeById = (objectId) => getNodes().find((node) => node.data.objectId === objectId);

    // 🔍 Hàm zoom đến node
    const handleZoomToNode = (objectId) => {
        const node = findNodeById(objectId);
        if (node) {
            reactFlowInstance.setCenter(node.position.x, node.position.y, { zoom: 1.5, duration: 800 });
        }
    };

    // Ngăn submit form khi nhấn Enter trong TextField
    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            e.preventDefault(); // Ngăn hành vi submit mặc định
            handleSearch(); // Gọi hàm tìm kiếm
        }
    };
    // Xử lý thay đổi TextField và tìm kiếm ngay lập tức
    const handleSearchChange = (e) => {
        const value = e.target.value;
        setSearchTerm(value);
        handleSearch();
    };

    const handleBack = () => {
        history.goBack(); // Quay lại trang trước đó trong lịch sử trình duyệt
    };
    return (
        <Grid
            container
            direction='column'
            spacing={2}
            style={{
                display: showSidebar ? "block" : "none",
                height: "100%",
                padding: "16px",
                width: "210px",
                background: "white",
            }}>
            {/* Autocomplete chọn department */}
            <Grid item>
                <GlobitsPagingAutocompleteV2
                    required
                    label='Phòng ban'
                    name='department'
                    api={pagingAllDepartments}
                    style={{ width: "100%" }}
                    searchObject={{ getOwn: !isAdmin }}
                    handleChange={(_, value) => {
                        setFieldValue("department", value);
                        setFieldValue("departmentId", value?.id);
                        setFieldValue("objectId", value?.id);
                    }}
                />
            </Grid>

            {/* Input số cấp bậc */}
            <Grid item>
                <GlobitsNumberInput
                    required
                    label={"Số lượng cấp bậc hiển thị"}
                    name='numberOfLevel'
                    inputProps={{ maxLength: 12 }}
                    style={{ width: "100%" }}
                />
            </Grid>

            {/* Nút cập nhật sơ đồ */}
            <Grid item>
                <Button
                    className='btn btn-primary'
                    variant='contained'
                    color='primary'
                    type='submit'
                    style={{
                        display: "flex",
                        justifyContent: "start",
                        alignItems: "end",
                        width: "100%",
                    }}>
                    Cập nhật sơ đồ
                </Button>
            </Grid>
            <Grid item>
                <Button
                    className='btn btn-primary'
                    variant='contained'
                    color='primary'
                    onClick={handleBack}
                    style={{ width: "100%" }}>
                    Trở lại
                </Button>
            </Grid>

            {/* Ô tìm kiếm */}
            <Grid item>
                <TextField
                    label='Tìm kiếm node...'
                    variant='outlined'
                    value={searchTerm}
                    onChange={handleSearchChange} // Tìm kiếm ngay khi thay đổi
                    onKeyDown={handleKeyDown} // Sử dụng hàm xử lý mới
                    fullWidth
                />
            </Grid>

            {/* Danh sách kết quả tìm kiếm */}
            {searchResults.length > 0 && (
                <Grid item>
                    <List
                        component='nav'
                        style={{
                            background: "#fff",
                            border: "1px solid #ccc",
                            maxHeight: "200px",
                            width: "100%",
                            overflowY: "auto",
                        }}>
                        {searchResults.map((node, index) => (
                            <React.Fragment key={node.id}>
                                <ListItem button onClick={() => handleZoomToNode(node.data?.objectId)}>
                                    <ListItemText primary={node.data.title} secondary={node.data.name} />
                                </ListItem>
                                {index !== searchResults.length - 1 && <Divider />}
                            </React.Fragment>
                        ))}
                    </List>
                </Grid>
            )}
        </Grid>
    );
}
