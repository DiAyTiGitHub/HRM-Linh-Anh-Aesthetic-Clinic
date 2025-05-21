import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsPopup from "app/common/GlobitsPopup";
import { Form, Formik } from "formik";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { pagingBudgetCategory } from "../BudgetCategory/BudgetCategoryService";

export default function VoucherItemPopup({ open, formData, handleClose, handleSubmit }) {
  const { t } = useTranslation();
  const validationSchema = Yup.object({
    budgetCategory: Yup.object().nullable().required(t("validation.required")), // Add required validation for Loại khoản
  });

  return (
    <GlobitsPopup open={open} noDialogContent title={formData.voucherType === 1 ? "Thu nhập mới" : "Chi phí mới"} onClosePopup={handleClose}>
      <Formik validationSchema={validationSchema} enableReinitialize initialValues={formData} onSubmit={(values) => handleSubmit(values)}>
        {({ isSubmitting }) => (
          <Form autoComplete='off'>
            <div className='dialog-body'>
              <DialogContent className='o-hidden'>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <GlobitsTextField label={t('voucherItem.note')} name={"note"} />
                  </Grid>
                  <Grid item xs={6}>
                    <GlobitsPagingAutocomplete
                      label={t('voucherItem.name')}
                      name={"budgetCategory"}
                      api={pagingBudgetCategory}
                      required // Mark "Loại khoản" as required in the UI
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <GlobitsVNDCurrencyInput  label={t('voucherItem.amount')}  name='amount' />
                  </Grid>
                </Grid>
              </DialogContent>
            </div>
            <div className='dialog-footer'>
              <DialogActions className='p-0'>
                <div className='flex flex-space-between flex-middle'>
                  <Button startIcon={<BlockIcon />} variant='contained' className='mr-12 btn btn-secondary d-inline-flex' color='secondary' onClick={() => handleClose()}>
                    {t("general.button.cancel")}
                  </Button>
                  <Button startIcon={<SaveIcon />} className='mr-0 btn btn-primary d-inline-flex' variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                    {t("general.button.save")}
                  </Button>
                </div>
              </DialogActions>
            </div>
          </Form>
        )}
      </Formik>
    </GlobitsPopup>
  );
}
