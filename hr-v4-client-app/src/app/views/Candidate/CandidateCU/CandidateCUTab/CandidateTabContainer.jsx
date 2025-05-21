import { Box, Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo, useMemo } from "react";
import TabCandidateInfo from "./TabCandidateInfo";
import PropTypes from "prop-types";
import TabsComponent from "app/common/Tab/TabComponent";
import TabCandidateWorkingHistory from "./TabCandidateWorkingExperience";
import TabCandidateEducationalHistory from "./TabCandidateEducationalHistory";
import TabCandidateCertificate from "./TabCandidateCertificate";
import TabRecruitmentInfo from "./TabRecruitmentInfo";
import { useStore } from "app/stores";
import TabCandidateAttachment from "./TabCandidateAttachment";
import { useFormikContext } from "formik";
import TabCandidateRecruitmentRound from "./TabCandidateRecruitmentRound";

const candidateTabList = [
    { label: "Thông tin ứng viên", content: <TabCandidateInfo />, fields: ['lastName','firstName','phoneNumber'] },
    { label: "Thông tin tuyển dụng", content: <TabRecruitmentInfo />, fields: ['positionTitle','recruitmentPlan','interviewDate'] },
    { label: "Quá trình tuyển dụng", onlyInEditMode: true, content: <TabCandidateRecruitmentRound /> },
    { label: "Quá trình đào tạo", content: <TabCandidateEducationalHistory /> },
    { label: "Chứng chỉ", content: <TabCandidateCertificate /> },
    { label: "Kinh nghiệm làm việc", content: <TabCandidateWorkingHistory /> },
    { label: "Tệp/Tài liệu liên quan", content: <TabCandidateAttachment /> },
];

function getFilteredCandidateTabs(isEditMode) {
    return candidateTabList.filter((tab) => !tab.onlyInEditMode || (tab.onlyInEditMode && isEditMode));
}

function CandidateTabContainer() {
    const { candidateStore } = useStore();
    const { tabCU, setTabCU } = candidateStore;
    const { values } = useFormikContext();
    const formik = useFormikContext();
    const isEditMode = Boolean(values?.id);
    const filteredTabs = useMemo(() => getFilteredCandidateTabs(isEditMode), [isEditMode]);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <TabsComponent
                    value={tabCU}
                    handleChange={(_, tabIndex) => setTabCU(tabIndex)}
                    tabList={filteredTabs}
                    formik={formik}
                    autoSwitchErrorTab={true}  // ✅ Thêm prop này

                />
            </Grid>

            <Grid item xs={12} className='p-0'>
                {filteredTabs.map((tab, index) => (
                    <TabPanel key={index} value={tabCU} index={index}>
                        {tab.content}
                    </TabPanel>
                ))}
            </Grid>
        </Grid>
    );
}

export default memo(observer(CandidateTabContainer));

function TabPanel(props) {
    const { children, value, index, ...other } = props;
    return (
        <div
            role='tabpanel'
            hidden={value !== index}
            id={`scrollable-force-tabpanel-${index}`}
            aria-labelledby={`scrollable-force-tab-${index}`}
            className={`tab-pane-custom tab-pane-custom-${index}`}
            {...other}
        >
            {value === index && (
                <Box p={1}>
                    <div>{children}</div>
                </Box>
            )}
        </div>
    );
}

TabPanel.propTypes = {
    children: PropTypes.node,
    index: PropTypes.any.isRequired,
    value: PropTypes.any.isRequired,
};
