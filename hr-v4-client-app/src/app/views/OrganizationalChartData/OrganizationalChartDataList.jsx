import { Icon , IconButton , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import React , { memo , useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import appConfig from "app/appConfig";
import history from "history.js";

function OrganizationalChartDataList() {
    const {organizationalChartDataStore} = useStore();
    const {t} = useTranslation();

    const {
        listOrganizationalChartData ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,

    } = organizationalChartDataStore;
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
            minWidth:"150px" ,
            align:"center" ,
            render:(rowData) => (
                <div className="flex flex-center">
                    <Tooltip title="Chi tiết biểu đồ" placement="top">
                        <IconButton size="small" onClick={async () => {
                            history.push(appConfig.ROOT_PATH + "organization/diagram/" + rowData?.id)
                        }}>
                            <Icon fontSize="small" style={{color:"green"}}>
                                remove_red_eye
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    {(isAdmin) && (
                        <Tooltip title="Chỉnh sửa" placement="top">
                            <IconButton size="small" onClick={() => handleOpenCreateEdit(rowData?.id)}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                    {(isAdmin) && (
                        <IconButton size="small" onClick={() => handleDelete(rowData)}>
                            <Icon fontSize="small" color="secondary">
                                delete
                            </Icon>
                        </IconButton>
                    )}

                </div>

            ) ,
        } ,
        {
            title:"Mã sơ đồ" ,
            minWidth:"150px" ,
            field:"code" ,
            align:"center" ,
        } ,


        {
            title:"Tên sơ đồ" ,
            field:"name" ,
            align:"center" ,
            minWidth:"150px" ,
        } ,
    ];

    return (
        <GlobitsTable
            selection
            data={listOrganizationalChartData}
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

export default memo(observer(OrganizationalChartDataList));
