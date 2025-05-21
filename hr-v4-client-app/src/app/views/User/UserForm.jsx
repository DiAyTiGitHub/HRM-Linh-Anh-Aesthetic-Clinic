import React, { useState, useEffect, memo } from "react";
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
import GlobitsAsyncAutocomplete from "../../common/form/GlobitsAsyncAutocomplete";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import { getAllRoles } from "./UserService";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import LocalConstants from "../../LocalConstants";
import "./UserStyle.scss";
import GlobitsPopup from "app/common/GlobitsPopup";
import { observer } from "mobx-react";
import ResetPassWord from "./ResetPassWord";
import ChangePassWordAccordion from "./ChangePassWordAccordion";
import ChooseUsingStaffSection from "./UsingAccountStaff/ChooseUsingStaffSection";
import CachedIcon from '@material-ui/icons/Cached';
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function UserForm ({readOnly}) {

  const {userStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();
  const {handleClose, selectedUser, saveUser, openViewPopup, shouldOpenEditorDialog} = userStore;
  const [open, setOpen] = useState (false);
  const [user, setUser] = useState ({
    id:null,
    displayName:null,
    gender:null,
    email:null,
    username:null,
    roles:[],
    password:null,
    confirmPassword:"",
    person:{
      gender:null,
      displayName:null,
    },
    active:true,
  });

  const validationSchema = Yup.object ({
    // person: Yup.object().shape({
    //   displayName: Yup.string().required(t("validation.required")).nullable(),
    // }),
    username:Yup.string ().required (t ("validation.required")).nullable (),
    roles:Yup.array ().required (t ("validation.required")),
    // password: Yup.string().required(t("validation.required")).nullable(),
    confirmPassword:Yup.string ()
        .nullable ()
        .oneOf ([Yup.ref ("password"), null], t ("validation.confirm_password")),

  });

  useEffect (() => {
    if (selectedUser) {
      setUser ({... selectedUser, confirmPassword:selectedUser.password});
    }
  }, [selectedUser]);

  async function hanledFormSubmit (user) {
    if (user != null) {
      await saveUser (user);
    }
  }

  const {isAdmin} = hrRoleUtilsStore;
  return (
      <GlobitsPopupV2
          size="md"
          open={shouldOpenEditorDialog || openViewPopup}
          title={openViewPopup? (t ("general.button.view") + " " + t ("user.title")) : ((selectedUser?.id? t ("general.button.edit") : t ("general.button.add")) + ' ' + t ("user.title"))}
          onClosePopup={handleClose}
          noDialogContent
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={user}
            onSubmit={(values, actions) => {
              hanledFormSubmit (values);
              actions.setSubmitting (false);
            }}
        >
          {({setFieldValue, values, isSubmitting, errors, touched}) => {
            return (
                <Form autoComplete="off">
                  <div className="dialog-body">
                    <DialogContent className="o-hidden p-12">
                      <Grid container spacing={2}>
                        <Grid item sm={6} xs={12} md={4}>
                          <GlobitsTextField
                              label={t ("user.username")}
                              validate
                              name="username"
                              readOnly={readOnly}
                          />
                        </Grid>
                        <Grid item sm={6} xs={12} md={4}>
                          <GlobitsTextField
                              type="email"
                              label={t ("user.email")}
                              name="email"
                              readOnly={readOnly}
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={4} className={"flex flex-end"}>

                          {/* Chọn nhân viên sử dụng tài khoản */}
                          <ChooseUsingStaffSection readOnly={readOnly}/>
                        </Grid>
                        <Grid item xs={12}>
                          <GlobitsAsyncAutocomplete
                              label={t ("user.role.title")}
                              validate
                              name="roles"
                              multiple
                              api={getAllRoles}
                              readOnly={readOnly}
                          />
                        </Grid>

                        {!Boolean (values?.id) && (

                            <ChangePassWordAccordion setFieldValue={setFieldValue} readOnly={readOnly}/>
                        )}
                      </Grid>
                    </DialogContent>
                  </div>
                  <div className="dialog-footer">
                    <DialogActions className="p-0">
                      <div className="flex flex-space-between flex-middle">
                        <Button
                            startIcon={<BlockIcon/>}
                            variant="contained"
                            className={`${isAdmin && !readOnly && "mr-12"} btn btn-gray d-inline-flex`}
                            // color="secondary"
                            onClick={() => handleClose ()}
                        >
                          {t ("general.button.cancel")}
                        </Button>

                        {(isAdmin && Boolean (values?.id) && !readOnly) && (
                            <Button
                                startIcon={<CachedIcon/>}
                                variant="contained"
                                className="mr-12 btn btn-info d-inline-flex"
                                // color="secondary"
                                onClick={() => setOpen (true)}
                            >
                              {t ("general.button.resetPassWord")}
                            </Button>
                        )}
                        {(isAdmin && !readOnly) && (
                            <Button
                                startIcon={<SaveIcon/>}
                                className="mr-0 btn btn-success d-inline-flex"
                                variant="contained"
                                // color="primary"
                                type="submit"
                            >
                              {t ("general.button.save")}
                            </Button>
                        )}
                      </div>
                    </DialogActions>
                  </div>
                </Form>
            );
          }}
        </Formik>
        <ResetPassWord open={open} setOpen={setOpen}/>
      </GlobitsPopupV2>
  );
}

export default memo (observer (UserForm));