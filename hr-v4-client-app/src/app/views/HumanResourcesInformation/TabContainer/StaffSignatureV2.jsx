import { Button, ButtonGroup, Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsTable from "app/common/GlobitsTable";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import "react-toastify/dist/ReactToastify.css";
import StaffSignaturePopupAdd from "./Popup/StaffSignaturePopupAdd";
import PreviewFile from "app/views/StaffDocumentItem/PreviewFile";
import { width } from "dom-helpers";
import StaffLabourAgreementFilePreviewPopup from "./Popup/StaffLabourAgreementFilePreviewPopup";

const useStyles = makeStyles((theme) => ({
    root: {
        "& .MuiAccordion-rounded, & .MuiPaper-root": { borderRadius: "5px" },
        "& .MuiAccordionSummary-root": {
            borderRadius: "5px",
            color: "#5899d1",
            fontWeight: "400",
            "& .MuiTypography-root": { fontSize: "1rem" },
        },
        "& .Mui-expanded .MuiAccordionSummary-root": {
            backgroundColor: "#EBF3F9",
            color: "#5899d1",
            fontWeight: "700",
            maxHeight: "50px !important",
            minHeight: "50px !important",
        },
        "& .MuiTypography-root": { fontWeight: 700 },
        "& .MuiButton-root": { borderRadius: "0.125rem !important" },
    },
    buttonGroupSpacing: { marginBottom: "10px" },
}));

function StaffSignatureV2() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values } = useFormikContext();
    const { staffSignatureStore } = useStore();
    const { id } = useParams();
    const [isLoading, setIsLoading] = useState(false);

    const {
        pagingStaffSignature,
        staffSignatureList,
        handleOpenCreateEdit,
        handleDelete,
        handleConfirmDelete,
        handleClose,
        openConfirmDeletePopup,
        setCurrentStaffId,
        openCreateEditPopup,
        searchObject,
        resetStore,
    } = staffSignatureStore;

    const handlePreviewFile = () => {};
    const handleDownloadFile = () => {};
    useEffect(() => {
        staffSignatureStore.openCreateEditPopup = false;

        if (id) {
            pagingStaffSignature();
            setCurrentStaffId(id);
        }
        setCurrentStaffId(id);

        return resetStore;
    }, [id]);

    const isAdmin = useMemo(() => {
        const roles = localStorageService.getLoginUser()?.user?.roles?.map((r) => r.authority) || [];
        return ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"].some((role) => roles.includes(role));
    }, []);

    const columns = [
        {
            title: t("general.action"),
            width: "5%",
            align: "center",
            render: (rowData) => (
                <div className='flex flex-middle justify-center'>
                    <Tooltip title={t("Cập nhật thông tin")} placement='top'>
                        <IconButton size='small' onClick={() => handleOpenCreateEdit(rowData?.id)}>
                            <Icon fontSize='small' color='primary'>
                                edit
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title={t("Xóa")} placement='top'>
                        <IconButton size='small' className='ml-4' onClick={() => handleDelete(rowData)}>
                            <Icon fontSize='small' color='secondary'>
                                delete
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            ),
        },
        {
            title: "Tệp đính kèm",
            field: "file",
            align: "left",
            minWidth: "50px",
            width: "10%",
            render: (rowData) => {
                if (rowData?.file) {
                    return (
                        <div className='flex flex-middle justify-center'>
                            <PreviewFile
                                fileProp={rowData?.file}
                                showPreview={true}
                                showDowload={true}
                                showDelete={false}
                                showName={false}
                            />
                        </div>
                    );
                } else {
                    return null;
                }
            },
        },
        { title: t("Tên"), field: "name", align: "left" },
        { title: t("Mã"), field: "code", align: "left", minWidth: "50px", width: "10%" },

        { title: t("Mô tả"), field: "description", align: "left" },
    ];

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray
                    name='agreements'
                    render={() => (
                        <>
                            {isAdmin && values?.id && (
                                <ButtonGroup className={classes.buttonGroupSpacing}>
                                    <Button startIcon={<AddIcon />} onClick={() => handleOpenCreateEdit()}>
                                        Thêm mới
                                    </Button>
                                </ButtonGroup>
                            )}
                            <Grid container spacing={2} className={classes.root}>
                                <Grid item xs={12}>
                                    {isLoading ? (
                                        <p className='w-100 text-center'>Đang tải dữ liệu...</p>
                                    ) : staffSignatureList?.length ? (
                                        <GlobitsTable
                                            data={staffSignatureList}
                                            columns={columns}
                                            maxWidth='100%'
                                            nonePagination
                                            selection={false}
                                        />
                                    ) : (
                                        <p className='w-100 text-center'>Chưa có mẫu chữ ký</p>
                                    )}
                                </Grid>
                            </Grid>
                        </>
                    )}
                />

                {openCreateEditPopup && <StaffSignaturePopupAdd open={openCreateEditPopup} />}
                {openConfirmDeletePopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeletePopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )}
            </Grid>
        </Grid>
    );
}

export default memo(observer(StaffSignatureV2));
