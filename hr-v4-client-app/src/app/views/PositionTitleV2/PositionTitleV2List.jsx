import React , { memo , useEffect } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { Icon , IconButton , Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import LocalConstants from "../../LocalConstants";

function PositionTitleV2List() {
    const {positionTitleV2Store} = useStore();
    const {t} = useTranslation();

    const {
        listPositionTitle ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleSelectListDelete ,
        handleOpenCreateEdit ,
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
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Tên chức danh" ,
            field:"name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Tên khác" ,
            field:"otherName" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Tên viết tắt" ,
            field:"shortName" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,

        {
            title:"Cấp bậc" ,
            field:"rankTitle.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        // {
        //     title:"Loại chức danh" ,
        //     field:"positionTitleType" ,
        //     align:"left" ,
        //     minWidth:"150px" ,
        //     render:data => LocalConstants.PositionTitleType.getListData()
        //         .find(item => item.value === data?.positionTitleType)?.name || ""
        // } ,
        {
            title:"Số ngày tuyển dụng" ,
            field:"recruitmentDays" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Nhóm ngạch" ,
            field:"parent.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
    ];

    return (
        <GlobitsTable
            selection
            data={listPositionTitle}
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

export default memo(observer(PositionTitleV2List));
