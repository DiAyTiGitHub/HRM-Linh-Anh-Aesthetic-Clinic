import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { Checkbox, Tooltip } from "@material-ui/core";

function SelectMulPositionsList(props) {
  const { t } = useTranslation();
  const {
  } = props;
  const { positionStore } = useStore();

  const {
    listPosition,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleSelectListDelete,
    listOnDelete
  } = positionStore;

  function handleSelectPosition(position) {
    let data = listOnDelete;

    const isSelected = data.some((item) => item?.id === position?.id);

    if (isSelected) {
      data = data.filter((item) => item.id !== position.id);
    } else {
      data = [...data, position];
    }

    handleSelectListDelete(data);
  }

  const columns = [
    {
      title: "Lựa chọn",
      sorting: false,
      align: "center",
      width: "10%",
      cellStyle: {
        textAlign: "center",
      },
      render: (rowData) => {
        // Check if the current staff is selected
        const isChecked = listOnDelete?.some((position) => position?.id === rowData?.id);

        return (
          <Tooltip
            title={isChecked ? "Bỏ chọn" : "Chọn"}
            placement="top"
          >
            <Checkbox
              className="pr-16"
              id={`radio${rowData?.id}`}
              name="radSelected"
              value={rowData.id}
              checked={isChecked}
              onClick={(event) => handleSelectPosition(rowData)}
            />
          </Tooltip>
        )
      }
    },

    {
      title: "Mã vị trí",
      field: "code",
      align: "left",
    },
    {
      title: "Tên vị trí",
      field: "name",
      align: "left",
    },
    {
      title: "Chức danh",
      field: "title",
      render: data => data?.title?.name,
      align: "left",
    },
    {
      title: "Đơn vị",
      field: "organization",
      render: data => data?.department?.organization?.name,
      align: "left",
    },
    {
      title: "Phòng ban",
      field: "department",
      render: data => data?.department?.name,
      align: "left",
    },
    {
      title: "Nhân viên",
      field: "staff.displayName",
      align: "left",
      render: data => {
        const displayName = data?.staff?.displayName ?? "";
        const staffCode = data?.staff?.staffCode ?? "";
        return displayName && staffCode ? `${displayName} - ${staffCode}` : displayName || staffCode || "Vacant";
      }
    },

    // { 
    //   title: "Trạng thái",
    //   render: data => {
    //     return getPositionStatus(data?.status);
    //   },
    //   align: "left",
    // },
    // {
    //   title: "Mô tả",
    //   field: "description",
    //   width: "40%"
    // },
  ];

  return (
    <GlobitsTable
      // selection
      data={listPosition}
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

export default memo(observer(SelectMulPositionsList));
