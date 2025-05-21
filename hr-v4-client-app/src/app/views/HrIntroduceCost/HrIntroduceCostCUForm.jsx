import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";

function HrIntroduceCostFilterCUForm (props) {
  const {staffId = null, onSaved} = props;
  const {t} = useTranslation ();
  const {hrIntroduceCostStore} = useStore ();

  const {
    handleClose,
    saveHrIntroduceCost,
    pagingHrIntroduceCost,
    selectedHrIntroduceCost,
    openCreateEditPopup,
  } = hrIntroduceCostStore;

  const validationSchema = Yup.object ({
    introducePeriod:Yup.date ().transform (function transformDate (castValue, originalValue) {
      return originalValue? new Date (originalValue) : castValue;
    }).required (t ("validation.required")).nullable ().typeError ("Dữ liệu sai định dạng."),
    cost:Yup.number ().required (t ("validation.required")).nullable (),

  });

  async function handleSaveForm (values) {
    await saveHrIntroduceCost (values);
    if (onSaved) {
      onSaved ();
    } else {
      await pagingHrIntroduceCost (staffId);
    }
  }

  const [initialValues, setInitialValues] = useState (
      selectedHrIntroduceCost
  );

  useEffect (function () {
    setInitialValues ({
      ... selectedHrIntroduceCost,
    });
  }, [selectedHrIntroduceCost, selectedHrIntroduceCost?.id]);


  return (
      <GlobitsPopupV2
          size="md"
          scroll={"body"}
          open={openCreateEditPopup}
          noDialogContent
          title={(selectedHrIntroduceCost?.id? t ("general.button.edit") : t ("general.button.add")) + ' ' + t ("navigation.hrIntroduceCost.title")}
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
                <Form autoComplete="off">
                  <DialogContent className="dialog-body p-12">
                    <Grid container spacing={2}>
                      {!staffId &&
                          <Grid item xs={12} md={4}>
                            <ChooseUsingStaffSection
                                required
                                label={"Nhân viên giới thiệu"}
                                placeholder={""}
                            />
                          </Grid>
                      }
                      <Grid item xs={12} md={4}>
                        <ChooseUsingStaffSection
                            required
                            label={"Nhân viên được giới thiệu"}
                            placeholder={""}
                            name={"introducedStaff"}
                        />
                      </Grid>
                      <Grid item xs={12} md={4}>
                        <GlobitsDateTimePicker
                            label={"Ngày tính giới thiệu đợt 1"}
                            name="introducePeriod"
                        />
                      </Grid>

                      <Grid item xs={12} md={4}>
                        <GlobitsVNDCurrencyInput
                            label={"Chi phí được hưởng 1"}
                            name='cost'
                        />
                      </Grid>
                      <Grid item xs={12} md={4}>
                        <GlobitsDateTimePicker
                            label={"Ngày tính giới thiệu đợt 2"}
                            name="introducePeriod2"
                        />
                      </Grid>

                      <Grid item xs={12} md={4}>
                        <GlobitsVNDCurrencyInput
                            label={"Chi phí được hưởng 2"}
                            name='cost2'
                        />
                      </Grid>
                      <Grid item xs={12} md={4}>
                        <GlobitsDateTimePicker
                            label={"Ngày tính giới thiệu đợt 3"}
                            name="introducePeriod3"
                        />
                      </Grid>
                      <Grid item xs={12} md={4}>
                        <GlobitsVNDCurrencyInput
                            label={"Chi phí được hưởng 3"}
                            name='cost3'
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <GlobitsTextField
                            label="Ghi chú"
                            name="note"
                            multiline
                            rows={3}
                        />
                      </Grid>
                    </Grid>
                  </DialogContent>

                  <DialogActions className="dialog-footer px-12">
                    <div className="flex flex-space-between flex-middle">
                      <Button
                          variant="contained"
                          className="mr-12 btn btn-secondary d-inline-flex"
                          color="secondary"
                          disabled={isSubmitting}
                          onClick={handleClose}
                      >
                        {t ("general.button.close")}
                      </Button>
                      <Button
                          className="mr-0 btn btn-primary d-inline-flex"
                          variant="contained"
                          color="primary"
                          type="submit"
                          disabled={isSubmitting}
                      >
                        {t ("general.button.save")}
                      </Button>
                    </div>
                  </DialogActions>
                </Form>
            );
          }
          }
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (HrIntroduceCostFilterCUForm));
