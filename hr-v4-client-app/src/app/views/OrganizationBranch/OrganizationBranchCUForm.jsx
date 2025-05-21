import React, { useState, useEffect, memo } from "react";
import { Formik, Form, Field } from "formik";
import { Grid, DialogActions, Button, DialogContent, makeStyles } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopup from "app/common/GlobitsPopup";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingCountry } from "../Country/CountryService";
import { pagingAdministratives } from "../AdministrativeUnit/AdministrativeUnitService";

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

function OrganizationBranchCUForm() {
  const { organizationBranchStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    saveOrganizationBranch,
    pagingOrganizationBranches,
    selectedOrganization,
    openCreateEditPopup
  } = organizationBranchStore;

  const classes = useStyles();

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
  });

  async function handleSaveForm(values) {
    await saveOrganizationBranch(values);
    await pagingOrganizationBranches();
    handleClose();
  }

  const [initialValues, setInitialValues] = useState(selectedOrganization);

  useEffect(function () {
    setInitialValues(selectedOrganization);
  }, [selectedOrganization, selectedOrganization?.id])

  return (
    <GlobitsPopup
      scroll={"body"}
      size="md"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedOrganization?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "địa điểm làm việc"}
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
                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsTextField
                        validate
                        label="Mã địa điểm"
                        name="code"
                        required
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsTextField
                        validate
                        label="Tên địa điểm"
                        name="name"
                        required
                      />
                    </Grid>

                    {/* <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocomplete
                        label="Công ty"
                        name="organization"
                        api={pagingCountry}
                      />
                    </Grid> */}

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocomplete
                        label={"Quốc gia"}
                        name="country"
                        api={pagingCountry}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocompleteV2
                        label="Tỉnh/Thành phố"
                        name="province"
                        value={values?.province}
                        api={pagingAdministratives}
                        searchObject={{ level: 3 }}
                        handleChange={(_, value) => {
                          setFieldValue("province", value);
                          setFieldValue("district", null);
                          setFieldValue("administrativeunit", null);
                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocompleteV2
                        label="Quận/Huyện"
                        name="district"
                        value={values?.district}
                        api={pagingAdministratives}
                        searchObject={{
                          level: 4,
                          parentId: values?.province?.id,
                        }}
                        allowLoadOptions={!!values?.province?.id}
                        clearOptionOnClose
                        handleChange={(_, value) => {
                          setFieldValue("district", value);
                          setFieldValue("administrativeunit", null);
                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocompleteV2
                        label="Xã/Phường"
                        name="commune"
                        api={pagingAdministratives}
                        searchObject={{
                          level: 5,
                          parentId: values?.district?.id,
                        }}
                        allowLoadOptions={!!values?.district?.id}
                        clearOptionOnClose
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsTextField
                        label="Địa chỉ"
                        name="address"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsNumberInput
                        label={t("humanResourcesInformation.phoneNumber")}
                        name="phoneNumber"
                        inputProps={{ maxLength: 11 }}
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsTextField
                        multiline={5}
                        label="Ghi chú"
                        name="note"
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
    </GlobitsPopup>
  );
}

export default memo(observer(OrganizationBranchCUForm));