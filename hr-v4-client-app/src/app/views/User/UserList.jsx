import React from "react";
import { observer } from "mobx-react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";

function MaterialButton (props) {
  const {item} = props;
  return (
      <div>
        <IconButton size="small" onClick={() => props.onSelect (item, 0)}>
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

export default observer (function UserList () {
  const {userStore} = useStore ();
  const {t} = useTranslation ();

  const {
    userList,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleEditUser,
    handleSelectedUser,
    handleOpenView
  } = userStore;

  let columns = [
    {
      title:t ("general.action"),
      // maxWidth: "100px",
      align:"center",
      render:(rowData) => (
          <div className="flex flex-center">
            <Tooltip
                arrow
                placement="top"
                title={"Xem chi tiết người dùng"}
            >
              <IconButton
                  className="ml-4"
                  size="small"
                  onClick={() => handleOpenView (rowData?.id)}
              >
                <Icon fontSize="small" style={{color:"green"}}>
                  remove_red_eye
                </Icon>
              </IconButton>

            </Tooltip>

            <Tooltip
                arrow
                title={"Cập nhật thông tin người dùng"}
                placement="top"
            >
              <IconButton size="small" onClick={() => handleEditUser (rowData?.id)}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>


            <Tooltip
                arrow
                title={"Xóa thông tin người dùng"}
                placement="top"
            >
              <IconButton size="small" onClick={() => handleDelete (rowData.id)}>
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>

          </div>
      ),
      ... Config.tableCellConfig,
    },
    {
      title:t ("user.username"),
      // minWidth: "100px",
      field:"username",
      ... Config.tableCellConfig,
    },
    {
      title:t ("user.displayName"),
      // minWidth: "100px",
      field:"displayName",
      ... Config.tableCellConfig,

    },
    {
      title:t ("user.email"),
      // minWidth: "100px",
      field:"email",
      align:"left",
      ... Config.tableCellConfig,
    },
    {
      title:t ("user.usedByEmployee"),
      field:"staff.displayName",
      align:"left",
      render:(data) => {
        const name = data?.staff?.displayName || "";
        const code = data?.staff?.staffCode || "";
        return (
            <span>
                {name}{code? ` - ${code}` : ""}
            </span>
        );
      },
      ... Config.tableCellConfig,
    },
    {
      title:t ("user.role.title"),
      field:"role",
      align:"left",
      render:(data) => {
        if (data?.roles && Array.isArray (data.roles)) {
          return data.roles.map ((role) => role.name).join (", ");
        }
        return "";
      },
      ... Config.tableCellConfig,
    }
  ];


  return (
      <GlobitsTable
          data={userList}
          columns={columns}
          totalPages={totalPages}
          handleChangePage={handleChangePage}
          setRowsPerPage={setRowsPerPage}
          pageSize={searchObject?.pageSize}
          pageSizeOption={[10, 25, 50]}
          totalElements={totalElements}
          page={searchObject?.pageIndex}
          selection
          handleSelectList={handleSelectedUser}
      />
  );
});
