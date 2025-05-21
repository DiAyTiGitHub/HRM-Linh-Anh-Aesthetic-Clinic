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
import { pagingAllowanceTypes } from "app/views/AllowanceType/AllowanceTypeService";
import * as Yup from "yup";
import moment from "moment";
import GlobitsPopup from "app/common/GlobitsPopup";

export default function StaffAllowanceHistoryAddPopup(props) {
  const { t } = useTranslation();
  const { open, handleClose, item, handleSubmit, editable } = props;

  const initialItem = {
    startDate: null,
    endDate: null,
    allowanceType: null,
    coefficient: null,
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

    endDate: Yup.date()
      .test(
        "is-greater",
        "Ngày kết thức phải lớn ngày bắt đầu",
        function (value) {
          const { startDate } = this.parent;
          if (startDate && value) {
            return moment(value).isAfter(moment(startDate), "date");
          }
          return true;
        }
      )
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày kết thúc không đúng định dạng")
      .nullable(),

    allowanceType: Yup.object().required(t("validation.required")).nullable(),
    coefficient: Yup.number()
      .min(0, "Giá trị phải lớn hơn hoặc bằng 0")
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
      noDialogContent
      open={open}
      onClosePopup={handleClose}
      title={
        <span className="mb-20">
          {editable ? t("general.button.add") : t("general.button.edit")}{" "}
          {t("allowanceHistory.title")}
        </span>
      }
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
                        {t("allowanceHistory.startDate")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                    name="startDate"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsDateTimePicker
                    label={
                      <span>
                        {t("allowanceHistory.endDate")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                    name="endDate"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsPagingAutocomplete
                    name="allowanceType"
                    label={t("allowanceHistory.allowanceType")}
                    requiredLabel
                    api={pagingAllowanceTypes}
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsTextField
                    label={
                      <span>
                        {t("allowanceHistory.coefficient")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                    name="coefficient"
                    type="number"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsTextField
                    label={t("allowanceHistory.note")}
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
