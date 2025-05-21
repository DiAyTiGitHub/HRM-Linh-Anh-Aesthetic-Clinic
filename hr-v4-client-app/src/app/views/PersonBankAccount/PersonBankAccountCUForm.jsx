import React, {memo, useEffect, useState} from "react";
import {Form, Formik} from "formik";
import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "app/stores";
import * as Yup from "yup";
import {observer} from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import {pagingBank} from "../Bank/BankService";

function PersonBankAccountCUForm(props) {
    const {t} = useTranslation();

    const {
        isDisabled,
        person,
        handleAfterSubmit

    } = props;

    const {personBankAccountStore} = useStore();

    const {
        handleClose,
        savePersonBankAccount,
        selectedPersonBankAccount,
        openCreateEditPopup

    } = personBankAccountStore;

    const validationSchema = Yup.object({
        // code: Yup.string().required(t("validation.code")).nullable(),
        bankAccountNumber: Yup.string().required(t("validation.required")).nullable(),
        bank: Yup.object().required(t("Chưa chọn ngân hàng")).nullable(),
        bankAccountName: Yup.string().required(t("validation.required")).matches(/^[A-Za-z ]*$/, "Không được chứa số hoặc ký tự đặc biệt.").nullable(),

    });

    async function handleSaveForm(values) {
        let newValues = values;
        if (person != null) {
            newValues = {...newValues, person: person};
        }

        try {
            const response = await savePersonBankAccount(newValues);

            await handleAfterSubmit(response);
        } catch (error) {
            console.error(error);
        }
    }

    const [initialValues, setInitialValues] = useState(selectedPersonBankAccount);

    useEffect(
        function () {
            setInitialValues(selectedPersonBankAccount);
        },
        [selectedPersonBankAccount, selectedPersonBankAccount?.id]
    );

    function toUpperCaseNoDiacritics(str) {
        return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toUpperCase();
    }

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='sm'
            open={openCreateEditPopup}
            noDialogContent
            title={(selectedPersonBankAccount?.id ? t("general.button.edit") : t("general.button.add")) + " " + "tài khoản ngân hàng"}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}>
                {({isSubmitting, values, setFieldValue, initialValues}) => {
                    // function handleAutoRenderCode(_, title) {
                    //     setFieldValue("title", title);
                    //     setFieldValue("name", title?.name);
                    //     const positionCode = convertToConstantFormat(title?.name);
                    //     setFieldValue("code", positionCode);
                    // }
                    if (values?.bankAccountName == null || values?.bankAccountName === "") {
                        setFieldValue("bankAccountName", toUpperCaseNoDiacritics(values?.person?.displayName))
                    }
                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError/>

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                label={"Tài khoản nhân viên"}
                                                name='person'
                                                disabled={true}
                                                value={values?.person?.displayName}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsPagingAutocompleteV2
                                                label='Ngân hàng'
                                                name='bank'
                                                api={pagingBank}
                                                getOptionLabel={(option) => {
                                                    return option?.code
                                                        ? `${option?.name} - ${option?.code}`
                                                        : option?.name;
                                                }}
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                label='Tên tài khoản'
                                                name='bankAccountName'
                                                // value={toUpperCaseNoDiacritics(values?.person?.displayName)}
                                                required
                                                onChange={(e) => {
                                                    setFieldValue("bankAccountName", e.target.value.toUpperCase());
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                required
                                                label='Số tài khoản ngân hàng'
                                                name='bankAccountNumber'
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                label='Chi nhánh ngân hàng'
                                                name='bankBranch'
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} className="mt-18">
                                            <GlobitsCheckBox
                                                label='Là tài khoản chính'
                                                name='isMain'
                                            />
                                        </Grid>


                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer dialog-footer-v2 py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex'
                                            color='secondary'
                                            onClick={handleClose}
                                            disabled={isSubmitting || isDisabled}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon/>}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'
                                            disabled={isSubmitting || isDisabled}>
                                            {t("general.button.save")}
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

export default memo(observer(PersonBankAccountCUForm));
