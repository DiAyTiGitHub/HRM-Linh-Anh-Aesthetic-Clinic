import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';

function ExportCandidateConfirmExportExcelPopup() {
    const { exportCandidateStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmExportExcel,
        handleConfirmExportExcel,
        handleClose,
        getOnboardStatusName,
        handleRemoveActionItem,
        listChosen,
        handleSelectListChosen,
        pagingExportCandidate,
    } = exportCandidateStore;

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
            title: "SĐT",
            field: "phoneNumber",
            render: rowData => (
                <>
                    {rowData?.phoneNumber && (
                        <span className="pr-8">
                            {rowData?.phoneNumber}
                        </span>
                    )}
                </>
            )
        },
        {
            title: "Ngày nộp hồ sơ",
            field: "submissionDate",
            render: (rowData) => (
                <span>
                    {rowData?.submissionDate && (formatDate("DD/MM/YYYY", rowData?.submissionDate))}
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
            title: "Ngày thi tuyển",
            field: "interviewDate",
            render: (rowData) => (
                <span>
                    {rowData?.interviewDate && (formatDate("DD/MM/YYYY", rowData?.interviewDate))}
                </span>
            ),
        },
        {
            title: "Trạng thái nhận việc",
            field: "onboardStatus",
            align: "center",
            render: function (candidate) {
                return (<span className="w-100 text-center">{getOnboardStatusName(candidate?.onboardStatus)}</span>);
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
        pagingExportCandidate();
    }

    let popupSize = "lg";
    if (!listChosen?.length || listChosen.length == 0) popupSize = "sm";

    return (
        <GlobitsColorfulThemePopup
            open={openConfirmExportExcel}
            handleClose={handleCloseConfirmPopup}
            size={popupSize}
            onConfirm={handleConfirmExportExcel}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        {/* Khi đã có ứng viên được chọn => Chỉ xuất danh sách các ứng viên đó */}
                        {listChosen?.length > 0 && (
                            <div className="dialogScrollContent">
                                <h6 className="text-red">
                                    <strong>
                                        {listChosen?.length <= 1 ? "Thông tin " : "Danh sách "}
                                        ứng viên được chọn kết xuất dữ liệu
                                    </strong>
                                </h6>

                                <GlobitsTable
                                    data={listChosen}
                                    handleSelectList={handleSelectListChosen}
                                    columns={columns}
                                    nonePagination
                                />
                            </div>
                        )}

                        {/* Nếu không có ứng viên nào được chọn => Xuất tất cả ứng viên theo bộ lọc */}
                        {(!listChosen?.length || listChosen.length == 0) && (
                            <ExportCandidateConfirmWarningContent />
                        )}

                    </Grid>
                </Grid>
            </div>
        </GlobitsColorfulThemePopup>
    );
}

export default memo(observer(ExportCandidateConfirmExportExcelPopup));

function ExportCandidateConfirmWarningContent() {
    return (
        <div className="dialogScrollContent">
            <h6 className="text-red">
                <strong>
                    {`Lưu ý: `}
                </strong>
                Bạn đang thực hiện hành động xuất danh sách ứng viên theo bộ lọc, hành động này sẽ lấy dữ liệu của tất cả ứng viên theo bộ lọc và
                <strong>
                    {` có thể cần đến vài phút`}
                </strong>
                <br />
                <strong className='flex pt-6'>BẠN CÓ CHẮC MUỐN THỰC HIỆN HÀNH ĐỘNG NÀY?</strong>
            </h6>
        </div>
    );
}