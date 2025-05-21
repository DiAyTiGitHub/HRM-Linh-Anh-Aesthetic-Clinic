import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import LocalConstants from "app/LocalConstants";

function OrphanedPayslipList() {
    const {
        salaryResultDetailStore,
        salaryStaffPayslipStore,
        hrRoleUtilsStore,
        payrollStore
    } = useStore();
    const { t } = useTranslation();

    const {
        orphanedPayslipsList
    } = payrollStore;

    const {
        getApprovalStatusName,
        handleOpenCreateEdit,
        handleSelectListDelete,
        handleDelete,
        handleOpenRecalculatePayslip,
    } = salaryStaffPayslipStore;

    const {
        isAdmin,
        isManager

    } = hrRoleUtilsStore;

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => {
                let isLock = rowData?.approvalStatus === LocalConstants.SalaryStaffPayslipApprovalStatus?.LOCKED.value

                return (
                    <div className='flex flex-middle justify-center'>
                        <Tooltip title='Xem phiếu lương nhân viên' placement='top'>
                            <IconButton
                                className='bg-white'
                                size='small'
                                onClick={() => {
                                    handleOpenCreateEdit(rowData?.id);
                                }}>
                                <Icon fontSize='small' style={{ color: "gray" }}>
                                    remove_red_eye
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        {!isLock && (isAdmin || isManager) && (
                            <Tooltip title='Tính lại phiếu lương' placement='top'>
                                <IconButton
                                    className='bg-white ml-4'
                                    size='small'
                                    onClick={() => {
                                        handleOpenRecalculatePayslip(rowData?.id);
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        monetization_on
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}

                        {!isLock && (isAdmin || isManager) && (
                            <Tooltip title='Xóa phiếu lương' placement='top'>
                                <IconButton className='ml-4' size='small' onClick={() => handleDelete(rowData)}>
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}
                    </div>
                );
            },
        },
        {
            align: "center",
            title: "Mã nhân viên",
            field: "staff.staffCode",
        },
        {
            align: "center",
            title: "Họ tên",
            field: "staff.displayName",
        },
        // {
        //   title: "Ngày sinh",
        //   field: "birthDate",
        //   render: (rowData) => (
        //     <span>
        //       {rowData?.staff?.birthDate && (formatDate("DD/MM/YYYY", rowData?.staff?.birthDate))}
        //     </span>
        //   ),
        // },
        {
            title: "Kỳ lương",
            field: "salaryPeriod.name",
            render: (rowData) => (
                <span>
                    {rowData?.salaryPeriod && (
                        <>
                            {`${rowData?.salaryPeriod?.name}`}
                            <br />
                            {`(${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.fromDate)} - ${formatDate(
                                "DD/MM/YYYY",
                                rowData?.salaryPeriod?.toDate
                            )})`}
                        </>
                    )}
                </span>
            ),
        },
        {
            title: "Mẫu bảng lương",
            field: "salaryTemplate.name",
        },

        {
            align: "center",
            title: "Trạng thái",
            field: "approvalStatus",
            render: (value) => <span className='px-6'>{getApprovalStatusName(value?.approvalStatus)}</span>,
        },
    ];


    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListDelete}
            columns={columns}
            data={orphanedPayslipsList || []}
            noPagination
        />
    );
}

export default memo(observer(OrphanedPayslipList));
