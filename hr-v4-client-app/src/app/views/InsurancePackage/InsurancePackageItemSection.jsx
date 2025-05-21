import { Button, ButtonGroup, Grid, makeStyles } from "@material-ui/core";
import { Add, Delete } from "@material-ui/icons";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from 'react-i18next';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import { HrDocumentItemRequired } from "app/LocalConstants";

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

function InsurancePackageItemSection() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = {
            name: "",
            description: "",
            displayOrder: null
        };
        push(newItem);
    }

    return (<Grid container spacing={2}>
        <Grid item xs={12}>
            <FieldArray name="packageItems">
                {({ insert, remove, push }) => (<>
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
                                        <Add color="white" className='mr-2 addIcon' />
                                        {t("Thêm mới hạng mục")}
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
                                        <th align="center" style={{ width: "5%" }}>{t("STT")}</th>
                                        <th align="center" style={{ width: "30%" }}>{t("Tên hạng mục trong gói hợp đồng")}</th>
                                        <th align="center" style={{ width: "50%" }}>{t("Mô tả hạng mục")}</th>
                                        <th align="center" style={{ width: "10%" }}>{t("Thứ tự hiển thị")}</th>
                                        <th align="center" style={{ width: "5%" }}>{t("Hành động")}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {values?.packageItems?.length > 0 ? (values?.packageItems?.map((order, index) => (
                                        <InsurancePackageItem
                                            key={index}
                                            index={index}
                                            order={order}
                                            insurancePackageItems={values?.packageItems}
                                            nameSpace={`packageItems[${index}]`}
                                            remove={() => remove(index)}
                                            push={() => push(index)}
                                        //  disabled={!hasEditPermission}
                                        />))) : (<tr className='row-table-body row-table-no_data'>
                                            <td colSpan={4} align='center' className="py-8">Chưa có hạng mục nào
                                            </td>
                                        </tr>)}
                                </tbody>
                            </table>
                        </section>
                    </Grid>

                </>)}
            </FieldArray>
        </Grid>
    </Grid>)
}

const InsurancePackageItem = memo((props) => {
    const {
        index, insurancePackageItems, order, remove, push, nameSpace, disabled
    } = props;

    const {
        setFieldValue, values

    } = useFormikContext();

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    return (<tr className='row-table-body' key={index}>
        <td style={{ textAlign: "center" }}>
            {index + 1}
        </td>
        <td>
            <GlobitsTextField name={withNameSpace("name")} />
        </td>

        <td>
            <GlobitsTextField name={withNameSpace("description")} />
        </td>

        <td>
            <GlobitsNumberInput name={withNameSpace("displayOrder")} />
        </td>

        {!disabled && <td align='center'>
            <span
                className="pointer tooltip text-red"
                style={{ cursor: 'pointer' }}
                onClick={remove}
            >
                <Delete
                    className="text-red"
                />
            </span>
        </td>}

    </tr>)
})

export default memo(observer(InsurancePackageItemSection));
