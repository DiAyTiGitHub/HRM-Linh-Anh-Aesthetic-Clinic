import {
  Button,
  ButtonGroup,
  Grid,
  Popover,
  useMediaQuery,
  useTheme,
} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import React from "react";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DashboardIcon from "@material-ui/icons/Dashboard";
import PublishIcon from "@material-ui/icons/Publish";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import PeriodSettingList from "./AnalyzeDataList";
import { Formik } from "formik";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import QueryBuilderIcon from "@material-ui/icons/QueryBuilder";

const AnalyzeDataIndex = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [anchorEl, setAnchorEl] = React.useState(null);

  const open = Boolean(anchorEl);
  const id = open ? "simple-popper" : undefined;
  const handleClick = (event) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
  };

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.timeKeeping.title") },
            { name: t("navigation.timeSheet.aggregateData") },
            { name: t("navigation.timeSheet.analyzeData") },
          ]}
        />
      </div>
      <div>
        <Grid className="" container spacing={2}>
          <Formik initialValues={{}} onSubmit={(values) => {}}>
            <>
              <Grid item sm={5} xs={3}>
                <GlobitsDateTimePicker name="dateStart" label="Từ ngày"/>
              </Grid>
              <Grid item sm={5} xs={3}>
                <GlobitsDateTimePicker name="dateStart" label="Đến ngày"/>
              </Grid>
              <Grid item sm={2} xs={3} className="flex align-end">
                <Button variant="outlined" startIcon={<QueryBuilderIcon />}>
                  Phân tích
                </Button>
              </Grid>
            </>
          </Formik>

          <Grid item xs={12}>
            <PeriodSettingList />
          </Grid>
        </Grid>
      </div>
    </div>
  );
};

export default AnalyzeDataIndex;
