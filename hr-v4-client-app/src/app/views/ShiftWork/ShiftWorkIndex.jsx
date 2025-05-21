/* eslint-disable react-hooks/exhaustive-deps */
import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import ShiftWorkForm from "./ShiftWorkForm";
import ShiftWorkList from "./ShiftWorkList";
import ShiftWorkToolbar from "./ShiftWorkToolbar";

function ShiftWorkIndex() {
  const {t} = useTranslation();
  const location = useLocation();
  const {hrRoleUtilsStore, shiftWorkStore} = useStore()

  const {
    selectedShiftWorkDeleted,
    shouldOpenConfirmationDeleteListDialog,
    handleConfirmDeleteList,
    search,
    onClosePopup,
    onDeletedShiftWork,
    openViewPopup,
    shouldOpenEditorDialog
  } = shiftWorkStore

  const {checkAllUserRoles} = hrRoleUtilsStore;

  useEffect(() => {
    // search();
    checkAllUserRoles();
  }, [location]);

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb routeSegments={[{name: t("shiftWork.title")}]}/>
      </div>

      <Grid className="mb-8 index-card" container spacing={2}>
        <Grid item xs={12}>
          <ShiftWorkToolbar/>
        </Grid>

        <Grid item xs={12}>
          <ShiftWorkList/>
        </Grid>
      </Grid>


      {shouldOpenEditorDialog && (
        <ShiftWorkForm/>
      )}
      {openViewPopup && <ShiftWorkForm readOnly={true}/>}
      <GlobitsConfirmationDialog
        open={Boolean(selectedShiftWorkDeleted)}
        onConfirmDialogClose={onClosePopup}
        onYesClick={onDeletedShiftWork}
        title={t("confirm_dialog.delete.title")}
        text={t("confirm_dialog.delete.text")}
        agree={t("confirm_dialog.delete.agree")}
        cancel={t("confirm_dialog.delete.cancel")}
      />

      <GlobitsConfirmationDialog
        open={shouldOpenConfirmationDeleteListDialog}
        onConfirmDialogClose={onClosePopup}
        onYesClick={handleConfirmDeleteList}
        title={t("confirm_dialog.delete_list.title")}
        text={t("confirm_dialog.delete_list.text")}
        agree={t("confirm_dialog.delete_list.agree")}
        cancel={t("confirm_dialog.delete_list.cancel")}
      />
    </div>
  );
};

export default observer(ShiftWorkIndex);