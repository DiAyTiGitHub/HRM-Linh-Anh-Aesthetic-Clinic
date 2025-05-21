import { Button, ButtonGroup, Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import { formatDate } from "app/LocalFunction";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import "react-toastify/dist/ReactToastify.css";
import StaffWorkingHistoryPopupAdd from "./Popup/StaffWorkingHistoryPopupAdd";

const useStyles = makeStyles((theme) => ({
    root: {
        "& .MuiAccordion-rounded": { borderRadius: "5px" },
        "& .MuiPaper-root": { borderRadius: "5px" },
        "& .MuiAccordionSummary-root": {
            borderRadius: "5px",
            color: "#5899d1",
            fontWeight: "400",
            "& .MuiTypography-root": { fontSize: "1rem" },
        },
        "& .Mui-expanded": {
            "& .MuiAccordionSummary-root": {
                backgroundColor: "#EBF3F9",
                color: "#5899d1",
                fontWeight: "700",
                maxHeight: "50px !important",
                minHeight: "50px !important",
            },
            "& .MuiTypography-root": { fontWeight: 700 },
        },
        "& .MuiButton-root": { borderRadius: "0.125rem !important" },
    },
    buttonGroupSpacing: {
        marginBottom: "10px",
    },
}));

function StaffWorkingHistoryV2() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values } = useFormikContext();
    const { staffWorkingHistoryStore } = useStore();
    const { id } = useParams();

    const {
        pagingStaffWorkingHistory,
        staffWorkingHistoryList,
        openCreateEditPopup,
        handleOpenCreateEdit,
        handleDelete,
        setOpenCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        setCurrentStaffId,
        openConfirmDeletePopup,
        resetStore,
    } = staffWorkingHistoryStore;

    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        staffWorkingHistoryStore.openCreateEditPopup = false;
        if (id) {
            setIsLoading(true);
            pagingStaffWorkingHistory({ staffId: id }).finally(() => {
                setIsLoading(false);
            });
            setCurrentStaffId(id);
        }
        setCurrentStaffId(id);

        return resetStore;
    }, [id]);

    const isAdmin = useMemo(() => {
        let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);

    const columns = [
        {
            title: t("general.action"),
            minWidth: "48px",
            align: "center",
            render: (rowData) => (
                <div className='flex flex-middle justify-center'>
                    <Tooltip title='Cập nhật thông tin' placement='top'>
                        <IconButton size='small' onClick={() => handleOpenCreateEdit(rowData?.id)}>
                            <Icon fontSize='small' color='primary'>
                                edit
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title='Xóa' placement='top'>
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
            title: t("staffWorkingHistory.startDate"),
            field: "startDate",
            align: "left",
            render: (data) => (data?.startDate ? <span>{formatDate("DD/MM/YYYY", data.startDate)}</span> : null),
        },
        {
            title: t("staffWorkingHistory.endDate"),
            field: "endDate",
            align: "left",
            render: (data) => (data?.endDate ? <span>{formatDate("DD/MM/YYYY", data.endDate)}</span> : ""),
        },
        {
            title: t("staffWorkingHistory.fromPosition"),
            field: "fromPosition.name",
            align: "center",
            render: (data) => data?.fromPosition?.name || "",
        },
        {
            title: t("staffWorkingHistory.toPosition"),
            field: "toPosition.name",
            align: "center",
            render: (data) => data?.toPosition?.name || "",
        },
        // {
        //     title: t("staffWorkingHistory.transferType"),
        //     field: "transferType",
        //     align: "center",
        //     render: (data) => {
        //         const transferType = LocalConstants.StaffWorkingHistoryTransferType.find(
        //             (item) => item.value === data?.transferType
        //         );
        //         return transferType ? transferType.name : "";
        //     },
        // },
        {
            title: t("staffWorkingHistory.note"),
            field: "note",
            align: "center",
            render: (data) => data?.note || "",
        },
    ];

    const columns2 = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => (
                <div className='flex flex-middle justify-center'>
                    <Tooltip title='Cập nhật thông tin' placement='top'>
                        <IconButton size='small' onClick={() => handleOpenCreateEdit(rowData?.id)}>
                            <Icon fontSize='small' color='primary'>
                                edit
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title='Xóa' placement='top'>
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
            title: "Vị trí khi nghỉ việc",
            field: "fromPosition.name",
            align: "center",
            render: (data) => data?.fromPosition?.name || "",
        },

        {
            title: t("Ngày tạm dừng/nghỉ việc"),
            field: "startDate",
            align: "center",
            render: (data) => (data?.startDate ? <span>{formatDate("DD/MM/YYYY", data.startDate)}</span> : null),
        },
        {
            title: t("Ngày làm lại"),
            field: "endDate",
            align: "center",
            render: (data) => (data?.endDate ? <span>{formatDate("DD/MM/YYYY", data.endDate)}</span> : ""),
        },
        {
            title: t("Lý do nghỉ việc"),
            field: "note",
            align: "center",
            render: (data) => data?.note || "",
        },
    ];
    const [firstGroup, setFirstGroup] = useState([]);
    const [secondGroup, setSecondGroup] = useState([]);

    useEffect(() => {
        if (staffWorkingHistoryList?.length > 0) {
            setSecondGroup(staffWorkingHistoryList.filter((item) => item.transferType === 3));
            setFirstGroup(staffWorkingHistoryList.filter((item) => item.transferType !== 3));
        } else {
            setFirstGroup([]);
            setSecondGroup([]);
        }
    }, [staffWorkingHistoryList]);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <TabAccordion title={"Quá trình công tác"}>
                    <Grid container spacing={2}>
                        {isAdmin && values?.id && (
                            <Grid item xs={12}>
                                <ButtonGroup color='container' aria-label='outlined primary button group'>
                                    <Button
                                        startIcon={<AddIcon />}
                                        type='button'
                                        onClick={() => handleOpenCreateEdit(null, 1)}>
                                        Thêm mới điều chuyển
                                    </Button>
                                </ButtonGroup>
                            </Grid>
                        )}
                        <Grid item xs={12}>
                            <GlobitsTable
                                data={firstGroup}
                                columns={columns}
                                maxWidth='100%'
                                nonePagination
                                selection={false}
                            />
                        </Grid>
                    </Grid>
                </TabAccordion>
            </Grid>
            <Grid item xs={12}>
                <TabAccordion title={"Tạm nghỉ"}>
                    <Grid container spacing={2}>
                        {isAdmin && values?.id && (
                            <Grid item xs={12}>
                                <ButtonGroup color='container' aria-label='outlined primary button group'>
                                    <Button
                                        startIcon={<AddIcon />}
                                        type='button'
                                        onClick={() => handleOpenCreateEdit(null, 3)}
                                        >
                                        Thêm mới tạm nghỉ
                                    </Button>
                                </ButtonGroup>
                            </Grid>
                        )}
                        <Grid item xs={12}>
                            <GlobitsTable
                                data={secondGroup}
                                columns={columns2}
                                maxWidth='100%'
                                nonePagination
                                selection={false}
                            />
                        </Grid>
                    </Grid>
                </TabAccordion>
            </Grid>

            <StaffWorkingHistoryPopupAdd />

            <GlobitsConfirmationDialog
                open={openConfirmDeletePopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDelete}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />
        </Grid>
    );
}

export default memo(observer(StaffWorkingHistoryV2));
