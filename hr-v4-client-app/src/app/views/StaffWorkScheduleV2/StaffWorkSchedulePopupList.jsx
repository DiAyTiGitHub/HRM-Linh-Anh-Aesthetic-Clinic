import { formatDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useMemo, useCallback } from "react";
import { useTranslation } from "react-i18next";
import { Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import GlobitsPagination from "../../common/GlobitsPagination";
import StaffWorkScheduleStatisticPopup from "./StaffWorkScheduleStatisticPopup";
import "app/views/TimekeepingReport/TimekeepingReportBoard/TimekeepingReportBoardStyles.scss";

const useStyles = makeStyles((theme) => ({
  widthTh: {
    "& th": {
      minWidth: "150px",
    }
  },
  tableContainer: {
    overflowX: "auto"
  }
}));

function StaffWorkSchedulePopupList({ oneStaff }) {
  const { t } = useTranslation();
  const classes = useStyles();

  const { staffWorkScheduleStore } = useStore();

  const {
    listStaffWorkSchedules,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    getStaffWorkScheduleWorkingStatusName,
    getTotalConfirmedOTHours,
    totalStaffWorkSchedule,
    openViewStatistic
  } = staffWorkScheduleStore;

  // Memoize the getTotalConfirmedOTHours function to prevent recreating on every render
  const memoizedGetTotalConfirmedOTHours = useCallback((rowData) => {
    return getTotalConfirmedOTHours(rowData);
  }, [getTotalConfirmedOTHours]);

  // Memoize the getStaffWorkScheduleWorkingStatusName function
  const memoizedGetWorkingStatusName = useCallback((rowData) => {
    return getStaffWorkScheduleWorkingStatusName(rowData);
  }, [getStaffWorkScheduleWorkingStatusName]);

  // Memoize the table rows to prevent recreation on every render
  const tableRows = useMemo(() => {
    return listStaffWorkSchedules?.map((rowData, index) => (
      <TableRow
        key={rowData?.id || index}
        rowData={rowData}
        index={index}
        getStaffWorkScheduleWorkingStatusName={memoizedGetWorkingStatusName}
        getTotalConfirmedOTHours={memoizedGetTotalConfirmedOTHours}
        isTotalRow={false}
      />
    ));
  }, [listStaffWorkSchedules, memoizedGetWorkingStatusName, memoizedGetTotalConfirmedOTHours]);


  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <section className={`commonTableContainer ${classes.tableContainer}`}>
          <table className={`commonTable w-100`}>
            <TableHeader />
            <tbody>
              <TableRow
                key={totalStaffWorkSchedule?.staffCode}
                rowData={totalStaffWorkSchedule}
                index={totalStaffWorkSchedule?.staffCode}
                getStaffWorkScheduleWorkingStatusName={memoizedGetWorkingStatusName}
                getTotalConfirmedOTHours={memoizedGetTotalConfirmedOTHours}
                isTotalRow={true}
              />
              {tableRows}
            </tbody>
          </table>
        </section>
      </Grid>

      <Grid item xs={12}>
        <GlobitsPagination
          totalPages={totalPages}
          handleChangePage={handleChangePage}
          setRowsPerPage={setPageSize}
          pageSize={searchObject?.pageSize}
          pageSizeOption={[10, 15, 25, 50, 100]}
          totalElements={totalElements}
          page={searchObject?.pageIndex}
        />
      </Grid>
      {
        openViewStatistic && (
          <StaffWorkScheduleStatisticPopup />
        )
      }
    </Grid>
  );
}

export default memo(observer(StaffWorkSchedulePopupList));

const TableHeader = memo(() => {
  const classes = useStyles();

  return (
    <thead>
      <tr className={`${classes.widthTh} tableHeader`}>
        <th
          align="center" className="stickyColumn stickyHeader actionStaffSWS-column" style={{ minWidth: "100px" }}>
          Hành động
        </th>
        <th
          align="center" className="stickyColumn stickyHeader displayNameStaffSWS-column">
          Nhân viên
        </th>
        <th
          align="center" className="stickyColumn stickyHeader workingDateSWS-column">
          Ngày làm việc
        </th>
        <th className="stickyHeader" align="center">
          Ca làm việc
        </th>
        <th className="stickyHeader" align="center">
          Người phân ca
        </th>
        <th className="stickyHeader" align="center">
          Trạng thái phê duyệt
        </th>
        <th className="stickyHeader" align="center">
          Số giờ làm việc ước tính (giờ)
        </th>
        <th className="stickyHeader" align="center">
          Loại nghỉ
        </th>
        <th className="stickyHeader" align="center">
          Đi muộn (phút)
        </th>
        <th className="stickyHeader" align="center">
          Đi sớm (phút)
        </th>
        <th className="stickyHeader" align="center">
          Về muộn (phút)
        </th>
        <th className="stickyHeader" align="center">
          Về sớm (phút)
        </th>
        <th className="stickyHeader" align="center">
          OT trước ca (giờ)
        </th>
        <th className="stickyHeader" align="center">
          OT sau ca (giờ)
        </th>
        <th className="stickyHeader" align="center">
          Người xác nhận OT
        </th>
        <th className="stickyHeader" align="center">
          Tổng số giờ làm (giờ)
        </th>
        <th className="stickyHeader" align="center">
          Giờ làm hợp lệ (giờ)
        </th>
        <th className="stickyHeader" align="center">
          Giờ nghỉ có lương (giờ)
        </th>
        <th className="stickyHeader" align="center">
          Giờ nghỉ không lương (giờ)
        </th>
        <th className="stickyHeader" align="center">
          OT được xác nhận (giờ)
        </th>
        <th className="stickyHeader" align="center">
          Công quy đổi (giờ)
        </th>
        <th className="stickyHeader" align="center">
          Ngày công được tính (ngày công)
        </th>
        <th className="stickyHeader" align="center">
          Nghỉ có lương (ngày công)
        </th>
        <th className="stickyHeader" align="center">
          Nghỉ không lương (ngày công)
        </th>
        <th className="stickyHeader" align="center">
          Trạng thái làm việc
        </th>
      </tr>
    </thead>
  );
});

const TableRow = memo(({
  rowData,
  index,
  getStaffWorkScheduleWorkingStatusName,
  getTotalConfirmedOTHours,
  isTotalRow
}) => {
  const { staffWorkScheduleStore } = useStore();

  const {
    handleOpenCreateEdit,
    handleOpenView
  } = staffWorkScheduleStore;

  return (
    <tr className={`row-table-body row-table-no_data`}>
      <td className={`${isTotalRow ? "lastRowCellStyle" : ""} stickyColumn stickyHeader actionStaffSWS-column`}
        style={{ minWidth: "100px" }}>
        {!isTotalRow && (
          <Tooltip
            arrow
            placement="top"
            title={"Xem chi tiết"}
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
        )}
        {!isTotalRow && (
          <Tooltip
            arrow
            title={"Cập nhật thông tin"}
            placement="top"
          >
            <IconButton
              size="small"
              onClick={() => {
                if (!isTotalRow) {
                  handleOpenCreateEdit(rowData?.id);
                }
              }}
            >
              <Icon fontSize="small" color="primary">
                edit
              </Icon>
            </IconButton>
          </Tooltip>

        )}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} stickyColumn stickyHeader displayNameStaffSWS-column`}>
        {rowData?.staff?.displayName}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} stickyColumn stickyHeader workingDateSWS-column`}>
        {isTotalRow ? "" : formatDate("DD/MM/YYYY", rowData?.workingDate)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {isTotalRow ? "" : rowData?.shiftWork?.name}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {isTotalRow ? "" : rowData?.coordinator?.displayName || "Không xác định"}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {isTotalRow ? "" : getApprovalStatusName(rowData?.approvalStatus, rowData)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.estimatedWorkingHours || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {isTotalRow ? "" : rowData?.leaveType?.name}
      </td>
      {/*<td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>*/}
      {/*    {rowData?.lateArrivalCount || 0}*/}
      {/*</td>*/}
      {/*<td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>*/}
      {/*    {rowData?.earlyExitCount || 0}*/}
      {/*</td>*/}
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {rowData?.lateArrivalMinutes || 0}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {rowData?.earlyArrivalMinutes || 0}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {rowData?.lateExitMinutes || 0}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {rowData?.earlyExitMinutes || 0}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.confirmedOTHoursBeforeShift || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.confirmedOTHoursAfterShift || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {isTotalRow ? "" : `${rowData?.otEndorser?.displayName || ""} - ${rowData?.otEndorser?.staffCode || ""}`}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.totalHours || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.totalValidHours || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.paidLeaveHours || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.unpaidLeaveHours || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(getTotalConfirmedOTHours(rowData)) || 0}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.convertedWorkingHours || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.totalPaidWork || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.paidLeaveWorkRatio || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {roundToTwo(rowData?.unpaidLeaveWorkRatio || 0)}
      </td>
      <td className={`${isTotalRow && "lastRowCellStyle"} px-6 center no-wrap-text`}>
        {isTotalRow ? "" : getStaffWorkScheduleWorkingStatusName(rowData)}
      </td>
    </tr>
  );
}, (prevProps, nextProps) => {
  return JSON.stringify(prevProps.rowData) === JSON.stringify(nextProps.rowData);
});
const roundToTwo = (num) => {
  if (!num) return "0.00"; // Handle null, undefined, or 0 cases explicitly
  return (Math.round(num * 100) / 100).toFixed(2);
};

const getApprovalStatusName = (status, data) => {
  if (!data?.needManagerApproval) {
    return "Không cần duyệt"
  }
  switch (status) {
    case 1:
      return "Chưa duyệt";
    case 2:
      return "Đã duyệt";
    case 3:
      return "Không duyệt";
    default:
      return "Không xác định";
  }
};