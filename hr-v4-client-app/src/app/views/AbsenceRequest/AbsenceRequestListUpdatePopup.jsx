import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';
import { getDate } from 'app/LocalFunction';

function AbsenceRequestListUpdatePopup() {
    const { absenceRequestStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmUpdateStatusPopup,
        handleClose,
        handleRemoveActionItem,
        listChosen,
        onUpdateStatus,
        handleSelectListChosen,
        pagingAbsenceRequest,
        handleConfirmUpdateStatus
    } = absenceRequestStore;

    const columns = [
        {
            title: "Nhân viên yêu cầu",
            field: "staff",
            align: "left",
            render: row => {
                return <span>{row?.workSchedule?.staff?.displayName}</span>
            }
        },
        {
            title: "Ngày làm việc",
            field: "workSchedule",
            align: "left",
            render: row => {
                return <span>{getDate(row?.workSchedule?.workingDate)}</span>
            }
        },
        {
            title: "Ngày yêu cầu nghỉ",
            field: "requestDate",
            align: "left",
            render: row => {
                return <span>{getDate(row?.requestDate)}</span>
            }
        },
        // {
        //     title: "Lý do nghỉ",
        //     field: "absenceReason",
        //     align: "left"
        // },
        {
            title: "Trạng thái",
            field: "approvalStatus",
            align: "left",
            render: row => {
                return <span>{LocalConstants.AbsenceRequestApprovalStatus.getListData().find(i => i.value == row?.approvalStatus)?.name}</span>
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
        pagingAbsenceRequest();
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
                                    yêu cầu nghỉ được cập nhật thành {LocalConstants.AbsenceRequestApprovalStatus.getListData().find(i => i.value == onUpdateStatus)?.name?.toUpperCase()}
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

export default memo(observer(AbsenceRequestListUpdatePopup));