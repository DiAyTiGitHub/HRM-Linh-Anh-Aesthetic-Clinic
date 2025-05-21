import React, { useEffect, useState } from "react";
import {
  Grid,
  DialogActions,
  Button,
  DialogContent,
} from "@material-ui/core";
import { Formik, Form } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import * as Yup from "yup";
import { pagingCategory } from "app/views/CivilServantCategory/CivilServantCategoryService";
import GlobitsPopup from "app/common/GlobitsPopup";

export default function StaffAllowanceSeniorityHistoryAddPopup(props) {
  const { t } = useTranslation();
  const { open, handleClose, item, handleSubmit, editable } = props;

  const initialItem = {
    startDate: null,
    quotaCode: null,
    percentReceived: null,
    note: "",
  };

  const validationSchema = Yup.object({
    startDate: Yup.date()
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày bắt đầu không đúng định dạng")
      .nullable(),

    quotaCode: Yup.object().required(t("validation.required")).nullable(),
    percentReceived: Yup.number()
      .min(0, "Giá trị phải lớn hơn bằng 0")
      .max(100, "Giá trị phải nhỏ hơn hoặc bằng 100")
      .required(t("validation.required"))
      .nullable(),
  });

  const [formValues, setFormValues] = useState(null);

  useEffect(() => {
    if (item) {
      setFormValues({ ...item });
    } else {
      setFormValues({ ...initialItem });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [item]);

  return (
    <GlobitsPopup
      open={open}
      onClosePopup={handleClose}
      title={
        <span className="mb-20">
          {editable ? t("general.button.add") : t("general.button.edit")}{" "}
          {t("allowanceSeniorityHistory.title")}
        </span>
      }
      noDialogContent
    >
      <Formik
        initialValues={formValues}
        onSubmit={(values) => handleSubmit(values)}
        validationSchema={validationSchema}
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <DialogContent
              className="dialog-body"
              style={{ maxHeight: "80vh", minWidth: "300px" }}
            >
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <GlobitsDateTimePicker
                    label={
                      <span>
                        {t("allowanceSeniorityHistory.startDate")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                    name="startDate"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsPagingAutocomplete
                    label={t("allowanceSeniorityHistory.quotaCode")}
                    requiredLabel
                    name="quotaCode"
                    api={pagingCategory}
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsTextField
                    name="percentReceived"
                    label={
                      <span>
                        {t("allowanceSeniorityHistory.percentReceived")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                    type="number"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsTextField
                    label={t("allowanceSeniorityHistory.note")}
                    name="note"
                    multiline
                    rows={3}
                  />
                </Grid>
              </Grid>
            </DialogContent>

            <DialogActions className="dialog-footer p-0">
              <div className="flex flex-space-between flex-middle">
                <Button
                  variant="contained"
                  className="mr-12 btn btn-secondary d-inline-flex"
                  color="secondary"
                  onClick={() => {
                    handleClose();
                  }}
                >
                  {t("general.button.close")}
                </Button>
                <Button
                  className="mr-0 btn btn-primary d-inline-flex"
                  variant="contained"
                  color="primary"
                  type="submit"
                  disabled={isSubmitting}
                >
                  {t("general.button.save")}
                </Button>
              </div>
            </DialogActions>
          </Form>
        )}
      </Formik>
    </GlobitsPopup>
  );
}
