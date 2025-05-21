import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import HrIntroduceCostToolbar from "./HrIntroduceCostToolbar";
import HrIntroduceCostList from "./HrIntroduceCostList";
import HrIntroduceCostCUForm from "./HrIntroduceCostCUForm";
import { getFullYear, getMonth } from "../../LocalFunction";

function HrIntroduceCostIndex () {
  const {hrIntroduceCostStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    openViewPopup,
    pagingHrIntroduceCost,
    handleSetSearchObject,
    searchObject
  } = hrIntroduceCostStore;
  const {checkAllUserRoles} = hrRoleUtilsStore
  const innitSearchObject = async () => {
    handleSetSearchObject ({
      ... searchObject,
      month:getMonth (new Date ()) + 1,
      year:getFullYear (new Date ()),
    })
    await checkAllUserRoles ();
    await pagingHrIntroduceCost ()
  }

  useEffect (() => {
    innitSearchObject ();
  }, []);
  return (
      <div className="content-index">
        <div className="index-breadcrumb py-6">
          <GlobitsBreadcrumb routeSegments={[
            {name:"Nhân viên"},
            {name:t ("navigation.staff.introduceCost")}
          ]}/>
        </div>
        <Grid className="index-card" container spacing={2}>
          <Grid item xs={12}>
            <HrIntroduceCostToolbar/>
          </Grid>

          <Grid item xs={12}>
            <HrIntroduceCostList/>
          </Grid>
        </Grid>

        {openCreateEditPopup && (
            <HrIntroduceCostCUForm/>
        )}
        {openViewPopup && (
            <HrIntroduceCostCUForm readOnly={true}/>
        )}


        {openConfirmDeletePopup && (
            <GlobitsConfirmationDialog
                open={openConfirmDeletePopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDelete}
                title={t ("confirm_dialog.delete.title")}
                text={t ("confirm_dialog.delete.text")}
                agree={t ("confirm_dialog.delete.agree")}
                cancel={t ("confirm_dialog.delete.cancel")}
            />
        )}

        {openConfirmDeleteListPopup && (
            <GlobitsConfirmationDialog
                open={openConfirmDeleteListPopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDeleteList}
                title={t ("confirm_dialog.delete_list.title")}
                text={t ("confirm_dialog.delete_list.text")}
                agree={t ("confirm_dialog.delete_list.agree")}
                cancel={t ("confirm_dialog.delete_list.cancel")}
            />
        )}
      </div>
  );
}

export default memo (observer (HrIntroduceCostIndex));
