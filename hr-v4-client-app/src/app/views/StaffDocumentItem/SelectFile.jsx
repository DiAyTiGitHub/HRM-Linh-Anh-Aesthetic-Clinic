import { ButtonGroup, Icon, IconButton, Tooltip } from "@material-ui/core";
import { useFormikContext } from "formik";
import { DropzoneDialog } from "material-ui-dropzone";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import PreviewFile from "./PreviewFile";
import { uploadFile } from "./StaffDocumentItemService";

function SelectFile(props) {
    const {
        readOnly = false,
        name = "file",
        fileProp,
        showPreviews = true,
        showName = false,
        showDowload = true,
        showDelete = true,
        handleAffterSubmit,
        acceptedFiles = [
            "image/jpeg",
            "image/png",
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        ], // Mặc định hỗ trợ JPG, PNG, PDF, DOCX
        maxFileSize = 1024000,
        // Các prop mới để truyền nút tùy chỉnh
        uploadButton,
        previewButton,
        downloadButton,
        deleteButton,
        handleConfirmDeleteFile,
    } = props;
    const { t } = useTranslation();
    const [openChooseFiles, setOpenChooseFiles] = useState(false);

    const { values, setFieldValue } = useFormikContext();

    function handleOpenChooseFilesPopup() {
        setOpenChooseFiles(true);
    }

    function handleCloseChooseFilesPopup() {
        setOpenChooseFiles(false);
    }

    async function handleUploadFiles(data) {
        if (data && data?.length > 0) {
            for (let i = 0; i < data?.length; i++) {
                const file = data[i];
                // console.log("file", file);
                try {
                    const response = await uploadFile(file);
                    setFieldValue(name, response?.data);

                    if (typeof handleAffterSubmit === "function") {
                        handleAffterSubmit(response?.data);
                    }
                } catch (err) {
                    console.error(err);
                    toast.error(
                        "Không thể tải file " +
                        file?.name +
                        ", vui lòng kiểm tra lại kích cỡ của file (không được quá lớn)"
                    );
                }
            }
        }
        //set new files to display
        handleCloseChooseFilesPopup();
    }

    return (
        <div>
            <ButtonGroup color='container' aria-label='outlined primary button group'>
                {!readOnly && (
                    <>
                        {uploadButton ? (
                            <span onClick={handleOpenChooseFilesPopup}>{uploadButton}</span>
                        ) : (
                            <Tooltip title='Upload'>
                                <IconButton size='small' style={{ color: "green" }} onClick={handleOpenChooseFilesPopup}>
                                    <Icon fontSize='small'>upload</Icon>
                                </IconButton>
                            </Tooltip>
                        )}
                    </>
                )}


                <PreviewFile
                    fileProp={fileProp}
                    showName={showName}
                    name={name}
                    handleConfirmDeleteFile={handleConfirmDeleteFile}
                    showPreview={showPreviews}
                    showDowload={showDowload}
                    showDelete={showDelete && !readOnly}
                    previewButton={previewButton}
                    downloadButton={downloadButton}
                    deleteButton={deleteButton}
                    handleAffterSubmit={handleAffterSubmit}
                />
            </ButtonGroup>

            <DropzoneDialog
                open={openChooseFiles}
                onSave={handleUploadFiles}
                showPreviews={true}
                onClose={handleCloseChooseFilesPopup}
                clearOnUnmount={true}
                dropzoneText={
                    <div>
                        <p>Chọn, kéo thả các tệp đính kèm tại đây...</p>
                        <p style={{ fontSize: "0.85em", color: "#666", marginTop: "8px" }}>
                            Định dạng được chấp nhận:{" "}
                            {acceptedFiles
                                .map((mime) => {
                                    // Chuyển đổi MIME type thành tên định dạng dễ đọc
                                    if (mime === "image/jpeg") return "JPG";
                                    if (mime === "image/png") return "PNG";
                                    if (mime === "application/pdf") return "PDF";
                                    if (
                                        mime ===
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                    )
                                        return "DOCX";
                                    // Thêm các MIME type khác nếu cần
                                    return mime.split("/").pop().toUpperCase();
                                })
                                .join(", ")}
                            <br />
                            Kích thước tối đa: {Math.round(maxFileSize / 1024 / 1024)} MB
                        </p>
                    </div>
                }
                previewText='Các tệp đính kèm đã được chọn:'
                useChipsForPreview
                dialogTitle='Thêm các tệp đính kèm'
                maxWidth='md'
                showFileNames
                showFileNamesInPreview
                filesLimit={1}
                maxFileSize={maxFileSize}
                acceptedFiles={acceptedFiles}
                getDropRejectMessage={(rejectedFile) => {
                    if (rejectedFile.size > maxFileSize) {
                        return `File "${rejectedFile.name}" quá lớn. Kích thước tối đa là ${Math.round(
                            maxFileSize / 1024 / 1024
                        )} MB.`;
                    }
                    return `File "${rejectedFile.name}" không được chấp nhận. Chỉ chấp nhận định dạng: ${acceptedFiles
                        .map((mime) => {
                            if (mime === "image/jpeg") return "JPG";
                            if (mime === "image/png") return "PNG";
                            if (mime === "application/pdf") return "PDF";
                            if (mime === "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                return "DOCX";
                            return mime.split("/").pop().toUpperCase();
                        })
                        .join(", ")}.`;
                }}
                submitButtonText="Tải lên" // Tùy chỉnh nút Submit thành "Gửi"
                cancelButtonText="Hủy" // Tùy chỉnh nút Cancel thành "Hủy"
            />
        </div>
    );
}

export default memo(observer(SelectFile));
