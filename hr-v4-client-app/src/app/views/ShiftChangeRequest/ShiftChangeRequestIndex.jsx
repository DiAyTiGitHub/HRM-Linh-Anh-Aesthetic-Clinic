import { Grid } from "@material-ui/core";
import { DoneAll, GroupWork, ThumbDown } from "@material-ui/icons";
import CloseIcon from '@material-ui/icons/Close';
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import ShiftChangeRequestListUpdatePopup from "./ShiftChangeRequestListUpdatePopup";
import ShiftChangeRequestForm from "./ShiftChangeRequestForm";
import ShiftChangeRequestList from "./ShiftChangeRequestList";
import ShiftChangeRequestIndexToolbar from "./ShiftChangeRequestIndexToolbar";
import { getInitialShiftChangeRequestFilter } from "./ShiftChangeRequestService";

const tabList = [
  {icon:<GroupWork fontSize="small"/>, label:"Tất cả"},
  {icon:<CloseIcon fontSize="small"/>, label:"Chưa duyệt"},
  {icon:<DoneAll fontSize="small"/>, label:"Đã duyệt"},
  {icon:<ThumbDown fontSize="small"/>, label:"Không duyệt"}
];

function ShiftChangeRequestIndex () {
  const {shiftChangeRequestStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    openViewPopup,
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
    pagingShiftChangeRequest,
    handleSetSearchObject
  } = shiftChangeRequestStore;

  const {checkAllUserRoles} = hrRoleUtilsStore;

  async function initalizeScreen () {
    try {
      const {data} = await getInitialShiftChangeRequestFilter ();

      handleSetSearchObject ({
        ... searchObject,
        ... data
      });
      await pagingShiftChangeRequest ();
    } catch (error) {
      console.error (error);
    }
  }


  useEffect (() => {
    checkAllUserRoles ();
    initalizeScreen ();
    return resetStore;
  }, []);

  async function handleChangeTabIndex (tabIndex) {
    handleChangePagingStatus (tabIndex);
    handleSelectListDelete ([]);
    await setPageIndex (1);
  }

  return (
      <div className="content-index">
        <div className="index-breadcrumb py-6">
          <GlobitsBreadcrumb
              routeSegments={[
                {name:t ("navigation.timeKeeping.title")},
                {name:t ("navigation.shiftChangeRequest.title")},
              ]}
          />
        </div>

        <Grid container spacing={2}>
          <Grid item xs={12} className="index-card">
            <ShiftChangeRequestIndexToolbar/>
          </Grid>

          <Grid item xs={12} className="index-card">
            <TabsComponent
                value={searchObject?.approvalStatus}
                handleChange={(_, tabIndex) => handleChangeTabIndex (tabIndex)}
                tabList={tabList}
            />

            <ShiftChangeRequestList/>
          </Grid>
        </Grid>

        {openCreateEditPopup && (
            <ShiftChangeRequestForm/>
        )}
        {openViewPopup && (
            <ShiftChangeRequestForm readOnly={true}/>
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

        {openConfirmUpdateStatusPopup && (
            <ShiftChangeRequestListUpdatePopup/>
        )}
      </div>
  );
}

export default memo (observer (ShiftChangeRequestIndex));
