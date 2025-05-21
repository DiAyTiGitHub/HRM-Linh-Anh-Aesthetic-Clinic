import { Tabs, Box, Tab, AppBar, Grid, makeStyles } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import PropTypes from "prop-types";
import GridOnIcon from "@material-ui/icons/GridOn";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import TabResultStaffs from "./SalaryResultConfigTabs/TabResultStaffs";
import TabResultItems from "./SalaryResultConfigTabs/TabResultItems";
import TabResultItemGroups from "./SalaryResultConfigTabs/TabResultItemGroups";
import SalaryResultInfoSection from "./SalaryResultConfigTabs/SalaryResultInfoSection";
import RemoveRedEyeIcon from '@material-ui/icons/RemoveRedEye';

const salaryResultTabs = [
    {
        // icon: <PermContactCalendarIcon fontSize="small" className="text-light-blue" />,
        label: "Nhân viên tính lương",
        content: <TabResultStaffs />
    },
    {
        // icon: <BlurLinearIcon fontSize="small" className="text-light-blue" />,
        label: "Nhóm thành phần lương",
        content: <TabResultItemGroups />
    },
    {
        // icon: <CastForEducationIcon fontSize="small" className="text-light-blue" />,
        label: "Thành phần lương",
        content: <TabResultItems />
    },
];


function SalaryResultConfigTabContainer() {
    const {
        salaryResultDetailStore,
        salaryResultStore
    } = useStore();

    const {
        tabCU,
        setTabCU
    } = salaryResultDetailStore;

    const {
        handleOpenPreview,
        isOpenPreview
    } = salaryResultStore;

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <SalaryResultInfoSection />
            </Grid>

            <Grid item xs={12} className="pb-0 pt-12">
                <div className="flex justify-between">
                    <div></div>

                    <div
                        className="flex flex-middle hyperLink cursor-pointer"
                        onClick={handleOpenPreview}
                    >
                        <RemoveRedEyeIcon className="mr-6" />
                        <span className="">
                            Xem trước bảng lương
                        </span>
                    </div>
                </div>
            </Grid>

            <Grid item xs={12}>
                <TabsComponent
                    value={tabCU}
                    handleChange={(_, tabIndex) => setTabCU(tabIndex)}
                    tabList={salaryResultTabs}
                />
            </Grid>

            <Grid item xs={12} className="p-0">
                {salaryResultTabs?.map(function (tab, index) {
                    return (
                        <TabPanel
                            key={index}
                            value={tabCU}
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

export default memo(observer(SalaryResultConfigTabContainer));


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