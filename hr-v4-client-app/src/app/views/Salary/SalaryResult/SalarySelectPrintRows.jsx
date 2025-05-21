import { Button, DialogActions, DialogContent, Grid, FormControlLabel, Checkbox } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik, Field } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useReactToPrint } from "react-to-print";
import SalaryResultPrint from "../Print/SalaryResultPrint";
import { getListTemplateItem } from "./SalaryResultService";
import { toast } from "react-toastify";

function SalarySelectPrintRows({ selectedRow, setSelectedRow }) {
    const { salaryResultStore } = useStore();
    const { t } = useTranslation();

    const [printData, setPrintData] = useState(null);

    const {
        openFormSelectRows,
        setOpenFormSelectRows,
        handleClose,
        getSalaryResultBoard,
        selectedColumns,
        handleSelectedColumns,
        allColumns,
        setAllColumns,
    } = salaryResultStore;

    const componentRef = useRef(null);

    useEffect(() => {
        if (selectedRow?.id) {
            getListTemplateItem(selectedRow?.id)
                .then((result) => {
                    console.log(result?.data);
                    setAllColumns(result?.data);
                    handleSelectedColumns(result?.data.map((col) => col.code));
                })
                .catch((err) => {
                    toast.error("Có lỗi xảy ra");
                });
        }
    }, [selectedRow, handleSelectedColumns]);

    const handlePrint = useReactToPrint({
        content: () => componentRef.current,
        documentTitle: "Phiếu lương",
        onAfterPrint: () => setPrintData(null),
    });

    async function handleSaveForm(values) {
        console.log("Selected columns:", values.selectedColumns);
        handleSelectedColumns(values.selectedColumns);

        if (selectedRow?.id) {
            try {
                const result = await getSalaryResultBoard(selectedRow.id);
                setPrintData(result);
                handlePrint();
                handleClose();
            } catch (error) {
                console.error("Error fetching salary data:", error);
            }
        }
    }

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='lg'
            open={openFormSelectRows}
            noDialogContent
            title={"Các thành phần trong phiếu in"}
            onClosePopup={handleClose}>
            <Formik enableReinitialize initialValues={{ selectedColumns: selectedColumns }} onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='p-12'>
                                <FormikFocusError />
                                {/* Nút Select All và Unselect All */}
                                <div className='mb-8'>
                                    <Button
                                        variant='outlined'
                                        className='mr-8'
                                        onClick={() =>
                                            setFieldValue(
                                                "selectedColumns",
                                                allColumns.map((col) => col.code)
                                            )
                                        }>
                                        {t("Chọn hết")}
                                    </Button>
                                    <Button variant='outlined' onClick={() => setFieldValue("selectedColumns", [])}>
                                        {t("Bỏ chọn")}
                                    </Button>
                                </div>
                                <Grid container spacing={2}>
                                    {allColumns.map((column) => (
                                        <Grid item xs={3} key={column.code}>
                                            <FormControlLabel
                                                control={
                                                    <Field
                                                        type='checkbox'
                                                        name='selectedColumns'
                                                        value={column.code}
                                                        as={Checkbox}
                                                    />
                                                }
                                                label={column.displayName}
                                            />
                                        </Grid>
                                    ))}
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer py-8'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        disabled={isSubmitting}
                                        color='secondary'
                                        onClick={() => handleClose()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon />}
                                        className='mr-0 btn btn-success d-inline-flex'
                                        variant='contained'
                                        type='submit'
                                        disabled={isSubmitting}>
                                        {t("In")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>

                        <SalaryResultPrint
                            printData={printData}
                            componentRef={componentRef}
                            selectedColumns={values.selectedColumns}
                        />
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SalarySelectPrintRows));
