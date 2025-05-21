import React from "react";
import { observer } from "mobx-react";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import GlobitsTable from "../../common/GlobitsTable";
import Config from "../../common/GlobitsConfigConst";

export default observer(function RoleList() {
  const { roleStore } = useStore();
  const { t } = useTranslation();

  const {
    roleList,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
  } = roleStore;

  let columns = [
    { title: t("role.name"),minWidth: "200px", field: "name", ...Config.tableCellConfig },
    {
      title: t("role.authority"),
      minWidth: "200px",
      field: "authority",
      ...Config.tableCellConfig,
    },
    {
      title: t("role.createdBy"),
      minWidth: "200px",
      field: "createdBy",
      ...Config.tableCellConfig,
    },
  ];

  return (
    <GlobitsTable
      data={roleList}
      columns={columns}
      totalPages={totalPages}
      handleChangePage={handleChangePage}
      setRowsPerPage={setRowsPerPage}
      pageSize={rowsPerPage}
      pageSizeOption={[10, 25, 50]}
      totalElements={totalElements}
      page={page}
    />
  );
});
