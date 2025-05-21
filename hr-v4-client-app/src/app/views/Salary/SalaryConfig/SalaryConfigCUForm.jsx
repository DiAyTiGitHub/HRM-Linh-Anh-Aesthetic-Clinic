import React, { useState, useEffect, memo } from "react";
import { Formik, Form, Field } from "formik";
import { Grid, DialogActions, Button, DialogContent, makeStyles } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingSalaryType } from "../SalaryType/SalaryTypeService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import GlobitsAsyncAutocomplete from "app/common/form/GlobitsAsyncAutocomplete";
import { pagingAllDepartments } from "../../Department/DepartmentService";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function SalaryConfigCUForm() {
  const { salaryConfigStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    saveSalaryConfig,
    pagingSalaryConfig,
    selectedSalaryConfig,
    openCreateEditPopup
  } = salaryConfigStore;

  const validationSchema = Yup.object({
    // code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
  });

  async function handleSaveForm(values) {
    await saveSalaryConfig(values);
    await pagingSalaryConfig();
  }

  const [initialValues, setInitialValues] = useState(selectedSalaryConfig);

  useEffect(function () {
    const initData = {
      ...selectedSalaryConfig
    };
    if (!initData?.voided) {
      initData.voided = 0;
    }
    else {
      initData.voided = 1;
    }
    setInitialValues(initData);
  }, [selectedSalaryConfig, selectedSalaryConfig?.id]);

  return (
    <GlobitsPopupV2
      size="md"
      scroll={"body"}
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedSalaryConfig?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.salaryConfig.title")}
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
            <Form autoComplete="off" autocomplete="off">
              <DialogContent className="o-hidden p-12">
                <FormikFocusError />

                <Grid container spacing={2}>
                  {/* <Grid item xs={12} sm={6}>
                      <GlobitsTextField
                        validate
                        label="Mã thành phần"
                        name="code"
                        required
                      />
                    </Grid> */}

                  <Grid item xs={12} sm={6}>
                    <GlobitsTextField
                      validate
                      label="Tên thành phần lương"
                      name="name"
                      required
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsTextField
                      label="Tên khác"
                      name="otherName"
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsTextField
                      label="Mô tả"
                      name="description"
                    // multiline
                    // rows={3}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsTextField
                      type="number"
                      label="Giá trị mặc định"
                      name="defaultValue"
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsSelectInput
                      label="Trạng thái"
                      name="voided"
                      hideNullOption
                      options={LocalConstants.ListSalaryConfigStatus}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <GlobitsPagingAutocomplete
                      label="Nhóm dữ liệu "
                      name="salaryType"
                      api={pagingSalaryType}
                    />
                  </Grid>
                  {/* <Grid item xs={12}>
                      <SalaryConfigDepartmentTable />
                    </Grid> */}

                  <Grid item xs={12}>
                    <GlobitsAsyncAutocomplete
                      label={"Áp dụng cho"}
                      name="departments"
                      multiple
                      searchObject={{
                        pageIndex: 1,
                        pageSize: 10
                      }}
                      api={pagingAllDepartments}
                    // getOptionDisabled={function (option) {
                    //   return values?.id == option?.id || values?.parent?.id == option?.id
                    // }}
                    />
                  </Grid>
                </Grid>
              </DialogContent>

              <DialogActions className="dialog-footer px-12">
                <div className="flex flex-space-between flex-middle">
                  <Button
                    startIcon={<BlockIcon />}
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    onClick={handleClose}
                    disabled={isSubmitting}
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

export default memo(observer(SalaryConfigCUForm));