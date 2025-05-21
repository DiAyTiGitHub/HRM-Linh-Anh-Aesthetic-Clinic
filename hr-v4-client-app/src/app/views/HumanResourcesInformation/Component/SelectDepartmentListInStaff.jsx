import React , { useCallback , useEffect , useRef , useState } from "react";
import { observer } from "mobx-react";
import { useStore } from "../../../stores";
import { useTranslation } from "react-i18next";
import { Radio } from "@material-ui/core";
import { useFormikContext } from "formik";
import GlobitsTable from "../../../common/GlobitsTable";

function SelectDepartmentListInStaff(props) {
    const {handleClose , organizationId , name = "department" , clearFields = []} = props;
    const {departmentStore , departmentV2Store} = useStore();
    const {t} = useTranslation();
    const {
        listDepartment ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        pagingAllDepartment ,
        handleSetSearchObject
    } = departmentV2Store;
    const {setFieldValue , values} = useFormikContext();
    const tableRef = useRef(null);
    const [expandedNodes , setExpandedNodes] = useState(new Set());

    useEffect(() => {
        const initializeSearchObject = {
            pageIndex:1 ,
            pageSize:10 ,
            organization:{id:organizationId} ,
        };
        handleSetSearchObject(initializeSearchObject)
        pagingAllDepartment();

        return () => {

        }
    } , [organizationId]);

    useEffect(() => {
        if (values?.[name]?.id) {
            const selectedDept = listDepartment.find((dept) => dept.id === values[name].id);
            if (selectedDept?.parentId) setExpandedNodes(new Set([selectedDept.parentId]));
        }
    } , [values[name]?.id , listDepartment]);

    useEffect(() => {
        if (tableRef.current) {
            tableRef.current.dataManager.treefiedData.forEach(node => node.tableData.isTreeExpanded = expandedNodes.has(node.id));
            tableRef.current.setState({... tableRef.current.dataManager.getRenderState()});
        }
    } , [expandedNodes]);

    const handleSelectItem = useCallback((_ , department) => {
        clearFields.forEach(field => values[field] !== undefined && setFieldValue(field , null));
        setFieldValue(name , values?.[name]?.id === department?.id ? null : department);
        handleClose?.();
    } , [clearFields , name , values , setFieldValue , handleClose]);

    const columns = [
        {
            title:t("general.popup.select") ,
            minWidth:"80px" ,
            render:(rowData) => (
                <Radio
                    id={`radio${rowData?.id}`}
                    name="radSelected"
                    value={rowData?.id}
                    checked={values?.[name]?.id === rowData?.id}
                    onClick={(event) => handleSelectItem(event , rowData)}
                />
            )
        } ,
        {
            title:t("department.code") ,
            field:"code" ,
            minWidth:"200px" ,
        } ,
        {
            title:"Tên phòng ban" ,
            field:"name" ,
            minWidth:"200px" ,
        } ,
        {
            title:"Đơn vị trực thuộc" ,
            field:"organization.name" ,
            minWidth:"200px" ,
            render:(data) => (
                <span className='px-4'>{`${data?.organization?.name || ""} - ${data?.organization?.code || ""}`}</span>
            ) ,
        } ,
        {
            title:t("department.shortName") ,
            field:"shortName" ,
            minWidth:"200px" ,
            render:(data) => data?.shortName ,
        } ,
        {
            title:t("department.description") ,
            field:"description" ,
            minWidth:"200px" ,
        } ,
    ];
    const uniqueData = Array.from(new Map(listDepartment.map((item) => [item.id , item])).values());

    return (
        <GlobitsTable
            data={uniqueData}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[5 , 10 , 25 , 50 , 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
            colParent
            defaultExpanded={!!(searchObject?.keyword || searchObject?.organization)}
        />
    );
}

export default observer(SelectDepartmentListInStaff);