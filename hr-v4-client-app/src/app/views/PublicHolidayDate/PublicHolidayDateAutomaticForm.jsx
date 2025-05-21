import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import moment from "moment";
import AutorenewIcon from '@material-ui/icons/Autorenew';
import { getFirstDateOfMonth, getLastDateOfMonth } from "app/LocalFunction";

function PublicHolidayDateAutomaticForm() {
  const { publicHolidayDateStore } = useStore();
  const { t } = useTranslation();

  const {
    handleClosePopupAutomatic,
    pagingPublicHolidayDate,
    selectedPublicHolidayDate,
    openPopupAutomatic,
    createPublicHolidayDateAutomatic
  } = publicHolidayDateStore;

  const validationSchema = Yup.object({
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
  });

  async function handleSaveForm(values) {
    await createPublicHolidayDateAutomatic(values);
    await pagingPublicHolidayDate();
  }

  const [initialValues, setInitialValues] = useState(
    selectedPublicHolidayDate
  );

  useEffect(function () {
    setInitialValues({
      ...selectedPublicHolidayDate,
      fromDate: getFirstDateOfMonth(),
      toDate: getLastDateOfMonth(),
    });
  }, []);

  return (
    <GlobitsPopupV2
      size="xs"
      scroll={"body"}
      open={openPopupAutomatic}
      noDialogContent
      title={t("Tự động tạo ngày nghỉ")}
      onClosePopup={handleClosePopupAutomatic}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleSaveForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {

          return (
            <Form autoComplete="off">
              <DialogContent className="o-hidden p-12">
                <Grid container spacing={2}>

                  <Grid item xs={12}>
                    <GlobitsDateTimePicker
                      validate
                      required
                      label={t("Từ ngày")}
                      name="fromDate"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <GlobitsDateTimePicker
                      validate
                      required
                      label={t("Đến ngày")}
                      name="toDate"
                    />
                  </Grid>
                  {/* <>List Date Holiday</> */}
                </Grid>
              </DialogContent>

              <DialogActions className="dialog-footer px-12">
                <div className="flex flex-space-between flex-middle">
                  <Button
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    disabled={isSubmitting}
                    onClick={handleClosePopupAutomatic}
                  >
                    {t("general.button.close")}
                  </Button>
                  <Button
                    className="mr-0 btn btn-primary d-inline-flex"
                    variant="contained"
                    color="primary"
                    type="submit"
                    disabled={isSubmitting}
                    startIcon={<AutorenewIcon />}
                  >
                    {/* {t("general.button.save")} */}
                    {t("Tạo các ngày nghỉ")}
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

export default memo(observer(PublicHolidayDateAutomaticForm));
