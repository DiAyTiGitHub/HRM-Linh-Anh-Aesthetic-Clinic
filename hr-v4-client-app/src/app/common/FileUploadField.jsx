import React, { useState, useCallback } from "react";
import {
    Button,
    Typography,
    IconButton,
    Paper,
    Tooltip,
    Box
} from "@material-ui/core";
import AttachFileIcon from "@material-ui/icons/AttachFile";
import ClearIcon from "@material-ui/icons/Clear";

const FileUploadField = ({ value = [], onFileChange, id }) => {
    const [isDragging, setIsDragging] = useState(false);

    const handleFiles = useCallback(
        (files) => {
            if (files && files.length > 0) {
                const newFiles = Array.from(files);
                onFileChange([...value, ...newFiles]);
            }
        },
        [onFileChange, value]
    );

    const handleDrop = (e) => {
        e.preventDefault();
        setIsDragging(false);
        handleFiles(e.dataTransfer.files);
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = () => {
        setIsDragging(false);
    };

    const handleChange = (event) => {
        handleFiles(event.target.files);
    };

    const handleRemove = (indexToRemove) => {
        const updatedFiles = value.filter((_, index) => index !== indexToRemove);
        onFileChange(updatedFiles);
    };

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            {/* Vùng kéo thả */}
            <Box
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                style={{
                    border: `2px dashed ${isDragging ? "#3f51b5" : "#ccc"}`,
                    padding: 16,
                    borderRadius: 8,
                    textAlign: "center",
                    backgroundColor: isDragging ? "#f0f8ff" : "#fafafa",
                    transition: "background-color 0.3s",
                    cursor: "pointer",
                }}
            >
                <Typography variant="body2" style={{ color: "#666" }}>
                    Kéo và thả file vào đây hoặc
                </Typography>

                <input
                    accept="*"
                    multiple
                    style={{ display: "none" }}
                    id={`file-upload-${id}`}
                    type="file"
                    onChange={handleChange}
                />
                <label htmlFor={`file-upload-${id}`}>
                    <Button
                        variant="outlined"
                        component="span"
                        size="small"
                        color="primary"
                        startIcon={<AttachFileIcon />}
                        style={{ marginTop: 8 }}
                    >
                        Chọn file
                    </Button>
                </label>
            </Box>

            {/* Danh sách file đã chọn */}
            {value.length > 0 && (
                <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    {value.map((file, index) => (
                        <Paper
                            key={index}
                            elevation={1}
                            style={{
                                padding: "6px 12px",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                backgroundColor: "#f9f9f9",
                            }}
                        >
                            <Typography
                                variant="body2"
                                style={{
                                    flex: 1,
                                    whiteSpace: "nowrap",
                                    overflow: "hidden",
                                    textOverflow: "ellipsis",
                                    color: "#333",
                                }}
                                title={file.name}
                            >
                                📎 {file.name}
                                <a
                                    href={URL.createObjectURL(file)}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    style={{ marginLeft: 12, fontWeight: 500, color: "#3f51b5" }}
                                >
                                    Xem
                                </a>
                            </Typography>

                            <Tooltip title="Xóa tệp">
                                <IconButton
                                    size="small"
                                    onClick={() => handleRemove(index)}
                                    style={{ color: "#f44336" }}
                                >
                                    <ClearIcon fontSize="small" />
                                </IconButton>
                            </Tooltip>
                        </Paper>
                    ))}
                </div>
            )}
        </div>
    );
};

export default FileUploadField;
