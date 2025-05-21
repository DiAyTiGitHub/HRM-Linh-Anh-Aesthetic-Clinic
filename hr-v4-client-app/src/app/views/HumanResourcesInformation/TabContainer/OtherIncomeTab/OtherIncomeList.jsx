import React, { useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { formatDate, formatVNDMoney } from "app/LocalFunction";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { OtherIncomeType } from "../../../../LocalConstants";

export default observer (function OtherIncomeList () {
  const {otherIncomeStore} = useStore ();
  const {t} = useTranslation ();

  const {
    listOtherIncome,
    handleDelete,
    handleOpenCreateEdit,
    handleSelectListDelete,
    handleOpenView,
    totalElements,
    totalPages,
    searchObject,
    handleChangePage,
    setPageSize
  } = otherIncomeStore;

  const {isAdmin, isManager, checkAllUserRoles} = useStore ().hrRoleUtilsStore;
  useEffect (() => {
    checkAllUserRoles ()
  }, []);
  const columns = [
    {
      title:t ("general.action"),
      width:"10%",
      minWidth:"100px",
      render:(rowData) => (
          <div className="flex flex-center">
            <Tooltip
                arrow
                placement="top"
                title={"Xem chi tiết"}
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
            {(isManager || isAdmin) && (
                <Tooltip
                    arrow
                    title={"Cập nhật thông tin"}
                    placement="top"
                >
                  <IconButton size="small" onClick={() => handleOpenCreateEdit (rowData?.id)}>
                    <Icon fontSize="small" color="primary">
                      edit
                    </Icon>
                  </IconButton>
                </Tooltip>
            )}
            {(isManager || isAdmin) && (
                <Tooltip
                    arrow
                    title={"Xóa thông tin"}
                    placement="top"
                >
                  <IconButton size="small" onClick={() => handleDelete (rowData?.id)}>
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
      title:t ("Tên khoản thu nhập/khấu trừ"),
      minWidth:"150px",
      field:"name"
    },
    {
      title:t ("Mã khoản thu nhập/khấu trừ"),
      minWidth:"150px",
      field:"code"
    },
    {
      title:t ("Kỳ lương"),
      field:"salaryPeriod",
      minWidth:"150px",
      render:(value) => value?.salaryPeriod?.name,
    },
    {
      title:t ("Số tiền"),
      field:"income",
      minWidth:"150px",
      render:(value) => formatVNDMoney (value?.income),
    },
    {
      title:t ("Loại khoản thu nhập/khấu trừ"),
      field:"type",
      minWidth:"150px",
      render:(value) => OtherIncomeType.getListData ().find ((item) => item?.value === value?.type)?.name,
    },
    {
      title:t ("Ngày quết định"),
      field:"decisionDate",
      minWidth:"150px",
      render:(value) => value?.decisionDate && formatDate ("DD/MM/YYYY", value?.decisionDate),
    },
    {
      title:t ("Ghi chú"),
      field:"note",
      minWidth:"150px",
    },
    // {
    //     title:t("Mô tả") ,
    //     field:"description" ,
    //     minWidth:"150px" ,
    // } ,

  ];
  return (
      <GlobitsTable
          selection
          handleSelectList={handleSelectListDelete}
          data={listOtherIncome}
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
});
