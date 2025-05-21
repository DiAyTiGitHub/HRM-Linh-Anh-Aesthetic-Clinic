import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import {useStore} from "../../../stores";
import {Form, Formik} from "formik";
import {observer} from "mobx-react";
import React from "react";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";
import {useHistory} from "react-router-dom";
import ConstantList from "../../../appConfig";

export default observer(function SalaryTemplateCreateForm({handleAfterSubmit, updateListOnClose, open}) {
    const {t} = useTranslation();
    const history = useHistory();

    const {salaryTemplateStore} = useStore();
    const {
        handleClose,
        setShouldOpenCreateForm,
        saveSalaryTemplate,
        shouldOpenCreateForm
    } = salaryTemplateStore;
    const validationSchema = Yup.object({
        code: Yup.string().nullable().required(t("validation.required")),
        name: Yup.string().nullable().required(t("validation.required")),
    });

    async function handleSaveForm(values) {
        try {
            const savedTemplate = await saveSalaryTemplate(values);

            //link to page edit when staff created new
            if (!savedTemplate?.id) throw Error("Error when saving salary template");

            const redirectUrl = ConstantList.ROOT_PATH + `salary-template/` + savedTemplate?.id;
            setShouldOpenCreateForm(false)
            history.push(redirectUrl);
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <GlobitsPopupV2
            open={shouldOpenCreateForm}
            size='xs'
            noDialogContent
            title={t("general.button.add") + " Mẫu bảng lương"}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={{}}
                onSubmit={(values) => handleSaveForm(values)}>
                {({isSubmitting, values, setFieldValue}) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label="Mã mẫu"
                                            name="code"
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label="Tên mẫu bảng lương"
                                            name="name"
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label="Mô tả"
                                            name="description"
                                            multiline
                                            rows={3}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon/>}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={() => handleClose()}>
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
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    )
        ;
})
;
