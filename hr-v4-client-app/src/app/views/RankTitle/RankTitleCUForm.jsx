import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsVNDCurrencyInput from "../../common/form/GlobitsVNDCurrencyInput";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";
import {CodePrefixes} from "../../LocalConstants";

function RankTitleCUForm ({readOnly}) {
  const {rankTitleStore} = useStore ();
  const {t} = useTranslation ();
  const {
    handleClose,
    saveRankTitle,
    pagingRankTitle,
    selectedRankTitle,
    openCreateEditPopup,
    openViewPopup,
    autoGenCode
  } = rankTitleStore;

  const validationSchema = Yup.object ({
    // code: Yup.string().required(t("validation.code")).nullable(),
    name:Yup.string ().required (t ("validation.name")).nullable (),
    shortName:Yup.string ().required (t ("validation.code")).nullable (),

  });

  async function handleSaveForm (values) {
    await saveRankTitle (values);
    await pagingRankTitle ();
  }

  const [initialValues, setInitialValues] = useState (selectedRankTitle);
  const autoGenCodeFunc = async () => {
    const code = await autoGenCode(CodePrefixes.CAP_BAC);
    if (code) {
      // Tạo object mới để tránh thay đổi trực tiếp state
      const updated = {...selectedRankTitle, ...{shortName:code}};
      setInitialValues(updated);
    }
  };
  useEffect(() => {
    if(!selectedRankTitle?.id){
      autoGenCodeFunc();
    }
  }, []);
  useEffect (function () {
    setInitialValues (selectedRankTitle);
  }, [selectedRankTitle, selectedRankTitle?.id]);
  const {isAdmin} = useStore ().hrRoleUtilsStore

  return (
      <GlobitsPopupV2
          scroll={"body"}
          size="xs"
          open={openCreateEditPopup || openViewPopup}
          noDialogContent
          title={openViewPopup? (t ("general.button.view") + ' ' + "cấp bậc") : (selectedRankTitle?.id? t ("general.button.edit") : t ("general.button.add")) + ' ' + "cấp bậc"}
          onClosePopup={handleClose}
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}
        >
          {({isSubmitting, values, setFieldValue, initialValues}) => {

            return (
                <Form autoComplete="off" autocomplete="off">
                  <div className="dialog-body">
                    <DialogContent className="p-12">
                      <FormikFocusError/>

                      <Grid container spacing={2}>
                        <Grid item xs={12}>
                          <GlobitsTextField
                              validate
                              label="Tên cấp bậc"
                              name="name"
                              required
                              readOnly={readOnly}
                          />
                        </Grid>

                        <Grid item xs={12}>
                          <GlobitsTextField
                              label="Mã cấp bậc"
                              name="shortName"
                              readOnly={readOnly}
                          />
                        </Grid>

                        <Grid item xs={12}>
                          <GlobitsTextField
                              type="number"
                              label="Level"
                              name="level"
                              readOnly={readOnly}
                          />
                        </Grid>
                        <Grid item xs={12}>
                          <GlobitsNumberInput
                              label="Lương đóng BHXH"
                              name="socialInsuranceSalary"
                              readOnly={readOnly}
                          />
                        </Grid>
                        <Grid item xs={12}>
                          <GlobitsNumberInput
                              label="Mức hưởng chi phí giới thiệu"
                              name="referralFeeLevel"
                              readOnly={readOnly}
                          />
                        </Grid>
                        <Grid item xs={12}>
                          <GlobitsTextField
                              label="Mô tả"
                              name="description"
                              multiline
                              rows={3}
                              readOnly={readOnly}
                          />
                        </Grid>
                      </Grid>

                    </DialogContent>
                  </div>

                  <div className="dialog-footer dialog-footer-v2 py-8">
                    <DialogActions className="p-0">
                      <div className="flex flex-space-between flex-middle">
                        <Button startIcon={<BlockIcon/>} variant='contained'
                                className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                color='secondary'
                                onClick={() => handleClose ()}>
                          {t ("general.button.cancel")}
                        </Button>
                        {((isAdmin) && !readOnly) && (
                            <Button startIcon={<SaveIcon/>}
                                    className='mr-0 btn btn-primary d-inline-flex'
                                    variant='contained' color='primary' type='submit'
                                    disabled={isSubmitting}>
                              {t ("general.button.save")}
                            </Button>
                        )}
                      </div>
                    </DialogActions>
                  </div>
                </Form>
            );
          }
          }
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (RankTitleCUForm));