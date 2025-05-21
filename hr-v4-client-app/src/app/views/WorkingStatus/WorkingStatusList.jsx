import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";

function WorkingStatusList() {
  const { t } = useTranslation();

  const {
    pageWorkingStatus,
    searchWorkingStatus,
    handleSelectListWorkingStatus,
    onChangeFormSearch,
    onOpenFormWorkingStatusEdit,
    setSelectedWorkingStatusDelete
  } = useStore().workingStatusStore;

  let columns = [
    {
      title: t("general.action"),
      width: "10%",
      align: "center",
      render: (rowData) => (
        <>
          <IconButton size="small" onClick={() => onOpenFormWorkingStatusEdit(rowData.id)}>
            <Icon fontSize="small" color="primary">
              edit
            </Icon>
          </IconButton>

          <IconButton size="small" onClick={() => setSelectedWorkingStatusDelete(rowData.id)}>
            <Icon fontSize="small" color="secondary">
              delete
            </Icon>
          </IconButton>
        </>

      ),
    },
    { title: t("workingStatus.code"), field: "code", align: "left", minWidth: "150px", },
    { title: t("workingStatus.name"), field: "name", minWidth: "250px", },
    { title: t("workingStatus.statusValue"), field: "statusValue", minWidth: "200px", },
  ];

  return (
    <GlobitsTable
      selection
      handleSelectList={handleSelectListWorkingStatus}
      columns={columns}
      data={pageWorkingStatus?.content}
      totalPages={pageWorkingStatus?.totalPages}
      totalElements={pageWorkingStatus?.totalElements}
      pageSize={searchWorkingStatus.pageSize}
      page={searchWorkingStatus.pageIndex}
      handleChangePage={(_, pageIndex) => onChangeFormSearch({ pageIndex: pageIndex })}
      setRowsPerPage={({ target }) => onChangeFormSearch({ pageSize: target.value })}
    />
  );
};

export default observer(WorkingStatusList);