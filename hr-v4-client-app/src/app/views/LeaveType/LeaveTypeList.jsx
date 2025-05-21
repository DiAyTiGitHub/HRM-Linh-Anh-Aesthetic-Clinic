import React, { useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';

export default observer(function LeaveType() {
    const { leaveTypeStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        leaveTypeList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleEdit,
        handleSelectListLeaveType,
        handleOpenView
    } = leaveTypeStore;

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
                        title={"Chi tiết loại nghỉ"}
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

                    {(isManager || isAdmin) && (
                        <Tooltip
                            arrow
                            title={"Cập nhật thông tin loại nghỉ"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleEdit(rowData.id)}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}

                    {(isManager || isAdmin) && (
                        <Tooltip
                            arrow
                            title={"Xóa thông tin loại nghỉ"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleDelete(rowData.id)}>
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
            title: t("leaveType.code"),
            width: "10%",
            field: "code",
            minWidth: "150px",
        },

        {
            title: t("leaveType.name"),
            width: "30%",
            field: "name",
            minWidth: "150px",
        },

        {
            title: t("leaveType.description"),
            field: "description",
            width: "40%",
        }
        ,
        {
            title: "Nghỉ có lương",
            field: "isPaid",
            width: "10%",
            render: data => {

                return (
                    <div className="flex justify-center w-100">
                        {data?.isPaid && (
                            <CheckIcon fontSize="small" style={{ color: "green" }} />
                        )}
                    </div>
                );

            }
        }
    ];
    return (
        <GlobitsTable
            selection
            data={leaveTypeList}
            handleSelectList={handleSelectListLeaveType}
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
})
    ;
