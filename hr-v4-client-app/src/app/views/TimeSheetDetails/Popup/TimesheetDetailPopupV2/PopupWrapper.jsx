import React, { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { makeStyles } from "@material-ui/core/styles";
import { Tabs, Box, Tab, AppBar } from "@material-ui/core";
import { Dialog, DialogTitle, Icon, IconButton, DialogContent, Grid, DialogActions, Button } from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import RateReviewIcon from '@material-ui/icons/RateReview';
import PropTypes from "prop-types";
import TimesheetDetailInfo from "./TimesheetDetailInfo";

function PopupWrapper() {
    const { t } = useTranslation();

    const { timeSheetDetailsStore } = useStore();

    const {
        handleClosePopup: handleClose,
        openFormTimeSheet: isOpen,
    } = timeSheetDetailsStore;

    const [tabIndex, setTabIndex] = useState(0);

    const classes = useStyles();

    return (
        <Dialog
            className={`dialog-container ${classes.root}`}
            open={isOpen}
            PaperComponent={PaperComponent}
            fullWidth
            scroll="body"
            maxWidth="md"
        >
            <DialogTitle
                className={`p-0 ${classes.styleTitle}`}
                style={{ cursor: "move" }}
                id="draggable-dialog-title"
            >
                <AppBar
                    className={`p-0 ${classes.appBarStyle}`}
                    position="static"
                    color="#ffff">
                    <Tabs
                        orientation="horizontal"
                        value={tabIndex}
                        onChange={(_, newValue) => setTabIndex(newValue)}
                        variant="scrollable"
                        scrollButtons="on"
                        // indicatorColor="primary"

                        TabIndicatorProps={{
                            style: {
                                display: "none",
                            }
                        }}
                        textColor="primary"
                        aria-label="staff tabs scrollable"
                        className={`${classes.appBarStyle}`}
                    >
                        <Tab
                            className={`${classes.tabItem} ${tabIndex === 0 && classes.tabItemSelected}`}
                            icon={<RateReviewIcon fontSize="small" className="mr-4" />}
                            label={"Nhật kí công việc"}
                        />
                    </Tabs>
                </AppBar>
            </DialogTitle>

            <IconButton
                className="p-12"
                style={{ position: "absolute", right: "0px", color: "white", top: 0 }}
                onClick={() => handleClose()}
            >
                <Icon title={t("general.close")}>
                    close
                </Icon>
            </IconButton>


            {/* info of timesheet detail */}
            <TabPanel
                value={tabIndex}
                index={0}
                color="#ffffff"
            >
                <TimesheetDetailInfo />
            </TabPanel>

        </Dialog >
    );
}

export default memo(observer(PopupWrapper));

const tabStyles = {
    tabItem: {
        backgroundColor: "#88b1b1 !important",
        borderTopLeftRadius: "4px",
        borderTopRightRadius: "4px",
        opacity: "0.8",
        margin: "unset !important",
        padding: "0px 4px !important",
        minHeight: "48px !important",

        "& .MuiTab-wrapper": {
            borderTopLeftRadius: "4px",
            borderTopRightRadius: "4px",
            flexDirection: "row",
            padding: "0px 8px",
            color: "white",
        }
    },

    tabItemSelected: {
        opacity: "1",
        backgroundColor: "white !important",
        boxShadow: "rgba(50, 50, 93, 0.25) 0px 0px 27px -5px, rgba(0, 0, 0, 0.3) 0px 0px 16px -8px",

        "& .MuiTab-wrapper": {
            color: "#2f4f4f !important",
        }

    },

    appBarStyle: {
        backgroundColor: "#2f4f4f",

        "& .MuiTabs-flexContainer": {
            borderTopLeftRadius: "4px",
            borderTopRightRadius: "4px",
        }
    },

    styleTitle: {
        borderBottom: 'none',
        backgroundColor: "#e1e1e1",
    },
    styleContent: {
        backgroundColor: "#f6fbff",

    }
}

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
                <Box>
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

function PaperComponent(props) {
    return (
        <Draggable
            handle="#draggable-dialog-title"
            cancel={'[class*="MuiDialogContent-root"]'}
        >
            <Paper {...props} />
        </Draggable>
    );
}
const useStyles = makeStyles(tabStyles);