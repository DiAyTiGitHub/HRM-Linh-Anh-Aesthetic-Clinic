import {
    Button, ButtonGroup, Grid, makeStyles
} from "@material-ui/core";
import { Add } from "@material-ui/icons";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from 'react-i18next';
import SalaryTemplateItemConfigRow from "./SalaryTemplateItemConfigRow";
import { SalaryTemplateItemConfig } from "app/common/Model/Salary/SalaryTemplateItemConfig";

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

function SalaryTemplateItemThresholds() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = {
            compareOrder: (values?.templateItemConfigs?.length || 0) + 1,
            configType: null,
            itemValue: null,
            formula: null,
            operatorMinValue: null,
            minValue: null,
            operatorMaxValue: null,
            maxValue: null
        };
        push(newItem);
    }

    return (
        <Grid container spacing={2}>
            <Grid item xs={12} className="pb-0">
                <p className="m-0 p-0 borderThrough2">Ngưỡng so sánh</p>
            </Grid>

            <Grid item xs={12}>
                <FieldArray name="templateItemConfigs">
                    {({ insert, remove, push }) => (<>
                        <Grid item xs={12}>
                            <Grid container spacing={1}>
                                <Grid item xs={12} sm={4}>
                                    <ButtonGroup color="container">
                                        <Button onClick={() => handleAddNewRow(push)} fullWidth>
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
                                            <th colSpan={2} align="center" style={{ width: "25%" }}>Giá trị tối thiểu</th>
                                            <th colSpan={2} align="center" style={{ width: "25%" }}>Giá trị tối đa</th>
                                            <th rowSpan={2} align="center" style={{ width: "10%" }}>Loại cấu hình</th>
                                            <th rowSpan={2} align="center" style={{ width: "15%" }}>Giá trị sử dụng</th>
                                            <th rowSpan={2} align="center" style={{ width: "20%" }}>Công thức sử dụng</th>
                                            <th rowSpan={2} align="center" style={{ width: "5%" }}>Thao tác</th>
                                        </tr>
                                        <tr className={classes.tableHeader}>
                                            <th align="center">Toán tử</th>
                                            <th align="center">Giá trị</th>
                                            <th align="center">Toán tử</th>
                                            <th align="center">Giá trị</th>
                                            {/* <th colSpan={4}></th> */}
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {values?.templateItemConfigs?.map((order, index) => (
                                            <SalaryTemplateItemConfigRow
                                                key={index}
                                                index={index}
                                                order={order}
                                                nameSpace={`templateItemConfigs[${index}]`}
                                                remove={() => remove(index)}
                                            />
                                        ))}

                                        {values?.templateItemConfigs?.length === 0 && (
                                            <tr className='row-table-body row-table-no_data'>
                                                <td colSpan={8} align='center' className="py-8">
                                                    Chưa có phần tử nào
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </section>
                        </Grid>
                    </>)}
                </FieldArray>
            </Grid>
        </Grid>
    );
}

export default memo(observer(SalaryTemplateItemThresholds));
