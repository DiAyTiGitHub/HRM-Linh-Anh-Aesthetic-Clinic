import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import DepartmentV2CUForm from "./DepartmentV2CUForm";
import DepartmentV2List from "./DepartmentV2List";
import DepartmentV2Toolbar from "./DepartmentV2Toolbar";

function DepartmentV2Index() {
    const { departmentV2Store } = useStore();
    const { t } = useTranslation();

    const {
        pagingAllDepartment,
        openCreateEditPopup,
        openConfirmDeletePopup,
        openConfirmDeleteListPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        openViewPopup
    } = departmentV2Store;

    useEffect(() => {
        pagingAllDepartment();
        return resetStore;
    }, []);

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.organization.title") },
                        { name: t("navigation.organizationalDirectory.title") },
                        { name: t("navigation.category.staff.departments") }
                    ]}
                />
            </div>

            {/* <TreeAutocompleteExample /> */}
            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <DepartmentV2Toolbar />
                </Grid>

                <Grid item xs={12}>
                    <DepartmentV2List />
                </Grid>
            </Grid>

            {openCreateEditPopup && <DepartmentV2CUForm />}

            {openViewPopup && <DepartmentV2CUForm readOnly={true} />}


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

export default memo(observer(DepartmentV2Index));
