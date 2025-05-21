import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { Checkbox, Tooltip } from "@material-ui/core";
import { useFormikContext } from "formik";

function SelectMulPositionsList (props) {
  const {t} = useTranslation ();
  const {} = props;
  const {positionStore} = useStore ();

  const {
    listPosition,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleSelectListDelete,
  } = positionStore;


  const {values, setFieldValue} = useFormikContext ()

  function handleSelectPosition (position) {
    let selectedPositions = values.positions || [];

    const isSelected = selectedPositions.some (item => item?.id === position?.id);

    if (isSelected) {
      selectedPositions = selectedPositions.filter (item => item.id !== position.id);
    } else {
      selectedPositions = [... selectedPositions, position];
    }

    setFieldValue ("positions", selectedPositions);
  }

  const columns = [
    {
      title:"Lựa chọn",
      sorting:false,
      align:"center",
      width:"10%",
      cellStyle:{
        textAlign:"center",
      },
      render:(rowData) => {
        const isChecked = (values.positions || []).some (position => position?.id === rowData?.id);

        return (
            <Tooltip title={isChecked? "Bỏ chọn" : "Chọn"} placement="top">
              <Checkbox
                  className="pr-16"
                  id={`radio${rowData?.id}`}
                  name="radSelected"
                  value={rowData.id}
                  checked={isChecked}
                  onClick={() => handleSelectPosition (rowData)}
              />
            </Tooltip>
        );
      },
    },

    {
      title:"Mã vị trí",
      field:"code",
      align:"left",
    },
    {
      title:"Tên vị trí",
      field:"name",
      align:"left",
    },
    {
      title:"Chức danh",
      field:"title",
      render:data => data?.title?.name,
      align:"left",
    },
    {
      title:"Đơn vị",
      field:"organization",
      render:data => data?.department?.organization?.name,
      align:"left",
    },
    {
      title:"Phòng ban",
      field:"department",
      render:data => data?.department?.name,
      align:"left",
    },
    {
      title:"Nhân viên",
      field:"staff.displayName",
      align:"left",
      render:data => {
        const displayName = data?.staff?.displayName ?? "";
        const staffCode = data?.staff?.staffCode ?? "";
        return displayName && staffCode? `${displayName} - ${staffCode}` : displayName || staffCode || "Vacant";
      }
    },
    {
      title:"Nhân viên tiền nhiệm",
      field:"previousStaff.displayName",
      align:"left",
      render:data => {
        const displayName = data?.previousStaff?.displayName ?? "";
        const staffCode = data?.previousStaff?.staffCode ?? "";
        return displayName && staffCode? `${displayName} - ${staffCode}` : displayName || staffCode || "";
      }
    },
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

export default memo (observer (SelectMulPositionsList));
