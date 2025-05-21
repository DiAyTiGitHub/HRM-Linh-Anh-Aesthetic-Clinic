import { Button , ButtonGroup , Collapse , Grid } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { pagingParentPositionTitle } from "../PositionTitle/PositionTitleService";
import { pagingRankTitle } from "../RankTitle/RankTitleService";
import { pagingCandidates } from "../Candidate/Candidate/CandidateService";
import {
    pagingCandidateRecruitmentRound
} from "../Candidate/CandidateRecruitmentRound/CandidateRecruitmentRoundService";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";
import LocalConstants , { InterviewScheduleStatus } from "../../LocalConstants";
import { pagingRecruitmentRound } from "../Recruitment/RecruitmentRound/RecruitmentRoundService";

function InterviewScheduleFilter(props) {
    const {t} = useTranslation();

    const {interviewScheduleStore} = useStore();
    const {intactSearchObject} = interviewScheduleStore;

    const {isOpenFilter , handleFilter , handleCloseFilter} = props;

    const {resetForm , values} = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ... JSON.parse(JSON.stringify(intactSearchObject)) ,
        };
        handleFilter(newSearchObject);
        resetForm();
    }

    return (
        <Collapse in={isOpenFilter} className='filterPopup'>
            <div className='flex flex-column'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className='filterContent pt-8'>
                            <Grid container spacing={2} className={"flex flex-end"}>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name='candidate'
                                        label={"Ứng viên tham gia phỏng vấn"}
                                        api={pagingCandidates}
                                        getOptionLabel={(option) =>
                                            option?.displayName || ""
                                        }
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name='recruitmentRound'
                                        label='Vòng tuyển dụng'
                                        api={pagingRecruitmentRound}
                                        // disabled={!values?.candidate?.recruitmentPlan?.id}
                                        // searchObject={{
                                        //     recruitmentPlanId:values?.candidate?.recruitmentPlan?.id || null
                                        // }}
                                        getOptionLabel={(option) =>
                                            option?.name
                                        }
                                    />

                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsSelectInput
                                        name='status'
                                        label='Trạng thái ứng viên'
                                        keyValue="value"
                                        displayvalue="name"
                                        options={InterviewScheduleStatus.getListData()}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsDateTimePicker label='Phỏng vấn từ ngày' name='fromDate'/>
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsDateTimePicker label='Phỏng vấn đến ngày' name='toDate'/>
                                </Grid>
                            </Grid>
                            <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                                <div className='flex justify-end'>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button onClick={handleResetFilter} startIcon={<RotateLeftIcon/>}>
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type='button'
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon/>}>
                                            Đóng bộ lọc
                                        </Button>
                                    </ButtonGroup>
                                </div>
                            </div>
                        </div>
                    </Grid>
                </Grid>
            </div>
        </Collapse>
    );
}

export default memo(observer(InterviewScheduleFilter));
