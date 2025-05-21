import { observer } from "mobx-react";
import React, { memo, useMemo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import ConstantList from "app/appConfig";
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import PieChartOutlinedIcon from '@material-ui/icons/PieChartOutlined';
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import FilterListIcon from '@material-ui/icons/FilterList';
import localStorageService from "app/services/localStorageService";
import ConfirmOvertimeFilter from "./ConfirmOvertimeFilter";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";

function ConfirmOvertimeToolbar () {
  const history = useHistory ();
  const {t} = useTranslation ();
  const {confirmOvertimeStore, hrRoleUtilsStore} = useStore ();

  const {
    pagingWorkScheduleResult,
    searchObject,
    handleSetSearchObject,
    minOTMinutes
  } = confirmOvertimeStore;

  function handlePreSubmit (values) {
    return {
      ... values,
      departmentId:values?.department?.id || null,
      organizationId:values?.organization?.id || null,
      staffId:values?.staff?.id || null,
      positionTitleId:values?.positionTitle?.id || null,
      salaryPeriodId:values?.salaryPeriod?.id || null,
    };
  }

  async function handleFilter (values) {
    const newSearchObject = {
      ... handlePreSubmit (values),
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    await pagingWorkScheduleResult ();
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

  const {
    hasShiftAssignmentPermission, isCompensationBenifit, isAdmin, isManager, hasRoleManageHCNS
  } = hrRoleUtilsStore;
  let canApproveOrUnApprove = false;
  if (isAdmin || isManager || isCompensationBenifit || hasShiftAssignmentPermission || hasRoleManageHCNS) {
    canApproveOrUnApprove = true;
  }
  return (
      <Formik
          enableReinitialize
          initialValues={JSON.parse (JSON.stringify (searchObject))}
          onSubmit={handleFilter}
      >
        {({resetForm, values, setFieldValue, setValues}) => {

          return (
              <Form autoComplete="off">
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <p className="m-0 p-0 borderThrough2">
                        {`Kết quả làm việc có thời gian làm việc trước ca hoặc sau ca vượt tối thiểu ${minOTMinutes} phút sẽ được thống
                        kê`}
                      </p>
                    </Grid>

                    <Grid
                        item
                        xs={12}
                    >
                      <Grid container spacing={1}>
                        <Grid item xs={12}>
                          <div className="flex justify-end align-center">
                            <div className="flex flex-center w-100">
                              <Grid container spacing={2}>
                                <Grid item xs={12} md={4}>
                                  <div className="flex items-center h-100 flex-end">
                                    <p className="no-wrap-text">
                                      <b>
                                        Kỳ lương:
                                      </b>
                                    </p>
                                  </div>
                                </Grid>

                                <Grid item xs={12} md={8}>
                                  <GlobitsPagingAutocompleteV2
                                      name='salaryPeriod'
                                      api={pagingSalaryPeriod}
                                      handleChange={(_, value) => {
                                        setFieldValue ("salaryPeriod", value);
                                        setFieldValue ("fromDate", value?.fromDate);
                                        setFieldValue ("toDate", value?.toDate);
                                      }}
                                  />
                                </Grid>
                              </Grid>
                            </div>
                            <div className="flex flex-center w-100">
                              <Grid container spacing={2}>
                                <Grid item xs={12} md={4}>
                                  <div className="flex items-center h-100 flex-end">
                                    <p className="no-wrap-text">
                                      <b>
                                        Từ ngày:
                                      </b>
                                    </p>
                                  </div>
                                </Grid>

                                <Grid item xs={12} md={8}>
                                  <GlobitsDateTimePicker
                                      // label="Từ ngày"
                                      name="fromDate"
                                      // placeholder="Ngày từ"
                                  />
                                </Grid>
                              </Grid>
                            </div>
                            <div className="flex flex-center w-100">
                              <Grid container spacing={2}>
                                <Grid item xs={12} sm={6} md={4}>
                                  <div className="flex items-center h-100 flex-end">
                                    <p className="no-wrap-text">
                                      <b>
                                        Đến ngày:
                                      </b>
                                    </p>
                                  </div>
                                </Grid>

                                <Grid item xs={12} sm={6} md={8}>
                                  <GlobitsDateTimePicker
                                      // label="Đến ngày"
                                      name="toDate"
                                      // placeholder="Đến ngày"
                                  />
                                </Grid>
                              </Grid>
                            </div>

                            <ButtonGroup
                                className="filterButtonV4"
                                color="container"
                                aria-label="outlined primary button group"
                            >
                              <Button
                                  startIcon={<SearchIcon className={``}/>}
                                  className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                  type="submit"
                              >
                                Tìm kiếm
                              </Button>
                              <Button
                                  startIcon={<FilterListIcon
                                      className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                                  className=" d-inline-flex py-2 px-8 btnHrStyle"
                                  onClick={handleTogglePopupFilter}
                              >
                                Bộ lọc
                              </Button>
                            </ButtonGroup>
                          </div>
                        </Grid>
                      </Grid>
                    </Grid>
                  </Grid>

                  <ConfirmOvertimeFilter
                      handleFilter={handleFilter}
                      isOpenFilter={isOpenFilter}
                      handleCloseFilter={handleCloseFilter}
                  />
                </Grid>
              </Form>
          );
        }}
      </Formik>
  );
}

export default memo (observer (ConfirmOvertimeToolbar));