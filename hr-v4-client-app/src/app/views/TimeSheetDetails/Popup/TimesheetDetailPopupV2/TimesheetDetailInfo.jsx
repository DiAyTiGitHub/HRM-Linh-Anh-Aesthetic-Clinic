import React, { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { toast } from "react-toastify";
import { Formik, Form, } from "formik";
import * as Yup from "yup";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import ListIcon from "@material-ui/icons/List";
import { pagingProject } from "app/views/Project/ProjectService";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsTimePicker from "app/common/form/GlobitsTimePicker";
import FormikFocusError from "app/common/FormikFocusError";
import LocalConstants from "app/LocalConstants";
import { formatDate, transformDate } from "app/LocalFunction";
import { pagingTimeSheet } from "app/views/TimeSheet/TimeSheetService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import moment from "moment";
import ChoosePAPopup from "../ChoosePAPopup";
import { DialogActions, DialogContent, Grid, Button, Tooltip } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import GlobitsEditor from "app/common/form/GlobitsEditor";
import TouchAppIcon from '@material-ui/icons/TouchApp';

function TimesheetDetailInfo() {
    const { t } = useTranslation();
    const classes = useStyles();

    const { timeSheetDetailsStore } = useStore();

    const {
        handleClosePopup,
        handleSubmitFormTimeSheet,
        dataEditTimeSheet,
        openFormTimeSheet,
        currentStaff,
    } = timeSheetDetailsStore;

    const [isOpenListActivity, setIsOpenListActivity] = useState(false);
    const openPopupPA = (project) => {
        if (!project) {
            toast.warning("Bạn chưa chọn dự án");
        } else {
            setIsOpenListActivity(true);
        }
    };

    const validationSchema = Yup.object({
        // project: Yup.object().required(t("validation.required")).nullable(),
        // workingStatus: Yup.object().required(t("validation.required")).nullable(),
        employee: Yup.object().required('Cần có nhân viên!').nullable(),
        startTime: Yup.date().transform(function transformDate(castValue, originalValue) {         return originalValue ? new Date(originalValue) : castValue;       }).required(t("validation.required")).transform(transformDate).test('test', t("Thời gian kết thúc phải sau thời gian bắt đầu"), function () {
            const { startTime, endTime } = this.parent;
            if (startTime && endTime)
                if (moment(endTime).isBefore(moment(startTime))) {
                    return false;
                }
            return true;
        }).nullable(),
        endTime: Yup.date().transform(function transformDate(castValue, originalValue) {         return originalValue ? new Date(originalValue) : castValue;       }).required(t("validation.required")).nullable(),
    });


    //open and close project activity
    const [openChoosePA, setOpenChoosePA] = useState(false);

    return (
        <Formik
            initialValues={dataEditTimeSheet}
            validationSchema={validationSchema}
            enableReinitialize
            onSubmit={(values, actions) => {
                handleSubmitFormTimeSheet(values);
                actions.setSubmitting(false);
            }}
        >
            {({ isSubmitting, values, setFieldValue, setValues }) => {
                return (
                    <React.Fragment>
                        <Form autoComplete="off">
                            <FormikFocusError />

                            <DialogContent
                                className={`p-0 ${classes.styleContent}`}
                                style={{
                                    overflow: 'hidden !important',
                                }}
                                dividers={false}
                            >

                                <Grid container spacing={2} className="p-12">


                                    <Grid item sm={6} xs={12}>
                                        <GlobitsPagingAutocomplete
                                            disabled={!values.employee}
                                            label={t("timeSheet.workingDate")}
                                            requiredLabel
                                            name="timeSheet"
                                            searchObject={{ staffId: values.employee?.id }}
                                            getOptionLabel={(option) => ` ${formatDate('HH:mm', option.startTime)} - ${formatDate('HH:mm', option.endTime)} (${formatDate('DD/MM/YYYY', option.workingDate)})`}
                                            onChange={(_, value) =>
                                                setValues({ ...values, timeSheet: value, endTime: value?.endTime, startTime: value?.startTime, })
                                            }
                                            api={pagingTimeSheet}
                                        />
                                    </Grid>

                                    <Grid item sm={6} xs={12}>
                                        <GlobitsTimePicker
                                            required
                                            label={t("timeSheet.startTime")}
                                            name="startTime"
                                            format="HH:mm "
                                        />
                                    </Grid>
                                    <Grid item sm={6} xs={12}>
                                        <GlobitsTimePicker
                                            required
                                            label={t("timeSheet.endTime")}
                                            name="endTime"
                                            format="HH:mm "
                                        />
                                    </Grid>

                                    <Grid item sm={6} xs={12}>
                                        <GlobitsSelectInput
                                            label={t("timeSheet.approveStatus")}
                                            name="approveStatus"
                                            keyValue="id"
                                            hideNullOption={true}
                                            options={LocalConstants.CandidateApprovalStatus?.getListData()}
                                        />
                                    </Grid>

                                    <Grid item sm={6} xs={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("timeSheet.project")}
                                            name="project"
                                            api={pagingProject}
                                            handleChange={(value) => {
                                                setFieldValue("project", value);
                                                setFieldValue("projectActivity", null);
                                            }}
                                        />
                                    </Grid>

                                    <Grid item sm={6} xs={12}>
                                        <Grid container spacing={1}>
                                            <Grid item xs={10} >
                                                <GlobitsTextField
                                                    disabled={true}
                                                    label="Hoạt động dự án"
                                                    name="projectActivity.name"
                                                    value={values?.projectActivity ? values?.projectActivity?.name : ""}
                                                />
                                            </Grid>
                                            <Grid item xs={2} className="flex align-end">
                                                <Tooltip placement="top" title="Chọn hoạt động dự án">
                                                    <Button
                                                        className="btn bgc-lighter-dark-green d-inline-flex w-100 mb-2"
                                                        // onClick={() => openPopupPA(values.project)}
                                                        onClick={function () {
                                                            if (!values || !values?.project || !values?.project?.id) {
                                                                toast.info("Chưa chọn dự án");
                                                                return;
                                                            }

                                                            setOpenChoosePA(true);
                                                        }}
                                                    >
                                                        <TouchAppIcon className="text-white" />
                                                    </Button>
                                                </Tooltip>
                                            </Grid>
                                        </Grid>
                                    </Grid>


                                    <Grid item xs={12}>
                                        <GlobitsEditor
                                            name="description"
                                            label={t("timeSheet.description")}
                                            placeholder="Nhập nội dung nhật kí..."
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            {/* choose project's activity with paging and search */}
                            {
                                openChoosePA && (
                                    <ChoosePAPopup
                                        open={openChoosePA}
                                        handleClose={function () {
                                            setOpenChoosePA(false);
                                        }}
                                    />
                                )
                            }

                            <DialogActions>
                                <div className="flex flex-space-between flex-middle px-4">
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant="contained"
                                        className="mr-12 btn btn-danger d-inline-flex"
                                        onClick={() => handleClosePopup()}
                                        disabled={isSubmitting}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon />}
                                        className="mr-0 btn btn-success d-inline-flex"
                                        variant="contained"
                                        type="submit"
                                        disabled={isSubmitting}
                                    >
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>

                        </Form>
                    </React.Fragment>
                );
            }}
        </Formik>
    );
}


export default memo(observer(TimesheetDetailInfo));



const tabStyles = {
    styleContent: {
        backgroundColor: "#f6fbff",
        overflow: "hidden !important",
    }
}

const useStyles = makeStyles(tabStyles);