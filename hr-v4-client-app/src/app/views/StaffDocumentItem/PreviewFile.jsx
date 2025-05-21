import { ButtonGroup, DialogContent, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { bytesToKB } from "app/LocalFunction";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import { downloadFile } from "../HumanResourcesInformation/StaffLabourAgreementAttachmentService";
import StaffLabourAgreementFilePreviewPopup from "../HumanResourcesInformation/TabContainer/Popup/StaffLabourAgreementFilePreviewPopup";

function PreviewFile(props) {
    const {
        fileProp,
        handleConfirmDeleteFile,
        showPreview = true,
        showDowload = true,
        showDelete = true,
        showName = false,
        name = "file",
        handleAffterSubmit,

        previewButton,
        downloadButton,
        deleteButton,
    } = props;
    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();

    function canPreviewFileByExtension(fileName) {
        // Define file extensions that can be previewed
        const previewableFileExtensions = [".jpg", ".jpeg", ".png", ".gif", ".pdf", ".txt", ".html"];

        // Get the file extension
        const fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // Check if the fileExtension is in the list of previewable file extensions
        return previewableFileExtensions.includes(fileExtension);
    }

    const [onPreviewFile, setOnPreviewFile] = useState(null);

    const [openPreviewPopup, setOpenPreviewPopup] = useState(false);

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

    const [onDeleteFile, setOnDeleteFile] = useState(null);

    function handleDeleteFile(file) {
        setOnDeleteFile(file);
    }

    // function handleConfirmDeleteFileDefault() {
    //     if (typeof handleConfirmDeleteFile === "function") {
    //         handleConfirmDeleteFile();
    //     } else {
    //         setFieldValue(name, null);
    //         setOnDeleteFile(null);
    //     }
    // }
    function handleConfirmDeleteFileDefault() {
        //console.log("values: ", values);
        if (typeof handleConfirmDeleteFile === "function") {
            handleConfirmDeleteFile();
        } else {
            setFieldValue(name, null);
            setOnDeleteFile(null);
        }

        if (typeof handleAffterSubmit === "function") {
            handleAffterSubmit(null);
        }
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

    return (
        <>
            {fileProp && (
                <>
                    {showName && (
                        <Grid item xs={6}>
                            {fileProp?.name} - {bytesToKB(fileProp?.contentSize)}
                        </Grid>
                    )}
                    {fileProp &&
                        canPreviewFileByExtension(fileProp?.name) &&
                        showPreview &&
                        (previewButton ? (
                            <span onClick={() => handlePreviewFile(fileProp)}>{previewButton}</span>
                        ) : (
                            <Tooltip title='Xem trước'>
                                <IconButton size='small' onClick={() => handlePreviewFile(fileProp)}>
                                    <Icon fontSize='small' color='primary'>
                                        visibility
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        ))}
                    {showDowload &&
                        fileProp &&
                        (downloadButton ? (
                            <span onClick={() => handleDownloadFile(fileProp)}>{downloadButton}</span>
                        ) : (
                            <Tooltip title='Tải tệp'>
                                <IconButton
                                    size='small'
                                    style={{ color: "green" }}
                                    onClick={() => handleDownloadFile(fileProp)}>
                                    <Icon fontSize='small'>get_app</Icon>
                                </IconButton>
                            </Tooltip>
                        ))}

                    {showDelete &&
                        fileProp &&
                        (deleteButton ? (
                            <span onClick={handleDeleteFile}>{deleteButton}</span>
                        ) : (
                            <Tooltip title='Xóa'>
                                <IconButton size='small' onClick={handleDeleteFile}>
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        ))}
                </>
            )}

            {onDeleteFile && Boolean(onDeleteFile) && (
                <GlobitsConfirmationDialog
                    open={Boolean(onDeleteFile)}
                    onConfirmDialogClose={function () {
                        setOnDeleteFile(null);
                    }}
                    onYesClick={handleConfirmDeleteFileDefault}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa tệp này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
            
            {openPreviewPopup && (
                <GlobitsPopupV2
                    popupId={"file-preview-popup"}
                    scroll={"body"}
                    size='sm'
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
                </GlobitsPopupV2>
            )}
        </>
    );
}

export default memo(observer(PreviewFile));
