import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import * as Yup from "yup";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPosition } from "./PositionService";
import LoopIcon from "@material-ui/icons/Loop";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
function StaffTransferForm(props) {
    const { isDisabled } = props;

    const { positionStore } = useStore();
    const { t } = useTranslation();
    const { handleClose, opentStaffFormTransfer, handleConfirmStaffTransfer, selectedPosition } = positionStore;

    // Validation schema
    const validationSchema = Yup.object({
        fromPosition: Yup.object()
            .shape({
                id: Yup.string().required(t("Chưa chọn vị trí hiện tại")),
            })
            .required(t("Chưa chọn vị trí hiện tại"))
            .nullable(),
        toPosition: Yup.object()
            .shape({
                id: Yup.string().required(t("Chưa chọn vị trí đích")),
            })
            .required(t("Chưa chọn vị trí đích"))
            .nullable(),
    });

    async function handleSaveForm(values) {
        if (values.toPosition && values?.fromPosition) {
            handleConfirmStaffTransfer(values.fromPosition, values.toPosition, values?.note);
        } else {
            toast.error("Chưa chọn vị trí đích");
        }
    }

    return (
        <GlobitsPopupV2
            size='md'
            open={opentStaffFormTransfer}
            noDialogContent
            title={"Điều chuyển nhân viên"}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                onSubmit={handleSaveForm}
                initialValues={{
                    fromOrganization: selectedPosition?.department?.organization || null,
                    fromDepartment: selectedPosition?.department || null,
                    fromPosition: selectedPosition || null,
                    staff: selectedPosition?.staff || null,
                    toPosition: null,
                    vacantOnly: true
                }}>
                {({ isSubmitting, values, setFieldValue, touched, errors }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent>
                                <Grid container spacing={2}>
                                    <Grid item xs={6}>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Đơn vị hiện tại"}
                                                    name='fromOrganization'
                                                    api={pagingAllOrg}
                                                    searchObject={{ pageSize: 10, pageIndex: 1 }}
                                                    value={values.fromOrganization} // Thêm value để hiển thị
                                                    handleChange={(e, newValue) => {
                                                        setFieldValue("fromOrganization", newValue);
                                                        setFieldValue("fromDepartment", null);
                                                        setFieldValue("fromPosition", null);
                                                        setFieldValue("staff", null);
                                                    }}
                                                    getOptionLabel={(option) => option?.name || ""}
                                                    error={touched.fromOrganization && Boolean(errors.fromOrganization)}
                                                    helperText={touched.fromOrganization && errors.fromOrganization?.id}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Phòng ban hiện tại"}
                                                    name='fromDepartment'
                                                    api={pagingAllDepartments}
                                                    searchObject={{
                                                        pageSize: 10,
                                                        pageIndex: 1,
                                                        organizationId: values?.fromOrganization?.id,
                                                    }}
                                                    value={values.fromDepartment} // Thêm value để hiển thị
                                                    handleChange={(e, newValue) => {
                                                        setFieldValue("fromDepartment", newValue);
                                                        setFieldValue("fromPosition", null);
                                                        setFieldValue("staff", null);
                                                        if (newValue?.organization) {
                                                            setFieldValue("fromOrganization", newValue.organization);
                                                        }
                                                    }}
                                                    allowLoadOptions={!!values?.fromOrganization?.id}
                                                    clearOptionOnClose
                                                    getOptionLabel={(option) => {
                                                        return option?.code
                                                            ? `${option?.name} - ${option?.code}`
                                                            : option?.name;
                                                    }}
                                                    error={touched.fromDepartment && Boolean(errors.fromDepartment)}
                                                    helperText={touched.fromDepartment && errors.fromDepartment?.id}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Vị trí hiện tại"}
                                                    name='fromPosition'
                                                    api={pagingPosition}
                                                    searchObject={{
                                                        pageSize: 10,
                                                        pageIndex: 1,
                                                        departmentId: values?.fromDepartment?.id,
                                                        vacant: false,
                                                    }}
                                                    value={values.fromPosition} // Thêm value để hiển thị
                                                    handleChange={(e, newValue) => {
                                                        setFieldValue("fromPosition", newValue);
                                                        setFieldValue("staff", newValue?.staff);
                                                        if (newValue?.department) {
                                                            setFieldValue("fromDepartment", newValue?.department);
                                                            if (newValue?.department?.organization) {
                                                                setFieldValue(
                                                                    "fromOrganization",
                                                                    newValue?.department?.organization
                                                                );
                                                            }
                                                        }
                                                    }}
                                                    getOptionLabel={(option) => {
                                                        return option?.code
                                                            ? `${option?.name} - ${option?.code}`
                                                            : option?.name;
                                                    }}
                                                    required
                                                    allowLoadOptions={!!values?.fromDepartment?.id}
                                                    error={touched.fromPosition && Boolean(errors.fromPosition)}
                                                    helperText={touched.fromPosition && errors.fromPosition?.id}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Nhân viên phụ trách"}
                                                    name='staff'
                                                    value={values.staff} // Thêm value để hiển thị
                                                    disabled
                                                    getOptionLabel={(option) =>
                                                        option?.displayName || option?.name - option?.code || ""
                                                    }
                                                    error={touched.staff && Boolean(errors.staff)}
                                                    helperText={touched.staff && errors.staff?.id}
                                                />
                                            </Grid>
                                        </Grid>
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
                                                    }}
                                                    getOptionLabel={(option) => option?.name || ""}
                                                    error={touched.toOrganization && Boolean(errors.toOrganization)}
                                                    helperText={touched.toOrganization && errors.toOrganization?.id}
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
                                                    allowLoadOptions={!!values?.toOrganization?.id}
                                                    clearOptionOnClose
                                                    getOptionLabel={(option) => {
                                                        return option?.code
                                                            ? `${option?.name} - ${option?.code}`
                                                            : option?.name;
                                                    }}
                                                    error={touched.toDepartment && Boolean(errors.toDepartment)}
                                                    helperText={touched.toDepartment && errors.toDepartment?.id}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Vị trí đích"}
                                                    name='toPosition'
                                                    api={pagingPosition}
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
                                                    required
                                                    allowLoadOptions={!!values?.toDepartment?.id}
                                                    error={touched.toPosition && Boolean(errors.toPosition)}
                                                    helperText={touched.toPosition && errors.toPosition?.id}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <GlobitsCheckBox
                                                    name='vacantOnly'
                                                    label='Chỉ hiển thị vị trí trống'
                                                />
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField label='Ghi chú' name='note' multiline rows={3} />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>

                        <div className='dialog-footer dialog-footer-v2 py-8'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={handleClose}
                                        disabled={isSubmitting || isDisabled}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<LoopIcon />}
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='primary'
                                        type='submit'
                                        disabled={isSubmitting || isDisabled}>
                                        {t("Điều chuyển")}
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
