import React , { memo , useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton , Icon , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";

function HrDepartmentIpList() {
    const {hrDepartmentIpStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        listDepartmentIp ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleOpenView
    } = hrDepartmentIpStore;
    const {isAdmin , isManager , checkAllUserRoles} = hrRoleUtilsStore;
    useEffect(() => {
        checkAllUserRoles()
    } , []);
    const columns = [
        {
            title:t("general.action") ,
            width:"10%" ,
            minWidth:"100px" ,
            render:(rowData) => (
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
                            <Icon fontSize="small" style={{color:"green"}}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>
                    {(isManager || isAdmin) && (
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
                    {(isManager || isAdmin) && (
                        <Tooltip
                            arrow
                            title={"Xóa thông tin"}
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
            ) ,
        } ,
        {
            title:"Địa chỉ" ,
            field:"ipAddress" ,
            align:"left" ,
            width:"20%" ,
            minWidth:"150px" ,
        } ,
        // {
        //     title:"Đơn vị" ,
        //     field:"department.organization.name" ,
        //     align:"left" ,
        //     minWidth:"150px" ,
        // } ,
        // {
        //     title:"Phòng ban sử dụng" ,
        //     field:"department.name" ,
        //     align:"left" ,
        //     minWidth:"150px" ,
        // } ,
        {
            title:"Mô tả" ,
            field:"description" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
    ];

    return (
        <GlobitsTable
            selection
            data={listDepartmentIp}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10 , 15 , 25 , 50 , 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(HrDepartmentIpList));
