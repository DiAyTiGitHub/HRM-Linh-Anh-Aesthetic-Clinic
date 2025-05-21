import {Grid} from "@material-ui/core";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import DoneAllIcon from "@material-ui/icons/DoneAll";
import EmojiPeopleIcon from "@material-ui/icons/EmojiPeople";
import GroupWorkIcon from "@material-ui/icons/GroupWork";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import HourglassEmptyIcon from "@material-ui/icons/HourglassEmpty";
import PauseCircleOutlineIcon from "@material-ui/icons/PauseCircleOutline";
import ThumbDownIcon from "@material-ui/icons/ThumbDown";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import {useStore} from "app/stores";
import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useTranslation} from "react-i18next";
import {CandidateStatus} from "../../../LocalConstants";
import CandidateIndexToolbar from "./CandidateIndexToolbar";
import CandidateList from "./CandidateList";
import CandidateChooseTemplatePopup from "./CandidatePopup/CandidateChooseTemplatePopup";
import CandidateConfirmPopup from "./CandidatePopup/CandidateConfirmPopup";
import CreateInterviewsPopup from "./CandidatePopup/CreateInterviewsPopup";
import CandidateRecruitmentRoundCUForm from "../CandidateRecruitmentRound/CandidateRecruitmentRoundCUForm";
import CandidateRecruitmentRoundPopup
    from "app/views/Recruitment/RecruitmentCU/CandidateRecruitmentRound/CandidateRecruitmentRoundPopup";
import EvaluationCandiateRoundPopup
    from "app/views/Recruitment/RecruitmentCU/EvaluationCandidateRound/EvaluationCandiateRoundPopup";
import PopupExportExcelRecruitmentReports from "app/views/Candidate/Candidate/PopupExportExcelRecruitmentReports";

const iconMap = {
    1: <HourglassEmptyIcon fontSize="small"/>, // NOT_APPROVED_YET
    2: <CheckCircleIcon fontSize="small"/>,    // SCREENED_PASS
    3: <HighlightOffIcon fontSize="small"/>,   // NOT_SCREENED
    4: <DoneAllIcon fontSize="small"/>,        // APPROVED
    5: <ThumbDownIcon fontSize="small"/>,      // REJECTED
    10: <PauseCircleOutlineIcon fontSize="small"/>, // PENDING_ASSIGNMENT
    13: <DoneAllIcon fontSize="small"/>,       // APPROVE_CV
};

export const candidateTabList = [
    {
        icon: <GroupWorkIcon fontSize="small"/>,
        label: "Tất cả",
        status: null,
        searchObject: {status: null},
    },
    ...CandidateStatus.getListData().filter(status => ![CandidateStatus.APPROVED, CandidateStatus.NOT_RESULT_YET].includes(status)).map(status => ({
        icon: iconMap[status.value] || <GroupWorkIcon fontSize="small"/>,
        label: status.name,
        searchObject: {status: status.value},
    }))
];

function CandidateIndex() {
    const {candidateStore, evaluationCandidateRoundStore, candidateRecruitmentRoundStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingCandidates,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        handleAddPropertiesSearchObj,
        setPageIndex,
        handleSelectListDelete,
        openApprovePopup,
        openRejectPopup,
        openScreenedPassPopup,
        openNotScreenedPopup,
        openResetApprovalStatus,
        handleConfirmResetApprovalStatus,
        listOnDelete,
        openCreateInterviewsPopup,
        openChooseTemplatePopup,
        handleSendMail,
        openPopupExportExcelRecruitmentReports,
    } = candidateStore;
    const {saveCandidateRecruitmentRound} = candidateRecruitmentRoundStore
    const handleAfterSubmit = async (value) => {
        await saveCandidateRecruitmentRound(value)
        await pagingCandidates()
    }

    const { openFormEvaluationCandidateRound} = evaluationCandidateRoundStore;

    const {openCreateEditPopup} = useStore().candidateRecruitmentRoundStore;

    const [tabIndex, setTabIndex] = React.useState(0);

    useEffect(() => {
        pagingCandidates();

        return resetStore;
    }, []);

    async function handleChangeTabIndex(tabIndex) {
        const searchObject = candidateTabList[tabIndex].searchObject;
        setTabIndex(tabIndex);
        handleAddPropertiesSearchObj(searchObject);
        handleSelectListDelete([]);
        await setPageIndex(1);
    }

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb routeSegments={[{name: "Tuyển dụng"}, {name: t("applicant.title")}]}/>
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className='index-card'>
                    <CandidateIndexToolbar/>
                </Grid>

                <Grid item xs={12} className='index-card'>
                    <TabsComponent
                        value={tabIndex}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={candidateTabList}
                    />

                    <CandidateList/>
                </Grid>
            </Grid>

            {openFormEvaluationCandidateRound && <EvaluationCandiateRoundPopup/>}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}

            {openResetApprovalStatus && (
                <GlobitsConfirmationDialog
                    open={openResetApprovalStatus}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmResetApprovalStatus}
                    title={"XÁC NHẬN CÀI LẠI TRẠNG THÁI"}
                    text={
                        <>
                            Bạn có chắc muốn cập nhật trạng thái hồ sơ của ứng viên {` `}
                            <span className='text-red'>
                                {listOnDelete
                                    ?.map(function (candidate) {
                                        return candidate?.displayName;
                                    })
                                    .join(", ")}
                            </span>{" "}
                            thành
                            <span className='text-red'>{` `} Chưa phê duyệt</span>?
                        </>
                    }
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
            {openApprovePopup && <CandidateConfirmPopup type={CandidateStatus.APPROVED.value}/>}
            {openRejectPopup && <CandidateConfirmPopup type={CandidateStatus.REJECTED.value}/>}
            {openScreenedPassPopup && <CandidateConfirmPopup type={CandidateStatus.SCREENED_PASS.value}/>}
            {openNotScreenedPopup && <CandidateConfirmPopup type={CandidateStatus.NOT_SCREENED.value}/>}
            {openCreateInterviewsPopup && <CreateInterviewsPopup/>}
            {openChooseTemplatePopup && (
                <CandidateChooseTemplatePopup
                    candidate={listOnDelete}
                    open={openChooseTemplatePopup}
                    handleClose={handleClose}
                    handleSubmit={handleSendMail}
                />
            )}
            {openCreateEditPopup && (<CandidateRecruitmentRoundPopup handleAfterSubmit={handleAfterSubmit}/>
            )}

            {openPopupExportExcelRecruitmentReports && (
                <PopupExportExcelRecruitmentReports/>
            )}
        </div>
    );
}

export default memo(observer(CandidateIndex));
