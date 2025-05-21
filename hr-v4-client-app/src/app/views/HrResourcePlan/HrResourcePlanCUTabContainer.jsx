import { Box, Grid } from "@material-ui/core";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import PropTypes from "prop-types";
import { memo } from "react";
import TabHrResourcePlan from "./HrResourcePlanTab/TabHrResourcePlan";
import TabAggregateHrResourcePlan from "./HrResourcePlanTab/TabAggregateHrResourcePlan";

const hrResourcePlanTab = [
    {
        label: "Định biên nhân sự",
        content: <TabHrResourcePlan />,
    },
    {
        label: "Tổng hợp định biên nhân sự",
        content: <TabAggregateHrResourcePlan />,
    },
];

function HrResourcePlanCUTabContainer() {
    const { hrResourcePlanStore } = useStore();
    const { tabCU, setTabCU } = hrResourcePlanStore;

    const { hrResourcePlanItemStore } = useStore();

    // const { shouldOpenEditorDialog } = hrResourcePlanItemStore;

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <TabsComponent
                    value={tabCU}
                    handleChange={(_, tabIndex) => setTabCU(tabIndex)}
                    tabList={hrResourcePlanTab}
                />
            </Grid>

            <Grid item xs={12} className='p-0'>
                {hrResourcePlanTab?.map(function (tab, index) {
                    return (
                        <TabPanel key={index} value={tabCU} index={index}>
                            {tab?.content}
                        </TabPanel>
                    );
                })}
            </Grid>
        </Grid>
    );
}

export default memo(observer(HrResourcePlanCUTabContainer));

function TabPanel(props) {
    const { children, value, index, ...other } = props;
    return (
        <div
            role='tabpanel'
            hidden={value !== index}
            id={`scrollable-force-tabpanel-${index}`}
            aria-labelledby={`scrollable-force-tab-${index}`}
            {...other}>
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
