import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

function PublicHolidayDateCUForm(props) {
    const { publicHolidayDateStore } = useStore();
    const { t } = useTranslation();
    const { readOnly } = props;
    const {
        handleClose,
        savePublicHolidayDate,
        pagingPublicHolidayDate,
        selectedPublicHolidayDate,
        openCreateEditPopup,
        openViewPopup
    } = publicHolidayDateStore;

    const validationSchema = Yup.object({
        holidayDate: Yup.string().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        await savePublicHolidayDate(values);
        await pagingPublicHolidayDate();
    }

    const [initialValues, setInitialValues] = useState(
        selectedPublicHolidayDate
    );

    useEffect(function () {
        setInitialValues({
            ...selectedPublicHolidayDate,
            //holidayType: LocalConstants.SalaryItemType.ADDITION.value
        });
    }, [selectedPublicHolidayDate, selectedPublicHolidayDate?.id]);


    return (
        <GlobitsPopupV2
            size="xs"
            scroll={"body"}
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? ("Xem chi tiết " + t("navigation.publicHolidayDate.title")) : (selectedPublicHolidayDate?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.publicHolidayDate.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}
            >
                {({ isSubmitting, values, setFieldValue, initialValues }) => {

                    return (
                        <Form autoComplete="off">
                            <DialogContent className="o-hidden p-12">
                                <Grid container spacing={2}>

                                    <Grid item xs={12}>
                                        <GlobitsDateTimePicker
                                            required
                                            label={t("Ngày nghỉ")}
                                            name="holidayDate"
                                            readOnly={readOnly}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsSelectInput
                                            label={"Loại ngày nghỉ"}
                                            name='holidayType'
                                            keyValue='value'
                                            options={LocalConstants.HolidayLeaveType.getListData()}
                                            readOnly={readOnly}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsVNDCurrencyInput
                                            label={t("Hệ số tính lương")}
                                            name="salaryCoefficient"
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsCheckBox
                                            label={"Chỉ nghỉ nửa ngày"}
                                            name='isHalfDayOff'
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsVNDCurrencyInput
                                            label={"Số giờ nghỉ"}
                                            name='leaveHours'
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("Mô tả")}
                                            name="description"
                                            multiline
                                            rows={3}
                                            readOnly={readOnly}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            <DialogActions className="dialog-footer px-12">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        variant="contained"
                                        className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                        color="secondary"
                                        disabled={isSubmitting}
                                        onClick={handleClose}
                                    >
                                        {t("general.button.close")}
                                    </Button>
                                    {!readOnly && (
                                        <Button
                                            className="mr-0 btn btn-primary d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.save")}
                                        </Button>
                                    )}
                                </div>
                            </DialogActions>
                        </Form>
                    );
                }
                }
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(PublicHolidayDateCUForm));
