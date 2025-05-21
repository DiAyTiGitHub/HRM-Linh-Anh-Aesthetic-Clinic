import { Button , ButtonGroup , Grid , Icon , IconButton , makeStyles , Tooltip } from "@material-ui/core";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { FieldArray , useFormikContext } from "formik";
import { observer } from "mobx-react";
import React , { memo , useState } from "react";
import { useTranslation } from 'react-i18next';
import AddIcon from "@material-ui/icons/Add";
import { SalaryTemplateItemGroup } from "app/common/Model/Salary/SalaryTemplateItemGroup";
import { useStore } from "app/stores";
import TabTemplateItemGroupRow from "./TabTemplateItemGroupRow";

const useStyles = makeStyles(() => ({
    root:{
        background:"#E4f5fc" , padding:"10px 15px" , borderRadius:"5px" ,
    } , groupContainer:{
        width:"100%" , "& .MuiOutlinedInput-root":{
            borderRadius:"0!important" ,
        } ,
    } , tableContainer:{
        marginTop:"2px" , overflowX:"auto" , overflowY:"hidden" , "& table":{
            border:"1px solid #ccc" , borderCollapse:"collapse" , "& td":{
                border:"1px solid #ccc" ,
            }
        }
    } , tableHeader:{
        width:"100%" , borderBottom:"1px solid #ccc" , marginBottom:"8px" , "& th":{
            border:"1px solid #ccc" , padding:"8px 0 8px 4px"
        } ,
    } ,
}));

function TabTemplateItemGroups() {
    const {hrRoleUtilsStore , salaryTemplateStore} = useStore();
    const {
        isAdmin ,
        isManager ,
        isStaffView
    } = hrRoleUtilsStore;

    const {openViewPopup:readOnly} = salaryTemplateStore;
    const {t} = useTranslation();
    const classes = useStyles();
    const {values , setFieldValue} = useFormikContext();

    function handleAddNewRow(pushAction) {
        const newRow = new SalaryTemplateItemGroup();
        newRow.salaryTemplateId = values?.id;

        pushAction({... newRow});
    }

    return (<Grid container spacing={2}>
        <FieldArray name="templateItemGroups">
            {({insert , remove , push}) => (<>
                <Grid item xs={12}>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} md={4}>
                            {((isAdmin || isManager) && !readOnly) && (
                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Button
                                        startIcon={<AddIcon/>}
                                        type="button"
                                        onClick={() => handleAddNewRow(push)}
                                    >
                                        Thêm nhóm thành phần
                                    </Button>
                                </ButtonGroup>
                            )}
                        </Grid>
                    </Grid>
                </Grid>

                <Grid item xs={12} style={{overflowX:"auto"}}>
                    <section className={classes.tableContainer}>
                        <table className={`${classes.table} w-100`}>
                            <thead>
                            <tr className={classes.tableHeader}>
                                {((isAdmin || isManager) && !readOnly) ? (
                                    <th width="10%">Thao tác</th>
                                ) : (
                                    <th width="10%">STT</th>
                                )}
                                < th width="45%">Tên nhóm thành phần <span className="text-red"> * </span></th>
                                <th width="45%">Mô tả nhóm</th>
                            </tr>
                            </thead>
                            <tbody>
                            {values?.templateItemGroups?.length > 0 ? (values?.templateItemGroups?.map((order , index) => (
                                <TabTemplateItemGroupRow
                                    key={index}
                                    index={index}
                                    templateItemGroups={values?.templateItemGroups}
                                    nameSpace={`templateItemGroups[${index}]`}
                                    remove={() => remove(index)}
                                    push={() => push(index)}
                                    readOnly={readOnly}
                                    //  disabled={!hasEditPermission}
                                />
                            ))) : (
                                <tr className='row-table-body row-table-no_data'>
                                    <td colSpan={10} align='center' className="py-8">Chưa có thông tin</td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </section>
                </Grid>

            </>)}

        </FieldArray>
    </Grid>)
}

export default memo(observer(TabTemplateItemGroups));


