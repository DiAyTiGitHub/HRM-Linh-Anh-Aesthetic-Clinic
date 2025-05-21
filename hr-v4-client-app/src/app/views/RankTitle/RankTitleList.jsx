import React, { memo, useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatVNDMoney } from "app/LocalFunction";


function RankTitleList () {
  const {rankTitleStore} = useStore ();
  const {t} = useTranslation ();

  const {
    listRankTitle,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
    handleOpenView
  } = rankTitleStore;
  const {
    isAdmin,
    checkAllUserRoles
  } = useStore ().hrRoleUtilsStore;

  useEffect (() => {
    checkAllUserRoles ();
  }, []);
  let columns = [
    {
      title:t ("general.action"),
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
            {(isAdmin) && (
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
            {(isAdmin) && (
                <Tooltip
                    arrow
                    title={"Xóa thông tin"}
                    placement="top"
                >
                  <IconButton size="small" onClick={() => handleDelete (rowData)}>
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
      title:"Tên cấp bậc",
      field:"name",
      minWidth:"150px",
      align:"left",
    },
    {
      title:"Mã cấp bậc",
      minWidth:"150px",
      field:"shortName",
      align:"left",
    },
    // {
    //   title: "Tên khác",
    //   field: "otherName",
    //   align: "left",
    // },
    {
      title:"Cấp bậc",
      field:"level",
      minWidth:"150px",
      align:"left",
    },
    {
      title:"Lương đóng BHXH",
      field:"socialInsuranceSalary",
      align:"left",
      minWidth:"150px",
      render:(rowData) => {
        return formatVNDMoney (rowData?.socialInsuranceSalary) + " VNĐ";
      },
    },
    {
      title:"Mức hưởng chi phí giới thiệu",
      field:"referralFeeLevel",
      align:"left",
      minWidth:"150px",
      render:(rowData) => {
        return formatVNDMoney (rowData?.referralFeeLevel) + " VNĐ";
      },
    },
    {
      title:"Mô tả",
      field:"description",
      align:"left",
      minWidth:"150px",
    },
  ];
  return (
      <GlobitsTable
          selection
          data={listRankTitle}
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

export default memo (observer (RankTitleList));
