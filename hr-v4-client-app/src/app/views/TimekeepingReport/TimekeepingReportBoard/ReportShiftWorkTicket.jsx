import {Tooltip} from "@material-ui/core";
import {observer} from "mobx-react";
import React, {memo} from "react";
import MoreVertIcon from "@material-ui/icons/MoreHoriz";
import DeleteIcon from '@material-ui/icons/Delete';
import {formatDate} from "app/LocalFunction";
import {useStore} from "app/stores";

function ReportShiftWorkTicket(props) {
    const {
        shiftWork,
        workingDate
    } = props;

    const {
        id,
        name,
        code,
        totalHours,
        timePeriods,
        staffWorkScheduleId,
        timeSheetDetails,
        workingStatus
    } = shiftWork;

    const {
        staffWorkScheduleStore,
        staffWorkScheduleCalendarStore
    } = useStore();

    const {
        mapStatusToColor,
        getShiftWorkStatus,
        mapStatusToBackgroundColor
    } = staffWorkScheduleCalendarStore;

    const shiftWorkStatus = getShiftWorkStatus(workingDate, timeSheetDetails, workingStatus);

    const kanbanCardStyle = {
        // borderLeftColor: cardProps?.color
        borderLeftColor: mapStatusToColor(shiftWorkStatus),
        backgroundColor: mapStatusToBackgroundColor(shiftWorkStatus)
    }

    return (
        <div
            className='shiftWorkTicket'
            style={kanbanCardStyle}
            key={id}
        >
            <div className='flex align-start justify-between pb-4'>
                <div>
                    <h5 className='card-name flex-grow-1'>{`${name} - ${code}`}</h5>
                </div>

                <div className="flex align-center justify-center">

                </div>
            </div>

            {totalHours && (
                <p className='cardAttribute text-left '>
                    <b>Số giờ:</b>
                    {` ${totalHours}`}
                </p>
            )}

            {timePeriods && timePeriods?.length > 0 && (
                <div className='w-100'>
                    <p className='cardAttribute text-left '>
                        <b>Thời gian:</b>
                    </p>

                    {Array.from(timePeriods)?.map(function (timePeriod, index) {

                        return (
                            <p className='cardAttribute text-left ' key={index}>
                                <span>
                                    {formatDate("HH:mm", timePeriod?.startTime)} - {formatDate("HH:mm", timePeriod?.endTime)}
                                </span>
                            </p>
                        );
                    })}
                </div>
            )}

            {(timeSheetDetails?.length > 0) && (
                <table
                    className="text-black mt-4"
                    style={{
                        borderCollapse: 'collapse',
                        width: '100%',
                        border: '1px solid '
                    }}
                >
                    <thead>
                    <tr>
                        <th
                            className={"text-black p-4 "}
                            style={{border: '1px solid'}}
                        >
                            Checkin
                        </th>
                        <th
                            className={"text-black p-4 "}
                            style={{border: '1px solid'}}
                        >
                            Checkout
                        </th>
                    </tr>
                    </thead>

                    <tbody>
                    {timeSheetDetails?.map((detail, index) => (
                        <tr key={index}>
                            <td
                                className={"text-black p-4 "}
                                style={{border: '1px solid'}}
                            >
                                {formatDate
                                ('HH:mm', detail?.startTime)}
                            </td>
                            <td
                                className={"text-black p-4 "}
                                style={{border: '1px solid'}}
                            >
                                {formatDate
                                ('HH:mm', detail?.endTime)}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}

        </div>
    );
}

export default memo(observer(ReportShiftWorkTicket));