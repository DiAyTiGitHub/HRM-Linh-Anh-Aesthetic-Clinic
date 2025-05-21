import { Button, DialogActions, DialogContent, Grid, makeStyles } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function WorkplaceCUForm() {
  const { workplaceStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    saveWorkplace,
    pagingWorkplace,
    selectedWorkplace,
    openCreateEditPopup
  } = workplaceStore;

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
  });

  async function handleSaveForm(values) {
    await saveWorkplace(values);
    await pagingWorkplace();
    // handleClose();
  }

  const [initialValues, setInitialValues] = useState(selectedWorkplace);

  useEffect(function () {
    setInitialValues(selectedWorkplace);
  }, [selectedWorkplace, selectedWorkplace?.id])

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="xs"
      open={openCreateEditPopup}
      noDialogContent
      title={`${selectedWorkplace?.id ? t("general.button.edit") : t("general.button.add")} địa điểm làm việc`}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleSaveForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {

          return (
            <Form autoComplete="off" autocomplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>

                    <Grid item xs={12}>
                      <GlobitsTextField
                        validate
                        label="Mã địa điểm làm việc"
                        name="code"
                        required
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsTextField
                        validate
                        label="Tên địa điểm làm việc"
                        name="name"
                        required
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsTextField
                        label="Mô tả"
                        name="description"
                        multiline
                        rows={3}
                      />
                    </Grid>

                  </Grid>
                </DialogContent>
              </div>

              <div className="dialog-footer py-8">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-gray d-inline-flex"
                      // color="secondary"
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="mr-0 btn btn-success d-inline-flex"
                      variant="contained"
                      // color="primary"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }
        }
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(WorkplaceCUForm));