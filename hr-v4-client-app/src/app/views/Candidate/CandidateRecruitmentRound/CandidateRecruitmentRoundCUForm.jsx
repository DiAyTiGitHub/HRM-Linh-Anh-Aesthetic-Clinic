import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { pagingCandidates } from "../Candidate/CandidateService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";

function CandidateRecruitmentRoundCUForm({handleAfterSave}) {
  const { t } = useTranslation();

  const {
    candidateRecruitmentRoundStore,
    recruitmentStore
  } = useStore();

  const {
    selectedRecruitment
  } = recruitmentStore;

  const {
    handleClose,
    saveCandidateRecruitmentRound,
    pagingCandidateRecruitmentRound,
    selectedCandidateRecruitmentRound,
    openCreateEditPopup
  } = candidateRecruitmentRoundStore;

  const validationSchema = Yup.object({
    recruitmentRound: Yup.object().nullable().required(t("validation.required")),
    candidate: Yup.object().nullable().required(t("validation.required")),
  });

  async function handleSaveForm(values) {
    await saveCandidateRecruitmentRound(values);
    await pagingCandidateRecruitmentRound();
    if(typeof handleAfterSave === 'function') {
      handleAfterSave();
    }
  }

  const [initialValues, setInitialValues] = useState(selectedCandidateRecruitmentRound);

  useEffect(function () {
    setInitialValues(selectedCandidateRecruitmentRound);
  }, [selectedCandidateRecruitmentRound, selectedCandidateRecruitmentRound?.id]);

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="sm"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedCandidateRecruitmentRound?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "quá trình tuyển dụng"}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleSaveForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {

          return (
            <Form autoComplete="off" autocomplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                      <GlobitsPagingAutocompleteV2
                        label="Ứng viên"
                        name="candidate"
                        readOnly={selectedCandidateRecruitmentRound?.id}
                        api={pagingCandidates}
                        searchObject={{
                          recruitmentId: selectedRecruitment?.id
                        }}
                        required
                        displayName={"displayName"}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <GlobitsAutocomplete
                        label={"Vòng tuyển dụng"}
                        readOnly={selectedCandidateRecruitmentRound?.id}
                        name="recruitmentRound"
                        required
                        options={selectedRecruitment?.recruitmentRounds}
                      />

                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <GlobitsTextField
                        label="Vị trí dự thi"
                        name="examPosition"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <GlobitsDateTimePicker
                        isDateTimePicker
                        label="Thời gian dự thi"
                        name="actualTakePlaceDate"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <GlobitsSelectInput
                        label={"Kết quả vòng thi"}
                        name="resultStatus"
                        options={LocalConstants.EVALUATION_STATUS_V2.getListData()}
                        keyValue="value"
                      // hideNullOption={true}
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField
                        label="Nhận xét ứng viên"
                        name="note"
                        multiline
                        rows={3}
                      />
                    </Grid>
                  </Grid>

                </DialogContent>
              </div>

              <div className="dialog-footer dialog-footer-v2 py-8">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-secondary d-inline-flex"
                      color="secondary"
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="mr-0 btn btn-primary d-inline-flex"
                      variant="contained"
                      color="primary"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }
        }
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(CandidateRecruitmentRoundCUForm));


