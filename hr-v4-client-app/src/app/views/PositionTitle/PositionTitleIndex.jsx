import React, { memo, useEffect, useState } from "react";
import { useParams, NavLink } from "react-router-dom";
import { Tab } from "@material-ui/core";
import { TabContext, TabList, TabPanel } from "@material-ui/lab";
import CivilServantTypeIndex from "../CivilServantType/CivilServantTypeIndex";
import CivilServantCategoryIndex from "../CivilServantCategory/CivilServantCategoryIndex";
import GradeIndex from "../Grade/GradeIndex";

const LIST_DEGREE = [
    { label: "Bậc công chức", path: "grade", component: <GradeIndex /> },
    { label: "Mã ngạch công chức", path: "civilservantcategory", component: <CivilServantCategoryIndex /> },
    { label: "Phân loại công chức", path: "civilservanttype", component: <CivilServantTypeIndex /> },
];

const ColorIndex = () => {
    const { typeDuty } = useParams();
    const [tabIndex, setTabIndex] = useState("0");

    useEffect(() => {
        const index = LIST_DEGREE.findIndex(item => item.path === typeDuty);
        setTabIndex(index >= 0 ? String(index) : "0");
    }, [typeDuty]);

    return (
        <div>
            <TabContext value={tabIndex}>
                <TabList
                    orientation="horizontal"
                    onChange={(_, newTab) => setTabIndex(newTab)}
                    variant="scrollable"
                    scrollButtons="on"
                    indicatorColor="primary"
                    textColor="primary"
                >
                    {LIST_DEGREE.map((item, index) => (
                        <Tab
                            key={index}
                            value={String(index)}
                            component={NavLink}
                            to={item.path}
                            label={item.label}
                        />
                    ))}
                </TabList>

                {LIST_DEGREE.map((item, index) => (
                    <TabPanel key={index} value={String(index)} className="p-0 cards-container mt-4">
                        {item.component}
                    </TabPanel>
                ))}
            </TabContext>
        </div>
    );
};

export default memo(ColorIndex);
