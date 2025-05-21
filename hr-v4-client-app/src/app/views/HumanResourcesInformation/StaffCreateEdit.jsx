import { Button, DialogActions } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import ConstantList from "app/appConfig";
import { Staff } from "app/common/Model/Staff";
import { Form, Formik } from "formik";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import { useHistory } from "react-router-dom";
import * as Yup from "yup";
import FormikFocusError from "../../common/FormikFocusError";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useStore } from "../../stores";
import TabIndex from "./TabContainer/TabIndex";
import { getByCodeCountry } from "../Country/CountryService";
import { DefaultInformationStaff } from "../../LocalConstants";
import { getReligionByCode } from "../Religion/ReligionService";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { observer } from "mobx-react";
import { getEthnicsByCode } from "../Ethnics/EthnicsService";
import { toast } from "react-toastify";

export default observer (function EditUpdateStaff (pro) {
  const {t} = useTranslation ();
  const {id} = useParams ();
  const history = useHistory ();

  const {staffStore, countryStore} = useStore ();

  const {
    getStaff,
    saveStaffWithoutAccount,
    setTabIndexValue,
    tabIndexValue,
    handleClose,
    handleDismissPositions,
    shouldDismissPositions,
  } = staffStore;

  const [staff, setStaff] = useState ({... new Staff ()});

  const validationSchema = Yup.object ({
    // Tab 1: thong tin ca nhan
    displayName:Yup.string ()
        .ensure ()
        .max (100, "Không được nhập quá 100 ký tự")
        // .matches(/^[^\d]*$/, "Dữ liệu không hợp lệ")
        .test ("no_special_chars", "Dữ liệu chứa ký tự đặc biệt", (value) => {
          const specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
          return ![... value].some ((char) => specialChars.includes (char));
        })
        .test ("length_fullName", "Dữ liệu không hợp lệ", (val) => val.trim ().split (" ").length >= 1)
        .required (t ("validation.required"))
        .nullable (),
    email:Yup.string ()
        .ensure ()
        .email ("Email không hợp lệ") // Kiểm tra định dạng email
        .max (100, "Email không được vượt quá 100 ký tự")
        .required (t ("validation.required"))
        .nullable (),
    permanentResidence:Yup.string ()
        .max (200, "Không được nhập quá 200 ký tự")
        .required (t ("validation.required"))
        .nullable (),
    currentResidence:Yup.string ().max (200, "Không được nhập quá 200 ký tự").nullable (),
    birthPlace:Yup.string ().max (200, "Không được nhập quá 200 ký tự").nullable (),
    socialInsuranceNote:Yup.string ().max (200, "Không được nhập quá 200 ký tự").nullable (),
    taxCode:Yup.string ().max (13, "Không được nhập quá 13 ký tự").nullable (),
    startDate:Yup.date ()
        .nullable ()
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        .required (t ("validation.required"))
        .typeError ("Dữ liệu sai định dạng."),
    birthDate:Yup.date ()
        .nullable ()
        .max (new Date (), "Ngày sinh phải nhỏ hơn ngày hiện tại.")
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        .typeError ("Dữ liệu sai định dạng."),

    idNumber:Yup.string ()
        .matches (/^\d+$/, "Chỉ được nhập số")
        .max (12, "Số không được quá 12 ký tự")
        .nullable (),

    idNumberIssueDate:Yup.date ()
        .nullable ()
        .max (new Date (), "Ngày cấp phải nhỏ hơn ngày hiện tại.")
        .transform ((castValue, originalValue) => originalValue? new Date (originalValue) : castValue)
        .typeError ("Dữ liệu sai định dạng."),

    idNumberIssueBy:Yup.string ()
        .max (200, "Không được nhập quá 200 ký tự")
        .nullable (),

    personalIdentificationNumber:Yup.string ()
        .matches (/^[0-9]{12}$/, "CCCD phải có đúng 12 số")
        .nullable (),

    personalIdentificationIssueDate:Yup.date ()
        .nullable ()
        .max (new Date (), "Ngày cấp phải nhỏ hơn ngày hiện tại.")
        .transform ((castValue, originalValue) => originalValue? new Date (originalValue) : castValue)
        .typeError ("Dữ liệu sai định dạng."),

    personalIdentificationIssuePlace:Yup.string ()
        .max (200, "Không được nhập quá 200 ký tự")
        .nullable (),

    apprenticeDays:Yup.string ().required (t ("validation.required")).nullable (),
    phoneNumber:Yup.string ().max (11, "Không được nhập quá 11 ký tự").required (t ("validation.required")).nullable (),
    // Tab 2: Thong tin ho so nhan vien
    status:Yup.object ().required (t ("validation.required")).nullable (),
    province:Yup.object ().required (t ("validation.required")).nullable (),
    district:Yup.object ().required (t ("validation.required")).nullable (),
    administrativeunit:Yup.object ().nullable (),
    nationality:Yup.object ().required (t ("validation.required")).nullable (),
    staffCode:Yup.string ().required (t ("validation.required")).nullable (), // civilServantType: Yup.object()
// ---- RULE TỔNG THỂ ĐỂ KIỂM TRA 2 NHÓM GIẤY TỜ ----
  });
  const setDefaultValues = () => {
    if (id) {
      getStaff (id)
          .then ((data) => {
            const staffData = {... data};

            if (!staffData?.staffPercentage) staffData.staffPercentage = 10.5;
            if (!staffData?.orgPercentage) staffData.orgPercentage = 21.5;

            setStaff (staffData);
            return staffData;
          })
          .then ((data) => {
            if (!data?.nationality?.id?.length > 0)
              getByCodeCountry (DefaultInformationStaff.COUNTRY.code).then ((nationality) => {
                if (nationality?.data?.id?.length > 0) {
                  setStaff ((prev) => ({... prev, nationality:nationality?.data}));
                }
              });
            if (!data?.religion?.id?.length > 0)
              getReligionByCode (DefaultInformationStaff.RELIGION.code).then ((religion) => {
                if (religion?.data?.id?.length > 0) {
                  setStaff ((prev) => ({... prev, religion:religion?.data}));
                }
              });
            if (!data?.ethnics?.id)
              getEthnicsByCode (DefaultInformationStaff.ETHNICS.code).then ((ethnics) => {
                if (ethnics?.data?.id) {
                  setStaff ((prev) => ({... prev, ethnics:ethnics?.data}));
                }
              });
          })
          .catch ((error) => console.error (error));
    } else {
      if (!staff?.nationality?.id)
        getByCodeCountry (DefaultInformationStaff.COUNTRY.code).then ((nationality) => {
          if (nationality?.data?.id) {
            setStaff ((prev) => ({... prev, nationality:nationality?.data}));
          }
        });

      if (!staff?.religion?.id)
        getReligionByCode (DefaultInformationStaff.RELIGION.code).then ((religion) => {
          if (religion?.data?.id) {
            setStaff ((prev) => ({... prev, religion:religion?.data}));
          }
        });

      if (!staff?.ethnics?.id)
        getEthnicsByCode (DefaultInformationStaff.ETHNICS.code).then ((ethnics) => {
          if (ethnics?.data?.id) {
            setStaff ((prev) => ({... prev, ethnics:ethnics?.data}));
          }
        });
    }
  };

  useEffect (() => {
    setDefaultValues ();
  }, [id, getStaff]);

  async function handleSubmitForm (staff) {
    // Lấy các trường cần kiểm tra từ staff
    const nationality = staff.nationality;
    const idNumber = staff.idNumber;
    const idNumberIssueDate = staff.idNumberIssueDate;
    const idNumberIssueBy = staff.idNumberIssueBy;
    const personalIdentificationNumber = staff.personalIdentificationNumber;
    const personalIdentificationIssueDate = staff.personalIdentificationIssueDate;
    const personalIdentificationIssuePlace = staff.personalIdentificationIssuePlace;

    const isVietnamese = nationality?.code === DefaultInformationStaff.COUNTRY.code;

    const isCMNDGroupFilled = idNumber && idNumberIssueDate && idNumberIssueBy;
    const isCCCDGroupFilled = personalIdentificationNumber && personalIdentificationIssueDate && personalIdentificationIssuePlace;

    if (isVietnamese && !isCMNDGroupFilled && !isCCCDGroupFilled) {
      toast.error ('Phải nhập đầy đủ 1 trong 2 nhóm giấy tờ (CMND hoặc CCCD)');
      return;
    }

    try {

      const data = await saveStaffWithoutAccount (staff);

      if (!staff.id && data?.id) {
        history.push (ConstantList.ROOT_PATH + `staff/edit/${data?.id}`);
      }
    } catch (error) {
      toast.error ('Lỗi khi lưu thông tin nhân viên');
      console.error (error);
    }
  }

  const submitDismissPositions = async () => {
    await handleDismissPositions ();
    await setDefaultValues ();
  }

  const [screenTitle, setScreenTitle] = useState ("");
  const [currentTab, setCurrentTab] = useState ({});

  const handleChangeTabIndexValue = (event, newValue, title, tab) => {
    setScreenTitle (title);
    setTabIndexValue (newValue);
    setCurrentTab (tab);
  };

  return (
      <div className='content-index'>
        <div className='index-breadcrumb'>
          <GlobitsBreadcrumb
              routeSegments={[
                {name:t ("staff.title")},
                {name:staff?.id? staff?.displayName + " - " + screenTitle : "Thêm mới nhân viên"},
              ]}
          />
        </div>
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            validateOnBlur={true}
            validateOnChange={false}
            initialValues={{
              personalIdentificationNumber:null,
              personalIdentificationIssueDate:null,
              personalIdentificationIssuePlace:null,
              permanentResidence:null,
              phoneNumber:null,
              email:null,
              startDate:null,
              district:null,
              province:null,
              staffCode:null,
              ... staff,
            }}
            onSubmit={handleSubmitForm}>
          {({isSubmitting, values, setFieldValue}) => {
            return (
                <Form autoComplete='off'>
                  <FormikFocusError/>

                  <TabIndex value={tabIndexValue} handleChangeValue={handleChangeTabIndexValue}/>

                  {currentTab?.showActionButtons && (
                      <DialogActions
                          className='p-0 flex flex-end flex-middle p-12'
                          style={{
                            position:"fixed",
                            bottom:"0px",
                            left:"0px",
                            right:"0px",
                            border:"none",
                            backgroundColor:"#fff",
                          }}>
                        <Button
                            startIcon={<BlockIcon/>}
                            variant='contained'
                            className='mr-12 btn btn-secondary d-inline-flex'
                            color='secondary'
                            onClick={() => history.goBack ()}
                            disabled={isSubmitting}>
                          {t ("general.button.cancel")}
                        </Button>
                        <Button
                            startIcon={<SaveIcon/>}
                            className='mr-0 btn btn-primary d-inline-flex'
                            variant='contained'
                            color='primary'
                            type='submit'
                            disabled={isSubmitting}>
                          {t ("general.button.save")}
                        </Button>
                      </DialogActions>
                  )}
                </Form>
            );
          }}
        </Formik>
        {shouldDismissPositions && (
            <GlobitsConfirmationDialog
                open={shouldDismissPositions}
                onConfirmDialogClose={handleClose}
                onYesClick={submitDismissPositions}
                title={t ("Xác nhận bãi nhiệm chức vụ")}
                text={t ("Bạn có chắc chắn muốn bãi nhiệm tất cả các chức vụ hiện tại của nhân viên này?")}
                agree={t ("confirm_dialog.delete_list.agree")}
                cancel={t ("confirm_dialog.delete_list.cancel")}
            />
        )}
      </div>
  );
});
