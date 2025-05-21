import {
    Button,
    ButtonGroup,
    Grid,
    makeStyles
} from "@material-ui/core";
import { Add, Delete } from "@material-ui/icons";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from 'app/stores';
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from 'react-i18next';
import { SalaryItemThreshold } from "app/common/Model/Salary/SalaryItemThreshold";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "16px",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            // width: "calc(100vw / 4)",
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));

function SalaryItemThresholdSection(){
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = new SalaryItemThreshold();
        push(newItem);
    }

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray name="thresholds">
                    {({ insert, remove, push }) => (
                        <>
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
                                                Thêm ngưỡng
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
                                                <th align="center" style={{ width: "18%" }}>Thứ tự</th>
                                                <th align="center" style={{ width: "28%" }}>Mức ngưỡng đạt</th>
                                                <th align="center" style={{ width: "48%" }}>Giá trị khi đạt ngưỡng</th>

                                            </tr>
                                        </thead>
                                        <tbody>
                                            {values?.thresholds?.length > 0 ? (
                                                values?.thresholds?.map((order, index) => (
                                                    <ThresholdItem
                                                        key={index}
                                                        index={index}
                                                        order={order}
                                                        thresholds={values?.thresholds}
                                                        nameSpace={`thresholds[${index}]`}
                                                        remove={() => remove(index)}
                                                        push={() => push(index)}
                                                    //  disabled={!hasEditPermission}
                                                    />
                                                ))
                                            ) : (
                                                <tr className='row-table-body row-table-no_data'>
                                                    <td colSpan={4} align='center' className="py-8">Chưa có vòng tuyển dụng nào</td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </table>
                                </section>
                            </Grid>

                        </>
                    )}
                </FieldArray>
                {/* </Form>
                    )}
                </Formik> */}
            </Grid>
        </Grid>
    )
}

const ThresholdItem = memo(({ index, thresholds, order, remove, push, nameSpace, disabled }) => {
    const { setFieldValue, values } = useFormikContext();
    const { t } = useTranslation();
    const handleTabKeyPress = (event) => {
        if (event.key === 'Tab') {
            if ((Number(index) === Number(thresholds?.length - 1))) {
                push();
            }
        }
    };

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    return (
        <tr className='row-table-body' key={index}>
            <td>
                <GlobitsNumberInput
                    name={withNameSpace("displayOrder")}
                />
            </td>
            <td>
                <GlobitsTextField name={withNameSpace("thresholdValue")} />
            </td>
            <td>
                <GlobitsTextField name={withNameSpace("inUseValue")} />
            </td>
            {!disabled &&
                <td align='center'>
                    <span
                        // tooltip={t("Xóa")}
                        className="pointer tooltip text-red"
                        style={{ cursor: 'pointer' }}
                        onClick={remove}
                    >
                        <Delete
                            className="text-red"
                        />
                    </span>
                </td>
            }

        </tr>
    )
})

export default memo(observer(SalaryItemThresholdSection));
