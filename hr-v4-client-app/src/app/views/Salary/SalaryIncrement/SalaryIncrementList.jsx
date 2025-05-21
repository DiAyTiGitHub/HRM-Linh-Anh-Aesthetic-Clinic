import React , { useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon , IconButton , Tooltip } from "@material-ui/core";
import Config from "app/common/GlobitsConfigConst";
import { observer } from "mobx-react";

export default observer(function SalaryIncrementList() {
    const {salaryIncrementStore} = useStore();
    const {t} = useTranslation();
    const {
        handleSelectListSalaryIncrement ,
        salaryIncrementList ,
        totalPages ,
        handleChangePage ,
        setRowsPerPage ,
        rowsPerPage ,
        totalElements ,
        page ,
        handleEditSalaryIncrement ,
        handleDelete ,
        handleOpenView
    } = salaryIncrementStore;
    const {
        isAdmin ,
        isManager ,
        checkAllUserRoles
    } = useStore().hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    } , []);
    let columns = [
        {
            title:t("general.action") ,
            width:"10%" ,
            minWidth:"100px" ,
            ... Config.tableCellConfig ,
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
                            <IconButton size="small" onClick={() => handleEditSalaryIncrement(rowData?.id)}>
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
            title:t("salaryItem.code") ,
            field:"code" ,
            minWidth:"100px" ,
            ... Config.tableCellConfig ,
        } ,
        {
            title:t("salaryItem.name") ,
            field:"name" ,
            minWidth:"200px" ,
            ... Config.tableCellConfig ,
        }
    ]
    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListSalaryIncrement}
            data={salaryIncrementList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10 , 25 , 50]}
            totalElements={totalElements}
            page={page}
        />
    );
});