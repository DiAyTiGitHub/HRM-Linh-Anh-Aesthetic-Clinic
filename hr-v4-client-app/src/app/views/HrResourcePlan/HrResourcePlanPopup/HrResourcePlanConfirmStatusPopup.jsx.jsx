import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';

function HrResourcePlanConfirmStatusPopup () {
  const {
    hrResourcePlanStore

  } = useStore ();

  const {t} = useTranslation ();

  const {
    openConfirmStatusPopup,
    handleClose,
    handleConfirmUpdateStatus,
    listOnDelete,
    handleSelectlistOnDelete,
    pagingHrResourcePlan,
    onUpdateStatus
  } = hrResourcePlanStore;

  const columns = [{
    title:t ("Mã định biên"), field:"code", cellStyle:{
      width:"15%",
    }, headerStyle:{
      width:"15%",
    }, align:"left",
  },

    {
      title:t ("Tên định biên"), field:"name", cellStyle:{
        width:"15%",
      }, headerStyle:{
        width:"15%",
      }, align:"left",
    },

    {
      title:t ("Ngày lập định biên"), field:"planDate", align:"left", cellStyle:{
        width:"15%",
      }, headerStyle:{
        width:"15%",
      }, render:(data) => data?.planDate && <span>{formatDate ("DD/MM/YYYY", data?.planDate)}</span>,
    },

    {
      title:"Người tạo yêu cầu", field:"staff", align:"left", render:(row) => (<span>
                    {row?.requester?.displayName}
        {row?.requester?.staffCode && (<>
          <br/>({row.requester.staffCode})
        </>)}
                </span>), minWidth:"120px",
    },

    {
      title:"Trạng thái PTGĐ", field:"viceGeneralDirectorStatus", width:"10%", render:data => {

        return (<div className="flex justify-center w-100">
          {LocalConstants.HrResourcePlanApprovalStatus.fromValue (data?.viceGeneralDirectorStatus)?.name}
        </div>);

      }
    },

    {
      title:"Trạng thái TGĐ", field:"generalDirectorStatus", width:"10%", render:data => {

        return (<div className="flex justify-center w-100">
          {LocalConstants.HrResourcePlanApprovalStatus.fromValue (data?.generalDirectorStatus)?.name}
        </div>);

      }
    },

    // {
    //     title: t("Mô tả"),
    //     field: "description",
    //     align: "left",
    //     cellStyle: {
    //         width: "30%",
    //     },
    //     headerStyle: {
    //         width: "30%",
    //     },
    // },
  ];

  function handleCloseConfirmPopup () {
    handleClose ();
    pagingHrResourcePlan ();
  }

  return (<GlobitsColorfulThemePopup
      open={openConfirmStatusPopup}
      handleClose={handleCloseConfirmPopup}
      size="lg"
      onConfirm={handleConfirmUpdateStatus}
  >
    <div className="dialog-body">
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <div className="dialogScrollContent">
            <h6 className="text-red">
              <strong>
                {listOnDelete?.length <= 1? "Thông tin " : "Danh sách "}
                yêu cầu được đổi trạng thái
                {` ${LocalConstants.HrResourcePlanApprovalStatus.fromValue (onUpdateStatus)?.name}`}
              </strong>
            </h6>

            <GlobitsTable
                data={listOnDelete}
                handleSelectList={handleSelectlistOnDelete}
                columns={columns}
                nonePagination
            />
          </div>
        </Grid>
      </Grid>
    </div>
  </GlobitsColorfulThemePopup>);
}

export default memo (observer (HrResourcePlanConfirmStatusPopup));