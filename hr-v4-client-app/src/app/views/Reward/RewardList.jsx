import React, { memo } from "react";
import { observer } from "mobx-react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import Config from "app/common/GlobitsConfigConst";
import history from "history.js";
import ConstantList from "../../appConfig";


function RewardList() {
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
  } = useStore().rewardStore;

  let columns = [
    {
      title: t("general.action"),
      minWidth: "100px",
      ...Config.tableCellConfig,
      render: (rowData) => (
        <>
          <IconButton size="small" onClick={() => {
            history.push(ConstantList.ROOT_PATH + "category/staff/reward-form?id=" + rowData?.id)
          }}>
            <Icon fontSize="small" color="primary">
              edit
            </Icon>
          </IconButton>

          <IconButton size="small" onClick={() => handleDelete(rowData.id)}>
            <Icon fontSize="small" color="secondary">
              delete
            </Icon>
          </IconButton>
        </>
      ),
    },
    {
      title: t("reward.code"),
      minWidth: "100px",
      field: "code",
      ...Config.tableCellConfig,
    },
    {
      title: t("reward.name"),
      minWidth: "200px",
      field: "name",
      ...Config.tableCellConfig,
    },
    {
      title: t("reward.languageKey"),
      minWidth: "150px",
      field: "languageKey",
      ...Config.tableCellConfig,
    },
    {
      title: t("reward.rewardType"),
      minWidth: "150px",
      field: "rewardType",
      ...Config.tableCellConfig,
    },
    {
      title: t("reward.formal"),
      minWidth: "100px",
      field: "formal",
      ...Config.tableCellConfig,
    },
    {
      title: t("reward.evaluateYear"),
      minWidth: "100px",
      field: "evaluateYear",
      ...Config.tableCellConfig,
    },
    {
      title: t("reward.evaluateLevel"),
      minWidth: "100px",
      field: "evaluateLevel",
      ...Config.tableCellConfig,
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
      options={{}}
    />
  );
}

export default memo(observer(RewardList));
