import React, { memo, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, formatMoney } from "app/LocalFunction";
import PostAddIcon from "@material-ui/icons/PostAdd";
import ConstantList from "../../appConfig";
import { useHistory } from "react-router-dom";
import ExportHICInfo from "./ExportHICInfo";

function StaffHasSocialInsuranceList() {
  const { staffHasSocialInsuranceStore } = useStore();
  const { t } = useTranslation();

  const {
    listStaffSocialInsurance,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleSelectListDelete,
    handleOpenConfirmChangeStatus,
    handleExportHICInfoToWord
  } = staffHasSocialInsuranceStore;

  const [anchorEl, setAnchorEl] = useState();
  const [selectedRow, setSelectedRow] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [idStaff, setIdStaff] = useState()
  const history = useHistory();

  const stafftName = (dataStaff) => (
    <a onClick={() => { history.push(ConstantList.ROOT_PATH + "staff/edit/" + dataStaff.id) }} style={{ cursor: "pointer", fontWeight: "450" }}>
      {dataStaff.displayName}
    </a>
  );

  function renderInsuranceAmount({ value, percentageKey }) {
    const insuranceSalary = value?.insuranceSalary || 0;
    const percentage = value?.[percentageKey] || 0;
    const currency = value?.id ? '' : ' VNĐ';
    const calculatedAmount = (insuranceSalary * percentage) / 100;

    // Nếu là dòng tổng (không có id) và không có phần trăm thì không hiển thị gì
    if (!value?.id && percentage === 0) return null;

    return (
      <span style={{ whiteSpace: 'pre-line' }}>
        {calculatedAmount.toLocaleString()} {currency}
        {percentage ? `\n(${percentage}%)` : ''}
      </span>
    );
  }

  let columns = [
    {
      title: t("general.action"),
      align: "center",
      field: "action",
      width: "3%",
      render: (rowData) => (
        <div>
          {rowData?.staff?.id && (
            <>
              {/* <Tooltip title="Cập nhật thông tin nhân viên" placement="top">
                <IconButton component={NavLink} size="small"
                  to={ConstantList.ROOT_PATH + "staff/edit/" + rowData.id}>
                  <Icon fontSize="small" color="primary">
                    edit
                  </Icon>
                </IconButton>
              </Tooltip> */}

              <Tooltip title="Tải xuống tờ khai gia hạn" placement="top">
                <IconButton size="small" onClick={() => {
                  setIdStaff(rowData?.staff?.id)
                  setOpenDialog(true)
                }}>
                  <PostAddIcon fontSize="small" color="primary">
                  </PostAddIcon>
                </IconButton>
              </Tooltip>
            </>

          )}
        </div>
      )
    },
    {
      title: "Mã nhân viên",
      field: "staffCode",
      align: "center",
      // render: (rowData) => (
      //   <span className="px-6">
      //         {rowData?.staffCode}
      //       </span>
      // ),
      render: function (rowData) {
        // if (!rowData?.id) return "Tổng cộng";
        return (
          <span className="px-6">
            {rowData?.staff?.staffCode}
          </span>
        );

      }
    },
    {
      title: "Nhân viên",
      minWidth: "200px",
      render: function (rowData) {
        if (!rowData?.staff?.id) return "Tổng cộng";
        return (
          <>
            {rowData?.staff && (
              <p className="m-0">
                <strong>
                  {stafftName(rowData?.staff)}
                  {/* {rowData.displayName} */}
                </strong>
              </p>
            )}

            {rowData?.staff?.birthDate && (
              <p className="m-0">Ngày sinh: {formatDate("DD/MM/YYYY", rowData?.staff?.birthDate)}</p>
            )}

            {rowData?.staff?.gender && (
              <p className="m-0">Giới
                tính: {rowData?.staff?.gender === "M" ? "Nam" : rowData?.staff?.gender === "F" ? "Nữ" : ""}</p>
            )}

            {rowData?.staff?.birthPlace && (
              <p className="m-0">Nơi sinh: {rowData?.staff?.birthPlace}</p>
            )}
          </>
        );

      }
    },
    {
      title: "Đơn vị",
      field: "staffOrganizationName",
      align: "left",
      minWidth: "120px",
      render: (rowData) => (
        <span className="pr-6">
          {rowData?.staff?.organization?.name}
        </span>
      ),
    },
    {
      title: "Phòng ban",
      field: "staffDepartmentName",
      align: "left",
      minWidth: "120px",
      render: (rowData) => (
        <span className="pr-6">
          {rowData?.staff?.department?.name}
        </span>
      ),
    },
    {
      title: "Vị trí",
      field: "staffCurrentPositionName",
      align: "left",
      minWidth: "120px",
      render: (rowData) => (
        <span className="pr-6">
          {rowData?.staff?.currentPosition?.name}
        </span>
      ),
    },
    {
      align: "center",
      title: "Ngày bắt đầu đóng BHXH",
      field: "insuranceStartDate",
      minWidth: "120px",
      render: function (value) {
        return formatDate("DD/MM/YYYY", value?.insuranceStartDate);
      }
    },
    {
      align: "center",
      title: "Ngày kết thúc đóng BHXH",
      field: "insuranceEndDate",
      minWidth: "120px",
      render: function (value) {
        return formatDate("DD/MM/YYYY", value?.insuranceEndDate);
      }
    },
    {
      align: "center",
      title: "Mức lương đóng BHXH",
      field: "insuranceSalary",
      minWidth: "120px",
      // render: (value) => formatMoney(value?.salaryInsurance)
      render: function (value) {
        const res = formatMoney(value?.insuranceSalary || 0);
        if (!res) return "";
        if (value?.id) {
          return res;
        }
        return res + " VNĐ"
      }
    },
    {
      align: "center",
      title: "Số tiền BHXH của nhân viên đóng",
      field: "socialInsurance",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'staffSocialInsurancePercentage',
        });
      }
    },
    {
      align: "center",
      title: "Số tiền BHYT nhân viên đóng",
      field: "healthInsurance",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'staffHealthInsurancePercentage',
        });
      }
    },
    {
      align: "center",
      title: "Số tiền BHTN nhân viên đóng",
      field: "unemploymentInsurance",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'staffUnemploymentInsurancePercentage',
        });
      }
    },
    {
      align: "center",
      title: "Tổng tiền bảo hiểm nhân viên đóng",
      field: "staffTotalInsuranceAmount",
      minWidth: "120px",
      render: function (value) {
        const res = formatMoney(value?.staffTotalInsuranceAmount || 0);
        if (!res) return "";
        if (value?.id) {
          return res;
        }
        return res + " VNĐ"
      }
    },
    {
      align: "center",
      title: "Số tiền BHXH của công ty đóng",
      field: "socialInsurance",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'orgSocialInsurancePercentage',
        });
      }
    },
    {
      align: "center",
      title: "Số tiền BHYT công ty đóng",
      field: "healthInsurance",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'orgHealthInsurancePercentage',
        });
      }
    },
    {
      align: "center",
      title: "Số tiền BHTN công ty đóng",
      field: "unemploymentInsurance",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'orgUnemploymentInsurancePercentage',
        });
      }
    },
    {
      align: "center",
      title: "Tổng tiền bảo hiểm công ty đóng",
      field: "orgTotalInsuranceAmount",
      minWidth: "120px",
      render: function (value) {
        const res = formatMoney(value?.orgTotalInsuranceAmount || 0);
        if (!res) return "";
        if (value?.id) {
          return res;
        }
        return res + " VNĐ"
      }
    },
    // {
    //   align: "center",
    //   title: "Số tiền đóng phí công đoàn",
    //   field: "unionDuesAmount",
    //   render: function (value) {
    //     const res = formatMoney(value?.unionDuesAmount || 0);
    //     const staffPercentage = value?.unionDuesPercentage;
    //     const currency = value?.id ? '' : " VNĐ"
    //     return (
    //       <span style={{ whiteSpace: "pre-line" }}>
    //         {res} {currency} {staffPercentage ? `\n(${staffPercentage}%)` : ""}
    //       </span>
    //     );
    //   }
    // },
    {
      align: "center",
      title: "Tổng tiền",
      minWidth: "120px",
      field: "totalInsuranceAmount",
      render: function (value) {
        const res = formatMoney(value?.totalInsuranceAmount);
        if (value?.id) {
          return res;
        }
        return res + " VNĐ"
      }
    },
  ];

  // function handleClosePopover() {
  //   setAnchorEl(null);
  //   setSelectedRow(null);
  // }

  return (
    <>

      <GlobitsTable
        // selection
        data={listStaffSocialInsurance}
        handleSelectList={handleSelectListDelete}
        columns={columns}
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setPageSize}
        pageSize={searchObject?.pageSize}
        pageSizeOption={[10, 25, 50]}
        totalElements={totalElements}
        specialStyleForLastRow={true}
        page={searchObject?.pageIndex}
      />

      {
        openDialog && (
          <ExportHICInfo
            open={openDialog}
            handleClose={() => setOpenDialog(false)}
            handleExport={(fileName) =>
              handleExportHICInfoToWord(idStaff, fileName)
            }
          />
        )
      }

      {/* {Boolean(anchorEl) && (
        <Menu
          id={"simple-menu-options"}
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={handleClosePopover}
          className="py-0"
        >
          {selectedRow?.paidStatus != LocalConstants.StaffSocialInsurancePaidStatus.PAID.value && (
            <MenuItem className="flex items-center justify-center" onClick={function () {
              handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
              handleOpenConfirmChangeStatus(LocalConstants.StaffSocialInsurancePaidStatus.PAID.value);
              handleClosePopover();
            }}>
              <Icon className="pr-6" fontSize="small">
                attach_money
              </Icon>
              Chi trả
            </MenuItem>
          )}


          {selectedRow?.paidStatus != LocalConstants.StaffSocialInsurancePaidStatus.UNPAID.value && (
            <MenuItem className="flex items-center justify-center" onClick={function () {
              handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
              handleOpenConfirmChangeStatus(LocalConstants.StaffSocialInsurancePaidStatus.UNPAID.value);
              handleClosePopover();
            }}>
              <Icon className="pr-6" fontSize="small" style={{ color: "green" }}>
                money_off
              </Icon>
              Đặt lại
            </MenuItem>
          )}
        </Menu>
      )} */}
    </>
  );
}

export default memo(observer(StaffHasSocialInsuranceList));
