import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid, Button, IconButton, ButtonGroup } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import SalaryItemV2CUForm from "app/views/Salary/SalaryItemV2/SalaryItemV2CUForm";
import SalaryItemV2List from "app/views/Salary/SalaryItemV2/SalaryItemV2List";
import SalaryItemIndexToolbar from "app/views/Salary/SalaryItemV2/SalaryItemIndexToolbar";
import { useFormikContext } from "formik";
import LocalConstants from "app/LocalConstants";
import TemplateSalaryItemList from "./TemplateSalaryItemList";


function ResultItemsViewCRUDItem() {
    const { t } = useTranslation(); 

    const { salaryItemStore } = useStore();

    const {
        pagingSalaryItem,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
    } = salaryItemStore;

    // useEffect(() => {
    //     pagingSalaryItem();

    //     return resetStore;
    // }, []);


    return (
        <>
            <div className="p-12">
                <Grid container spacing={2}>
                    <Grid item xs={12} >
                        <SalaryItemIndexToolbar />
                    </Grid>

                    <Grid item xs={12}>
                        <TemplateSalaryItemList />
                    </Grid>
                </Grid>
            </div>

            {openCreateEditPopup && (
                <SalaryItemV2CUForm />
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
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}
        </>
    );
}

export default memo(observer(ResultItemsViewCRUDItem));