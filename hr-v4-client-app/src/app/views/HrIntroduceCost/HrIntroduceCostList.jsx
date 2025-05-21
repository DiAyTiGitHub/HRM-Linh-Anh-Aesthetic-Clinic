import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, formatVNDMoney, getDate, getMonth } from "app/LocalFunction";

function HrIntroduceCostList () {
  const {hrIntroduceCostStore} = useStore ();
  const {t} = useTranslation ();

  const {
    hrIntroduceCostList,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = hrIntroduceCostStore;
  const isInSelectedMonthYear = (dateString) => {
    if (!dateString) return false;
    const date = new Date (dateString);
    const isValid = date instanceof Date && !isNaN (date);
    if (!isValid) return false;
    return (
        date.getMonth () + 1 === Number (searchObject?.month) &&
        date.getFullYear () === Number (searchObject?.year)
    );
  };

  let columns = [
    {
      title:t ("general.action"),
      width:"10%",
      align:"center",
      render:(rowData) => {
        return (
            <div className="flex flex-middle justify-center">
              <Tooltip title="Cập nhật thông tin" placement="top">
                <IconButton
                    size="small"
                    onClick={() => handleOpenCreateEdit (rowData?.id)}
                >
                  <Icon fontSize="small" color="primary">
                    edit
                  </Icon>
                </IconButton>
              </Tooltip>

              <Tooltip title="Xóa" placement="top">
                <IconButton
                    size="small"
                    className="ml-4"
                    onClick={() => handleDelete (rowData)}
                >
                  <Icon fontSize="small" color="secondary">
                    delete
                  </Icon>
                </IconButton>
              </Tooltip>
            </div>
        );
      },
    },
    {
      title:t ("Mã Nhân viên"),
      field:"custom",
      minWidth:"150px",
      render:(data) => <span>{data?.introducedStaff?.staffCode}</span>,
      align:"left",
    },
    {
      title:t ("Họ tên"),
      field:"custom",
      minWidth:"150px",
      render:(data) => <span>{data?.introducedStaff?.displayName}</span>,
      align:"left",
    },
    {
      title:t ("Ban/Chi nhánh"),
      field:"custom",
      minWidth:"150px",
      render:(data) => <span>{data?.introducedStaff?.department?.name}</span>,
      align:"left",
    },
    {
      title:t ("Ngày vào"),
      field:"custom",
      minWidth:"150px",
      render:(row) =>
          <span>{formatDate ("DD/MM/YYYY", row?.introStaffStartDate)}</span>,
      align:"left",
    },
    {
      title:t ("Người giới thiệu"),
      field:"custom",
      minWidth:"150px",
      render:(data) => <span>{data?.staff?.displayName}</span>,
      align:"left",
    },
    {
      title:t ("Mã giới thiệu"),
      field:"custom",
      minWidth:"150px",
      render:(data) => <span>{data?.staff?.staffCode}</span>,
      align:"left",
    },
    {
      title:t ("Ngày pass thử việc"),
      field:"startDate",
      minWidth:"150px",
      render:(row) =>
          <span>{formatDate ("DD/MM/YYYY", row?.officialDate)}</span>,
      align:"left",
    },
    {
      title:t ("Sáu tháng làm việc"),
      field:"custom",
      minWidth:"150px",
      render:(row) =>
          <span>{formatDate ("DD/MM/YYYY", row?.sixMonthsWorking)}</span>,
      align:"left",
    },
    {
      title:t ("Level"),
      field:"introducePeriod",
      minWidth:"150px",
      render:(row) =>
          <span>{row?.rankTitle?.name}</span>,
      align:"left",
    },
    {
      title:t ("Tổng thưởng theo cấp bậc"),
      field:"referralFeeLevel",
      minWidth:"150px",
      render:(row) => <span>{formatVNDMoney (row?.referralFeeLevel)}</span>,
      align:"left",
    },
    {
      title:t ("Ngày tính giới thiệu đợt 1"),
      field:"introducePeriod",
      minWidth:"150px",
      render:(row) =>
          <span>{formatDate ("DD/MM/YYYY", row?.introducePeriod)}</span>,
      align:"left",
      cellStyle:(row) =>
          isInSelectedMonthYear (row)
              ? {backgroundColor:"#ffff00"}
              : {}
    },
    {
      title:t ("Chi phí giới thiệu đợt 1"),
      field:"cost",
      minWidth:"150px",
      render:(row) => <span>{formatVNDMoney (row?.cost)}</span>,
      align:"left",
    },
    {
      title:t ("Ngày tính giới thiệu đợt 2"),
      field:"introducePeriod2",
      minWidth:"150px",
      render:(row) =>
          <span>{formatDate ("DD/MM/YYYY", row?.introducePeriod2)}</span>,
      align:"left",
      cellStyle:(row) =>
          isInSelectedMonthYear (row)
              ? {backgroundColor:"#ffff00"}
              : {}
    },
    {
      title:t ("Chi phí giới thiệu đợt 2"),
      field:"cost2",
      minWidth:"150px",
      render:(row) => <span>{formatVNDMoney (row?.cost2)}</span>,
      align:"left",
    },
    {
      title:t ("Ngày tính giới thiệu đợt 3"),
      field:"introducePeriod3",
      minWidth:"150px",
      render:(row) =>
          <span>{formatDate ("DD/MM/YYYY", row?.introducePeriod3)}</span>,
      align:"left",
      cellStyle:(row) =>
          isInSelectedMonthYear (row)
              ? {backgroundColor:"#ffff00"}
              : {}
    },
    {
      title:t ("Chi phí giới thiệu đợt 3"),
      field:"cost3",
      minWidth:"150px",
      render:(row) => <span>{formatVNDMoney (row?.cost3)}</span>,
      align:"left",
    },
    {
      title:t ("Ghi chú"),
      field:"note",
      minWidth:"200px",
      render:(row) => <span>{row?.note}</span>,
      align:"left",
    },
  ];
  return (
      <GlobitsTable
          selection
          data={hrIntroduceCostList}
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

export default memo (observer (HrIntroduceCostList));