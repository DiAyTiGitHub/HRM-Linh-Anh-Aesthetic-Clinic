import { Icon , IconButton , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import React , { memo , useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import Config from "../../common/GlobitsConfigConst";

function MaterialButton(props) {
    const {item} = props;
    return (
        <div>
            <IconButton size="small" onClick={() => props.onSelect(item , 0)}>
                <Icon fontSize="small" color="primary">
                    edit
                </Icon>
            </IconButton>
            <IconButton size="small" onClick={() => props.onSelect(item , 1)}>
                <Icon fontSize="small" color="secondary">
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

function DepartmentTypeList() {
    const {departmentTypeStore} = useStore();
    const {t} = useTranslation();

    const {
        listDepartmentType ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleOpenView
    } = departmentTypeStore;
    const {
        isAdmin ,
        checkAllUserRoles
    } = useStore().hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    } , []);
    let columns = [
        {
            title:t("general.action") ,
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
                    {(isAdmin) && (
                        <Tooltip
                            arrow
                            title={"Cập nhật thông tin"}
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData?.id)}>
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
                </div>
            ) ,
        } ,
        {
            title:"Mã" ,
            field:"code" ,
            align:"center" ,
            minWidth:"150px" , t
        } ,
        {
            title:"Loại phòng ban" ,
            field:"name" ,
            align:"left" ,
        } ,
        {
            title:"Tên khác" ,
            field:"otherName" ,
            align:"left" ,
        } ,
        {
            title:"Tên viết tắt" ,
            field:"shortName" ,
            align:"left" ,
        } ,
        {
            title:"Trọng số" ,
            field:"sortNumber" ,
            minWidth:"150px" ,
            align:"center" ,
        } ,
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
            data={listDepartmentType}
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

export default memo(observer(DepartmentTypeList));
