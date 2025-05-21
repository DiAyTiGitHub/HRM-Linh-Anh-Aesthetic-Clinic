import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';

function RecruitmentRequestListUpdatePopup() {
    const { recruitmentRequestStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmUpdateStatusPopup,
        handleClose,
        handleRemoveActionItem,
        listChosen,
        onUpdateStatus,
        handleSelectListChosen,
        pagingRecruitmentRequest,
        handleConfirmUpdateStatus
    } = recruitmentRequestStore;

    const columns = [
        {
            title: "Mã",
            field: "code",
            align: "left",
        },
        {
            title: "Tên yêu cầu",
            field: "name",
            align: "left",
        },

        {
            title: "Phòng ban",
            field: "hrDepartment.name",
            align: "left",
        },

        {
            title: "Vị trí cần tuyển",
            field: "recruitmentRequestItems[0].positionTitle.name",
            align: "left",
        },
        {
            title: "Số lượng",
            field: "recruitmentRequestItems[0].announcementQuantity",
            align: "center",
        },
        {
            title: "Trạng thái hiện tại",
            field: "status",
            align: "left",
            render: row => {
                return <span>{LocalConstants.RecruitmentRequestStatus.getListData().find(i => i.value == row?.status)?.name}</span>
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
        pagingRecruitmentRequest();
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
                                    yêu cầu tuyển dụng được cập nhật thành {LocalConstants.RecruitmentRequestStatus.getListData().find(i => i.value == onUpdateStatus)?.name?.toUpperCase()}
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

export default memo(observer(RecruitmentRequestListUpdatePopup));