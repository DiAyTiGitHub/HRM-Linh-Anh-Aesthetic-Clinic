import React, { memo, useEffect, useMemo, useState } from "react";
import { Grid, makeStyles, ButtonGroup, Button, IconButton, Icon, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import { useFormikContext, FieldArray } from "formik";
import { useParams } from "react-router";
import { observer } from "mobx-react";
import GlobitsTable from "app/common/GlobitsTable";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import { formatDate, formatMoney, formatVNDMoney } from "app/LocalFunction";
import "react-toastify/dist/ReactToastify.css";
import StaffSocialInsuranceV2CUForm from "./Popup/StaffSocialInsuranceV2CUForm";

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

function StaffSocialInsuranceV2() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values } = useFormikContext();
    const { staffSocialInsuranceStore } = useStore();
    const { id } = useParams();
    const [isLoading, setIsLoading] = useState(false);

    const {
        pagingStaffSocialInsurance,
        listStaffSocialInsurance,
        handleOpenCreateEdit,
        handleDelete,
        openCreateEditPopup,
        setOpenCreateEditPopup,
        handleConfirmDelete,
        handleClose,
        openConfirmDeletePopup,
        selectedStaffSocialInsurance,
        searchObject,
    } = staffSocialInsuranceStore;

    useEffect(() => {
        staffSocialInsuranceStore.openCreateEditPopup = false;
        if (id) {
            setIsLoading(true);
            searchObject.staffId = id;
            searchObject.staff = { id: id };
            pagingStaffSocialInsurance().finally(() => {
                setIsLoading(false);
            });
        }
    }, [id]);

    const isAdmin = useMemo(() => {
        const roles = localStorageService.getLoginUser()?.user?.roles?.map((r) => r.authority) || [];
        return ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"].some((role) => roles.includes(role));
    }, []);

    const columns = [
        {
            title: t("general.action"),
            width: "5%",
            minWidth: "80px",
            align: "center",
            render: (rowData) => {
                if (isAdmin) {
                    return (
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
                    );
                }
            },
        },
        {
            title: t("Ngày bắt đầu"),
            field: "startDate",
            minWidth: "150px",
            render: (data) => (
                <span style={{ padding: "0 16px" }}>
                    {data?.startDate ? formatDate("DD/MM/YYYY", data?.startDate) : ""}
                </span>
            ),
        },
        {
            title: t("Ngày kết thúc"),
            field: "endDate",
            minWidth: "150px",
            render: (data) => (
                <span style={{ padding: "0 16px" }}>{data?.endDate ? formatDate("DD/MM/YYYY", data?.endDate) : ""}</span>
            ),
        },
        {
            title: t("humanResourcesInformation.insuranceSalary"),
            field: "insuranceSalary",
            minWidth: "200px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.insuranceSalary)}</span>,
        },
        {
            title: t("humanResourcesInformation.staffPercentage"),
            field: "staffSocialInsurancePercentage",
            minWidth: "200px",
            render: (data) => <span
                style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.staffSocialInsurancePercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.staffHealthInsurancePercentage"),
            field: "staffHealthInsurancePercentage",
            minWidth: "200px",
            render: (data) => <span
                style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.staffHealthInsurancePercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.staffUnemploymentInsurancePercentage"),
            field: "staffUnemploymentInsurancePercentage",
            minWidth: "200px",
            render: (data) => <span
                style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.staffUnemploymentInsurancePercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.orgPercentage"),
            field: "orgSocialInsurancePercentage",
            minWidth: "200px",
            render: (data) => <span
                style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.orgSocialInsurancePercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.orgHealthInsurancePercentage"),
            field: "orgHealthInsurancePercentage",
            minWidth: "200px",
            render: (data) => <span
                style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.orgHealthInsurancePercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.orgUnemploymentInsurancePercentage"),
            field: "orgUnemploymentInsurancePercentage",
            minWidth: "200px",
            render: (data) => <span
                style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.orgUnemploymentInsurancePercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.staffInsuranceAmount"),
            field: "staffTotalInsuranceAmount",
            minWidth: "200px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.staffTotalInsuranceAmount)}</span>,
        },
        {
            title: t("humanResourcesInformation.orgInsuranceAmount"),
            field: "orgTotalInsuranceAmount",
            minWidth: "200px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.orgTotalInsuranceAmount)}</span>,
        },
        {
            title: t("humanResourcesInformation.totalInsuranceAmount"),
            field: "totalInsuranceAmount",
            minWidth: "150px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.totalInsuranceAmount)}</span>,
        },
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
                                    ) : listStaffSocialInsurance?.length ? (
                                        <GlobitsTable
                                            data={listStaffSocialInsurance}
                                            columns={columns}
                                            maxWidth='100%'
                                            nonePagination
                                            selection={false}
                                        />
                                    ) : (
                                        <p className='w-100 text-center'>Chưa có lần đóng BHXH theo kỳ lương nào</p>
                                    )}
                                </Grid>
                            </Grid>
                        </>
                    )}
                />

                {openCreateEditPopup && <StaffSocialInsuranceV2CUForm />}

                {openConfirmDeletePopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeletePopup}
                        onConfirmDialogClose={() => handleClose()}
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

export default memo(observer(StaffSocialInsuranceV2));
