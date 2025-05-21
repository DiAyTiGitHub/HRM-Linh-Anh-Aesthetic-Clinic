import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import {observer} from "mobx-react";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import {useStore} from "app/stores";
import {Form, Formik} from "formik";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

const defaultBank = {
    name: "",
    code: "",
    description: "",
};

export default observer(function BankForm() {
    const {bankStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        saveOrUpdate,
        selectedBank,
        shouldOpenEditorDialog
    } = bankStore;

    const [bank, setBank] = useState(defaultBank);
    const validationSchema = Yup.object({
        name: Yup.string()
            .trim()
            .required(t("validation.required")),

        code: Yup.string()
            .trim()
            .required(t("validation.required")),
        //
        // description: Yup.string()
        //     .trim()
        //     .required(t("validation.required"))
    });

    useEffect(() => {
        if (selectedBank) setBank(selectedBank);
        else setBank(defaultBank);
    }, [selectedBank]);

    function handleFormSubmit(values) {
        saveOrUpdate(values);
    }

    return (
        <GlobitsPopupV2
            open={shouldOpenEditorDialog}
            size='sm'
            noDialogContent
            title={(bank?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("")}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={bank}
                onSubmit={(values) => handleFormSubmit(values)}>
                {({isSubmitting}) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <GlobitsTextField label={t("Tên ngân hàng")} name='name' required/>
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField label={t("Mã mẫu ngân hàng")} name='code' required/>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("Mô tả mẫu ngân hàng")}
                                            name='description'
                                            multiline
                                            rows={3}
                                        />
                                    </Grid>

                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-4'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button startIcon={<BlockIcon/>} variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex' color='secondary'
                                            onClick={() => handleClose()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button startIcon={<SaveIcon/>} className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
});

