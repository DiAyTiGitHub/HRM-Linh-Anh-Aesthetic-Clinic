import React , { memo , useState } from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { IconButton , Icon , Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { formatDate , getCheckInAndCheckOutTimeOfShiftWork , getDate } from "app/LocalFunction";
import { useHistory } from "react-router-dom/cjs/react-router-dom";
import LocalConstants from "app/LocalConstants";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import CheckIcon from '@material-ui/icons/Check';

function ConfirmStaffWorkScheduleList() {
    const history = useHistory();
    const {t} = useTranslation();

    const {
        confirmStaffWorkScheduleStore ,
        hrRoleUtilsStore,
        staffWorkScheduleStore
    } = useStore();

    const {
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        listConfirmStaffWorkSchedule ,
        handleSelectListDelete ,
        handleOpenConfirmUpdateStatusPopup ,
        listChosen ,
        
    } = confirmStaffWorkScheduleStore;

    const {
        handleOpenCreateEdit,
        handleOpenView
    } = staffWorkScheduleStore;

    const {isAdmin , isManager} = hrRoleUtilsStore;

    const columns = [
        {
            title:t("general.action") ,
            width:"10%" ,
            minWidth:"100px" ,
            align:"center" ,
            render:(rowData) => {
                console.log("rowData",rowData)
                let displayCU = false;

                if (isAdmin || isManager) {
                    displayCU = true;
                }

                if (!isAdmin && !isManager && rowData?.approvalStatus != LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value) {
                    displayCU = true;
                }

                return (
                    <div className="flex flex-middle justify-center">

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
                        
                        {(isAdmin || isManager) && (
                            <Tooltip title="Thao tác khác" placement="top">
                                <IconButton
                                    className="ml-8"
                                    size="small"
                                    onClick={(event) => {
                                        setSelectedRow(rowData);
                                        setAnchorEl(event?.currentTarget);
                                    }}
                                >
                                    <MoreHorizIcon/>
                                </IconButton>
                            </Tooltip>
                        )}

                        {/* {!isAdmin && !displayCU && (
                            <Tooltip
                                placement="top"
                                arrow
                                title="Phiếu yêu cầu đã được xử lý toàn tất"
                            >
                                <CheckIcon fontSize="small" style={{ color: "green" }} />
                            </Tooltip>
                        )} */}

                    </div>
                );
            } ,
        } ,

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

    const [anchorEl , setAnchorEl] = useState();
    const [selectedRow , setSelectedRow] = useState(null);

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    // console.log("current list chosen: ", listChosen);

    return (
        <>
            <GlobitsTable
                data={listConfirmStaffWorkSchedule}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10 , 15 , 25 , 50 , 100]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
                selection
                handleSelectList={handleSelectListDelete}
                selectedRows={listChosen || []}
            />

            {Boolean(anchorEl) && (
                <Menu
                    id={"simple-menu-options"}
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClosePopover}
                    className="py-0"
                >

                    {selectedRow?.approvalStatus != LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color:"green"}}>
                                done_all
                            </Icon>

                            <span className="ml-4">
                                Phê duyệt
                            </span>
                        </MenuItem>
                    )}

                    {selectedRow?.approvalStatus != LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color:"red"}}>
                                close
                            </Icon>

                            <span className="ml-4">
                                Không duyệt
                            </span>
                        </MenuItem>
                    )}

                    {selectedRow?.approvalStatus != LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED_YET.value && (
                        <Tooltip
                            arrow
                            placement="bottom"
                            title="Đặt lại trạng thái là chưa duyệt"
                        >
                            <MenuItem className="flex items-center justify-center" onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmUpdateStatusPopup(LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED_YET.value);
                                handleClosePopover();
                            }}>
                                <Icon className="pr-6" fontSize="small" style={{color:"blue"}}>
                                    loop
                                </Icon>

                                <span className="ml-4">
                                    Đặt lại
                                </span>
                            </MenuItem>
                        </Tooltip>
                    )}
                </Menu>
            )}
        </>

    );
}

export default memo(observer(ConfirmStaffWorkScheduleList));
