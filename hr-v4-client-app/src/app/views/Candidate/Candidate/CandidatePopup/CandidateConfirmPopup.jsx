import { observer } from "mobx-react";
import React, { memo } from "react";
import { Button, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { formatDate } from "app/LocalFunction";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants, { CandidateStatus } from "app/LocalConstants";

function CandidateConfirmPopup (props) {
  const {type} = props;

  const {candidateStore} = useStore ();
  const {t} = useTranslation ();
  const {
    openApprovePopup,
    handleClose,
    getApprovalStatus,
    handleRemoveActionItem,
    listOnDelete,
    handleSelectListDelete,
    openRejectPopup,
    openScreenedPassPopup,
    openNotScreenedPopup,
    handleConfirmApproveCandidate,
    handleConfirmScreenedPassCandidate,
    handleConfirmNotScreenedCandidate,
    handleConfirmRejectCandidate,
  } = candidateStore;

  const columns = [
    {
      title:"Mã ứng viên",
      field:"candidateCode",
    },
    {
      title:"Họ tên",
      field:"displayName",
    },
    {
      title:"Ngày sinh",
      field:"birthDate",
      render:(rowData) => <span>{rowData?.birthDate && formatDate ("DD/MM/YYYY", rowData?.birthDate)}</span>,
    },
    {
      title:"Đơn vị tuyển dụng",
      field:"organization.name",
      align:"left",
      minWidth:"150px",
    },
    {
      title:"Phòng ban tuyển dụng",
      field:"department.name",
      align:"left",
      minWidth:"150px",
    },
    {
      title:"Vị trí tuyển dụng",
      field:"positionTitle.name",
      align:"left",
      minWidth:"150px",
    },
    {
      title:"Trạng thái hiện tại",
      field:"status",
      render:function (applicant) {
        return <span>{LocalConstants.CandidateStatus.getNameByValue (applicant.status)}</span>;
      },
    },
    {
      title:t ("general.action"),
      width:"6%",
      align:"center",
      render:(rowData) => {
        return (
            <div className='flex flex-middle w-100 justify-center'>
              <Tooltip title='Loại bỏ' placement='top'>
                <IconButton className='' size='small' onClick={() => handleRemoveActionItem (rowData?.id)}>
                  <Icon fontSize='small' color='secondary'>
                    delete
                  </Icon>
                </IconButton>
              </Tooltip>
            </div>
        );
      },
    },
  ];

  const getValidationSchema = (type, t) => {
    return Yup.object ({
      interviewDate:Yup.date ()
          .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
          .nullable ()
          .test ("required-if-approved", t ("validation.required"), function (value) {
            if (type === CandidateStatus.APPROVED.value) {
              return value != null;
            }
            return true;
          }),

      // refusalReason:Yup.string ()
      //     .nullable ()
      //     .test ("required-if-rejected-or-not-screened", t ("validation.required"), function (value) {
      //       if (type === CandidateStatus.REJECTED.value || type === CandidateStatus.NOT_SCREENED.value) {
      //         return !!value;
      //       }
      //       return true;
      //     }),
    });
  };
  const validationSchema = getValidationSchema (type, t);

  const initialValues = {
    interviewDate:null,
    refusalReason:null,
  };

  const getStatusLabel = (type) => {
    switch (type) {
      case CandidateStatus.SCREENED_PASS.value:
        return "Đã sơ lọc";
      case CandidateStatus.NOT_SCREENED.value:
        return "Không qua sơ lọc";
      case CandidateStatus.APPROVED.value:
        return "Đã duyệt";
      case CandidateStatus.REJECTED.value:
        return "Đã từ chối";
      default:
        return "Không xác định";
    }
  };

  const handleConfirm = async (values) => {
    if (type === CandidateStatus.APPROVED.value) {
      await handleConfirmApproveCandidate (values);
    } else if (type === CandidateStatus.SCREENED_PASS.value) {
      await handleConfirmScreenedPassCandidate (values);
    } else if (type === CandidateStatus.NOT_SCREENED.value) {
      await handleConfirmNotScreenedCandidate (values);
    } else if (type === CandidateStatus.REJECTED.value) {
      await handleConfirmRejectCandidate (values);
    }
  };
  return (
      <GlobitsColorfulThemePopup
          open={openApprovePopup || openRejectPopup || openScreenedPassPopup || openNotScreenedPopup}
          handleClose={handleClose}
          hideFooter
          size='lg'
          onConfirm={handleConfirm}>
        <div className='dialog-body'>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={8} md={9}>
              <div className='dialogScrollContent'>
                <h6 className='text-red'>
                  <strong>
                    {listOnDelete?.length <= 1? "Thông tin " : "Danh sách "}
                    ứng viên được chọn {getStatusLabel (type).toUpperCase ()}
                  </strong>
                </h6>
                <GlobitsTable
                    data={listOnDelete}
                    handleSelectList={handleSelectListDelete}
                    columns={columns}
                    nonePagination
                />
              </div>
            </Grid>

            <Grid item xs={12} sm={4} md={3}>
              <Formik
                  validationSchema={validationSchema}
                  enableReinitialize
                  initialValues={initialValues}
                  onSubmit={handleConfirm}>
                {({isSubmitting, values, setFieldValue, initialValues}) => {
                  return (
                      <Form autoComplete='off' autocomplete='off'>
                        <FormikFocusError/>

                        <Grid container spacing={2}>
                          {(type === CandidateStatus.REJECTED.value ||
                              type === CandidateStatus.NOT_SCREENED.value) && (
                              <Grid item xs={12}>
                                <GlobitsTextField
                                    // required
                                    label='Lý do từ chối'
                                    name='refusalReason'
                                    multiline
                                    rows={3}
                                />
                              </Grid>
                          )}

                          {type === CandidateStatus.APPROVED.value && (
                              <Grid item xs={12}>
                                <GlobitsDateTimePicker
                                    isDateTimePicker
                                    required
                                    name='interviewDate'
                                    label='Ngày phỏng vấn/thi tuyển'
                                />
                              </Grid>
                          )}
                          {type === CandidateStatus.APPROVED.value && (
                              <Grid item xs={12}>
                                <GlobitsTextField label='Vị trí dự thi' name='examPosition'/>
                              </Grid>
                          )}
                        </Grid>

                        <div className='pt-12' style={{color:"#5e6c84"}}>
                          {t ("task.action")}
                        </div>

                        <div className='listButton'>
                          <Button
                              variant='contained'
                              className='btn-green'
                              startIcon={<SaveIcon/>}
                              type='submit'
                              disabled={isSubmitting}>
                            Xác nhận
                          </Button>

                          <Button
                              startIcon={<DeleteIcon/>}
                              variant='contained'
                              onClick={handleClose}
                              className='btn-danger'
                              disabled={isSubmitting}>
                            Hủy bỏ
                          </Button>
                        </div>
                      </Form>
                  );
                }}
              </Formik>
            </Grid>
          </Grid>
        </div>
      </GlobitsColorfulThemePopup>
  );
}

export default memo (observer (CandidateConfirmPopup));
