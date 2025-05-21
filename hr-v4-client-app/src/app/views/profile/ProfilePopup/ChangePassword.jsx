import GlobitsTextField from 'app/common/form/GlobitsTextField'
import GlobitsPopup from 'app/common/GlobitsPopup'
import { Form, Formik } from 'formik'
import React from 'react'
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import { useTranslation } from 'react-i18next';
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useStore } from 'app/stores';
import { observer } from 'mobx-react';
import * as Yup from "yup";
import SettingsPowerIcon from '@material-ui/icons/SettingsPower';

export default observer(function ChangePassword() {
    const { openFormChangePassWord, handleClosePopup, dataChangePassWord, submitFormChangePassWord } = useStore().profileStore;

    const { t } = useTranslation();

    const validationSchema = Yup.object({
        oldPassword: Yup.string().required(t("validation.required")).nullable(),
        password: Yup.string().required(t("validation.required")).nullable(),
        confirmPassword: Yup.string()
            .oneOf([Yup.ref("password"), null], t("validation.confirm_password"))
            .required(t("validation.required"))
            .nullable(),
    });

    return (
        <GlobitsPopup
            open={openFormChangePassWord}
            title='Đổi mật khẩu'
            onClosePopup={handleClosePopup}
            size='xs'
            noDialogContent
        >
            <Formik
                initialValues={dataChangePassWord}
                onSubmit={submitFormChangePassWord}
                validationSchema={validationSchema}
            >
                {({ values, isSubmitting }) => (
                    <Form autoComplete='off'>
                        <DialogContent className="o-hidden dialog-body p-12">
                            <GlobitsTextField name='oldPassword' validate label='Mật khẩu hiện tại' type='password' />

                            <GlobitsTextField name='password' validate label='Mật khẩu mới' type='password' />

                            <GlobitsTextField name='confirmPassword' validate label='Xác nhận mật khẩu mới' type='password' />
                        </DialogContent>

                        <DialogActions className="dialog-footer p-0">
                            <Grid item xs={6} sm={3}>
                                <Button
                                    fullWidth
                                    startIcon={<BlockIcon />}
                                    className="btn btn-secondary d-inline-flex"
                                    onClick={handleClosePopup}
                                >
                                    {t("general.button.cancel")}
                                </Button>
                            </Grid>

                            <Grid item xs={6} sm={3}>
                                <Button
                                    fullWidth
                                    startIcon={<SaveIcon />}
                                    className="btn btn-primary d-inline-flex"
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {t("general.button.save")}
                                </Button>
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <Button
                                    fullWidth
                                    startIcon={<SettingsPowerIcon />}
                                    className="btn btn-success d-inline-flex"
                                    onClick={() => submitFormChangePassWord(values, true)}
                                >
                                    Lưu và đăng xuất
                                </Button>
                            </Grid>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopup>
    )
})