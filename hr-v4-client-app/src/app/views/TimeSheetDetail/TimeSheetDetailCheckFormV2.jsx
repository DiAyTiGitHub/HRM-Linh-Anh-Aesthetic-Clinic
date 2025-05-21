import React, { memo, useEffect, useMemo, useState } from "react";
import { Formik, Form } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { Grid, DialogActions, Button, DialogContent, Paper, TableHead, TableCell, TableRow, TableContainer, TableBody, Table, Radio } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import localStorageService from "app/services/localStorageService";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import FormikFocusError from "app/common/FormikFocusError";
import * as Yup from "yup";
import { formatDate } from "app/LocalFunction";
import AlarmOnIcon from '@material-ui/icons/AlarmOn';
import { getStaff } from "../HumanResourcesInformation/StaffService";
import LocalConstants from "app/LocalConstants";
import TimesheetCheckFormChoosePeriodSection from "./TimesheetCheckFormChoosePeriodSection";
import TimesheetChooseScheduleSectionV2 from "./TimesheetChooseScheduleSectionV2";
import { transformDateDatePicker } from "app/common/CommonFunctions";

function TimeSheetDetailCheckFormV2(props) {
    const { t } = useTranslation();

    const {
        staffId,
        handleSumbit
    } = props;

    const {
        timeSheetDetailStore, hrRoleUtilsStore
    } = useStore();

    const {
        openFormTimeSheetDetailCheck,
        handleClose,
        selectedStaff,
        currentTimekeeping
    } = timeSheetDetailStore;

    const { isAdmin, isManager } = hrRoleUtilsStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        workingDate: Yup.date()
            .transform(transformDateDatePicker)
            .required(t("validation.required")).nullable(),
        typeTimeSheetDetail: Yup.number().required(t("validation.required")).nullable(),
    });

    const [initialValues, setInitialValues] = useState(currentTimekeeping);

    const loggedStaff = localStorageService.getLoginUser();

    useEffect(function () {
        setInitialValues({
            ...currentTimekeeping,
        });
    }, [currentTimekeeping?.staffId]);

    useEffect(() => {
        const fetchData = async () => {
            if (staffId) {
                const response = await getStaff(staffId);

                setInitialValues({
                    ...initialValues,
                    staff: response?.data
                })
            }
        }

        fetchData();
    }, [staffId])

    return (
        <GlobitsPopupV2
            size='md'
            open={openFormTimeSheetDetailCheck}
            onClosePopup={handleClose}
            noDialogContent
            title={"Chấm công"}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSumbit}
            >
                {({ values, setFieldValue, submitForm }) => {

                    function handleChangeWorkingDate(value) {
                        setFieldValue("workingDate", value);
                        setFieldValue("staffWorkSchedule", null);
                        setFieldValue("currentTime", null);
                    }

                    return (
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        {(isAdmin || isManager) &&
                                            <Grid item xs={12} sm={4}>
                                                <ChooseUsingStaffSection
                                                    required
                                                    label={"Nhân viên chấm công"}
                                                />
                                            </Grid>
                                        }

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsDateTimePicker
                                                required
                                                name="workingDate"
                                                label="Ngày làm việc"
                                                disabled={!isAdmin && !isManager}
                                                onChange={handleChangeWorkingDate}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsSelectInput
                                                required
                                                label={"Loại chấm công"}
                                                name="typeTimeSheetDetail"
                                                hideNullOption
                                                options={LocalConstants.TimesheetDetailType.getListData()}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsDateTimePicker
                                                required
                                                name="currentTime"
                                                label="Thời điểm chấm công"
                                                isDateTimePicker
                                                disabled={!isAdmin && !isManager}
                                            />
                                        </Grid>

                                        <TimesheetChooseScheduleSectionV2 />

                                        <TimesheetCheckFormChoosePeriodSection />


                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className="dialog-footer dialog-footer-v2 py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant="contained"
                                            className="mr-12 btn btn-secondary d-inline-flex"
                                            color="secondary"
                                            onClick={handleClose}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>

                                        <Button
                                            startIcon={<AlarmOnIcon />}
                                            className="mr-0 btn btn-primary d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                        >
                                            Chấm công
                                        </Button>
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(TimeSheetDetailCheckFormV2));


