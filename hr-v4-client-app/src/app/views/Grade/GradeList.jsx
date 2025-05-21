import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
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

export default observer(function GradeList() {
  const { gradeStore } = useStore();
  const { t } = useTranslation();

  const {
    gradeList,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleEditGrade,
    handleSelectListGrade,
  } = gradeStore;

  let columns = [
    {
      title: t("general.action"),
      minWidth:"100px",
      ...Config.tableCellConfig,
      render: (rowData) => (
        <MaterialButton
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) {
              handleEditGrade(rowData.id);
            } else if (method === 1) {
              handleDelete(rowData.id);
            } else {
              alert("Call Selected Here:" + rowData.id);
            }
          }}
        />
      ),
    },
    {
      title: t("grade.code"),
      minWidth:"150px",
      field: "code",
      ...Config.tableCellConfig,
    },
    { title: t("grade.name"),minWidth:"100px", field: "name", ...Config.tableCellConfig },
    { title: t("grade.salaryCoefficient"),minWidth:"150px", field: "salaryCoefficient", ...Config.tableCellConfig },
    {
      title: t("grade.description"),
      field: "description",minWidth:"200px",
      ...Config.tableCellConfig,
    },
  ];
  return (
    <GlobitsTable
      selection
      handleSelectList={handleSelectListGrade}
      data={gradeList}
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
