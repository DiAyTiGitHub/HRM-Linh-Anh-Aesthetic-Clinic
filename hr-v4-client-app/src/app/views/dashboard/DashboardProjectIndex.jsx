import React, { memo, useEffect, useState } from "react";
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
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
import { useTranslation } from 'react-i18next';
import FormikFocusError from "app/common/FormikFocusError";
import DescriptionIcon from '@material-ui/icons/Description';

function DashboardProjectIndex(props) {
  const { t } = useTranslation();
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
      { name: "activity", baseField: "activityId" }
    ],
    columns: [{ name: "project", baseField: "projectId" }],
    values: [
      { name: "duration", caption: "Số giờ làm", type: "Sum" },
      { name: "taskId", caption: "Số Task hoàn thành", type: "Count" }
    ]
  }

  useEffect(() => {
    let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN"];

    if (roles.some((role) => auth.indexOf(role) !== -1)) {
      loadAllTimeSheet(getAllTimesheet)
    } else {
      loadTimeSheet(searchTimeSheetDate)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentMonth]);

  function loadAllTimeSheet(api) {
    api({ monthReport: currentMonth.getMonth() + 1, yearReport: currentMonth.getFullYear() }).then((response) => {
      setListTimeSheetMonth(response.data.length > 0 ? response.data : []);
    }).catch(() => {
      toast('Thất bại!', "Đã có lỗi xảy ra!");
      setListTimeSheetMonth([])
    })
  }

  function loadTimeSheet(api) {
    api({ monthReport: currentMonth.getMonth() + 1, yearReport: currentMonth.getFullYear() }).then((response) => {
      if (response != null && response.data != null) {
        setListTimeSheetMonth(response.data.items.length > 0 ? response.data.items : []);
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
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: "Tổng quan" },
            { name: t('navigation.statisticsByProject') }
          ]}
        />
      </div>

      <Grid
        container
        spacing={2}
        className="index-card"
      >
        <Grid item xs={12}>
          <Formik
            enableReinitialize
            initialValues={{
              month: currentMonth.getMonth() + 1,
              year: currentMonth.getFullYear(),
            }}
          >
            {({ setFieldValue }) => {
              return (
                <React.Fragment component={Form}>
                <Form autoComplete="off" autocomplete="off">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6} className="flex gap-2">

                      <ButtonGroup
                        color="container"
                        aria-label="outlined primary button group"
                      >
                        <Tooltip placement="top" title="Xuất báo cáo Excel">
                          <Button
                            startIcon={<DescriptionIcon />}
                            // className="btn btn-info d-inline-flex"
                            onClick={() => pivotObj.grid.excelExport()}
                          >
                            {t('general.button.export_excel')}
                          </Button>
                        </Tooltip>
                      </ButtonGroup>



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

                    <Grid item xs={12} sm={6} className="flex items-center justify-end">
                      <div style={{ width: '135px' }}>
                        <GlobitsSelectInput
                        className="abc"
                          name={'month'}
                          options={LocalConstants.LIST_MONTH}
                          handleChange={({ target }) => handleChangeMonth(target.value, setFieldValue)}
                        />
                      </div>

                      <div style={{ width: '130px' }} className="ml-8">
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

                </Form>
                
                </React.Fragment>
              );
            }}
          </Formik>
        </Grid>

        <Grid item xs={12}>
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
        </Grid>

      </Grid>

    </section>
  )
}

export default memo(observer(DashboardProjectIndex));
