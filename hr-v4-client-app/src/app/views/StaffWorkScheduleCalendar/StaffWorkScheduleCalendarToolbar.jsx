import {
  Button,
  ButtonGroup,
  Grid
} from "@material-ui/core";
import PieChartOutlinedIcon from '@material-ui/icons/PieChartOutlined';
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import * as Yup from "yup";
import StaffWorkScheduleCalendarFilter from "./StaffWorkScheduleCalendarFilter";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";


function StaffWorkScheduleIndexToolbar() {
  const { t } = useTranslation();
  const history = useHistory();

  const {
    staffWorkScheduleCalendarStore,
    staffWorkScheduleStore,
    hrRoleUtilsStore
  } = useStore();
  const {
    isAdmin,
    hasShiftAssignmentPermission,
    isCompensationBenifit,
  } = hrRoleUtilsStore;
  const {
    handleSetSearchObject,
    getWorkingScheduleByFilter,
    searchObject,
  } = staffWorkScheduleCalendarStore;

  const {
    handleOpenAssignForm

  } = staffWorkScheduleStore;

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await getWorkingScheduleByFilter();
  }

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
        "Ngày kết thức phải lớn honw ngày bắt đầu",
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

  return (
    <Formik
      validationSchema={validationSchema}
      enableReinitialize
      initialValues={searchObject}
      onSubmit={handleFilter}
    >
      {({ resetForm, values, setFieldValue, setValues }) => {

        return (
          <Form autoComplete="off">
            <Grid container spacing={2}>
              {(hasShiftAssignmentPermission) && (
                  <Grid item xs={12} lg={4}>
                    <ButtonGroup
                      color="container"
                      aria-label="outlined primary button group"
                    >
                      <Button
                        type="button"
                        startIcon={<PieChartOutlinedIcon />}
                        onClick={() => handleOpenAssignForm()}
                      >
                        Phân ca làm việc
                      </Button>
                    </ButtonGroup>
                  </Grid>
                )}

              <Grid item xs={12}>

                <StaffWorkScheduleCalendarFilter
                  handleFilter={handleFilter}
                />

              </Grid>
            </Grid>

          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(StaffWorkScheduleIndexToolbar));