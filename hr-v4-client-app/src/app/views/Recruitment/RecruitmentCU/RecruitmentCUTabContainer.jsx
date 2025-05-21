import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import TabRecruitmentInfo from './RecruitmentCUTabs/TabRecruitmentInfo'
import TabContactInfo from './RecruitmentCUTabs/TabContactInfo'
import TabRecruitmentRound from './RecruitmentCUTabs/TabRecruitmentRound'
import { Box, Grid } from '@material-ui/core'
import TabsComponent from 'app/common/Tab/TabComponent'
import { useStore } from "app/stores";
import PropTypes from "prop-types";

const recruitmentTablist = [
    {
        // icon: <PermContactCalendarIcon fontSize="small" className="text-light-blue" />,
        label: "Thông tin tuyển dụng",
        content: <TabRecruitmentInfo />
    },
    {
        // icon: <PermContactCalendarIcon fontSize="small" className="text-light-blue" />,
        label: "Thông tin liên hệ",
        content: <TabContactInfo />
    },
    {
        // icon: <PermContactCalendarIcon fontSize="small" className="text-light-blue" />,
        label: "Vòng tuyển dụng",
        content: <TabRecruitmentRound />
    },
];

function RecruitmentCUTabContainer() {
    const { recruitmentStore } = useStore();

    const {
        tabIndex,
        setTabIndex
    } = recruitmentStore;

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <TabsComponent
                    tabList={recruitmentTablist}
                    value={tabIndex}
                    handleChange={(_, tabIndex) => setTabIndex(tabIndex)}
                />
            </Grid>


            <Grid item xs={12}>
                {recruitmentTablist?.map(function (tab, index) {
                    return (
                        <TabPanel
                            key={index}
                            value={tabIndex}
                            index={index}
                        >
                            {tab?.content}
                        </TabPanel>
                    );
                })}
            </Grid>
        </Grid>
    );


}

export default memo(observer(RecruitmentCUTabContainer));

function TabPanel(props) {
    const { children, value, index, ...other } = props;
    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`scrollable-force-tabpanel-${index}`}
            aria-labelledby={`scrollable-force-tab-${index}`}
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