import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Radio } from "@material-ui/core";
import { formatDate } from "app/LocalFunction";
import MaterialTable from "material-table";
import GlobitsPagination from "app/common/GlobitsPagination";

function SelectStaffList({
  handleSelect
}) {
  const { staffStore } = useStore();
  const { t } = useTranslation();

  const { onPagingStaff, pageStaff, searchStaff, onChangeFormSearch } = staffStore;

  function handleSelectItem(_, department) {
    handleSelect(department)
  };

  let columns = [
    {
      title: t("general.popup.select"),
      align: "center",
      sorting: false,
      render: (rowData) => (
        <Radio
          id={`radio${rowData?.id}`}
          name="radSelected"
          value={rowData?.id}
          onClick={(event) => handleSelectItem(event, rowData)}
        />
      ),
    },
    {
      title: "Nhân viên",
      sorting: false,
      render: (rowData) => (
        <>
          {rowData.displayName && (
            <p className="m-0"><strong>{rowData.displayName}</strong></p>
          )}

          {rowData.birthDate && (
            <p className="m-0">Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>
          )}

          {rowData.gender && (
            <p className="m-0">Giới tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>
          )}

          {rowData.birthPlace && (
            <p className="m-0">Nơi sinh: {rowData.birthPlace}</p>
          )}
        </>
      ),
    },
    {
      title: "Thông tin liên hệ",
      sorting: false,
      render: (rowData) => (
        <>
          {rowData.phoneNumber && (
            <p className="m-0">SĐT: {rowData.phoneNumber}</p>
          )}

          {rowData.email && (
            <p className="m-0">Email: {rowData.email}</p>
          )}
        </>
      ),
    },
    {
      title: "Loại hợp đồng",
      sorting: false,
      field: "labourAgreementType.name",
      align: "left",
    },
  ];

  useEffect(function () {
    onPagingStaff("all");
  }, []);

  return (
    <div className="w-100 pt-8">
      <MaterialTable
        data={pageStaff?.content || []}
        columns={columns}
        parentChildData={(row, rows) => {
          var list = rows.find((a) => a?.id === row?.parentId);
          return list;
        }}
        options={{
          selection: false,
          actionsColumnIndex: -1,
          paging: false,
          search: false,
          toolbar: false,
          maxBodyHeight: "300px",
          headerStyle: {
            backgroundColor: "#2f4f4f",
            color: "#fff",
          },
          rowStyle: (rowData, index) => ({
            backgroundColor: index % 2 === 1 ? "rgb(237, 245, 251)" : "#FFF",
          }),
        }}
        onSelectionChange={(rows) => {
          this.data = rows;
        }}
      />
      <GlobitsPagination
        totalPages={pageStaff?.totalPages}
        handleChangePage={(_, pageIndex) => onChangeFormSearch({ pageIndex: pageIndex })}
        setRowsPerPage={({ target }) => onChangeFormSearch({ pageSize: target.value })}
        pageSizeOption={[10, 25, 50]}
        totalElements={pageStaff?.totalElements}
        page={searchStaff?.pageIndex}
        pageSize={searchStaff?.pageSize}
      />
    </div>
  );
}

export default memo(observer(SelectStaffList));
