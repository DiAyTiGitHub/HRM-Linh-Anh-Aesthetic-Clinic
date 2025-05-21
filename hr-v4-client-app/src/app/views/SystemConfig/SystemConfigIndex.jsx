import { Button, ButtonGroup, Grid } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import DeleteIcon from "@material-ui/icons/Delete";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import SystemConfigList from "./SystemConfigList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import SystemConfigCUForm from "./SystemConfigCUForm";

function SystemConfigIndex() {
    const {
        systemConfigStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        handleSetSearchObject,
        handleOpenCreateEdit,
        searchObject,
        openCreateEditPopup,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        listChosen,
        handleDeleteList,
        openConfirmDeleteListPopup,
        handleConfirmDeleteList,
        resetStore,
        pagingSystemConfig,
        openViewPopup
    } = systemConfigStore;

    const {
        checkAllUserRoles,
        isAdmin,
        isManager

    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();

        pagingSystemConfig();

        return resetStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingSystemConfig();
    }

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: "Cấu hình hệ thống" }
                    ]}
                />
            </div>

            <Grid className='index-card' container spacing={2}>
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
                                        {(isManager || isAdmin) && (
                                            <ButtonGroup
                                                color="container"
                                                aria-label="outlined primary button group"
                                            >
                                                <Button
                                                    startIcon={<AddIcon />}
                                                    type="button"
                                                    onClick={() => handleOpenCreateEdit()}
                                                >
                                                    Thêm mới
                                                </Button>

                                                <Button
                                                    startIcon={<DeleteIcon />}
                                                    onClick={() => handleDeleteList()}
                                                    disabled={listChosen.length <= 0}
                                                >
                                                    {t("general.button.delete")}
                                                </Button>
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
                    <SystemConfigList />
                </Grid>
            </Grid>

            {openCreateEditPopup && <SystemConfigCUForm />}

            {openViewPopup && <SystemConfigCUForm readOnly={true} />}

            <GlobitsConfirmationDialog
                open={openConfirmDeletePopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDelete}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />

            <GlobitsConfirmationDialog
                open={openConfirmDeleteListPopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDeleteList}
                title={t("confirm_dialog.delete_list.title")}
                text={t("confirm_dialog.delete_list.text")}
                agree={t("confirm_dialog.delete_list.agree")}
                cancel={t("confirm_dialog.delete_list.cancel")}
            />
        </div>
    );
}

export default memo(observer(SystemConfigIndex));
