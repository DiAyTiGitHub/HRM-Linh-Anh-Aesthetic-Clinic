import { Button, ButtonGroup, Grid, Popover } from "@material-ui/core";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import PublishIcon from "@material-ui/icons/Publish";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import OrganizationalChartDataCUForm from "./OrganizationalChartDataCUForm";
import OrganizationalChartDataList from "./OrganizationalChartDataList";
import { Form, Formik } from "formik";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

function OrganizationalChartDataIndex() {
    const { organizationalChartDataStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();
    const [anchorEl, setAnchorEl] = React.useState(null);

    const open = Boolean(anchorEl);
    const id = open ? "simple-popper" : undefined;

    const {
        pagingOrgChartData,
        handleOpenCreateEdit,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        searchObject,
        listOnDelete,
        resetStore,
        handleSetSearchObject,
        handleDeleteList,
    } = organizationalChartDataStore;

    const {
        checkAllUserRoles,
        isAdmin
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
        pagingOrgChartData();
        return resetStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
            pageSize: searchObject.pageSize
        };
        handleSetSearchObject(newSearchObject);
        await pagingOrgChartData();
    }

    const handleClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.organization.title") },
                        { name: t("navigation.organizationalDirectory.title") },
                        { name: t("navigation.organization.diagramList") }
                    ]}
                />
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={searchObject}
                        onSubmit={handleFilter}
                    >
                        {({ resetForm, values, setFieldValue, setValues }) => (
                            <Form autoComplete="off">
                                <Grid container spacing={2}>
                                    <Grid item xs={12} md={6}>
                                        {(isAdmin) && (
                                            <ButtonGroup
                                                color="container"
                                                aria-label="outlined primary button group"
                                            >
                                                <Button
                                                    startIcon={<AddIcon />}
                                                    onClick={() => {
                                                        handleOpenCreateEdit();
                                                    }}
                                                >
                                                    {!isMobile && t("general.button.add")}
                                                </Button>
                                                <Button
                                                    disabled={listOnDelete?.length === 0}
                                                    startIcon={<DeleteOutlineIcon />}
                                                    onClick={handleDeleteList}
                                                >
                                                    {!isMobile && t("general.button.delete")}
                                                </Button>
                                                {/* <Button startIcon={<DashboardIcon />} aria-describedby={id} type="button" onClick={handleClick}>
              Khác
            </Button> */}
                                                <Popover
                                                    id={id}
                                                    open={open}
                                                    anchorEl={anchorEl}
                                                    onClose={handleClick}
                                                    anchorOrigin={{
                                                        vertical: 'bottom',
                                                        horizontal: 'right',
                                                    }}
                                                    transformOrigin={{
                                                        vertical: 'top',
                                                        horizontal: 'right',
                                                    }}
                                                >
                                                    <div className="menu-list-button">
                                                        <div className="menu-item-button">
                                                            <PublishIcon
                                                                style={{ fontSize: 16, transform: "rotate(180deg)" }}
                                                            />{" "}
                                                            Kết xuất danh sách
                                                        </div>
                                                        <div className="menu-item-button">
                                                            <PublishIcon style={{ fontSize: 16 }} /> Import dữ liệu
                                                        </div>
                                                    </div>
                                                </Popover>
                                            </ButtonGroup>
                                        )}
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <div className="flex justify-between align-center">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo từ khóa"
                                                name="keyword"
                                                variant="outlined"
                                                notDelay
                                            />
                                            <ButtonGroup
                                                className="filterButtonV4"
                                                color="container"
                                                aria-label="outlined primary button group"
                                            >
                                                <Button
                                                    startIcon={<SearchIcon />}
                                                    className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                    type="submit"
                                                >
                                                    Tìm kiếm
                                                </Button>
                                            </ButtonGroup>
                                        </div>
                                    </Grid>
                                </Grid>
                            </Form>
                        )}
                    </Formik>
                </Grid>
                <Grid item xs={12}>
                    <OrganizationalChartDataList />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <OrganizationalChartDataCUForm />
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

            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={() => {
                        pagingOrgChartData();
                        handleClose();
                    }}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}
        </div>
    );
}

export default memo(observer(OrganizationalChartDataIndex));
