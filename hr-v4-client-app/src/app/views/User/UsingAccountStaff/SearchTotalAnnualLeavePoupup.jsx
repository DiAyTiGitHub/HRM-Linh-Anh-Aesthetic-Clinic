import React, { useCallback, useEffect, useState } from "react";
import { observer } from "mobx-react";
import { Formik, Form } from "formik";
import { Grid, Button, Typography, DialogContent } from "@material-ui/core";
import GlobitsSelectInputV2 from "app/common/form/GlobitsSelectInputV2";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { LIST_YEAR } from "app/LocalConstants";
import { useStore } from "app/stores";

const SearchTotalAnnualLeavePopup = observer((props) => {
    const { open, handleClose } = props;
    const { staffStore } = useStore();
    const { remainingLeave, fetchRemainingLeave, selectedStaff } = staffStore;

    const currentYear = new Date().getFullYear();
    const [year, setYear] = useState(currentYear);

    useEffect(() => {
        if (open && selectedStaff?.id) {
            fetchRemainingLeave(selectedStaff.id, year);
        }
    }, [open, year, selectedStaff?.id, fetchRemainingLeave]);

    const handleSubmit = useCallback(
        (values) => {
            setYear(values.year);
            fetchRemainingLeave(selectedStaff?.id, values.year);
        },
        [fetchRemainingLeave, selectedStaff?.id]
    );

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId='annual-leave-popup'
            open={open}
            title='Số ngày nghỉ phép năm'
            size='xs'
            scroll='body'
            onClosePopup={handleClose}>
            <Formik initialValues={{ year }} enableReinitialize onSubmit={handleSubmit}>
                {({ setFieldValue }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <Grid container alignItems='center' spacing={2}>
                                            <Grid item xs={8}>
                                                <GlobitsSelectInputV2
                                                    name='year'
                                                    options={LIST_YEAR}
                                                    handleChange={(_, value) => setFieldValue("year", value)}
                                                />
                                            </Grid>
                                            <Grid item xs={4} style={{ display: "flex", justifyContent: "end" }}>
                                                <Button type='submit' variant='contained' color='primary' fullWidth>
                                                    Xem
                                                </Button>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Typography variant='h6' align='center'>
                                                    Số ngày nghỉ còn lại:{" "}
                                                    <strong>{remainingLeave?.annualLeaveDays || 0}</strong>
                                                </Typography>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
});

export default SearchTotalAnnualLeavePopup;
