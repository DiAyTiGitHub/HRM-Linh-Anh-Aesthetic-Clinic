import React, { useState, useEffect } from "react";
import { Formik, Form } from "formik";
import {
  Grid,
  DialogActions,
  Button,
  DialogContent,
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import "./UserStyle.scss";
import GlobitsPopup from "app/common/GlobitsPopup";
import { observer } from "mobx-react";

export default observer(
  function ResetPassWord(props) {

    const { userStore } = useStore();
    const { t } = useTranslation();
    const { selectedUser,resetPassWord } = userStore;
    const { open, setOpen } = props;
    const [user, setUser] = useState({
      id: null,
      password: null,
      confirmPassword: "",
    });

    const validationSchema = Yup.object({
      password: Yup.string().required(t("validation.required")).nullable(),
      confirmPassword: Yup.string()
        .oneOf([Yup.ref("password"), null], t("validation.confirm_password"))
        .required(t("validation.required"))
        .nullable(),
    });

    useEffect(() => {
      if (selectedUser) {
        setUser({ id : selectedUser?.id });
      }
    }, [selectedUser]);
console.log(selectedUser);
    function hanledFormSubmit(user) {
        resetPassWord(user).then(() => {
            setOpen(false)
          });
    }

    return (
      <GlobitsPopup
        open={open}
        title={t("general.button.resetPassWord")}
        onClosePopup={()=> setOpen(false)}
        noDialogContent
        size="xs"
      >
        <Formik
          validationSchema={validationSchema}
          enableReinitialize
          initialValues={user}
          onSubmit={(values, actions) => {
            hanledFormSubmit(values);
            actions.setSubmitting(false);
          }}
        >
          {({ setFieldValue, values, isSubmitting, errors, touched }) => (
            <Form autoComplete="off">
              <div className="dialog-body">
                <DialogContent className="o-hidden">
                <Grid container spacing={2}>
                <Grid item sm={6} xs={12}>
                  <GlobitsTextField
                    type="password"
                    label={t("user.password")}
                    validate
                    name="password"
                  />
                </Grid>
                <Grid item sm={6} xs={12}>
                  <GlobitsTextField
                    type="password"
                    validate
                    label={t("user.rePassword")}
                    name="confirmPassword"
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
                      onClick={() =>  setOpen(false)}
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
                  </div>
                </DialogActions>
              </div>
            </Form>
          )}
        </Formik>
      </GlobitsPopup>
    );
  }
)