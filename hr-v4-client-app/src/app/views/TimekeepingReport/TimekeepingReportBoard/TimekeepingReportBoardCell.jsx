import React, { memo, useEffect, useMemo, useState } from "react";
import { Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CellReportTooltipDetails from "./CellReportTooltipDetails";
import { formatNumber } from 'app/LocalFunction';
import { useStore } from "app/stores";

function TimekeepingReportBoardCell(props) {
  const {
    workingSchedule,
    staffId,
  } = props;

  const {
    timekeepingReportStore,
    staffWorkScheduleStore
  } = useStore();

  if (!workingSchedule) return null; // Handle missing data gracefully

  const {
    handleOpenViewSchedulesInDays
  } = timekeepingReportStore;

  const {
    openViewStatistic,
    handleOpenViewStatistic
  } = staffWorkScheduleStore;

  const {
    workingDate,
    shiftWorks,
    totalAssignHours,
    totalWorkingHours
  } = workingSchedule;

  const getTotalWorkingHoursStyle = (totalWorkingHours, totalAssignHours) => ({
    fontWeight: "bold",
    color: totalWorkingHours < totalAssignHours ? "red" : "green", // Red for underhours, green for sufficient hours
  });

  async function handleViewCellDetails() {
    try {
      if (shiftWorks == null || shiftWorks.length == 0) {
        console.log("no shift displayed");
        return;
      }

      if (shiftWorks.length == 1) {
        handleOpenViewStatistic(shiftWorks[0].staffWorkScheduleId);
        return;
      }

      handleOpenViewSchedulesInDays(staffId, workingDate);

    }
    catch (error) {
      console.error(error);
    }
  }

  return (
    <Tooltip
      title={
        <CellReportTooltipDetails
          workingSchedule={workingSchedule}
        />
      }
      placement="left"
      arrow
    >
      <div
        className="px-4 reportCell"
        onClick={handleViewCellDetails}
      >
        {(totalWorkingHours || totalAssignHours) ? (

          <span>
            <span
              className="font-size-14"
              style={getTotalWorkingHoursStyle(totalWorkingHours, totalAssignHours)}
            >
              {formatNumber(totalWorkingHours)}
            </span>
            <span className="font-size-12">
              {" / "}
              {formatNumber(totalAssignHours)}
            </span>

          </span>

        ) : (
          <>-</>
        )}
      </div>

    </Tooltip>
  );
}

export default memo(observer(TimekeepingReportBoardCell));
