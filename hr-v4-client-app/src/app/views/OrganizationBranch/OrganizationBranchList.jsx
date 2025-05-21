import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";

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

function OrganizationBranchList() {
  const { organizationBranchStore } = useStore();
  const { t } = useTranslation();

  const {
    listOrganizationBranches,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = organizationBranchStore;

  const columns = [
    {
      title: t("general.action"),
      minWidth: "48px",
      render: (rowData) => (
        <MaterialButton
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) {
              handleOpenCreateEdit(rowData?.id);
            } else if (method === 1) {
              handleDelete(rowData);
            } else {
              alert("Call Selected Here:" + rowData?.id);
            }
          }}
        />
      ),
    },
    {
      title: "Mã",
      field: "code",
      align: "left",
    },
    {
      title: "Tên địa điểm",
      field: "name",
      align: "left",
    },
    {
      title: "Công ty",
      field: "organization",
      render: (rowData) => (
        <span>
          {rowData?.organization?.name}
        </span>
      ),
    },
    {
      title: "Quốc gia",
      field: "country",
      render: (rowData) => (
        <span>
          {rowData?.country?.name}
        </span>
      ),
    },
    {
      title: "Tỉnh/Thành phố",
      field: "province",
      render: (rowData) => (
        <span>
          {rowData?.province?.name}
        </span>
      ),
    },
    {
      title: "Quận/Huyện",
      field: "district",
      render: (rowData) => (
        <span>
          {rowData?.district?.name}
        </span>
      ),
    },
    {
      title: "Xã/Phường",
      field: "commune",
      render: (rowData) => (
        <span>
          {rowData?.commune?.name}
        </span>
      ),
    },
    {
      title: "Địa chỉ",
      field: "address",
    },
    {
      title: "SĐT",
      field: "phoneNumber",
    },
  ];

  return (
    <GlobitsTable
      selection
      data={listOrganizationBranches}
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

export default memo(observer(OrganizationBranchList));
