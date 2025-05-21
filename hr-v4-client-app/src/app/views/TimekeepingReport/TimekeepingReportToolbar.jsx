import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import moment from "moment";
import * as Yup from "yup";
import PieChartOutlinedIcon from '@material-ui/icons/PieChartOutlined';
import TimekeepingReportFilter from "./TimekeepingReportFilter";


function TimekeepingReportToolbar() {
  const { t } = useTranslation();
  const history = useHistory();

  const {
    timekeepingReportStore,
    hrRoleUtilsStore
  } = useStore();

  const {
    handleSetSearchObject,
    getTimekeepingReportByFitler,
    searchObject,
  } = timekeepingReportStore;




  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await getTimekeepingReportByFitler();
  }

  const validationSchema = Yup.object({
    fromDate: Yup.date()
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày bắt đầu không đúng định dạng")
      .nullable(),

    toDate: Yup.date()
      .test(
        "is-greater",
        "Ngày kết thức phải lớn honw ngày bắt đầu",
        function (value) {
          const { startDate } = this.parent;
          if (startDate && value) {
            return moment(value).isAfter(moment(startDate), "date");
          }
          return true;
        }
      )
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày kết thúc không đúng định dạng")
      .nullable(),
  });

 

  return (
    <Formik
      validationSchema={validationSchema}
      enableReinitialize
      initialValues={searchObject}
      onSubmit={handleFilter}
    >
      {({ resetForm, values, setFieldValue, setValues }) => {

        return (
          <Form autoComplete="off">
            <Grid container spacing={2}>
              {/*/!* <Grid item xs={12} lg={4}>*/}
              {/*    <ButtonGroup*/}
              {/*        color="container"*/}
              {/*        aria-label="outlined primary button group"*/}
              {/*    >*/}
              {/*        <Button*/}
              {/*            type="button"*/}
              {/*            startIcon={<PieChartOutlinedIcon />}*/}
              {/*            onClick={() => handleOpenAssignForm()}*/}
              {/*        >*/}
              {/*            Phân ca làm việc*/}
              {/*        </Button>*/}
              {/*    </ButtonGroup>*/}
              {/*</Grid> *!/*/}

              <Grid item xs={12} className="pt-0">

                <TimekeepingReportFilter
                  handleFilter={handleFilter}
                />

              </Grid>
            </Grid>

          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(TimekeepingReportToolbar));