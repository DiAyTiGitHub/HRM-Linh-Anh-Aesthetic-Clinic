import { makeAutoObservable } from "mobx";
import { getCurrentStaff, getCurrentUser, getUserByStaffId, uploadImage } from "../profile/ProfileService";
import i18n from "i18n";

import {
  createStaff,
  editStaff,
  getStaff,
} from "../HumanResourcesInformation/StaffService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import { changePassWord } from "../User/UserService";
import JwtAuthService from 'app/services/jwtAuthService';

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

export default class ProfileStore {
  currentStaff = {
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
  };
  currentUser = {};
  openFormChangePassWord = false;

  dataChangePassWord = {
    id: null,
    oldPassword: null, 
    password: null,
    confirmPassword: null
  }

  constructor() {
    makeAutoObservable(this);
  }

  handleOpenChangePassWord = () => this.openFormChangePassWord = true;

  handleClosePopup = () => this.openFormChangePassWord = false;

  setLoadingInitial = (state) => {
    this.loadingInitial = state;
  };

  updatePageData = async () => {
    let response = await getCurrentStaff();
    let response2 = await getCurrentUser();
    
    this.currentStaff = response.data;
    this.currentUser = response2.data;
    this.dataChangePassWord.id = response2.data.id
    this.setLoadingInitial(false);

    return response;
  };
  getPageProfileData = async (staffId) => {
    if(staffId) {
      let response = await getStaff(staffId);
      let response2 = await getUserByStaffId(staffId);
      
      this.currentStaff = response.data;
      this.currentUser = response2.data;
      this.dataChangePassWord.id = response2.data.id
      this.setLoadingInitial(false);
    }else {
      let response = await getCurrentStaff();
      let response2 = await getCurrentUser();
      
      this.currentStaff = response.data;
      this.currentUser = response2.data;
      this.dataChangePassWord.id = response2.data.id
      this.setLoadingInitial(false);
      
    }
  };
  
  updatePageDataStaff = async () => {
    let data = await getCurrentStaff();
    this.currentStaff = data.data;
    this.setLoadingInitial(false);
  };

  uploadImage = async (file) => {
    try {
      if (file != null) {
        const formData = new FormData();
        formData.append("uploadfile", file);
        let newObj = {
          formData: formData,
          id: this.currentStaff.id,
        };
        await uploadImage(newObj);
      }
      toast.success(i18n.t("toast.add_success"));
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  createStaff = async (staff) => {
    try {
      staff.displayName =
        staff.lastName + (staff.lastName ? " " : "") + staff.firstName;
      staff.staffCode = this.currentUser.username;
      await createStaff(staff);
      toast.success(i18n.t("toast.add_success"));
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  editStaff = async (staff) => {
    try {
      staff.displayName =
        staff.lastName + (staff.lastName ? " " : "") + staff.firstName;
      await editStaff(staff);
      toast.success(i18n.t("toast.update_success"));
    } catch (error) {
      console.log(error);
      toast.warning(i18n.t("toast.error"));
    }
  };

  submitFormChangePassWord = (value,isLogout) => {
    changePassWord(value).then(response => {
      toast.warning(response.data);
      console.log(response.data);
      if(isLogout === true && response.data === "success.passwordChanged"){
        JwtAuthService.logout()
      }
    }).catch((error) => {
      toast.warning('Mật khẩu cũ không đúng!');
    })
    this.handleClosePopup()
  }
}