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
import moment from "moment";
import "react-toastify/dist/ReactToastify.css";
import StaffMaternityHistoryPopupAdd from "./Popup/StaffMaternityHistoryPopupAdd";

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

function StaffMaternityHistoryV2() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();
    const { staffMaternityHistoryStore } = useStore();
    const { id } = useParams();
    const [isLoading, setIsLoading] = useState(false);
    const [selectedItem, setSelectedItem] = useState(null);

    const {
        staffMaternityHistoryList,
        openCreateEditPopup,
        openConfirmDeletePopup,
        setCurrentStaffId,
        pagingStaffMaternityHistory,
        resetStore,
        handleOpenCreateEdit,
        handleDelete,
        handleClose,
        handleConfirmDelete,
    } = staffMaternityHistoryStore;

    const isAdmin = useMemo(() => {
        const roles = localStorageService.getLoginUser()?.user?.roles?.map((r) => r.authority) || [];
        return ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"].some((role) => roles.includes(role));
    }, []);

    useEffect(() => {
        if (id) {
            setIsLoading(true);
            setCurrentStaffId(id);
            pagingStaffMaternityHistory().finally(() => setIsLoading(false));
        }
        return resetStore;
    }, [id]);

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
                        <IconButton
                            size='small'
                            className='ml-4'
                            onClick={() => {
                                handleDelete(rowData);
                            }}>
                            <Icon fontSize='small' color='secondary'>
                                delete
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            ),
        },
        {
            title: t("maternityHistory.startDate"),
            field: "startDate",
            align: "center",
            render: (rowData) => (rowData.startDate ? moment(rowData.startDate).format("DD/MM/YYYY") : ""),
        },
        {
            title: t("maternityHistory.endDate"),
            field: "endDate",
            align: "center",
            render: (rowData) => (rowData.endDate ? moment(rowData.endDate).format("DD/MM/YYYY") : ""),
        },
        {
            title: t("maternityHistory.birthNumber"),
            field: "birthNumber",
            align: "center",
            render: (rowData) => rowData.birthNumber || "",
        },
        {
            title: t("maternityHistory.note"),
            field: "note",
            align: "left",
            render: (rowData) => rowData.note || "",
        },
    ];

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray
                    name='maternityHistory'
                    render={() => (
                        <>
                            {isAdmin && values?.id && (
                                <ButtonGroup className={classes.buttonGroupSpacing}>
                                    <Button startIcon={<AddIcon />} onClick={() => handleOpenCreateEdit(null)}>
                                        Thêm mới
                                    </Button>
                                </ButtonGroup>
                            )}
                            <Grid container spacing={2} className={classes.root}>
                                <Grid item xs={12}>
                                    {isLoading ? (
                                        <p className='w-100 text-center'>Đang tải dữ liệu...</p>
                                    ) : staffMaternityHistoryList.length > 0 ? (
                                        <GlobitsTable
                                            data={staffMaternityHistoryList}
                                            columns={columns}
                                            maxWidth='100%'
                                            nonePagination
                                            selection={false}
                                        />
                                    ) : (
                                        <p className='w-100 text-center text-primary'>Chưa có quá trình thai sản</p>
                                    )}
                                </Grid>
                            </Grid>
                        </>
                    )}
                />

                {openCreateEditPopup && <StaffMaternityHistoryPopupAdd open={openCreateEditPopup} />}

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

export default memo(observer(StaffMaternityHistoryV2));
