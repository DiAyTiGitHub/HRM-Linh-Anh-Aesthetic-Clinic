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

export default observer(function ContractTypeForm(props) {
  const { contractTypeStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    createContractType,
    editContractType,
    selectedContractType,
  } = contractTypeStore;

  const [contractType, setContractType] = useState({
    id: "",
    code: "",
    name: "",
    languageKey: "",
  });

  const validationSchema = Yup.object({
    code: Yup.number()
      .min(0, "Giá trị phải lớn hơn 0 !")
      .required(t("validation.required"))
      .nullable(),
    name: Yup.string().required(t("validation.required")).nullable(),
    languageKey: Yup.string().required(t("validation.required")).nullable(),
  });

  useEffect(() => {
    if (selectedContractType) setContractType(selectedContractType);
  }, [selectedContractType]);

  return (
    <GlobitsPopup
      open={props.open}
      noDialogContent
      title={(selectedContractType?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("contractType.title")}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={contractType}
        onSubmit={(values) =>
          values.id.length === 0
            ? createContractType(values)
            : editContractType(values)
        }
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <div className="dialog-body">
              <DialogContent className="o-hidden">
                <Grid container spacing={2}>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={
                        <span>
                          {t("contractType.code")}
                          <span style={{ color: "red" }}> * </span>
                        </span>
                      }
                      name="code"
                      type="number"
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={
                        <span>
                          {t("contractType.name")}
                          <span style={{ color: "red" }}> * </span>
                        </span>
                      }
                      name="name"
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={
                        <span>
                          {t("contractType.languageKey")}
                          <span style={{ color: "red" }}> * </span>
                        </span>
                      }
                      name="languageKey"
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
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    onClick={() => handleClose()}
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
        )}
      </Formik>
    </GlobitsPopup>
  );
});
