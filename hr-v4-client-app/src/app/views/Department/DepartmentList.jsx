import React from "react";
import { observer } from "mobx-react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { Icon , IconButton } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";

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

export default observer(function DepartmaentList() {
    const {departmentStore} = useStore();
    const {t} = useTranslation();

    const {
        departmentList ,
        totalPages ,
        totalElements ,
        rowsPerPage ,
        page ,
        handleChangePage ,
        setRowsPerPage ,
        handleDelete ,
        handleEditDepartment ,
        handleSelectListDepartment ,
    } = departmentStore;

    const columns = [
        {
            title:t("general.action") ,
            ... Config.tableCellConfig ,
            render:(rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData , method) => {
                        if (method === 0) {
                            handleEditDepartment(rowData?.id);
                        } else if (method === 1) {
                            handleDelete(rowData?.id);
                        } else {
                            alert("Call Selected Here:" + rowData?.id);
                        }
                    }}
                />
            ) ,
            maxWidth:"100px" ,
            align:"center" ,
        } ,
        {
            title:t("department.code") ,
            field:"code" ,
            ... Config.tableCellConfig ,
        } ,
        {
            title:"Tên phòng ban" ,
            field:"name" ,
            ... Config.tableCellConfig ,
        } ,
        {
            title:"Đơn vị trực thuộc" ,
            field:"organization.name" ,
            ... Config.tableCellConfig ,
            render:(data) => data?.organization?.code ,
        } ,
        {
            title:t("department.shortName") ,
            field:"shortName" ,
            ... Config.tableCellConfig ,
            render:(data) => data?.shortName ,
        } ,
        {
            title:t("department.sortNumber") ,
            field:"sortNumber" ,
            width:"10%" ,
            ... Config.tableCellConfig ,
        } ,
        // {
        //   title: t("department.industryBlock"),
        //   field: "industryBlock",
        //   ...Config.tableCellConfig
        // },
        // {
        //   title: t("department.hrdepartmentType"),
        //   field: "hrDepartmentType.name",
        //   ...Config.tableCellConfig
        // },
        {
            title:t("department.description") ,
            field:"description" ,
            ... Config.tableCellConfig ,
        } ,
    ];
    const uniqueData = Array.from(new Map(departmentList.map(item => [item.id , item])).values());

    return (

        <GlobitsTable
            selection
            handleSelectList={handleSelectListDepartment}
            data={uniqueData}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10 , 25 , 50]}
            totalElements={totalElements}
            page={page}
            colParent
        />
    );
});
