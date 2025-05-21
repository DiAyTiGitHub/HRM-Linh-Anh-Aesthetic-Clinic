import React, { useState, useEffect } from "react";
import { Formik, Form } from "formik";
import {
  Grid,
  makeStyles,
  DialogActions,
  Button,
  DialogContent,
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import * as Yup from "yup";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { convertToConstantFormat } from "app/common/CommonFunctions";

const useStyles = makeStyles((theme) => ({
  gridContainerForm: {
    maxHeight: "68vh",
    overflowY: "auto",
    marginBottom: 10,
  },
  textField: {
    width: "100%",
    margin: "10px 0px !important",
  },
}));

export default observer(function FamilyRelationshipForm(props) {
  const classes = useStyles();
  const { familyRelationshipStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    createFamilyRelationship,
    editFamilyRelationship,
    selectedFamilyRelationship,
  } = familyRelationshipStore;

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
  });

  function hanledFormSubmit(familyRelationship) {
    if (!familyRelationship?.id) {
      createFamilyRelationship(familyRelationship);
    } else {
      editFamilyRelationship(familyRelationship);
    }
  }

  // console.log({selectedFamilyRelationship});


  return (
    <GlobitsPopupV2
      open={props.open}
      noDialogContent
      size="sm"
      title={
        (selectedFamilyRelationship?.id
          ? t("general.button.edit")
          : t("general.button.add")) +
        " " +
        t("navigation.category.staff.relationshipType")
      }
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={selectedFamilyRelationship}
        onSubmit={hanledFormSubmit}
      >
        {({ isSubmitting, values, setFieldValue }) => {

          function handleAutoRenderCode(e) {
            const value = e.target.value;
            setFieldValue("name", value);

            const autoRenderedCode = convertToConstantFormat(value);
            setFieldValue("code", autoRenderedCode);
          }


          return (
            <Form autoComplete="off">
              <div className="dialog-body">
                <DialogContent className="o-hidden p-12">
                  <Grid container spacing={2}
                  >

                    <Grid item xs={12}>
                      <GlobitsTextField
                        validate
                        label={"Tên loại quan hệ"}
                        name="name"
                        onChange={handleAutoRenderCode}
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsTextField
                        label={"Mã loại quan hệ"}
                        validate
                        name="code"
                      />
                    </Grid>

                    <Grid item xs={6} sm={12}>
                      <GlobitsTextField
                        label={"Mô tả"}
                        name="description"
                        multiline
                        rows={4}
                      />
                    </Grid>
                  </Grid>
                </DialogContent>
              </div>

              <div className="dialog-footer">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-gray d-inline-flex"
                      // color="secondary"
                      onClick={() => handleClose()}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="mr-0 btn btn-success d-inline-flex"
                      variant="contained"
                      type="submit"
                    >
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }}
      </Formik>
    </GlobitsPopupV2>
  );
});
