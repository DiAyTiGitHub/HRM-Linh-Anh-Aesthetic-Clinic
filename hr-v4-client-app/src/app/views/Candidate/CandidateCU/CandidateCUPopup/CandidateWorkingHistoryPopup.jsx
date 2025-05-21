import React, { memo, useEffect, useState } from "react";
import { Formik, Form } from "formik";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import moment from "moment";
import { Grid, DialogActions, Button, DialogContent, } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

import { pagingCountry } from "app/views/Country/CountryService";
import { pagingSpecialities } from "app/views/Speciality/SpecialityService";
import { pagingEducationTypes } from "app/views/EducationType/EducationTypeService";
import { pagingEducationDegrees } from "app/views/EducationDegree/EducationDegreeService";
import { pagingTrainingBases } from "app/views/TrainingBase/TrainingBaseService";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import { EducationalHistory } from "app/common/Model/EducationalHistory";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { pagingPosition } from "app/views/Position/PositionService";

function CandidateWorkingHistoryPopup(props) {
    const { t } = useTranslation();
    const { open, handleClose, item, handleSubmit, editable } = props;

    const validationSchema = Yup.object({
        startDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable(),

        endDate: Yup.date()
            .test(
                "is-greater",
                "Ngày kết thúc phải lớn ngày bắt đầu",
                function (value) {
                    const { startDate } = this.parent;
                    if (startDate && value) {
                        return moment(value).isAfter(moment(startDate), "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày kết thúc không đúng định dạng")
            .nullable(),
    });

    const [formValues, setFormValues] = useState(null);

    useEffect(() => {
        if (item) {
            setFormValues({ ...item });
        } else {
            setFormValues(new EducationalHistory());
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [item]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            open={open}
            onClosePopup={handleClose}
            title={
                <span>
                    Chi tiết Kinh nghiệm làm việc
                </span>
            }
            size="md"
            noDialogContent
        >
            <Formik
                initialValues={formValues}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({ isSubmitting }) => (
                    <Form autoComplete="off">
                        <DialogContent
                            className="dialog-body p-12"

                        // style={{ maxHeight: "80vh", minWidth: "300px" }}
                        >
                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6}>
                                    <GlobitsDateTimePicker
                                        label="Làm việc từ ngày"
                                        name={("fromDate")}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsDateTimePicker
                                        label="Làm việc đến ngày"
                                        name={("toDate")}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsTextField
                                        label="Tên công ty"
                                        name={("companyName")}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsTextField
                                        label="Phòng ban"
                                        name={("departmentName")}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsPagingAutocomplete
                                        label="Vị trí"
                                        name={("position")}
                                        api={pagingPosition}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsVNDCurrencyInput
                                        label="Mức lương"
                                        name={("salary")}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        label="Mô tả công việc"
                                        multiline
                                        rows={4}
                                        name={("description")}
                                    />
                                </Grid>

                                <Grid item xs={12} >
                                    <GlobitsTextField
                                        label="Lý do nghỉ việc"
                                        multiline
                                        rows={3}
                                        name={("leavingReason")}
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer p-12">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    variant="contained"
                                    className="mr-12 btn btn-secondary d-inline-flex"
                                    color="secondary"
                                    onClick={() => {
                                        handleClose();
                                    }}
                                >
                                    {t("general.button.close")}
                                </Button>
                                <Button
                                    className="mr-0 btn btn-primary d-inline-flex"
                                    variant="contained"
                                    color="primary"
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    )
}

export default memo(observer(CandidateWorkingHistoryPopup));
