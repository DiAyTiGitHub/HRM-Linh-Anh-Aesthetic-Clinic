import { Grid, makeStyles, ButtonGroup, IconButton, Icon, Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { useFormikContext } from "formik";
import React, { memo, useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import { Button } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import localStorageService from "app/services/localStorageService";
import { useParams } from "react-router";

// pop up
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import CheckIcon from "@material-ui/icons/Check";
import LoadingTabSkeleton from "app/common/Skeleton/SkeletonTab";
import StaffWorkingLocationCUForm from "app/views/StaffWorkingLocation/StaffWorkingLocationCUForm";
import { pagingStaffWorkingLocation } from "app/views/StaffWorkingLocation/StaffWorkingLocationService";

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
    noAllowance: {
        textAlign: "center",
        marginTop: theme.spacing(4),
        fontStyle: "italic",
        color: "#999",
    },
    buttonGroupSpacing: {
        marginBottom: "10px",
    },
}));

function StaffWorkingLocations() {
    const { t } = useTranslation();

    const classes = useStyles();

    const { values } = useFormikContext();

    const {
        staffWorkingLocationStore
    } = useStore();

    const { id } = useParams();

    const {
        openCreateEditPopup,
        setOpenCreateEditPopup,
        searchObject,
        handleSetSearchObject,
        openConfirmDeletePopup,
        handleConfirmDelete: handleConfirmDeleteSingle,
        handleClose,
        handleOpenCreateEdit,
        handleDelete,
        setSelectedWorkingLocation
    } = staffWorkingLocationStore;

    const [workingLocations, setWorkingLocations] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        fetchWorkingLocations();
    }, [id]);

    const fetchWorkingLocations = async () => {
        if (!id) return;
        setIsLoading(true);
        try {

            const payload = {
                ...searchObject,
                staffId: id,
                staff: {
                    id: id,
                },
                pageIndex: 1,
                pageSize: 9999
            };

            const { data } = await pagingStaffWorkingLocation(payload);

            setWorkingLocations(data?.content);
        } catch (error) {
            console.error("Lỗi khi tải danh sách địa điểm làm việc", error);
        } finally {
            setIsLoading(false);
        }
    };

    const columns = [
        {
            title: t("general.action"),
            width: "15%",
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Cập nhật thông tin" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleOpenCreateEdit(rowData?.id);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Xóa" placement="top">
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => handleDelete(rowData)
                                }
                            >
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },

        {
            title: t("Địa điểm làm việc"),
            field: "workplace.name",
            width: "60%",
            render: row => <span className="px-4">{row?.workplace?.name}</span>,
            align: "center"
        },

        {
            title: "Địa điểm chính",
            field: "isMainLocation",
            width: "10%",
            align: "center",
            render: (data) => {
                if (data?.isMainLocation) return <CheckIcon fontSize='small' style={{ color: "green" }} />;
                return "";
            },
        },

    ];

    const isAdmin = useMemo(() => {
        let roles =
            localStorageService
                .getLoginUser()
                ?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);


    async function handleConfirmDelete() {
        try {
            const response = await handleConfirmDeleteSingle();

            if (!response) throw new Error();

            await fetchWorkingLocations();
        } catch (error) {
            console.error(error);
        }
    }

    async function handleOpenCUStaffWorkingLocation() {
        setSelectedWorkingLocation({
            staff: values,
            staffId: values?.id
        });
        setOpenCreateEditPopup(true);
    }

    return (
        <>

            <Grid container spacing={2} className={classes.root}>
                {(isAdmin && values?.id) && (
                    <Grid item xs={12} className="pb-0">
                        <ButtonGroup
                            color="container"
                            aria-label="outlined primary button group"
                            className={classes.buttonGroupSpacing}
                        >
                            <Tooltip title="Thêm mới địa điểm làm việc cho nhân viên" placement="top" arrow>
                                <Button
                                    startIcon={<AddIcon />}
                                    type="button"
                                    onClick={handleOpenCUStaffWorkingLocation}
                                >
                                    Thêm mới
                                </Button>
                            </Tooltip>

                        </ButtonGroup>
                    </Grid>
                )}

                <Grid item xs={12}>
                    {isLoading ? (
                        <LoadingTabSkeleton />
                    ) : workingLocations && workingLocations.length > 0 ? (
                        <GlobitsTable
                            data={workingLocations}
                            columns={columns}
                            maxWidth="100%"
                            nonePagination
                            selection={false}
                        />
                    ) : (
                        <p className="w-100 text-center">Chưa có địa điểm làm việc nào</p>
                    )}
                </Grid>
            </Grid>


            {openCreateEditPopup && (
                <StaffWorkingLocationCUForm
                    staff={values}
                    staffId={values?.id}
                    onSaved={fetchWorkingLocations}
                />
            )}

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
        </>
    );
}

export default memo(observer(StaffWorkingLocations));
