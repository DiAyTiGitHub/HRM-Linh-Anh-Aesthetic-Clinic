import { Icon, IconButton, Tooltip } from "@material-ui/core";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import { getDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom/cjs/react-router-dom";

function ShiftChangeRequestList() {
    const history = useHistory();
    const { shiftChangeRequestStore } = useStore();
    const { t } = useTranslation();

    const {
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        listShiftChangeRequests,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenConfirmUpdateStatusPopup,
        listChosen,
        handleOpenView
    } = shiftChangeRequestStore;

    const { hrRoleUtilsStore } = useStore();
    const { isManager, isAdmin, checkAllUserRoles } = hrRoleUtilsStore;
    useEffect(() => {
        checkAllUserRoles()
    }, []);
    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            minWidth: "32px",
            align: "center",
            render: (rowData) => (
                <div className='flex flex-middle justify-center'>
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Chi tiết yêu cầu thay đổi ca làm việc"}
                    >
                        <IconButton
                            size="small"
                            onClick={() => handleOpenView(rowData)}
                        >
                            <Icon fontSize="small" style={{ color: "green" }}>
                                remove_red_eye
                            </Icon>
                        </IconButton>
                    </Tooltip>

                    {(isManager || isAdmin) && rowData?.approvalStatus === LocalConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED_YET.value && (
                        <>
                            <Tooltip title='Cập nhật' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={() => handleOpenCreateEdit(rowData)}
                                >
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>

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

                            <Tooltip title='Thao tác khác' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={(event) => {
                                        setSelectedRow(rowData);
                                        setAnchorEl(event?.currentTarget);
                                    }}
                                >
                                    <MoreHorizIcon />
                                </IconButton>
                            </Tooltip>
                        </>
                    )}
                </div>
            )
        },
        {
            title: "Nhân viên yêu cầu",
            field: "staff",
            align: "left",
            minWidth: "150px",
            render: (row) => (
                <span>
                    {row?.registerStaff?.displayName}
                    {row?.registerStaff?.staffCode && (
                        <>
                            <br />
                            {row.registerStaff.staffCode}
                        </>
                    )}
                </span>
            ),
        },
        {
            title: "Ngày yêu cầu",
            field: "requestDate",
            align: "left",
            minWidth: "150px",
            render: (row) => {
                return <span>{getDate(row?.requestDate)}</span>;
            },
        },
        {
            title: "Ngày làm việc cần thay đổi",
            field: "fromWorkingDate",
            align: "left",
            minWidth: "200px",
            render: (row) => {
                return <span>{getDate(row?.fromWorkingDate)}</span>;
            },
        },
        {
            title: "Ca làm việc cần thay đổi",
            field: "fromShiftWork",
            align: "left",
            minWidth: "200px",
            render: (row) => {
                return <span>{row?.fromShiftWork?.name}</span>;
            },
        },
        {
            title: "Ngày làm việc yêu cầu đổi",
            field: "toWorkingDate",
            align: "left",
            minWidth: "200px",
            render: (row) => {
                return <span>{getDate(row?.toWorkingDate)}</span>;
            },
        },
        {
            title: "Ca làm việc yêu cầu đổi",
            field: "toShiftWork",
            align: "left",
            minWidth: "200px",
            render: (row) => {
                return <span>{row?.toShiftWork?.name}</span>;
            },
        },
        {
            title: "Trạng thái",
            field: "approvalStatus",
            align: "left",
            minWidth: "150px",
            render: (row) => {
                return (
                    <span>
                        {
                            LocalConstants.ShiftChangeRequestApprovalStatus.getListData().find(
                                (i) => i.value == row?.approvalStatus
                            )?.name
                        }
                    </span>
                );
            },
        },
        {
            title: "Người xác nhận",
            field: "approvalStaff",
            align: "left",
            minWidth: "150px",
            render: (row) => {
                return <span>{row?.approvalStaff?.displayName}</span>;
            },
        },
    ];

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    // console.log("current list chosen: ", listChosen);

    return (
        <>
            <GlobitsTable
                data={listShiftChangeRequests}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 15, 25, 50, 100]}
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
                    className='py-0'>
                    {selectedRow?.approvalStatus ===
                        LocalConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED_YET.value ? (
                        <>
                            <MenuItem
                                className='flex items-center justify-center'
                                onClick={function () {
                                    handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                    handleOpenConfirmUpdateStatusPopup(
                                        LocalConstants.ShiftChangeRequestApprovalStatus.APPROVED.value
                                    );
                                    handleClosePopover();
                                }}>
                                <Icon fontSize='small' style={{ color: "green" }}>
                                    done_all
                                </Icon>
                                <span className="ml-6">
                                    Phê duyệt
                                </span>
                            </MenuItem>

                            <MenuItem
                                className='flex items-center justify-center'
                                onClick={function () {
                                    handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                    handleOpenConfirmUpdateStatusPopup(
                                        LocalConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED.value
                                    );
                                    handleClosePopover();
                                }}>
                                <Icon fontSize='small' style={{ color: "red" }}>
                                    thumb_down
                                </Icon>
                                <span className="ml-6">
                                    Không duyệt
                                </span>
                            </MenuItem>
                        </>
                    ) : null}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(ShiftChangeRequestList));
