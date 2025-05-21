import { Grid, AppBar, Tabs, Tab, Box, Typography } from "@material-ui/core";
import ConstantList from "../../appConfig";
import React, { useState, useEffect } from "react";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import { Formik, Form } from "formik";
import { useStore } from "../../stores";
import { useParams } from "react-router";
import { useHistory } from "react-router-dom";
import moment from "moment";
import FormikFocusError from "../../common/FormikFocusError";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import StaffInformationView from "./TabViewStaff/StaffInformationView";
import StaffTimeSheetLmpl from "./TabViewStaff/StaffTimeSheetLmpl";

function TabPanel(props) {
  const { children, value, index, ...other } = props;
  return (
    <React.Fragment>
      <div
        role="tabpanel"
        hidden={value !== index}
        id={`scrollable-force-tabpanel-${index}`}
        aria-labelledby={`scrollable-force-tab-${index}`}
        {...other}
      >
        {value === index && (
          <Box p={3}>
            <Typography>{children}</Typography>
          </Box>
        )}
      </div>
    </React.Fragment>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: "100%",
    backgroundColor: theme.palette.background.paper,
  },
  tabHeader: {
    // boxShadow: "none!important",
    boxShadow:
      "rgba(50, 50, 105, 0.15) 0px 2px 5px 0px, rgba(0, 0, 0, 0.05) 0px 1px 1px 0px !important",
    marginBottom: "15px",
  },
  indicator: {
    display: "none",
  },
}));

export default function EditUpdateStaff(props) {
  const { t } = useTranslation();
  const [staff, setStaff] = useState({
    id: "",
    //tab1 start
    firstName: "",
    lastName: "",
    displayName: "",
    gender: "",
    birthDate: null,
    birthPlace: "",
    permanentResidence: "",
    currentResidence: "",
    accommodationToday: "",
    idNumber: "",
    idNumberIssueDate: null,
    idNumberIssueBy: "",
    nationality: null,
    ethnics: null,
    religion: null,
    email: "",
    phoneNumber: "",
    maritalStatus: "",

    /* new */
    administrativeunit: null,
    district: null,
    province: null,
    familyComeFrom: null,
    familyPriority: null,
    familyYourself: null,
    //tab1 end

    //tab2 start
    status: null,
    department: null,
    staffCode: null,
    civilServantType: null,
    civilServantCategory: null,
    grade: null,
    labourAgreementType: null,
    contractDate: null,
    recruitmentDate: null,
    professionalTitles: "",
    profession: null,
    highestPosition: "",
    dateOfReceivingPosition: null,
    positionDecisionNumber: "",
    currentWorkingStatus: null,
    startDate: null,
    jobTitle: "",
    allowanceCoefficient: "",
    dateOfReceivingAllowance: null,
    salaryLeve: "",
    salaryCoefficient: "",
    salaryStartDate: null,
    socialInsuranceNumber: "",

    //tab2 end

    //tab3 start
    personCertificate: [
      {
        certificate: null,
        issueDate: null,
        level: "",
        name: "",
      },
    ],

    ethnicLanguage: false,
    physicalEducationTeacher: false,

    studying: null,
    highSchoolEducation: "",

    qualification: "",
    specializedName: "",

    formsOfTraining: "",
    trainingPlaces: "",

    graduationYear: null,
    trainingCountry: "",

    academicRank: null,
    yearOfRecognitionAcademicRank: null,

    degree: null,
    yearOfRecognitionDegree: null,

    /* new */
    politicalTheoryLevel: null,
    stateManagementQualifications: null,
    educationalManagementQualifications: null,
    computerSkill: null,

    englishLevel: null,
    // graduationYear: null,

    englishCertificate: null,
    certificationScore: "",

    yearOfCertification: null,
    note: "",

    otherLanguage: null,
    otherLanguageLevel: null,

    conferred: null,
    yearOfConferred: null,

    //tab3 end

    //qua trinh chuc vu
    positions: [
      {
        decisionCode: null,
        decisionDate: null,
        fromDate: null,
        toDate: null,
        position: null,
        department: null,
        allowanceCoefficient: null,
        note: "",
        current: false,
        connectedAllowanceProcess: false,
        positionSelect: "1",
      },
    ],

    // quan he than nhan
    familyRelationships: [
      {
        fullName: "",
        profession: null,
        birthDate: null,
        familyRelationship: null,
        address: "",
        workingPlace: "",
      },
    ],
    // qua trinh dao tao
    educationHistory: [
      {
        startDate: null,
        endDate: null,

        actualGraduationYear: null,
        returnDate: null,

        educationalInstitution: null,
        country: null,

        decisionCode: null,
        decisionDate: null,

        fundingSource: null,

        extendDateByDecision: null,
        extendDecisionCode: null,
        extendDecisionDate: null,

        speciality: null,
        major: null,
        educationType: null,

        educationDegree: null,
        isConfirmation: false,

        basis: "",
        description: "",

        isCurrent: false,
        isCountedForSeniority: false,
        isExtended: false,
        notFinish: false,
      },
    ],

    // Hop dong
    agreements: [
      {
        signedDate: null,
        startDate: null,
        endDate: null,
        agreementStatus: null,
        labourAgreementType: null,
      },
    ],

    // Qua trinh dong BHXH
    stafInsuranceHistory: [
      {
        startDate: null,
        endDate: null,
        note: "",
        salaryCofficient: null,
        insuranceSalary: null,
        staffPercentage: null,
        orgPercentage: null,
        staffInsuranceAmount: null,
        orgInsuranceAmount: null,
      },
    ],

    // Qua trinh luong
    salaryHistory: [
      {
        coefficient: null,
        staffTypeCode: "",
        coefficientOverLevel: null,
        percentage: null,
        decisionDate: null,
        decisionCode: "",
        salaryIncrementType: null,
      },
    ],

    // Qua trinh cong tac nuoc ngoai
    overseasWorkHistory: [
      {
        startDate: null,
        endDate: null,
        country: null,
        companyName: "",
        decisionNumber: null,
        decisionDate: null,
        purpose: "",
      },
    ],

    // Qua trinh khen thuong
    rewardHistory: [
      { organizationName: "", rewardDate: null, rewardType: null },
    ],

    //Qua trinh thai san
    maternityHistory: [
      {
        startDate: null,
        endDate: null,
        birthNumber: null,
        note: "",
      },
    ],

    // Qua trinh buoi duong
    trainingHistory: [
      {
        startDate: null,
        endDate: null,
        trainingPlace: "",
        trainingCountry: null,
        certificate: null,
        trainingContent: "",
      },
    ],

    //Qua trinh cong tac
    workingHistory: [
      {
        employeeStatus: null,
        startDate: null,
        endDate: null,
        position: null,
        department: null,
        note: "",
        unpaidLeave: false,
      },
    ],

    //Qua trinh phu cap
    allowanceHistory: [
      {
        startDate: null,
        endDate: null,
        allowanceType: null,
        coefficient: null,
        note: "",
      },
    ],

    // Qua trinh phu cap tham nien nghe giao
    allowanceSeniorityHistory: [
      {
        startDate: null,
        quotaCode: null,
        percentReceived: null,
        note: "",
      },
    ],
  });

  const { staffStore } = useStore();
  const { createStaff, editStaff, getStaff } = staffStore;

  const classes = useStyles();
  const [value, setValue] = React.useState(0);
  const { id } = useParams();

  const history = useHistory();
  const handleChangeValue = (event, newValue) => {
    setValue(newValue);
  };
  useEffect(() => {
    if (id)
      getStaff(id).then((data) =>
        setStaff({
          ...data,
          administrativeunit: data?.administrativeunit,
          district: data?.administrativeunit?.parent,
          province: data?.administrativeunit?.province,
        })
      );
  }, [id, getStaff]);

  function hanledFormSubmit(staff) {
    if (staff.id.length === 0) {
      createStaff(staff).then((data) => {
        if (data === false) {
        } else {
          history.push(ConstantList.ROOT_PATH + "staff");
        }
      });
    } else {
      editStaff(staff).then((data) => {
        if (data === false) {
        } else {
          history.push(ConstantList.ROOT_PATH + "staff");
        }
      });
    }
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: t("staff.title") }]} />
      </div>

      <Grid className="index-card tab" container spacing={3}>
        <Grid className="tab-item" item xs={12}>
          <Formik
            enableReinitialize
            validateOnBlur={true}
            validateOnChange={false}
            initialValues={{
              ...staff,
              timeReport: 2,
              yearReport: new Date().getFullYear(),
              monthReport: moment(new Date()).format("MM"),
            }}
            onSubmit={hanledFormSubmit}
          >
            {() => (
              <Form autoComplete="off">
                <FormikFocusError />

                <div className="tab-container">
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <div className={classes.root} value={value} index={0}>
                        <AppBar
                          className={classes.tabHeader}
                          position="static"
                          color="#ffffff"
                        >
                          <Tabs
                            orientation="horizontal"
                            value={value}
                            onChange={handleChangeValue}
                            variant="scrollable"
                            scrollButtons="on"
                            textColor="primary"
                            aria-label="staff tabs scrollable"
                            classes={{
                              indicator: classes.indicator,
                            }}
                          >
                            <Tab label={t("humanResourcesInformation.personalInformation")} />
                          </Tabs>
                        </AppBar>
                        <div className="dialog-body">
                          <TabPanel
                            value={value}
                            index={0}
                            style={{ height: "auto" }}
                            color="#ffffff"
                          >
                            <StaffInformationView id={id} />
                          </TabPanel>
                          <TabPanel
                            value={value}
                            index={1}
                            style={{ height: "auto" }}
                            color="#ffffff"
                          >
                            <StaffTimeSheetLmpl id={id} />
                          </TabPanel>
                        </div>
                      </div>
                    </Grid>
                  </Grid>
                </div>
              </Form>
            )}
          </Formik>
        </Grid>
      </Grid>
    </div>
  );
}
