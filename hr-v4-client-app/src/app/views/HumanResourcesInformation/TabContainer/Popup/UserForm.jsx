import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import CachedIcon from "@material-ui/icons/Cached";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsAsyncAutocomplete from "app/common/form/GlobitsAsyncAutocomplete";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import ChangePassWordAccordion from "app/views/User/ChangePassWordAccordion";
import ResetPassWord from "app/views/User/ResetPassWord";
import { getAllRoles, getFilterRoles } from "app/views/User/UserService";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import * as Yup from "yup";

function UserForm() {
  const { t } = useTranslation();

  const {
    userStore,
    staffStore,
    hrRoleUtilsStore,
  } = useStore();


  const {
    saveUser,
    updateUser
  } = userStore;

  const {
    checkAdmin
  } = hrRoleUtilsStore;

  const { handleClose, recentSWH, shouldOpenCreateUserDialog, selectedStaff } = staffStore;
  const [open, setOpen] = useState(false);
  const [isEditingAdminRole, setIsEditingAdminRole] = useState(false);
  const [user, setUser] = useState({
    id: null,
    displayName: null,
    gender: null,
    email: null,
    username: null,
    roles: [],
    password: null,
    confirmPassword: "",
    person: {
      gender: null,
      displayName: null,
    },
    active: true,
  });

  const validationSchema = Yup.object({
    // person: Yup.object().shape({
    //   displayName: Yup.string().required(t("validation.required")).nullable(),
    // }),
    username: Yup.string().required(t("validation.required")).nullable(),
    roles: Yup.array().required(t("validation.required")),
    // password: Yup.string().required(t("validation.required")).nullable(),
    confirmPassword: Yup.string()
      .nullable()
      .oneOf([Yup.ref("password"), null], t("validation.confirm_password")),
  });

  useEffect(() => {
    if (selectedStaff?.user) {
      setUser({ ...selectedStaff?.user, confirmPassword: selectedStaff?.user?.password, staff: { id: selectedStaff?.id, displayName: selectedStaff?.displayName } });

      const userRoles = selectedStaff?.user?.roles?.map((role) => role.name);
      const isAdmin = checkAdmin();
      const hasAdminRole = userRoles.includes("ROLE_ADMIN") || userRoles.includes("ROLE_SUPER_ADMIN");

      if (isAdmin) {
        setIsEditingAdminRole(false);
      } else {
        setIsEditingAdminRole(hasAdminRole);
      }

    } else {
      setUser({
        id: null,
        displayName: null,
        gender: null,
        email: null,
        username: null,
        roles: [],
        password: null,
        confirmPassword: "",
        person: {
          gender: null,
          displayName: null,
        },
        active: true,
        staff: { id: selectedStaff?.id, displayName: selectedStaff?.displayName },
      });
    }
  }, [selectedStaff?.user]);

  async function hanledFormSubmit(user) {
    if (user != null) {
      await saveUser(user);
      handleClose();
    }
  }

  return (
    <GlobitsPopupV2 size='md' open={shouldOpenCreateUserDialog} title={(selectedStaff?.user?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("user.title")} onClosePopup={handleClose} noDialogContent>
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={user}
        onSubmit={(values, actions) => {
          hanledFormSubmit(values);
          actions.setSubmitting(false);
        }}>
        {({ setFieldValue, values, isSubmitting, errors, touched }) => {
          return (
            <Form autoComplete='off'>
              <div className='dialog-body'>
                <DialogContent className='o-hidden p-12'>
                  <Grid container spacing={2}>
                    <Grid item sm={6} xs={12} md={4}>
                      <GlobitsTextField label={t("user.username")} validate name='username' />
                    </Grid>
                    <Grid item sm={6} xs={12} md={4}>
                      <GlobitsTextField type='email' label={t("user.email")} name='email' />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                      {/* Chọn nhân viên sử dụng tài khoản */}
                      <ChooseUsingStaffSection disabled />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsAsyncAutocomplete
                        label={t("user.role.title")}
                        validate
                        name='roles'
                        multiple
                        api={getFilterRoles}
                        disabled={isEditingAdminRole}
                      />
                    </Grid>
                    {!Boolean(values?.id) && <ChangePassWordAccordion setFieldValue={setFieldValue} />}
                  </Grid>
                </DialogContent>
              </div>
              <div className='dialog-footer'>
                <DialogActions className='p-0'>
                  <div className='flex flex-space-between flex-middle'>
                    <Button
                      startIcon={<BlockIcon />}
                      variant='contained'
                      className='mr-12 btn btn-gray d-inline-flex'
                      // color="secondary"
                      onClick={() => handleClose()}>
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className='mr-0 btn btn-success d-inline-flex'
                      variant='contained'
                      // color="primary"
                      type='submit'>
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }}
      </Formik>
      <ResetPassWord open={open} setOpen={setOpen} />
    </GlobitsPopupV2>
  );
}

export default memo(observer(UserForm));
