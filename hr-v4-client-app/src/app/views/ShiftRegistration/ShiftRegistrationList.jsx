import React, { memo, useState } from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { formatDate, getDate } from "app/LocalFunction";
import { useHistory } from "react-router-dom/cjs/react-router-dom";
import LocalConstants from "app/LocalConstants";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import { DateRangeSharp } from "@material-ui/icons";

function ShiftRegistrationList() {
  const history = useHistory();
  const { t } = useTranslation();

  const {
    ShiftRegistrationStore,
    hrRoleUtilsStore
  } = useStore();

  const {
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    listShiftRegistrations,
    handleSelectListDelete,
    handleOpenCreateEdit,
    handleOpenConfirmUpdateStatusPopup,
    handleCreateWorkShift
  } = ShiftRegistrationStore;

  const columns = [
    {
      title: t("general.action"),
      width: "10%",
      align: "center",
      render: (rowData) => {
        return (
          <div className="flex flex-middle justify-center">
            {rowData?.approvalStatus === LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value && (
              <Tooltip title="Cập nhật" placement="top">
                <IconButton size="small" onClick={function () {
                  handleOpenCreateEdit(rowData);
                }}>
                  <Icon fontSize="small" color="primary">
                    edit
                  </Icon>
                </IconButton>
              </Tooltip>
            )}

            {rowData?.approvalStatus === LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value && (
              <Tooltip title="Xóa" placement="top">
                <IconButton className="ml-8" size="small" onClick={() => handleDelete(rowData)}>
                  <Icon fontSize="small" color="secondary">
                    delete
                  </Icon>
                </IconButton>
              </Tooltip>
            )}

            {/* {rowData?.approvalStatus === LocalConstants.ShiftRegistrationApprovalStatus.APPROVED.value && (
              <Tooltip title="Phân ca làm việc" placement="top">
                <IconButton className="ml-8" size="small" onClick={() => handleCreateWorkShift(rowData)}>
                  <DateRangeSharp fontSize="small" color="secondary">
                    Phân ca làm việc
                  </DateRangeSharp>
                </IconButton>
              </Tooltip>
            )} */}

            {rowData?.approvalStatus === LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value && (
              <Tooltip title="Thao tác khác" placement="top">
                <IconButton
                  className="ml-8"
                  size="small"
                  onClick={(event) => {
                    setSelectedRow(rowData);
                    setAnchorEl(event?.currentTarget);
                  }}
                >
                  <MoreHorizIcon />
                </IconButton>
              </Tooltip>
            )}

          </div>
        );
      },
    },
    {
      title: "Nhân viên đăng ký",
      width: "10%",
      field: "registerStaff.displayName",
      align: "left",
      render: function (rowData) {
        return (
          <>
            {rowData?.registerStaff?.displayName && (
              <p className="m-0">
                <strong>
                  {rowData?.registerStaff?.displayName}
                </strong>
              </p>
            )}

            {rowData?.registerStaff?.staffCode && (
              <p className="m-0">Mã nhân viên: {rowData?.registerStaff?.staffCode}</p>
            )}
          </>
        );
      }
    },
    {
      title: "Ca làm việc",
      width: "20%",
      align: "left",
      field: "shiftWork.name",
      render: row => <span>{`${row?.shiftWork?.name}`}</span>
    },
    {
      title: "Ngày làm việc",
      field: "workingDate",
      width: "10%",
      align: "left",
      render: row => <span>{getDate(row?.workingDate)}</span>
    },
    // {
    //   title: "Người phê duyệt",
    //   width: "10%",
    //   field: "approvalStaff.displayName",
    //   align: "left",
    // },
    {
      title: "Trạng thái",
      width: "10%",
      field: "approvalStatus",
      align: "left",
      render: row => <span>{LocalConstants.ShiftRegistrationApprovalStatus.getListData().find(i => i.value == row?.approvalStatus)?.name}</span>
    },
  ];

  const [anchorEl, setAnchorEl] = useState();
  const [selectedRow, setSelectedRow] = useState(null);

  function handleClosePopover() {
    setAnchorEl(null);
    setSelectedRow(null);
  }

  return (
    <>
      <GlobitsTable
        data={listShiftRegistrations}
        columns={columns}
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setPageSize}
        pageSize={searchObject?.pageSize}
        pageSizeOption={[10, 15, 25, 50, 100]}
        totalElements={totalElements}
        page={searchObject?.pageIndex}
        selection
        handleSelectList={handleSelectListDelete}
      />

      {Boolean(anchorEl) && (
        <Menu
          id={"simple-menu-options"}
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={handleClosePopover}
          className="py-0"
        >

          {selectedRow?.approvalStatus != LocalConstants.ShiftRegistrationApprovalStatus.APPROVED.value && (
            <MenuItem className="flex items-center justify-center" onClick={function () {
              handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
              handleOpenConfirmUpdateStatusPopup(LocalConstants.ShiftRegistrationApprovalStatus.APPROVED.value);
              handleClosePopover();
            }}>
              <Icon className="pr-6" fontSize="small" style={{ color: "green" }}>
                done_all
              </Icon>
              <span className="ml-4">
                Phê duyệt
              </span>
            </MenuItem>
          )}

          {selectedRow?.approvalStatus != LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED.value && (
            <MenuItem className="flex items-center justify-center" onClick={function () {
              handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
              handleOpenConfirmUpdateStatusPopup(LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED.value);
              handleClosePopover();
            }}>
              <Icon className="pr-6" fontSize="small" style={{ color: "red" }}>
                thumb_down
              </Icon>
              <span className="ml-4">
                Từ chối
              </span>
            </MenuItem>
          )}

          {selectedRow?.approvalStatus != LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value && (
            <MenuItem className="flex items-center justify-center" onClick={function () {
              handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
              handleOpenConfirmUpdateStatusPopup(LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value);
              handleClosePopover();
            }}>
              <Icon className="pr-6" fontSize="small" style={{ color: "blue" }}>
                loop
              </Icon>
              <span className="ml-4">
                Chưa phê duyệt
              </span>
            </MenuItem>
          )}
        </Menu>
      )}
    </>

  );
}

export default memo(observer(ShiftRegistrationList));
