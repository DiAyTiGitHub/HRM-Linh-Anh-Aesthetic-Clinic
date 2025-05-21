import React, {memo} from "react";
import GlobitsTable from "../../common/GlobitsTable";
import {useStore} from "../../stores";
import {useTranslation} from "react-i18next";
import {IconButton, Icon, Tooltip} from "@material-ui/core";
import {observer} from "mobx-react";
import LocalConstants from "../../LocalConstants";
import {getDate} from "app/LocalFunction";

function StaffPositionList() {
    const {t} = useTranslation();

    const {staffPositionStore} = useStore();
    const {
        staffPositionList,
        totalElements,
        totalPages,
        searchObject,
        handleChangePage,
        setPageSize,
        handleOpenCreateEdit,
        handleDelete
    } = staffPositionStore;

    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Cập nhật thông tin" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleOpenCreateEdit(rowData?.id);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Xóa" placement="top">
                            <IconButton
                                size="small"
                                className="ml-4"
                                onClick={() => handleDelete(rowData)
                                }
                            >
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: t("Nhân viên"),
            field: "staff.displayName",
            align: "left",
        },
        {
            title: t("Vị trí"),
            field: "position.name",
            align: "left",
        },
        {
            title: t("Mối quan hệ"),
            field: "relationshipType",
            render: data => {
                const relationship = LocalConstants.RelationshipType.getListData().find(item => item.value === data.relationshipType);
                return <span>{relationship ? relationship.name : "Không xác định"}</span>;
            },
        },
        {
            title: t("Phòng ban"),
            field: "hrDepartment.name",
            align: "left",
        },
        {
            title: t("Người quản lý"),
            field: "supervisor.displayName",
            align: "left",
        },
        {
            title: t("Từ ngày"),
            field: "fromDate",
            render: data => <span>{getDate(data?.fromDate)}</span>,
                align: "left",
            },
            {
                title: t("Đến ngày"),
                field: "toDate",
                render: data => <span>{getDate(data?.toDate)}</span>,
            align: "left",
        }
    ];
    return (
        <GlobitsTable
            data={staffPositionList}
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

export default memo(observer(StaffPositionList));