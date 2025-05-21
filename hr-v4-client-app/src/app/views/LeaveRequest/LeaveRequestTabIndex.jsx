import { Grid } from "@material-ui/core";
import { DoneAll, GroupWork, ThumbDown } from "@material-ui/icons";
import CloseIcon from "@material-ui/icons/Close";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import LeaveRequestListUpdatePopup from "./LeaveRequestListUpdatePopup";
import LeaveRequestForm from "./LeaveRequestForm";
import LeaveRequestList from "./LeaveRequestList";
import LeaveRequestIndexToolbar from "./LeaveRequestIndexToolbar";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";

const tabList = [
    { icon: <GroupWork fontSize='small' />, label: "Tất cả" },
    { icon: <CloseIcon fontSize='small' />, label: "Chưa duyệt" },
    { icon: <DoneAll fontSize='small' />, label: "Đã duyệt" },
    { icon: <ThumbDown fontSize='small' />, label: "Không duyệt" },
];

function LeaveRequestTabIndex() {
    const { leaveRequestStore, hrRoleUtilsStore, staffStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingLeaveRequest,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        handleChangePagingStatus,
        searchObject,
        openCreateEditPopup,
        setPageIndex,
        handleSelectListDelete,
        openConfirmUpdateStatusPopup,
        openViewPopup,
    } = leaveRequestStore;

    const { checkAllUserRoles, checkHasShiftAssignmentPermission } = hrRoleUtilsStore;

    const { getStaff, selectedStaff } = staffStore;

    async function handleChangeTabIndex(tabIndex) {
        handleChangePagingStatus(tabIndex);
        handleSelectListDelete([]);
        await setPageIndex(1);
    }

    const { id } = useParams();
    useEffect(() => {
        checkAllUserRoles();
        checkHasShiftAssignmentPermission();
        return resetStore;
    }, []);
    useEffect(() => {
        if (id) {
            getStaff(id);
        }
    }, [id]);

    return (
        <>
            <Grid container spacing={2}>
                <Grid item xs={12} className='index-card'>
                    <LeaveRequestIndexToolbar />
                </Grid>

                <Grid item xs={12} className='index-card'>
                    <TabsComponent
                        value={searchObject?.approvalStatus}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />

                    <LeaveRequestList />
                </Grid>
            </Grid>

            {openCreateEditPopup && <LeaveRequestForm />}
            {openViewPopup && <LeaveRequestForm readOnly={true} />}
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

            {openConfirmUpdateStatusPopup && <LeaveRequestListUpdatePopup />}
        </>
    );
}

export default memo(observer(LeaveRequestTabIndex));
