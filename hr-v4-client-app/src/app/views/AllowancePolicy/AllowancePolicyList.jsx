import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { getDate } from "app/LocalFunction";

function AllowancePolicyList() {
  const { allowancePolicyStore } = useStore();
  const { t } = useTranslation();

  const {
    allowancePolicyList,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = allowancePolicyStore;

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
      title: t("Tên"),
      field: "name",
      align: "left",
    },
    {
      title: t("Mã"),
      field: "code",
      align: "left"
    },
    {
      title: t("Đơn vị"),
      field: "organization",
      render: data => <span>{data?.organization?.name}</span>,
      align: "left",
    },
    {
      title: t("Phòng ban"),
      field: "allowance",
      render: data => <span>{data?.department?.name}</span>,
      align: "left",
    },
    {
      title: t("Vị trí"),
      field: "position",
      render: data => <span>{data?.position?.name}</span>,
      align: "left",
    },
    {
      title: t("Phụ cấp"),
      field: "allowance",
      render: data => <span>{data?.allowance?.name}</span>,
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
  ];
  return (
    <GlobitsTable
      selection
      data={allowancePolicyList}
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
export default memo(observer(AllowancePolicyList));