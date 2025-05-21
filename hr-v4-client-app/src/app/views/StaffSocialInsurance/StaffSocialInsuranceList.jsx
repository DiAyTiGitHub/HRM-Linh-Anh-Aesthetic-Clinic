import React, { memo, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, formatMoney, formatVNDMoney } from "app/LocalFunction";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import LocalConstants from "app/LocalConstants";
import VisibilityIcon from '@material-ui/icons/Visibility';
import ViewStaffSocialInsurance from "./ViewStaffSocialInsurance";

function StaffSocialInsuranceList() {
    const { staffSocialInsuranceStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        listStaffSocialInsurance,
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
        handleViewStaffSocialInsurance,
        openViewStaffSocialInsurance,
    } = staffSocialInsuranceStore;

    const {
        isCompensationBenifit,
        checkAllUserRoles,
        isManager,
        isAdmin
    } = hrRoleUtilsStore;

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    function renderInsuranceAmount({ value, percentageKey }) {
        const insuranceSalary = value?.insuranceSalary || 0;
        const percentage = value?.[percentageKey] || 0;
        const currency = value?.id ? '' : ' VNĐ';
        const calculatedAmount = (insuranceSalary * percentage) / 100;

        // Nếu là dòng tổng (không có id) và không có phần trăm thì không hiển thị gì
        if (!value?.id && percentage === 0) return null;

        return (
            <span style={{ whiteSpace: 'pre-line' }}>
                {calculatedAmount.toLocaleString()} {currency}
                {/* {percentage ? `\n(${percentage}%)` : ''} */}
            </span>
        );
    }

    let canMannipulateData = false;
    if (isAdmin || isManager || isCompensationBenifit) {
        canMannipulateData = true;
    }

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            minWidth: "100px",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        {/* <Tooltip title="Chi tiết" placement="top">
                            <IconButton className="ml-4" size="small" onClick={() => handleViewStaffSocialInsurance(rowData?.id)}>
                                <VisibilityIcon fontSize="small" color="action" />
                            </IconButton>
                        </Tooltip> */}

                        <Tooltip title="Cập nhật" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleOpenCreateEdit(rowData?.id);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        {(canMannipulateData &&
                            <>
                                <Tooltip title="Xóa" placement="top">
                                    <IconButton className="ml-4" size="small" onClick={() => handleDelete(rowData)}>
                                        <Icon fontSize="small" color="secondary">
                                            delete
                                        </Icon>
                                    </IconButton>
                                </Tooltip>

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
                            </>
                        )}
                    </div >
                );
            },
        },
        {
            align: "center",
            title: "Mã nhân viên",
            field: "staff.staffCode",
            minWidth: "150px",
        },
        {
            align: "center",
            title: "Tên nhân viên",
            field: "staff.displayName",
            minWidth: "150px",
        },
        {
            title: "Kỳ lương",
            field: "salaryPeriod.name",
            minWidth: "150px",
            render: (rowData) => (
                <span>
                    {
                        rowData?.salaryPeriod && (
                            <>
                                {`${rowData?.salaryPeriod?.name}`}
                                <br />
                                {`(${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.fromDate)} - ${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.toDate)})`}
                            </>
                        )
                    }
                </span>
            ),
        },
        // {
        //     title: "Dữ liệu từ",
        //     field: "salaryResult.name",
        //     minWidth: "150px",
        // },
        {
            align: "center",
            title: "Mức lương đóng BHXH",
            field: "insuranceSalary",
            minWidth: "150px",
            render: (value) => formatMoney(value?.insuranceSalary)
        },
        {
            align: "center",
            title: "Số tiền BHXH của nhân viên đóng (8%)",
            field: "socialInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'staffSocialInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHYT nhân viên đóng (1.5%) ",
            field: "healthInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'staffHealthInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHTN nhân viên đóng (1%)",
            field: "unemploymentInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'staffUnemploymentInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Tổng tiền bảo hiểm nhân viên đóng ",
            field: "staffTotalInsuranceAmount",
            minWidth: "150px",
            render: function (value) {
                const res = formatMoney(value?.staffTotalInsuranceAmount || 0);
                if (!res) return "";
                if (value?.id) {
                    return res;
                }
                return res + " VNĐ"
            }
        },
        {
            align: "center",
            title: "Số tiền BHXH của công ty đóng (17.5%)",
            field: "socialInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'orgSocialInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHYT công ty đóng (3%)",
            field: "healthInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'orgHealthInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHTN công ty đóng (1%)",
            field: "unemploymentInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'orgUnemploymentInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Tổng tiền bảo hiểm công ty đóng",
            field: "orgTotalInsuranceAmount",
            minWidth: "150px",
            render: function (value) {
                const res = formatMoney(value?.orgTotalInsuranceAmount || 0);
                if (!res) return "";
                if (value?.id) {
                    return res;
                }
                return res + " VNĐ"
            }
        },
        {
            align: "center",
            title: "Tổng tiền",
            minWidth: "150px",
            field: "totalInsuranceAmount",
            render: function (value) {
                const res = formatMoney(value?.totalInsuranceAmount);
                if (value?.id) {
                    return res;
                }
                return res + " VNĐ"
            }
        },

        // {
        //     align: "center",
        //     title: "Tỷ lệ cá nhân đóng",
        //     field: "staffPercentage",
        //     minWidth: "150px",
        //     render: (value) => (
        //         <>
        //             {value?.staffPercentage && (
        //                 <span>
        //                     {`${formatVNDMoney(value?.staffPercentage)}%`}
        //                 </span>
        //             )}
        //         </>
        //     ),
        // },
        // {
        //     align: "center",
        //     title: "Số tiền cá nhân đóng",
        //     field: "staffInsuranceAmount",
        //     minWidth: "150px",
        //     render: (value) => formatMoney(value?.staffInsuranceAmount)
        // },
        // {
        //     align: "center",
        //     title: "Tỷ lệ đơn vị đóng",
        //     field: "orgPercentage",
        //     minWidth: "150px",
        //     render: (value) => (
        //         <>
        //             {value?.orgPercentage && (
        //                 <span>
        //                     {`${formatVNDMoney(value?.orgPercentage)}%`}
        //                 </span>
        //             )}
        //         </>
        //     ),
        // },
        // {
        //     align: "center",
        //     title: "Số tiền đơn vị đóng",
        //     field: "orgInsuranceAmount",
        //     minWidth: "150px",
        //     render: (value) => formatMoney(value?.orgInsuranceAmount)
        // },
        // {
        //     align: "center",
        //     title: "Tổng tiền",
        //     field: "totalInsuranceAmount",
        //     minWidth: "150px",
        //     render: (value) => formatMoney(value?.totalInsuranceAmount)
        // },
        {
            align: "center",
            title: "Trạng thái",
            field: "paidStatus",
            minWidth: "150px",
            render: (value) => (
                <span className="pr-4">
                    {getPaidStatusName(value?.paidStatus)}
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
                data={listStaffSocialInsurance}
                handleSelectList={handleSelectListDelete}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 25, 50, 100, 200]}
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
                    {selectedRow?.paidStatus != LocalConstants.StaffSocialInsurancePaidStatus.PAID.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmChangeStatus(LocalConstants.StaffSocialInsurancePaidStatus.PAID.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small">
                                attach_money
                            </Icon>
                            Chi trả
                        </MenuItem>
                    )}


                    {selectedRow?.paidStatus != LocalConstants.StaffSocialInsurancePaidStatus.UNPAID.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmChangeStatus(LocalConstants.StaffSocialInsurancePaidStatus.UNPAID.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{ color: "green" }}>
                                money_off
                            </Icon>
                            Đặt lại
                        </MenuItem>
                    )}
                </Menu>
            )}

            {openViewStaffSocialInsurance && <ViewStaffSocialInsurance />}
        </>
    );
}

export default memo(observer(StaffSocialInsuranceList));
