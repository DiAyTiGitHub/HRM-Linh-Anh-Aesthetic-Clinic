import React, { memo, useEffect, useState } from "react";
import { Grid, DialogActions, Button, DialogContent, Tooltip, IconButton, Icon, ButtonGroup } from "@material-ui/core";
import { observer } from "mobx-react";
import { DropzoneArea, DropzoneDialog } from "material-ui-dropzone";
import { useFormikContext } from "formik";
import AttachmentIcon from "@material-ui/icons/Attachment";
import { useTranslation } from "react-i18next";
import axios from "axios";
import ConstantList from "app/appConfig";
import { toast } from "react-toastify";
import { downloadFile, uploadFile } from "../../StaffLabourAgreementAttachmentService";
import GlobitsTable from "app/common/GlobitsTable";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsPopup from "app/common/GlobitsPopup";
import { bytesToKB, getFileType } from "app/LocalFunction";
import StaffLabourAgreementFilePreviewPopup from "./StaffLabourAgreementFilePreviewPopup";
import SelectFile from "app/views/StaffDocumentItem/SelectFile";

function StaffLabourAgreementAttachmentSection() {
    const { values, setFieldValue } = useFormikContext();
    const { t } = useTranslation();

    const [openChooseFiles, setOpenChooseFiles] = useState(false);

    function handleOpenChooseFilesPopup() {
        setOpenChooseFiles(true);
    }

    function handleCloseChooseFilesPopup() {
        setOpenChooseFiles(false);
    }
    const acceptedFiles = [
        "image/jpeg",
        "image/png",
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ]; // Mặc định hỗ trợ JPG, PNG, PDF, DOCX
    async function handleUploadFiles(data) {
        const uploadedFiles = [];
        if (data && data?.length > 0) {
            for (let i = 0; i < data?.length; i++) {
                const file = data[i];
                // console.log("file", file);
                try {
                    const response = await uploadFile(file);
                    // console.log("response", response);
                    uploadedFiles.push(response?.data);
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

        // console.log("uploadedFiles: ", uploadedFiles);
        const existedFiles = values?.files || [];
        const newFiles = [...existedFiles, ...uploadedFiles];
        //set new files to display
        setFieldValue("files", newFiles);
        handleCloseChooseFilesPopup();
    }

    async function handleDownloadFile(file) {
        try {
            const response = await downloadFile(file?.id);
            // console.log("downloadFile: ", response);
            // Create a blob from the file stream
            const blob = new Blob([response?.data], { type: response?.headers["content-type"] });

            // Create blob link to download
            const url = window.URL.createObjectURL(new Blob([blob]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", file?.name);

            // Append to html link element page
            document.body.appendChild(link);

            // Start download
            link.click();

            // Clean up and remove the link
            link.parentNode.removeChild(link);
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi tải tệp " + file?.name + ", vui lòng thử lại sau");
        }
    }

    const [onDeleteFile, setOnDeleteFile] = useState(null);

    function handleDeleteFile(file) {
        setOnDeleteFile(file);
    }

    function handleConfirmDeleteFile() {
        const remainFiles = [];
        if (Array.isArray(values?.files)) {
            Array.from(values?.files)?.forEach(function (file) {
                if (file?.id != onDeleteFile?.id && file?.name != onDeleteFile?.name) {
                    remainFiles.push(file);
                }
            });
        }
        setFieldValue("files", remainFiles);
        setOnDeleteFile(null);
    }

    const [openPreviewPopup, setOpenPreviewPopup] = useState(false);
    const [onPreviewFile, setOnPreviewFile] = useState(null);

    function handleClosePreviewPopup() {
        setOpenPreviewPopup(false);
        setOnPreviewFile(null);
    }

    function handleOpenPreviewPopup(file) {
        if (!file) return;
        setOnPreviewFile(file);
        setOpenPreviewPopup(true);
    }

    async function handlePreviewFile(file) {
        try {
            handleOpenPreviewPopup(file);
        } catch (error) {
            console.error(error);
            setOpenPreviewPopup(false);
            toast.error("Có lỗi xảy ra khi xem trước tệp, vui lòng thử lại sau");
        }
    }

    function canPreviewFileByExtension(fileName) {
        // Define file extensions that can be previewed
        const previewableFileExtensions = [".jpg", ".jpeg", ".png", ".gif", ".pdf", ".txt", ".html"];

        // Get the file extension
        const fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // Check if the fileExtension is in the list of previewable file extensions
        return previewableFileExtensions.includes(fileExtension);
    }

    const columns = [
        {
            title: "Tên tệp",
            field: "name",
            align: "left",
        },
        // {
        //     title: 'Loại tệp',
        //     field: 'contentType',
        //     align: 'left',
        // },
        {
            title: "Kích thước",
            field: "contentSize",
            align: "left",
            render: (file) => file?.contentSize && <span>{bytesToKB(file?.contentSize)}</span>,
        },
        // {
        //     title: 'Đuôi mở rộng',
        //     field: 'extension',
        //     align: 'left',
        // },
        // {
        //     title: 'File Path',
        //     field: 'filePath',
        //     align: 'left',
        // },
        {
            title: "Thao tác",
            align: "center",
            field: "action",
            render: function (file) {
                const canPreview = canPreviewFileByExtension(file?.name);

                return (
                    <div className='flex flex-middle flex-center items-center'>
                        {canPreview && (
                            <Tooltip title='Xem trước' placement='top'>
                                <IconButton
                                    className=''
                                    size='small'
                                    onClick={function () {
                                        handlePreviewFile(file);
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        visibility
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}

                        <Tooltip title='Tải tệp' placement='top'>
                            <IconButton
                                className='ml-8'
                                size='small'
                                style={{ color: "green" }}
                                onClick={function () {
                                    handleDownloadFile(file);
                                }}>
                                <Icon fontSize='small'>get_app</Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title='Xóa' placement='top'>
                            <IconButton
                                className='ml-8'
                                size='small'
                                onClick={function () {
                                    handleDeleteFile(file);
                                }}>
                                <Icon fontSize='small' color='secondary'>
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
    ];

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <div className='flex justify-between items-center py-8'>
                    <div className='d-block'>
                        <strong>{t("agreements.attachments")}:</strong>
                    </div>

                    <ButtonGroup
                        className='filterButtonV4'
                        color='container'
                        aria-label='outlined primary button group'>
                        <Button
                            startIcon={<AttachmentIcon />}
                            type='button'
                            onClick={handleOpenChooseFilesPopup}
                            className='d-inline-flex py-2 px-8 btnHrStyl'>
                            Thêm tệp đính kèm
                        </Button>
                        {/* <Button
                            startIcon={<SearchIcon className={``} />}
                            className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                            type="submit"
                        >
                            Tìm kiếm
                        </Button>
                        <Button
                            startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                            className=" d-inline-flex py-2 px-8 btnHrStyle"
                            onClick={handleTogglePopupFilter}
                        >
                            Bộ lọc
                        </Button> */}
                    </ButtonGroup>
                </div>

                <GlobitsTable data={values?.files || []} columns={columns} nonePagination={true} />
            </Grid>

            {openPreviewPopup && (
                <GlobitsPopup
                    popupId={"file-preview-popup"}
                    scroll={"body"}
                    size='xl'
                    open={openPreviewPopup}
                    noDialogContent
                    title={"Nội dung tệp xem trước"}
                    onClosePopup={handleClosePreviewPopup}>
                    <div className='dialog-body'>
                        <DialogContent className='p-4'>
                            <StaffLabourAgreementFilePreviewPopup
                                file={onPreviewFile}
                                handleClose={handleClosePreviewPopup}
                            />
                        </DialogContent>
                    </div>
                </GlobitsPopup>
            )}

            {onDeleteFile && Boolean(onDeleteFile) && (
                <GlobitsConfirmationDialog
                    open={Boolean(onDeleteFile)}
                    onConfirmDialogClose={function () {
                        setOnDeleteFile(null);
                    }}
                    onYesClick={handleConfirmDeleteFile}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa tệp này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
            {/* <SelectFile
                name={"file"}
                fileProp={values?.files}
                showPreview={true}
                showDowload={true}
                showDelete={true}
                showName={false}
                handleAffterSubmit={handleUploadFiles}
                maxFileSize={1024000}
            /> */}

            <DropzoneDialog
                open={openChooseFiles}
                onSave={handleUploadFiles}
                showPreviews={true}
                onClose={handleCloseChooseFilesPopup}
                clearOnUnmount={true}
                // dropzoneText={"Chọn, kéo thả các tệp đính kèm tại đây..."}
                previewText='Các tệp đính kèm đã được chọn:'
                useChipsForPreview
                dialogTitle='Thêm các tệp đính kèm'
                maxWidth='lg'
                showFileNames
                showFileNamesInPreview
                filesLimit={12}
                maxFileSize={5242880}
                acceptedFiles={acceptedFiles}
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
                            Kích thước tối đa: {Math.round(5242880 / 1024 / 1024)} MB
                        </p>
                    </div>
                }
                submitButtonText={"Lưu"}
                cancelButtonText={"Hủy"}
                // Icon={<AttachmentIcon />}
            />
        </Grid>
    );
}

export default memo(observer(StaffLabourAgreementAttachmentSection));
