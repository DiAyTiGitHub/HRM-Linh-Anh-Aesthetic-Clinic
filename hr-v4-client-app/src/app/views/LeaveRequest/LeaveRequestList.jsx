import React , { memo , useState } from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { IconButton , Icon , Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { getDate , getDateTime } from "app/LocalFunction";
import { useHistory } from "react-router-dom/cjs/react-router-dom";
import LocalConstants from "app/LocalConstants";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import { max } from "lodash";

function LeaveRequestList() {
    const history = useHistory();
    const {leaveRequestStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        isAdmin ,
        isManager
    } = hrRoleUtilsStore;

    const {
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        listLeaveRequests ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleOpenConfirmUpdateStatusPopup ,
        listChosen ,
        handleOpenView ,
        handleExportRequestLeave
    } = leaveRequestStore;

    const columns = [
        {
            title:t("general.action") ,
            minWidth:"180px" ,
            align:"center" ,
            render:(rowData) => {
                return (
                    <div className='flex flex-middle justify-center'>
                        <Tooltip
                            arrow
                            placement="top"
                            title={"Xem chi tiết"}
                        >
                            <IconButton
                                size="small"
                                onClick={() => handleOpenView(rowData)}
                            >
                                <Icon fontSize="small" style={{color:"green"}}>
                                    remove_red_eye
                                </Icon>
                            </IconButton>

                        </Tooltip>
                        {(isAdmin || isManager) && (
                            <Tooltip title='Cập nhật' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={function () {
                                        handleOpenCreateEdit(rowData);
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}
                        {(isAdmin || isManager) && (

                            <Tooltip title='Xóa' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={() => handleDelete(rowData)}
                                >
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}
                        <Tooltip title="Tải xuống yêu cầu nghỉ phép" arrow>
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => handleExportRequestLeave(rowData)}
                            >
                                <Icon fontSize="small" color="blue">
                                    description
                                </Icon>
                            </IconButton>
                        </Tooltip>
                        {(isAdmin || isManager) && (
                            <Tooltip title='Thao tác khác' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={(event) => {
                                        setSelectedRow(rowData);
                                        setAnchorEl(event?.currentTarget);
                                    }}>
                                    <MoreHorizIcon/>
                                </IconButton>
                            </Tooltip>
                        )}
                    </div>
                );
            } ,
        } ,
        {
            title:"Nhân viên yêu cầu" ,
            field:"staff" ,
            align:"left" ,
            minWidth:"150px" ,
            render:(row) => (
                <span>
                    {row?.requestStaff?.displayName}
                    {row?.requestStaff?.staffCode && (
                        <>
                            <br/>
                            {row.requestStaff.staffCode}
                        </>
                    )}
                </span>
            ) ,
        } ,

        {
            title:"Ngày yêu cầu" ,
            field:"requestDate" ,
            align:"left" ,
            minWidth:"150px" ,
            render:(row) => {
                return <span>{getDate(row?.requestDate)}</span>;
            } ,
        } ,
        {
            title:"Loại nghỉ" ,
            field:"leaveType" ,
            align:"left" ,
            minWidth:"150px" ,
            render:(row) => {
                return <span>{row?.leaveType?.name}</span>;
            } ,
        } ,
        {
            title:"Thời điểm bắt đầu nghỉ" ,
            field:"fromDate" ,
            align:"left" ,
            minWidth:"150px" ,
            render:(row) => {
                return <span>{getDate(row?.fromDate)}</span>;
            } ,
        } ,
        {
            title:"Thời điểm kết thúc" ,
            field:"toDate" ,
            align:"left" ,
            render:(row) => {
                return <span>{getDate(row?.toDate)}</span>;
            } ,
            minWidth:"180px" ,
        } ,
        {
            title:"Tổng số ngày nghỉ" ,
            field:"totalDays" ,
            align:"left" ,
            render:(row) => {
                return <span>{row?.totalDays}</span>;
            } ,
            minWidth:"150px" ,
        } ,
        {
            title:"Trạng thái" ,
            field:"approvalStatus" ,
            align:"left" ,
            render:(row) => {
                return (
                    <span>
                        {
                            LocalConstants.LeaveRequestApprovalStatus.getListData().find(
                                (i) => i.value == row?.approvalStatus
                            )?.name
                        }
                    </span>
                );
            } ,
            minWidth:"150px" ,
        } ,
        {
            title:"Người xác nhận" ,
            field:"approvalStaff" ,
            align:"left" ,
            render:(row) => {
                return <span>{row?.approvalStaff?.displayName}</span>;
            } ,
            minWidth:"150px" ,
        } ,
        {
            title:"Đơn vị" ,
            field:"requestStaff.organization.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Phòng ban" ,
            field:"department.name" ,
            align:"left" ,
            render:(rowData) => (
                <>
                    {rowData?.requestStaff?.department?.name &&
                        <p className='m-0'>{rowData?.requestStaff?.department?.name}</p>}
                    {rowData?.requestStaff?.department?.code &&
                        <p className='m-0'>({rowData?.requestStaff?.department?.code})</p>}
                </>
            ) ,
            minWidth:"150px" ,
        } ,
        {
            title:"Chức danh" ,
            field:"requestStaff.positionTitle.name" ,
            align:"left" ,
            minWidth:"150px" ,
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
                selection
                columns={columns}
                data={listLeaveRequests}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10 , 15 , 25 , 50 , 100]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
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
                    className='py-0'>
                    {selectedRow?.approvalStatus != LocalConstants.LeaveRequestApprovalStatus.APPROVED.value && (
                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmUpdateStatusPopup(
                                    LocalConstants.LeaveRequestApprovalStatus.APPROVED.value
                                );
                                handleClosePopover();
                            }}>
                            <Icon className='pr-6' fontSize='small' style={{color:"green"}}>
                                done_all
                            </Icon>
                            Phê duyệt
                        </MenuItem>
                    )}

                    {selectedRow?.approvalStatus != LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED.value && (
                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmUpdateStatusPopup(
                                    LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED.value
                                );
                                handleClosePopover();
                            }}>
                            <Icon className='pr-6' fontSize='small' style={{color:"red"}}>
                                thumb_down
                            </Icon>
                            Không duyệt
                        </MenuItem>
                    )}

                    {selectedRow?.approvalStatus !=
                        LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED_YET.value && (
                            <MenuItem
                                className='flex items-center justify-center'
                                onClick={function () {
                                    handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                    handleOpenConfirmUpdateStatusPopup(
                                        LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED_YET.value
                                    );
                                    handleClosePopover();
                                }}>
                                <Icon className='pr-6' fontSize='small' style={{color:"blue"}}>
                                    loop
                                </Icon>
                                Chưa duyệt
                            </MenuItem>
                        )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(LeaveRequestList));
