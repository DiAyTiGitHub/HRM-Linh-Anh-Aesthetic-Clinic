import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import StaffLabourAgreementList from "./StaffLabourAgreementList";
import StaffLabourAgreementCUForm from "./StaffLabourAgreementCUForm";
import StaffLabourAgreementIndexToolbar from "./StaffLabourAgreementIndexToolbar";
import { DoneAll, GroupWork, ThumbDown } from "@material-ui/icons";
import CloseIcon from "@material-ui/icons/Close";
import HighlightOffOutlinedIcon from "@material-ui/icons/HighlightOffOutlined";
import TabsComponent from "../../common/Tab/TabComponent";
import StaffLabourAgreementOverdueContract from "./StaffLabourAgreementOverdueContract";
import LabourAgreementViewer from "./LabourAgreementViewer";

const tabList = [
    { icon: <GroupWork fontSize='small' />, label: "Tất cả" },
    { icon: <CloseIcon fontSize='small' />, label: "Hợp đồng chưa được ký" },
    { icon: <DoneAll fontSize='small' />, label: "Hợp đồng đã được ký" },
    { icon: <HighlightOffOutlinedIcon fontSize='small' />, label: "Đã chấm dứt" },
];

function StaffLabourAgreementIndex() {
    const {
        staffLabourAgreementStore,
        hrRoleUtilsStore
    } = useStore();

    const { t } = useTranslation();

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;

    const {
        pagingStaffLabourAgreement,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        handleChangePagingStatus,
        handleCheckHasOverdueContract,
        openCreateEditPopupOverdueContract,
        searchObject,
        selectedStaffLabourAgreement,
        openPreviewPopup,
    } = staffLabourAgreementStore;

    useEffect(() => {

        checkAllUserRoles();
        pagingStaffLabourAgreement();
        // handleCheckHasOverdueContract();
        return resetStore;
    }, []);

    async function handleChangeTabIndex(tabIndex) {
        await handleChangePagingStatus(tabIndex);
    }

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[{ name: "Nhân viên" }, { name: t("navigation.staff.staffLabourAgreement") }]}
                />
            </div>
            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <StaffLabourAgreementIndexToolbar />
                </Grid>

                <Grid item xs={12}>
                    <TabsComponent
                        value={searchObject?.agreementStatus}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />
                    <StaffLabourAgreementList />
                </Grid>
            </Grid>

            {openCreateEditPopup && <StaffLabourAgreementCUForm />}

            {openCreateEditPopupOverdueContract && <StaffLabourAgreementOverdueContract />}

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
            
            {openPreviewPopup && <LabourAgreementViewer id={selectedStaffLabourAgreement?.id} />}
        </div>
    );
}

export default memo(observer(StaffLabourAgreementIndex));
