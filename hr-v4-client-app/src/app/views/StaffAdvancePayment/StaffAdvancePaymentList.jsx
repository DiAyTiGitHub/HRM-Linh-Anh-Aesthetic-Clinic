import React, { memo, useEffect, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, formatMoney } from "app/LocalFunction";
import { useHistory } from "react-router-dom";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import LocalConstants from "app/LocalConstants";

function StaffAdvancePaymentList() {
    const { staffAdvancePaymentStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        listStaffAdvancePayment,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        getApprovalStatusName,
        handleOpenCreateEdit,
        handleSelectListDelete,
        handleDelete,
        handleOpenConfirmChangeStatus,
        handleOpenView
    } = staffAdvancePaymentStore;

    const history = useHistory();

    const {
        isAdmin,
        isManager,
        checkAllUserRoles,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            render: (rowData) => (
                <div className="flex flex-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Xem chi tiết"}
                    >
                        <IconButton
                            className="ml-4"
                            size="small"
                            onClick={() => handleOpenView(rowData?.id)}
                        >
                            <Icon fontSize="small" style={{ color: "green" }}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>

                    {(isCompensationBenifit) && (
                        <Tooltip
                            arrow
                            title={"Cập nhật thông tin"}
                            placement="top"
                        >
                            <IconButton
                                size="small"
                                onClick={() =>
                                    handleOpenCreateEdit(rowData.id)}
                            >
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}

                    {(isCompensationBenifit) && (
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

                    {(isCompensationBenifit) && (
                        <Tooltip title="Thao tác khác" placement="top">
                            <IconButton
                                className="ml-4"
                                size="small"
                                onClick={(event) => {
                                    setSelectedRow(rowData);
                                    setAnchorEl(event?.currentTarget);
                                }}
                            >
                                <MoreHorizIcon />
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
            ),
        },
        
        {
            align: "center",
            title: "Mã nhân viên",
            field: "staff.staffCode",
        },
        {
            align: "center",
            title: "Nhân viên",
            field: "staff.displayName",
        },
        // {
        //     title: "Ngày sinh",
        //     field: "birthDate",
        //     render: (rowData) => (
        //         <span>
        //             {rowData?.staff?.birthDate && (formatDate("DD/MM/YYYY", rowData?.staff?.birthDate))}
        //         </span>
        //     ),
        // },
        {
            title: "Kỳ lương ứng tiền",
            field: "salaryPeriod.name",
            render: (rowData) => (
                <span>
                    {
                        rowData?.salaryPeriod && (
                            <>
                                {`${rowData?.salaryPeriod?.name}`}
                                <br />
                                {`(${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.fromDate)} - ${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.toDate)}`}
                            </>
                        )
                    }
                </span>
            ),
        },
        {
            title: "Ngày yêu cầu",
            field: "requestDate",
            render: (rowData) => (
                <span>
                    {rowData?.requestDate && (formatDate("DD/MM/YYYY", rowData?.requestDate))}
                </span>
            ),
        },
        {
            title: "Số tiền ứng trước",
            field: "advancedAmount",
            render: (rowData) => (
                <span>
                    {rowData?.advancedAmount && (formatMoney(rowData?.advancedAmount))}
                </span>
            ),
        },
        {
            title: "Lý do tạm ứng",
            field: "requestReason",
            render: (rowData) => (
                <span className='px-4'>
                    {rowData?.requestReason}
                </span>
            ),
        },
        {
            align: "center",
            title: "Trạng thái",
            field: "approvalStatus",
            render: (value) => (
                <span className="pr-4">
                    {getApprovalStatusName(value?.approvalStatus)}
                </span>
            )
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
                data={listStaffAdvancePayment}
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
                    className="py-0"
                >

                    {selectedRow?.approvalStatus != LocalConstants.StaffAdvancePaymentApprovalStatus.APPROVED.value && (
                        <MenuItem className="flex items-center justify-center"
                            disabled={!isAdmin}
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmChangeStatus(LocalConstants.StaffAdvancePaymentApprovalStatus.APPROVED.value);
                                handleClosePopover();
                            }}>
                            <Icon className="pr-6" fontSize="small">
                                done_all
                            </Icon>
                            <span className="pl-8">
                                Duyệt
                            </span>
                        </MenuItem>
                    )}

                    {selectedRow?.approvalStatus != LocalConstants.StaffAdvancePaymentApprovalStatus.NOT_APPROVED.value && (
                        <MenuItem className="flex items-center justify-center"
                            disabled={!isAdmin}
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmChangeStatus(LocalConstants.StaffAdvancePaymentApprovalStatus.NOT_APPROVED.value);
                                handleClosePopover();
                            }}>
                            <Icon className="pr-6" fontSize="small">
                                close
                            </Icon>
                            <span className="pl-8">
                                Không duyệt
                            </span>
                        </MenuItem>
                    )}


                    {selectedRow?.approvalStatus != LocalConstants.StaffAdvancePaymentApprovalStatus.NOT_APPROVED_YET.value && (
                        <MenuItem className="flex items-center justify-center"
                            disabled={!isAdmin}
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmChangeStatus(LocalConstants.StaffAdvancePaymentApprovalStatus.NOT_APPROVED_YET.value);
                                handleClosePopover();
                            }}>
                            <Icon className="pr-6" fontSize="small">
                                hourglass_empty
                            </Icon>
                            <span className="pl-8">
                                Đặt lại
                            </span>
                        </MenuItem>
                    )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(StaffAdvancePaymentList));
