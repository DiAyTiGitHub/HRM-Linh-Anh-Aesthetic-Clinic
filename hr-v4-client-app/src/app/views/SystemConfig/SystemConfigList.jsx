import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';

function SystemConfigList() {
    const {
        systemConfigStore,
        hrRoleUtilsStore
    } = useStore();

    const { t } = useTranslation();

    const {
        systemConfigList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleOpenCreateEdit,
        handleSelectListDelete,
        handleOpenView
    } = systemConfigStore;

    const {
        isAdmin,
        isManager
    } = hrRoleUtilsStore;

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
                        title={"Chi tiết Cấu hình hệ thống"}
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
                            title={"Cập nhật thông tin Cấu hình hệ thống"}
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
                            title={"Xóa thông tin Cấu hình hệ thống"}
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
            title: "Mã cấu hình",
            width: "30%",
            field: "configKey",
            // align : "center",
        },

        {
            title: "Giá trị cấu hình",
            width: "40%",
            maxWidth: "480px",
            field: "configValue",
            // align : "center",
            render: data => (
                <Tooltip
                    arrow
                    placement="top"
                    title={data?.configValue}
                >
                    <span className="px-6 multiline-ellipsis">
                        {data?.configValue}
                    </span>
                </Tooltip>
            )
        },

        {
            title: "Ghi chú",
            field: "note",
        }
        ,

    ];
    return (
        <GlobitsTable
            selection
            data={systemConfigList}
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

export default memo(observer(SystemConfigList));
