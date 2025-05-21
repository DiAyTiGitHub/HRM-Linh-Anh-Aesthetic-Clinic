import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";

const ShiftWorkList = () => {
  const { shiftWorkStore, hrRoleUtilsStore } = useStore();
  const { t } = useTranslation();
  const { isManager, isAdmin } = hrRoleUtilsStore;

  const {
    rowsPerPage,
    page,
    handleSelectListShiftWork,
    handleEditShiftWork,
    setSelectShiftWorkDeleted,
    shiftWorkList,
    totalElements,
    totalPages,
    setRowsPerPage,
    setPage,
    handleOpenView,
  } = shiftWorkStore;

  let columns = [
    {
      title: t("general.action"),
      width: "10%",
      render: (rowData) => (
        <div className="flex flex-center">
          <Tooltip
            arrow
            placement="top"
            title={"Chi tiết ca làm việc"}
          >
            <IconButton
              className="ml-4"
              size="small"
              onClick={() => handleOpenView(rowData?.id)}
            >
              <Icon fontSize="small" style={{ color: "green" }}>
                remove_red_eye
              </Icon>
            </IconButton>

          </Tooltip>
          {(isManager || isAdmin) && (
            <Tooltip
              arrow
              title={"Cập nhật thông tin ca làm việc"}
              placement="top"
            >
              <IconButton size="small" onClick={() => handleEditShiftWork(rowData.id)}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>
          )}
          {(isManager || isAdmin) && (
            <Tooltip
              arrow
              title={"Xóa thông tin ca làm việc"}
              placement="top"
            >
              <IconButton size="small" onClick={() => setSelectShiftWorkDeleted(rowData.id)}>
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>
          )}
        </div>
      ),
    },

    {
      title: t("shiftWork.code"),
      field: "code",
      width: "10%",
      align: "center",
    },

    {
      title: t("shiftWork.name"),
      field: "name",
      align: "center",
    },

    {
      title: t("shiftWork.totalHours"),
      field: `totalHours`,
      align: "center",
    },

    {
      title: "Số giờ công quy đổi",
      field: `convertedWorkingHours`,
      align: "center",
    },
  ];

  return (
    <GlobitsTable
      selection
      columns={columns}
      handleSelectList={handleSelectListShiftWork}
      data={shiftWorkList || []}
      totalPages={totalPages}
      totalElements={totalElements}
      page={page}
      pageSize={rowsPerPage}
      handleChangePage={(_, pageIndex) => setPage(pageIndex)}
      setRowsPerPage={setRowsPerPage}
    />
  );
};

export default observer(ShiftWorkList)