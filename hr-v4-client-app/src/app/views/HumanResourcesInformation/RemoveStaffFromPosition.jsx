import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import { removeStaffFromPosition } from "../Position/PositionService";

import BackspaceIcon from "@material-ui/icons/Backspace";
import BlockIcon from "@material-ui/icons/Block";
import { toast } from "react-toastify";
import AutoFillMainPosition from "./AutoFillMainPosition";

function RemoveStaffFromPosition() {
    const { t } = useTranslation();
    const { staffStore } = useStore();

    const { handleClose, shouldOpenTranserDialog, selectedStaff, selectedPosition, opentFormLeavePosition } =
        staffStore;

    const validationSchema = Yup.object({
        fromPosition: Yup.object().required(t("validation.required")).nullable(),
    });

    const handleSubmit = async (values) => {
        if (values?.fromPosition) {
            const { data } = await removeStaffFromPosition(values?.fromPosition?.id);
            toast.success(t("toast.delete_success"));
            handleClose();
        }
    };

    return (
        <GlobitsPopupV2
            size={"sm"}
            scroll={"body"}
            open={opentFormLeavePosition}
            onClosePopup={handleClose}
            title={"Bãi nhiệm nhân viên"}
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
                    note: "",
                }}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}>
                {({ isSubmitting, setFieldValue, values }) => (
                    <Form autoComplete='off'>
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <AutoFillMainPosition selectedPosition={selectedPosition} required />
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
                                        startIcon={<BackspaceIcon />}
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='warning'
                                        type='submit'>
                                        Bãi nhiệm
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

export default memo(observer(RemoveStaffFromPosition));
