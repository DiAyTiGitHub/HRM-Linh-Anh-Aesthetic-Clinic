import { observer } from "mobx-react";
import React, { memo } from "react";
import { Icon, IconButton, Grid, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import { getDate, getDateTime } from "app/LocalFunction";

function ShiftChangeRequestListUpdatePopup() {
    const { shiftChangeRequestStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmUpdateStatusPopup,
        handleClose,
        handleRemoveActionItem,
        listChosen,
        onUpdateStatus,
        handleSelectListChosen,
        pagingShiftChangeRequest,
        handleConfirmUpdateStatus,
    } = shiftChangeRequestStore;

    const columns = [
        {
            title: "Nhân viên yêu cầu",
            field: "staff",
            align: "left",
            cellStyle: {
                textAlign: "left",
                padding: "8px"
            },
            render: (row) => (
                <span>
                    {`${row?.registerStaff?.displayName} - ${row.registerStaff.staffCode}`}
                </span>
            ),
        },
        {
            title: "Ngày yêu cầu",
            field: "requestDate",
            align: "center",
            cellStyle: {
                textAlign: "center"
            },
            render: (row) => {
                return <span>{getDate(row?.requestDate)}</span>;
            },
        },
        {
            title: "Ngày làm việc cần thay đổi",
            field: "fromWorkingDate",
            align: "center",
            cellStyle: {
                textAlign: "center"
            },
            render: (row) => {
                return <span>{getDate(row?.fromWorkingDate)}</span>;
            },
        },
        {
            title: "Ca làm việc cần thay đổi",
            field: "fromShiftWork",
            align: "left",
            cellStyle: {
                textAlign: "left"
            },
            render: (row) => {
                return <span>{row?.fromShiftWork?.name}</span>;
            },
        },
        {
            title: "Ngày làm việc yêu cầu đổi",
            field: "toWorkingDate",
            align: "center",
            cellStyle: {
                textAlign: "center"
            },
            render: (row) => {
                return <span>{getDate(row?.toWorkingDate)}</span>;
            },
        },
        {
            title: "Ca làm việc yêu cầu đổi",
            field: "toShiftWork",
            align: "left",
            cellStyle: {
                textAlign: "left"
            },
            render: (row) => {
                return <span>{row?.toShiftWork?.name}</span>;
            },
        },
        {
            title: "Trạng thái",
            field: "approvalStatus",
            align: "left",
            cellStyle: {
                textAlign: "left"
            },
            render: (row) => {
                return (
                    <span>
                        {LocalConstants.ShiftChangeRequestApprovalStatus.getListData().find(
                            (i) => i.value == row?.approvalStatus
                        )?.name}
                    </span>
                );
            },
        },
        {
            title: "Người xác nhận",
            field: "approvalStaff",
            align: "left",
            cellStyle: {
                textAlign: "left"
            },
            render: (row) => {
                return <span>{row?.approvalStaff?.displayName}</span>;
            },
        },
        {
            title: t("general.action"),
            width: "6%",
            align: "center",
            cellStyle: {
                textAlign: "center"
            },
            render: (rowData) => {
                return (
                    <div className='flex flex-middle w-100 justify-center'>
                        <Tooltip title='Loại bỏ' placement='top'>
                            <IconButton size='small' onClick={() => handleRemoveActionItem(rowData?.id)}>
                                <Icon fontSize='small' color='secondary'>
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
        pagingShiftChangeRequest();
    }

    return (
        <GlobitsColorfulThemePopup
            open={openConfirmUpdateStatusPopup}
            handleClose={handleCloseConfirmPopup}
            size='lg'
            onConfirm={handleConfirmUpdateStatus}>
            <div className='dialog-body'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className='dialogScrollContent'>
                            <h6 className='text-red'>
                                <strong>
                                    {listChosen?.length <= 1 ? "Thông tin " : "Danh sách "}
                                    yêu cầu nghỉ được cập nhật thành{" "}
                                    {LocalConstants.ShiftChangeRequestApprovalStatus.getListData()
                                        .find((i) => i.value == onUpdateStatus)
                                        ?.name?.toUpperCase()}
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

export default memo(observer(ShiftChangeRequestListUpdatePopup));
