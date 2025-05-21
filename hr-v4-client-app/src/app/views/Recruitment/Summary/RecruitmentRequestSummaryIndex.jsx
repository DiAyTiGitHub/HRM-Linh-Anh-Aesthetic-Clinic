import React, {useEffect} from "react";
import {useStore} from "../../../stores";
import RecruitmentRequestSummaryTable from "./RecruitmentRequestSummaryTable";
import RecruitmentRequestSummaryFilter from "./RecruitmentRequestSummaryFilter";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "../../../common/GlobitsBreadcrumb";

const RecruitmentRequestSummaryIndex = () => {

    const {getRecruitmentRequestSummaries, payload} = useStore().recruitmentRequestSummaryStore

    useEffect(() => {
        getRecruitmentRequestSummaries(payload)
    }, []);
    return (
        <>
            <div className='content-index'>
                <div className='index-breadcrumb py-6'>
                    <GlobitsBreadcrumb
                        routeSegments={[
                            {name: "BC ứng viên theo Y/C tuyển dụng"}
                        ]}
                    />
                </div>

                <Grid className='index-card' container spacing={2}>
                    <Grid item xs={12}>
                        <RecruitmentRequestSummaryFilter/>
                    </Grid>

                    <Grid item xs={12}>
                        <RecruitmentRequestSummaryTable/>
                    </Grid>
                </Grid>
            </div>
        </>
    )
}

export default RecruitmentRequestSummaryIndex;