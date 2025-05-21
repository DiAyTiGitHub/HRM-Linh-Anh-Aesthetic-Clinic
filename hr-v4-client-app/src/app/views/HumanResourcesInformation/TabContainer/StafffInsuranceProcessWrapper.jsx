import React, { memo, useState } from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import { useTranslation } from "react-i18next";
import { Grid } from "@material-ui/core";
import { useFormikContext } from "formik";
import TabAccordion from "app/common/Accordion/TabAccordion";
import StaffInsuranceHistoryTabIndex from "./StaffInsuranceHistory/StaffInsuranceHistoryTabIndex";
import StaffSocialInsuranceV2 from "./StaffSocialInsuranceV2";


function StafffInsuranceProcessWrapper() {
  const { t } = useTranslation();

  const [openStaffInsuranceHistory, setOpenStaffInsuranceHistory] = useState(true);
  const [openStaffInsuranceTickets, setOpenStaffInsuranceTickets] = useState(true);

  const { values } = useFormikContext();

  return (
    <Grid container spacing={2}>
      {
        values?.id && (
          <>
            <Grid item xs={12} lg={4}>
              <TabAccordion
                open={openStaffInsuranceHistory}
                handleOnClick={() => setOpenStaffInsuranceHistory(!openStaffInsuranceHistory)}
                title='Quá trình đóng BHXH'>
                {openStaffInsuranceHistory && <StaffInsuranceHistoryTabIndex />}
              </TabAccordion>
            </Grid>

            <Grid item xs={12} lg={8}>
              <TabAccordion
                open={openStaffInsuranceTickets}
                handleOnClick={() => setOpenStaffInsuranceTickets(!openStaffInsuranceTickets)}
                title='Lịch sử đóng theo kỳ lương'>
                {openStaffInsuranceTickets && <StaffSocialInsuranceV2 />}
              </TabAccordion>
            </Grid>
          </>

        )
      }

    </Grid >
  );
}

export default memo(observer(StafffInsuranceProcessWrapper));
