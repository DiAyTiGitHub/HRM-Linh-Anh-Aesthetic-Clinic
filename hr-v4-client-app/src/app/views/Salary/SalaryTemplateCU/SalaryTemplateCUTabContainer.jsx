import { Box, Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo } from "react";
import PropTypes from "prop-types";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import TabTemplateInfo from "./SalaryTemplateCUTab/TabTemplateInfo";
import TabTemplateItemGroups from "./SalaryTemplateCUTab/TabTemplateItemGroups";
import TabTemplateItems from "./SalaryTemplateCUTab/TabTemplateItems";
import SalaryTemplateItemForm from "./SalaryTemplateItem/SalaryTemplateItemForm";
import TabTemplateStaff from "./SalaryTemplateCUTab/TabChooseStaff/PopupStaff";
import StaffSalaryTemplateIndex from "./SalaryTemplateCUTab/StaffSalaryTemplate/StaffSalaryTemplateIndex";

const salaryTemplateTab = [
    {
        // icon: <PermContactCalendarIcon fontSize="small" className="text-light-blue" />,
        label: "Thông tin mẫu",
        content: <TabTemplateInfo />
    },
    {
        // icon: <BlurLinearIcon fontSize="small" className="text-light-blue" />,
        label: "Nhóm thành phần lương",
        content: <TabTemplateItemGroups />
    },
    {
        // icon: <CastForEducationIcon fontSize="small" className="text-light-blue" />,
        label: "Thành phần lương",
        content: <TabTemplateItems />
    },
    {
        // icon: <CastForEducationIcon fontSize="small" className="text-light-blue" />,
        label: "Nhân viên áp dụng",
        content: <StaffSalaryTemplateIndex />
    },
];


function SalaryTemplateCUTabContainer() {
    const { salaryTemplateStore } = useStore();
    const { tabCU, setTabCU } = salaryTemplateStore;

    const { salaryTemplateItemStore } = useStore();

    const {
        shouldOpenEditorDialog
    } = salaryTemplateItemStore;

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <TabsComponent
                    value={tabCU}
                    handleChange={(_, tabIndex) => setTabCU(tabIndex)}
                    tabList={salaryTemplateTab}
                />
            </Grid>

            <Grid item xs={12} className="p-0">
                {salaryTemplateTab?.map(function (tab, index) {
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

            {shouldOpenEditorDialog && (<SalaryTemplateItemForm />)}
        </Grid>
    );
}

export default memo(observer(SalaryTemplateCUTabContainer));


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