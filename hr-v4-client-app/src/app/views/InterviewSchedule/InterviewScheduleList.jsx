import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { Icon , IconButton } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
import { observer } from "mobx-react";
import LocalConstants from "app/LocalConstants";

function MaterialButton({item , onSelect}) {
    return (
        <div>
            <IconButton size='small' onClick={() => onSelect(item , 0)}>
                <Icon fontSize='small' color='primary'>
                    edit
                </Icon>
            </IconButton>
            <IconButton size='small' onClick={() => onSelect(item , 1)}>
                <Icon fontSize='small' color='secondary'>
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

export default observer(function InterviewScheduleList() {
    const {interviewScheduleStore} = useStore();
    const {t} = useTranslation();

    const {
        interviewScheduleList ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        handleDelete ,
        handleEditInterviewSchedule ,
        handleSelectListInterviewSchedule ,
    } = interviewScheduleStore;

    const columns = [
        {
            title:t("general.action") ,
            minWidth:"100px" ,
            ... Config.tableCellConfig ,
            render:(rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData , method) => {
                        if (method === 0) {
                            handleEditInterviewSchedule(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        }
                    }}
                />
            ) ,
        } ,
        {
            title:t("interviewSchedule.candidate") ,
            minWidth:"150px" ,
            field:"candidate.displayName" , // Giả sử candidate có field "name"
            ... Config.tableCellConfig ,
        } ,
        {
            title:t("interviewSchedule.interviewTime") ,
            minWidth:"150px" ,
            field:"interviewTime" ,
            ... Config.tableCellConfig ,
            render:(rowData) => {
                const formattedTime = rowData.interviewTime ? new Date(rowData.interviewTime).toLocaleString() : "";
                return formattedTime;
            } ,
        } , {
            title:t("interviewSchedule.recruitmentRound") ,
            field:"recruitmentRound" ,
            minWidth:"150px" ,
            ... Config.tableCellConfig ,
            render:(rowData) => rowData?.recruitmentRound?.name
        } ,
        {
            title:t("interviewSchedule.interviewLocation") ,
            minWidth:"150px" ,
            field:"interviewLocation" ,
            ... Config.tableCellConfig ,
        } ,
        {
            title:t("interviewSchedule.status") ,
            field:"status" ,
            minWidth:"150px" ,
            ... Config.tableCellConfig ,
            render:(rowData) => {
                const name = LocalConstants.InterviewScheduleStatus.getNameByValue(rowData.status);
                return name ? name : "";
            } ,
        } ,
        {
            title:t("interviewSchedule.note") ,
            field:"note" ,
            minWidth:"150px" ,
            ... Config.tableCellConfig ,
        } ,
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListInterviewSchedule}
            data={interviewScheduleList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10 , 25 , 50]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
});
