import { Grid } from "@material-ui/core";
import { DoneAll, GroupWork } from "@material-ui/icons";
import CloseIcon from "@material-ui/icons/Close";
import HourglassEmptyIcon from "@material-ui/icons/HourglassEmpty";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import StaffWorkScheduleV2CUForm from "app/views/StaffWorkScheduleV2/StaffWorkScheduleV2CUForm";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import ConfirmStaffWorkScheduleList from "./ConfirmStaffWorkScheduleList";
import ConfirmStaffWorkSchedulePopup from "./Popup/ConfirmStaffWorkSchedulePopup";
import ConfirmStaffWorkScheduleIndexToolbar from "./SearchComponent/ConfirmStaffWorkScheduleIndexToolbar";
import { getInitialOvertimeRequestFilter } from "./ConfirmStaffWorkScheduleService";

const tabList = [{icon:<GroupWork fontSize='small'/>, label:"Tất cả"}, {
  icon:<HourglassEmptyIcon fontSize='small'/>,
  label:"Chưa duyệt"
}, {icon:<DoneAll fontSize='small'/>, label:"Đã duyệt"}, {icon:<CloseIcon fontSize='small'/>, label:"Không duyệt"},];

function ConfirmStaffWorkScheduleIndex () {
  const {confirmStaffWorkScheduleStore, hrRoleUtilsStore, staffWorkScheduleStore} = useStore ();
  const {t} = useTranslation ();

  const {
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,
    handleChangePagingStatus,
    searchObject,
    setPageIndex,
    handleSelectListDelete,
    openConfirmUpdateStatusPopup,
    pagingOvertimeRequest,
    handleSetSearchObject,
  } = confirmStaffWorkScheduleStore;

  const {openViewPopup} = staffWorkScheduleStore;

  const {checkAllUserRoles} = hrRoleUtilsStore;

  async function initalizeScreen () {
    try {
      const {data} = await getInitialOvertimeRequestFilter ();

      handleSetSearchObject ({
        ... searchObject, ... data
      });
      await checkAllUserRoles ()
      await pagingOvertimeRequest ();

    } catch (error) {
      console.error (error);
    }
  }

  useEffect (() => {
    initalizeScreen ();
    return resetStore
  }, []);

  async function handleChangeTabIndex (tabIndex) {
    handleChangePagingStatus (tabIndex);
    handleSelectListDelete ([]);
    await setPageIndex (1);
  }

  return (<div className='content-index'>
        <div className='index-breadcrumb py-6'>
          <GlobitsBreadcrumb
              routeSegments={[{name:t ("navigation.timeKeeping.title")}, {name:t ("navigation.confirmStaffworkSchedule.title")},]}
          />
        </div>

        <Grid container spacing={2}>
          <Grid item xs={12} className='index-card'>
            <ConfirmStaffWorkScheduleIndexToolbar/>
          </Grid>

          <Grid item xs={12} className='index-card'>
            <TabsComponent
                value={searchObject?.approvalStatus}
                handleChange={(_, tabIndex) => handleChangeTabIndex (tabIndex)}
                tabList={tabList}
            />

            <ConfirmStaffWorkScheduleList/>
          </Grid>
        </Grid>

        {openViewPopup && <StaffWorkScheduleV2CUForm readOnly={true}/>}

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

        {openConfirmUpdateStatusPopup && <ConfirmStaffWorkSchedulePopup/>}
      </div>);
}

export default memo (observer (ConfirmStaffWorkScheduleIndex));
