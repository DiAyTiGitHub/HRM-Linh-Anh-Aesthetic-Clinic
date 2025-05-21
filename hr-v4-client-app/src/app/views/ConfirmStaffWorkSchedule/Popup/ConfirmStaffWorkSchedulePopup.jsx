import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';
import { formatDate, getCheckInAndCheckOutTimeOfShiftWork, getDate } from 'app/LocalFunction';

function ConfirmStaffWorkSchedulePopup() {
    const { confirmStaffWorkScheduleStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmUpdateStatusPopup,
        handleClose,
        handleRemoveActionItem,
        listChosen,
        onUpdateStatus,
        handleSelectListChosen,
        pagingOvertimeRequest,
        handleConfirmUpdateStatus
    } = confirmStaffWorkScheduleStore;

    const columns = [
            {
                title:"Nhân viên yêu cầu" ,
                field:"staff" ,
                align:"left" ,
                minWidth:"150px" ,
                render:row => {
                    return <span>{row?.staff?.displayName}</span>
                }
            } ,
            {
                title:"Đơn vị" ,
                field:"organization" ,
                align:"left" ,
                minWidth:"150px" ,
                render:row => {
                    return <span>{row?.staff?.organization?.name}</span>
                }
            } ,
            {
                title:"Phòng ban" ,
                field:"department" ,
                align:"left" ,
                minWidth:"150px" ,
                render:(row) => (
                    <>
                        {row?.staff?.department?.name && <p className='m-0'>{row?.staff?.department?.name}</p>}
                        {row?.staff?.department?.code && <p className='m-0'>({row?.staff?.department?.code})</p>}
                    </>
                ) ,
            } ,
            {
                title:"Chức danh" ,
                field:"positionTitle" ,
                align:"left" ,
                minWidth:"150px" ,
                render:row => {
                    return <span>{row?.staff?.positionTitle?.name}</span>
                }
            } ,
            {
                title:"Ngày làm việc" ,
                field:"workingDate" ,
                width:"10%" ,
                align:"center" ,
                minWidth:"150px" ,
                render:row => <span className="px-2">{getDate(row?.workingDate)}</span>
            } ,
    
            {
                title:"Ca làm việc" ,
                width:"30%" ,
                align:"center" ,
                field:"shiftWork.name" ,
                minWidth:"150px" ,
                // render: row => <span className="px-2">{`${row?.shiftWork?.name} `}</span>
    
                render:(rowData) => {
                    const {shiftWork} = rowData;
                    const {
                        name ,
                        code ,
                    } = shiftWork;
    
                    const {
                        checkInTime ,
                        checkOutTime
                    } = getCheckInAndCheckOutTimeOfShiftWork(shiftWork);
    
                    return (
                        <div className="px-4">
                            {name && (
                                <p className='m-0 pb-4'>
                                    <span>
                                        <strong>{`${name} - ${code}`} </strong>
                                    </span>
                                </p>
                            )}
    
                            {(checkInTime && checkOutTime) && <p className='m-0 no-wrap-text'>
                                {/* Thời gian:  */}
                                {`${formatDate("HH:mm" , checkInTime)} - ${formatDate("HH:mm" , checkOutTime)}`}</p>}
    
                        </div>
                    );
                }
            } ,
    
            {
                title:"Giờ vào" ,
                width:"10%" ,
                align:"center" ,
                field:"firstCheckIn" ,
                minWidth:"150px" ,
                render:row => {
                    if (row?.firstCheckIn)
                        return (
                            <span className="px-2">{formatDate('HH:mm' , row?.firstCheckIn)}</span>
                        );
    
                    return "-";
                }
            } ,
    
            {
                title:"Giờ ra" ,
                width:"10%" ,
                align:"center" ,
                field:"lastCheckout" ,
                minWidth:"150px" ,
                render:row => {
                    if (row?.lastCheckout)
                        return (
                            <span className="px-2">{formatDate('HH:mm' , row?.lastCheckout)}</span>
                        );
    
                    return "-";
                }
            } ,
    
            {
                title:"Yêu cầu xác nhận trước ca" ,
                field:"requestOTHoursBeforeShift" ,
                align:"center" ,
                minWidth:"150px" ,
                render:row => {
                    return (
                        <span className="px-2">
                            {row?.requestOTHoursBeforeShift ? `${row?.requestOTHoursBeforeShift} giờ` : ''}
                        </span>
                    );
                }
            } ,
    
            {
                title:"Yêu cầu xác nhận sau ca" ,
                field:"requestOTHoursAfterShift" ,
                align:"center" ,
                minWidth:"150px" ,
                render:row => {
                    return (
                        <span className="px-2">
                            {row?.requestOTHoursAfterShift ? `${row?.requestOTHoursAfterShift} giờ` : ''}
                        </span>
                    );
                }
            } ,
    
            {
                title:"Trạng thái" ,
                field:"approvalStatus" ,
                align:"left" ,
                minWidth:"150px" ,
                render:row => {
                    return <span>{LocalConstants.OvertimeRequestApprovalStatus.getListData().find(i => i.value == row?.approvalStatus)?.name}</span>
                }
            } ,
    ];


    function handleCloseConfirmPopup() {
        handleClose();
        pagingOvertimeRequest();
    }

    return (
        <GlobitsColorfulThemePopup
            open={openConfirmUpdateStatusPopup}
            handleClose={handleCloseConfirmPopup}
            size="lg"
            onConfirm={handleConfirmUpdateStatus}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong>
                                    {listChosen?.length <= 1 ? "Thông tin " : "Danh sách "}
                                    yêu cầu được cập nhật thành {LocalConstants.OvertimeRequestApprovalStatus.getListData().find(i => i.value == onUpdateStatus)?.name?.toUpperCase()}
                                </strong>
                            </h6>
                            <GlobitsTable
                                data={listChosen}
                                handleSelectList={handleSelectListChosen}
                                columns={columns}
                                nonePagination
                            />
                        </div>
                    </Grid>
                </Grid>
            </div>
        </GlobitsColorfulThemePopup>
    );
}

export default memo(observer(ConfirmStaffWorkSchedulePopup));