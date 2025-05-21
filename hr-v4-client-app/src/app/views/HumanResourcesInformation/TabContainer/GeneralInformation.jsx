import React, { useEffect, useState } from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import { useTranslation } from "react-i18next";
import { Grid, makeStyles, TextField, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { pagingCountry } from "../../Country/CountryService";
import { pagingEthnicities } from "../../Ethnics/EthnicsService";
import { pagingReligions } from "../../Religion/ReligionService";
import { Field, useFormikContext } from "formik";
import { pagingEmployeeStatus } from "../../EmployeeStatus/EmployeeStatusService";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import GlobitsSelectInput from "../../../common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "../../../common/form/GlobitsDateTimePicker";
import GlobitsImageUpload from "../../../common/form/FileUpload/GlobitsImageUpload";
import GlobitsPagingAutocomplete from "../../../common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";
import PersonCertificateTabIndex from "../PersonCertificate/PersonCertificateTabIndex";
import LocalConstants, { DefaultInformationStaff } from "../../../LocalConstants";
import { pagingAdministratives } from "app/views/AdministrativeUnit/AdministrativeUnitService";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import { pagingEducationDegrees } from "app/views/EducationDegree/EducationDegreeService";
import StaffFamilyRelationshipIndex from "../StaffFamilyRelationship/StaffFamilyRelationshipIndex";
import { pagingShiftWork } from "app/views/ShiftWork/ShiftWorkService";
import AnnualLeavePoupup from "app/views/User/UsingAccountStaff/AnnualLeavePoupup";

const useStyles = makeStyles ((theme) => ({
  root:{
    "& .MuiAccordion-rounded":{
      borderRadius:"5px",
    },

    "& .MuiPaper-root":{
      borderRadius:"5px",
    },

    "& .MuiAccordionSummary-root":{
      borderRadius:"5px", // backgroundColor: "#EBF3F9",
      color:"#5899d1 ",
      fontWeight:"400",

      "& .MuiTypography-root":{
        fontSize:"1rem",
      },
    },

    "& .Mui-expanded":{
      "& .MuiAccordionSummary-root":{
        backgroundColor:"#EBF3F9",
        color:"#5899d1 ", // borderLeft: "3px solid rgba(88, 153, 202, 0.84) !important",
        fontWeight:"700",
        maxHeight:"50px !important",
        minHeight:"50px !important",
      },
      "& .MuiTypography-root":{
        fontWeight:700,
      },
    },

    "& .MuiButton-root":{
      borderRadius:"0.125rem !important",
    },
  },
}));

export default observer (function GeneralInformation () {
  const {t} = useTranslation ();
  const classes = useStyles ();

  const [openDepartmentPopup, setOpenDepartmentPopup] = useState (false);
  const [openPersonCertificate, setOpenPersonCertificate] = useState (false);
  const [openStaffFamilyRelationShip, setOpenStaffFamilyRelationShip] = useState (false);

  const {departmentStore, staffSocialInsuranceStore, staffStore} = useStore ();

  const {setShouldDismissPositions, setStaffStatusDismiss} = staffStore;
  const {updatePageData} = departmentStore;

  const {values, setFieldValue, initialValues, errors, touched} = useFormikContext ();

  useEffect (() => {
    updatePageData ();
  }, [updatePageData]);

  function handleClosePopup () {
    setFieldValue ("department", initialValues?.department);
    setOpenDepartmentPopup (false);
  }

  function splitFullName (fullName = "") {
    const parts = fullName.trim ().split (/\s+/);
    const firstName = parts.pop () || ""; // Lấy tên (từ cuối)
    const lastName = parts.join (" ") || ""; // Họ + tên đệm
    return {firstName, lastName};
  }

  const handleChange = (name, e) => {
    let val = e.target.value || "";

    // Xóa toàn bộ khoảng trắng
    val = val.replace (/\s+/g, "");

    // Chỉ giữ lại số
    val = val.replace (/\D/g, "");

    // Cắt tối đa 12 ký tự
    val = val.slice (0, 12);

    setFieldValue (name, val);
  };
  return (
      <React.Fragment>
        <Grid container spacing={2} className={classes.root}>
          <Grid item md={12} sm={12} xs={12}>
            <Grid container spacing={2}>
              <Grid item md={12} sm={12} xs={12}>
                <TabAccordion title='Thông tin cá nhân'>
                  <Grid container spacing={2}>
                    <Grid item md={4} sm={12} xs={12}>
                      <Field
                          name='file'
                          component={GlobitsImageUpload}
                          onChange={setFieldValue}
                          imagePath={values?.imagePath}
                          nameStaff={initialValues.lastName}
                      />
                    </Grid>
                    <Grid container item md={8} sm={12} xs={12} spacing={1}>
                      <Grid item md={4} sm={4} xs={12}>
                        <GlobitsTextField
                            validate
                            label={t ("humanResourcesInformation.name")}
                            name='displayName'
                            onChange={(e) => {
                              const {firstName, lastName} = splitFullName (e.target.value);
                              setFieldValue ("firstName", firstName);
                              setFieldValue ("lastName", lastName);
                            }}
                        />
                      </Grid>

                      {/* <Grid item md={6} sm={6} xs={12}>
                                            <GlobitsTextField
                                                validate
                                                label={t("humanResourcesInformation.firstName")}
                                                name='lastName'
                                                readOnly={true}
                                            />
                                        </Grid>
                                        <Grid item md={6} sm={6} xs={12}>
                                            <GlobitsTextField
                                                validate
                                                label={t("humanResourcesInformation.lastName")}
                                                name='firstName'
                                                readOnly={true}
                                            />
                                        </Grid> */}

                      <Grid item md={2} sm={2} xs={4}>
                        <GlobitsSelectInput
                            label={t ("user.gender")}
                            name='gender'
                            keyValue='id'
                            options={LocalConstants.ListGender}
                        />
                      </Grid>
                      <Grid item md={3} sm={3} xs={3}>
                        <GlobitsDateTimePicker
                            label={t ("humanResourcesInformation.birthDate")}
                            name='birthDate'
                            disableFuture={true}
                            onChange={(date) => {
                              const parsedDate = date? new Date (date) : null;
                              setFieldValue ("birthDate", parsedDate);
                            }}
                        />
                      </Grid>

                      <Grid item md={3} sm={3} xs={6}>
                        <GlobitsSelectInput
                            label={t ("humanResourcesInformation.maritalStatus")}
                            name='maritalStatus'
                            keyValue='value'
                            options={LocalConstants.ListMaritalStatus}
                        />
                      </Grid>

                      <Grid item md={4} sm={6} xs={12}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("Tỉnh thường trú")}
                            name='province'
                            required
                            validate
                            value={values?.province}
                            api={pagingAdministratives}
                            searchObject={{level:3}}
                            handleChange={(_, value) => {
                              setFieldValue ("province", value);
                              setFieldValue ("district", null);
                              setFieldValue ("administrativeunit", null);
                            }}
                        />
                      </Grid>

                      <Grid item md={4} sm={6} xs={12}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("Huyện thường trú")}
                            name='district'
                            required
                            validate
                            value={values?.district}
                            api={pagingAdministratives}
                            searchObject={{
                              level:2,
                              provinceId:values?.province?.id,
                            }}
                            allowLoadOptions={!!values?.province?.id}
                            clearOptionOnClose
                            handleChange={(_, value) => {
                              setFieldValue ("district", value);
                              setFieldValue ("administrativeunit", null);
                            }}
                        />
                      </Grid>

                      <Grid item md={4} sm={6} xs={12}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("Xã thường trú")}
                            name='administrativeunit'
                            api={pagingAdministratives}
                            searchObject={{
                              level:1,
                              districtId:values?.district?.id,
                            }}
                            allowLoadOptions={!!values?.district?.id}
                            clearOptionOnClose
                        />
                      </Grid>
                      <Grid item md={4} sm={4} xs={12}>
                        <GlobitsTextField
                            label={t ("humanResourcesInformation.permanentResidence")}
                            name='permanentResidence'
                            required
                            validate
                        />
                      </Grid>

                      <Grid item md={4} sm={4} xs={12}>
                        <GlobitsTextField
                            label={t ("humanResourcesInformation.accommodationToday")}
                            name='currentResidence'
                        />
                      </Grid>

                      <Grid item md={4} sm={4} xs={12}>
                        <GlobitsTextField
                            label={t ("humanResourcesInformation.birthPlace")}
                            name='birthPlace'
                        />
                      </Grid>
                    </Grid>

                    <Grid container item md={12} sm={12} xs={12} spacing={1}>
                      <Grid item md={2} sm={2} xs={4}>
                        <label htmlFor={"idNumber"} className={'label-container'}>
                          <span>{t ("humanResourcesInformation.identityCardNumber")}</span>
                        </label>
                        <TextField
                            className={"input-container"}
                            name="idNumber"
                            value={values.idNumber || ""}
                            onChange={(e) => {
                              handleChange ("idNumber", e)
                            }}
                            variant="outlined"
                            size="small"
                            fullWidth
                            error={touched.idNumber && !!errors.idNumber}
                            helperText={touched.idNumber && errors.idNumber}
                        />
                      </Grid>

                      <Grid item md={2} sm={2} xs={4}>
                        <GlobitsDateTimePicker
                            label={t ("humanResourcesInformation.dateRange")}
                            name='idNumberIssueDate'
                            disableFuture={true}
                            onChange={(date) => {
                              const parsedDate = date? new Date (date) : null;
                              setFieldValue ("idNumberIssueDate", parsedDate);
                            }}
                        />

                      </Grid>
                      <Grid item md={4} sm={4} xs={6}>
                        <GlobitsTextField
                            label={t ("humanResourcesInformation.licensePlace")}
                            name='idNumberIssueBy'
                        />
                      </Grid>

                      <Grid item md={2} sm={2} xs={2}>
                        <label htmlFor={"personalIdentificationNumber"} className={'label-container'}>
                          <span>Số CCCD</span>
                        </label>
                        <TextField
                            className={"input-container"}
                            name="personalIdentificationNumber"
                            value={values.personalIdentificationNumber || ""}
                            onChange={(e) => {
                              handleChange ("personalIdentificationNumber", e)
                            }}
                            variant="outlined"
                            size="small"
                            fullWidth
                            error={touched.personalIdentificationNumber && !!errors.personalIdentificationNumber}
                            helperText={touched.personalIdentificationNumber && errors.personalIdentificationNumber}
                        />
                      </Grid>

                      <Grid item md={2} sm={2} xs={2}>
                        <GlobitsDateTimePicker
                            label={t ("humanResourcesInformation.personalIdentificationIssueDate")}
                            name='personalIdentificationIssueDate'
                            // required={values?.nationality?.code === DefaultInformationStaff.COUNTRY.code}
                            disableFuture={true}
                            onChange={(date) => {
                              console.log (date);
                              const parsedDate = date? new Date (date) : null;
                              setFieldValue ("personalIdentificationIssueDate", parsedDate);
                            }}
                        />
                      </Grid>

                      <Grid item md={3} sm={3} xs={6}>
                        <GlobitsTextField
                            label={t ("humanResourcesInformation.personalIdentificationIssuePlace")}
                            name='personalIdentificationIssuePlace'
                            // required={values?.nationality?.code === DefaultInformationStaff.COUNTRY.code}
                        />
                      </Grid>

                      <Grid item md={1} sm={1} xs={2}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("humanResourcesInformation.nationality")}
                            name='nationality'
                            required
                            validate
                            api={pagingCountry}
                        />
                      </Grid>

                      <Grid item md={1} sm={1} xs={2}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("humanResourcesInformation.ethnic")}
                            name='ethnics'
                            api={pagingEthnicities}
                        />
                      </Grid>

                      <Grid item md={1} sm={1} xs={2}>
                        <GlobitsPagingAutocompleteV2
                            label={t ("humanResourcesInformation.religion")}
                            name='religion'
                            api={pagingReligions}
                        />
                      </Grid>

                      <Grid item md={2} sm={2} xs={4}>
                        <GlobitsPagingAutocomplete
                            label={"Trình độ học vấn"}
                            name='educationDegree'
                            api={pagingEducationDegrees}
                        />
                      </Grid>

                      <Grid item md={2} sm={2} xs={4}>
                        <GlobitsTextField
                            label={t ("humanResourcesInformation.email")}
                            name='email'
                            required
                            validate
                        />
                      </Grid>

                      <Grid item md={2} sm={2} xs={4}>
                        <GlobitsNumberInput
                            label={t ("humanResourcesInformation.phoneNumber")}
                            name='phoneNumber'
                            required
                            validate
                            inputProps={{maxLength:11}}
                        />
                      </Grid>
                    </Grid>
                  </Grid>
                </TabAccordion>
              </Grid>

              <Grid item xs={12}>
                <TabAccordion title='Thông tin hồ sơ nhân viên'>
                  <Grid container spacing={2}>

                    <Grid item lg={2} md={2} sm={2} xs={4}>
                      <GlobitsTextField
                          required
                          validate
                          label={"Mã nhân viên"}
                          name='staffCode'
                          // readOnly={values?.id}
                      />
                    </Grid>

                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsPagingAutocompleteV2
                          required
                          validate
                          label={t ("humanResourcesInformation.status")}
                          name='status'
                          api={pagingEmployeeStatus}
                          handleChange={(_, value) => {
                            setFieldValue ("status", value);
                            if (value?.code === LocalConstants.DismissPositions.DA_NGHI_VIEC) {
                              setShouldDismissPositions (true);
                              setStaffStatusDismiss (value);
                            }
                          }}
                      />
                    </Grid>

                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsSelectInput
                          hideNullOption
                          label='Hình thức làm việc'
                          name='staffWorkingFormat'
                          keyValue='value'
                          options={LocalConstants.StaffWorkingFormat.getListData ()}
                      />
                    </Grid>

                    <Grid item lg={3} md={4} sm={6} xs={12}>
                      <ChooseUsingStaffSection
                          label='Người giới thiệu'
                          placeholder='Người giới thiệu'
                          name='introducer'
                          //disabled={!isAdmin}
                      />
                    </Grid>

                    <Grid item lg={3} md={4} sm={6} xs={12}>
                      <ChooseUsingStaffSection
                          label='Người quyết định tuyển dụng'
                          placeholder='Người quyết định tuyển dụng'
                          name='recruiter'
                          //disabled={!isAdmin}
                      />
                    </Grid>

                    {/* Ngày tuyển dụng = ngày vào */}
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsDateTimePicker
                          label={t ("humanResourcesInformation.recruitmentDate")}
                          name='recruitmentDate'
                          required
                          onChange={(value) => {
                            setFieldValue ("recruitmentDate", value);

                            // Tính toán lại ngày bắt đầu nếu có số ngày thử việc
                            if (value && values.apprenticeDays) {
                              try {
                                const calculatedDate = new Date (value);
                                calculatedDate.setDate (
                                    calculatedDate.getDate () + parseInt (values.apprenticeDays)
                                );
                                setFieldValue ("startDate", calculatedDate);
                              } catch (error) {
                                console.error ("Lỗi tính toán ngày:", error);
                              }
                            } else {
                              setFieldValue ("startDate", value);
                            }
                          }}
                      />

                    </Grid>
                    {" "}

                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsVNDCurrencyInput
                          label={"Số ngày thử việc/học việc"}
                          name='apprenticeDays'
                          required
                          validate
                          onChange={(value) => {
                            setFieldValue ("apprenticeDays", value?.target?.value);

                            // Tính toán lại ngày bắt đầu nếu có cả ngày tuyển dụng
                            if (values.recruitmentDate && value?.target?.value) {
                              try {
                                const calculatedDate = new Date (values.recruitmentDate);
                                calculatedDate.setDate (
                                    calculatedDate.getDate () + parseInt (value?.target?.value)
                                );
                                setFieldValue ("startDate", calculatedDate);
                              } catch (error) {
                                console.error ("Lỗi tính toán ngày:", error);
                              }
                            }
                          }}
                      />
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsDateTimePicker
                          label={t ("Ngày bắt đầu chính thức")}
                          name='startDate'
                          required
                          validate
                      />
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsTextField label={"Email công ty"} name='companyEmail'/>
                    </Grid>

                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsSelectInput
                          label={"Tình trạng TV/CT/HV"}
                          name='staffPhase'
                          keyValue='value'
                          options={LocalConstants.StaffPhase.getListData ()}
                      />
                    </Grid>

                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsSelectInput
                          label={"Loại vị trí việc làm"}
                          name='staffPositionType'
                          keyValue='value'
                          hideNullOption
                          options={LocalConstants.StaffPositionType.getListData ()}
                      />
                    </Grid>

                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsTextField
                          label={t ("Thông tin người liên hệ")}
                          name='contactPersonInfo'
                      />
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsNumberInput
                          label={"Mã số thuế"}
                          name='taxCode'
                          inputProps={{maxLength:13}}
                      />
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsTextField
                          label={t ("Mã số BHXH")}
                          name='socialInsuranceNumber'
                          // inputProps={{ maxLength: 10 }}
                      />
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsTextField label={"Mã số BHYT"} name='healthInsuranceNumber'/>
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsTextField label={"Tình trạng sổ BHXH"} name='socialInsuranceNote'/>
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsTextField
                          label={"Nơi mong muốn ĐKKCB"}
                          name='desireRegistrationHealthCare'
                      />
                    </Grid>

                    {values?.nationality?.code !== DefaultInformationStaff.COUNTRY.code && (
                        <Grid item lg={2} md={2} sm={4} xs={4}>
                          <GlobitsNumberInput
                              label={"Số giấy phép lao động"}
                              name='workPermitNumber'
                          />
                        </Grid>
                    )}

                    {values?.nationality?.code !== DefaultInformationStaff.COUNTRY.code && (
                        <Grid item lg={2} md={2} sm={4} xs={4}>
                          <GlobitsNumberInput label={"Số sổ hộ chiếu"} name='passportNumber'/>
                        </Grid>
                    )}
                    <Grid item lg={2} md={2} sm={4} xs={4} className={"flex align-end"}>
                      <GlobitsCheckBox
                          label={t ("Có đóng BHXH")}
                          name='hasSocialIns'
                      />
                    </Grid>
                    <Grid item lg={2} md={2} sm={4} xs={4} className={"flex align-end"}>
                      <GlobitsCheckBox
                          label={"Bắt buộc chấm công"}
                          name='requireAttendance'
                          // checked={values?.requireAttendance}
                      />
                    </Grid>

                    {values?.requireAttendance && (
                        <Grid item lg={2} md={2} sm={4} xs={4} className={"flex align-end"}>
                          <GlobitsCheckBox
                              label={"Cho phép chấm công ngoài công ty"}
                              name='allowExternalIpTimekeeping'
                          />
                        </Grid>
                    )}


                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsSelectInput
                          label={"Loại phân ca"}
                          name='staffWorkShiftType'
                          keyValue='value'
                          hideNullOption
                          options={LocalConstants.StaffWorkShiftType.getListData ()}
                      />
                    </Grid>


                    {values?.staffWorkShiftType == LocalConstants.StaffWorkShiftType.FIXED.value && (
                        <Grid item lg={2} md={2} sm={4} xs={4}>
                          <GlobitsPagingAutocompleteV2
                              name='fixShiftWork'
                              label={"Ca làm việc cố định"}
                              api={pagingShiftWork}
                              searchObject={{
                                staffId:values?.id
                              }}
                              getOptionLabel={(option) =>
                                  option?.name && option?.code
                                      ? `${option.name} - ${option.code}`
                                      : option?.name || option?.code || ""
                              }
                          />
                        </Grid>

                    )}

                    <Grid item lg={2} md={2} sm={4} xs={4}>
                      <GlobitsSelectInput
                          label={"Loại nghỉ trong tháng"}
                          name='staffLeaveShiftType'
                          keyValue='value'
                          hideNullOption
                          options={LocalConstants.StaffLeaveShiftType.getListData ()}
                      />
                    </Grid>

                    {values?.staffLeaveShiftType == LocalConstants.StaffLeaveShiftType.FIXED.value && (
                        <Grid item lg={2} md={2} sm={4} xs={4}>
                          <GlobitsSelectInput
                              label={"Ngày nghỉ cố định đầu"}
                              name='fixLeaveWeekDay'
                              keyValue='value'
                              hideNullOption
                              options={LocalConstants.WeekDays.getListData ()}
                          />
                        </Grid>
                    )}

                    {values?.staffLeaveShiftType == LocalConstants.StaffLeaveShiftType.FIXED.value && (
                        <Grid item lg={2} md={2} sm={4} xs={4}>
                          <GlobitsSelectInput
                              label={"Ngày nghỉ cố định kế tiếp"}
                              name='fixLeaveWeekDay2'
                              keyValue='value'
                              hideNullOption
                              options={LocalConstants.WeekDays.getListData ()}
                          />
                        </Grid>
                    )}


                    <Grid item lg={2} md={2} sm={4} xs={4} className={"flex align-end"}>
                      <Tooltip
                          arrow
                          placement="top"
                          title="Nhân viên không được tính đi muộn về sớm ở chấm công"
                      >
                        <GlobitsCheckBox
                            label={"Không tính đi muộn, về sớm"}
                            name='skipLateEarlyCount'
                            // checked={values?.requireAttendance}
                        />
                      </Tooltip>
                    </Grid>

                    <Grid item lg={2} md={2} sm={4} xs={4} className={"flex align-end"}>
                      <Tooltip
                          arrow
                          placement="top"
                          title="Không tính làm thêm giờ và ngày vượt công nếu ô này được chọn"
                      >
                        <GlobitsCheckBox
                            label={"Không tính làm thêm giờ"}
                            name='skipOvertimeCount'
                            // checked={values?.requireAttendance}
                        />
                      </Tooltip>
                    </Grid>

                    {/* <Grid item lg={2} md={2} sm={4} xs={4}>
                      <AnnualLeavePoupup/>
                    </Grid> */}

                  </Grid>
                </TabAccordion>
              </Grid>
            </Grid>
          </Grid>
          {
              values?.id && (
                  <Grid item xs={12}>
                    <Grid container spacing={2}>
                      <Grid item md={6} sm={12} xs={12}>
                        <TabAccordion
                            open={openPersonCertificate}
                            handleOnClick={() => setOpenPersonCertificate (!openPersonCertificate)}
                            title='Chứng chỉ/chứng nhận'>
                          {openPersonCertificate && <PersonCertificateTabIndex/>}
                        </TabAccordion>
                      </Grid>

                      <Grid item md={6} sm={12} xs={12}>
                        <TabAccordion
                            open={openStaffFamilyRelationShip}
                            handleOnClick={() => setOpenStaffFamilyRelationShip (!openStaffFamilyRelationShip)}
                            title='Quan hệ thân nhân'>
                          {openStaffFamilyRelationShip && <StaffFamilyRelationshipIndex/>}
                        </TabAccordion>
                      </Grid>
                    </Grid>
                  </Grid>
              )
          }
        </Grid>
      </React.Fragment>
  )
      ;
});
