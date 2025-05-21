import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import * as Yup from "yup";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingAllDepartments } from "../Department/DepartmentService";
import DetailTimePeriod from "./DetailTimePeriod";

function ShiftWorkForm(props) {
  const { t } = useTranslation();
  const { readOnly = false } = props;

  const {
    shiftWorkStore,
  } = useStore();

  const {
    selectedShiftWork,
    onSaveShiftWork,
    handleClose,
    openViewPopup,
    shouldOpenEditorDialog
  } = shiftWorkStore;


  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.required")).nullable(),
    name: Yup.string().required(t("validation.required")).nullable(),
    timePeriods: Yup.array()
      .test("has-at-least-one", "Ca làm việc phải có ít nhất 1 giai đoạn làm việc", function (value) {
        return value && value.length > 0;
      })
      .test("required-time-period", "Cần nhập đủ thời gian bắt đầu và thời gian kết thúc", function (value) {
        return !value?.some(item => !Boolean(item?.startTime) || !Boolean(item?.endTime));
      })
      .test("invalid-start-time", "Có thời gian bắt đầu không hợp lệ", function (value) {
        return !value?.some(item => {
          const start = new Date(item?.startTime);
          return isNaN(start.getTime());
        });
      })
      .test("invalid-end-time", "Có thời gian kết thúc không hợp lệ", function (value) {
        return !value?.some(item => {
          const end = new Date(item?.endTime);
          return isNaN(end.getTime());
        });
      })
      .test("check-startDate-endDate", "Thời gian kết thúc phải sau thời gian bắt đầu", function (value) {
        return !value?.some(item => {
          const start = new Date(item?.startTime);
          const end = new Date(item?.endTime);
          return (start.getHours() > end.getHours()) || (start.getHours() === end.getHours() && start.getMinutes() > end.getMinutes());
        });
      })
      .test("no-duplicate-code", "Mã giai đoạn không được trùng nhau", function (value) {
        const codes = value?.map(item => item.code);
        const uniqueCodes = new Set(codes);
        return uniqueCodes?.size === codes?.length;
      })
      .test("minWorkTimeHour-check", "'Thời gian tối thiểu để tính đã đi làm (giờ)' không được lớn hơn thời gian của giai đoạn", function (value) {
        return !value?.some(item => {
          if (!item?.startTime || !item?.endTime || item?.minWorkTimeHour == null) return false;

          const start = new Date(item.startTime);
          const end = new Date(item.endTime);
          const startMinutes = start.getHours() * 60 + start.getMinutes();
          const endMinutes = end.getHours() * 60 + end.getMinutes();

          const durationHours = (endMinutes - startMinutes) / 60;

          return item.minWorkTimeHour > durationHours;
        });
      })
      .nullable()
  });


  function handleFormSubmit(values, form) {
    let kt = 0;
    for (let i = 0; i < values.timePeriods.length; i++) {
      if (values.timePeriods[i].startTime && values.timePeriods[i].endTime) {
        kt = 1;
      } else {
        kt = 0;
      }
    }
    if (kt === 1) {
      onSaveShiftWork(values, form);
    } else {
      toast.warn("Cần nhập đủ thời gian bắt đầu và thời gian kết thúc");
    }
  }

  return (
    <GlobitsPopupV2
      open={shouldOpenEditorDialog || openViewPopup}
      title={
        openViewPopup
          ? t("Xem chi tiết") + " " + t("shiftWork.title") + " và giai đoạn trong ca"
          : (selectedShiftWork?.id ? t("general.button.edit") : t("general.button.add")) +
          " " +
          t("shiftWork.title") +
          " và giai đoạn trong ca"
      }
      onClosePopup={handleClose}
      noDialogContent
      scroll={"body"}
      size="md"
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={selectedShiftWork || {}}
        onSubmit={handleFormSubmit}
      >
        {({ isSubmitting, values }) => (
          <Form autoComplete="off">
            <FormikFocusError />

            <DialogContent className="o-hidden dialog-body p-12">
              <Grid container spacing={2}>
                <Grid item xs={12} className="pb-0">
                  <p className="m-0 p-0 borderThrough2">
                    Thông tin ca làm việc
                  </p>
                </Grid>

                <Grid item md={4} sm={6} xs={12}>
                  <GlobitsTextField
                    validate
                    label={t("shiftWork.code")}
                    name="code"
                    readOnly={readOnly}
                  />
                </Grid>

                <Grid item md={4} sm={6} xs={12}>
                  <GlobitsTextField
                    validate
                    label={t("shiftWork.name")}
                    name="name"
                    readOnly={readOnly}
                  />
                </Grid>

                <Grid item xs={12} className="pb-0">
                  <p className="m-0 p-0 borderThrough2">
                    Các phòng ban áp dụng ca làm việc
                  </p>
                </Grid>

                <Grid item xs={12}>
                  <GlobitsPagingAutocompleteV2
                    label={t("department.title")}
                    name="departments"
                    multiple
                    api={pagingAllDepartments}
                    getOptionDisabled={(option) => {
                      return option?.name && option?.code ? `${option.name} - ${option.code}` : option?.name || option?.code;
                    }}
                    getOptionLabel={(option) => {
                      return option?.name && option?.code ? `${option.name} - ${option.code}` : option?.name || option?.code;
                    }}
                    readOnly={readOnly}
                  />
                </Grid>

                <Grid item xs={12} className="pb-0">
                  <p className="m-0 p-0 borderThrough2">
                    Giai đoạn trong ca làm việc
                  </p>
                </Grid>

                {/*{values?.totalHours && (*/}
                <Grid item md={4} sm={6} xs={12}>
                  <GlobitsTextField
                    readOnly
                    label={t("shiftWork.totalHours") + " (giờ)"}
                    name="totalHours"
                  />
                </Grid>

                <Grid item md={4} sm={6} xs={12}>

                  <GlobitsVNDCurrencyInput
                    label="Số giờ công quy đổi"
                    name="convertedWorkingHours"
                    readOnly={readOnly}
                  />

                </Grid>
                {/*)}*/}

                <Grid item xs={12}>
                  <DetailTimePeriod readOnly={readOnly} />
                </Grid>
              </Grid>
            </DialogContent>

            {!readOnly && (
              <DialogActions className="dialog-footer flex flex-end flex-middle px-12">
                <Button
                  startIcon={<BlockIcon />}
                  className="btn btn-secondary d-inline-flex"
                  onClick={() => handleClose()}
                  disabled={isSubmitting}
                >
                  {t("general.button.cancel")}
                </Button>

                <Button
                  startIcon={<SaveIcon />}
                  className="ml-12 btn btn-primary d-inline-flex"
                  type="submit"
                  disabled={isSubmitting}
                >
                  {t("general.button.save")}
                </Button>
              </DialogActions>
            )}

          </Form>
        )}
      </Formik>
    </GlobitsPopupV2>
  );
};

export default memo(observer(ShiftWorkForm))