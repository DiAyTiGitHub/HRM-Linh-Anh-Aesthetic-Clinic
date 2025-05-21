import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import {
  Button,
  ButtonGroup,
  Grid,
} from "@material-ui/core";
import { Form, Formik } from "formik";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
import { useParams } from "react-router";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import moment from "moment";
import StaffWorkAssignmentTable from "./StaffWorkAssignmentTable";
import SearchIcon from '@material-ui/icons/Search';
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";
import InsertInvitationIcon from '@material-ui/icons/InsertInvitation';
import Web from "@material-ui/icons/Web";
import ShiftRegistrationFormPopup from "app/views/StaffMonthSchedule/ShiftRegistrationFormPopup";
import { getInitialStaffWorkScheduleFilter } from "../../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryPeriod } from "../../Salary/SalaryPeriod/SalaryPeriodService";

function StaffShiftWorkAssignment () {

  const {t} = useTranslation ();
  const history = useHistory ();

  const {id} = useParams ();

  const {
    staffWorkScheduleStore,
    staffStore,
    ShiftRegistrationStore
  } = useStore ();

  const {
    handleSetSearchObject,
    pagingStaffWorkSchedule,
    handleDeleteList,
    listOnDelete,
    searchObject,
    openConfirmDeleteListPopup,
    handleClose,
    handleConfirmDeleteList,
    resetStore,
  } = staffWorkScheduleStore;

  const {
    openFormShiftRegristration,
    handleOpenFormShiftRegristration
  } = ShiftRegistrationStore;

  const {
    tabIndexValue
  } = staffStore;

  const validationSchema = Yup.object ({
    fromDate:Yup.date ()
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        .required (t ("validation.required"))
        .typeError ("Ngày bắt đầu không đúng định dạng")
        .nullable (),

    toDate:Yup.date ()
        .test (
            "is-greater",
            "Ngày kết thức phải lớn honw ngày bắt đầu",
            function (value) {
              const {startDate} = this.parent;
              if (startDate && value) {
                return moment (value).isAfter (moment (startDate), "date");
              }
              return true;
            }
        )
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        .required (t ("validation.required"))
        .typeError ("Ngày kết thúc không đúng định dạng")
        .nullable (),
  });

  async function handleFilterShiftWork (values) {
    const newSearchObject = {
      ... values,
      pageIndex:1,
      staffId:id,
      staff:{
        id:id
      }
    };
    handleSetSearchObject (newSearchObject);
    await pagingStaffWorkSchedule ();
  }

  // useEffect(function () {
  //     resetStore();
  // }, [tabIndexValue]);
  async function initalizeScreen () {
    try {
      const {data} = await getInitialStaffWorkScheduleFilter ();

      handleSetSearchObject (
          {
            ... searchObject,
            ... data,
            staff:{
              id:id
            }
          }
      );

      await pagingStaffWorkSchedule ();
    } catch (error) {
      console.error (error);
    }
  }

  useEffect (function () {
    initalizeScreen ();
  }, []);

  function redirectToScheduleCalendar () {
    const routePath = ConstantList.ROOT_PATH + "staff-month-schedule-calendar/" + id;

    history.push (routePath);
  }

  return (
      <>
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilterShiftWork}
        >
          {({isSubmitting, values, setFieldValue}) => {
            // console.log("values", values);

            console.log ("values", values);
            console.log ("id", id);

            return (
                <Form autoComplete="off">
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <Grid container spacing={2}>
                        <Grid item xs={12} xl={4}>
                          <ButtonGroup
                              color="container"
                              aria-label="outlined primary button group"
                          >
                            <Button
                                type="button"
                                startIcon={<InsertInvitationIcon/>}
                                onClick={redirectToScheduleCalendar}
                            >
                              Lịch làm việc
                            </Button>

                            {/* <Button
                                                    // disabled={listOnDelete?.length <= 0}
                                                    startIcon={<Web />}
                                                    onClick={() => handleOpenFormShiftRegristration()}
                                                >
                                                    Đăng ký ca làm việc
                                                </Button> */}

                            <Button
                                disabled={listOnDelete?.length <= 0}
                                startIcon={<DeleteOutlineIcon/>}
                                onClick={handleDeleteList}
                            >
                              {t ("general.button.delete")}
                            </Button>
                          </ButtonGroup>
                        </Grid>

                        <Grid item xs={12} xl={8}>
                          <Grid container spacing={1}>
                            <Grid item xs={12}>
                              <div className="flex justify-end align-center">
                                <div className="flex flex-center w-100">
                                  <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6} md={4}>
                                      <div className="flex items-center h-100 flex-end">
                                        <p className="no-wrap-text">
                                          <b>
                                            Kỳ lương:
                                          </b>
                                        </p>
                                      </div>
                                    </Grid>
                                    <Grid item xs={12} sm={6} md={8}>
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
                                    </Grid>
                                  </Grid>
                                </div>
                                <div className="flex flex-center w-100">
                                  <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6} md={4}>
                                      <div className="flex items-center h-100 flex-end">
                                        <p className="no-wrap-text">
                                          <b>
                                            Từ ngày:
                                          </b>
                                        </p>
                                      </div>
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={8}>
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
                                      onClick={() => handleFilterShiftWork (values)}
                                  >
                                    Tìm kiếm
                                  </Button>
                                </ButtonGroup>
                              </div>
                            </Grid>
                          </Grid>
                        </Grid>
                      </Grid>
                    </Grid>

                    <Grid item xs={12}>
                      <StaffWorkAssignmentTable/>
                    </Grid>

                  </Grid>
                </Form>
            );
          }}

        </Formik>

        {openFormShiftRegristration && (
            <ShiftRegistrationFormPopup
                staffId={id}
            />
        )}

        {openConfirmDeleteListPopup && (
            <GlobitsConfirmationDialog
                open={openConfirmDeleteListPopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDeleteList}
                title={t ("confirm_dialog.delete_list.title")}
                text={t ("confirm_dialog.delete_list.text")}
                agree={t ("confirm_dialog.delete_list.agree")}
                cancel={t ("confirm_dialog.delete_list.cancel")}
            />
        )}
      </>
  );
}

export default memo (observer (StaffShiftWorkAssignment));
