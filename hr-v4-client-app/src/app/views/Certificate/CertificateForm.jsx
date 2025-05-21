import React, { useState, useEffect } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

export default observer (function CertificateForm (props) {
  const {certificateStore} = useStore ();
  const {t} = useTranslation ();
  const {
    handleClose,
    createCertificate,
    editCertificate,
    selectedCertificate,
  } = certificateStore;

  const [certificate, setCertificate] = useState ({
    id:"",
    code:"",
    name:"",
    type:null,
  });

  const validationSchema = Yup.object ({
    code:Yup.string ().required (t ("validation.required")).nullable (),
    // type:Yup.number ().min (0, "Giá trị thấp nhất là 0").required (t ("validation.required")).nullable (),
  });

  useEffect (() => {
    if (selectedCertificate) setCertificate (selectedCertificate);
  }, [selectedCertificate]);


  return (
      <GlobitsPopupV2
          size="sm"
          open={props.open}
          noDialogContent
          title={(selectedCertificate?.id? t ("general.button.edit") : t ("general.button.add")) + ' ' + t ("certificate.title")}
          onClosePopup={handleClose}
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={certificate}
            onSubmit={(values) => values.id.length === 0? createCertificate (values) : editCertificate (values)}
        >
          {({isSubmitting}) => (
              <Form autoComplete="off">
                <div className="dialog-body">
                  <DialogContent className="o-hidden">
                    <Grid container spacing={2}>
                      <Grid item xs={12}>
                        <GlobitsTextField
                            label={<span>{t ("Mã chứng chỉ")}<span className="text-danger"> * </span></span>}
                            name="code"/>
                      </Grid>
                      <Grid item xs={12}>
                        <GlobitsTextField label={t ("Mô tả")} name="name"/>
                      </Grid>
                      {/*<Grid item xs={12}>*/}
                      {/* <GlobitsTextField
                      label={<span>{t("certificate.type")}<span className="text-danger"> * </span></span>}
                      type="number"
                      name="type"
                    /> */}
                      {/*  <GlobitsSelectInput*/}
                      {/*      options={LocalConstants?.CertificateType?.getListData ()}*/}
                      {/*      label={<span>{t ("certificate.type")}<span className="text-danger"> * </span></span>}*/}
                      {/*      name="type"*/}
                      {/*  />*/}
                      {/*</Grid>*/}
                    </Grid>
                  </DialogContent>
                </div>
                <div className="dialog-footer">
                  <DialogActions className="p-0">
                    <div className="flex flex-space-between flex-middle">
                      <Button
                          startIcon={<BlockIcon/>}
                          variant="contained"
                          className="mr-12 btn btn-secondary d-inline-flex"
                          color="secondary"
                          onClick={() => handleClose ()}
                      >
                        {t ("general.button.cancel")}
                      </Button>
                      <Button
                          startIcon={<SaveIcon/>}
                          className="mr-0 btn btn-primary d-inline-flex"
                          variant="contained"
                          color="primary"
                          type="submit"
                          disabled={isSubmitting}
                      >
                        {t ("general.button.save")}
                      </Button>
                    </div>
                  </DialogActions>
                </div>
              </Form>
          )}
        </Formik>
      </GlobitsPopupV2>
  );
});
