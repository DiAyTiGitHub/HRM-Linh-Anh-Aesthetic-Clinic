import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import SelectDepartmentComponent from "app/common/SelectComponent/SelectDepartment/SelectDepartmentComponent";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";

function PositionNodeForm(props) {
    const { isDisabled, positionId, open, handleClose, handleFormSubmit } = props;

    const { positionStore } = useStore();
    const { t } = useTranslation();
    const { savePosition, getById } = positionStore;

    const validationSchema = Yup.object({
        // code: Yup.string().required(t("validation.code")).nullable(),
        name: Yup.string().required(t("validation.name")).nullable(),
        title: Yup.object().required(t("Chưa nhập chức danh")).nullable(),
    });

    async function handleSaveForm(values) {
        await savePosition(values);
        handleFormSubmit(values);
        handleClose();

    }


    const [initialValues, setInitialValues] = useState({ id: positionId, department: null });

    useEffect(
        function () {
            if (positionId) {
                getById(positionId).then((data) => {
                    console.log("getById", data);
                    setInitialValues(data);
                });
            }
        },
        [positionId]
    );
    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='md'
            open={open}
            noDialogContent
            title={"Bổ nhiệm nhân viên"}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                validate
                                                label='Tên vị trí'
                                                name='name'
                                                disabled={isDisabled}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                validate
                                                label='Mã vị trí'
                                                disabled={isDisabled}
                                                name='code'
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <SelectDepartmentComponent
                                                name={"department"}
                                                clearFields={["title"]}
                                                disabled={isDisabled}
                                                disabledTextFieldOnly={true}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsPagingAutocompleteV2
                                                label='Chức danh'
                                                name='title'
                                                api={pagingPositionTitle}
                                                disabled={!values?.department || isDisabled}
                                                searchObject={{
                                                    pageIndex: 1,
                                                    pageSize: 9999,
                                                    keyword: "",
                                                    departmentId: values?.department?.id,
                                                }}
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <ChooseUsingStaffSection
                                                label={"Nhân viên"}
                                                // disabled={isDisabled}
                                                disabledTextFieldOnly={true}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={3} className='pt-28 pl-20'>
                                            <GlobitsCheckBox
                                                label='Là vị trí chính'
                                                // disabled={isDisabled}
                                                name='isMain'
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label='Mô tả'
                                                name='description'
                                                // disabled={isDisabled}
                                                multiline
                                                rows={3}
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
                                            disabled={isSubmitting}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon />}
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
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(PositionNodeForm));
