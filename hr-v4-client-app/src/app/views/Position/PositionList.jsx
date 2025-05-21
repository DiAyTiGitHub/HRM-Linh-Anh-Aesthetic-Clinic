import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from "@material-ui/icons/Check";

function PositionList() {
    const { positionStore } = useStore();
    const { t } = useTranslation();

    const {
        listPosition,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleTransferStaff,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenView,
    } = positionStore;
    const {
        isAdmin,
        checkAllUserRoles,
        isManager
    } = useStore().hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
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
                    {(isAdmin || isManager) && (
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
                    {(isAdmin || isManager) && (
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
                    {(isAdmin && rowData?.staff) && (
                        <Tooltip title='Điều chuyển nhân viên' placement='left'>
                            <IconButton
                                size='small'
                                onClick={() => handleTransferStaff(rowData)}
                            >
                                <Icon fontSize='small' color='secondary'>
                                    compare_arrows
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
            ),
        },
        {
            title: "Mã vị trí",
            field: "code",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Tên vị trí",
            field: "name",
            align: "left",
            minWidth: "150px",
            render: (data) => {
                if (data.departmentManager) {
                    return (
                        <Tooltip title='Vị trí quản lý phòng ban'>
                            <span>
                                {data.name}
                                <span style={{ color: "red" }}> * </span>
                            </span>
                        </Tooltip>
                    );
                }
                return data.name;
            },
        },
        {
            title: "Chức danh",
            field: "title",
            minWidth: "150px",
            render: (data) => data?.title?.name,
            align: "left",
        },
        {
            title: "Đơn vị",
            field: "organization",
            minWidth: "150px",
            render: (data) => data?.department?.organization?.name,
            align: "left",
        },
        {
            title: "Phòng ban",
            field: "department",
            minWidth: "150px",
            render: (data) => data?.department?.name,
            align: "left",
        },
        {
            title: "Nhân viên",
            field: "staff.displayName",
            align: "left",
            minWidth: "150px",
            render: (data) => {
                const displayName = data?.staff?.displayName ?? "";
                const staffCode = data?.staff?.staffCode ?? "";
                return displayName && staffCode
                    ? `${displayName} - ${staffCode}`
                    : displayName || staffCode || "Vacant";
            },
        },

        {
            title: "Vị trí chính",
            field: "isMain",
            width: "10%",
            align: "center",
            minWidth: "150px",
            render: (data) => {
                if (data?.isMain) return <CheckIcon fontSize='small' style={{ color: "green" }} />;
                return "";
            },
        },
        {
            title: "Vị trí kiêm nhiệm",
            field: "isConcurrent",
            width: "10%",
            align: "center",
            minWidth: "150px",
            render: (data) => {
                if (data?.isConcurrent) return <CheckIcon fontSize='small' style={{ color: "green" }} />;
                return "";
            },
        },
    ];

    return (
        <GlobitsTable
            selection
            data={listPosition}
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

export default memo(observer(PositionList));
