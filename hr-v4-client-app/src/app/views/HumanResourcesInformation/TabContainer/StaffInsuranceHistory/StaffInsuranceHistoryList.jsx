import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, formatMoney } from "app/LocalFunction";
import LocalConstants from "app/LocalConstants";

function StaffInsuranceHistoryList() {
    const { staffInsuranceHistoryStore } = useStore();
    const { t } = useTranslation();

    const {
        staffInsuranceHistoryList,
        handleDelete,
        handleEdit,
        handleSelectListStaffLeave
    } = staffInsuranceHistoryStore;

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

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => (
                <div className="flex flex-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title="Cập nhật"
                    >
                        <IconButton size='small' onClick={() => handleEdit(rowData?.id)}>
                            <Icon fontSize='small' color='primary'>
                                edit
                            </Icon>
                        </IconButton>
                    </Tooltip>

                    <Tooltip
                        arrow
                        placement="top"
                        title="Xóa"
                    >
                        <IconButton size='small' onClick={() => handleDelete(rowData?.id)}>
                            <Icon fontSize='small' color='error'>
                                delete
                            </Icon>
                        </IconButton>
                    </Tooltip>

                </div>
            ),
        },
        {
            align: "center",
            title: "Mức lương đóng BHXH",
            field: "insuranceSalary",
            minWidth: "150px",
            render: (value) => formatMoney(value?.insuranceSalary)
        },

        {
            align: "center",
            title: "Tỷ lệ cá nhân đóng (%)",
            field: "staffPercentage",
            minWidth: "150px",
            render: function (value) {
                return value?.staffPercentage;
            }
        },

        {
            align: "center",
            title: "Số tiền nhân viên đóng (VNĐ) ",
            field: "staffInsuranceAmount",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'staffPercentage',
                });
            }
        },

        {
            align: "center",
            title: "Tỷ lệ đơn vị đóng (%)",
            field: "orgPercentage",
            minWidth: "150px",
            render: function (value) {
                return value?.orgPercentage;
            }
        },

        {
            align: "center",
            title: "Số tiền đơn vị đóng (VNĐ) ",
            field: "orgInsuranceAmount",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'orgPercentage',
                });
            }
        },

        {
            align: "center",
            title: "Tổng tiền",
            minWidth: "150px",
            field: "totalInsuranceAmount",
            render: function (value) {
                const res = formatMoney(value?.staffInsuranceAmount + value?.orgInsuranceAmount);
                if (value?.id) {
                    return res;
                }
                return res + " VNĐ"
            }
        },
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListStaffLeave}
            data={staffInsuranceHistoryList}
            columns={columns}
            nonePagination
        />
    );
}

export default memo(observer(StaffInsuranceHistoryList));
