import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import AdministrativeToolbar from "./AdministrativeToolbar";
import AdministrativeUnitForm from "./AdministrativeUnitForm";
import AdministrativeUnitList from "./AdministrativeUnitList";

export default observer(function EthnicsIndex() {
    const { administrativeUnitStore } = useStore();
    const { t } = useTranslation();

    const {
        search,
        handleSetSearchObject,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
        resetAdministrativeStore,
    } = administrativeUnitStore;

    useEffect(() => {
        return resetAdministrativeStore;
    }, []);


    return (<div className="content-index">
        <div className="index-breadcrumb">
            <GlobitsBreadcrumb
                routeSegments={[{ name: t("administrativeUnit.title") }]}
            />
        </div>
        <Grid className="index-card" container spacing={3}>

            <Grid item lg={12} md={12} sm={12} xs={12}>
                <AdministrativeToolbar />
            </Grid>
            {shouldOpenEditorDialog && <AdministrativeUnitForm />}
            {/*<ImportExcelDialog*/}
            {/*    open={shouldOpenImportDialog}*/}
            {/*    t={t}*/}
            {/*    handleClose={() => handleClose()}*/}
            {/*/>*/}

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

            <Grid className="list-container" item xs={12}>
                <Grid item xs={12}>
                    <AdministrativeUnitList />
                </Grid>
            </Grid>
        </Grid>
    </div>);
});
