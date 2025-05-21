import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPosition } from "../Position/PositionService";
import LoopIcon from "@material-ui/icons/Loop";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import moment from "moment";
import AutoFillMainPosition from "./AutoFillMainPosition";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

function StaffTransferForm() {
    const { t } = useTranslation();

    const { staffStore, staffWorkingHistoryStore } = useStore();

    const { onlySave } = staffWorkingHistoryStore;

    const { handleClose, shouldOpenTranserDialog, selectedStaff, selectedPosition } = staffStore;

    const validationSchema = Yup.object({
        startDate: Yup.date()
            .test("is-greater", "Ngày bắt đầu phải lớn thiết lập", function (value) {
                const { signedDate } = this.parent;
                if (signedDate && value) {
                    return moment(value).isAfter(moment(signedDate), "date");
                }
                return true;
            })
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable(),
        toPosition: Yup.object().required(t("validation.required")).nullable(),
    });

    const handleSubmit = (values) => {
        console.log(values);
        let dto = {
            ...values,
            staff: {
                id: selectedStaff?.id,
            },
        };

        onlySave(dto)
            .then((result) => {
                console.log(result);
                handleClose();
            })
            .catch((err) => {
                console.log(err);
            });
    };

    return (
        <GlobitsPopupV2
            size={"md"}
            scroll={"body"}
            open={shouldOpenTranserDialog}
            onClosePopup={handleClose}
            title={"Điều chuyển nhân viên"}
            noDialogContent>
            <Formik
                initialValues={{
                    staff: selectedStaff,
                    fromOrganization: null,
                    fromDepartment: null,
                    fromPosition: null,
                    toOrganization: null,
                    toDepartment: null,
                    toPosition: null,
                    startDate: null,
                    endDate: null,
                    transferType: 1,
                    vacantOnly: true, // 👈 Thêm trường này
                    note: "",
                }}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}>
                {({ isSubmitting, setFieldValue, values }) => (
                    <Form autoComplete='off'>
                        <DialogContent
                            className='dialog-body p-12'
                        // style={{ maxHeight: "80vh", minWidth: "300px" }}
                        >
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={6}>
                                            <AutoFillMainPosition selectedPosition={selectedPosition} />
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12}>
                                                    <GlobitsPagingAutocompleteV2
                                                        label={"Đơn vị đích"}
                                                        name='toOrganization'
                                                        api={pagingAllOrg}
                                                        searchObject={{ pageSize: 10, pageIndex: 1 }}
                                                        value={values.toOrganization} // Thêm value để hiển thị
                                                        handleChange={(e, newValue) => {
                                                            setFieldValue("toOrganization", newValue);
                                                            setFieldValue("toDepartment", null);
                                                            setFieldValue("toPosition", null);
                                                            if (values?.fromOrganization?.id === newValue?.id) {
                                                                setFieldValue("transferType", 1);
                                                            } else {
                                                                setFieldValue("transferType", 2);
                                                            }
                                                        }}
                                                        getOptionLabel={(option) => option?.name || ""}
                                                    />
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <GlobitsPagingAutocompleteV2
                                                        label={"Phòng ban đích"}
                                                        name='toDepartment'
                                                        api={pagingAllDepartments}
                                                        searchObject={{
                                                            pageSize: 10,
                                                            pageIndex: 1,
                                                            organizationId: values?.toOrganization?.id,
                                                        }}
                                                        value={values.toDepartment} // Thêm value để hiển thị
                                                        handleChange={(e, newValue) => {
                                                            setFieldValue("toDepartment", newValue);
                                                            setFieldValue("toPosition", null);
                                                            if (newValue?.organization) {
                                                                setFieldValue("toOrganization", newValue?.organization);
                                                            }
                                                        }}
                                                        // allowLoadOptions={!!values?.toOrganization?.id}
                                                        clearOptionOnClose
                                                        getOptionLabel={(option) => {
                                                            return option?.code
                                                                ? `${option?.name} - ${option?.code}`
                                                                : option?.name;
                                                        }}
                                                    />
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <GlobitsPagingAutocompleteV2
                                                        label={"Vị trí đích"}
                                                        name='toPosition'
                                                        api={pagingPosition}
                                                        required
                                                        searchObject={{
                                                            pageSize: 10,
                                                            pageIndex: 1,
                                                            departmentId: values?.toDepartment?.id,
                                                            vacant: values?.vacantOnly,
                                                        }}
                                                        value={values.toPosition} // Thêm value để hiển thị
                                                        handleChange={(e, newValue) => {
                                                            setFieldValue("toPosition", newValue);
                                                            if (newValue?.department) {
                                                                setFieldValue("toDepartment", newValue?.department);
                                                                if (newValue?.department?.organization) {
                                                                    setFieldValue(
                                                                        "toOrganization",
                                                                        newValue?.department?.organization
                                                                    );
                                                                }
                                                            }
                                                        }}
                                                        getOptionLabel={(option) => {
                                                            return option?.code
                                                                ? `${option?.name} - ${option?.code}`
                                                                : option?.name || "";
                                                        }}
                                                    // allowLoadOptions={!!values?.toDepartment?.id}
                                                    />
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <GlobitsCheckBox
                                                        name='vacantOnly'
                                                        label='Chỉ hiển thị vị trí trống'
                                                    />
                                                </Grid>

                                                {/* <Grid item xs={12}>
                                                    <GlobitsSelectInput
                                                        label={t("staffWorkingHistory.transferType")}
                                                        name='transferType'
                                                        value={values?.transferType}
                                                        keyValue='value'
                                                        options={
                                                            LocalConstants.StaffWorkingHistoryTransferWithOutBreakType
                                                        }
                                                    />
                                                </Grid> */}
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </Grid>
                                <Grid item xs={6}>
                                    <GlobitsDateTimePicker
                                        required
                                        label={t("staffWorkingHistory.startDate")}
                                        name='startDate'
                                    />
                                </Grid>

                                <Grid item xs={6}>
                                    <GlobitsDateTimePicker label={t("staffWorkingHistory.endDate")} name='endDate' />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        label={t("staffWorkingHistory.note")}
                                        name='note'
                                        multiline
                                        rows={4}
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <div className='dialog-footer dialog-footer-v2 py-8'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={handleClose}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<LoopIcon />}
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='primary'
                                        type='submit'>
                                        Điều chuyển
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffTransferForm));
