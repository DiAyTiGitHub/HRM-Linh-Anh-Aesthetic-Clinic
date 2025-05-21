import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Form , Formik } from "formik";
import React , { memo , useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
import GlobitsEditor from "app/common/form/GlobitsEditor";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants, {CodePrefixes, RECRUITMENT_REQUEST} from "app/LocalConstants";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { pagingRecruitmentRequest } from "../RecruitmentRequestV2/RecruitmentRequestV2Service";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import TabRecruitmentRound from "../RecruitmentCU/RecruitmentCUTabs/TabRecruitmentRound";
import LocalStorageService from "../../../services/localStorageService";
import CreateInterviewsPopup from "app/views/Candidate/Candidate/CandidatePopup/CreateInterviewsPopup";
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";
import {paging} from "../../System/SystemParam/ContentTemplate/ContentTemplateService";

function RecruitmentPlanV2Form() {
    const {recruitmentPlanStore , candidateStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        saveRecruitmentPlan ,
        pagingRecruitmentPlan ,
        selectedRecruitmentPlan ,
        openCreateEditPopup ,
        autoGenCode
    } = recruitmentPlanStore;

    const validationSchema = Yup.object({
        name:Yup.string().required(t("validation.required")).nullable() ,
        code:Yup.string().required(t("validation.required")).nullable() ,
        status:Yup.number().required(t("validation.required")).nullable() ,
        recruitmentRounds:Yup.array()
            .of(
                Yup.object().shape({
                    roundOrder:Yup.number().nullable().required(t("validation.required")) ,
                })
            )
            .nullable() ,
    });
    const {
        openCreateInterviewsPopup ,
    } = candidateStore;

    async function handleSaveForm(values) {
        try {
            const response = await saveRecruitmentPlan(values);
            if (response) await pagingRecruitmentPlan();
        } catch (error) {
            console.error(error);
        }
    }

    const [initialValues , setInitialValues] = useState(selectedRecruitmentPlan);

    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.KE_HOACH_TUYEN_DUNG);
        if (code) {
            setInitialValues({ ...selectedRecruitmentPlan, code });
        }
    };


    useEffect(
        function () {
            if(!selectedRecruitmentPlan.id){
                autoGenCodeFunc()
            }
            setInitialValues(selectedRecruitmentPlan);
        } ,
        [selectedRecruitmentPlan , selectedRecruitmentPlan?.id]
    );

    return (
        <GlobitsPopupV2
            scroll={"paper"}
            size='lg'
            open={openCreateEditPopup}
            noDialogContent
            title={
                (selectedRecruitmentPlan?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("navigation.recruitment.plan")
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={{
                    ... initialValues ,
                    personApproveCVName:initialValues?.recruitmentRequest?.personInCharge?.displayName ,
                    personApproveCV:initialValues?.recruitmentRequest?.personInCharge ,
                }}
                onSubmit={handleSaveForm}>
                {({isSubmitting , values , setFieldValue , initialValues}) => {
                    return (
                        <Form autoComplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError/>
                                    <Grid container spacing={2}>
                                        <Grid item sm={6} xs={12}>
                                            <GlobitsTextField label={"Mã kế hoạch"} validate name='code'/>
                                        </Grid>

                                        <Grid item sm={6} xs={12}>
                                            <GlobitsTextField label={"Tên kế hoạch"} name='name' validate/>
                                        </Grid>

                                        <Grid item sm={6} xs={12}>
                                            <GlobitsPagingAutocomplete
                                                label={"Yêu cầu tuyển dụng"}
                                                validate
                                                name='recruitmentRequest'
                                                api={pagingRecruitmentRequest}
                                                searchObject={{
                                                    pageIndex:1 ,
                                                    pageSize:9999 ,
                                                    keyword:"" ,
                                                    personInChargeId:LocalStorageService.getLoginUser().id ,
                                                    recruitmentRequestStatus: [RECRUITMENT_REQUEST.START_RECRUITING, RECRUITMENT_REQUEST.RECRUITING],
                                                }}
                                                onChange={(_ , value) => {
                                                    setFieldValue("recruitmentRequest" , value);
                                                    setFieldValue("personApproveCV" , value?.personInCharge);
                                                    setFieldValue(
                                                        "personApproveCVName" ,
                                                        value?.personInCharge?.displayName
                                                    );
                                                }}
                                            />
                                        </Grid>

                                        {/* <Grid item sm={6} xs={12}>
                                            <GlobitsSelectInput
                                                hideNullOption
                                                label={"Trạng thái"}
                                                name='status'
                                                keyValue='value'
                                                options={LocalConstants.RecruitmentPlanStatus.getListData()}
                                            />
                                        </Grid> */}

                                        <Grid item xs={12}>
                                            <div className='flex align-center'>
                                                <div className='flex-column flex-1'>
                                                    <GlobitsDateTimePicker
                                                        name='estimatedTimeFrom'
                                                        label='Thời gian dự kiến'
                                                    />
                                                </div>

                                                <span className='mt-18 px-8'>đến</span>

                                                <div className='flex-column flex-1 mt-auto'>
                                                    <GlobitsDateTimePicker name='estimatedTimeTo'/>
                                                </div>
                                            </div>
                                        </Grid>

                                        <Grid item sm={6} xs={12}>
                                            <GlobitsTextField
                                                disabled
                                                label={"Người duyệt CV"}
                                                name='personApproveCVName'
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsEditor label={"Nội dung kế hoạch"} name='description'/>
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TabRecruitmentRound/>
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer dialog-footer-v2 py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex'
                                            color='secondary'
                                            onClick={handleClose}
                                            disabled={isSubmitting}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon/>}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'
                                            disabled={isSubmitting}>
                                            {t("general.button.save")}
                                        </Button>
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
            {openCreateInterviewsPopup && <CreateInterviewsPopup/>}
        </GlobitsPopupV2>
    );
}

export default memo(observer(RecruitmentPlanV2Form));
