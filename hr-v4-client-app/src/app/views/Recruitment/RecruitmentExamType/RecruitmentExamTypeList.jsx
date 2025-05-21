import React from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";

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

export default observer(function ExamCategoryList() {
  const { recruitmentExamTypeStore } = useStore();
  const { t } = useTranslation();

  const {

    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleOpenForm,
    handleSelectedUser,
    examCategoryList,
  } = recruitmentExamTypeStore;

  let columns = [
    {
      title: t("general.action"),
      width: "10%",
      align: "center",
      render: (rowData) => (
        <div>
          <MaterialButton
            item={rowData}
            onSelect={(rowData, method) => {
              if (method === 0) {
                handleOpenForm(rowData.id);
              } else if (method === 1) {
                handleDelete(rowData.id);
              } else {
                alert("Call Selected Here:" + rowData.id);
              }
            }}
          />
        </div>
      ),
    },
    {
      title: "Mã loại kiểm tra",
      width: "20%",
      field: "code",
      align: "left",
    },
    {
      title: "Loại kiểm tra",
      // minWidth: "100px",
      field: "name",
      align: "left",
    },
    {
      title: "Mô tả",
      width: "40%",
      field: "description",
      align: "left",
    },
  ];


  return (
    <GlobitsTable
      data={examCategoryList || []}
      columns={columns}
      totalPages={totalPages}
      handleChangePage={handleChangePage}
      setRowsPerPage={setRowsPerPage}
      pageSize={rowsPerPage}
      pageSizeOption={[10, 25, 50]}
      totalElements={totalElements}
      page={page}
      selection
      handleSelectList={handleSelectedUser}
      options={
        {

        }
      }
    />
  );
});
