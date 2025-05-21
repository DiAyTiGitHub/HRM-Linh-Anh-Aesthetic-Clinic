import React , { memo , useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon , IconButton , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate , formatNumber , getCheckInAndCheckOutTimeOfShiftWork , getDate } from "app/LocalFunction";

function ConfirmOvertimeList(props) {
    const {t} = useTranslation();

    const {
        confirmOvertimeStore ,
        staffWorkScheduleStore ,
        hrRoleUtilsStore
    } = useStore();

    const {
        getStaffWorkScheduleWorkingStatusName
    } = staffWorkScheduleStore;

    const {
        listStaffWorkSchedules ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleOpenView
    } = confirmOvertimeStore;

    const {
        isOnChoosingMode ,
        selectedItem ,
        handleChooseItem
    } = props;
    const {
        isAdmin ,
        isManager ,
        checkAllUserRoles
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    } , []);
    let columns = [
        {
            title:t("general.action") ,
            width:"10%" ,
            minWidth:"100px" ,
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
                            onClick={() => handleOpenView(rowData?.id)}
                        >
                            <Icon fontSize="small" style={{color:"green"}}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>
                    {(isManager || isAdmin) && (
                        <Tooltip placement="top" arrow title="Thông tin chấm công theo lịch phân ca">
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData?.id)}>
                                <Icon fontSize="small" color="primary">
                                    add_alarm
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
            ) ,
        } ,
        {
            title:"Mã nhân viên" ,
            field:"staff.staffCode" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Nhân viên" ,
            field:"staff.displayName" ,
            minWidth:"150px" ,
            align:"left" ,
        } ,

        {
            title:"Ngày làm việc" ,
            field:"workingDate" ,
            minWidth:"150px" ,
            align:"left" ,
            render:row => <span className="px-2">{getDate(row?.workingDate)}</span>
        } ,

        {
            title:"Ca làm việc" ,
            minWidth:"150px" ,
            align:"left" ,
            field:"shiftWork.name" ,
            // render: row => <span className="px-2">{`${row?.shiftWork?.name} `}</span>

            render:(rowData) => {
                const {shiftWork} = rowData;
                const {
                    name ,
                    code ,
                    totalHours ,
                    timePeriods
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

                        {/* {totalHours && <p className='m-0 no-wrap-text'>Quy định: {formatVNDMoney(totalHours)} giờ</p>} */}

                        {(checkInTime && checkOutTime) && <p className='m-0 no-wrap-text'>
                            {/* Thời gian:  */}
                            {`${formatDate("HH:mm" , checkInTime)} - ${formatDate("HH:mm" , checkOutTime)}`}</p>}

                    </div>
                );
            }
        } ,

        {
            title:"Giờ vào" ,
            minWidth:"150px" ,
            align:"center" ,
            field:"firstCheckIn" ,
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
            minWidth:"150px" ,
            align:"center" ,
            field:"lastCheckout" ,
            render:row => {
                if (row?.lastCheckout)
                    return (
                        <span className="px-2">{formatDate('HH:mm' , row?.lastCheckout)}</span>
                    );

                return "-";
            }
        } ,

        // {
        //   title: "Trạng thái làm việc",
        //   field: "workingStatus",
        //   align: "center",
        //   render: row => <span>{getStaffWorkScheduleWorkingStatusName(row)}</span>
        // },

        // {
        //   title: "Số giờ quy định",
        //   field: "shiftWork.totalHours",
        //   width: "20%",
        //   align: "center",
        //   render: row => <span className="px-2">{formatVNDMoney(row?.shiftWork?.totalHours)}</span>
        // },

        {
            title:"Số giờ làm việc thực thế" ,
            field:"totalHours" ,
            minWidth:"150px" ,
            align:"center" ,
            render:row => <span className="px-2">{formatNumber(row?.totalHours)}</span>
        } ,

        {
            title:"Số giờ công quy đổi" ,
            field:"convertedWorkingHours" ,
            minWidth:"150px" ,
            align:"center" ,
            render:row => <span className="px-2">
        {formatNumber(row?.convertedWorkingHours)}
      </span>
        } ,

        // {
        //   title: "Loại làm việc",
        //   field: "workingType",
        //   render: row => <span className="px-2">{getStaffWorkScheduleWorkingTypeName(row?.workingType)}</span>,
        //   align: "center",
        // },

        // {
        //   title: "Số lần đi muộn",
        //   field: "lateArrivalCount",
        //   render: row => <span className="px-2">{row?.lateArrivalCount}</span>,
        //   align: "center",
        // },

        {
            title:"Số phút đi sớm" ,
            minWidth:"150px" ,
            field:"earlyArrivalMinutes" ,
            render:row => <span className="px-2">
        {row?.earlyArrivalMinutes || "0"}
      </span> ,
            align:"center" ,
        } ,

        {
            title:"Số phút đi muộn" ,
            minWidth:"150px" ,
            field:"lateArrivalMinutes" ,
            render:row => <span className="px-2">{row?.lateArrivalMinutes || "0"}</span> ,
            align:"center" ,
        } ,

        // {
        //   title: "Số lần về sớm",
        //   field: "earlyExitCount",
        //   render: row => <span className="px-2">{row?.earlyExitCount}</span>,
        //   align: "center",
        // },

        {
            title:"Số phút về sớm" ,
            minWidth:"150px" ,
            field:"earlyExitMinutes" ,
            render:row => <span className="px-2">{row?.earlyExitMinutes || "0"}</span> ,
            align:"center" ,
        } ,

        {
            title:"Số phút về muộn" ,
            minWidth:"150px" ,
            field:"lateExitMinutes" ,
            render:row => <span className="px-2">{row?.lateExitMinutes || "0"}</span> ,
            align:"center" ,
        } ,

        {
            title:"Số giờ OT trước ca được xác nhận" ,
            field:"confirmedOTHoursBeforeShift" ,
            minWidth:"200px" ,
            align:"center" ,
            render:row => <span className="px-2">
        {formatNumber(row?.confirmedOTHoursBeforeShift)}
      </span>
        } ,

        {
            title:"Số giờ OT sau ca được xác nhận" ,
            field:"confirmedOTHoursAfterShift" ,
            minWidth:"200px" ,
            align:"center" ,
            render:row => <span className="px-2">
        {formatNumber(row?.confirmedOTHoursAfterShift)}
      </span>
        } ,

        {
            title:"Người xác nhận OT" ,
            field:"otEndorser" ,
            minWidth:"150px" ,
            align:"center" ,
            render:row => <span className="px-2">
        {`${row?.otEndorser?.displayName || ''} - ${row?.otEndorser?.staffCode || ''}`}
      </span>
        } ,
    ];

    let displayColumns = [
        ... columns
    ];
    // if (searchObject?.workingStatus == 0) {
    //   displayColumns = [
    //     ...displayColumns,
    //     {
    //       title: "Trạng thái làm việc",
    //       field: "workingStatus",
    //       render: row => <span>{getStaffWorkScheduleWorkingStatusName(row)}</span>
    //     },
    //   ]
    // }

    return (
        <GlobitsTable
            // selection
            data={listStaffWorkSchedules}
            handleSelectList={handleSelectListDelete}
            columns={displayColumns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10 , 15 , 25 , 50 , 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(ConfirmOvertimeList));
