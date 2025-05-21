import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import StaffDocumentItemToolbar from "./StaffDocumentItemToolbar";
import StaffDocumentItemList from "./StaffDocumentItemList";
import StaffDocumentItemCUForm from "./StaffDocumentItemCUForm";

function StaffDocumentItemIndex () {
  const {staffDocumentItemStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    openViewPopup,
    pagingStaffDocumentItem,
    resetStore,
  } = staffDocumentItemStore;
  const {checkAllUserRoles} = hrRoleUtilsStore
  useEffect (() => {
    checkAllUserRoles ();
    pagingStaffDocumentItem ()

    return resetStore
  }, []);
  return (
      <div className="content-index">
        <div className="index-breadcrumb py-6">
          <GlobitsBreadcrumb routeSegments={[
            {name:"Nhân viên"},
            {name:t ("navigation.staff.staffDocumentItem")}
          ]}/>
        </div>
        <Grid className="index-card" container spacing={2}>
          <Grid item xs={12}>
            <StaffDocumentItemToolbar/>
          </Grid>

          <Grid item xs={12}>
            <StaffDocumentItemList/>
          </Grid>
        </Grid>

        {openCreateEditPopup && (
            <StaffDocumentItemCUForm/>
        )}
        {openViewPopup && (
            <StaffDocumentItemCUForm readOnly={true}/>
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

export default memo (observer (StaffDocumentItemIndex));
