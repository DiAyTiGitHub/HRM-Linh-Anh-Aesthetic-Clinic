import React from "react";
import { Formik, Form } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { Grid, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPopup from "app/common/GlobitsPopup";

function TimeKeepingForm() {
  const { t } = useTranslation();

  const { calendarStore } = useStore();
  const { dataEditFormJournals, openFormJournals, handleClosePopup, handleSubmitFormJournals } = calendarStore;

  return (
    <GlobitsPopup
      size='sm'
      title={dataEditFormJournals.type === 1 ? 'Sự kiện' : 'Note'}
      open={openFormJournals}
      onClosePopup={handleClosePopup}
      noDialogContent
    >
      <Formik
        enableReinitialize
        initialValues={dataEditFormJournals}
        onSubmit={handleSubmitFormJournals}
      >
        {({ values }) => (
          <Form autoComplete="off">
            <DialogContent className="dialog-body overflow-auto">
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <GlobitsTextField name='name' label={'Tên'} />
                </Grid>

                {values.type === 1 && (
                  <>
                    <Grid item xs={12}>
                      <GlobitsTextField name='location' label={'Địa điểm'} />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsDateTimePicker name='fromDate' label={'Thời gian bắt đầu'} />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsDateTimePicker name='toDate' label={'Thời gian kết thúc'} />
                    </Grid>
                  </>
                )}
                <Grid item xs={12}>
                  <GlobitsTextField name='description' label={'Mô tả'} multiline minRows={3} />
                </Grid>
              </Grid>
            </DialogContent>

            <DialogContent className="dialog-footer flex justify-end p-0">
              <Button
                startIcon={<BlockIcon />}
                variant="contained"
                className="mr-12 btn btn-secondary d-inline-flex"
                color="secondary"
                onClick={() => handleClosePopup()}
              >
                {t("general.button.cancel")}
              </Button>
              <Button
                startIcon={<SaveIcon />}
                className="mr-0 btn btn-primary d-inline-flex"
                variant="contained"
                color="primary"
                type="submit"
              >
                {t("general.button.save")}
              </Button>
            </DialogContent>
          </Form>
        )}
      </Formik>
    </GlobitsPopup>
  );
};

export default observer(TimeKeepingForm)