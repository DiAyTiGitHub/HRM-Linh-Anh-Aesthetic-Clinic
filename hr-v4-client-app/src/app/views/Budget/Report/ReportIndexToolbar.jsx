import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { pagingBudget } from "../Budget/BudgetService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";

function ReportIndexToolbar() {
  const history = useHistory();
  const { waitingJobCandidateStore } = useStore();
  const { t } = useTranslation();

  const { pagingWaitingJobCandidates, searchObject, listChosen, handleSetSearchObject, handleOpenReceiveJobPopup, handleOpenNotComeToReceivePopup } = waitingJobCandidateStore;

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await pagingWaitingJobCandidates();
  }

  const [isOpenFilter, setIsOpenFilter] = useState(false);

  function handleCloseFilter() {
    if (isOpenFilter) {
      setIsOpenFilter(false);
    }
  }

  function handleOpenFilter() {
    if (!isOpenFilter) {
      setIsOpenFilter(true);
    }
  }
  function handleTogglePopupFilter() {
    if (isOpenFilter) handleCloseFilter();
    else handleOpenFilter();
  }

  const monthOptions = Array.from({ length: 12 }, (_, i) => ({
    id: i + 1,
    name: `Tháng ${i + 1}`,
  }));

  const yearOptions = Array.from({ length: 5 }, (_, i) => {
    const year = new Date().getFullYear() - i;
    return { id: year, name: `${year}` };
  }); 
  return (
    <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
      {({ resetForm, values, setFieldValue, setValues }) => {
        return (
          <Form autoComplete='off'>
            <Grid item xs={12}>
              <Grid container spacing={2}>
                <Grid item xs={12} lg={8}>
                  <Grid container spacing={1}>
                    <Grid item xs={12} sm={3} lg={3}>
                      <Tooltip title='Ngày nhận việc' placement='top'>
                        <GlobitsPagingAutocomplete label={t("budget.title")} name='budget' api={pagingBudget} value={values.budget} onChange={(e, selectedBudget) => setFieldValue("budget", selectedBudget)} required />
                      </Tooltip>
                    </Grid>{" "}
                    <Grid item xs={12} sm={3} lg={3}>
                      <Tooltip title='Ngày nhận việc' placement='top'>
                        <GlobitsSelectInput label={t("Chọn tháng")} name='month' keyValue='id' options={monthOptions} value={values.month} onChange={(e) => setFieldValue("month", e.target.value)} />
                      </Tooltip>
                    </Grid>{" "}
                    <Grid item xs={12} sm={3} lg={3}>
                      <Tooltip title='Ngày nhận việc' placement='top'>
                        <GlobitsSelectInput label={t("Chọn năm")} name='year' keyValue='id' options={yearOptions} value={values.year} onChange={(e) => setFieldValue("year", e.target.value)} />
                      </Tooltip>
                    </Grid>
                    <Grid item xs={12} sm={6} lg={7}>
                      <div className='flex justify-between align-center'>
                        <ButtonGroup className='filterButtonV4' color='container' aria-label='outlined primary button group'>
                          <Button startIcon={<SearchIcon className={``} />} className='ml-8 d-inline-flex py-2 px-8 btnHrStyle' type='submit'>
                            Tìm kiếm
                          </Button>
                        </ButtonGroup>
                      </div>
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(ReportIndexToolbar));
