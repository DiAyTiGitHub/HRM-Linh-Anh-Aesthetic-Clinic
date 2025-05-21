import React , { memo , useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { Icon , IconButton , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";

function GroupPositionTitleV2List() {
    const {positionTitleV2Store} = useStore();
    const {t} = useTranslation();

    const {
        listParentPositionTitle ,
        totalParentPages ,
        totalParentElements ,
        searchParentObject ,
        handleParentChangePage ,
        setParentPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
        getTitleType ,
        handleOpenView ,
    } = positionTitleV2Store;
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
            title:"Mã nhóm" ,
            field:"code" ,
            align:"left" ,
        } ,
        {
            title:"Tên nhóm" ,
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
        }
    ];

    return (
        <GlobitsTable
            selection
            data={listParentPositionTitle}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalParentPages}
            handleChangePage={handleParentChangePage}
            setRowsPerPage={setParentPageSize}
            pageSize={searchParentObject?.pageSize}
            pageSizeOption={[10 , 15 , 25 , 50 , 100]}
            totalElements={totalParentElements}
            page={searchParentObject?.pageIndex}
        />
    );
}

export default memo(observer(GroupPositionTitleV2List));
