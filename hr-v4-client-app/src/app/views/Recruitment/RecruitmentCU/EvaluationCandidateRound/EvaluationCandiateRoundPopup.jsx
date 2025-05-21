import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { t } from "app/common/CommonFunctions";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import EvaluationValuesForm from "./EvaluationValuesForm";
import FooterEvaluationCandidateForm from "./FooterEvaluationCandidateForm";
import HeaderEvaluationCandidateForm from "./HeaderEvaluationCandidateForm";

function EvaluationCandiateRoundPopup({ handleAfterSubmit, updateListOnClose }) {
    const { evaluationCandidateRoundStore } = useStore();
    const {
        handleOpenFormEvaluationCandidateRound,
        selectedEvaluationCandidateRound,
        saveEvaluationCandidate,
        openFormEvaluationCandidateRound,
        handleClose,
    } = evaluationCandidateRoundStore;

    const handleFormSubmit = async (values) => {
        await saveEvaluationCandidate(values);
        handleClose();
    };

    return (
        <GlobitsPopupV2
            open={openFormEvaluationCandidateRound}
            size='md'
            noDialogContent
            title={"Đánh giá vòng phỏng vấn của ứng viên"}
            onClosePopup={handleClose}>
            <Formik
                // validationSchema={validationSchema}
                enableReinitialize
                initialValues={selectedEvaluationCandidateRound}
                onSubmit={handleFormSubmit}>
                {({ isSubmitting, values }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <HeaderEvaluationCandidateForm />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <EvaluationValuesForm />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <FooterEvaluationCandidateForm />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={handleClose}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon />}
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='primary'
                                        type='submit'
                                        // disabled={isSubmitting}
                                    >
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default observer(EvaluationCandiateRoundPopup);
