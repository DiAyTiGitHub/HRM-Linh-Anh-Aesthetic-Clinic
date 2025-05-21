import { Button , ButtonGroup , Grid , makeStyles } from "@material-ui/core";
import { Add , Delete } from "@material-ui/icons";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { FieldArray , useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo , useEffect } from "react";
import { useTranslation } from "react-i18next";
import { pagingPosition } from "./PositionService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import { PositionRelationship } from "app/common/Model/HumanResource/PositionRelationship";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { useStore } from "../../stores";

const useStyles = makeStyles(() => ({
    root:{
        background:"#E4f5fc" ,
        padding:"10px 15px" ,
        borderRadius:"5px" ,
    } ,
    groupContainer:{
        width:"100%" ,
        "& .MuiOutlinedInput-root":{
            borderRadius:"0!important" ,
        } ,
    } ,
    tableContainer:{
        marginTop:"16px" ,
        "& table":{
            border:"1px solid #ccc" ,
            borderCollapse:"collapse" ,
            "& td":{
                border:"1px solid #ccc" ,
            } ,
        } ,
    } ,
    tableHeader:{
        width:"100%" ,
        borderBottom:"1px solid #ccc" ,
        marginBottom:"8px" ,
        "& th":{
            // width: "calc(100vw / 4)",
            border:"1px solid #ccc" ,
            padding:"8px 0 8px 4px" ,
        } ,
    } ,
}));

const PositionRelationShipRound = (props) => {
    const {disabled , readOnly} = props;
    const {t} = useTranslation();
    const classes = useStyles();
    const {values , setFieldValue} = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = new PositionRelationship();
        push(newItem);
    }

    const {
        isAdmin ,
        checkAllUserRoles
    } = useStore().hrRoleUtilsStore;
    useEffect(() => {
        checkAllUserRoles();
    } , []);
    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray name='relationships'>
                    {({insert , remove , push}) => (
                        <>
                            <Grid item xs={12}>
                                <Grid container spacing={1} className=''>
                                    <Grid item xs={12} sm={4} md={3}>
                                        {(isAdmin && !readOnly) && (
                                            <ButtonGroup color='container' aria-label='outlined primary button group'>
                                                <Button onClick={() => handleAddNewRow(push)} fullWidth
                                                        disabled={disabled}>
                                                    <Add color='white' className='mr-2 addIcon'/>
                                                    Thêm mối quan hệ
                                                </Button>
                                            </ButtonGroup>
                                        )}
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={12}>
                                <section className={classes.tableContainer}>
                                    <table className={`w-100 ${classes.table}`}>
                                        <thead>
                                        <tr className={classes.tableHeader}>
                                            <th style={{width:(isAdmin && !readOnly) ? "40%" : "50%"}} align='center'>
                                                Mối quan hệ
                                            </th>
                                            <th style={{width:(isAdmin && !readOnly) ? "40%" : "50%"}} align='center'>
                                                Vị trí
                                            </th>
                                            {(isAdmin && !readOnly) && (
                                                <th style={{width:"20%"}} align='center'>
                                                    Thao tác
                                                </th>
                                            )}

                                        </tr>
                                        </thead>
                                        <tbody>
                                        {values?.relationships?.length > 0 ? (
                                            values?.relationships?.map((order , index) => (
                                                <PositionRelationShipRow
                                                    key={index}
                                                    index={index}
                                                    order={order}
                                                    relationships={values?.relationships}
                                                    nameSpace={`relationships[${index}]`}
                                                    remove={() => remove(index)}
                                                    push={() => push(index)}
                                                    disabled={disabled}
                                                    readOnly={readOnly}
                                                />
                                            ))
                                        ) : (
                                            <tr className='row-table-body row-table-no_data'>
                                                <td colSpan={7} align='center' className='py-8'>
                                                    Chưa có mối quan hệ nào
                                                </td>
                                            </tr>
                                        )}
                                        </tbody>
                                    </table>
                                </section>
                            </Grid>
                        </>
                    )}
                </FieldArray>
            </Grid>
        </Grid>
    );
};

const PositionRelationShipRow = memo(({
                                          index ,
                                          relationships ,
                                          order ,
                                          remove ,
                                          push ,
                                          nameSpace ,
                                          disabled ,
                                          readOnly
                                      }) => {
    const {setFieldValue , values} = useFormikContext();
    const {t} = useTranslation();
    const handleTabKeyPress = (event) => {
        if (event.key === "Tab") {
            if (Number(index) === Number(relationships?.length - 1)) {
                push();
            }
        }
    };

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`;
        return nameSpace;
    };

    const handleRelationshipTypeChange = (event) => {
        const value = event.target.value;
        setFieldValue(withNameSpace("relationshipType") , value);

        if (value === 1 || value === 2) {
            setFieldValue(withNameSpace("supervisor") , null);
        } else if (value === 3 || value === 4) {
            setFieldValue(withNameSpace("department") , null);
        }
    };
    const {
        isAdmin ,
    } = useStore().hrRoleUtilsStore;
    return (
        <tr className='row-table-body' key={index}>
            <td>
                <GlobitsSelectInput
                    // label={"Kiểu giá trị"}
                    name={withNameSpace("relationshipType")}
                    options={LocalConstants.PositionRelationshipType.getListData()}
                    disabled={disabled}
                    hideNullOption={true}
                    keyValue='value'
                    handleChange={handleRelationshipTypeChange} // Gọi hàm xử lý sự kiện khi relationshipType thay đổi
                    readOnly={readOnly}
                />
            </td>
            {(order?.relationshipType === 1 || order?.relationshipType === 2) && (
                <td>
                    <GlobitsPagingAutocompleteV2
                        api={pagingAllDepartments}
                        disabled={disabled}
                        name={withNameSpace("department")}
                        // getOptionLabel={(option) => `${option?.name} - ${option?.staff?.displayName || "Vacant"}`}
                        readOnly={readOnly}
                    />
                </td>
            )}
            {(order?.relationshipType === 3 || order?.relationshipType === 4) && (
                <td>
                    <GlobitsPagingAutocompleteV2
                        api={pagingPosition}
                        disabled={disabled}
                        name={withNameSpace("supervisor")}
                        getOptionLabel={(option) => `${option?.name} - ${option?.staff?.displayName || "Vacant"}`}
                        readOnly={readOnly}
                    />
                </td>
            )}
            {!disabled && (isAdmin && !readOnly) && (
                <td align='center'>
                    <span
                        // tooltip={t("Xóa")}
                        className='pointer tooltip text-red'
                        style={{cursor:"pointer"}}
                        onClick={remove}>
                        <Delete className='text-red'/>
                    </span>
                </td>
            )}
        </tr>
    );
});

export default memo(observer(PositionRelationShipRound));
