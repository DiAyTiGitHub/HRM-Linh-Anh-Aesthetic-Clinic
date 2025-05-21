import { observer } from "mobx-react";
import React, { memo } from "react";
import { useStore } from "app/stores";
import ReportShiftWorkTicket from "./ReportShiftWorkTicket";


function CellReportTooltipDetails(props) {
    const {
        workingSchedule,
    } = props;


    const {
        workingDate,
        shiftWorks,
        totalAssignHours,
        totalWorkingHours

    } = workingSchedule;


    return (
        <React.Fragment>
            <div
            >
                {(shiftWorks?.length === 0) && (
                    <div className="flex align-start justify-center">
                        <p
                            className="card-name flex-grow-1 text-center"
                        >
                            Nhân viên không có ca làm việc
                        </p>
                    </div>
                )}

                {shiftWorks?.map(function (shiftWork, index) {

                    return (
                        <ReportShiftWorkTicket
                            key={index}
                            workingDate={workingDate}
                            shiftWork={shiftWork}
                        />
                    );
                })}

            </div>
        </React.Fragment>

    );
}

export default memo(observer(CellReportTooltipDetails));



