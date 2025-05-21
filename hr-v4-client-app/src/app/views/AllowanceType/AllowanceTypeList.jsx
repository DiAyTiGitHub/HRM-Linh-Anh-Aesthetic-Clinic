import React , { useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { Icon , IconButton , Tooltip } from "@material-ui/core";

export default observer(function AllowanceTypeList() {
    const {allowanceTypeStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        allowanceTypeList ,
        totalPages ,
        totalElements ,
        rowsPerPage ,
        page ,
        handleChangePage ,
        setRowsPerPage ,
        handleDelete ,
        handleEditAllowanceType ,
        handleSelectListAllowanceType ,
        handleOpenView
    } = allowanceTypeStore;
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
                            <IconButton size="small" onClick={() => handleEditAllowanceType(rowData.id)}>
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
            title:t("allowanceType.name") ,
            field:"name" ,
            minWidth:"150px" ,
        } ,
        {
            title:t("allowanceType.code") ,
            field:"code" ,
            minWidth:"150px" ,
        } ,
    ];
    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListAllowanceType}
            data={allowanceTypeList}
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
