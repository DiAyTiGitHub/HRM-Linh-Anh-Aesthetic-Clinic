import { Icon, IconButton, Menu, MenuItem, Tooltip } from "@material-ui/core";
import { CloseOutlined, PauseCircleOutline } from "@material-ui/icons";
import AvTimerIcon from "@material-ui/icons/AvTimer";
import CheckIcon from "@material-ui/icons/Check";
import InsertInvitationIcon from "@material-ui/icons/InsertInvitation";
import ConstantList from "app/appConfig";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import { formatDate, getDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { NavLink } from "react-router-dom/cjs/react-router-dom";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";


function StaffWorkScheduleV2List(props) {
    const { isOnChoosingMode, selectedItem, handleChooseItem } = props;

    const { t } = useTranslation();

    const { staffWorkScheduleStore, hrRoleUtilsStore } = useStore();

    const {
        listStaffWorkSchedules,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenView,
        handleOpenViewStatistic,
        getStaffWorkScheduleWorkingStatusName
    } = staffWorkScheduleStore;

    const { isAdmin, isManager } = hrRoleUtilsStore;


    const [anchorEl, setAnchorEl] = useState();

    function handleCloseAnchor() {
        setAnchorEl(null);
        setTimeout(() => {
            setSelectedRowData(null);
        }, 100);
    }

    const [selectedRowData, setSelectedRowData] = useState(null);

    const getApprovalStatusName = (status) => {
        switch (status) {
            case 1:
                return "Chưa duyệt";
            case 2:
                return "Đã duyệt";
            case 3:
                return "Không duyệt";
            default:
                return "Không xác định";
        }
    };
    const columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            align: "center",
            render: (rowData) => {
                const isLocked = rowData?.isLocked
                return (
                    <div className='flex align-center justify-center'>
                        <Tooltip arrow placement='top' title={"Chi tiết lần phân ca"}>
                            <IconButton className='ml-4' size='small' onClick={() => handleOpenView(rowData?.id)}>
                                <Icon fontSize='small' style={{ color: "green" }}>
                                    remove_red_eye
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        {(isAdmin || isManager) && !isLocked && (
                            <Tooltip arrow placement='top' title={"Chỉnh sửa phân ca làm việc"}>
                                <IconButton className='ml-4' size='small' onClick={() => handleOpenCreateEdit(rowData?.id)}>
                                    <Icon fontSize='small' style={{ color: "#3f51b5" }}>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}

                        <IconButton
                            size='small'
                            onClick={(event) => {
                                setAnchorEl(event.currentTarget);
                                setSelectedRowData(rowData);
                            }}>
                            <Icon style={{ fontSize: "20px" }} className='m-0 '>
                                more_vert
                            </Icon>
                        </IconButton>

                        <Menu
                            id='simple-menu'
                            anchorEl={anchorEl}
                            keepMounted
                            open={Boolean(anchorEl)}
                            onClose={handleCloseAnchor}
                        >
                            <Tooltip arrow placement='right' title={"Lịch làm việc đã được phân của nhân viên"}>
                                <MenuItem
                                    component={NavLink}
                                    to={
                                        ConstantList.ROOT_PATH +
                                        "staff-month-schedule-calendar/" +
                                        selectedRowData?.staff?.id
                                    }>
                                    <InsertInvitationIcon style={{ color: "#13529f" }} />

                                    <span className='ml-6'>Lịch làm việc</span>
                                </MenuItem>
                            </Tooltip>

                            <Tooltip arrow placement='right' title={"Thống kê kết quả làm việc"}>
                                <MenuItem
                                    fullWidth
                                    onClick={function () {
                                        handleCloseAnchor();
                                        handleOpenViewStatistic(selectedRowData?.id);
                                    }}>
                                    <AvTimerIcon style={{ color: "orange" }} />

                                    <span className='ml-6'>Thống kê </span>
                                </MenuItem>
                            </Tooltip>

                            {(isAdmin || isManager) && !selectedRowData?.isLocked && (
                                <Tooltip arrow placement='top' title={"Xóa thông tin lần phân ca"}>
                                    <MenuItem
                                        fullWidth
                                        onClick={function () {
                                            handleCloseAnchor();
                                            handleDelete(selectedRowData);
                                        }}>
                                        <Icon fontSize='small' color='secondary'>
                                            delete
                                        </Icon>

                                        <span className='ml-6'>Xóa</span>
                                    </MenuItem>
                                </Tooltip>
                            )}
                        </Menu>
                    </div>
                )
            },
        },
        {
            title: "Nhân viên",
            field: "staff",
            align: "left",
            render: (row) => (
                <span>
                    {row?.staff?.displayName}
                    {row?.staff?.staffCode && (
                        <>
                            <br />({row.staff.staffCode})
                        </>
                    )}
                </span>
            ),
            minWidth: "120px",
        },
        // {
        //     title:"Đơn vị" ,
        //     field:"staff.organization.name" ,
        //     align:"left" ,
        // } ,
        {
            title: "Phòng ban",
            field: "department.name",
            align: "left",
            minWidth: "150px",
            render: (rowData) => (
                <>
                    {rowData?.staff?.department?.name && <p className='m-0'>{rowData?.staff?.department?.name}</p>}
                    {rowData?.staff?.department?.code && <p className='m-0'>({rowData?.staff?.department?.code})</p>}
                </>
            ),
        },
        {
            title: "Chức danh",
            field: "staff.positionTitle.name",
            align: "left",
            minWidth: "150px",
        },

        {
            title: "Ngày làm việc",
            field: "workingDate",
            align: "left",
            minWidth: "150px",
            render: (row) => <span className='px-2'>{getDate(row?.workingDate)}</span>,
        },

        {
            title: "Ca làm việc",
            align: "left",
            field: "shiftWork.name",
            // render: row => <span className="px-2">{`${row?.shiftWork?.name} `}</span>
            render: (rowData) => {
                const { checkInTime, checkOutTime } = rowData;
                return (
                    <div className='px-4'>
                        {rowData?.shiftWork?.name && (
                            <p className='m-0 pb-4'>
                                <span>
                                    <strong>{`${rowData.shiftWork.name} - ${rowData.shiftWork.code}`} </strong>
                                </span>
                            </p>
                        )}
                        {checkInTime && checkOutTime && (
                            <p className='m-0 no-wrap-text'>
                                {`${formatDate("HH:mm", checkInTime)} - ${formatDate("HH:mm", checkOutTime)}`}
                            </p>
                        )}
                    </div>
                );
            },
            minWidth: "200px",
        },
        {
            title: "Trạng thái làm việc",
            field: "workingStatus",
            minWidth: "150px",
            render: (row) => {
                return <span>{getStaffWorkScheduleWorkingStatusName(row)}</span>;
            },
        },
        {
            title: "Cần phê duyệt",
            field: "needManagerApproval",
            minWidth: "150px",
            align: "center",
            render: (data) =>
                data?.needManagerApproval ? <CheckIcon fontSize='small' style={{ color: "green" }} /> : "",
        },
        {
            title: "Trạng thái duyệt",
            field: "approvalStatus",
            minWidth: "150px",
            align: "center",
            render: (data) => {
                switch (data?.approvalStatus) {
                    case LocalConstants.StaffWorkScheduleApprovalStatus.NOT_APPROVED_YET.value:
                        return <PauseCircleOutline fontSize='small' style={{ color: "orange" }} />;
                    case LocalConstants.StaffWorkScheduleApprovalStatus.APPROVED.value:
                        return <CheckIcon fontSize='small' style={{ color: "green" }} />;
                    case LocalConstants.StaffWorkScheduleApprovalStatus.NOT_APPROVED.value:
                        return <CloseOutlined fontSize='small' style={{ color: "red" }} />;
                    default:
                        return "";
                }
            },
        },
    ]

    return (
        <GlobitsTable
            selection
            data={listStaffWorkSchedules}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(StaffWorkScheduleV2List));
