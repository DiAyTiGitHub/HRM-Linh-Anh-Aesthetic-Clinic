import React, { memo, useMemo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { Radio } from "@material-ui/core";

function MaterialButton(props) {
  const { item } = props;
  return (
    <div className="flex-center justify-center">
      <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
        <Icon fontSize="small" color="primary">
          edit
        </Icon>
      </IconButton>
      <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
        <Icon fontSize="small" color="secondary">
          delete
        </Icon>
      </IconButton>
    </div>
  );
}

function StaffTypeV2List(props) {
  const { staffTypeStore } = useStore();
  const { t } = useTranslation();

  const {
    listStaffType,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
    getSalaryItemCalculationTypeName,
    getSalaryItemTypeName,
    getSalaryItemValueTypeName
  } = staffTypeStore;

  const {
    isOnChoosingMode,
    selectedItem,
    handleChooseItem
  } = props;

  const columns = [
    {
      title: t("general.action"),
      align: "center",
      width: "10%",
      render: (rowData) => (
        <MaterialButton
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) {
              handleOpenCreateEdit(rowData.id);
            } else if (method === 1) {
              handleDelete(rowData);
            } else {
              alert("Call Selected Here:" + rowData.id);
            }
          }}
        />
      ),
    },
    {
      title: "Mã",
      // minWidth: "100px",
      width: "20%",
      field: "code",
      align: "center",
    },
    {
      title: "Loại nhân viên",
      // minWidth: "100px",
      width: "40%",
      field: "name",
      align: "center",
    },
    {
      title:"Mô tả",
      // minWidth: "100px",
      field: "description",
      align: "center",
    },
  ];


  return (
    <GlobitsTable
      selection
      data={listStaffType}
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

export default memo(observer(StaffTypeV2List));
