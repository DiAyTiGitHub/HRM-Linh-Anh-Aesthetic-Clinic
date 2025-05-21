import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { getDate } from "app/LocalFunction";

function SalaryPeriodList() {
    const { salaryPeriodStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        listSalaryPeriods,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenView
    } = salaryPeriodStore;


    const {
        isAdmin,
        isManager,
        checkAllUserRoles,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    }, []);

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
                </div>
            ),
        },
        {
            title: "Mã kỳ lương",
            field: "code",
            align: "left",
        },
        {
            title: "Tên kỳ lương",
            field: "name",
            align: "left",
        },
        {
            title: "Ngày bắt đầu",
            field: "fromDate",
            align: "left",
            render: (row) => getDate(row?.fromDate)

        },
        {
            title: "Ngày kết thúc",
            field: "toDate",
            align: "left",
            render: (row) => getDate(row?.toDate)
        },
        {
            title: "Mô tả",
            field: "description",
            align: "left",
        },
    ];

    return (
        <GlobitsTable
            selection
            data={listSalaryPeriods}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(SalaryPeriodList));
