import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import AggregateHrResourcePlanCUForm from "./AggregateHrResourcePlanCUForm";
import HrResourcePlanCUForm from "./HrResourcePlanCUForm";
import HrResourcePlanIndexToolbar from "./HrResourcePlanIndexToolbar";
import HrResourcePlanList from "./HrResourcePlanList";
import HrResourcePlanConfirmStatusPopup from "./HrResourcePlanPopup/HrResourcePlanConfirmStatusPopup.jsx";

function HrResourcePlanIndex () {
  const {hrResourcePlanStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    pagingHrResourcePlan,
    openAggregateCreateEditPopup,
    openCreateEditPopup,
    openConfirmDeletePopup,
    openConfirmDeleteListPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    openViewPopup,
    openConfirmStatusPopup,
  } = hrResourcePlanStore;

  const {
    checkAllUserRoles
  } = hrRoleUtilsStore;

  useEffect (() => {
    checkAllUserRoles ();
    pagingHrResourcePlan ();
  }, []);

  return (<div className='content-index'>
    <div className='index-breadcrumb py-6'>
      <GlobitsBreadcrumb
          routeSegments={[{name:t ("navigation.organization.title")}, {name:t ("Yêu cầu định biên")},]}
      />
    </div>

    <Grid className='index-card' container spacing={2}>
      <Grid item xs={12}>
        <HrResourcePlanIndexToolbar/>
      </Grid>

      <Grid item xs={12}>
        <HrResourcePlanList/>
      </Grid>
    </Grid>

    {openCreateEditPopup && <HrResourcePlanCUForm/>}

    {openViewPopup && <HrResourcePlanCUForm readOnly={true}/>}

    {openAggregateCreateEditPopup && <AggregateHrResourcePlanCUForm/>}


    {openConfirmDeletePopup && (<GlobitsConfirmationDialog
        open={openConfirmDeletePopup}
        onConfirmDialogClose={handleClose}
        onYesClick={handleConfirmDelete}
        title={t ("confirm_dialog.delete.title")}
        text={t ("confirm_dialog.delete.text")}
        agree={t ("confirm_dialog.delete.agree")}
        cancel={t ("confirm_dialog.delete.cancel")}
    />)}

    {openConfirmDeleteListPopup && (<GlobitsConfirmationDialog
        open={openConfirmDeleteListPopup}
        onConfirmDialogClose={handleClose}
        onYesClick={handleConfirmDeleteList}
        title={t ("confirm_dialog.delete_list.title")}
        text={t ("confirm_dialog.delete_list.text")}
        agree={t ("confirm_dialog.delete_list.agree")}
        cancel={t ("confirm_dialog.delete_list.cancel")}
    />)}

    {openConfirmStatusPopup && (
        <HrResourcePlanConfirmStatusPopup/>
    )}

  </div>);
}


export default memo (observer (HrResourcePlanIndex));