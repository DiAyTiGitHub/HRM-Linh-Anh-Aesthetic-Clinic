import { Button, ButtonGroup, Grid, makeStyles, Tooltip } from "@material-ui/core";
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import DeleteIcon from '@material-ui/icons/Delete';
import GroupIcon from '@material-ui/icons/Group';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import SaveOutlinedIcon from '@material-ui/icons/SaveOutlined';
import ConstantList from "app/appConfig";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import LocalConstants, { CodePrefixes } from "app/LocalConstants";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useLocation, useParams } from "react-router-dom/cjs/react-router-dom";
import * as Yup from "yup";
import CandidateTabContainer from "./CandidateCUTab/CandidateTabContainer";
import DuplicateStaffsCandidatesPopup from "./DuplicateStaffsCandidates/DuplicateStaffsCandidatesPopup";

const useStyles = makeStyles ((theme) => ({
  root:{
    "& .MuiAccordion-rounded":{
      borderRadius:"5px",
    },

    "& .MuiPaper-root":{
      borderRadius:"5px",
    },

    "& .MuiAccordionSummary-root":{
      borderRadius:"5px",
      // backgroundColor: "#EBF3F9",
      color:"#5899d1 ",
      fontWeight:"400",

      "& .MuiTypography-root":{
        fontSize:"1rem",
      },
    },

    "& .Mui-expanded":{
      "& .MuiAccordionSummary-root":{
        backgroundColor:"#EBF3F9",
        color:"#5899d1 ",
        // borderLeft: "3px solid rgba(88, 153, 202, 0.84) !important",
        fontWeight:"700",
        maxHeight:"50px !important",
        minHeight:"50px !important",
      },
      "& .MuiTypography-root":{
        fontWeight:700,
      },
    },

    "& .MuiButton-root":{
      borderRadius:"0.125rem !important",
    },
  },
}));

function CandidateCUIndex () {
  const location = useLocation ();
  const queryParams = new URLSearchParams (location?.search); // Parse the query string
  const isFromExamCandidate = queryParams.get ('isFromExamCandidate'); // Get the value of the 'isFromExamCandidate' parameter
  const isFromPassedCandidate = queryParams.get ('isFromPassedCandidate'); // Get the value of the 'isFromPassedCandidate' parameter
  const isFromWaitingJobCandidate = queryParams.get ('isFromWaitingJobCandidate'); // Get the value of the 'isFromWaitingJobCandidate' parameter
  const isFromNotComeCandidate = queryParams.get ('isFromNotComeCandidate'); // Get the value of the 'isFromNotComeCandidate' parameter
  const isFromOnboardedCandidate = queryParams.get ('isFromOnboardedCandidate'); // Get the value of the 'isFromOnboardedCandidate' parameter
  const isFromCandidatesInRecruitment = queryParams.get ('isFromCandidatesInRecruitment'); // Get the value of the 'isFromCandidatesInRecruitment' parameter
  const isFromCandidateRecruitmentRound = queryParams.get ('isFromCandidateRecruitmentRound'); // Get the value of the 'isFromCandidateRecruitmentRound' parameter
  const isFromStaffDetail = queryParams.get ('isFromStaffDetail');
  const {id:candidateId} = useParams ();

  const {candidateStore} = useStore ();
  const {t} = useTranslation ();
  const {
    handleClose,
    saveCandidate,
    selectedCandidate,
    openConfirmDeletePopup,
    handleConfirmDelete,
    resetStore,
    handleGetCandidateData,
    handleDelete,
    setTabCU,
    checkDuplicateCandidate,
  } = candidateStore;

  const classes = useStyles ();
  const candidateValidationSchema = Yup.object ({
    // Tab 1: Thông tin cá nhân
    lastName:Yup.string ()
        .ensure ()
        .required (t ("validation.required"))
        .matches (/^[^\d]*$/, "Dữ liệu không hợp lệ")
        .test ("validation_lastName", "Dữ liệu chứa ký tự đặc biêt", (value) => {
          var specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
          for (let i = 0; i < specialChars?.length; i++) {
            if (value.indexOf (specialChars[i]) > -1) {
              return false;
            }
          }
          return true;
        })
        .test ("length_lastName", "Dữ liệu không hợp lệ", (val) => val.length > 1)
        .nullable (),
    firstName:Yup.string ()
        .ensure ()
        .required (t ("validation.required"))
        .matches (/^[^\d]*$/, "Dữ liệu không hợp lệ")
        .test ("validation_firstName", "Dữ liệu chứa ký tự đặc biêt", (value) => {
          var specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
          for (let i = 0; i < specialChars?.length; i++) {
            if (value.indexOf (specialChars[i]) > -1) {
              return false;
            }
          }
          return true;
        })
        .test ("length_firstName", "Dữ liệu không hợp lệ", (val) => val.length > 1)
        .nullable (),
    phoneNumber:Yup.string ().max (11, "Không được nhập quá 11 ký tự").required (t ("validation.required")).nullable (),
    // tab 2
    // Conditional validation for interviewDate based on status
    // tab3
    candidateEducationalHistories:Yup.array ()
        .of (
            Yup.object ().shape ({
              startDate:Yup.date ()
                  .transform (function transformDate (castValue, originalValue) {
                    return originalValue? new Date (originalValue) : castValue;
                  })
                  .required (t ("validation.required"))
                  .typeError ("Ngày nhập học không đúng định dạng")
                  .nullable (),

              endDate:Yup.date ()
                  .test (
                      "is-greater",
                      "Ngày tốt nghiệp phải lớn ngày nhập học",
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
                  .typeError ("Ngày tốt nghiệp không đúng định dạng")
                  .nullable (),

              educationalInstitution:Yup.object ()
                  .required (t ("validation.required"))
                  .nullable (),
              country:Yup.object ().required (t ("validation.required")).nullable (),
              major:Yup.object ().required (t ("validation.required")).nullable (),
              educationType:Yup.object ().required (t ("validation.required")).nullable (),
              educationDegree:Yup.object ().required (t ("validation.required")).nullable (),
            })
        ).nullable (),
    candidateWorkingExperiences:Yup.array ()
        .of (
            Yup.object ().shape ({
              startDate:Yup.date ()
                  .transform (function transformDate (castValue, originalValue) {
                    return originalValue? new Date (originalValue) : castValue;
                  })
                  .required (t ("validation.required"))
                  .typeError ("Ngày bắt đầu không đúng định dạng")
                  .nullable (),

              endDate:Yup.date ()
                  .test (
                      "is-greater",
                      "Ngày kết thúc phải lớn ngày bắt đầu",
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
                  .typeError ("Ngày kết thúc không đúng định dạng")
                  .nullable (),
            })
        ).nullable (),
  });

  const history = useHistory ();

  async function handleSaveForm (values) {
    try {

      const savedCandidate = await saveCandidate(values);

      //link to page edit when staff created new
      if (!savedCandidate?.id) throw Error ("Error when saving candidate");

      let redirectUrl = ConstantList.ROOT_PATH + `candidate/` + savedCandidate?.id;
      if (isFromExamCandidate) {
        redirectUrl += "?isFromExamCandidate=true";
      } else if (isFromPassedCandidate) {
        redirectUrl += "?isFromPassedCandidate=true";
      } else if (isFromWaitingJobCandidate) {
        redirectUrl += "?isFromWaitingJobCandidate=true";
      } else if (isFromNotComeCandidate) {
        redirectUrl += "?isFromNotComeCandidate=true";
      } else if (isFromOnboardedCandidate) {
        redirectUrl += "?isFromOnboardedCandidate=true";
      } else if (isFromCandidatesInRecruitment) {
        redirectUrl += "?isFromCandidatesInRecruitment=true";
      } else if (isFromCandidateRecruitmentRound) {
        redirectUrl += "?isFromCandidateRecruitmentRound=true";
      } else if (isFromStaffDetail) {
        redirectUrl += "?isFromStaffDetail=true";
      }
      history.push (redirectUrl);
    } catch (error) {
      console.error (error);
    }

  }

  const [initialValues, setInitialValues] = useState (selectedCandidate);
  useEffect (function () {
    async function getCandidateData (candidateId) {
      await handleGetCandidateData (candidateId);
    }

    if (candidateId == "new-candidate") {
      getCandidateData (null);
    } else {
      getCandidateData (candidateId);
    }
    return resetStore;
  }, [candidateId]);
  useEffect (() => {
    if (selectedCandidate?.id) {
      setInitialValues (selectedCandidate);
    }
  }, [selectedCandidate]);

  const switchToTabWithError = (errors, values) => {
    const tabFields = [
      {
        index:0,
        fields:['firstName', 'lastName']
      },
      {
        index:1,
        fields:['candidateCode', 'position']
      }
    ];

    // console.log("errors", errors);

    // candidate's profile is approved => validate field interviewDate
    if (values?.status === LocalConstants.CandidateStatus.APPROVED.value) {
      tabFields[1].fields.push ('interviewDate');
    }

    for (let i = 0; i < tabFields.length; i++) {
      const hasErrorInTab = tabFields[i].fields.some ((field) => errors[field]);
      if (hasErrorInTab) {
        setTabCU (i); // Redirect to the tab that has error
        break;
      }
    }
  }

  async function handleConfirmDeleteCandidate () {
    try {
      const response = await handleConfirmDelete ();
      if (response)
        handleReturn ();
    } catch (error) {
      console.error (error);
    }
  }

  function handleReturn () {
    let redirectUrl = ConstantList.ROOT_PATH + `candidate`;
    if (isFromExamCandidate) {
      redirectUrl = ConstantList.ROOT_PATH + `exam-candidate`;
    } else if (isFromPassedCandidate) {
      redirectUrl = ConstantList.ROOT_PATH + `passed-candidate`;
    } else if (isFromWaitingJobCandidate) {
      redirectUrl = ConstantList.ROOT_PATH + `waiting-job-candidate`;
    } else if (isFromNotComeCandidate) {
      redirectUrl = ConstantList.ROOT_PATH + `not-come-candidate`;
    } else if (isFromOnboardedCandidate) {
      redirectUrl = ConstantList.ROOT_PATH + `onboarded-candidate`;
    } else if (isFromCandidatesInRecruitment) {
      redirectUrl = ConstantList.ROOT_PATH + `candidates-in-recruitment/${selectedCandidate?.recruitment?.id}`;
    } else if (isFromCandidateRecruitmentRound) {
      redirectUrl = ConstantList.ROOT_PATH + `recruitment-process/${selectedCandidate?.recruitment?.id}`;
    } else if (isFromStaffDetail) {
      redirectUrl = ConstantList.ROOT_PATH + `staff/edit/${selectedCandidate?.staff?.id}`;
    }

    history.push (redirectUrl);
    // history.goBack();
  }

  return (
      <>
        <div className="content-index">
          <div className="index-breadcrumb py-6">
            <GlobitsBreadcrumb routeSegments={[
              {name:"Tuyển dụng"},
              {name:t ("applicant.title")},
              {name:selectedCandidate?.displayName || "Ứng viên mới"}
            ]}/>

          </div>

          <Grid container spacing={2} className="index-card">
            <Grid item xs={12}>
              <Formik
                  validationSchema={candidateValidationSchema}
                  enableReinitialize
                  initialValues={initialValues}
                  onSubmit={handleSaveForm}
              >
                {({
                    isSubmitting,
                    values,
                    setFieldValue,
                    initialValues,
                    resetForm,
                    errors,
                    handleSubmit
                  }) => {

                  return (
                      <Form autoComplete="off" autocomplete="off">
                        <FormikFocusError/>

                        <Grid container spacing={2} className={classes.root}>
                          <Grid item xs={12}>
                            <ButtonGroup
                                color="container"
                                aria-label="outlined primary button group"
                            >
                              <Button
                                  // className="btn px-8 py-2 btn-info d-inline-flex mr-12"
                                  type="button"
                                  onClick={handleReturn}
                                  disabled={isSubmitting}
                              >
                                <ArrowBackIcon className="mr-6"/>
                                Quay lại
                              </Button>

                              {selectedCandidate?.id && (
                                  <Button
                                      // className="btn px-8 py-2 btn-danger d-inline-flex mr-12"
                                      type="button"
                                      disabled={isSubmitting}
                                      onClick={() => handleDelete (selectedCandidate)}
                                  >
                                    <DeleteIcon className="mr-6"/>
                                    Xóa ứng viên
                                  </Button>
                              )}

                              <Button
                                  // className="btn px-8 py-2 bg-light-gray d-inline-flex mr-12"
                                  type="button"
                                  onClick={() => resetForm ()}
                                  disabled={isSubmitting || JSON.stringify (values) === JSON.stringify (initialValues)}

                              >
                                <RotateLeftIcon className="mr-6"/>
                                Đặt lại
                              </Button>
                              {/*
                                                    {
                                                        !selectedCandidate?.id && ( */}
                              <Tooltip
                                  arrow
                                  title="Kiểm tra ứng viên liệu có trùng với nhân viên cũ"
                                  placement="top"
                              >
                                <Button
                                    // className="btn px-8 py-2 bg-light-gray d-inline-flex mr-12"
                                    type="button"
                                    onClick={() => checkDuplicateCandidate (values)}
                                    disabled={isSubmitting || JSON.stringify (values) === JSON.stringify (initialValues)}

                                >
                                  <GroupIcon className="mr-6"/>
                                  Kiếm tra trùng
                                </Button>
                              </Tooltip>
                              {/* )
                                                    } */}


                              <Button
                                  // className="btn px-8 py-2 btn-success d-inline-flex"
                                  // fullWidth
                                  type="submit"
                                  disabled={isSubmitting || JSON.stringify (values) === JSON.stringify (initialValues)}

                                  onClick={() => {
                                    switchToTabWithError (errors, values);
                                    handleSubmit ();
                                  }}
                              >
                                <SaveOutlinedIcon className="mr-6"/>
                                Lưu thông tin
                              </Button>
                            </ButtonGroup>
                          </Grid>

                          <Grid item xs={12}>
                            <CandidateTabContainer/>
                          </Grid>
                        </Grid>

                        {/* {openListDuplicate && ( */}
                        <DuplicateStaffsCandidatesPopup
                            values={values}
                            setFieldValue={setFieldValue}
                        />
                        {/* )} */}
                      </Form>
                  );
                }
                }
              </Formik>

            </Grid>
          </Grid>


          {openConfirmDeletePopup && (
              <GlobitsConfirmationDialog
                  open={openConfirmDeletePopup}
                  onConfirmDialogClose={handleClose}
                  onYesClick={handleConfirmDeleteCandidate}
                  title={t ("confirm_dialog.delete.title")}
                  text={"Bạn có chắc chắn muốn xóa ứng viên này?"}
                  agree={t ("confirm_dialog.delete.agree")}
                  cancel={t ("confirm_dialog.delete.cancel")}
              />
          )}
        </div>
      </>
  );
}

export default memo (observer (CandidateCUIndex));
