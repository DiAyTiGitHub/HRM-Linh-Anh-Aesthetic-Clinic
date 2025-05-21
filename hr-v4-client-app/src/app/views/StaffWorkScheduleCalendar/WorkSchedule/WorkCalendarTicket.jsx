import { Icon , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import React , { memo , useEffect , useState } from "react";
import EditIcon from '@material-ui/icons/Edit';
import DeleteIcon from "@material-ui/icons/Delete";
import { useStore } from "app/stores";
import ShiftWorkTooltipDetails from "./ShiftWorkTooltipDetails";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import localStorageService from "../../../services/localStorageService";
import CompareArrowsIcon from "@material-ui/icons/CompareArrows";

function WorkCalendarTicket(props) {
    const {staffWorkScheduleStore , staffWorkScheduleCalendarStore , hrRoleUtilsStore} = useStore();
    const {mapStatusToColor , mapStatusToBackgroundColor , getShiftWorkStatus} = staffWorkScheduleCalendarStore;


    const {shiftWork , workingDate} = props;

    const {
        id ,
        name ,
        code ,
        totalHours ,
        timePeriods ,
        staffWorkScheduleId ,
        timeSheetDetails ,
        workingStatus ,
        staffId ,
        leaveType
    } = shiftWork;

    const shiftWorkStatus = getShiftWorkStatus(workingDate , timeSheetDetails , workingStatus , leaveType);

    const kanbanCardStyle = {
        // borderLeftColor: cardProps?.color
        borderLeftColor:mapStatusToColor(shiftWorkStatus) ,
        backgroundColor:mapStatusToBackgroundColor(shiftWorkStatus)
    };
    return (
        <React.Fragment key={id}>
            <Tooltip arrow
                     title={
                         <ShiftWorkTooltipDetails
                             workingDate={workingDate}
                             timeSheetDetails={timeSheetDetails}
                             workingStatus={workingStatus}
                             leaveType={leaveType}
                         />}
                     placement='left'>
                <div className='shiftWorkTicket' style={kanbanCardStyle}>
                    <div className='flex align-start justify-between'>
                        <div>
                            <h5 className='card-name flex-grow-1'
                                style={{textAlign:"start"}}
                            >
                                {`${name} - ${code}`}
                            </h5>
                        </div>
                        <ButtonsTable
                            staffWorkScheduleId={staffWorkScheduleId}
                            shiftWorkStatus={shiftWorkStatus}
                            staffId={staffId}
                            shiftWork={shiftWork}
                            workingDate={workingDate}
                        />
                    </div>

                    {totalHours && (
                        <p className='cardAttribute text-left'>
                            <b>Số giờ:</b>
                            {` ${totalHours}`}
                        </p>
                    )}

                    {/*{timePeriods && timePeriods?.length > 0 && (*/}
                    {/*  <div className='w-100'>*/}
                    {/*    <p className='cardAttribute text-left'>*/}
                    {/*      <b>Thời gian:</b>*/}
                    {/*    </p>*/}

                    {/*    {Array.from(timePeriods)?.map(function (timePeriod, index) {*/}
                    {/*      return (*/}
                    {/*        <p className='cardAttribute text-left' key={index}>*/}
                    {/*          <span>*/}
                    {/*            {formatDate("HH:mm", timePeriod?.startTime)} - {formatDate("HH:mm", timePeriod?.endTime)}*/}
                    {/*          </span>*/}
                    {/*          /!* <span>{timePeriod?.startTime} - {timePeriod?.endTime}</span> *!/*/}
                    {/*        </p>*/}
                    {/*      );*/}
                    {/*    })}*/}
                    {/*  </div>*/}
                    {/*)}*/}

                </div>
            </Tooltip>
        </React.Fragment>
    );
}

export default memo(observer(WorkCalendarTicket));

const ButtonsTable = (props) => {
    const {hrRoleUtilsStore , staffWorkScheduleStore , shiftChangeRequestStore} = useStore();
    const {staffWorkScheduleId , shiftWorkStatus , staffId , shiftWork , workingDate} = props;
    const [anchorEl , setAnchorEl] = useState();
    const [openShiftChange , setOpenShiftChange] = useState(false);
    const {
        isManager ,
        isAdmin ,
        hasShiftAssignmentPermission ,
    } = hrRoleUtilsStore;

    const {
        handleDelete ,
        handleOpenCreateEdit ,
        handleOpenView
    } = staffWorkScheduleStore;

    const loginUser = localStorageService.getLoginUser()

    useEffect(() => {
        //shiftWorkStatus ==1 => chưa đến lịch làm việc
        let isShiftChange = (staffId === loginUser?.id || isManager || isAdmin || hasShiftAssignmentPermission) && shiftWorkStatus === 1;
        setOpenShiftChange(isShiftChange)
    } , [isManager , isAdmin , hasShiftAssignmentPermission ,]);


    const {
        handleSetSelectedShiftChangeRequest ,
        selectedShiftChangeRequest ,
        handleOpenCreateEdit:handleOpenShiftChangeRequest
    } = shiftChangeRequestStore;
    const handleShiftChangeRequest = () => {
        const newValue = {
            ... selectedShiftChangeRequest ,
            registerStaff:loginUser ,
            fromShiftWork:shiftWork ,
            fromWorkingDate:workingDate ,
            // staffWorkSchedule:
        }
        handleOpenShiftChangeRequest();
        handleSetSelectedShiftChangeRequest(newValue)
    }

    return (
        <>
            <Icon
                style={{fontSize:"15.4px"}}
                className='m-0 '
                onClick={(event) => setAnchorEl(event.currentTarget)}
            >
                more_vert
            </Icon>
            <Menu
                id='simple-menu'
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={() => setAnchorEl(null)}>
                <MenuItem onClick={() => handleOpenView(staffWorkScheduleId)}>
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Chi tiết ca làm việc"}
                    >
                        <div className='iconWrapper flex cursor-pointer'>
                            <Icon
                                style={{color:"green" , fontSize:"15.4px"}}
                                className='m-0 '
                            >
                                remove_red_eye
                            </Icon>
                        </div>
                    </Tooltip>
                </MenuItem>
                {(isManager || isAdmin || hasShiftAssignmentPermission) &&
                    <MenuItem onClick={() => {
                        handleOpenCreateEdit(staffWorkScheduleId);
                    }}>
                        <Tooltip arrow title='Sửa phân ca' placement='top'>
                            <div className='iconWrapper flex cursor-pointer'>
                                <EditIcon
                                    style={{fontSize:"15.4px" , color:"#484848"}}
                                    className='m-0 '
                                />
                            </div>
                        </Tooltip>
                    </MenuItem>
                }
                {(openShiftChange) && (
                    <MenuItem onClick={handleShiftChangeRequest}>
                        <Tooltip arrow title='Đổi ca làm việc' placement='top'>
                            <div className='iconWrapper flex cursor-pointer'>
                                <CompareArrowsIcon style={{fontSize:"20px" , color:"green"}}/>
                            </div>
                        </Tooltip>
                    </MenuItem>
                )}
                {(isManager || isAdmin || hasShiftAssignmentPermission) &&
                    <MenuItem onClick={() => {
                        const selectedItem = {
                            id:staffWorkScheduleId ,
                        };

                        handleDelete(selectedItem);
                    }}>
                        <Tooltip arrow title='Xóa lịch phân ca' placement='top'>
                            <div className='iconWrapper flex cursor-pointer'>
                                <DeleteIcon
                                    style={{color:"red" , fontSize:"15.4px"}}
                                    className='m-0 '
                                />
                            </div>
                        </Tooltip>
                    </MenuItem>}
            </Menu>
        </>
    );
};
