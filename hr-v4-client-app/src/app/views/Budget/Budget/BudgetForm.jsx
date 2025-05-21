import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import {observer} from "mobx-react";
import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import {useStore} from "../../../stores";
import {Form, Formik} from "formik";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";

const defaultBudget = {
    id: "",
    name: "",
    code: "",
    description: null,
    organization: null,
    currency: "VND", // Default currency
    openingBalance: 0,
    endingBalance: 0,
};

export default observer(function BudgetForm({handleAfterSubmit, updateListOnClose, open}) {
    const {budgetStore} = useStore();
    const {t} = useTranslation();
    const {handleClose, saveOrUpdateBudget, selectedBudget} = budgetStore;

    const [budget, setBudget] = useState(defaultBudget);

    const validationSchema = Yup.object({
        name: Yup.string().required(t("budgetCategory.validation.nameRequired")),
        code: Yup.string().required(t("validation.required")),

    });

    useEffect(() => {
        if (selectedBudget) setBudget(selectedBudget);
        else setBudget(defaultBudget);
    }, [selectedBudget]);

    function handleFormSubmit(budget) {
        console.log(budget);
        let res = saveOrUpdateBudget(budget);
        if (handleAfterSubmit) handleAfterSubmit(res);
    }

    return (
        <GlobitsPopupV2 open={open} size='sm' noDialogContent
                        title={(selectedBudget?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("budget.title")}
                        onClosePopup={handleClose}>
            <Formik validationSchema={validationSchema} enableReinitialize initialValues={budget}
                    onSubmit={(values) => handleFormSubmit(values)}>
                {({isSubmitting}) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <GlobitsTextField label={t("budget.code")} name='code' required/>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField label={t("budget.name")} name='name' required/>
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsVNDCurrencyInput label={t("budget.openingBalance")}
                                                                 name='openingBalance'/>
                                    </Grid>
                                    {/*
                  <Grid item xs={6}>
                    <GlobitsVNDCurrencyInput
                      label={t("budget.endingBalance")}
                      name="endingBalance"
                    />
                  </Grid> */}

                                    <Grid item xs={12}>
                                        <GlobitsTextField isTextArea={true} multiline minRows={2} m
                                                          label={t("budget.description")} name='description'/>
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-0'>
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
