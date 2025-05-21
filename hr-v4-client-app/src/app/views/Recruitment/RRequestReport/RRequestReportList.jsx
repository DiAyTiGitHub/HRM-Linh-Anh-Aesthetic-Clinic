import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { NavLink } from "react-router-dom";
import ConstantList from "app/appConfig";

function RRequestReportList() {
    const {
        rRequestReportStore,
        hrRoleUtilsStore,
        recruitmentRequestStore
    } = useStore();

    const { t } = useTranslation();

    const {
        requestReportList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleSelectListDelete,
    } = rRequestReportStore;

    const {

        handleOpenCreateEdit,
        handleOpenView,
        handleDelete,

    } = recruitmentRequestStore;

    const {
        isAdmin,
        isManager
    } = hrRoleUtilsStore;

    let columns = [
        {
            title: t("general.action"),
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
            align: "center",
            render: (rowData) => {
                return (
                    <div className='flex flex-middle justify-center'>
                        <Tooltip
                            arrow
                            placement="top"
                            title={"Xem chi tiết"}
                        >
                            <IconButton
                                className="ml-4"
                                size="small"
                                onClick={() => {
                                    const payload = {
                                        id: rowData?.recruitmentRequestId
                                    };
                                    handleOpenView(payload);
                                }}
                            >
                                <Icon fontSize="small" style={{ color: "green" }}>
                                    remove_red_eye
                                </Icon>
                            </IconButton>

                        </Tooltip>
                        
                        {rowData?.editPermission && (
                            <Tooltip title='Cập nhật' className='ml-4' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={function () {
                                        const payload = {
                                            id: rowData?.recruitmentRequestId
                                        };
                                        handleOpenCreateEdit(payload);
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}

                        <Tooltip title='Xóa' placement='top'>
                            <IconButton className='ml-4' size='small' onClick={() => handleDelete(rowData)}>
                                <Icon fontSize='small' color='secondary'>
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        { title: "Đơn vị", field: "unit", minWidth: "120px" },
        { title: "Ban", field: "division", minWidth: "120px" },
        { title: "Phòng/Cơ sở", field: "department", minWidth: "120px" },
        { title: "Bộ phận", field: "subDepartment", minWidth: "120px" },
        { title: "Người đề xuất", field: "proposer", minWidth: "120px" },
        { title: "Chức danh tuyển dụng", field: "proposedPosition", minWidth: "120px" },
        { title: "Ngày nhận đề xuất", field: "proposalDate", minWidth: "120px" },
        { title: "Thời hạn tuyển dụng (ngày)", field: "recruitmentDurationDays", minWidth: "120px" },
        { title: "Ngày hết hạn tuyển dụng", field: "recruitmentDeadline", minWidth: "120px" },
        { title: "Tình trạng", field: "status", minWidth: "120px" },
        { title: "Tuyển trong định biên", field: "isWithinHeadcount", minWidth: "120px" },
        { title: "Tuyển ngoài định biên", field: "isOutOfHeadcount", minWidth: "120px" },
        { title: "Số lượng yêu cầu tuyển dụng", field: "requestedQuantity", minWidth: "120px" },
        { title: "Số lượng nhân sự nhận việc", field: "onboardedQuantity", minWidth: "120px" },
        { title: "Số lượng còn lại cần tuyển", field: "remainingQuantity", minWidth: "120px" },
        { title: "Số lượng chờ nhận việc", field: "pendingOnboardQuantity", minWidth: "120px" },
        { title: "Nguồn đăng tuyển", field: "recruitmentSource", minWidth: "120px" },
        // { title: "Ngày ứng viên onboard", field: "onboardDate", minWidth: "120px" },
        { title: "Số lượng từ chối offer", field: "offerDeclinedQuantity", minWidth: "120px" },
        { title: "Số lượng nhân sự nghỉ việc trong thời gian thử việc", field: "probationQuitQuantity", minWidth: "120px" },
        { title: "HR phụ trách", field: "hrInCharge", minWidth: "120px" },
        { title: "Ghi chú", field: "note", minWidth: "120px" },
    ];



    return (
        <GlobitsTable
            selection
            data={requestReportList}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(RRequestReportList));
