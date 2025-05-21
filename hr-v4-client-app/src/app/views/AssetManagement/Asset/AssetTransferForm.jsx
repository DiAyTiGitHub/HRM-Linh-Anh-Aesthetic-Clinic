import { Button, DialogActions, DialogContent, Grid, Typography } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";

const AssetTransferForm = (props) => {
    const { handleAfterConfirm } = props;
    const { t } = useTranslation();
    const { selectedTransfer, onSaveTransfer, onCloseTransferPopup } = useStore().assetManagementStore.assetStore;

    const validationSchema = Yup.object().shape({
        staff: Yup.object().required("Vui lòng chọn người sử dụng mới").nullable(), // required cho field 'staff'
        asset: Yup.object().required("Vui lòng chọn công cụ/ dụng cụ chuyển").nullable(),
    });

    const handleSubmitForm = async (values) => {
        await onSaveTransfer(values);

        if (typeof handleAfterConfirm === "function") {
            handleAfterConfirm();
        }
    };

    return (
        <GlobitsPopupV2
            open={Boolean(selectedTransfer)}
            noDialogContent
            title='Đổi người sử dụng công cụ/dụng cụ'
            onClosePopup={onCloseTransferPopup}
            size='sm'>
            <Formik
                enableReinitialize
                validationSchema={validationSchema}
                initialValues={selectedTransfer}
                onSubmit={handleSubmitForm}>
                {({ isSubmitting, values }) => (
                    <Form autoComplete='off'>
                        <DialogContent className='p-12'>
                            <Grid container spacing={2}>
                                <Grid item md={12}>
                                    <GlobitsPagingAutocomplete
                                        name='product'
                                        label='Công cụ/dụng cụ hiện tại'
                                        value={values?.asset?.product}
                                        readOnly={true}
                                    />
                                </Grid>
                                <Grid item md={12}>
                                    <GlobitsPagingAutocomplete
                                        name='currentStaff'
                                        label='Người sử dụng hiện tại'
                                        value={values?.asset?.staff}
                                        displayData='displayName'
                                        readOnly={true}
                                        getOptionLabel={(option) =>
                                            option ? `${option.displayName} - ${option.staffCode}` : ""
                                        }
                                    />
                                </Grid>

                                {/* Divider */}
                                <Grid item xs={12}>
                                    <Typography
                                        variant='subtitle1'
                                        sx={{
                                            borderBottom: "1px solid #ccc",
                                            paddingBottom: 1,
                                            marginTop: 1,
                                            marginBottom: 1,
                                        }}>
                                        Thông tin chuyển giao
                                    </Typography>
                                </Grid>

                                {/* New User Info */}
                                <Grid item md={12}>
                                    <GlobitsPagingAutocomplete
                                        name='staff'
                                        label='Người sử dụng mới'
                                        api={pagingStaff}
                                        getOptionLabel={(option) =>
                                            option ? `${option.displayName} - ${option.staffCode}` : ""
                                        }
                                        required
                                    />
                                </Grid>
                                <Grid item md={12}>
                                    <GlobitsDateTimePicker name='startDate' label='Ngày bắt đầu sử dụng' />
                                </Grid>
                                <Grid item md={12}>
                                    <GlobitsDateTimePicker name='endDate' label='Ngày kết thúc' />
                                </Grid>
                            </Grid>
                        </DialogContent>
                        <DialogActions className='dialog-footer px-12'>
                            <div className='flex flex-space-between flex-middle'>
                                <Button
                                    variant='contained'
                                    className='mr-12 btn btn-secondary d-inline-flex'
                                    color='secondary'
                                    disabled={isSubmitting}
                                    onClick={onCloseTransferPopup}>
                                    {t("general.button.close")}
                                </Button>
                                <Button
                                    className='mr-0 btn btn-primary d-inline-flex'
                                    variant='contained'
                                    color='primary'
                                    type='submit'
                                    disabled={isSubmitting}>
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
};

export default memo(observer(AssetTransferForm));
