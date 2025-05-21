import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import WorkCalendarTicket from "./WorkCalendarTicket";
import ConstantList from "app/appConfig";
import { useHistory } from "react-router-dom";
import AddIcon from "@material-ui/icons/Add";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles({
    stickyCellContent: {
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
        padding: "0 24px",
        textAlign: "center",
    },
    staffName: {
        margin: 0,
        fontWeight: "bold",
    },
    staffCode: {
        margin: 0,
    },
});

function WorkCalendarBody(props) {
    const classes = useStyles();

    const {
        staffWorkScheduleCalendarStore,
        hrRoleUtilsStore,
        staffWorkScheduleStore
    } = useStore();

    const {
        listWorkSchedules

    } = staffWorkScheduleCalendarStore;

    const {
        checkAllUserRoles,
        checkHasShiftAssignmentPermission,
        hasShiftAssignmentPermission

    } = hrRoleUtilsStore;

    const {
        handleOpenFormCreateMultipleStaffWorkSchedule

    } = staffWorkScheduleStore;

    const history = useHistory();

    useEffect(() => {
        checkAllUserRoles();
    }, []);

    return (
        <tbody>
            {listWorkSchedules?.map((staff) => {
                const { staffId, displayName, staffCode, workingSchedules } = staff;

                return (
                    <tr key={staffId} className='row-table-body row-table-no_data'>
                        <td className='stickyCell'>
                            <div className={classes.stickyCellContent}>
                                <div>
                                    <Tooltip title='Xem lịch làm việc tháng' placement='bottom'>
                                        <IconButton
                                            size='small'
                                            onClick={() =>
                                                history.push(
                                                    ConstantList.ROOT_PATH + `staff-month-schedule-calendar/` + staffId
                                                )
                                            }>
                                            <Icon fontSize='small' color='primary'>
                                                visibility
                                            </Icon>
                                        </IconButton>
                                    </Tooltip>
                                </div>

                                {displayName && (
                                    <p className={classes.staffName}>{displayName}</p>
                                )}

                                {staffCode && (
                                    <p className={classes.staffCode}>{staffCode}</p>
                                )}
                            </div>
                        </td>

                        {workingSchedules?.map(({ workingDate, shiftWorks }) => (
                            <td key={workingDate} className="work-calendar-cell">
                                {
                                    hasShiftAssignmentPermission 
                                    // && (!shiftWorks || shiftWorks.length == 0)
                                     && (
                                        <Tooltip
                                            title='Phân ca làm việc trong ngày'
                                            placement='top'
                                            arrow
                                        >
                                            <IconButton
                                                size='small'
                                                className='addMoreShiftButton'
                                                onClick={() =>
                                                    handleOpenFormCreateMultipleStaffWorkSchedule({
                                                        staff: staff?.staff,
                                                        department: staff?.staff?.department,
                                                        workingDate,
                                                        shiftWorks: [],
                                                    })
                                                }>
                                                <AddIcon
                                                    fontSize='small'
                                                    style={{ color: "white", fontSize: "15.4px" }}
                                                />
                                            </IconButton>
                                        </Tooltip>
                                    )
                                }

                                <div
                                    style={{
                                        display: "flex",
                                        flexDirection: "column",
                                        justifyContent: "center",
                                        textAlign: "center",
                                    }}>

                                    {shiftWorks?.map((shiftWork) => (
                                        <WorkCalendarTicket
                                            key={shiftWork?.id}
                                            workingDate={workingDate}
                                            shiftWork={shiftWork}
                                            staffId={staffId}
                                        />
                                    ))}
                                </div>
                            </td>
                        ))}
                    </tr>
                );
            })}
        </tbody>
    );
}

export default memo(observer(WorkCalendarBody));
