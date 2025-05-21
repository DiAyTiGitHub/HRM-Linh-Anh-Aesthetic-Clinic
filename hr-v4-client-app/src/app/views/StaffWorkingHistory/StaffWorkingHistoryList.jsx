import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { getDate } from "app/LocalFunction";

function StaffWorkingHistoryList() {
  const { staffWorkingHistoryStore } = useStore();
  const { t } = useTranslation();

  const {
    staffWorkingHistoryList,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = staffWorkingHistoryStore;

  let columns = [
    {
      title: t("general.action"),
      width: "10%",
      align: "center",
      render: (rowData) => {
        return (
          <div className="flex flex-middle justify-center">
            <Tooltip title="Cập nhật thông tin" placement="top">
              <IconButton size="small" onClick={function () {
                handleOpenCreateEdit(rowData?.id);
              }}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Xóa" placement="top">
              <IconButton
                size="small"
                className="ml-4"
                onClick={() => handleDelete(rowData)
                }
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
      title: t("Tên nhân viên"),
      field: "staff",
      render: data => <span>{data?.staff?.displayName}</span>,
      align: "left",
    },
    {
      title: t("Ngày bắt đầu"),
      field: "startDate",
      render: row => <span>{getDate(row?.startDate)}</span>,
      align: "left",
    },
    {
      title: t("Ngày kết thúc"),
      field: "endDate",
      render: row => <span>{getDate(row?.endDate)}</span>,
      align: "left",
    },
    
    {
      title: t("Từ đơn vị"),
      field: "staff",
      render: data => <span>{data?.fromOrganization?.name}</span>,
      align: "left",
    },
    {
      title: t("Đến đơn vị"),
      field: "staff",
      render: data => <span>{data?.toOrganization?.name}</span>,
      align: "left",
    },
    {
      title: t("Từ phòng ban"),
      field: "staff",
      render: data => <span>{data?.fromDepartment?.name}</span>,
      align: "left",
    },
    {
      title: t("Đến phòng ban"),
      field: "staff",
      render: data => <span>{data?.toDepartment?.name}</span>,
      align: "left",
    },
    {
      title: t("Từ vị trí"),
      field: "staff",
      render: data => <span>{data?.fromPosition?.name}</span>,
      align: "left",
    },
    {
      title: t("Đến vị trí"),
      field: "staff",
      render: data => <span>{data?.toPosition?.name}</span>,
      align: "left",
    },

    
  ];
  return (
    <GlobitsTable
      data={staffWorkingHistoryList}
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
export default memo(observer(StaffWorkingHistoryList));