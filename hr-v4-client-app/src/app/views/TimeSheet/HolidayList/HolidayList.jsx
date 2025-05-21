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
      {/* <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
        <Icon fontSize="small" color="secondary">
          delete
        </Icon>
      </IconButton> */}
    </div>
  );
}

export default observer(function HolidayList() {
  const { userStore } = useStore();
  const { t } = useTranslation();

  const {
    userList,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleEditUser,
    handleSelectedUser
  } = userStore;

  let columns = [
    {
      title: t("general.action"),
      maxWidth: "100px",
      align: "center",
      render: (rowData) => (
        <MaterialButton
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) {
              handleEditUser(rowData.id);
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
      title: "Mã ngày lễ",
      // minWidth: "100px",
      field: "username",
    },
    {
      title: "Tên ngày lễ",
      // minWidth: "100px",
      field: "displayName",
     
    },
    {
      title:"Phòng ban",
      // minWidth: "100px",
      field: "email",
      align: "left",
    },
    {
      title: "Ngày nghỉ",
      // minWidth: "100px",
      field: "staff.dislayName",
      align: "left",
      render: data => data?.staff?.displayName && (<span>{data?.staff?.displayName}</span>),
    },
    {
        title:"Công ty cho phép nghỉ",
        // minWidth: "100px",
        field: "email",
        align: "left",
      },
      {
        title:"Số ngày nghỉ",
        // minWidth: "100px",
        field: "email",
        align: "left",
      },
      {
        title:"Người cập nhật",
        // minWidth: "100px",
        field: "email",
        align: "left",
      },
      {
        title:"Ngày cập nhật",
        // minWidth: "100px",
        field: "email",
        align: "left",
      },
      {
        title:"Ghi chú",
        // minWidth: "100px",
        field: "email",
        align: "left",
      },
  ];


  return (
    <GlobitsTable
      data={userList}
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
