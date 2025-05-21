import {Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@material-ui/core";
import {useState} from "react";

const ExportHICInfo = ({ open, handleClose, handleExport }) => {
    const [fileName, setFileName] = useState("TO_KHAI");

    const handleDownload = () => {
        handleExport(fileName);
        setFileName("TO_KHAI")
        handleClose();
    };

    return (
        <Dialog open={open} onClose={handleClose}>
            <DialogTitle>Nhập tên file bạn muốn lưu</DialogTitle>
            <DialogContent>
                <TextField
                    autoFocus
                    margin="dense"
                    label="Tên file"
                    fullWidth
                    variant="outlined"
                    value={fileName}
                    onChange={(e) => setFileName(e.target.value)}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color="secondary">
                    Hủy
                </Button>
                <Button onClick={handleDownload} color="primary">
                    Lưu
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ExportHICInfo;