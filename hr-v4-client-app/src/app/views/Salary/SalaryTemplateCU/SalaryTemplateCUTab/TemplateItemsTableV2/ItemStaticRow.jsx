import { observer } from "mobx-react";
import React , { memo , useState } from "react";
import { useTranslation } from "react-i18next";
import { useSortable } from "@dnd-kit/sortable";
import { Tooltip , makeStyles } from "@material-ui/core";
import DragIndicatorIcon from '@material-ui/icons/DragIndicator';
import { useFormikContext } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import EditIcon from '@material-ui/icons/Edit';
import { Delete } from "@material-ui/icons";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import LocalConstants from "app/LocalConstants";
import { CSS } from "@dnd-kit/utilities";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

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
        marginTop:"2px" ,
        overflowX:"auto" ,
        overflowY:"hidden" ,
        "& table":{
            border:"1px solid #ccc" ,
            borderCollapse:"collapse" ,
            "& td":{
                border:"1px solid #ccc" ,
            }
        }
    } ,
    tableHeader:{
        width:"100%" ,
        borderBottom:"1px solid #ccc" ,
        marginBottom:"8px" ,
        "& th":{
            border:"1px solid #ccc" ,
            padding:"8px 0 8px 4px"
        } ,
    } ,
}));


function ItemStaticRow(props) {
    const {t} = useTranslation();
    const classes = useStyles();

    const {
        index ,
        remove ,
        nameSpace ,
        data ,
        readOnly
    } = props;

    const {
        attributes ,
        listeners ,
        transform ,
        transition ,
        setNodeRef ,
        isDragging
    } = useSortable({
        id:data?.id
    });

    const style = {
        transform:CSS.Transform.toString(transform) ,
        transition:transition
    };

    const {setFieldValue , values} = useFormikContext();
    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    return (
        <>
            <table className={`${classes.table} w-100`}>
                <thead>
                <tr
                    {... attributes} {... listeners}
                    className='row-table-body bg-white'
                    key={index}
                    style={style}
                    ref={setNodeRef}
                >
                    {!readOnly && (
                        <th width="128px">
                            <div className='w-100 one_row_display_cell aligns-center justify-center px-6'>
                                <span

                                    className="pr-8 pointer tooltip"
                                    style={{cursor:'grabbing'}}
                                >
                                    <DragIndicatorIcon className="text-green font-size-20"/>
                                </span>

                                {/* <span
                                    className="pr-8 pointer tooltip"
                                    style={{ cursor: 'pointer' }}
                                >
                                    <EditIcon className="text-primary font-size-20" />
                                </span> */}

                                <span
                                    className="pointer tooltip"
                                    style={{cursor:'pointer'}}
                                >
                                    <Delete className="text-red font-size-20"/>
                                </span>
                            </div>
                        </th>
                    )}

                    <th>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.salaryItem?.name || ""}
                            </span>
                    </th>

                    <th>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.salaryItem?.code || ""}
                            </span>
                    </th>

                    <th style={{minWidth:"320px"}}>
                        {
                            (data?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value
                                || data?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value
                            )
                            && (
                                <GlobitsTextField
                                    value={data?.formula}
                                    name={withNameSpace("formula")}
                                    isTextArea
                                    multiline
                                    minRows={1}
                                    readOnly={readOnly}
                                />
                            )}
                    </th>

                    {/* <th>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {LocalConstants.SalaryItemType.getListData().find(i => Number(i.value) == Number(data?.salaryItem?.type))?.name || ""}
                            </span>
                        </th> */}

                    <th>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {LocalConstants.SalaryItemCalculationType.getListData().find(i => Number(i.value) == Number(data?.calculationType))?.name || ""}
                            </span>
                    </th>

                    <td>
                            <span
                                className="one_row_display_cell"
                            >
                                {LocalConstants.SalaryItemValueType.getListData().find(i => Number(i.value) == Number(data?.valueType))?.name || ""}
                            </span>
                    </td>

                    <th style={{minWidth:"280px"}}>
                        <GlobitsTextField
                            value={data?.displayName}
                            name={withNameSpace("displayName")}
                            readOnly={readOnly}
                        />
                    </th>

                    <th style={{minWidth:"240px"}}>
                        <GlobitsSelectInput
                            name={withNameSpace("templateItemGroupId")}
                            value={data?.templateItemGroupId}
                            keyValue="id"
                            options={values?.templateItemGroups}
                            readOnly={readOnly}
                        />
                    </th>

                    <td>
                        <GlobitsCheckBox
                            name={withNameSpace("hiddenOnSalaryBoard")}
                            checked={data?.hiddenOnSalaryBoard}
                            readOnly={readOnly}
                        />
                    </td>

                    <td>
                        <GlobitsCheckBox
                            name={withNameSpace("hiddenOnPayslip")}
                            checked={data?.hiddenOnPayslip}
                            readOnly={readOnly}
                        />
                    </td>


                </tr>
                </thead>
            </table>
        </>
    );
}

export default memo(observer(ItemStaticRow));