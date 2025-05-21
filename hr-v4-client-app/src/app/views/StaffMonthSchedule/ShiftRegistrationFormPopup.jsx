import {
  Button,
  DialogActions,
  DialogContent,
  Grid,
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Form, Formik } from "formik";
import { memo, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "app/LocalConstants";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { pagingShiftWork } from "app/views/ShiftWork/ShiftWorkService";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import { saveShiftRegistration } from "../ShiftRegistration/ShiftRegistrationService";
import localStorageService from "app/services/localStorageService";
import { toast } from "react-toastify";
import { getStaff } from "../HumanResourcesInformation/StaffService";
import ShiftRegistrationHistory from "./ShiftRegistrationHistory";

function ShiftRegistrationFormPopup(props) {
  const { t } = useTranslation();

  const {
    ShiftRegistrationStore,
    hrRoleUtilsStore
  } = useStore();

  const {
    isAdmin,
    isManager
  } = hrRoleUtilsStore;


  const {
    handleCloseConfirmDeletePopup,
    handleClose,
    pagingShiftRegistration,
    selectedShiftRegistration,
    openFormShiftRegristration,
    handleResetSelectedShiftRegistration,
    listShiftRegistrations,
    searchObject,
    handleSetSearchObject
  } = ShiftRegistrationStore;

  const validationSchema = Yup.object({
    shiftWork: Yup.object().required(t("validation.required")).nullable(),
    registerStaff: Yup.object().required(t("validation.required")).nullable(),
    workingDate: Yup.string().required(t("validation.required")).nullable(),
  });

  async function handleSaveForm(values) {
    try {
      const response = await saveShiftRegistration(values);
      toast.success("Thông tin đăng kí ca làm việc đã được lưu");
      if (response) await pagingShiftRegistration();
    } catch (error) {
      console.error(error);
    }
  }

  const [initialValues, setInitialValues] = useState(selectedShiftRegistration);

  useEffect(function () {
    let registerStaff = {
      id: props?.staffId
    };
    if (registerStaff == null) {
      registerStaff = localStorageService.getLoginUser();
    }

    setInitialValues(selectedShiftRegistration?.id ? selectedShiftRegistration : {
      ...selectedShiftRegistration,
      registerStaff: registerStaff,
      registerStaffId: registerStaff?.id,

    });

    handleSetSearchObject({
      ...searchObject,
      registerStaff: registerStaff,
      registerStaffId: registerStaff?.id,
    })


    pagingShiftRegistration();
  }, [selectedShiftRegistration, selectedShiftRegistration?.id]);

  useEffect(() => {
    const fetchData = async () => {
      if (props?.staffId) {
        const response = await getStaff(props?.staffId)
        setInitialValues({
          ...initialValues,
          registerStaff: response?.data
        })
      }
    }

    fetchData()
  }, [props?.staffId])




  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="lg"
      open={openFormShiftRegristration}
      noDialogContent
      title={"Đăng ký ca làm việc"}
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
            <Form autoComplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={8}>

                      <Grid container spacing={2}>
                        <Grid item xs={12} className="pb-0">
                          <p className="m-0 p-0 borderThrough2">
                            {`Lịch sử đăng ký ca làm việc ${initialValues?.registerStaff?.displayName ? initialValues?.registerStaff?.displayName : ""}`}
                          </p>
                        </Grid>

                        <Grid item xs={12}>
                          <div
                          //  className="dialogScrollContent"
                          >
                            <ShiftRegistrationHistory
                            />
                          </div>
                        </Grid>

                      </Grid>

                    </Grid>

                    <Grid item xs={12} sm={4}>
                      <Grid container spacing={2}>
                        {/* <Grid item xs={12} className="pb-0">
                          <p className="pb-0">
                            <strong>
                              
                            </strong>
                          </p> 
                        </Grid> */}

                        <Grid item xs={12} className="pb-0">
                          <p className="m-0 p-0 borderThrough2">
                            Đăng ký ca
                          </p>
                        </Grid>

                        <Grid item xs={12}>
                          <ChooseUsingStaffSection
                            label={t("Nhân viên đăng ký")}
                            name="registerStaff"
                            required
                            readOnly={
                              true
                              // !(isAdmin || isManager)
                            }
                          />
                        </Grid>

                        {/* <Grid item xs={12}>
                          <ChooseUsingStaffSection
                            label={t("Người phê duyệt")}
                            name="approvalStaff"
                          />
                        </Grid> */}

                        <Grid item xs={12}>
                          <GlobitsPagingAutocomplete
                            name='shiftWork' label={t("Ca làm việc")}
                            api={pagingShiftWork}
                            required
                          />
                        </Grid>

                        <Grid item xs={12}>
                          <GlobitsDateTimePicker
                            label={t("Ngày làm việc")}
                            name="workingDate"
                            required
                          />
                        </Grid>

                        {/* {isAdmin && (
                          <Grid item xs={12}>
                            <GlobitsSelectInput
                              hideNullOption
                              label={"Trạng thái"}
                              name="approvalStatus"
                              keyValue="value"
                              options={LocalConstants.ShiftRegistrationApprovalStatus.getListData()}
                            />
                          </Grid>
                        )} */}


                      </Grid>
                    </Grid>
                  </Grid>

                </DialogContent>
              </div>

              <div className="dialog-footer dialog-footer-v2 py-8">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-secondary d-inline-flex"
                      color="secondary"
                      onClick={async () => {
                        await handleResetSelectedShiftRegistration()
                        await setInitialValues({
                          ...selectedShiftRegistration,
                          registerStaff: localStorageService.getLoginUser(),
                        });
                      }}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    {selectedShiftRegistration?.approvalStatus === LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value &&
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
                    }

                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }}
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(ShiftRegistrationFormPopup));

