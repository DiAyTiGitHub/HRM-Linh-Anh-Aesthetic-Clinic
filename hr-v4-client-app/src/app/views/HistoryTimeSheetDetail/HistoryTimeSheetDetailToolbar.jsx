import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import FilterListIcon from '@material-ui/icons/FilterList';
import SearchIcon from '@material-ui/icons/Search';
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import HistoryTimeSheetDetailFilter from "./HistoryTimeSheetDetailFilter";
import { useHistory } from "react-router-dom";
import * as Yup from "yup";
import moment from "moment/moment";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";
import GlobitsTextField from "app/common/form/GlobitsTextField";

function HistoryTimeSheetDetailToolbar ({oneStaff}) {
  const history = useHistory ();
  const {t} = useTranslation ();

  const {staffWorkScheduleStore, hrRoleUtilsStore} = useStore ();


  const {
    pagingStaffWorkSchedule,
    searchObject,
    handleSetSearchObject,
    handleGetTotalStaffWorkSchedule
  } = staffWorkScheduleStore;

  async function handleFilter (values) {
    const newSearchObject = {
      ... values,
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    await handleGetTotalStaffWorkSchedule ()
    await pagingStaffWorkSchedule ();
  }

  const [isOpenFilter, setIsOpenFilter] = useState (true);

  function handleCloseFilter () {
    if (isOpenFilter) {
      setIsOpenFilter (false);
    }
  }

  function handleOpenFilter () {
    if (!isOpenFilter) {
      setIsOpenFilter (true);
    }
  }

  function handleTogglePopupFilter () {
    if (isOpenFilter) handleCloseFilter ();
    else handleOpenFilter ();
  }

  const validationSchema = Yup.object ({
    fromDate:Yup.date ()
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        // .required(t("validation.required"))
        .typeError ("Từ ngày không đúng định dạng")
        .nullable (),

    toDate:Yup.date ()
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        // .required(t("validation.required"))
        .typeError ("Đến ngày không đúng định dạng")
        .nullable ()
        .test ("is-greater-or-equal", "Đến ngày phải lớn hơn hoặc bằng Từ ngày", function (value) {
          const {fromDate} = this.parent;
          if (fromDate && value) {
            return moment (value).isSameOrAfter (moment (fromDate), "date");
          }
          return true;
        }),
  });
  return (
      <Formik
          enableReinitialize
          initialValues={searchObject}
          validationSchema={validationSchema}
          onSubmit={handleFilter}
      >
        {({resetForm, values, setFieldValue, setValues}) => {
          return (
              <Form autoComplete='off'>
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={12} className='mt-10'>
                      <p className='m-0 p-0 borderThrough2'>Lọc và tìm kiếm</p>
                    </Grid>

                    <Grid item xs={12} lg={6} className='flex items-center' style={{width:"150px"}}>
                      <div className='flex items-center h-100 flex-end pr-10'>
                        <p className='no-wrap-text'>
                          <b>Kỳ lương:</b>
                        </p>
                      </div>
                      <div style={{width:"200px"}}>
                        <GlobitsPagingAutocompleteV2
                            name='salaryPeriod'
                            // label='Kỳ lương'
                            api={pagingSalaryPeriod}
                            handleChange={(_, value) => {
                              setFieldValue ("salaryPeriod", value);
                              // setFieldValue("salaryPeriodId", value?.id);
                              setFieldValue ("fromDate", value?.fromDate);
                              setFieldValue ("toDate", value?.toDate);
                            }}
                        />
                      </div>
                    </Grid>


                    <Grid item xs={12} lg={6}>
                      <div className='flex justify-between align-center'>
                        <Tooltip placement='top' title='Tìm kiếm theo từ khóa'>
                          <GlobitsTextField
                              placeholder='Tìm kiếm theo từ khóa'
                              name='keyword'
                              variant='outlined'
                              notDelay
                          />
                        </Tooltip>

                        <ButtonGroup
                            className='filterButtonV4'
                            color='container'
                            aria-label='outlined primary button group'>
                          <Button
                              startIcon={<SearchIcon className={``}/>}
                              className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                              type='submit'>
                            Tìm kiếm
                          </Button>
                          <Button
                              startIcon={
                                <FilterListIcon
                                    className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                />
                              }
                              className=' d-inline-flex py-2 px-8 btnHrStyle'
                              onClick={handleTogglePopupFilter}>
                            Bộ lọc
                          </Button>
                        </ButtonGroup>
                      </div>
                    </Grid>
                  </Grid>
                  <HistoryTimeSheetDetailFilter
                      handleFilter={handleFilter}
                      isOpenFilter={isOpenFilter}
                      handleCloseFilter={handleCloseFilter}
                      oneStaff={oneStaff}
                  />
                </Grid>
              </Form>
          );
        }}
      </Formik>
  );
}

export default memo (observer (HistoryTimeSheetDetailToolbar));