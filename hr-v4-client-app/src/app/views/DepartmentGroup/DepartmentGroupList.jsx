import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";

function MaterialButton(props) {
  const { item } = props;
  return (
    <div>
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

function DepartmentGroupList() {
  const { departmentGroupStore } = useStore();
  const { t } = useTranslation();

  const {
    listDepartmentGroup,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = departmentGroupStore;

  const columns = [
    {
      title: t("general.action"),
      width: "20%",
      align: "center",
      minWidth: "48px",
      render: (rowData) => (
        <MaterialButton
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) {
              handleOpenCreateEdit(rowData?.id);
            } else if (method === 1) {
              handleDelete(rowData);
            } else {
              alert("Call Selected Here:" + rowData?.id);
            }
          }}
        />
      ),
    },
    {
      title: "Tên nhóm phòng ban",
      field: "name",
      align: "left",
    },
    {
      title: "Tên viết tắt",
      field: "shortName",
      align: "left",
    },
    {
      title: "Tên khác",
      field: "otherName",
      align: "left",
    },
    {
      title: "Thứ tự",
      field: "sortNumber",
      width: "20%",
      align: "center",
    },
    {
      title: "Mô tả",
      field: "description",
      align: "left",
    },
  ];

  return (
    <GlobitsTable
      selection
      data={listDepartmentGroup}
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

export default memo(observer(DepartmentGroupList));
