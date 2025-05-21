import React, { useEffect, useState } from "react";
import { Button, Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { PivotViewComponent, FieldList, CalculatedField, Inject } from '@syncfusion/ej2-react-pivotview';
import './timesheetSummary.scss';
import { observer } from "mobx-react";
import { getAllTimesheet, searchTimeSheetDate } from "../TimeSheetDetails/TimeSheetDetailsService";
import { Form, Formik } from "formik";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import { toast } from "react-toastify";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import ImportExcelDialog from "./ImportTimesheetDataPopup";
import { useTranslation } from "react-i18next";
import ImportExcelDialogV2 from "./ImportExcelDialogV2";

export default observer(function TimesheetSummaryIndex(props) {

  const { timeKeepStore } = useStore();
  const { t } = useTranslation();
  const {
      // import V1
      shouldOpenImportDialog,
      setShouldOpenImportDialog,
      // import V2
      shouldOpenImportDialogV2,
      setShouldOpenImportDialogV2
  } = timeKeepStore;

  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [pivotObj, setPivotObj] = useState(null);

  const firsDateCurrentMonth = new Date(currentMonth).setDate(1);

  const [listTimeSheetMonth, setListTimeSheetMonth] = useState([]);

  const dataSourceSettings = {
    dataSource: listTimeSheetMonth,
    expandAll: false,
    formatSettings: [
      { name: 'workingDate', type: 'date', format: 'dd/MM/yyyy' },
      { name: 'startTime', type: 'date', format: 'hh:mm a' },
      { name: 'endTime', type: 'date', format: 'hh:mm a' },
    ],
    rows: [
      { name: "staffName", baseField: "staffId" },
      { name: "project", baseField: "projectId" },
      { name: "activity", baseField: "activityId" }
    ],
    columns: [{ name: "workingDate" }],
    values: [
      { name: "duration", caption: "Số giờ làm", type: "Sum" },
      { name: "taskId", caption: "Số Task hoàn thành", type: "Count" }
    ]
  }

  useEffect(() => {
    let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN"];

    if (roles.some((role) => auth.indexOf(role) !== -1)) {
      loadAllTimeSheet()
    } else {
      loadTimeSheet()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentMonth]);

  function loadAllTimeSheet() {
    getAllTimesheet({ monthReport: currentMonth.getMonth() + 1, yearReport: currentMonth.getFullYear() }).then(({ data }) => {
      setListTimeSheetMonth(data.length > 0 ? data : []);
    }).catch(() => {
      toast('Thất bại!', "Đã có lỗi xảy ra!");
      setListTimeSheetMonth([])
    })
  }

  function loadTimeSheet() {
    searchTimeSheetDate({ monthReport: currentMonth.getMonth() + 1, yearReport: currentMonth.getFullYear() }).then(({ data }) => {
      if (data) {
        setListTimeSheetMonth(data.items.length > 0 ? data.items : []);
      }
    }).catch(() => {
      toast('Thất bại!', "Đã có lỗi xảy ra!");
      setListTimeSheetMonth([])
    })
  }

  function handleChangeMonth(month, setFieldValue) {
    const firsDateCurrentMonthToDate = new Date(firsDateCurrentMonth);
    const date = new Date(firsDateCurrentMonthToDate.setMonth(month - 1));
    setCurrentMonth(date);
    setFieldValue('month', date.getMonth() + 1);
    setFieldValue('year', date.getFullYear());
  }

  return (
    <section className="content-index index-card px-16">

      <GlobitsBreadcrumb routeSegments={[{ name: "Thống kê Timesheet" }]} />

      <Formik
        enableReinitialize
        initialValues={{
          month: currentMonth.getMonth() + 1,
          year: currentMonth.getFullYear(),
        }}
      >
        {({ setFieldValue }) => (
          <Grid container spacing={2} component={Form} className="mb-8">
            <Grid item lg={6} md={5} xs={12} className="flex gap-2">
              <ImportExcelDialog
                open={shouldOpenImportDialog}
                t={t}
                handleClose={() => {
                  setShouldOpenImportDialog(false);
                  loadTimeSheet();
                }}
              />

              <ImportExcelDialogV2
                open={shouldOpenImportDialogV2}
                t={t}
                handleClose={() => {
                  setShouldOpenImportDialogV2(false);
                  loadTimeSheet();
                }}
              />
              
              <Button
                className="btn btn-info d-inline-flex"
                onClick={() => pivotObj.grid.excelExport()}
              >
                {t('general.button.export_excel')}
              </Button>

              {/* <Button
                className="btn btn-success d-inline-flex"
                onClick={() => pivotObj.dataSourceSettings.expandAll = true}
              >
                {t('general.button.expand')}
              </Button>

              <Button
                className="btn btn-danger d-inline-flex"
                onClick={() => pivotObj.dataSourceSettings.expandAll = false}
              >
                {t('general.button.collapse')}
              </Button> */}
            </Grid>

            <Grid item lg={6} md={5} xs={12} className="flex items-center gap-2 justify-end">
              <div style={{ width: '135px' }}>
                <GlobitsSelectInput
                  name={'month'}
                  options={LocalConstants.LIST_MONTH}
                  handleChange={({ target }) => handleChangeMonth(target.value, setFieldValue)}
                />
              </div>

              <div style={{ width: '130px' }}>
                <GlobitsSelectInput
                  name={'year'}
                  options={LocalConstants.LIST_YEAR}
                  handleChange={({ target }) => {
                    setFieldValue('year', target.value)
                    setCurrentMonth(new Date(currentMonth.setFullYear(target.value)))
                  }}
                />
              </div>
            </Grid>
          </Grid>
        )}
      </Formik>

      <PivotViewComponent
        height={'100%'}
        width={'100%'}
        dataSourceSettings={dataSourceSettings}
        showFieldList={true}
        allowCalculatedField={true}
        ref={d => setPivotObj(d)}
        allowExcelExport={true}
      >
        <Inject services={[FieldList, CalculatedField]}></Inject>
      </PivotViewComponent>
    </section>
  )
});
