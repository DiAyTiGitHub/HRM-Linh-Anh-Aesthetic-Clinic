import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsSelectInputV2 from "app/common/form/GlobitsSelectInputV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants, { RECRUITMENT_TYPE, RESULT_STATUS } from "app/LocalConstants";
import { useStore } from "app/stores";
import { pagingCandidates } from "app/views/Candidate/Candidate/CandidateService";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import CandidateRecruitmentRoundDocumentList from "./CandidateRecruitmentRoundDocumentList";
import { pagingWorkplace } from "../../../Workplace/WorkplaceService";

const defaultCandidateRecruitmentRound = {
  id:"",
  candidateName:"",
  roundName:"",
  result:"",
  note:"",
  recruitmentPlanId:null,
  status:LocalConstants.CandidateRecruitmentRoundStatus.WAIT_RESPONSE,
};

export default observer (function CandidateRecruitmentRoundPopup ({handleAfterSubmit, data, title}) {
  const {candidateRecruitmentRoundStore, recruitmentPlanStore} = useStore ();
  const {t} = useTranslation ();
  const {handleClose, selectedCandidateRecruitmentRound, openCreateEditPopup} = candidateRecruitmentRoundStore;
  const {selectedRecruitmentPlan} = recruitmentPlanStore;
  const {selectedRound} = useStore ().candidateStore;
  const validationSchema = Yup.object ({
    candidate:Yup.object ().nullable ().required (t ("Đây là trường bắt buộc")),
  });
  const handleFormSubmit = async (value) => {
    if (typeof handleAfterSubmit === "function") {
      handleAfterSubmit (value);
      handleClose ()
    }
  };

  return (
      <GlobitsPopupV2
          open={openCreateEditPopup}
          size='md'
          noDialogContent
          title={
              (selectedCandidateRecruitmentRound?.id? t ("general.button.edit") : t ("general.button.add")) +
              " " +
              `Ứng viên - Vòng ${title ?? ""}`
          }
          onClosePopup={handleClose}>
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={selectedCandidateRecruitmentRound}
            onSubmit={handleFormSubmit}>
          {({isSubmitting, values}) => (
              <Form autoComplete='off'>
                <div className='dialog-body'>
                  <DialogContent className='o-hidden'>
                    <Grid container spacing={2}>
                      <Grid item xs={6}>
                        <GlobitsPagingAutocompleteV2
                            label={"Ứng viên"}
                            required
                            readOnly={!!values?.id}
                            name='candidate'
                            api={pagingCandidates}
                            searchObject={{
                              recruitmentPlanId:selectedRecruitmentPlan?.id || null,
                              status:4,
                              ... (!selectedRecruitmentPlan?.id && {
                                findNullPlain:!selectedRound?.id,
                              }),
                            }}
                            getOptionLabel={(option) =>
                                option?.displayName && option?.candidateCode
                                    ? `${option.displayName} - ${option.candidateCode}`
                                    : option?.displayName || option?.candidateCode || ""
                            }
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <GlobitsSelectInput
                            name='status'
                            label='Trạng thái'
                            options={Object.keys (LocalConstants.CandidateRecruitmentRoundStatus).map (
                                (key) => {
                                  return {
                                    value:key,
                                    name:LocalConstants.CandidateRecruitmentRoundStatusLabel[key],
                                  };
                                }
                            )}
                            keyValue='value'
                            displayvalue='name'
                            required
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <GlobitsDateTimePicker
                            name='actualTakePlaceDate'
                            label={"Thời gian phỏng vấn"}
                            validate={true}
                            format='dd/MM/yyyy HH:mm'
                            isDateTimePicker={true}
                        />
                      </Grid>

                      <Grid item xs={6}>
                        {/*<GlobitsTextField*/}
                        {/*    label={"Địa điểm phỏng vấn"}*/}
                        {/*    name='examPosition'/>*/}
                        <GlobitsPagingAutocompleteV2
                            name='workplace'
                            label='Địa điểm phỏng vấn'
                            api={pagingWorkplace}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <GlobitsSelectInputV2
                            label={"Hình thức phỏng vấn"}
                            name={"recruitmentType"}
                            options={RECRUITMENT_TYPE}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <GlobitsSelectInputV2
                            label={"Kết quả"}
                            name={"resultStatus"}
                            options={RESULT_STATUS}
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <GlobitsTextField label='Ghi chú' name='note' multiline rows={3}/>
                      </Grid>
                      <Grid item xs={12}>
                        <CandidateRecruitmentRoundDocumentList/>
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
                          onClick={handleClose}>
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
});
