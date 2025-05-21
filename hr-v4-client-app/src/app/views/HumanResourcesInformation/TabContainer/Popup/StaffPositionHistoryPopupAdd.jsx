import React, { useEffect, useState } from "react";
import {
  Grid,
  DialogActions,
  Button,
  DialogContent,
} from "@material-ui/core";
import { Formik, Form, useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsRadioGroup from "app/common/form/GlobitsRadioGroup";
import * as Yup from "yup";
import moment from "moment";
import LocalConstants from "app/LocalConstants";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import { pagingAllDepartments, } from "app/views/Department/DepartmentService";
import GlobitsPopup from "app/common/GlobitsPopup";

export default function StaffPositionHistoryPopupAdd(props) {
  const { t } = useTranslation();
  const { open, handleClose, item, handleSubmit, editable } = props;
  const { values } = useFormikContext();

  const initialItem = {
    decisionCode: null,
    decisionDate: null,
    fromDate: null,
    toDate: null,
    position: null,
    department: null,
    allowanceCoefficient: null,
    note: "",
    current: false,
    connectedAllowanceProcess: false,
    positionSelect: "1",
  };

  const validationSchema = Yup.object({
    decisionCode: Yup.number()
      .min(0, "Giá trị phải lớn hơn hoặc bằng 0 !")
      .required(t("validation.required"))
      .nullable(),

    decisionDate: Yup.date()
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .typeError("Ngày bắt đầu không đúng định dạng")
      .nullable(true),

    fromDate: Yup.date()
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày bắt đầu không đúng định dạng")
      .nullable(),

    toDate: Yup.date()
      .test(
        "is-greater",
        "Ngày kết thúc phải lớn hơn ngày bắt đầu",
        function (value) {
          const { fromDate } = this.parent;
          if (fromDate && value) {
            return moment(value).isAfter(moment(fromDate), "date");
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

    department: Yup.object().required(t("validation.required")).nullable(),
    position: Yup.object().required(t("validation.required")).nullable(),
    allowanceCoefficient: Yup.number()
      .min(0, "Giá trị phải lớn hơn hoặc bằng 0")
      .required(t("validation.required"))
      .nullable(),
  });

  const [formValues, setFormValues] = useState(null);

  useEffect(() => {
    if (item) {
      setFormValues({ ...item });
    } else {
      setFormValues({ ...initialItem, position : values?.currentPosition?.title || null });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [item]);

  return (
    <GlobitsPopup
      open={open}
      onClosePopup={handleClose}
      noDialogContent
      title={
        <span className="mb-20">
          {editable ? t("general.button.add") : t("general.button.edit")}{" "}
          {t("trainingHistory.title")}
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
                <Grid item xs={12} md={6}>
                  <GlobitsTextField
                    validate
                    name="decisionCode"
                    label={t("positionHistory.decisionCode")}
                    type="number"
                  />
                </Grid>

                <Grid item xs={12} md={6}>
                  <GlobitsDateTimePicker
                    label={t("positionHistory.decisionDate")}
                    name="decisionDate"
                  />
                </Grid>

                <Grid item xs={12}>
                  <GlobitsDateTimePicker
                    required
                    label={t("positionHistory.fromDate")}
                    name="fromDate"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsDateTimePicker
                    required
                    label={t("positionHistory.toDate")}
                    name="toDate"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsPagingAutocomplete
                    label={t("positionHistory.department")}
                    name="department"
                    api={pagingAllDepartments}
                  />
                </Grid>

                <Grid item xs={12}>
                  <GlobitsPagingAutocomplete
                    name="position"
                    label={t("positionHistory.position")}
                    api={pagingPositionTitle}
                  />
                </Grid>

                <Grid item xs={12}>
                  <GlobitsTextField
                    validate
                    label={t("positionHistory.allowanceCoefficient")}
                    name="allowanceCoefficient"
                    type="number"
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsTextField
                    label={t("positionHistory.note")}
                    name="note"
                    multiline
                    rows={6}
                  />
                </Grid>

                <Grid item xs={12}>
                  <GlobitsCheckBox
                    label={t("positionHistory.current")}
                    name="current"
                  />
                </Grid>

                <Grid item xs={12}>
                  <GlobitsCheckBox
                    label={t("positionHistory.connectedAllowanceProcess")}
                    name="connectedAllowanceProcess"
                  />
                </Grid>

                <Grid item xs={12} md={12}>
                  <GlobitsRadioGroup
                    name="positionSelect"
                    keyValue="value"
                    options={LocalConstants.ListPosition}
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
