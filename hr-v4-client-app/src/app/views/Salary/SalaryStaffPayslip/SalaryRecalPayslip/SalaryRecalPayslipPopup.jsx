import { Button, ButtonGroup, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';
import { getSalaryTemplatesOfStaff } from "app/views/StaffSalaryTemplate/StaffSalaryTemplateService";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { pagingSalaryPeriod } from "../../SalaryPeriod/SalaryPeriodService";
import SalaryRecalPayslipUserFillSection from "./SalaryRecalPayslipUserFillSection";

function SalaryRecalPayslipPopup(props) {
  const {
    actionAfterSave
  } = props;

  const { t } = useTranslation();

  const {
    salaryStaffPayslipStore,
    salaryResultDetailStore
  } = useStore();

  const {
    getSalaryResultBoard,
  } = salaryResultDetailStore;

  const {
    handleClose,
    openRecalculatePayslip,
    handleCalculatePayslip,
    selectedStaffPayslip
  } = salaryStaffPayslipStore;

  async function handleSubmitForm(values) {
    try {
      const response = await handleCalculatePayslip(values);

      if (!response?.id) {
        throw new Error();
      }

      // if (response?.salaryResultId) {
      //   await getSalaryResultBoard(response?.salaryResultId);
      // }


      if (actionAfterSave) {
        console.log("catched")
        actionAfterSave();
      }


      // handleClose();
    }
    catch (error) {
      console.error(error);
    }
  }

  const validationSchema = Yup.object({
    salaryPeriod: Yup.object().required(t("validation.required")).nullable(),
    staff: Yup.object().required(t("validation.required")).nullable(),
    salaryResultStaffItems: Yup.array().of(
      Yup.object().shape({
        value: Yup.mixed()
          .nullable()
          .required("Giá trị không được để trống"),
      })
    ),
    salaryTemplate: Yup.object().required(t("validation.required")).nullable(),
  });

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size='md'
      open={openRecalculatePayslip}
      noDialogContent
      title={"Tính lương nhân viên"}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={selectedStaffPayslip}
        onSubmit={handleSubmitForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {


          return (
            <Form autoComplete='off' autocomplete='off'>
              <div className='dialog-body'>
                <DialogContent className="p-12">
                  <Grid container spacing={2}>

                    <Grid item xs={12} sm={4}>
                      <GlobitsPagingAutocompleteV2
                        name="staff"
                        label={t("Nhân viên")}
                        api={pagingStaff}
                        disabled
                        getOptionLabel={(option) =>
                          option?.displayName && option?.staffCode
                            ? `${option.displayName} - ${option.staffCode}`
                            : option?.displayName || option?.staffCode || ''
                        }
                      // onChange={(value) => handleFilter({ ...searchObject, staffId: value?.id })}
                      />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                      <GlobitsPagingAutocompleteV2
                        label="Chọn kỳ lương"
                        name="salaryPeriod"
                        api={pagingSalaryPeriod}
                        displayData="name"
                        disabled
                        required
                      />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                      <GlobitsPagingAutocompleteV2
                        label="Sử dụng mẫu bảng lương"
                        name="salaryTemplate"
                        required
                        disabled
                        api={getSalaryTemplatesOfStaff}
                        searchObject={{
                          staffId: values?.staff?.id || null,
                          salaryPeriodId: values?.salaryPeriod?.id || null
                        }}
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <SalaryRecalPayslipUserFillSection />
                    </Grid>
                  </Grid>

                </DialogContent>
              </div>

              <div className='dialog-footer py-8'>
                <DialogActions className='p-0'>
                  <div className='flex flex-space-between flex-middle'>
                    {/* <Button
                      startIcon={<BlockIcon />}
                      variant='contained'
                      className='mr-12 btn btn-secondary d-inline-flex'
                      color='secondary'
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button> */}

                    {/* <Tooltip placement="top" arrow title="Tính lại phiếu lương của nhân viên với các thông tin đã nhập">
                      <Button
                        startIcon={<MonetizationOnIcon />}
                        className="btn bgc-lighter-dark-blue d-inline-flex"
                        variant="contained"
                        color="primary"
                        type="submit"
                        disabled={isSubmitting}
                      >
                        Tính toán lại
                      </Button>
                    </Tooltip> */}

                    <Tooltip placement="top" arrow title="Tính lại phiếu lương của nhân viên với các thông tin đã nhập">
                      <Button
                        startIcon={<MonetizationOnIcon />}
                        className="ml-8 btn bgc-lighter-dark-blue d-inline-flex"
                        variant="contained"
                        color="primary"
                        type="submit"
                        disabled={isSubmitting}
                      >
                        Tính toán lại
                      </Button>
                    </Tooltip>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }}
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(SalaryRecalPayslipPopup));

