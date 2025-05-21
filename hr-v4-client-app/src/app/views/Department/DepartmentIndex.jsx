import {Grid} from "@material-ui/core";
import {useTheme} from "@material-ui/core/styles";
import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import {useStore} from "../../stores";
import DepartmentForm from "./DepartmentForm";
import DepartmentList from "./DepartmentList";
import DepartmentToolbar from "./DepartmentToolbar";

function DepartmentIndex() {
    const {departmentStore} = useStore();
    const {t} = useTranslation();

    const {
        updatePageData,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        shouldOpenConfirmationDeleteListDialog,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
    } = departmentStore;

    useEffect(() => {
        resetStore();

        updatePageData();
    }, [updatePageData]);
    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[
                        {name: t("navigation.category.title")},
                        {name: t("navigation.category.staff.title")},
                        {name: t("navigation.category.staff.departments")},
                    ]}
                />
            </div>

            <Grid className="index-card" container spacing={2}>
                <DepartmentToolbar/>
                {shouldOpenEditorDialog && (
                    <DepartmentForm open={shouldOpenEditorDialog}/>
                )}
                <GlobitsConfirmationDialog
                    open={shouldOpenConfirmationDialog}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />

                <GlobitsConfirmationDialog
                    open={shouldOpenConfirmationDeleteListDialog}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />

                <Grid item xs={12}>
                    <DepartmentList/>
                </Grid>
            </Grid>
        </div>
    );
}

export default memo(observer(DepartmentIndex));
