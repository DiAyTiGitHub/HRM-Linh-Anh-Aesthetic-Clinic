import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import PositionCUForm from "../Position/PositionCUForm";
import HrResourcePlanReportIndexToolbar from "./HrResourcePlanReportIndexToolbar";
import HrResourcePlanReportList from "./HrResourcePlanReportList";

export default observer(function HrResourcePlanReportIndex() {
    const { hrResourcePlanReportStore , positionStore, departmentStore, positionTitleStore} = useStore();
    const { t } = useTranslation();

    const {
        getDepartmentResourcePlan,
        openAggregateCreateEditPopup,
        openCreateEditPopup,
        openConfirmDeletePopup,
        openConfirmDeleteListPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        openViewPopup
    } = hrResourcePlanReportStore;

    const {
        openCreateEditPopup : openPositionForm,
    } = positionStore;
    
    const{selectedDepartment, handleSelectDepartment} = departmentStore;
    const {selectedPositionTitle, handleSelectPositionTitle} = positionTitleStore;
    useEffect(() => {
        getDepartmentResourcePlan();
    }, []);

    return (<div className='content-index'>
        <div className='index-breadcrumb py-6'>
            <GlobitsBreadcrumb
                routeSegments={[{ name: t("navigation.organization.title") }, { name: t("navigation.hrResourcePlan.title") },]}
            />
        </div>

        <Grid className='index-card' container spacing={2}>
            <Grid item xs={12}>
                <HrResourcePlanReportIndexToolbar />
            </Grid>

            <Grid item xs={12}>
                <HrResourcePlanReportList />
            </Grid>
        </Grid>

        {/* {openCreateEditPopup && <HrResourcePlanCUForm />}
        {openViewPopup && <HrResourcePlanCUForm readOnly={true} />}

        {openAggregateCreateEditPopup && <AggregateHrResourcePlanCUForm />} */}

        {openPositionForm && <PositionCUForm department={selectedDepartment} positionTitle={selectedPositionTitle}/>}
        {openConfirmDeletePopup && (<GlobitsConfirmationDialog
            open={openConfirmDeletePopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDelete}
            title={t("confirm_dialog.delete.title")}
            text={t("confirm_dialog.delete.text")}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
        />)}

        {openConfirmDeleteListPopup && (<GlobitsConfirmationDialog
            open={openConfirmDeleteListPopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDeleteList}
            title={t("confirm_dialog.delete_list.title")}
            text={t("confirm_dialog.delete_list.text")}
            agree={t("confirm_dialog.delete_list.agree")}
            cancel={t("confirm_dialog.delete_list.cancel")}
        />)}
    </div>);
});
