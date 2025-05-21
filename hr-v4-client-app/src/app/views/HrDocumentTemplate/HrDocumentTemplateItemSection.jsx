import {Button, ButtonGroup, Grid, makeStyles} from "@material-ui/core";
import {Add, Delete} from "@material-ui/icons";
import {FieldArray, useFormikContext} from "formik";
import {observer} from "mobx-react";
import React, {memo} from "react";
import {useTranslation} from 'react-i18next';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import {HrDocumentItemRequired} from "app/LocalConstants";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc", padding: "10px 15px", borderRadius: "5px",
    }, groupContainer: {
        width: "100%", "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    }, tableContainer: {
        marginTop: "16px", "& table": {
            border: "1px solid #ccc", borderCollapse: "collapse", "& td": {
                border: "1px solid #ccc",
            }
        }
    }, tableHeader: {
        width: "100%", borderBottom: "1px solid #ccc", marginBottom: "8px", "& th": {
            // width: "calc(100vw / 4)",
            border: "1px solid #ccc", padding: "8px 0 8px 4px"
        },
    },
}));

function HrDocumentTemplateItemSection() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values, setFieldValue} = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = {
            name: "",
            code: "",
            description: "",
            displayOrder: null,
            isRequired: false
        };
        push(newItem);
    }

    return (<Grid container spacing={2}>
        <Grid item xs={12}>
            <FieldArray name="documentItems">
                {({insert, remove, push}) => (<>
                    <Grid item xs={12}>
                        <Grid container spacing={1} className=''>
                            <Grid item xs={12} sm={4}>
                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Button
                                        onClick={() => handleAddNewRow(push)}
                                        fullWidth
                                        //  disabled={!hasEditPermission}
                                    >
                                        <Add color="white" className='mr-2 addIcon'/>
                                        {t("Thêm mới tài liệu")}
                                    </Button>
                                </ButtonGroup>
                            </Grid>
                        </Grid>
                    </Grid>

                    <Grid item xs={12}>
                        <section className={classes.tableContainer}>
                            <table className={`w-100 ${classes.table}`}>
                                <thead>
                                <tr className={classes.tableHeader}>
                                    <th align="center" style={{width: "5%"}}>{t("STT")}</th>
                                    <th align="center" style={{width: "20%"}}>{t("Tên tài liệu")}</th>
                                    <th align="center" style={{width: "15%"}}>{t("Mã tài liệu")}</th>
                                    <th align="center" style={{width: "20%"}}>{t("Mô tả tài liệu")}</th>
                                    <th align="center" style={{width: "15%"}}>{t("Thứ tự hiển thị")}</th>
                                    <th align="center" style={{width: "12%"}}>{t("Cần phải nộp")}</th>
                                    <th align="center" style={{width: "10%"}}>{t("Hành động")}</th>
                                </tr>
                                </thead>
                                <tbody>
                                {values?.documentItems?.length > 0 ? (values?.documentItems?.map((order, index) => (
                                    <HrDocumentItem
                                        key={index}
                                        index={index}
                                        order={order}
                                        hrDocumentItems={values?.documentItems}
                                        nameSpace={`documentItems[${index}]`}
                                        remove={() => remove(index)}
                                        push={() => push(index)}
                                        //  disabled={!hasEditPermission}
                                    />))) : (<tr className='row-table-body row-table-no_data'>
                                    <td colSpan={4} align='center' className="py-8">Chưa có phần tử nào
                                    </td>
                                </tr>)}
                                </tbody>
                            </table>
                        </section>
                    </Grid>

                </>)}
            </FieldArray>
            {/* </Form>
                    )}
                </Formik> */}
        </Grid>
    </Grid>)
}

const HrDocumentItem = memo((props) => {
    const {
        index, hrDocumentItems, order, remove, push, nameSpace, disabled
    } = props;

    const {
        setFieldValue, values

    } = useFormikContext();

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    return (<tr className='row-table-body' key={index}>
        <td style={{textAlign: "center"}}>
            {index + 1}
        </td>
        <td>
            <GlobitsTextField name={withNameSpace("name")}/>
        </td>
        <td>
            <GlobitsTextField name={withNameSpace("code")}/>
        </td>

        <td>
            <GlobitsTextField name={withNameSpace("description")}/>
        </td>

        <td>
            <GlobitsNumberInput name={withNameSpace("displayOrder")}/>
        </td>

        <td>
            <GlobitsSelectInput
                hideNullOption
                name={withNameSpace("isRequired")}
                keyValue="value"
                displayvalue="name"
                options={HrDocumentItemRequired}
            />

        </td>
        {!disabled && <td align='center'>
                    <span
                        className="pointer tooltip text-red"
                        style={{cursor: 'pointer'}}
                        onClick={remove}
                    >
                        <Delete
                            className="text-red"
                        />
                    </span>
        </td>}

    </tr>)
})

export default memo(observer(HrDocumentTemplateItemSection));
