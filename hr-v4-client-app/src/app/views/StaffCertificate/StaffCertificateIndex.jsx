import { Button, ButtonGroup, Grid } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import DeleteIcon from "@material-ui/icons/Delete";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import StaffCertificateList from "./StaffCertificateList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import StaffCertificateCUForm from "./StaffCertificateCUForm";
import StaffCertificateToolbar from "./StaffCertificateToolbar";
import { getInitialPersonCertificateFilter } from "../HumanResourcesInformation/PersonCertificate/PersonCertificateService";

function StaffCertificateIndex() {
    const {
        staffCertificateStore,
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
        pagingPersonCertificate,
        openViewPopup
    } = staffCertificateStore;

    const {
        checkAllUserRoles,
        isAdmin,
        isManager

    } = hrRoleUtilsStore;

    async function initalizeScreen() {
        try {
            const { data } = await getInitialPersonCertificateFilter();

            handleSetSearchObject({
                ...searchObject,
                ...data
            });

            await pagingPersonCertificate();
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        resetStore();

        checkAllUserRoles();
        initalizeScreen();


        return resetStore;
    }, []);


    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: "Chứng chỉ nhân viên" }
                    ]}
                />
            </div>

            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <StaffCertificateToolbar />
                </Grid>

                <Grid item xs={12}>
                    <StaffCertificateList />
                </Grid>
            </Grid>

            {openCreateEditPopup && <StaffCertificateCUForm />}

            {openViewPopup && <StaffCertificateCUForm readOnly={true} />}

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

export default memo(observer(StaffCertificateIndex));
