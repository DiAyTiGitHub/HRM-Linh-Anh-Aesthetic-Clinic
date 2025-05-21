import { Icon, IconButton } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";

function WorkplaceList() {
  const { workplaceStore } = useStore();
  const { t } = useTranslation();

  const {
    listWorkplace,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = workplaceStore;

  const columns = [
    {
      title: t("general.action"),
      width: "10%",
      align: "center",
      render: (rowData) => (
        <>
          <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData.id)}>
            <Icon fontSize="small" color="primary">
              edit
            </Icon>
          </IconButton>

          <IconButton size="small" onClick={() => handleDelete(rowData)}>
            <Icon fontSize="small" color="secondary">
              delete
            </Icon>
          </IconButton>
        </>

      ),
    },
    {
      title: "Mã địa điểm",
      field: "code",
      align: "center",
      minWidth: "150px"
    },
    {
      title: "Tên địa điểm",
      field: "name",
      align: "left",
      minWidth: "250px"
    },
    {
      title: "Mô tả",
      field: "description",
      align: "left",
      minWidth: "250px"
    },
  ];

  return (
    <GlobitsTable
      selection
      data={listWorkplace}
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

export default memo(observer(WorkplaceList));
