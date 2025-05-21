import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";

function SalaryConfigList() {
  const { salaryConfigStore } = useStore();
  const { t } = useTranslation();

  const {
    listSalaryConfig,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
    getDisplayVoidedStatus
  } = salaryConfigStore;

  const columns = [
    {
      title: t("general.action"),
      width: "6%",
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
    // {
    //   title: "Mã thành phần",
    //   field: "code",
    //   align: "left",
    // },
    {
      title: "Tên thành phần lương",
      field: "name",
      align: "left",
    },
    {
      title: "Tên khác",
      field: "otherName",
      align: "left",
    },
    {
      title: "Giá trị mặc định",
      field: "defaultValue",
      align: "left",
    },
    {
      title: "Nhóm dữ liệu",
      field: "salaryType",
      align: "left",
      render: data => data?.salaryType && (<span>{data?.salaryType?.name}</span>),
    },
    {
      title: "Trạng thái",
      field: "voided",
      render: data => getDisplayVoidedStatus(data?.voided),
      align: "left",
    },
  ];

  return (
    <GlobitsTable
      selection
      data={listSalaryConfig}
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

export default memo(observer(SalaryConfigList));
