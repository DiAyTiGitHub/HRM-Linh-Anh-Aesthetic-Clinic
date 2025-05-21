import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import moment from "moment";
import { formatDate } from "app/LocalFunction";

function StaffAnnualLeaveHistoryList() {
    const {
        staffAnnualLeaveHistoryStore,
        hrRoleUtilsStore
    } = useStore();

    const { t } = useTranslation();

    const {
        staffAnnualLeaveHistoryList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleOpenCreateEdit,
        handleSelectListDelete,
        handleOpenView
    } = staffAnnualLeaveHistoryStore;

    const {
        isAdmin,
        isManager
    } = hrRoleUtilsStore;

    const roundToTwo = (num) => {
        if (!num) return "0.00"; // Handle null, undefined, or 0 cases explicitly
        return (Math.round(num * 100) / 100).toFixed(2);
    };

    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            minWidth: "100px",
            render: (rowData) => (
                <div className="flex flex-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Chi tiết"}
                    >
                        <IconButton
                            className="ml-4"
                            size="small"
                            onClick={() => handleOpenView(rowData)}
                        >
                            <Icon fontSize="small" style={{ color: "green" }}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>

                    {(isManager || isAdmin) && (
                        <Tooltip
                            arrow
                            title={"Cập nhật thông tin"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData)}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}

                    {(isManager || isAdmin) && (
                        <Tooltip
                            arrow
                            title={"Xóa thông tin"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleDelete(rowData)}>
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
            ),
        },

        {
            title: "Mã nhân viên",
            field: "staffCode",
            align: "center",
            minWidth: "150px",
            render: (rowData) => <span className='px-6'>{rowData?.staff?.staffCode}</span>,
        },
        {
            title: "Nhân viên",
            // align: "center",
            minWidth: "150px",
            render: (rowData) => (
                <div className="pr-6">
                    {rowData?.staff?.displayName && (
                        <p className='m-0 no-wrap-text'>
                            <strong>{rowData?.staff?.displayName}</strong>
                        </p>
                    )}

                    {rowData?.staff?.birthDate && (
                        <p className='m-0 no-wrap-text'>Ngày
                            sinh: {formatDate("DD/MM/YYYY", rowData?.staff?.birthDate)}</p>
                    )}

                    {rowData?.staff?.gender && (
                        <p className='m-0 no-wrap-text'>
                            Giới
                            tính: {rowData?.staff?.gender === "M" ? "Nam" : rowData?.staff?.gender === "F" ? "Nữ" : ""}
                        </p>
                    )}

                    {rowData?.staff?.birthPlace &&
                        <p className='m-0 no-wrap-text'>Nơi sinh: {rowData?.staff?.birthPlace}</p>}
                </div>
            ),
        },

        {
            title: "Đơn vị",
            field: "staff.organization.name",
            width: "10%",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Phòng ban",
            field: "department.name",
            width: "10%",
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
            width: "10%",
            minWidth: "150px",
            align: "left",
        },

        {
            title: "Năm thống kê",
            field: "year",
            width: "10%",
            minWidth: "150px",
            align: "center",
        },


        {
            title: "Nghỉ phép được cấp",
            field: "grantedLeaveDays",
            minWidth: "150px",
            align: "center",
            render: (rowData) =>
                typeof rowData.grantedLeaveDays === "number"
                    ? roundToTwo(rowData.grantedLeaveDays)
                    : "",
        },
        {
            title: "Nghỉ phép chuyển từ năm trước",
            field: "carriedOverLeaveDays",
            minWidth: "150px",
            align: "center",
            render: (rowData) =>
                typeof rowData.carriedOverLeaveDays === "number"
                    ? roundToTwo(rowData.carriedOverLeaveDays)
                    : "",
        },
        {
            title: "Nghỉ phép theo thâm niên",
            field: "seniorityLeaveDays",
            minWidth: "150px",
            align: "center",
            render: (rowData) =>
                typeof rowData.seniorityLeaveDays === "number"
                    ? roundToTwo(rowData.seniorityLeaveDays)
                    : "",
        },
        {
            title: "Nghỉ phép thưởng",
            field: "bonusLeaveDays",
            minWidth: "150px",
            align: "center",
            render: (rowData) =>
                typeof rowData.bonusLeaveDays === "number"
                    ? roundToTwo(rowData.bonusLeaveDays)
                    : "",
        },
        {
            title: "Nghỉ phép bị hủy",
            field: "cancelledLeaveDays",
            minWidth: "150px",
            align: "center",
            render: (rowData) =>
                typeof rowData.cancelledLeaveDays === "number"
                    ? roundToTwo(rowData.cancelledLeaveDays)
                    : "",
        },

        {
            title: "Số này nghỉ đã sử dụng",
            field: "totalUsedLeaveDays",
            minWidth: "150px",
            align: "center",
            render: (rowData) =>
                typeof rowData.totalUsedLeaveDays === "number"
                    ? roundToTwo(rowData.totalUsedLeaveDays)
                    : "",
        },

        
    ];

    return (
        <GlobitsTable
            selection
            data={staffAnnualLeaveHistoryList}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(StaffAnnualLeaveHistoryList));
