import React, { memo } from "react";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import LocalConstants from "app/LocalConstants";
import GlobitsRadioGroup from "app/common/form/GlobitsRadioGroup";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";
import { convertToConstantFormat } from "app/common/CommonFunctions";

function EmployeeStatusForm() {
  const { t } = useTranslation();
  const { employeeStatusStore } = useStore();
  const { selectedEmployeeStatusEdit, onClosePopup, onSaveEmployeeStatus } = employeeStatusStore;

  const validationSchema = Yup.object({
    name: Yup.string().required(t("validation.required")).nullable(),
    code: Yup.string().required(t("validation.required")).nullable(),
    languageKey: Yup.string().required(t("validation.required")).nullable(),
  });

  const initialValues = {
    active: true,
    ...selectedEmployeeStatusEdit,
  };

  if (selectedEmployeeStatusEdit?.id && !selectedEmployeeStatusEdit?.active) {
    initialValues.active = false;
  }


  // console.log("checking initialvalues: ", initialValues)
  return (
    <GlobitsPopupV2
      open={Boolean(selectedEmployeeStatusEdit)}
      noDialogContent
      title={(selectedEmployeeStatusEdit?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("employeeStatus.title")}
      onClosePopup={onClosePopup}
      size="xs"
    >
      <Formik
        enableReinitialize
        validationSchema={validationSchema}
        initialValues={initialValues}
        onSubmit={onSaveEmployeeStatus}
      >
        {({ isSubmitting, values }) => (
          <Form autoComplete="off">
            <DialogContent className="o-hidden dialog-body p-12">
              <FormFields />
            </DialogContent>

            <DialogActions className="p-0 dialog-footer px-12 py-8">
              <Button
                startIcon={<BlockIcon />}
                variant="contained"
                className="mr-12 btn btn-secondary d-inline-flex"
                color="secondary"
                onClick={() => onClosePopup()}
              >
                {t("general.button.cancel")}
              </Button>

              <Button
                startIcon={<SaveIcon />}
                className="mr-0 btn btn-primary d-inline-flex"
                variant="contained"
                color="primary"
                type="submit"
                disabled={isSubmitting}
              >
                {t("general.button.save")}
              </Button>
            </DialogActions>
          </Form>
        )}
      </Formik>
    </GlobitsPopupV2>
  );
};

export default memo(observer(EmployeeStatusForm));


function FormFields() {
  const { values, setFieldValue } = useFormikContext();
  const { t } = useTranslation();

  function handleAutoRenderCode(e) {
    const value = e.target.value;
    setFieldValue("name", value);

    const autoRenderedCode = convertToConstantFormat(value);
    setFieldValue("code", autoRenderedCode);
  }

  return (
    <Grid container spacing={2}>

      <Grid item xs={12}>
        <GlobitsTextField
          label={t("civilServantType.name")}
          name="name"
          validate
          notDelay
          onChange={handleAutoRenderCode}
        />
      </Grid>

      <Grid item xs={12}>
        <GlobitsTextField
          label={t("civilServantType.code")}
          name="code"
          validate
          notDelay
        />
      </Grid>

      <Grid item xs={12}>
        <GlobitsTextField
          label={t("civilServantType.languageKey")}
          name="languageKey"
          validate
          notDelay
        />
      </Grid>

      {/* <Grid item xs={12}>
        <GlobitsSelectInput
                    label="Tình trạng"
                    name="active"
                    keyValue="value"
                    hideNullOption
                    options={LocalConstants.ListIsActiveOption}
                  />

        <GlobitsRadioGroup
          name="active"
          label={<span className="text-black">Kích hoạt</span>}
          options={LocalConstants.ListIsActiveOption}
        />
      </Grid> */}
    </Grid>
  );
}