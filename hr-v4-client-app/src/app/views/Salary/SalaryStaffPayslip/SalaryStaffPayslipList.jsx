import React, { memo, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, formatMoney, formatVNDMoney } from "app/LocalFunction";
import { useHistory } from "react-router-dom";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import LocalConstants from "app/LocalConstants";

function SalaryStaffPayslipList() {
    const { salaryStaffPayslipStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        listSalaryStaffPayslip,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        getPaidStatusName,
        handleOpenCreateEdit,
        handleSelectListDelete,
        handleDelete,
        handleOpenConfirmChangeStatus,
        handleOpenRecalculatePayslip,
    } = salaryStaffPayslipStore;

    const { isAdmin, isManager, isCompensationBenifit } = hrRoleUtilsStore;

    const history = useHistory();

    const [anchorEl, setAnchorEl] = useState();

    const [selectedRow, setSelectedRow] = useState(null);

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => {
                let isLock = rowData?.isLocked;

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

                        {!isLock && (isCompensationBenifit) && (
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

                        {!isLock && (isCompensationBenifit) && (
                            <Tooltip title='Xóa phiếu lương' placement='top'>
                                <IconButton className='ml-4' size='small' onClick={() => handleDelete(rowData)}>
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}

                        {!isLock && (isCompensationBenifit) && (
                            <Tooltip title='Thao tác khác' placement='top'>
                                <IconButton
                                    className='ml-4'
                                    size='small'
                                    onClick={(event) => {
                                        setSelectedRow(rowData);
                                        setAnchorEl(event?.currentTarget);
                                    }}>
                                    <MoreHorizIcon />
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
            field: "paidStatus",
            render: (value) => <span className='px-6'>{getPaidStatusName(value?.paidStatus)}</span>,
        },
    ];

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    return (
        <>
            <GlobitsTable
                selection
                data={listSalaryStaffPayslip}
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

            {Boolean(anchorEl) && (
                <Menu
                    id={"simple-menu-options"}
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClosePopover}
                    className='py-0'>
                    {selectedRow?.paidStatus !== LocalConstants.StaffPayslipsPaidStatus.PAID.value && (
                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmChangeStatus(
                                    LocalConstants.StaffPayslipsPaidStatus.PAID.value
                                );
                                handleClosePopover();
                            }}>
                            <Icon className='pr-6' fontSize='small'>
                                done_all
                            </Icon>
                            Đã chi trả
                        </MenuItem>
                    )}

                    {selectedRow?.paidStatus !==
                        LocalConstants.StaffPayslipsPaidStatus.UNPAID.value && (
                            <MenuItem
                                className='flex items-center justify-center'
                                onClick={function () {
                                    handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                    handleOpenConfirmChangeStatus(
                                        LocalConstants.StaffPayslipsPaidStatus.UNPAID.value
                                    );
                                    handleClosePopover();
                                }}>
                                <Icon className='pr-6' fontSize='small'>
                                    close
                                </Icon>
                                Chưa chi trả
                            </MenuItem>
                        )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(SalaryStaffPayslipList));
