import React from "react";
import { Grid, TextField } from "@material-ui/core";
import moment from "moment";
import TabAccordion from "app/common/Accordion/TabAccordion";

const PreviousWorkShift = ({ recentSWH }) => {
  return (
    <Grid item md={12}>
      <TabAccordion className='pb-0 mb-0' title='Vị trí làm việc trước đó'>
        {/* Hiển thị thông tin recentSWH */}
        <Grid container spacing={2}>
          <Grid item sm={6}>
            <TextField label='Ngày bắt đầu trước đó' value={recentSWH?.startDate ? moment(recentSWH.startDate).format("DD/MM/YYYY") : ""} InputProps={{ readOnly: true }} fullWidth />
          </Grid>
          <Grid item sm={6}>
            <TextField label='Ngày kết thúc trước đó' value={recentSWH?.endDate ? moment(recentSWH.endDate).format("DD/MM/YYYY") : ""} InputProps={{ readOnly: true }} fullWidth />
          </Grid>
          <Grid item sm={4}>
            <TextField label='Đơn vị trước đó' value={recentSWH?.toOrganization?.name} InputProps={{ readOnly: true }} fullWidth />
          </Grid>
          <Grid item sm={4}>
            <TextField label='Phòng ban trước đó' value={recentSWH?.toDepartment?.name} InputProps={{ readOnly: true }} fullWidth />
          </Grid>
          <Grid item sm={4}>
            <TextField label='Vị trí trước đó' value={recentSWH?.toPosition?.name} InputProps={{ readOnly: true }} fullWidth />
          </Grid>
        </Grid>
      </TabAccordion>
    </Grid>
  );
};

export default PreviousWorkShift;
