import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {AppBar, Box, Tab, Tabs} from "@material-ui/core";
import PropTypes from "prop-types";
import NumberFormat from "react-number-format";
import PoliticaltheoryLevelList from "./PoliticaltheoryLevelList";
import ProfessionalDegreeIndex from "../ProfessionalDegree/ProfessionalDegreeIndex";
import StateManagementLevelIndex from "../StateManagementLevel/StateManagementLevelIndex";
import EducationalManagementLevelIndex from "../EducationalManagementLevel/EducationalManagementLevelIndex";
import EducationDegreeIndex from "../EducationDegree/EducationDegreeIndex";
import InformaticDegreeIndex from "../InformaticDegree/InformaticDegreeIndex";
import {NavLink, useParams} from "react-router-dom";
import OtherLanguageIndex from "../OtherLanguage/OtherLanguageIndex";

function NumberFormatCustom(props) {
    const {inputRef, onChange, ...other} = props;
    return (
        <NumberFormat
            {...other}
            getInputRef={inputRef}
            onValueChange={(values) => {
                props.onChange({
                    target: {
                        name: props.name,
                        value: values.value,
                    },
                });
            }}
            name={props.name}
            value={props.value}
            thousandSeparator
            isNumericString
        />
    );
}

function TabPanel(props) {
    const {children, value, index, ...other} = props;
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

NumberFormatCustom.propTypes = {
    inputRef: PropTypes.func.isRequired,
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
};

export default observer(function PoliticaltheoryLevelIndex() {
    const {degreeType} = useParams();

    const listDegree = [
        {
            label: "Chuyên môn",
            path: "professional",
            component: <ProfessionalDegreeIndex/>,
        },
        {
            label: "Chính trị",
            path: "politicaltheory",
            component: <PoliticaltheoryLevelList/>,
        },
        {
            label: "Quản lý nhà nước",
            path: "stateManagement",
            component: <StateManagementLevelIndex/>,
        },
        {
            label: "Quản lý giáo dục",
            path: "educational-management",
            component: <EducationalManagementLevelIndex/>,
        },
        {
            label: "Học vấn",
            path: "education",
            component: <EducationDegreeIndex/>,
        },
        {
            label: "Tin học",
            path: "informatic",
            component: <InformaticDegreeIndex/>,
        },
        {
            label: "Ngoại ngữ",
            path: "language",
            component: <OtherLanguageIndex/>
        },
    ];

    const [tabeIndex, setTabIndex] = React.useState(0);

    useEffect(() => {
        const index = listDegree.findIndex(item => degreeType && item.path === degreeType);
        setTabIndex(index ? index : 0);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [degreeType]);

    return (
        <div value={tabeIndex} index={0}>
            <AppBar position="static" color="#ffff" className="degree-tabs">
                <Tabs
                    orientation="horizontal"
                    value={tabeIndex}
                    onChange={(event, newValue) => setTabIndex(newValue)}
                    variant="scrollable"
                    scrollButtons="on"
                    indicatorColor="primary"
                    textColor="primary"
                    aria-label="degree tabs scrollable"
                >
                    {listDegree.map((item, index) => (
                        <Tab
                            key={index}
                            className=""
                            style={{color: "yellow !important"}}
                            label={<NavLink to={`${item.path}`}>{item.label}</NavLink>}
                        />
                    ))}
                </Tabs>
            </AppBar>
            <div className="dialog-body">
                {listDegree.map((item, index) => (
                    <TabPanel
                        key={index}
                        value={tabeIndex}
                        index={index}
                        style={{height: "auto"}}
                        color="#ffffff"
                    >
                        {item.component}
                    </TabPanel>
                ))}
            </div>
        </div>
    );
});
