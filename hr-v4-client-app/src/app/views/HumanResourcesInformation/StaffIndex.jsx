/* eslint-disable react-hooks/exhaustive-deps */
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import StaffList from "./StaffList";
import StaffListCreateUsers from "./StaffListCreateUsers";
import StaffToolbar from "./StaffToolbar";
import StaffTransferForm from "./StaffTransferForm";
import UserForm from "./TabContainer/Popup/UserForm";
import RemoveStaffFromPosition from "./RemoveStaffFromPosition";
import StaffCreateForm from "./StaffCreateForm";
import StaffFixShiftDateRangePopup from "./StaffFixShiftDateRangePopup";
import { getInitialStaffFilter } from "./StaffService";

function StaffIndex(props) {
    const { t } = useTranslation();
    let query = useLocation();

    const {
        staffStore,
        hrRoleUtilsStore
    } = useStore();

    const {
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        onPagingStaff,
        shouldOpenFormCreate,
        shouldOpenCreateUsers,
        handleCreateUsersForStaff,
        shouldOpenCreateUserDialog,
        opentFormLeavePosition,
        openFixShiftDateRangePopup,
        handleSetUsingStaffSO,
        searchStaff
    } = staffStore;

    const {
        checkAllUserRoles,
    } = hrRoleUtilsStore;

    async function initalizeScreen() {
        try {
            const { data } = await getInitialStaffFilter();

            handleSetUsingStaffSO(
                {
                    ...searchStaff,
                    ...data
                }
            );

            await onPagingStaff();
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        checkAllUserRoles();
        initalizeScreen();

    }, [query]);

    return (
        <section className='staff-root flex-column'>
            <div className='px-25 bg-white'>
                <GlobitsBreadcrumb
                    routeSegments={[{ name: t("staff.title") }, { name: t("navigation.staff.staffManagement") }]}
                />
            </div>
            <Grid container spacing={2} className='p-12 h-100 pb-48'>
                <Grid
                    item
                    xs={12}
                    //  lg={9}
                    className='h-100'>
                    <div className='bg-white p-12 h-100 overflow-auto'>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <StaffToolbar />
                            </Grid>

                            <Grid item xs={12}>
                                <StaffList />
                            </Grid>
                        </Grid>
                    </div>
                </Grid>
            </Grid>

            {shouldOpenFormCreate && (
                <StaffCreateForm />
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
                open={shouldOpenCreateUsers}
                onConfirmDialogClose={handleClose}
                onYesClick={handleCreateUsersForStaff}
                title={t("confirm_dialog.createUser.title")}
                text={t("confirm_dialog.createUser.text")}
                agree={t("confirm_dialog.createUser.agree")}
                cancel={t("confirm_dialog.createUser.cancel")}
            />

            <StaffTransferForm />
            
            <StaffListCreateUsers />

            {shouldOpenCreateUserDialog && <UserForm />}

            {opentFormLeavePosition && <RemoveStaffFromPosition />}

            {openFixShiftDateRangePopup && (
                <StaffFixShiftDateRangePopup />
            )}

        </section>
    );
}

export default memo(observer(StaffIndex));
