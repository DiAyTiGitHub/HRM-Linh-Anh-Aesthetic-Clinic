import React from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon , IconButton } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import LocalConstants from "app/LocalConstants";

function MaterialButton(props) {
    const {item} = props;
    return (
        <div>
            <IconButton size='small' onClick={() => props.onSelect(item , 0)}>
                <Icon fontSize='small' color='primary'>
                    edit
                </Icon>
            </IconButton>
            <IconButton size='small' onClick={() => props.onSelect(item , 1)}>
                <Icon fontSize='small' color='error'>
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

export default observer(function StaffDisciplineHistoryList() {
    const {staffLeaveStore} = useStore();
    const {t} = useTranslation();

    const {
        staffLeaveList ,
        handleDelete ,
        handleEdit ,
        handleSelectListStaffLeave
    } = staffLeaveStore;

    let columns = [
        {
            title:t("general.action") ,
            minWidth:"100px" ,
            cellStyle:{textAlign:"center"} ,
            headerStyle:{textAlign:"center"} ,
            render:(rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData , method) => {
                        if (method === 0) {
                            handleEdit(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert(t("general.alert.callSelected") + rowData.id); // Dịch thông báo
                        }
                    }}
                />
            ) ,
        } ,
        {
            title:t("staffLeave.decisionNumber") ,
            minWidth:"150px" ,
            field:"decisionNumber"
        } ,
        {
            title:t("staffLeave.leaveDate") ,
            field:"leaveDate" ,
            minWidth:"150px" ,
            render:(value) => value?.leaveDate && formatDate("DD/MM/YYYY" , value?.leaveDate) ,
        } ,
        {
            title:t("staffLeave.stillInDebt") ,
            minWidth:"150px" ,
            field:"stillInDebt"
        } ,
        {
            title:t("staffLeave.paidStatus") ,
            field:"paidStatus" ,
            minWidth:"150px" ,
            render:(value) => LocalConstants.PaidStatusOfLeaveStaff.getListData().find(item => item.value === value?.paidStatus)?.name || "" ,
        }
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListStaffLeave}
            data={staffLeaveList}
            columns={columns}
            nonePagination
        />
    );
});
