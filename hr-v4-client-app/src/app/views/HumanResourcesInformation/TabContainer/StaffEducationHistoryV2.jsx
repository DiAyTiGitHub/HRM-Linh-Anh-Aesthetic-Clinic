import { Button, ButtonGroup, Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import "react-toastify/dist/ReactToastify.css";
import StaffEducationHistoryPopupAdd from "./Popup/StaffEducationHistoryPopupAdd";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";

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

function StaffEducationHistoryV2() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values } = useFormikContext();
    const { staffEducationHistoryStore } = useStore();
    const { id } = useParams();

    const {
        pagingEducationHistory,
        educationHistoryList,
        shouldOpenEditorDialog,
        handleEditEducationHistory,
        handleDelete,
        setOpenCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        setSelectedEducationHistory,
        selectedEducationHistory,
        setCurrentStaffId,
        shouldOpenConfirmationDialog,
        resetEducationHistoryStore,
    } = staffEducationHistoryStore;

    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        staffEducationHistoryStore.openCreateEditPopup = false;
        if (id) {
            setIsLoading(true);
            pagingEducationHistory({ staffId: id }).finally(() => {
                setIsLoading(false);
            });
        }
        setCurrentStaffId(id);

        return resetEducationHistoryStore;
    }, [id]);

    const isAdmin = useMemo(() => {
        let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);

    async function handleOpenEducationHistoryForm() {
        setOpenCreateEditPopup(true);
        setSelectedEducationHistory({ staff: { id: id } });
    }
    const columns = [
        {
            title: t("general.action"),
            minWidth: "48px",
            align: "center",
            render: (rowData) => (
                <div className='flex flex-middle justify-center'>
                    <Tooltip title='Cập nhật thông tin' placement='top'>
                        <IconButton size='small' onClick={() => handleEditEducationHistory(rowData?.id)}>
                            <Icon fontSize='small' color='primary'>
                                edit
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title='Xóa' placement='top'>
                        <IconButton size='small' className='ml-4' onClick={() => handleDelete(rowData?.id)}>
                            <Icon fontSize='small' color='secondary'>
                                delete
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            ),
        },
        {
            title: t("educationHistory.startDate"),
            field: "startDate",
            align: "left",
            render: (data) => (data?.startDate ? <span>{formatDate("DD/MM/YYYY", data.startDate)}</span> : null),
        },
        {
            title: t("educationHistory.endDate"),
            field: "endDate",
            align: "left",
            render: (data) => (data?.endDate ? <span>{formatDate("DD/MM/YYYY", data.endDate)}</span> : null),
        },
        {
            title: t("educationHistory.educationalInstitution"),
            field: "educationalInstitution.name",
            align: "left",
            render: (data) => data?.educationalInstitution?.name || "",
        },
        {
            title: t("educationHistory.country"),
            field: "country.name",
            align: "left",
            render: (data) => data?.country?.name || "",
        },
        {
            title: t("educationHistory.speciality"),
            field: "speciality.name",
            align: "left",
            render: (data) => data?.speciality?.name || "",
        },
        {
            title: t("educationHistory.formsOfTraining"),
            field: "educationType.name",
            align: "left",
            render: (data) => data?.educationType?.name || "",
        },
        {
            title: t("educationHistory.degree"),
            field: "educationDegree.name",
            align: "left",
            render: (data) => data?.educationDegree?.name || "",
        },
        {
            title: t("educationHistory.note"),
            field: "description",
            align: "left",
            render: (data) => data?.description || "",
        },
    ];

    return (
        <Grid container spacing={2}>
            {isAdmin && values?.id && (
                <Grid item xs={12}>
                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                        <Button startIcon={<AddIcon />} type='button' onClick={() => handleOpenEducationHistoryForm()}>
                            Thêm mới
                        </Button>
                    </ButtonGroup>
                </Grid>
            )}
            <Grid item xs={12}>
                {isLoading ? (
                    <p className='w-100 text-center'>Đang tải dữ liệu...</p>
                ) : educationHistoryList ? (
                    <GlobitsTable
                        data={educationHistoryList}
                        columns={columns}
                        maxWidth='100%'
                        nonePagination
                        selection={false}
                    />
                ) : (
                    <p className='w-100 text-center'>Chưa có lịch sử giáo dục</p>
                )}
            </Grid>

            <StaffEducationHistoryPopupAdd />

            <GlobitsConfirmationDialog
                open={shouldOpenConfirmationDialog}
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

export default memo(observer(StaffEducationHistoryV2));
