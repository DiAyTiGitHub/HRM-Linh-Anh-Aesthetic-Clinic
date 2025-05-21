import {
    Button,
    ButtonGroup,
    Grid,
    makeStyles,
    Tooltip
} from "@material-ui/core";
import { Add, Delete } from "@material-ui/icons";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from 'react-i18next';
import AddIcon from "@material-ui/icons/Add";
import { SalaryResultItemGroup } from "app/common/Model/Salary/SalaryResultItemGroup";

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
        marginTop: "2px",
        overflowX: "auto",
        overflowY: "hidden",
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
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));

function TabResultItemGroups() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    function handleAddNewRow(pushAction) {
        const newRow = new SalaryResultItemGroup();
        newRow.salaryResultId = values?.id;

        pushAction({ ...newRow });
    }

    return (
        <Grid container spacing={2}>
            <FieldArray name="resultItemGroups">
                {({ insert, remove, push }) => (
                    <>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6} md={4}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AddIcon />}
                                            type="button"
                                            onClick={() => handleAddNewRow(push)}
                                        >
                                            Thêm nhóm thành phần
                                        </Button>
                                    </ButtonGroup>
                                </Grid>
                            </Grid>
                        </Grid>

                        <Grid item xs={12} style={{ overflowX: "auto" }}>
                            <section className={classes.tableContainer}>
                                <table className={`${classes.table} w-100`}>
                                    <thead>
                                        <tr className={classes.tableHeader}>
                                            <th width="10%">Thao tác</th>
                                            <th width="50%">Tên nhóm thành phần <span className="text-red"> * </span> </th>
                                            <th width="40%">Mô tả nhóm <span className="text-red"> * </span> </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {values?.resultItemGroups?.length > 0 ? (
                                            values?.resultItemGroups?.map((order, index) => (
                                                <ResultItemGroup
                                                    key={index}
                                                    index={index}
                                                    nameSpace={`resultItemGroups[${index}]`}
                                                    remove={() => remove(index)}
                                                    push={() => push(index)}
                                                //  disabled={!hasEditPermission}
                                                />
                                            ))
                                        ) : (
                                            <tr className='row-table-body row-table-no_data'>
                                                <td colSpan={10} align='center' className="py-8">Chưa có thông tin</td>
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
    )
}

export default memo(observer(TabResultItemGroups));


const ResultItemGroup = memo(({ index, remove, push, nameSpace, disabled }) => {
    const { setFieldValue, values } = useFormikContext();
    const { t } = useTranslation();

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    const [openConfirmDeletePopup, setOpenConfirmDeletePopup] = useState(false);

    function handleConfirmDeleteItem() {
        // setOpenConfirmDeletePopup(true);
        remove();
    }

    return (
        <>
            <tr className='row-table-body' key={index}>
                <td align='center'>
                    <Tooltip placement="top" title="Xóa">
                        <span
                            className="pointer tooltip"
                            style={{ cursor: 'pointer' }}
                            onClick={() => setOpenConfirmDeletePopup(true)}
                        >
                            <Delete className="text-red font-size-20" />
                        </span>
                    </Tooltip>
                </td>

                <td>
                    <GlobitsTextField name={withNameSpace("name")} />
                </td>

                <td>
                    <GlobitsTextField name={withNameSpace("description")} />
                </td>
            </tr>

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={() => setOpenConfirmDeletePopup(false)}
                    onYesClick={handleConfirmDeleteItem}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa nhóm thành phần này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>
    )
});
