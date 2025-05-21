import React, { memo } from "react";
import GlobitsTable from "../../../common/GlobitsTable";
import { useStore } from "../../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import Config from "../../../common/GlobitsConfigConst";
import { observer } from "mobx-react";
import { formatMoney } from "app/LocalFunction";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryItem } from "../SalaryItemV2/SalaryItemV2Service";
import { Box, Chip, Typography } from "@material-ui/core";

function SalaryAutoMapList() {
  const { t } = useTranslation();

  const {
    salaryAutoMapStore,
    hrRoleUtilsStore
  } = useStore();

  const {
    isAdmin,
    isManager
  } = hrRoleUtilsStore;

  const {
    salaryAutoMapList,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleEditSalaryAutoMap,
    handleSelectListSalaryAutoMap,
  } = salaryAutoMapStore;

  let columns = [
    {
      title: t("general.action"),
      width: "10%",
      align: "center",
      render: (rowData) => (
        <div className="flex flex-center justify-center">
          {
            (isAdmin || isManager) && (
              <Tooltip title={"Cập nhật"} placement="top">
                <IconButton
                  className="pr-8"
                  size="small"
                  onClick={() => handleEditSalaryAutoMap(rowData.id)}
                >
                  <Icon fontSize="small" color="primary">
                    edit
                  </Icon>
                </IconButton>
              </Tooltip>
            )
          }
        </div>
      ),
    },
    // {
    //   title: t("salaryAutoMap.salaryAutoMapField"),
    //   field: "salaryAutoMapField",
    //   align: "left",
    //   width: "30%",
    // },
    {
      title: t("salaryAutoMap.description"),
      field: "description",
      align: "left",
      width: "40%",
      render: (rowData) => (
        <span className="px-6 w-100">
          {rowData?.description}
        </span>
      )
    },
    {
      title: t("salaryAutoMap.salaryItem"),
      align: "center",
      field: "salaryItem.name",
      render: (rowData) => (
        <span className="w-100">
          <SalaryItemListView
            items={rowData?.salaryItems}
          />
        </span>
      )
    }
  ];

  return (
    <GlobitsTable
      selection={false}
      handleSelectList={handleSelectListSalaryAutoMap}
      data={salaryAutoMapList}
      columns={columns}
      totalPages={totalPages}
      handleChangePage={handleChangePage}
      setRowsPerPage={setRowsPerPage}
      pageSize={rowsPerPage}
      pageSizeOption={[10, 25, 50]}
      totalElements={totalElements}
      page={page}
      nonePagination={true}
    />
  );
}

export default memo(observer(SalaryAutoMapList));


function SalaryItemListView({ items = [] }) {
  return (
    <Box
    // style={{ width: 500 }}
    >
      {/* <Typography variant="subtitle1" style={{ marginBottom: 8 }}>
        Thành phần lương kết nối
      </Typography> */}

      <Box
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: 0,
        }}
      >
        {items.map((item) => (
          <Chip
            key={item.id}
            label={`${item.name} - ${item.code}`}
            style={{ margin: 1 }}
          // Không có onDelete => không thể xóa
          />
        ))}
      </Box>
    </Box>
  );
}
