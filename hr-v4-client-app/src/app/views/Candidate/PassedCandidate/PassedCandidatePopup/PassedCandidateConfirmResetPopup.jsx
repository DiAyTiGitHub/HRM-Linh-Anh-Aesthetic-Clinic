import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';

function PassedCandidateConfirmResetPopup() {
    const { passedCandidateStore } = useStore();
    const { t } = useTranslation();
    const {
        openResetPopup,
        handleClose,
        handleConfirmResetCandidate,
        getReceptionStatusName,
        handleRemoveActionItem,
        listChosen,
        handleSelectListChosen,
        pagingPassedCandidates,
    } = passedCandidateStore;

    const columns = [
        {
            title: "Mã ứng viên",
            field: "candidateCode",
        },
        {
            title: "Họ tên",
            field: "displayName",
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            render: (rowData) => (
                <span>
                    {rowData?.birthDate && (formatDate("DD/MM/YYYY", rowData?.birthDate))}
                </span>
            ),
        },
        {
            title: "Đợt tuyển dụng",
            field: "recruitment",
            render: rowData => (
                <>
                    {rowData?.recruitment && (
                        <span className="pr-8">
                            {rowData?.recruitment?.name}
                        </span>
                    )}
                </>
            )
        },
        {
            title: "Vị trí ứng tuyển",
            field: "position",
            render: rowData => (
                <>
                    {rowData?.position && (
                        <span className="pr-8">
                            {rowData?.position?.name}
                        </span>
                    )}
                </>
            )
        },
        {
            title: "Trạng thái hiện tại",
            field: "receptionStatus",
            render: function (applicant) {
                return (<span>{getReceptionStatusName(applicant?.receptionStatus)}</span>);
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
                    </div >
                );
            },
        },
    ];

    function handleCloseConfirmPopup() {
        handleClose();
        pagingPassedCandidates();
    }

    return (
        <GlobitsColorfulThemePopup
            open={openResetPopup}
            handleClose={handleCloseConfirmPopup}
            size="lg"
            onConfirm={handleConfirmResetCandidate}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong>
                                    {listChosen?.length <= 1 ? "Thông tin " : "Danh sách "}
                                    ứng viên ĐẶT LẠI TRẠNG THÁI
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

export default memo(observer(PassedCandidateConfirmResetPopup));