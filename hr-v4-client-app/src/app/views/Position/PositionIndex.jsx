import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import PositionCUForm from "./PositionCUForm";
import PositionList from "./PositionList";
import PositionToolbar from "./PositionToolbar";
import PositionTransferForm from "./PositionTransferForm";
import StaffTransferForm from "./StaffTransferForm";
import DepartmentV2CUForm from "../DepartmentV2/DepartmentV2CUForm";

function PositionIndex() {
    const { positionStore, departmentV2Store, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingPosition,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        opentFormTransfer,
        opentStaffFormTransfer,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        openViewPopup,
    } = positionStore;

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;


    const { openCreateEditPopup: departmentV2StoreDepartment } = departmentV2Store;

    useEffect(() => {
        checkAllUserRoles();
        pagingPosition();
        return resetStore;
    }, []);

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.organization.title") },
                        { name: t("navigation.category.staff.listPositions") },
                    ]}
                />
            </div>
            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    {/* <TreeAutocompleteExample /> */}
                </Grid>
                <Grid item xs={12}>
                    <PositionToolbar />
                </Grid>
                <Grid item xs={12}>
                    <PositionList />
                </Grid>
            </Grid>

            {openCreateEditPopup && <PositionCUForm />}

            {openViewPopup && <PositionCUForm readOnly={true} />}

            {departmentV2StoreDepartment && <DepartmentV2CUForm />}

            {opentFormTransfer && <PositionTransferForm />}

            {opentStaffFormTransfer && <StaffTransferForm />}

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

export default memo(observer(PositionIndex));
