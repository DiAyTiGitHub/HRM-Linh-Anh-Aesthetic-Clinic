import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';
import { getDate, getDateTime } from 'app/LocalFunction';

function LeaveRequestListUpdatePopup() {
    const { leaveRequestStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmUpdateStatusPopup,
        handleClose,
        handleRemoveActionItem,
        listChosen,
        onUpdateStatus,
        handleSelectListChosen,
        pagingLeaveRequest,
        handleConfirmUpdateStatus
    } = leaveRequestStore;

    const columns = [
        {
            title: "Nhân viên yêu cầu",
            field: "staff",
            align: "left",
            render: row => {
                return <span>{row?.requestStaff?.displayName}</span>
            }
        },
        {
            title: "Ngày yêu cầu",
            field: "requestDate",
            align: "left",
            render: row => {
                return <span>{getDate(row?.requestDate)}</span>
            }
        },
        {
            title: "Loại nghỉ",
            field: "leaveType",
            align: "left",
            render: (row) => {
                return <span>{row?.leaveType?.name}</span>;
            },
        },
        {
            title: "Nghỉ từ ngày",
            field: "fromDate",
            align: "left",
            render: row => {
                return <span>{getDateTime(row?.fromDate)}</span>
            }
        },
        {
            title: "Nghỉ đến ngày",
            field: "toDate",
            align: "left",
            render: row => {
                return <span>{getDateTime(row?.toDate)}</span>
            }
        },
        {
            title: "Tổng số ngày nghỉ",
            field: "totalDays",
            align: "left",
            render: row => {
                return <span>{row?.totalDays}</span>
            }
        },
        {
            title: "Trạng thái",
            field: "approvalStatus",
            align: "left",
            render: row => {
                return <span>{LocalConstants.LeaveRequestApprovalStatus.getListData().find(i => i.value == row?.approvalStatus)?.name}</span>
            }
        },
        {
            title: "Người xác nhận",
            field: "approvalStaff",
            align: "left",
            render: row => {
                return <span>{row?.approvalStaff?.displayName}</span>
            }
        },
        {
            title: t("general.action"),
            width: "6%",
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle w-100 justify-center">
                        <Tooltip title="Loại bỏ" placement="top">
                            <IconButton className="" size="small" onClick={() => handleRemoveActionItem(rowData?.id)}>
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
    ];


    function handleCloseConfirmPopup() {
        handleClose();
        pagingLeaveRequest();
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
                                    yêu cầu nghỉ được cập nhật thành {LocalConstants.LeaveRequestApprovalStatus.getListData().find(i => i.value == onUpdateStatus)?.name?.toUpperCase()}
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

export default memo(observer(LeaveRequestListUpdatePopup));