import React from "react";
import { observer } from "mobx-react";
import { Grid, Button } from "@material-ui/core";
import "../TimeSheetDetails/time-sheet-styles.scss";
import { useStore } from "app/stores";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { ListWeeks } from "app/LocalConstants";
import { formatDate, getDay, } from "app/LocalFunction";
import { useTranslation } from "react-i18next";
import CalendarMonth from "app/common/Calendar/CalendarMonth";
import { useEffect } from "react";
import CalendarForm from "./CalendarForm";
import DeleteIcon from '@material-ui/icons/Delete';
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";

function TimeKeepingIndex(props) {

  const { t } = useTranslation();
  const { calendarStore } = useStore();
  const {
    loadJournalsByMonth,
    handleOpenFormJournals,
    handleChangeCurrentMonth,
    handleChangeDateValue,
    handleOpenConfirmDeleteJournals,
    handleClosePopup,
    handleConfirmDeleteJournals,
    handleChangeValueCurrentDate,
    openConfirmDeleteJournals,
    listDataJournals,
    dateValue,
    valueCurrentDate
  } = calendarStore;

  useEffect(() => {
    loadJournalsByMonth();
  }, [loadJournalsByMonth]);

  function renderDate(date) {
    const dateFormat = formatDate('YYYY MM DD', date)
    const valueDate = (Array.isArray(listDataJournals) && listDataJournals?.length > 0) ? listDataJournals?.filter(e => formatDate('YYYY MM DD', e.journalDate) === dateFormat) : null;
    if (Array.isArray(valueDate) && valueDate.length > 0) {

      if (valueCurrentDate.length === 0 && dateFormat === formatDate('YYYY MM DD', dateValue)) {
        handleChangeValueCurrentDate(valueDate)
      }

      return valueDate.map((item, index) => (
        <p key={index} className="btn-primary-d p-4 rounded-2" style={{ width: "fit-content" }} >
          {item.name}
        </p>
      ))
    }
    return <></>
  }

  return (
    <section className="content-index">
      <GlobitsBreadcrumb routeSegments={[{ name: t("calendar.title") }]} />

      <Grid container spacing={1}>
        <Grid item xs={12} lg={3} className='value-date-group' style={{ height: "fit-content" }}>
          <p className="text-to-date">
            {t(ListWeeks.find(e => e.index === getDay(dateValue)).name)} - {formatDate('DD/MM/YYYY', dateValue)}
          </p>

          <div className="scroll-area">
            {(Array.isArray(valueCurrentDate) && valueCurrentDate.length > 0) ? (
              valueCurrentDate.map((item, index) => (
                <article key={index} className='item-timesheet mb-1' onClick={() => handleOpenFormJournals(item.id)}>
                  <div className="flex justify-between">
                    {item?.location ? (
                      <p className="name-project m-0" style={{ height: "fit-content" }}>{item?.location}</p>
                    ) : (
                      <p className="m-0"></p>
                    )}

                    <DeleteIcon color="secondary" onClick={(event) => { handleOpenConfirmDeleteJournals(item); event.stopPropagation() }} />
                  </div>
                  <p className="activity-project text-center m-0" style={{ fontSize: "20px" }}><strong>{item.name}</strong></p>
                  <p className="activity-project module line-clamp-5 m-0">{item.description}</p>
                  <div className="flex justify-between"></div>
                </article>
              ))) : (
              <p className="text-center m-0">
                {t("timeSheet.noTimeSheet")}
              </p>
            )}
          </div>

          <div className="py-10 flex items-center justify-between">
            <Button className={`btn btn-info`} variant="contained"
              onClick={() => handleOpenFormJournals(null, 1, dateValue)}
            >
              {t("calendar.addEvent")}
            </Button>
            <Button className={`btn btn-info`} variant="contained"
              onClick={() => handleOpenFormJournals(null, 0, dateValue)}
            >
              {t("Thêm ghi chú")}
            </Button>
          </div>
        </Grid>
        <Grid item xs={12} lg={9}>
          <CalendarMonth
            onClickDate={handleChangeDateValue}
            renderDate={renderDate}
            onChangeDate={handleChangeCurrentMonth}
          />
        </Grid>
      </Grid>
      <CalendarForm />
      <GlobitsConfirmationDialog
        open={openConfirmDeleteJournals}
        onConfirmDialogClose={handleClosePopup}
        onYesClick={handleConfirmDeleteJournals}
        title={t("confirm_dialog.delete.title")}
        text={t("confirm_dialog.delete.text")}
        agree={t("confirm_dialog.delete.agree")}
        cancel={t("confirm_dialog.delete.cancel")}
      />
    </section>
  );
};

export default observer(TimeKeepingIndex)