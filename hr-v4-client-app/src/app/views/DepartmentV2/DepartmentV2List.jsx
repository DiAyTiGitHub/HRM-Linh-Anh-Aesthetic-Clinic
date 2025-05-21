import React, { memo, useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { getDate } from "app/LocalFunction";
import { useHistory } from "react-router-dom/cjs/react-router-dom.min";
import ConstantList from "../../appConfig";
import Config from "../../common/GlobitsConfigConst";
import CheckIcon from "@material-ui/icons/Check";

function DepartmentV2List() {
    const {
        departmentV2Store,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const history = useHistory();

    const handleNavigate = (id) => {
        history.push(ConstantList.ROOT_PATH + "category/department/diagram/" + id);
    };

    const {
        listDepartment,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenView,
    } = departmentV2Store;

    const {
        isAdmin,
        checkAllUserRoles
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    }, []);

    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            minWidth: "150px",
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
                    {(isAdmin) && (
                        <Tooltip
                            arrow
                            title={"Cập nhật thông tin"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData.id)}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                    {(isAdmin) && (
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
                    <Tooltip title='Cơ cấu tổ chức theo phòng ban' placement='top'>
                        <IconButton size='small' className='ml-4' onClick={() => handleNavigate(rowData?.id)}>
                            <Icon fontSize='small'>
                                account_tree
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            ),
        },
        {
            title: t("department.code"),
            field: "code",
            minWidth: "200px",
            render: data => {
                return (
                    <span className='px-4'>{`${data?.code || ""}`}</span>
                );
            }
        },
        {
            title: "Tên phòng ban",
            field: "name",
            minWidth: "200px",
        },
        {
            title: "Đơn vị trực thuộc",
            field: "organization.name",
            minWidth: "200px",
            render: (data) => (
                <span className='px-4'>{`${data?.organization?.name || ""} - ${data?.organization?.code || ""}`}</span>
            ),
        },
        {
            title: "Phòng đã có người quản lý",
            field: "custom",
            minWidth: "150px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => rowData?.positionManager ? <CheckIcon fontSize="small" style={{ color: "green" }} /> : "",
        },
        {
            title: t("department.shortName"),
            field: "shortName",
            minWidth: "200px",
            render: (data) => data?.shortName,
        },
        {
            title: t("department.description"),
            field: "description",
            minWidth: "200px",
        },
    ];

    const uniqueData = Array.from(new Map(listDepartment.map((item) => [item.id, item])).values());

    return (
        <GlobitsTable
            selection
            // isPositionManager={searchObject?.isManager}
            data={uniqueData}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[5, 10, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
            colParent
            defaultExpanded={(searchObject?.keyword || searchObject?.organization) ? true : false}
        />
    );
}

export default memo(observer(DepartmentV2List));
