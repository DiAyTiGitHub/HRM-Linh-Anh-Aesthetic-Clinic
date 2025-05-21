import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import LoopIcon from "@material-ui/icons/Save";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectDepartmentComponent from "app/common/SelectComponent/SelectDepartment/SelectDepartmentComponent";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import * as Yup from "yup";

function PositionTransferForm(props) {
    const { isDisabled, staff } = props;

    const { positionStore } = useStore();
    const { t } = useTranslation();
    const { handleClose, opentFormTransfer, handleConfirmTransfer } = positionStore;

    const validationSchema = Yup.object({
        // code: Yup.string().required(t("validation.code")).nullable(),
        department: Yup.object().required(t("Chưa chọn phòng ban")).nullable(),
    });

    async function handleSaveForm(values) {
        if (values.department) {
            handleConfirmTransfer(values.department);
        } else {
            toast.error("Chưa chọn phòng ban");
        }
    }

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='sm'
            open={opentFormTransfer}
            noDialogContent
            title={"Điều chuyển vị trí"}
            onClosePopup={handleClose}>
            <Formik validationSchema={validationSchema} enableReinitialize onSubmit={handleSaveForm} initialValues={{}}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {

                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <SelectDepartmentComponent
                                                label={t("Chọn phòng ban điều chuyển tới")}
                                                name={"department"}
                                                disabled={isDisabled}
                                                disabledTextFieldOnly={true}
                                            />
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
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(PositionTransferForm));
