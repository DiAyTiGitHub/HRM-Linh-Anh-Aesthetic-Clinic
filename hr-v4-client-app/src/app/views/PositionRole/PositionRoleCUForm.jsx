import React, {memo, useEffect, useState} from "react";
import {Form, Formik} from "formik";
import {Button, DialogActions, DialogContent, Grid, makeStyles} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import * as Yup from "yup";
import {observer} from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiAccordion-rounded": {
      borderRadius: "5px",
    },

    "& .MuiPaper-root": {
      borderRadius: "5px",
    },

    "& .MuiAccordionSummary-root": {
      borderRadius: "5px",
      // backgroundColor: "#EBF3F9",
      color: "#5899d1 ",
      fontWeight: "400",

      "& .MuiTypography-root": {
        fontSize: "1rem",
      },
    },

    "& .Mui-expanded": {
      "& .MuiAccordionSummary-root": {
        backgroundColor: "#EBF3F9",
        color: "#5899d1 ",
        // borderLeft: "3px solid rgba(88, 153, 202, 0.84) !important",
        fontWeight: "700",
        maxHeight: "50px !important",
        minHeight: "50px !important",
      },
      "& .MuiTypography-root": {
        fontWeight: 700,
      },
    },

    "& .MuiButton-root": {
      borderRadius: "0.125rem !important",
    },
  },
}));

function PositionRoleCUForm() {
  const { positionRoleStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    savePositionRole,
    pagingPositionRole,
    selectedPositionRole,
    openCreateEditPopup
  } = positionRoleStore;

  const validationSchema = Yup.object({
    // code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
  });

  async function handleSaveForm(values) {
    await savePositionRole(values);
    await pagingPositionRole();
  }

  const [initialValues, setInitialValues] = useState(selectedPositionRole);

  useEffect(function () {
    setInitialValues(selectedPositionRole);
  }, [selectedPositionRole, selectedPositionRole?.id])

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="md"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedPositionRole?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.positionRole.title")}
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
                    <Grid item xs={12} md={4}>
                      <GlobitsTextField
                        validate
                        label="Tên nhóm quyền mặc định"
                        name="name"
                        required
                      />
                    </Grid>

                    <Grid item xs={12} md={4}>
                      <GlobitsTextField
                        label="Tên viết tắt"
                        name="shortName"
                      />
                    </Grid>

                    <Grid item xs={12} md={4}>
                      <GlobitsTextField
                        label="Tên khác"
                        name="otherName"
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
                      className="mr-12 btn btn-secondary d-inline-flex"
                      color="secondary"
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="mr-0 btn btn-primary d-inline-flex"
                      variant="contained"
                      color="primary"
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

export default memo(observer(PositionRoleCUForm));