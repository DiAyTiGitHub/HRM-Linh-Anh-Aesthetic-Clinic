import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import StaffSalaryTemplateList from "./StaffSalaryTemplateList";
import { Form, Formik } from "formik";
import AddIcon from "@material-ui/icons/Add";
import GlobitsTextField from "../../../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsConfirmationDialog from "../../../../../common/GlobitsConfirmationDialog";
import ListStaffSalaryTemplateForm from "./ListStaffSalaryTemplateForm";
import StaffSalaryTemplateForm from "./StaffSalaryTemplateForm";
import { DeleteOutline } from "@material-ui/icons";

function StaffSalaryTemplateIndex() {
    const { popupStaffSalaryTemplateStore, salaryTemplateStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingStaffSalaryTemplate,
        openCreate,
        openEdit,
        searchObject,
        handleOpenCreate,
        handleSetSearchObject,
        openConfirmDeletePopup,
        handleConfirmDelete,
        handleClose,
        handleDeleteList,
        listOnDelete,
        openConfirmDeleteListPopup,
        handleConfirmDeleteList,
        openViewPopup
    } = popupStaffSalaryTemplateStore;

    const {
        selectedSalaryTemplate,
        openViewPopup: readOnly,
    } = salaryTemplateStore;
    const {
        checkAllUserRoles,
        isAdmin,
        isManager,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    async function handleFilter(values) {
        await handleSetSearchObject(values)
        await pagingStaffSalaryTemplate();
    }

    useEffect(() => {
        checkAllUserRoles();

        const values = {
            ...searchObject,
            staffSalaryTemplateId: selectedSalaryTemplate?.id
        }

        handleSetSearchObject(values);
        
        pagingStaffSalaryTemplate();
    }, [selectedSalaryTemplate]);

    return (
        <div>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={searchObject}
                        onSubmit={handleFilter}
                    >
                        {({ resetForm, values, setFieldValue, setValues }) => {

                            return (
                                <Form autoComplete="off">
                                    <Grid item xs={12}>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12} md={6}>
                                                <ButtonGroup
                                                    color="container"
                                                    aria-label="outlined primary button group"
                                                >
                                                    {((isAdmin || isManager || isCompensationBenifit) && !readOnly) && (
                                                        <Tooltip placement="top"
                                                            title="Thêm mới nhân viên sử dụng mẫu bảng lương hiện tại">
                                                            <Button
                                                                disabled={selectedSalaryTemplate?.name == null}
                                                                startIcon={<AddIcon />}
                                                                onClick={() => handleOpenCreate()}

                                                            >
                                                                {t("general.button.add")}
                                                            </Button>
                                                        </Tooltip>
                                                    )}
                                                    {((isAdmin || isManager || isCompensationBenifit) && !readOnly) && (
                                                        <Tooltip placement="top"
                                                            title="Xóa nhân viên sử dụng mẫu bảng lương hiện tại">
                                                            <Button
                                                                disabled={listOnDelete?.length === 0}
                                                                startIcon={<DeleteOutline />}
                                                                onClick={() => handleDeleteList()}
                                                            >
                                                                {t("general.button.delete")}
                                                            </Button>
                                                        </Tooltip>
                                                    )}
                                                </ButtonGroup>
                                            </Grid>

                                            <Grid item xs={12} md={6}>
                                                <div className="flex justify-between align-center">
                                                    <Tooltip placement="top"
                                                        title="Tìm kiếm theo tên nhân viên">
                                                        <GlobitsTextField
                                                            placeholder="Tìm kiếm theo tên nhân viên"
                                                            name="keyword"
                                                            variant="outlined"
                                                            notDelay
                                                        />
                                                    </Tooltip>

                                                    <ButtonGroup
                                                        className="filterButtonV4"
                                                        color="container"
                                                        aria-label="outlined primary button group"
                                                    >
                                                        <Button
                                                            startIcon={<SearchIcon className={``} />}
                                                            className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                            type="submit"
                                                        >
                                                            Tìm kiếm
                                                        </Button>
                                                    </ButtonGroup>
                                                </div>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </Form>
                            );
                        }}
                    </Formik>
                </Grid>

                <Grid item xs={12}>
                    <StaffSalaryTemplateList />
                </Grid>
            </Grid>

            {openCreate && (
                <ListStaffSalaryTemplateForm />
            )}

            {openEdit && (<StaffSalaryTemplateForm />)}

            {openViewPopup && (<StaffSalaryTemplateForm readOnly={true} />)}

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
                    onConfirmDialogClose={handleClose}
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

export default memo(observer(StaffSalaryTemplateIndex));
