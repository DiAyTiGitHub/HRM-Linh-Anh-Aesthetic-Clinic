import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Tabs, Tab,TabPanel, Grid } from "@material-ui/core";
import CandidateWorkingTab1 from "./CandidateWorkingTab1";
import CandidateWorkingTab2 from "./CandidateWorkingTab2";
import CandidateWorkingTab3 from "./CandidateWorkingTab3";


const tabList = [
    {
        value:1,
        components:<CandidateWorkingTab1 />
    },
    {
        value:2,
        components:<CandidateWorkingTab2 />
    },
    {
        value:3,
        components:<CandidateWorkingTab3 />
    },
]

const CandidateWorkingIndex = () => {
  const { t } = useTranslation();

  const [tab, setTab] = useState(1);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.personnel.employeeOnboarding") },
            { name: t("navigation.personnel.candidateWorking") },
          ]}
        />
      </div>

      <div>
        <Tabs
          orientation="horizontal"
          value={tab}
          onChange={(event, newValue) => {
            console.log({ event, newValue });
            setTab(newValue);
          }}
          variant="scrollable"
          scrollButtons="on"
          indicatorColor="primary"
          textColor="primary"
        >
          <Tab value={1} className="tabStaff" label={"Nhân viên thử việc"} />
          <Tab value={2} className="tabStaff" label={t("staff.working")} />
          <Tab value={3} className="tabStaff" label={t("staff.quitJob")} />
        </Tabs>

          <div className="pt-16">
            {tabList?.map((item,i) => {
                if(item?.value === tab) return <div key={item?.value}>{item?.components}</div>
                return null
            })}
          </div>
      </div>
    </div>
  );
};

export default CandidateWorkingIndex;
