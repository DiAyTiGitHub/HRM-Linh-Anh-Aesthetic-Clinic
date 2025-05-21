import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import {useStore} from "app/stores";
import {Form, Formik} from "formik";
import {observer} from "mobx-react";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import {HrDocumentItemRequired} from "../../LocalConstants";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import {pagingHrDocumentTemplate} from "../HrDocumentTemplate/HrDocumentTemplateService";

const defaultHrDocumentItem = {
    id: "",
    name: "",
    code: "",
    description: "",
    isRequired: false,
    documentTemplate: null,
    // documentItems: [
    //     {
    //         name: "",
    //         code: "",
    //         weight: "",
    //         usedForSalary: "",
    //     },
    // ],
};
export default observer(function HrDocumentItemForm({handleAfterSubmit, updateListOnClose, open}) {
    const {hrDocumentItemStore} = useStore();
    const {t} = useTranslation();
    const {handleClose, saveOrUpdateHrDocumentItem, selectedHrDocumentItem, handleSelectHrDocumentItem} =
        hrDocumentItemStore;

    const [hrDocumentItem, setHrDocumentItem] = useState(defaultHrDocumentItem);

    const validationSchema = Yup.object({
        name: Yup.string().trim().required(t("validation.required")),
        code: Yup.string().trim().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedHrDocumentItem) setHrDocumentItem(selectedHrDocumentItem);
        else setHrDocumentItem(defaultHrDocumentItem);
    }, [selectedHrDocumentItem]);

    function handleFormSubmit(hrDocumentItem) {
        saveOrUpdateHrDocumentItem(hrDocumentItem);
    }

    return (
        <GlobitsPopupV2
            open={open}
            size='sm'
            noDialogContent
            title={
                (selectedHrDocumentItem?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("Tài liệu/Hồ sơ")
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={hrDocumentItem}
                onSubmit={(values) => handleFormSubmit(values)}>
                {({isSubmitting}) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField
                                            label={t("Mã tài liệu")}
                                            name='code'
                                            required/>
                                    </Grid>

                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField
                                            label={t("Tên tài liệu")}
                                            name='name'
                                            required/>
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsSelectInput
                                            hideNullOption
                                            label={t("Bắt buộc nộp")}
                                            name={"isRequired"}
                                            keyValue="value"
                                            displayvalue="name"
                                            options={HrDocumentItemRequired}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsPagingAutocompleteV2
                                            name="documentTemplate"
                                            label={"Thuộc mẫu tài liệu"}
                                            api={pagingHrDocumentTemplate}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("Mô tả")}
                                            name='description'
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
    );
});
