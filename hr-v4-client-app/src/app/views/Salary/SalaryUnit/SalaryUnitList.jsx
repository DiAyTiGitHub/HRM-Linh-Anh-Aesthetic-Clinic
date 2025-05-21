import React , { memo , useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton , Icon , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import Config from "../../../common/GlobitsConfigConst";

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

function SalaryUnitList() {
    const {salaryUnitStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        listSalaryUnit ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleOpenView
    } = salaryUnitStore;

    const {
        isAdmin ,
        isManager ,
        checkAllUserRoles
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    } , []);
    let columns = [
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
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData?.id)}>
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
                            <IconButton size="small" onClick={() => handleDelete(rowData?.id)}>
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
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Tên đơn vị" ,
            field:"name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Ngày công (của 1 người)" ,
            field:"manDays" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
    ];

    return (
        <GlobitsTable
            selection
            data={listSalaryUnit}
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

export default memo(observer(SalaryUnitList));
