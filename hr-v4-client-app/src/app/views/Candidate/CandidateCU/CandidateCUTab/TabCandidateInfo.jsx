import { Grid } from "@material-ui/core";
import GlobitsImageUpload from "app/common/form/FileUpload/GlobitsImageUpload";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsSelectInputV2 from "app/common/form/GlobitsSelectInputV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import LocalConstants, {EVALUATE_PERSON} from "app/LocalConstants";
import { pagingAdministratives } from "app/views/AdministrativeUnit/AdministrativeUnitService";
import { pagingCountry } from "app/views/Country/CountryService";
import { pagingEthnicities } from "app/views/Ethnics/EthnicsService";
import { pagingReligions } from "app/views/Religion/ReligionService";
import { Field, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import GlobitsDateTime from "../../../../common/form/GlobitsDateTime";
import GlobitsVNDCurrencyInput from "../../../../common/form/GlobitsVNDCurrencyInput";

function TabCandidateInfo () {
  const {values, setFieldValue} = useFormikContext ();
  const {t} = useTranslation ();

  return (
      <Grid container spacing={2}>
        <Grid item md={4} sm={12} xs={12}>
          <Field
              name="file"
              component={GlobitsImageUpload}
              onChange={setFieldValue}
              imagePath={values?.imagePath}
              nameStaff={values?.lastName}
          />
        </Grid>
        <Grid container item md={8} sm={12} xs={12} spacing={1}>
          <Grid item md={6} sm={6} xs={12}>
            <GlobitsTextField
                validate
                id={"lastName"}
                label={t("humanResourcesInformation.firstName")}
                name="lastName"
            />
          </Grid>
          <Grid item md={6} sm={6} xs={12}>
            <GlobitsTextField
                validate
                id={"firstName"}
                label={t ("humanResourcesInformation.lastName")}
                name="firstName"
            />
          </Grid>

          <Grid item md={6} sm={6} xs={12}>
            <GlobitsTextField
                disabled
                label={t ("humanResourcesInformation.name")}
                name="displayName"
                id={"displayName"}
                value={(values?.lastName?.trim () || "") + ' ' + (values?.firstName?.trim () || "")}
            />
          </Grid>

          <Grid item md={6} sm={6} xs={12}>
            <GlobitsSelectInput
                label={t ("user.gender")}
                name="gender"
                keyValue="id"
                id={"gender"}
                options={LocalConstants.ListGender}
            />
          </Grid>
          <Grid item md={6} sm={6} xs={12}>
            <GlobitsDateTimePicker
                label={t ("humanResourcesInformation.birthDate")}
                name="birthDate"
                id={"birthDate"}
                disableFuture={true}
            />
          </Grid>

          <Grid item md={6} sm={6} xs={12}>
            <GlobitsSelectInput
                label={t ("humanResourcesInformation.maritalStatus")}
                name="maritalStatus"
                keyValue="value"
                id={"maritalStatus"}
                options={LocalConstants.ListMaritalStatus}
            />
          </Grid>
        </Grid>
        <Grid container item md={12} sm={12} xs={12} spacing={1}>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsPagingAutocompleteV2
                label={t ("Nguyên quán/Nơi sinh")}
                name="nativeVillage"
                id={"nativeVillage"}
                api={pagingAdministratives}
                searchObject={{level:3}}
                value={values?.nativeVillage}
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsTextField
                label={t ("humanResourcesInformation.permanentResidence")}
                name="permanentResidence"
                id={"permanentResidence"}
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsTextField
                label={t (
                    "humanResourcesInformation.accommodationToday"
                )}
                id={"currentResidence"}
                name="currentResidence"
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsPagingAutocompleteV2
                label={t ("humanResourcesInformation.province")}
                name="province"
                id={"province"}
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
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsPagingAutocompleteV2
                label={t ("humanResourcesInformation.district")}
                name="district"
                id={"district"}
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

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsPagingAutocompleteV2
                label={t ("humanResourcesInformation.wards")}
                name="administrativeUnit"
                api={pagingAdministratives}
                id={"administrativeUnit"}
                searchObject={{
                  level:1,
                  districtId:values?.district?.id,
                }}
                allowLoadOptions={!!values?.district?.id}
                clearOptionOnClose
            />
          </Grid>


          <Grid item md={3} sm={6} xs={12}>
            <GlobitsNumberInput
                label={t ("humanResourcesInformation.identityCardNumber")}
                name="idNumber"
                id={"idNumber"}
                inputProps={{maxLength:12}}
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsDateTimePicker
                label={t ("humanResourcesInformation.dateRange")}
                name="idNumberIssueDate"
                id={"idNumberIssueDate"}
                disableFuture={true}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsTextField
                label={t ("humanResourcesInformation.licensePlace")}
                id={"idNumberIssueBy"}
                name="idNumberIssueBy"
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsNumberInput
                label={t ("humanResourcesInformation.personalIdentificationNumber")}
                name="personalIdentificationNumber"
                id={"personalIdentificationNumber"}
                inputProps={{maxLength:12}}
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsDateTimePicker
                label={t ("humanResourcesInformation.personalIdentificationIssueDate")}
                name="personalIdentificationIssueDate"
                id={"personalIdentificationIssueDate"}
                disableFuture={true}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsTextField
                label={t ("humanResourcesInformation.personalIdentificationIssuePlace")}
                name="personalIdentificationIssuePlace"
                id={"personalIdentificationIssuePlace"}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsPagingAutocomplete
                label={t ("humanResourcesInformation.nationality")}
                name="nationality"
                id={"nationality"}
                api={pagingCountry}
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsPagingAutocomplete
                label={t ("humanResourcesInformation.ethnic")}
                name="ethnics"
                id={"ethnics"}
                api={pagingEthnicities}
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsPagingAutocomplete
                label={t ("humanResourcesInformation.religion")}
                name="religion"
                id={"religion"}
                api={pagingReligions}
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsNumberInput
                label={t ("humanResourcesInformation.phoneNumber")}
                name="phoneNumber"
                id={"phoneNumber"}
                inputProps={{maxLength:11}}
                required
            />
          </Grid>

          <Grid item md={3} sm={6} xs={12}>
            <GlobitsTextField
                type="email"
                label={t ("humanResourcesInformation.email")}
                name="email"
                id={"email"}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsSelectInputV2
                label={t ("Trạng thái hồ sơ")}
                name="status"
                id={"status"}
                options={LocalConstants.CandidateStatus.getListData ()}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsSelectInputV2
                label={t ("Trạng thái sơ lọc")}
                name="preScreenStatus"
                id={"preScreenStatus"}
                options={LocalConstants.PRE_SCREEN_STATUS.getListData ()}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsSelectInputV2
                label={t ("Trạng thái nhận việc")}
                name="onboardStatus"
                id={"onboardStatus"}
                options={LocalConstants.CandidateReceptionStatus.getListData ()}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsDateTimePicker
                label="Ngày nhận việc"
                name="onboardDate"
                id={"onboardDate"}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsVNDCurrencyInput
                label={"Lương cơ bản"}
                name='basicIncome'
                id={"basicIncome"}
                suffix={"VND"}
                onChange={(event) => {
                  setFieldValue("basicIncome", event.target.value);
                  setFieldValue("probationIncome", event.target.value*0.85);
                }}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsVNDCurrencyInput
                label={"Lương thử việc"}
                name='probationIncome'
                id={"probationIncome"}
                suffix={"VND"}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsVNDCurrencyInput
                label={"Thưởng vị trí"}
                name='positionBonus'
                id={"positionBonus"}
                suffix={"VND"}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsVNDCurrencyInput
                label={"Trợ cấp"}
                name='allowance'
                id={"allowance"}
                suffix={"VND"}
            />
          </Grid>
          <Grid item md={3} sm={6} xs={12}>
            <GlobitsVNDCurrencyInput
                label={"Trợ cấp khác"}
                name='otherBenefit'
                id={"otherBenefit"}
                suffix={"VND"}
            />
          </Grid>

        </Grid>
      </Grid>
  );
}

export default memo (observer (TabCandidateInfo));

