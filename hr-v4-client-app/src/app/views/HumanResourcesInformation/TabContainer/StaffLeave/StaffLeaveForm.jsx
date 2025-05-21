import React, { useEffect, useState } from "react";
import { Button, DialogActions, DialogContent, Grid, Tooltip, } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { Form, Formik } from "formik";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";
import SelectFile from "../../../StaffDocumentItem/SelectFile";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import VisibilityIcon from "@material-ui/icons/Visibility";
import GetAppIcon from "@material-ui/icons/GetApp";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsPagingAutocompleteV2 from "../../../../common/form/GlobitsPagingAutocompleteV2";
import { pagingOrganization } from "../../../Organization/OrganizationService";
import { pagingAllDepartments } from "../../../Department/DepartmentService";
import { pagingDiscipline } from "../../../Discipline/DisciplineService";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { PaidStatusOfLeaveStaff } from "app/LocalConstants";
import StaffLeaveHandOverItem from "./StaffLeaveHandOverItem";

export default observer(function StaffLeaveForm(props) {
    const { staffLeaveStore } = useStore();
    const { t } = useTranslation();
    const { id } = useParams();
    const {
        handleClose,
        saveOrUpdateStaffLeave,
        selectedStaffLeave,
        shouldOpenEditorDialog
    } = staffLeaveStore;

    const validationSchema = Yup.object({
        decisionNumber: Yup.string().required(t("validation.required")).nullable(),
        leaveDate: Yup.date().required(t("validation.required")).nullable()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày đúng định dạng"),
        handleOverItems: Yup.array()
            .of(
                Yup.object().shape({
                    // displayOrder:Yup.number().nullable().required(t("validation.required")) ,
                    name: Yup.string().nullable().required(t("validation.required")),
                })
            )
            .nullable(),
    });

    const [staffLeave, setStaffLeave] = useState(selectedStaffLeave);

    useEffect(() => {
        if (selectedStaffLeave) setStaffLeave(selectedStaffLeave);
        else setStaffLeave(selectedStaffLeave);
        if (id) {
            setStaffLeave(prev => ({
                ...prev,
                staff: { id: id }
            }));
        }

    }, [selectedStaffLeave?.id]);

    async function handleSubmit(values) {
        await saveOrUpdateStaffLeave(values);
    }

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={shouldOpenEditorDialog}
            onClosePopup={handleClose}
            noDialogContent
            title={(staffLeave?.id?.length > 0 ? t("general.button.add") : t("general.button.edit")) + " " + t("staffLeave.title")}
        >
            <Formik
                initialValues={staffLeave}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({ isSubmitting, values }) => (
                    <Form autoComplete="off">
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12} className='mt-10'>
                                    <p className='m-0 p-0 borderThrough2'>Thông tin cơ bản</p>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <GlobitsTextField
                                        label={t("staffLeave.decisionNumber")}
                                        name="decisionNumber"
                                        required
                                    />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <GlobitsDateTimePicker
                                        name="leaveDate"
                                        label={t("staffLeave.leaveDate")}
                                        required
                                    />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <GlobitsTextField
                                        label={t("staffLeave.stillInDebt")}
                                        name="stillInDebt"
                                    />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <GlobitsSelectInput
                                        name='paidStatus'
                                        label={t("staffLeave.paidStatus")}
                                        keyValue={"value"}
                                        options={PaidStatusOfLeaveStaff.getListData()}
                                    />
                                </Grid>

                                <Grid item xs={12} className='mt-10'>
                                    <p className='m-0 p-0 borderThrough2'>Các hạng mục cần bàn giao</p>
                                </Grid>

                                <Grid item xs={12}>
                                    <StaffLeaveHandOverItem />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className='dialog-footer px-12'>
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    startIcon={<BlockIcon />}
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
                                    startIcon={<SaveIcon />}
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
})