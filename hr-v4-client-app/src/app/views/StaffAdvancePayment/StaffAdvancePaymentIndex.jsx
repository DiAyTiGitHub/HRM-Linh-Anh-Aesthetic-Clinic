import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import StaffAdvancePaymentList from "./StaffAdvancePaymentList";
import StaffAdvancePaymentIndexToolbar from "./StaffAdvancePaymentIndexToolbar";
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import TabsComponent from "app/common/Tab/TabComponent";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import StaffSocialInsuranceChangePaidStatusPopup from "./StaffAdvancePaymentChangeApprovalStatusPopup";
import CloseIcon from '@material-ui/icons/Close';
import HourglassEmptyIcon from '@material-ui/icons/HourglassEmpty';
import DoneAll from '@material-ui/icons/DoneAll';
import StaffSocialInsuranceCUForm from "./StaffAdvancePaymentCUForm";
import { getInitialAdvancePaymentFilter, getInitialFilter } from "./StaffAdvancePaymentService";

const tabList = [
    { icon: <GroupWorkIcon fontSize="small" />, label: "Tất cả" },
    { icon: <HourglassEmptyIcon fontSize="small" />, label: "Chưa xét duyệt" },
    { icon: <DoneAll fontSize="small" />, label: "Đã duyệt" },
    { icon: <CloseIcon fontSize="small" />, label: "Không duyệt" },
];

function StaffAdvancePaymentIndex() {
    const { 
        staffAdvancePaymentStore, 
        hrRoleUtilsStore 

    } = useStore();

    const { t } = useTranslation();

    const {
        pagingStaffAdvancePayment,
        searchObject,
        resetStore,
        handleChangeViewStatus,
        handleSelectListDelete,
        setPageIndex,
        openConfirmDeletePopup,
        openConfirmDeleteListPopup,
        handleClose,
        handleConfirmDeleteList,
        handleConfirmDelete,
        openConfirmChangeStatus,
        openCreateEditPopup,
        openViewPopup,
        handleSetSearchObject
    } = staffAdvancePaymentStore;

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;


    async function initalizeScreen() {
        try {
            const { data } = await getInitialAdvancePaymentFilter();

            handleSetSearchObject(
                {
                    ...searchObject,
                    ...data
                }
            );

            await pagingStaffAdvancePayment();
        }
        catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        resetStore();

        checkAllUserRoles();
        initalizeScreen();

        return resetStore;
    }, []);

    async function handleChangeTabIndex(tabIndex) {
        handleChangeViewStatus(tabIndex);
        handleSelectListDelete([]);
        await setPageIndex(1);
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.salary") },
                        { name: t("navigation.advancePayment.title") }
                    ]} />
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className="index-card"> 
                    <StaffAdvancePaymentIndexToolbar />
                </Grid>

                <Grid item xs={12} className="index-card">
                    <TabsComponent
                        value={searchObject?.approvalStatus}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />

                    <StaffAdvancePaymentList />
                </Grid>
            </Grid>

            {openConfirmChangeStatus && (
                <StaffSocialInsuranceChangePaidStatusPopup />
            )}
            
            {openCreateEditPopup && <StaffSocialInsuranceCUForm />}

            {openViewPopup && <StaffSocialInsuranceCUForm readOnly={true} />}

            {
                openConfirmDeletePopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeletePopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )
            }

            {
                openConfirmDeleteListPopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeleteListPopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDeleteList}
                        title={t("confirm_dialog.delete_list.title")}
                        text={t("confirm_dialog.delete_list.text")}
                        agree={t("confirm_dialog.delete_list.agree")}
                        cancel={t("confirm_dialog.delete_list.cancel")}
                    />
                )
            }

        </div>
    );
}

export default memo(observer(StaffAdvancePaymentIndex));