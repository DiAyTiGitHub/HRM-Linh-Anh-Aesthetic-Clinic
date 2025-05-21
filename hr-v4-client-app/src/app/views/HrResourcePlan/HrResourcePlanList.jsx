import React, { memo, useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "../../LocalFunction";
import Brightness6Icon from '@material-ui/icons/Brightness6';
import Brightness7Icon from '@material-ui/icons/Brightness7';
import LocalConstants from "app/LocalConstants";
import CheckIcon from "@material-ui/icons/Check";

function HrResourcePlanList () {
  const {hrResourcePlanStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    hrResourcePlanList,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
    handleOpenView
  } = hrResourcePlanStore;
  const {
    isAdmin,
    checkAllUserRoles
  } = hrRoleUtilsStore;

  useEffect (() => {
    checkAllUserRoles ();
  }, []);

  const columns = [
    {
      title:t ("general.action"),
      width:"10%",
      render:(rowData) => (
          <div className="flex flex-center">
            <Tooltip
                arrow
                placement="top"
                title={"Xem chi tiết"}
            >
              <IconButton
                  className="ml-4"
                  size="small"
                  onClick={() => handleOpenView (rowData?.id)}
              >
                <Icon fontSize="small" style={{color:"green"}}>
                  remove_red_eye
                </Icon>
              </IconButton>

            </Tooltip>

            <Tooltip
                arrow
                title={"Cập nhật thông tin thành phần lương"}
                placement="top"
            >
              <IconButton size="small" onClick={() => handleOpenCreateEdit (rowData?.id)}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>


            <Tooltip
                arrow
                title={"Xóa thông tin thành phần lương"}
                placement="top"
            >
              <IconButton size="small" onClick={() => handleDelete (rowData)}>
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>

          </div>
      ),
    },
    {
      title:t ("Tên định biên"),
      field:"name",
      cellStyle:{
        width:"15%",
      },
      headerStyle:{
        width:"15%",
      },
      align:"left",
    },
    {
      title:t ("Mã định biên"),
      field:"code",
      cellStyle:{
        width:"15%",
      },
      headerStyle:{
        width:"15%",
      },
      align:"left",
    },
    // {
    //     title:t("Loại định biên") ,
    //     // field: "code",
    //     align:"left" ,
    //     cellStyle:{
    //         width:"15%" ,
    //     } ,
    //     headerStyle:{
    //         width:"15%" ,
    //     } ,
    //     render:(data) => data?.department ? "Định biên nhân viên" : "Định biên tổng hợp" ,

    // } ,
    {
      title:t ("Ngày lập định biên"),
      field:"planDate",
      align:"left",
      cellStyle:{
        width:"15%",
      },
      headerStyle:{
        width:"15%",
      },
      render:(data) => data?.planDate && <span>{formatDate ("DD/MM/YYYY", data?.planDate)}</span>,
    },

    {
      title:"Người tạo yêu cầu",
      field:"staff",
      align:"left",
      render:(row) => (
          <span>
                    {row?.requester?.displayName}
            {row?.requester?.staffCode && (
                <>
                  <br/>({row.requester.staffCode})
                </>
            )}
                </span>
      ),
      minWidth:"120px",
    },
    // {
    //   title:"Là vị tuyển lọc",
    //   field:"isTemporary",
    //   width:"10%",
    //   align:"center",
    //   minWidth:"150px",
    //   render:(data) => {
    //     if (data?.isTemporary) return <CheckIcon fontSize='small' style={{color:"green"}}/>;
    //     return "";
    //   },
    // },
    {
      title:"Trạng thái PTGĐ",
      field:"viceGeneralDirectorStatus",
      width:"10%",
      render:data => {

        return (
            <div className="flex justify-center w-100">
              {LocalConstants.HrResourcePlanApprovalStatus.fromValue (data?.viceGeneralDirectorStatus)?.name}
            </div>
        );

      }
    },

    {
      title:"Trạng thái TGĐ",
      field:"generalDirectorStatus",
      width:"10%",
      render:data => {

        return (
            <div className="flex justify-center w-100">
              {LocalConstants.HrResourcePlanApprovalStatus.fromValue (data?.generalDirectorStatus)?.name}
            </div>
        );

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

  return (
      <GlobitsTable
          selection
          data={hrResourcePlanList}
          handleSelectList={handleSelectListDelete}
          columns={columns}
          totalPages={totalPages}
          handleChangePage={handleChangePage}
          setRowsPerPage={setPageSize}
          pageSize={searchObject?.pageSize}
          pageSizeOption={[10, 15, 25, 50, 100]}
          totalElements={totalElements}
          page={searchObject?.pageIndex}
      />
  );
}

export default memo (observer (HrResourcePlanList));