import React , { memo , useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton , Icon , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate , formatMoney } from "app/LocalFunction";
import Config from "../../../common/GlobitsConfigConst";

function SalaryAreaList() {
    const {salaryAreaStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        listSalaryArea ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        handleOpenView
    } = salaryAreaStore;
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
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Tên vùng lương" ,
            field:"name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Mức lương tối thiểu tháng (đồng/tháng)" ,
            field:"minMonth" ,
            align:"left" ,
            minWidth:"150px" ,
            render:(value) => formatMoney(value?.minMonth)
        } ,
        {
            title:"Mức lương tối thiểu giờ (đồng/giờ)" ,
            field:"minHour" ,
            align:"left" ,
            minWidth:"150px" ,
            render:(value) => formatMoney(value?.minHour)
        } ,
    ];

    return (
        <GlobitsTable
            selection
            data={listSalaryArea}
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

export default memo(observer(SalaryAreaList));
