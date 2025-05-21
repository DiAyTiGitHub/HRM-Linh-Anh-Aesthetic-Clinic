import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import { useStore } from "../../stores";
import { pagingAllowance } from "../Allowance/AllowanceService";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPosition } from "../Position/PositionService";
import TabResultStaffs from "../Salary/SalaryResultBoardConfig/SalaryResultConfigTabs/TabResultStaffs";

function AllowancePolicyFilterCUForm() {
  const { t } = useTranslation();
  const { allowancePolicyStore } = useStore();

  const {
    handleClose,
    saveAllowancePolicy,
    pagingAllowancePolicy,
    selectedAllowancePolicy,
    openCreateEditPopup,
  } = allowancePolicyStore;

  const validationSchema = Yup.object({
    name: Yup.string().required(t("validation.required")).nullable(),
    code: Yup.string().required(t("validation.required")).nullable(),
    allowance: Yup.object().required(t("validation.required")).nullable(),
    formula: Yup.string().required(t("validation.required")).nullable(),

    startDate: Yup.date()
      .test("is-greater", "Ngày bắt đầu phải lớn thiết lập", function (value) {
        const { signedDate } = this.parent;
        if (signedDate && value) {
          return moment(value).isAfter(moment(signedDate), "date");
        }
        return true;
      })
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày bắt đầu không đúng định dạng")
      .nullable(),

    endDate: Yup.date()
      .test(
        "is-greater",
        "Ngày kết thúc phải lớn ngày bắt đầu",
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
      // .required(t("validation.required"))
      .typeError("Ngày kết thúc không đúng định dạng")
      .nullable()
  });

  async function handleSaveForm(values) {
    await saveAllowancePolicy(values);
    await pagingAllowancePolicy();
  }

  const [initialValues, setInitialValues] = useState(
    selectedAllowancePolicy
  );

  useEffect(function () {
    setInitialValues({
      ...selectedAllowancePolicy,
    });
  }, [selectedAllowancePolicy, selectedAllowancePolicy?.id]);


  return (
    <GlobitsPopupV2
      size="md"
      scroll={"body"}
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedAllowancePolicy?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.allowancePolicy.title")}
      onClosePopup={handleClose}
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
                  <Grid item xs={12} sm={6}>
                    <GlobitsTextField
                      validate
                      label={t("Tên")}
                      name="name"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <GlobitsTextField
                      validate
                      label={t("Mã")}
                      name="code"
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsPagingAutocompleteV2
                      name="organization"
                      label="Đơn vị"
                      api={pagingAllOrg}
                      value={values?.organization}
                      handleChange={(_, value) => {
                        setFieldValue("organization", value);
                        setFieldValue("department", null);
                        setFieldValue("position", null);
                      }}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsPagingAutocompleteV2
                      name="department"
                      label="Phòng ban"
                      value={values?.department}
                      api={pagingAllDepartments}
                      disabled={!values?.organization?.id}
                      searchObject={{
                        organizationId: values?.organization?.id,
                      }}
                      handleChange={(_, value) => {
                        setFieldValue("department", value);
                        setFieldValue("position", null);
                      }}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsPagingAutocompleteV2
                      name="position"
                      label="Vị trí"
                      value={values?.position}
                      api={pagingPosition}
                      disabled={!values?.department?.id}
                      searchObject={{
                        departmentId: values?.department?.id,
                      }}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsPagingAutocompleteV2
                      required
                      name="allowance"
                      label={"Phụ cấp"}
                      api={pagingAllowance}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsDateTimePicker
                      label={"Ngày bắt đầu"}
                      name="startDate"
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsDateTimePicker
                      label={"Ngày kết thúc"}
                      name="endDate"
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsTextField
                      label="Công thức/Giá trị tính toán"
                      name="formula"
                      multiline
                      rows={2}
                      required
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsTextField
                      label="Mô tả"
                      name="description"
                      multiline
                      rows={3}
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <div className="d-block">
                      <strong>
                        {t("Nhân viên thuộc chính sách")}
                      </strong>
                    </div>

                    <TabResultStaffs />
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
          );
        }
        }
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(AllowancePolicyFilterCUForm));
