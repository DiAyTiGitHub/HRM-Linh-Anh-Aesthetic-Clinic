import { Button, List, ListItem, ListItemText, TextField, Divider } from "@material-ui/core";
import { useReactFlow } from "@xyflow/react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useFormikContext } from "formik";
import { useCallback, useState } from "react";
import { useDnD } from "../Context/DnDContext";
import PositionNode from "../Nodes/PositionNode";

export default function TopBar({ showSidebar, packetFunction }) {
    const [_, setType] = useDnD();
    const { getNodes } = useReactFlow();
    const { values, setFieldValue } = useFormikContext();
    const reactFlowInstance = useReactFlow();

    const [searchTerm, setSearchTerm] = useState("");
    const [searchResults, setSearchResults] = useState([]); // Lưu danh sách node tìm thấy

    const handleReset = () => {
        setSearchTerm("");
        setSearchResults([]);
        packetFunction.handleRefresh();
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

    return (
        <aside className='pt-4' style={{ flexDirection: "column", display: showSidebar ? "flex" : "none" }}>
            <div style={{ position: "relative" }}>
                <div className='title'>Menu</div>
                <hr />
            </div>

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
            <Button className='btn btn-primary' onClick={handleSearch} fullWidth>
                Tìm kiếm
            </Button>

            {/* Danh sách kết quả tìm kiếm */}
            {searchResults.length > 0 && (
                <List
                    component='nav'
                    style={{
                        background: "#fff",
                        border: "1px solid #ccc",
                        marginTop: "10px",
                        maxHeight: "300px",
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

            <div className='mt-4 flex flex-end gap-4'>
                <Button className='btn btn-primary flex-1' onClick={handleReset}>
                    Làm mới
                </Button>
            </div>
        </aside>
    );
}
