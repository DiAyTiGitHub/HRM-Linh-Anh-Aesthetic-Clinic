import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Radio, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";


function SalaryItemV2List(props) {
  const { salaryItemStore, hrRoleUtilsStore } = useStore();
  const { t } = useTranslation();

  const {
    listSalaryItem,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
    getSalaryItemCalculationTypeName,
    getSalaryItemValueTypeName,
    handleOpenView
  } = salaryItemStore;

  const {
    isAdmin,
    isManager,
    isCompensationBenifit,
    checkAllUserRoles
  } = hrRoleUtilsStore;

  useEffect(() => {
    checkAllUserRoles();
  }, []);

  const { isOnChoosingMode, selectedItem, handleChooseItem } = props;

  const columns = [
    {
      title: t("general.action"),
      width: "10%",
      render: (rowData) => (
        <div className="flex flex-center">
          <Tooltip
            arrow
            placement="top"
            title={"Xem chi tiết"}
          >
            <IconButton
              className="ml-4"
              size="small"
              onClick={() => handleOpenView(rowData?.id)}
            >
              <Icon fontSize="small" style={{ color: "green" }}>
                remove_red_eye
              </Icon>
            </IconButton>

          </Tooltip>

          {(isCompensationBenifit) && (
            <Tooltip
              arrow
              title={"Cập nhật thông tin thành phần lương"}
              placement="top"
            >
              <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData?.id)}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>
          )}

          {(isCompensationBenifit) && (
            <Tooltip
              arrow
              title={"Xóa thông tin thành phần lương"}
              placement="top"
            >
              <IconButton size="small" onClick={() => handleDelete(rowData)}>
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>
          )}

        </div>
      ),
    },

    {
      title: t("salaryItem.code"),
      width: "20%",
      field: "code",
      align: "left",
    },

    {
      title: t("salaryItem.name"),
      width: "20%",
      field: "name",
      align: "left",
    },

    {
      title: "Cách tính giá trị",
      width: "20%",
      align: "left",
      render: (data) => (data?.calculationType ? getSalaryItemCalculationTypeName(data.calculationType) : ""),
    },

    {
      title: "Kiểu giá trị",
      width: "20%",
      align: "left",
      render: (data) => (data?.valueType ? getSalaryItemValueTypeName(data.valueType) : ""),
    }
  ];

  if (isOnChoosingMode) {
    columns.unshift({
      title: t("general.popup.select"),
      align: "center",
      cellStyle: { textAlign: "center" },
      render: (rowData) => (
        <Tooltip title="Chọn sử dụng" placement="top">
          <Radio
            className="pr-16"
            id={`radio${rowData?.id}`}
            name="radSelected"
            value={rowData.id}
            checked={selectedItem?.id === rowData?.id}
            onClick={() => handleChooseItem(rowData)}
          />
        </Tooltip>
      ),
    });
  }

  return (
    <GlobitsTable
      selection={(isAdmin || isManager)}
      data={listSalaryItem}
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

export default memo(observer(SalaryItemV2List));
