import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
import { observer } from "mobx-react";

function EmployeeStatusList() {
  const { t } = useTranslation();

  const {
    handleSelectListEmployeeStatus,
    pageEmployeeStatus,
    searchEmployeeStatus,
    onChangeFormSearch,
    onOpenFormEmployeeStatusEdit,
    setSelectedEmployeeStatusDelete
  } = useStore().employeeStatusStore;

  let columns = [
    {
      title: t("general.action"),
      width: "10%",
      ...Config.tableCellConfig,
      render: (rowData) => (
        <>
          <IconButton size="small" onClick={() => onOpenFormEmployeeStatusEdit(rowData.id)}>
            <Icon fontSize="small" color="primary">
              edit
            </Icon>
          </IconButton>

          <IconButton size="small" onClick={() => setSelectedEmployeeStatusDelete(rowData.id)}>
            <Icon fontSize="small" color="secondary">
              delete
            </Icon>
          </IconButton>
        </>

      ),
    },
    {
      title: t("employeeStatus.code"),
      width: "10%",
      field: "code",
      ...Config.tableCellConfig,
    },
    {
      title: t("employeeStatus.name"),
      minWidth: "200px",
      field: "name",
      ...Config.tableCellConfig,
    },
    {
      title: t("employeeStatus.languageKey"),
      minWidth: "150px",
      field: "languageKey",
      ...Config.tableCellConfig,
    },
    // {
    //   title: "Kích hoạt",
    //   minWidth: "50px",
    //   render: (rowData) => {
    //     const isActive = rowData?.active ? "Được sử dụng" : "";

    //     return (isActive);
    //   }
    // },
  ];

  return (
    <GlobitsTable
      selection
      handleSelectList={handleSelectListEmployeeStatus}
      columns={columns}
      data={pageEmployeeStatus?.content}
      totalPages={pageEmployeeStatus?.totalPages}
      totalElements={pageEmployeeStatus?.totalElements}
      page={searchEmployeeStatus.pageIndex}
      pageSize={searchEmployeeStatus.pageSize}
      handleChangePage={(_, pageIndex) => onChangeFormSearch({ pageIndex: pageIndex })}
      setRowsPerPage={({ target }) => onChangeFormSearch({ pageSize: target.value })}
    />
  );
};

export default memo(observer(EmployeeStatusList))