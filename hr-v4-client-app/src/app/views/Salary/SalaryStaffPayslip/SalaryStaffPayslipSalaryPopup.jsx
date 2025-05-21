import { Button, ButtonGroup, DialogActions, DialogContent, Grid, makeStyles, Table, TableBody, TableCell, TableHead, TableRow } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { FieldArray, Form, Formik, getIn, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocomplete from "../../../common/form/GlobitsPagingAutocomplete";
import { pagingSalaryPeriod } from "../SalaryPeriod/SalaryPeriodService";
import TabResultStaffs from "../SalaryResultBoardConfig/SalaryResultConfigTabs/TabResultStaffs";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import TableChartIcon from '@material-ui/icons/TableChart';
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import LocalConstants from "app/LocalConstants";
import { handleCalculateSalary, updateSalaryStaff } from "./SalaryStaffPayslipService";
import { Dialpad } from "@material-ui/icons";
import * as Yup from "yup";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { getSalaryTemplatesOfStaff } from "app/views/StaffSalaryTemplate/StaffSalaryTemplateService";
import SalaryResultStaffItemSection from "./SalaryResultStaffItemSection";
import { SalaryResultStaff } from "app/common/Model/Salary/SalaryResultStaff";

function SalaryStaffPayslipSalaryPopup(props) {
  const {
    salaryStaffPayslipStore
  } = useStore();

  const { t } = useTranslation();

  const {
    handleClose,
    openPopupSalary,
    handleUpdateSalaryStaff,
    pagingSalaryStaffPayslip,
  } = salaryStaffPayslipStore;

  const [formValues, setFormValues] = useState(new SalaryResultStaff());

  const handleCalculate = async (values) => {
    const { data } = await handleCalculateSalary(values);
    setFormValues(data)
  }

  const handleUpdate = async (values) => {
    const { data } = await updateSalaryStaff(values);
    setFormValues(data)
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

  const handleAfterSubmit = (setFieldValue) => {
    setFieldValue("salaryPeriod", null);
    setFieldValue("salaryTemplate", null);
    setFieldValue("salaryResultStaffItems", null);
  }

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size='md'
      open={openPopupSalary}
      noDialogContent
      title={"Tính lương nhân viên"}
      onClosePopup={() => {
        handleClose()
        pagingSalaryStaffPayslip()
      }}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={formValues}
        onSubmit={handleCalculate} 
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {


          return (
            <Form autoComplete='off' autocomplete='off'>
              <div className='dialog-body'>
                <DialogContent className="p-12">
                  <Grid container spacing={2}>

                    <Grid item xs={12} sm={4}>
                      <ChooseUsingStaffSection
                        label="Chọn nhân viên"
                        name="staff"
                        required
                        handleAfterSubmit={() => handleAfterSubmit(setFieldValue)}
                      />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                      <GlobitsPagingAutocompleteV2
                        label="Chọn kỳ lương"
                        name="salaryPeriod"
                        api={pagingSalaryPeriod}
                        displayData="name"
                        required
                        handleChange={(_, value) => {
                          setFieldValue("salaryPeriod", value);
                          setFieldValue("salaryTemplate", null);
                          setFieldValue("salaryResultStaffItems", null);
                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                      <GlobitsPagingAutocompleteV2
                        label="Sử dụng mẫu bảng lương"
                        name="salaryTemplate"
                        required
                        disabled={!values?.staff?.id || !values?.salaryPeriod?.id}
                        api={getSalaryTemplatesOfStaff}
                        searchObject={{
                          staffId: values?.staff?.id || null,
                          salaryPeriodId: values?.salaryPeriod?.id || null
                        }}
                        handleChange={(_, value) => {
                          setFieldValue("salaryTemplate", value);
                          setFieldValue("salaryResultStaffItems");
                        }}
                      />
                    </Grid>


                    <Grid
                      item
                      xs="auto"
                      className="flex align-end justify-end"
                    >
                      <ButtonGroup color="container" aria-label="outlined primary button group">
                        <Button startIcon={<TableChartIcon />} type="submit">
                          Tính lương
                        </Button>
                      </ButtonGroup>
                    </Grid>

                    <Grid item xs={12}>
                      <SalaryResultStaffItemSection />
                    </Grid>
                  </Grid>

                </DialogContent>
              </div>

              {/* <div className='dialog-footer py-8'>
                <DialogActions className='p-0'>
                  <div className='flex flex-space-between flex-middle'>
                    <Button
                      startIcon={<Dialpad />}
                      className='mr-12 btn btn-green d-inline-flex'
                      variant='contained'
                      color='primary'
                      onClick={() => handleUpdate(values)}
                      disabled={isSubmitting}
                    >
                      {t("Tính lại từ đầu")}
                    </Button>
                    <Button
                      startIcon={<BlockIcon />}
                      variant='contained'
                      className='mr-12 btn btn-secondary d-inline-flex'
                      color='secondary'
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("Đóng")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className='mr-0 btn btn-primary d-inline-flex'
                      variant='contained'
                      color='primary'
                      type='submit'
                      disabled={isSubmitting}
                    >
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div> */}
            </Form>
          );
        }}
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(SalaryStaffPayslipSalaryPopup));

