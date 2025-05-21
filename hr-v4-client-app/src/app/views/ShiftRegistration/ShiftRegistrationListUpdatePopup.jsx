import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';

function ShiftRegistrationListUpdatePopup() {
    const { ShiftRegistrationStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmUpdateStatusPopup,
        handleClose,
        handleRemoveActionItem,
        listChosen,
        onUpdateStatus,
        handleSelectListChosen,
        pagingShiftRegistration,
        handleConfirmUpdateStatus
    } = ShiftRegistrationStore;

    const columns = [
        {
            title: "Nhân viên đăng ký",
            width: "10%",
            field: "registerStaff.displayName",
            align: "left",
        },
        {
            title: "Ca làm việc",
            width: "20%",
            field: "shiftWork.name",
            align: "left",
        },
        {
            title: "Ngày làm việc",
            width: "20%",
            field: "workingDate",
            render: (rowData) => (
                <span>
                    {rowData?.workingDate && (formatDate("DD/MM/YYYY", rowData?.workingDate))}
                </span>
            ),
            align: "left",
        },
        {
            title: "Người phê duyệt",
            width: "10%",
            field: "approvalStaff.displayName",
            align: "left",
        },
        {
            title: "Trạng thái hiện tại",
            width: "10%",
            field: "approvalStatus",
            align: "left",
            render: row => <span>{LocalConstants.ShiftRegistrationApprovalStatus.getListData().find(i => i.value == row?.approvalStatus)?.name}</span>
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
        pagingShiftRegistration();
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
                                    ca làm của nhân viên được cập nhật thành {LocalConstants.ShiftRegistrationApprovalStatus.getListData().find(i => i.value == onUpdateStatus)?.name?.toUpperCase()}
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

export default memo(observer(ShiftRegistrationListUpdatePopup));