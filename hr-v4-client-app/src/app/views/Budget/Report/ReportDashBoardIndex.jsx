import React, { useEffect } from "react";
import { Grid, Button, Collapse } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo } from "react";
import { Formik, Form } from "formik";
import ReportLineChart from "./ReportLineChart";
import ReportPieChart from "./ReportPieChart";
import { useStore } from "app/stores";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingBudget } from "../Budget/BudgetService";
import { useTranslation } from "react-i18next";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import ReportColumnChart from "./ReportColumnChart ";

function ReportDashBoardIndex() {
  const { fetchReportByMonth, fetchReportByYear } = useStore().reportStore;
  const { t } = useTranslation();

  // Lấy giá trị budget từ localStorage
  const getDefaultBudget = () => {
    const storedBudget = localStorage.getItem("selectedBudget");
    return storedBudget ? JSON.parse(storedBudget) : null;
  };

  const initialValues = {
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1,
    budget: getDefaultBudget(),
  };

  const monthOptions = Array.from({ length: 12 }, (_, i) => ({
    id: i + 1,
    name: `Tháng ${i + 1}`,
  }));

  const yearOptions = Array.from({ length: 5 }, (_, i) => {
    const year = new Date().getFullYear() - i;
    return { id: year, name: `${year}` };
  });

  useEffect(() => {
    if (initialValues.budget) {
      fetchReportByMonth(initialValues);
      fetchReportByYear(initialValues);
    }
  }, []);

  const handleSearch = (values) => {
    fetchReportByMonth(values);
    fetchReportByYear(values);
    // Lưu budget vào localStorage để dùng lại
    if (values.budget) {
      localStorage.setItem("selectedBudget", JSON.stringify(values.budget));
    }
  };

  return (
    <Formik initialValues={initialValues} onSubmit={handleSearch}>
      {({ values, setFieldValue }) => (
        <Form>
          <Grid container spacing={2} style={{ padding: 16 }}>
            {/* Filter Section */}
            <Grid item xs={12}>
              <Collapse in={true} className='filterPopup'>
                <div className='flex flex-column'>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <div className='filterContent pt-8'>
                        <Grid container spacing={2}>
                          <Grid item xs={12} sm={6} md={4} lg={3}>
                            
                            <GlobitsPagingAutocomplete name='budget' api={pagingBudget} value={values.budget} onChange={(e, selectedBudget) => setFieldValue("budget", selectedBudget)} required />
                          </Grid>
                          <Grid item xs={12} sm={6} md={4} lg={3}>
                            
                            <GlobitsSelectInput name='month' keyValue='id' options={monthOptions} value={values.month} onChange={(e) => setFieldValue("month", e.target.value)} />
                          </Grid>
                          <Grid item xs={12} sm={6} md={4} lg={3}>
                            
                            <GlobitsSelectInput name='year' keyValue='id' options={yearOptions} value={values.year} onChange={(e) => setFieldValue("year", e.target.value)} />
                          </Grid>
                          <Grid item xs={12} sm={6} md={4} lg={3}>
                            <Grid container justifyContent='flex-end' alignItems='flex-end'>
                              <Button variant='contained' type='submit' disabled={!values.budget}>
                                {t("Apply Filters")}
                              </Button>
                            </Grid>
                          </Grid>
                        </Grid>
                      </div>
                    </Grid>
                  </Grid>
                </div>
              </Collapse>
            </Grid>
            {/* Pie Chart */}
            <Grid item xs={12} md={6}>
              <ReportPieChart title={`Biểu đồ thể hiện số liệu tiêu dùng tính từng tháng trong tháng ${values.month}`} />
            </Grid>
            {/* Column Chart */}
            <Grid item xs={12} md={6}>
              <ReportColumnChart title={`Biểu đồ Thu và Chi từng tháng trong năm ${values.year}`} />
            </Grid>
            {/* Line Chart */}
            {/* <Grid item xs={12} md={6}>
              <ReportLineChart
                title={`Biểu đồ thể hiện số liệu tiêu dùng tính từng tháng trong năm ${values.year}`}
              />
            </Grid> */}
          </Grid>
        </Form>
      )}
    </Formik>
  );
}

export default memo(observer(ReportDashBoardIndex));
