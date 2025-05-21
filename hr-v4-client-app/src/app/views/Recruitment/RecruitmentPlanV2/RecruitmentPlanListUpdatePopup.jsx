import { observer } from 'mobx-react';
import React , { memo } from "react";
import { Icon , IconButton , Grid , Button , Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';

function RecruitmentPlanListUpdatePopup() {
    const {recruitmentPlanStore} = useStore();
    const {t} = useTranslation();
    const {
        openConfirmUpdateStatusPopup ,
        handleClose ,
        handleRemoveActionItem ,
        listChosen ,
        onUpdateStatus ,
        handleSelectListChosen ,
        pagingRecruitmentPlan ,
        handleConfirmUpdateStatus
    } = recruitmentPlanStore;

    const columns = [
        {
            title:"Mã kế hoạch" ,
            width:"10%" ,
            field:"code" ,
            align:"left" ,
        } ,
        {
            title:"Tên kế hoạch" ,
            width:"20%" ,
            field:"name" ,
            align:"left" ,
        } ,
        {
            title:"Yêu cầu tuyển dụng" ,
            width:"20%" ,
            field:"recruitmentRequest.name" ,
            align:"left" ,
        } ,
        {
            title:"Số lượng" ,
            width:"10%" ,
            field:"quantity" ,
            align:"left" ,
        } ,
        {
            title:"Dự kiến từ" ,
            field:"estimatedTimeFrom" ,
            render:(rowData) => (
                <span>
                    {rowData?.estimatedTimeFrom && (formatDate("DD/MM/YYYY" , rowData?.estimatedTimeFrom))}
                </span>
            ) ,
        } ,
        {
            title:"Dự kiến đến" ,
            field:"estimatedTimeTo" ,
            render:(rowData) => (
                <span>
                    {rowData?.estimatedTimeTo && (formatDate("DD/MM/YYYY" , rowData?.estimatedTimeTo))}
                </span>
            ) ,
        } ,

        {
            title:"Trạng thái hiện tại" ,
            width:"10%" ,
            field:"status" ,
            align:"left" ,
            render:row =>
                <span>{LocalConstants.RecruitmentPlanStatus.getListData().find(i => i.value == row?.status)?.name}</span>
        } ,
        {
            title:t("general.action") ,
            width:"6%" ,
            align:"center" ,
            render:(rowData) => {
                return (
                    <div className="flex flex-middle w-100 justify-center">
                        <Tooltip title="Loại bỏ" placement="top">
                            <IconButton className="" size="small" onClick={() => handleRemoveActionItem(rowData?.id)}>
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            } ,
        } ,
    ];


    function handleCloseConfirmPopup() {
        handleClose();
        pagingRecruitmentPlan();
    }

    return (
        <GlobitsColorfulThemePopup
            open={openConfirmUpdateStatusPopup}
            handleClose={handleCloseConfirmPopup}
            size="lg"
            onConfirm={handleConfirmUpdateStatus}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong>
                                    {listChosen?.length <= 1 ? "Thông tin " : "Danh sách "}
                                    kế hoạch tuyển dụng được cập nhật
                                    thành {LocalConstants.RecruitmentPlanStatus.getListData().find(i => i.value == onUpdateStatus)?.name?.toUpperCase()}
                                </strong>
                            </h6>
                            <GlobitsTable
                                data={listChosen}
                                handleSelectList={handleSelectListChosen}
                                columns={columns}
                                nonePagination
                            />
                        </div>
                    </Grid>
                </Grid>
            </div>
        </GlobitsColorfulThemePopup>
    );
}

export default memo(observer(RecruitmentPlanListUpdatePopup));