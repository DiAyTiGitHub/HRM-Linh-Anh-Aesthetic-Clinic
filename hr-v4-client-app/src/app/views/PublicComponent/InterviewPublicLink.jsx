import {Box, Button, Grid, Typography} from "@material-ui/core";
import {Field, Form, Formik, useFormikContext} from "formik";
import GlobitsImageUpload from "../../common/form/FileUpload/GlobitsImageUpload";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import LocalConstants, {HttpStatus} from "../../LocalConstants";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";
import GlobitsPagingAutocomplete from "../../common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInputV2 from "../../common/form/GlobitsSelectInputV2";
import React from "react";
import {useTranslation} from "react-i18next";
import GlobitsVNDCurrencyInput from "../../common/form/GlobitsVNDCurrencyInput";
import TabCandidateEducationalHistory from "../Candidate/CandidateCU/CandidateCUTab/TabCandidateEducationalHistory";
import TabCandidateCertificate from "../Candidate/CandidateCU/CandidateCUTab/TabCandidateCertificate";
import TabCandidateWorkingExperience from "../Candidate/CandidateCU/CandidateCUTab/TabCandidateWorkingExperience";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import * as Yup from "yup";
import {
    pagingPublicAdministratives,
    pagingPublicCountry,
    pagingPublicEthnicities,
    pagingPublicReligions
} from "./PublicService";
import {saveCandidate} from "../../services/PublicServices";
import {t} from "../../common/CommonFunctions";
import {toast} from "react-toastify";
import i18n from "../../../i18n";
import {useParams} from "react-router-dom";
import FormikFocusError from "../../common/FormikFocusError";
import moment from "moment/moment";
import {Candidate} from "../../common/Model/Candidate/Candidate";

function splitFullName(fullName = "") {
    const cleaned = fullName.trim();
    if (!cleaned) return {firstName: "", lastName: ""};

    const parts = cleaned.split(/\s+/);
    const firstName = parts.pop() || ""; // tên chính (từ cuối)
    const lastName = parts.join(" ") || ""; // họ + đệm
    return {firstName, lastName};
}

export default function InterviewPublicLink() {
    const {t} = useTranslation();
    const {planId} = useParams()

    const handleFormSubmit = async (values) => {
        const payload = {
            ...values,
            displayName: values?.lastName + (values?.lastName ? " " : "") + values?.firstName,
        }
        const response = await saveCandidate(payload)
        if (response.status === HttpStatus.OK) {
            toast.success("Bạn đã gửi CV Linh Anh sẽ phản hồi sớm nhất có thể");
            window.location.href = '/public-router/thank-you';
        } else {
            toast.error(i18n.t("toast.error"));
        }
    }
    const validationSchema = Yup.object({
        // Tab 1: Thông tin cá nhân
        lastName: Yup.string()
            .ensure()
            .required(t("validation.required"))
            .matches(/^[^\d]*$/, "Dữ liệu không hợp lệ")
            .test("validation_lastName", "Dữ liệu chứa ký tự đặc biêt", (value) => {
                var specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
                for (let i = 0; i < specialChars?.length; i++) {
                    if (value.indexOf(specialChars[i]) > -1) {
                        return false;
                    }
                }
                return true;
            })
            .test("length_lastName", "Dữ liệu không hợp lệ", (val) => val.length > 1)
            .nullable(),
        firstName: Yup.string()
            .ensure()
            .required(t("validation.required"))
            .matches(/^[^\d]*$/, "Dữ liệu không hợp lệ")
            .test("validation_firstName", "Dữ liệu chứa ký tự đặc biêt", (value) => {
                var specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
                for (let i = 0; i < specialChars?.length; i++) {
                    if (value.indexOf(specialChars[i]) > -1) {
                        return false;
                    }
                }
                return true;
            })
            .test("length_firstName", "Dữ liệu không hợp lệ", (val) => val.length > 1)
            .nullable(),
        recruitmentPlan: Yup.object().nullable().required(t("validation.required")),
        birthDate: Yup.date()
            .transform((value, originalValue) => {
                if (typeof originalValue === 'number') {
                    // Nếu là timestamp -> ép sang Date
                    const date = new Date(originalValue);
                    return isNaN(date.getTime()) ? null : date;
                }
                return value;
            })
            .nullable()
            .required(t("validation.required")),
        gender: Yup.string().nullable().required(t("validation.required")),
        // Conditional validation for interviewDate based on status
        phoneNumber: Yup.string().max(11, "Không được nhập quá 11 ký tự").required(t("validation.required")).nullable(),
    });

    return (
        <>
            <Formik
                initialValues={{
                    ...new Candidate(),
                    recruitmentPlan: {
                        id: planId
                    },
                    submissionDate: new Date(),
                }}
                enableReinitialize
                validationSchema={validationSchema}

                onSubmit={(values) => handleFormSubmit(values)}
            >
                {({
                      isSubmitting,
                      values,
                      setFieldValue,
                      initialValues,
                      resetForm,
                      errors,
                      handleSubmit
                  }) => (
                    <Box
                        sx={{
                            backgroundColor: "#fff",
                            borderRadius: 2,
                            boxShadow: 3,
                            p: 3,
                            mt: 2,
                        }}
                    >
                        <Form autoComplete="off">
                            <FormikFocusError/>
                            <Grid container spacing={2} style={{gap: "10"}}>
                                <Grid item md={12} sm={12} xs={12}>
                                    <Box
                                        sx={{
                                            textAlign: 'center',
                                            mt: 4,
                                            mb: 4,
                                        }}
                                    >
                                        <Typography
                                            variant="h2"
                                            component="h1"
                                            sx={{
                                                fontFamily: `'Playfair Display', serif`,
                                                fontWeight: 700,
                                                textTransform: 'uppercase',
                                                letterSpacing: '3px',
                                                background: 'linear-gradient(45deg, #FF69B4, #BA55D3)',
                                                WebkitBackgroundClip: 'text',
                                                WebkitTextFillColor: 'transparent',
                                                animation: 'float 3s ease-in-out infinite',
                                                textShadow: '0 0 10px rgba(255,105,180,0.5)',
                                                '@keyframes float': {
                                                    '0%': {
                                                        transform: 'translateY(0)',
                                                    },
                                                    '50%': {
                                                        transform: 'translateY(-8px)',
                                                    },
                                                    '100%': {
                                                        transform: 'translateY(0)',
                                                    },
                                                },
                                            }}
                                        >
                                            Tuyển dụng Linh Anh
                                        </Typography>
                                    </Box>
                                </Grid> {/* Avatar / Image Upload */}
                                <Grid item md={4} sm={12} xs={12} sx={{
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    p: 2,
                                }}>
                                    <Field
                                        name="file"
                                        component={GlobitsImageUpload}
                                        onChange={setFieldValue}
                                        imagePath={values?.imagePath}
                                        nameStaff={values?.lastName}
                                    />
                                </Grid>

                                {/* Main Personal Info */}
                                <Grid container item md={8} sm={12} xs={12} spacing={2}>
                                    <Grid item md={4} sm={6} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("humanResourcesInformation.firstName")}
                                            name="lastName"
                                        />
                                    </Grid>
                                    <Grid item md={4} sm={6} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("humanResourcesInformation.lastName")}
                                            name="firstName"
                                        />
                                    </Grid>
                                    <Grid item md={4} sm={6} xs={12}>
                                        <GlobitsTextField
                                            disabled
                                            InputProps={{readOnly: true}}
                                            label={t("humanResourcesInformation.name")}
                                            name="displayName"
                                            value={
                                                (values?.lastName?.trim() || "") +
                                                " " +
                                                (values?.firstName?.trim() || "")
                                            }
                                            onChange={(e) => {
                                                const {firstName, lastName} = splitFullName(e.target.value);
                                                setFieldValue("displayName", e.target.value);
                                                setFieldValue("firstName", firstName);
                                                setFieldValue("lastName", lastName);
                                            }}
                                        />
                                    </Grid>
                                    <Grid item md={4} sm={6} xs={12}>
                                        <GlobitsSelectInput
                                            required
                                            label={t("user.gender")}
                                            name="gender"
                                            keyValue="id"
                                            options={LocalConstants.ListGender}
                                        />
                                    </Grid>
                                    <Grid item md={4} sm={6} xs={12}>
                                        <GlobitsDateTimePicker
                                            required
                                            label={t("humanResourcesInformation.birthDate")}
                                            name="birthDate"
                                            disableFuture
                                        />
                                    </Grid>
                                    <Grid item md={4} sm={6} xs={12}>
                                        <GlobitsSelectInput
                                            label={t("humanResourcesInformation.maritalStatus")}
                                            name="maritalStatus"
                                            keyValue="value"
                                            options={LocalConstants.ListMaritalStatus}
                                        />
                                    </Grid>
                                </Grid>

                                {/* Address & Identification */}
                                <Grid container item spacing={2}>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("Nguyên quán/Nơi sinh")}
                                            name="nativeVillage"
                                            api={pagingPublicAdministratives}
                                            searchObject={{level: 3}}
                                            value={values?.nativeVillage}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsTextField
                                            label={t("humanResourcesInformation.permanentResidence")}
                                            name="permanentResidence"
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsTextField
                                            label={t("humanResourcesInformation.accommodationToday")}
                                            name="currentResidence"
                                        />
                                    </Grid>

                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("humanResourcesInformation.province")}
                                            name="province"
                                            value={values?.province}
                                            api={pagingPublicAdministratives}
                                            searchObject={{level: 3}}
                                            handleChange={(_, value) => {
                                                setFieldValue("province", value);
                                                setFieldValue("district", null);
                                                setFieldValue("administrativeunit", null);
                                            }}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("humanResourcesInformation.district")}
                                            name="district"
                                            value={values?.district}
                                            api={pagingPublicAdministratives}
                                            searchObject={{
                                                level: 2,
                                                parentId: values?.province?.id,
                                            }}
                                            handleChange={(_, value) => {
                                                setFieldValue("district", value);
                                                setFieldValue("administrativeunit", null);
                                            }}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("humanResourcesInformation.wards")}
                                            name="administrativeUnit"
                                            api={pagingPublicAdministratives}
                                            searchObject={{
                                                level: 1,
                                                parentId: values?.district?.id,
                                            }}
                                        />
                                    </Grid>

                                    {/* CMND */}
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsNumberInput
                                            label={t("humanResourcesInformation.identityCardNumber")}
                                            name="idNumber"
                                            inputProps={{maxLength: 12}}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsDateTimePicker
                                            label={t("humanResourcesInformation.dateRange")}
                                            name="idNumberIssueDate"
                                            disableFuture
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsTextField
                                            label={t("humanResourcesInformation.licensePlace")}
                                            name="idNumberIssueBy"
                                        />
                                    </Grid>
                                    {/*CCCD*/}
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsNumberInput
                                            label={t("humanResourcesInformation.personalIdentificationNumber")}
                                            name="personalIdentificationNumber"
                                            inputProps={{maxLength: 12}}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsDateTimePicker
                                            label={t("humanResourcesInformation.personalIdentificationIssueDate")}
                                            name="personalIdentificationIssueDate"
                                            disableFuture
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsTextField
                                            label={t("humanResourcesInformation.personalIdentificationIssuePlace")}
                                            name="personalIdentificationIssuePlace"
                                        />
                                    </Grid>

                                    {/* Others */}
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("humanResourcesInformation.nationality")}
                                            name="nationality"
                                            api={pagingPublicCountry}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("humanResourcesInformation.ethnic")}
                                            name="ethnics"
                                            api={pagingPublicEthnicities}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("humanResourcesInformation.religion")}
                                            name="religion"
                                            api={pagingPublicReligions}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsNumberInput
                                            label={t("humanResourcesInformation.phoneNumber")}
                                            name="phoneNumber"
                                            required
                                            inputProps={{maxLength: 11}}
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsTextField
                                            type="email"
                                            label={t("humanResourcesInformation.email")}
                                            name="email"
                                        />
                                    </Grid>
                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsVNDCurrencyInput
                                            label={t("Mức lương kỳ vọng (VNĐ)")}
                                            name="desiredPay"
                                        />
                                    </Grid>

                                    <Grid item md={3} sm={6} xs={12}>
                                        <GlobitsDateTimePicker
                                            label={t("Ngày có thể bắt đầu làm việc")}
                                            name="possibleWorkingDate"
                                        />
                                    </Grid>
                                </Grid>
                                <Grid item>
                                    <TabCandidateEducationalHistory props={{public: true}}/>
                                </Grid>
                                <Grid item>
                                    <TabCandidateCertificate props={{public: true}}/>
                                </Grid>
                                <Grid item>
                                    <TabCandidateWorkingExperience props={{public: true}}/>
                                </Grid>
                                <Grid item md={12}>
                                    <div className='bg-white flex items-center justify-center gap-4'>
                                        <Button
                                            className="ml-8 d-inline-flex py-2 px-8 btnHrStyle btn-primary"
                                            type="submit">
                                            <SaveOutlinedIcon className="mr-6"/>
                                            Lưu thông tin
                                        </Button>
                                    </div>
                                </Grid>
                            </Grid>
                        </Form>
                    </Box>
                )}
            </Formik>
        </>
    )
}
const DisplayNameComponent = () => {
    const {values, setFieldValue} = useFormikContext();

    function splitFullName(fullName = "") {
        const cleaned = fullName.trim();
        if (!cleaned) return {firstName: "", lastName: ""};

        const parts = cleaned.split(/\s+/);
        const firstName = parts.pop() || ""; // tên chính (từ cuối)
        const lastName = parts.join(" ") || ""; // họ + đệm
        return {firstName, lastName};
    }

    return (
        <GlobitsTextField
            label={t("humanResourcesInformation.name")}
            name='displayName'
            onChange={(e) => {
                const {firstName, lastName} = splitFullName(e.target.value);
                setFieldValue("displayName", e.target.value);
                setFieldValue("firstName", firstName);
                setFieldValue("lastName", lastName);
            }}
        />
    );
};
