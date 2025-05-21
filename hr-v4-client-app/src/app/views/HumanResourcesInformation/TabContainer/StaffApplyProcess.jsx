import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import {
  Button,
  ButtonGroup, DialogActions, DialogContent,
  Grid
} from "@material-ui/core";
import { Form, Formik, useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { useParams } from "react-router";
import { getDate } from "app/LocalFunction";
import { useHistory } from "react-router-dom";
import GlobitsTable from "app/common/GlobitsTable";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import ConstantList from "app/appConfig";
import GlobitsBreadcrumb from "../../../common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import HrDocumentItemList from "../../HrDocumentItem/HrDocumentItemList";
import HrDocumentItemForm from "../../HrDocumentItem/HrDocumentItemForm";
import GlobitsConfirmationDialog from "../../../common/GlobitsConfirmationDialog";
import * as Yup from "yup";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import GlobitsSelectInput from "../../../common/form/GlobitsSelectInput";
import LocalConstants, { HrDocumentItemRequired } from "../../../LocalConstants";
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";
import { pagingHrDocumentTemplate } from "../../HrDocumentTemplate/HrDocumentTemplateService";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "../../../common/form/GlobitsDateTimePicker";
import { pagingRecruitment } from "../../Recruitment/Recruitment/RecruitmentService";
import { pagingAllOrg } from "../../Organization/OrganizationService";
import { pagingAllDepartments } from "../../Department/DepartmentService";
import { pagingPositionTitle } from "../../PositionTitle/PositionTitleService";
import GlobitsVNDCurrencyInput from "../../../common/form/GlobitsVNDCurrencyInput";
import ChooseUsingStaffSection from "../../User/UsingAccountStaff/ChooseUsingStaffSection";
import { Candidate } from "../../../common/Model/Candidate/Candidate";
import { pagingRecruitmentPlan } from "../../Recruitment/RecruitmentPlanV2/RecruitmentPlanV2Service";
import CheckIcon from "@material-ui/icons/Check";
import TabCandidateAttachment from "../../Candidate/CandidateCU/CandidateCUTab/TabCandidateAttachment";
import GlobitsRadioGroup from "../../../common/form/GlobitsRadioGroup";
import GlobitsCheckBox from "../../../common/form/GlobitsCheckBox";

function StaffApplyProcess () {
  const {t} = useTranslation ();
  const history = useHistory ();
  const {id} = useParams ();

  const {candidateStore, staffStore} = useStore ();

  const {
    getExistCandidateProfileOfStaff,
    candidateProfilesOfStaff,
    resetStore,
    openCreateCandidateForm,
    handleSetOpenCreateCandidateForm,
    handleDelete,
    handleConfirmDeleteNoPaging,
    openConfirmDeletePopup,
    setOpenConfirmDeletePopup
  } = candidateStore;

  const {
    tabIndexValue
  } = staffStore;

  useEffect (function () {
    resetStore ();
  }, [tabIndexValue]);

  useEffect (function () {
    if (id) {
      getExistCandidateProfileOfStaff (id);
    }
  }, []);

  let columns = [
    {
      title:t ("general.action"),
      minWidth:"100px",
      align:"center",
      render:(row) => {
        return (
            <div className="flex flex-middle justify-center">
              <Tooltip title="Chi tiết hồ sơ ứng viên" placement="top">
                <IconButton size="small" onClick={function () {
                  //link to new page like a staff
                  history.push (ConstantList.ROOT_PATH + `candidate/` + row?.id + "?isFromStaffDetail=true");
                }}>
                  <Icon fontSize='small' color='primary'>
                    edit
                  </Icon>
                </IconButton>
              </Tooltip>
              <Tooltip title="Xóa hồ sơ ứng viên" placement="top">
                <IconButton size='small' onClick={() => handleDelete (row)}>
                  <Icon fontSize='small' color='secondary'>
                    delete
                  </Icon>
                </IconButton>
              </Tooltip>
            </div>
        );
      }
    },
    {
      title:"Mã ứng viên",
      field:"candidateCode",
      align:"center",
      minWidth:"150px",
      render:row => <span>{(row?.candidateCode)}</span>
    },
    {
      title:"Đơn vị",
      field:"organization",
      align:"center",
      minWidth:"150px",
      render:row => <span>{(row?.organization?.name)}</span>
    },
    {
      title:"Phòng ban",
      field:"department",
      align:"center",
      minWidth:"150px",
      render:row => <span>{(row?.department?.name)}</span>
    },
    {
      title:"Vị trí",
      field:"postion",
      align:"center",
      minWidth:"150px",
      render:row => <span>{(row?.postion?.name)}</span>
    },
    {
      title:"Kế hoạch tuyển dụng",
      field:"recruitmentPlan.name",
      minWidth:"150px",
      align:"center",
    },
    {
      title:"Yêu cầu tuyển dụng",
      field:"recruitmentPlan.recruitmentRequest.name",
      align:"center",
      minWidth:"150px",
    },
    {
      title:"Tuyển mới",
      field:"custom",
      align:"center",
      minWidth:"150px",
      render:(rowData) => !rowData?.recruitmentPlan?.recruitmentRequest?.recruitmentRequestItem?.isReplacementRecruitment?
          <CheckIcon fontSize="small" style={{color:"green"}}/> : "",
    },
    {
      title:"Tuyển thay thế",
      field:"custom",
      align:"center",
      minWidth:"150px",
      render:(rowData) => rowData?.recruitmentPlan?.recruitmentRequest?.recruitmentRequestItem?.isReplacementRecruitment?
          <CheckIcon fontSize="small" style={{color:"green"}}/> : "",
    },
    {
      title:"Ngày nộp hồ sơ",
      field:"submissionDate",
      align:"center",
      minWidth:"150px",
      render:row => <span>{getDate (row?.submissionDate)}</span>
    },
    {
      title:"Nơi ở",
      field:"currentResidence",
      align:"center",
      minWidth:"150px",
      render:row => <span>{row?.currentResidence}</span>
    },
  ];
  const hadleConfirmDelete = async () => {
    await handleConfirmDeleteNoPaging ()
    setOpenConfirmDeletePopup (false)
    await getExistCandidateProfileOfStaff (id);
  }
  return (
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Formik
              enableReinitialize
          >
            {({resetForm, values, setFieldValue, setValues}) => (
                <Form autoComplete="off">
                  <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                      <ButtonGroup
                          color="container"
                          aria-label="outlined primary button group"
                      >
                        <Button
                            startIcon={<AddIcon/>}
                            type="button"
                            onClick={handleSetOpenCreateCandidateForm}
                        >
                          Thêm mới
                        </Button>
                      </ButtonGroup>
                    </Grid>
                  </Grid>
                </Form>
            )}
          </Formik>
        </Grid>
        <Grid item xs={12}>
          <GlobitsTable
              selection={false}
              data={candidateProfilesOfStaff}
              columns={columns}
              nonePagination
          />
        </Grid>
        {openCreateCandidateForm && (
            <StaffApplyProcessForm open={openCreateCandidateForm}/>
        )}
        {
            openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={() => {
                      setOpenConfirmDeletePopup (false)
                    }}
                    onYesClick={hadleConfirmDelete}
                    title={t ("confirm_dialog.delete.title")}
                    text={t ("confirm_dialog.delete.text")}
                    agree={t ("confirm_dialog.delete.agree")}
                    cancel={t ("confirm_dialog.delete.cancel")}
                />
            )
        }
      </Grid>
  );
}

export default memo (observer (StaffApplyProcess));

function StaffApplyProcessForm ({handleAfterSubmit, updateListOnClose, open}) {
  const history = useHistory ();
  const {values} = useFormikContext ()
  const {candidateStore} = useStore ();
  const {t} = useTranslation ();
  const {
    handleClose,
    saveCandidate,
    selectedCandidate,
  } = candidateStore;

  const [candidate, setCandidate] = useState (new Candidate ());

  const validationSchema = Yup.object ({
    candidateCode:Yup.string ().required (t ("validation.required")).nullable (),
    interviewDate:Yup.date ()
        .nullable ()
        .when ('approvalStatus', {
          is:LocalConstants.CandidateApprovalStatus.APPROVED.value,  // Validate only when approvalStatus equals APPROVED
          then:Yup.date ()
              .transform (function transformDate (castValue, originalValue) {
                return originalValue? new Date (originalValue) : castValue;
              })
              .required (t ("validation.required")).nullable ()
              .typeError ("Ngày phỏng vấn không đúng định dạng"),
          otherwise:Yup.date ().nullable (),  // If approvalStatus is not 2, no validation
        }),
    recruitmentPlan:Yup.object ().required (t ("validation.required")).nullable (),
  });

  useEffect (() => {
    setCandidate ((prevState) => ({
      ... prevState,
      firstName:values?.firstName,
      lastName:values?.lastName,
      displayName:values?.displayName,
      staff:values
    }));
  }, [selectedCandidate]);


  async function handleSaveForm (values) {
    try {
      const savedCandidate = await saveCandidate (values);
      if (!savedCandidate?.id) throw Error ("Error when saving candidate");

      history.push (ConstantList.ROOT_PATH + `candidate/` + savedCandidate?.id + "?isFromStaffDetail=true");
    } catch (error) {
      console.error (error);
    }
  }

  return (
      <GlobitsPopupV2
          open={open}
          size='md'
          noDialogContent
          title={
              t ("general.button.add") +
              " " +
              t ("Quá trình ứng tuyển")
          }
          onClosePopup={handleClose}>
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={candidate}
            onSubmit={(values) => handleSaveForm (values)}>
          {({isSubmitting, values, setFieldValue}) => (
              <Form autoComplete='off'>
                <div className='dialog-body'>
                  <DialogContent className='o-hidden p-12'>
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsTextField
                            label={t ("Mã ứng viên")}
                            name="candidateCode"
                            required
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsDateTimePicker
                            label={t ("Ngày nộp hồ sơ")}
                            name="submissionDate"
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("Kế hoạch tuyển dụng")}
                            name="recruitmentPlan"
                            api={pagingRecruitmentPlan}
                            handleChange={(_, value) => {
                              setFieldValue ("recruitmentPlan", value);
                              setFieldValue ("recruitmentRequest", value?.recruitmentRequest);
                              setFieldValue ("organization", value?.recruitmentRequest?.organization);
                              setFieldValue ("department", value?.recruitmentRequest?.hrDepartment);
                              setFieldValue ("positionTitle", value?.recruitmentRequest?.recruitmentRequestItem?.positionTitle);
                              setFieldValue ("recruitmentNew", !values?.recruitmentPlan?.recruitmentRequest?.recruitmentRequestItem?.isReplacementRecruitment);
                              setFieldValue ("replacementRecruitment", values?.recruitmentPlan?.recruitmentRequest?.recruitmentRequestItem?.isReplacementRecruitment);
                            }}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("Yêu cầu tuyển dụng")}
                            name="recruitmentRequest"
                            api={pagingRecruitmentPlan}
                            readOnly={true}
                        />
                      </Grid>
                      {values?.recruitmentNew && (
                          <Grid item xs={12} sm={6} md={4}>
                            <GlobitsCheckBox
                                label={t ("Tuyển mới")}
                                name="recruitmentNew"
                                readOnly={true}
                            />
                          </Grid>
                      )}
                      {values?.replacementRecruitment && (
                          <Grid item xs={12} sm={6} md={4}>
                            <GlobitsCheckBox
                                label={t ("Tuyển thay thế")}
                                name="replacementRecruitment"
                                readOnly={true}
                            />
                          </Grid>
                      )}

                      <Grid item md={4} sm={6} xs={12}>
                        <GlobitsPagingAutocompleteV2
                            name="organization"
                            label="Đơn vị"
                            api={pagingAllOrg}
                            disabled={values?.recruitment?.id}
                            handleChange={(_, value) => {
                              setFieldValue ("organization", value);
                              setFieldValue ("department", null);
                              setFieldValue ("positionTitle", null);
                            }}
                            readOnly={true}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("Phòng ban")}
                            name="department"
                            api={pagingAllDepartments}
                            handleChange={(_, value) => {
                              setFieldValue ("department", value);
                              setFieldValue ("positionTitle", null);
                            }}
                            searchObject={{
                              pageIndex:1,
                              pageSize:9999,
                              keyword:"",
                              organizationId:values?.organization?.id,
                            }}
                            disabled={values?.recruitment?.id}
                            readOnly={true}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsPagingAutocompleteV2
                            label={"Chức danh cần tuyển"}
                            validate
                            name="positionTitle"
                            api={pagingPositionTitle}
                            disabled={!values?.department}
                            searchObject={{
                              pageIndex:1,
                              pageSize:9999,
                              keyword:"",
                              departmentId:values?.department?.id,
                            }}
                            readOnly={true}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsVNDCurrencyInput
                            label={t ("Mức lương kỳ vọng (VNĐ)")}
                            name="desiredPay"
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsDateTimePicker
                            label={t ("Ngày có thể làm việc")}
                            name="possibleWorkingDate"
                        />
                      </Grid>

                      {values?.approvalStatus === LocalConstants.CandidateApprovalStatus.APPROVED.value && (
                          <Grid item xs={12} sm={6} md={4}>
                            <GlobitsDateTimePicker
                                label={t ("Ngày phỏng vấn/thi tuyển")}
                                name="interviewDate"
                                required
                            />
                          </Grid>
                      )}

                      {values?.approvalStatus === LocalConstants.CandidateApprovalStatus.APPROVED.value &&
                          values?.examStatus === LocalConstants.CandidateExamStatus.PASSED.value &&
                          values?.receptionStatus === LocalConstants.CandidateReceptionStatus.RECEPTED.value && (
                              <Grid item xs={12} sm={6} md={4}>
                                <GlobitsDateTimePicker
                                    label={t ("Ngày nhận việc")}
                                    name="onboardDate"
                                    required
                                />
                              </Grid>
                          )}

                      <Grid item xs={12} sm={6} md={4}>
                        <ChooseUsingStaffSection
                            label="Người giới thiệu"
                            placeholder="Người giới thiệu"
                            name="introducer"
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <TabCandidateAttachment/>
                      </Grid>
                    </Grid>
                  </DialogContent>
                </div>
                <div className='dialog-footer'>
                  <DialogActions className='p-0'>
                    <div className='flex flex-space-between flex-middle'>
                      <Button
                          startIcon={<BlockIcon/>}
                          variant='contained'
                          className='mr-12 btn btn-secondary d-inline-flex'
                          color='secondary'
                          onClick={() => handleClose ()}>
                        {t ("general.button.cancel")}
                      </Button>
                      <Button
                          startIcon={<SaveIcon/>}
                          className='mr-0 btn btn-primary d-inline-flex'
                          variant='contained'
                          color='primary'
                          type='submit'
                          disabled={isSubmitting}>
                        {t ("general.button.save")}
                      </Button>
                    </div>
                  </DialogActions>
                </div>
              </Form>
          )}
        </Formik>
      </GlobitsPopupV2>
  );
};
