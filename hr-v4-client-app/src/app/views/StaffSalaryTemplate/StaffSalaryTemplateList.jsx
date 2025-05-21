import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";

function StaffSalaryTemplateList() {
    const {
        staffSalaryTemplateStore,
        hrRoleUtilsStore

    } = useStore();
    const { t } = useTranslation();

    const {
        staffSalaryTemplateList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenView
    } = staffSalaryTemplateStore;

    const {
        isAdmin,
        isManager,
        checkAllUserRoles,
        isCompensationBenifit
    } = hrRoleUtilsStore;
    useEffect(() => {
        checkAllUserRoles()
    }, []);

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            minWidth: "100px",
            render: (rowData) => (
                <div className="flex flex-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Chi tiết thành phần lương"}
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
                            title={"Cập nhật thông tin thành phần lương"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData.id)}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                    
                    {(isCompensationBenifit) && (
                        <Tooltip
                            arrow
                            title={"Xóa thông tin thành phần lương"}
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
            title: t("Mẫu bản lương"),
            field: "salaryTemplate.name",
            width: "20%",
            align: "center",
            minWidth: "150px",
        }
    ]

    return (
        <GlobitsTable
            selection={(isAdmin || isManager)}
            data={staffSalaryTemplateList}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 25, 50, 100, 200, 500]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(StaffSalaryTemplateList));