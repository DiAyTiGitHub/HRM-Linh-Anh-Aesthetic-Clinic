import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { Form , Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { pagingProduct } from "../AssetManagementService";
import GlobitsTextField from "app/common/form/GlobitsTextField";

const AssetForm = () => {
    const {t} = useTranslation();

    const {selectedAsset , onSaveAsset , onClosePopup} = useStore().assetManagementStore.assetStore;

    const validationSchema = Yup.object({
        product:Yup.object().required(t("validation.required")).nullable() ,
        staff:Yup.object().required(t("validation.required")).nullable() ,
        startDate:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .nullable() ,
    });

    return (
        <GlobitsPopupV2
            open={Boolean(selectedAsset)}
            noDialogContent
            title='Quản lý sử dụng công cụ/dụng cụ'
            onClosePopup={onClosePopup}
            size='sm'>
            <Formik
                enableReinitialize
                validationSchema={validationSchema}
                initialValues={selectedAsset}
                onSubmit={onSaveAsset}>
                {({isSubmitting}) => (
                    <Form autoComplete='off'>
                        <DialogContent className='p-12'>
                            <Grid container spacing={2}>
                                <Grid item md={6}>
                                    <GlobitsPagingAutocomplete
                                        required
                                        name='product'
                                        label='Công cụ/ dụng cụ'
                                        api={pagingProduct}
                                    />
                                </Grid>

                                <Grid item md={6}>
                                    <GlobitsPagingAutocomplete
                                        name='staff'
                                        label='Nhân viên'
                                        api={pagingStaff}
                                        displayData='displayName'
                                        required
                                    />
                                </Grid>

                                <Grid item md={6}>
                                    <GlobitsDateTimePicker
                                        required
                                        name='startDate'
                                        label='Ngày bắt đầu dùng'/>
                                </Grid>

                                <Grid item md={6}>
                                    <GlobitsDateTimePicker
                                        name='endDate'
                                        label='Ngày kết thúc'/>
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        label='Ghi chú'
                                        name='note'
                                        multiline rows={3}
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className='confirmDeletePopupFooter'>
                            <div className='flex flex-space-between flex-middle'>
                                <Button
                                    startIcon={<BlockIcon/>}
                                    variant='contained'
                                    className='mr-12 btn btn-secondary d-inline-flex'
                                    color='secondary'
                                    onClick={() => onClosePopup()}>
                                    {t("general.button.cancel")}
                                </Button>
                                <Button
                                    startIcon={<SaveIcon/>}
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

export default memo(observer(AssetForm));
