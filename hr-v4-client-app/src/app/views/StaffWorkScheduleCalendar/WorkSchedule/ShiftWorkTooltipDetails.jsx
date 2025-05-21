import { Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import React , { memo } from "react";
import MoreVertIcon from "@material-ui/icons/MoreHoriz";
import DeleteIcon from '@material-ui/icons/Delete';
import { formatDate } from "app/LocalFunction";
import { useStore } from "app/stores";

function ShiftWorkTooltipDetails(props) {
    const {
        timeSheetDetails ,
        workingDate ,
        workingStatus ,
        leaveType
    } = props;

    const {
        staffWorkScheduleCalendarStore
    } = useStore();

    const {
        getShiftWorkStatus ,
        mapTicketStatusToText
    } = staffWorkScheduleCalendarStore;

    const shiftWorkStatus = getShiftWorkStatus(workingDate , timeSheetDetails , workingStatus , leaveType);

    return (
        <React.Fragment>
            <div
            >

                <div className="flex align-start justify-center">
                    <p
                        className="card-name flex-grow-1 text-center"
                    >
                        {mapTicketStatusToText(shiftWorkStatus)}
                    </p>
                </div>

                {(timeSheetDetails?.length > 0) && (
                    <table
                        className="mt-4"
                        style={{
                            borderCollapse:'collapse' ,
                            width:'100%' ,
                            border:'1px solid white'
                        }}
                    >
                        <thead>
                        <tr>
                            <th
                                className={"p-4"}
                                style={{border:'1px solid white'}}
                            >
                                Checkin
                            </th>
                            <th
                                className={"p-4"}
                                style={{border:'1px solid white'}}
                            >
                                Checkout
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        {timeSheetDetails?.map((detail , index) => (
                            <tr key={index}>
                                <td
                                    className={"p-4"}
                                    style={{border:'1px solid white'}}
                                >
                                    {formatDate
                                    ('HH:mm' , detail?.startTime)}
                                </td>
                                <td
                                    className={"p-4"}
                                    style={{border:'1px solid white'}}
                                >
                                    {formatDate
                                    ('HH:mm' , detail?.endTime)}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}


            </div>
        </React.Fragment>

    );
}

export default memo(observer(ShiftWorkTooltipDetails));