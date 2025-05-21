import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";

function AllowanceList() {
  const { allowanceStore } = useStore();
  const { t } = useTranslation();

  const {
    allowanceList,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = allowanceStore;

  let columns = [
    {
      title: t("general.action"),
      width: "6%",
      align: "center",
      render: (rowData) => {
        return (
          <div className="flex flex-middle justify-center">
            <Tooltip title="Cập nhật thông tin" placement="top">
              <IconButton size="small" onClick={function () {
                handleOpenCreateEdit(rowData?.id);
              }}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Xóa" placement="top">
              <IconButton
                size="small"
                className="ml-4"
                onClick={() => handleDelete(rowData)
                }
              >
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>
          </div>
        );
      },
    },
    {
      title: t("Tên"), // allowance.name
      field: "name",
      align: "left",
    },
    {
      title: t("Mã"), // allowance.code
      field: "code",
      align: "left",
    },
    {
      title: t("Loại"), // allowance.type
      field: "allowanceType",
      render: data => <span>{data?.allowanceType?.name}</span>,
      align: "left",
    },
    // {
    //   title: t("Mô tả"), // allowance.description
    //   field: "description",
    //   ...Config.tableCellConfig
    // }
  ];
  return (
    <GlobitsTable
      selection
      data={allowanceList}
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
export default memo(observer(AllowanceList));