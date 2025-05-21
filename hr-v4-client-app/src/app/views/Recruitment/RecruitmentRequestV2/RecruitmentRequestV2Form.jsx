import {
    Box,
    Button,
    Checkbox,
    DialogActions,
    DialogContent,
    FormControlLabel,
    Grid,
    IconButton,
    TextField,
    Typography,
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import {useStore} from "app/stores";
import {Form, Formik} from "formik";
import React, {forwardRef, memo, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";
// import "./RequestStyle.scss";
import GlobitsEditor from "app/common/form/GlobitsEditor";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import {pagingAllDepartments} from "app/views/Department/DepartmentService";
import {pagingAllOrg} from "app/views/Organization/OrganizationService";
import {observer} from "mobx-react";
import GlobitsNumberInput from "../../../common/form/GlobitsNumberInput";
import GlobitsSelectInput from "../../../common/form/GlobitsSelectInput";
import LocalConstants, {
    CodePrefixes,
    EVALUATE_PERSON,
    HR_DEPARTMENT_TYPE_ENUM,
    ListGender
} from "../../../LocalConstants";
import {pagingPositionTitle} from "../../PositionTitle/PositionTitleService";
import FormGroup from "@material-ui/core/FormGroup";
import {pagingStaff} from "../../HumanResourcesInformation/StaffService";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import GlobitsVNDCurrencyInput from "../../../common/form/GlobitsVNDCurrencyInput";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import localStorageService from "../../../services/localStorageService";
import PositionRecruitmentRequestList from "./PositionRecruitmentRequestList";
import GlobitsConfirmationDialog from "../../../common/GlobitsConfirmationDialog";
import {pagingWorkplace} from "../../Workplace/WorkplaceService";

function RecruitmentRequestV2Form({readOnly}) {
    const {recruitmentRequestStore, hrRoleUtilsStore, positionTitleV2Store, staffStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        saveRecruitmentRequest,
        pagingRecruitmentRequest,
        selectedRecruitmentRequest,
        openCreateEditPopup,
        handelOpenDepartmentPopup,
        openViewPopup,
        handleOpenConfirmDialog,
        checkNumberIsWithinHeadcount,
        isNeedCheck,
        handleSavePayload,
        autoGenCode
    } = recruitmentRequestStore;
    const {isAdmin, checkAllUserRoles} = useStore().hrRoleUtilsStore;
    const currentUser = localStorageService.getLoginUser();
    const [generatedCode, setGeneratedCode] = useState("");

    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.YEU_CAU_TUYEN_DUNG);
        if (code) {
            setGeneratedCode(code)
        }
    };

    useEffect(() => {
        if(!selectedRecruitmentRequest.id){
            autoGenCodeFunc()
        }
        checkAllUserRoles();
    }, []);

    const initialValues = React.useMemo(() => ({
        id: selectedRecruitmentRequest.id,
        code: selectedRecruitmentRequest.id ? selectedRecruitmentRequest.code : generatedCode,
        name: selectedRecruitmentRequest.name,
        organization: selectedRecruitmentRequest.organization,
        hrDepartment: selectedRecruitmentRequest.hrDepartment,
        recruitingStartDate: selectedRecruitmentRequest.recruitingStartDate,
        recruitingEndDate: selectedRecruitmentRequest.recruitingEndDate,
        team: selectedRecruitmentRequest.team,
        workPlace: selectedRecruitmentRequest.workPlace,
        proposer: selectedRecruitmentRequest.proposer || (!isAdmin ? currentUser : null),
        proposalDate: selectedRecruitmentRequest.proposalDate || new Date(),
        proposalReceiptDate: selectedRecruitmentRequest.proposalReceiptDate,
        positionTitle: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.positionTitle,
        announcementQuantity: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.announcementQuantity,
        inPlanQuantity: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.inPlanQuantity,
        extraQuantity: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.extraQuantity,
        professionalLevel: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.professionalLevel,
        professionalSkills: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.professionalSkills,
        workType: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.workType,
        gender: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.gender,
        weight: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.weight,
        height: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.height,
        yearOfExperience: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.yearOfExperience,
        otherRequirements: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.otherRequirements,
        minimumAge: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.minimumAge,
        maximumAge: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.maximumAge,
        minimumIncome: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.minimumIncome,
        maximumIncome: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.maximumIncome,
        isWithinHeadcount: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.isWithinHeadcount,
        isReplacementRecruitment: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.isReplacementRecruitment,
        replacedPerson: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.replacedPerson,
        request: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.request,
        description: selectedRecruitmentRequest?.recruitmentRequestItems?.[0]?.description,
        positionRequests: selectedRecruitmentRequest?.positionRequests || [],
    }), [selectedRecruitmentRequest,generatedCode]);


    const validationSchema = Yup.object({
        name: Yup.string().required(t("validation.required")).nullable(),
        code: Yup.string().required(t("validation.required")).nullable(),
        minimumAge: Yup.number()
            .nullable()
            .transform((value, originalValue) => (originalValue === "" ? null : value))
            .min(0, "Tuổi từ không được âm"),
        maximumAge: Yup.number()
            .nullable()
            .transform((value, originalValue) => (originalValue === "" ? null : value))
            .min(0, "Tuổi đến không được âm")
            .when("minimumAge", (minAge, schema) =>
                minAge != null ? schema.min(minAge, "Tuổi đến phải lớn hơn hoặc bằng tuổi từ") : schema
            ),
        weight: Yup.number().nullable().min(0, "Cân nặng không được âm"),
        height: Yup.number().nullable().min(0, "Chiều cao không được âm"),
    });

    async function handleSaveForm(values) {
        try {
            if (values.isWithinHeadcount) {
                if(isNeedCheck){
                    if (values?.hrDepartment?.id && values?.positionTitle?.id && values?.inPlanQuantity) {
                        const check = await checkNumberIsWithinHeadcount(values?.hrDepartment?.id, values?.positionTitle?.id, values?.inPlanQuantity);
                        if (!check) {
                            handleSavePayload(values)
                            handleOpenConfirmDialog(true);
                            return;
                        }
                    }
                }
            }
            const response = await saveRecruitmentRequest(values);
            if (response) await pagingRecruitmentRequest();
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='md'
            open={true || openCreateEditPopup || openViewPopup}
            noDialogContent
            title={
                openViewPopup
                    ? t("general.button.view") + " " + t("navigation.recruitment.request")
                    : (selectedRecruitmentRequest?.id ? t("general.button.edit") : t("general.button.add")) +
                    " " +
                    t("navigation.recruitment.request")
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={(value) => handleSaveForm(value)}>
                {({isSubmitting, values, setFieldValue, initialValues}) => {
                    function handleClosePopup() {
                        setFieldValue("hrDepartment", initialValues?.hrDepartment);
                        handelOpenDepartmentPopup(false);
                    }

                    return (
                        <>
                            <Form autoComplete='off'>
                                <div className='dialog-body'>
                                    <DialogContent className='p-12'>
                                        <FormikFocusError/>
                                        <Typography style={{fontWeight: "700", fontSize: "large", color: "red"}}>
                                            I. YÊU CẦU TUYỂN DỤNG
                                        </Typography>
                                        <Grid container spacing={2}>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsTextField
                                                    label={"Mã yêu cầu"}
                                                    validate
                                                    name='code'
                                                    readOnly={readOnly}
                                                />
                                            </Grid>

                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsTextField
                                                    validate
                                                    label={"Tên yêu cầu"}
                                                    name='name'
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsPagingAutocompleteV2
                                                    disabled={!isAdmin}
                                                    name='proposer'
                                                    label='Người đề xuất'
                                                    api={pagingStaff}
                                                    getOptionLabel={(option) => {
                                                        return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                                    }}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsDateTimePicker
                                                    disabled={!isAdmin}
                                                    label={"Ngày đề xuất"}
                                                    name='proposalDate'
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsDateTimePicker
                                                    disabled={!isAdmin}
                                                    label={"Ngày nhận đề xuất"}
                                                    name='proposalReceiptDate'
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsPagingAutocompleteV2
                                                    name='organization'
                                                    label='Đơn vị'
                                                    api={pagingAllOrg}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>

                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Phòng/Cơ sở"}
                                                    validate
                                                    name='hrDepartment'
                                                    searchObject={{
                                                        organizationId: values?.organization?.id,
                                                        departmentTypeCode: [HR_DEPARTMENT_TYPE_ENUM.LPB_0005.value, HR_DEPARTMENT_TYPE_ENUM.LPB_0006.value],
                                                    }}
                                                    api={pagingAllDepartments}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Bộ phận/Nhóm"}
                                                    validate
                                                    name='team'
                                                    api={pagingAllDepartments}
                                                    searchObject={{
                                                        parentId: values?.hrDepartment?.id,
                                                        departmentTypeCode: [HR_DEPARTMENT_TYPE_ENUM.LPB_0007.value, HR_DEPARTMENT_TYPE_ENUM.LPB_0008.value],
                                                    }}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={"Chức danh"}
                                                    name={`positionTitle`}
                                                    api={pagingPositionTitle}
                                                    searchObject={{
                                                        departmentId: values?.team?.id || values?.hrDepartment?.id,
                                                    }}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsNumberInput
                                                    label={"Số lượng cần tuyển"}
                                                    name='announcementQuantity'
                                                    readOnly={readOnly}
                                                    onChange={(e) => {
                                                        setFieldValue("announcementQuantity",+e.target.value);
                                                        setFieldValue("inPlanQuantity",0);
                                                        setFieldValue("extraQuantity", 0);
                                                    }}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsNumberInput
                                                    label={"Số lượng cần tuyển trong định biên"}
                                                    name='inPlanQuantity'
                                                    readOnly={readOnly}
                                                    onChange={(e) => {
                                                        setFieldValue("inPlanQuantity", +e.target.value);
                                                        setFieldValue("announcementQuantity", +e.target.value + (+values?.extraQuantity || 0));
                                                    }}

                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsNumberInput
                                                    label={"Số lượng cần tuyển ngoài định biên"}
                                                    name='extraQuantity'
                                                    readOnly={readOnly}
                                                    onChange={(e) => {
                                                        setFieldValue("extraQuantity", e.target.value);
                                                        setFieldValue("announcementQuantity", +e.target.value + (+values?.inPlanQuantity || 0));
                                                    }}
                                                />
                                            </Grid>
                                            {/*<Grid item sm={6} xs={12} md={4}>*/}
                                            {/*    <GlobitsTextField*/}
                                            {/*        label={"Nơi làm việc"}*/}
                                            {/*        value={values?.workPlace || values?.department?.workPlace?.name || ""}*/}
                                            {/*        name={"workPlace"}*/}
                                            {/*        readOnly={readOnly}*/}
                                            {/*    />*/}
                                            {/*</Grid>*/}
                                                <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsPagingAutocompleteV2
                                                    name='workPlace'
                                                    label='Địa điểm làm việc'
                                                    api={pagingWorkplace}
                                                    readOnly={readOnly}
                                                    handleChange={(_, workplace) => {
                                                        setFieldValue ("workPlace", workplace);
                                                    }}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsSelectInput
                                                    label={"Hình thức làm việc"}
                                                    name={"workType"}
                                                    keyValue='id'
                                                    options={LocalConstants.WorkType}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsTextField
                                                    multiline
                                                    label={"Trình độ chuyên môn"}
                                                    name='professionalLevel'
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsTextField
                                                    multiline
                                                    label={"Kỹ năng chuyên môn"}
                                                    name='professionalSkills'
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <Box>
                                                    <Typography variant='subtitle2' gutterBottom>
                                                        Độ tuổi
                                                    </Typography>

                                                    <Box display='flex' alignItems='center' gap={1}>
                                                        <GlobitsNumberInput
                                                            type='number'
                                                            size='small'
                                                            name='minimumAge'
                                                            fullWidth
                                                            InputProps={{
                                                                endAdornment: (
                                                                    <span
                                                                        style={{
                                                                            marginRight: "8px",
                                                                            color: "#757575",
                                                                        }}>
                                                                    tuổi
                                                                </span>
                                                                ),
                                                            }}
                                                            readOnly={readOnly}
                                                        />

                                                        <Box
                                                            sx={{
                                                                height: "1px",
                                                                backgroundColor: "rgba(0, 0, 0, 0.2)",
                                                                width: "40px", // Độ dài gạch, bạn có thể thay bằng 100% nếu muốn gạch căng ra
                                                            }}
                                                        />

                                                        <GlobitsNumberInput
                                                            type='number'
                                                            size='small'
                                                            name='maximumAge'
                                                            fullWidth
                                                            InputProps={{
                                                                endAdornment: (
                                                                    <span
                                                                        style={{
                                                                            marginRight: "8px",
                                                                            color: "#757575",
                                                                        }}>
                                                                    tuổi
                                                                </span>
                                                                ),
                                                            }}
                                                            readOnly={readOnly}
                                                        />
                                                    </Box>
                                                </Box>
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsSelectInput
                                                    label={"Giới tính"}
                                                    name='gender'
                                                    keyValue='id'
                                                    options={ListGender}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsNumberInput
                                                    decimal={true}
                                                    label={"Chiều cao"}
                                                    type='number'
                                                    size='small'
                                                    name='height'
                                                    fullWidth
                                                    InputProps={{
                                                        endAdornment: (
                                                            <span
                                                                style={{
                                                                    marginRight: "8px",
                                                                    color: "#757575",
                                                                }}>
                                                            cm
                                                        </span>
                                                        ),
                                                    }}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsNumberInput
                                                    decimal={true}
                                                    label={"Cân nặng"}
                                                    type='number'
                                                    size='small'
                                                    name='weight'
                                                    fullWidth
                                                    InputProps={{
                                                        endAdornment: (
                                                            <span
                                                                style={{
                                                                    marginRight: "8px",
                                                                    color: "#757575",
                                                                }}>
                                                            kg
                                                        </span>
                                                        ),
                                                    }}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsNumberInput
                                                    label={"Số năm kinh nghiệm liên quan đến vị trí tuyển"}
                                                    type='number'
                                                    size='small'
                                                    name='yearOfExperience'
                                                    fullWidth
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item sm={8} xs={12} md={8}>
                                                <Box>
                                                    <Typography variant='subtitle2' gutterBottom>
                                                        Thu nhập đề xuất
                                                    </Typography>

                                                    <Box display='flex' alignItems='center' gap={1}>
                                                        <GlobitsVNDCurrencyInput
                                                            decimal={true}
                                                            size='small'
                                                            name='minimumIncome'
                                                            fullWidth
                                                            suffix={"VND"}
                                                            readOnly={readOnly}
                                                        />

                                                        <Box
                                                            sx={{
                                                                height: "1px",
                                                                backgroundColor: "rgba(0, 0, 0, 0.2)",
                                                                width: "40px", // Độ dài gạch, bạn có thể thay bằng 100% nếu muốn gạch căng ra
                                                            }}
                                                        />

                                                        <GlobitsVNDCurrencyInput
                                                            decimal={true}
                                                            size='small'
                                                            name='maximumIncome'
                                                            fullWidth
                                                            suffix={"VND"}
                                                            readOnly={readOnly}
                                                        />
                                                    </Box>
                                                </Box>
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsDateTimePicker
                                                    label={"Ngày bắt đầu tuyển dụng"}
                                                    name='recruitingStartDate'
                                                />
                                            </Grid>
                                            <Grid item sm={6} xs={12} md={4}>
                                                <GlobitsDateTimePicker
                                                    label={"Ngày kết thúc tuyển dụng"}
                                                    name='recruitingEndDate'
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <GlobitsTextField
                                                    label={"Yêu cầu khác"}
                                                    name='otherRequirements'
                                                    multiline
                                                    readOnly={readOnly}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <GlobitsEditor
                                                    label={"Mô tả công việc"}
                                                    name='description'
                                                    readOnly={readOnly}
                                                />
                                            </Grid>

                                            <Grid item xs={12}>
                                                <GlobitsEditor label={"Yêu cầu"} name='request' readOnly={readOnly}/>
                                            </Grid>
                                        </Grid>
                                        <Typography style={{fontWeight: "700", fontSize: "large", color: "red"}}>
                                            II. LÝ DO TUYỂN DỤNG
                                        </Typography>
                                        <Grid item xs={12} md={12}>
                                            <FormGroup>
                                                {[
                                                    {id: true, name: "1. Tuyển dụng trong định biên"},
                                                    {id: false, name: "2. Tuyển dụng mới ngoài định biên"},
                                                ].map((contract) => {
                                                    if (contract.id === true) {
                                                        return (
                                                            <>
                                                                <Grid container style={{display: "flex", gap: 12}}>
                                                                    <Grid
                                                                        key={contract.id}
                                                                        className='contract-row'
                                                                        item
                                                                        xs={12}
                                                                        md={12}>
                                                                        <FormControlLabel
                                                                            label={
                                                                                <Typography style={{fontWeight: "700"}}>
                                                                                    {contract.name}
                                                                                </Typography>
                                                                            }
                                                                            control={
                                                                                <Checkbox
                                                                                    name='isWithinHeadcount'
                                                                                    checked={
                                                                                        values?.isWithinHeadcount === true
                                                                                    }
                                                                                    onChange={(__, value) => {
                                                                                        if (value) {
                                                                                            setFieldValue(
                                                                                                "isWithinHeadcount",
                                                                                                true
                                                                                            );
                                                                                        } else {
                                                                                            setFieldValue(
                                                                                                "isWithinHeadcount",
                                                                                                null
                                                                                            );
                                                                                        }
                                                                                    }}
                                                                                    disabled={readOnly}
                                                                                    readOnly={readOnly}
                                                                                />
                                                                            }
                                                                        />
                                                                    </Grid>
                                                                </Grid>
                                                                {values?.isWithinHeadcount === contract.id && (
                                                                    <>
                                                                        {[
                                                                            {
                                                                                id: true,
                                                                                name: "Tuyển thay thế (ghi rõ tên nhân sự được thay thế)",
                                                                            },
                                                                            {id: false, name: "Tuyển mới"},
                                                                        ].map((value) => {
                                                                            if (value.id === true) {
                                                                                return (
                                                                                    <div style={{marginLeft: "8px"}}>
                                                                                        <FormGroup row>
                                                                                            <FormControlLabel
                                                                                                key={value.id}
                                                                                                control={
                                                                                                    <Checkbox
                                                                                                        name='isReplacementRecruitment'
                                                                                                        checked={
                                                                                                            values?.isReplacementRecruitment ===
                                                                                                            value.id
                                                                                                        }
                                                                                                        onChange={(
                                                                                                            __,
                                                                                                            value
                                                                                                        ) => {
                                                                                                            if (value) {
                                                                                                                setFieldValue(
                                                                                                                    "isReplacementRecruitment",
                                                                                                                    true
                                                                                                                );
                                                                                                            } else {
                                                                                                                setFieldValue(
                                                                                                                    "isReplacementRecruitment",
                                                                                                                    null
                                                                                                                );
                                                                                                            }
                                                                                                        }}
                                                                                                        disabled={readOnly}
                                                                                                        readOnly={readOnly}
                                                                                                    />
                                                                                                }
                                                                                                label={value.name}
                                                                                            />
                                                                                        </FormGroup>
                                                                                        {values.isReplacementRecruitment ===
                                                                                            true && (
                                                                                                <Grid item xs={12}>
                                                                                                    {/* <ChooseUsingStaffSection
                                                                                                label='Nhân sự được thay thế'
                                                                                                placeholder='Nhân sự được thay thế'
                                                                                                name='replacedPerson'
                                                                                                readOnly={readOnly}
                                                                                            /> */}
                                                                                                    <PositionRecruitmentRequestList/>
                                                                                                </Grid>
                                                                                            )}
                                                                                    </div>
                                                                                );
                                                                            } else {
                                                                                return (
                                                                                    <div style={{marginLeft: "8px"}}>
                                                                                        <FormControlLabel
                                                                                            key={value.id}
                                                                                            control={
                                                                                                <Checkbox
                                                                                                    name='isReplacementRecruitment'
                                                                                                    checked={
                                                                                                        values?.isReplacementRecruitment ===
                                                                                                        value.id
                                                                                                    }
                                                                                                    onChange={(
                                                                                                        __,
                                                                                                        value
                                                                                                    ) => {
                                                                                                        if (value) {
                                                                                                            setFieldValue(
                                                                                                                "isReplacementRecruitment",
                                                                                                                false
                                                                                                            );
                                                                                                        } else {
                                                                                                            setFieldValue(
                                                                                                                "isReplacementRecruitment",
                                                                                                                null
                                                                                                            );
                                                                                                        }
                                                                                                    }}
                                                                                                    disabled={readOnly}
                                                                                                    readOnly={readOnly}
                                                                                                />
                                                                                            }
                                                                                            label={value.name}
                                                                                        />
                                                                                    </div>
                                                                                );
                                                                            }
                                                                        })}
                                                                    </>
                                                                )}
                                                            </>
                                                        );
                                                    } else {
                                                        return (
                                                            <>
                                                                <FormControlLabel
                                                                    key={contract.id}
                                                                    label={
                                                                        <Typography style={{fontWeight: "700"}}>
                                                                            {contract.name}
                                                                        </Typography>
                                                                    }
                                                                    control={
                                                                        <Checkbox
                                                                            name='isWithinHeadcount'
                                                                            checked={
                                                                                values?.isWithinHeadcount === contract.id
                                                                            }
                                                                            onChange={(__, value) => {
                                                                                if (value) {
                                                                                    setFieldValue(
                                                                                        "isWithinHeadcount",
                                                                                        false
                                                                                    );
                                                                                } else {
                                                                                    setFieldValue(
                                                                                        "isWithinHeadcount",
                                                                                        null
                                                                                    );
                                                                                }
                                                                            }}
                                                                            disabled={readOnly}
                                                                            readOnly={readOnly}
                                                                        />
                                                                    }
                                                                />
                                                                {values?.isWithinHeadcount === contract.id && (
                                                                    <GlobitsTextField
                                                                        label={"Lý do"}
                                                                        name={"reason"}
                                                                        multiline
                                                                        readOnly={readOnly}
                                                                    />
                                                                )}
                                                            </>
                                                        );
                                                    }
                                                })}
                                            </FormGroup>
                                        </Grid>
                                    </DialogContent>
                                </div>

                                <div className='dialog-footer dialog-footer-v2 py-8'>
                                    <DialogActions className='p-0'>
                                        <div className='flex flex-space-between flex-middle'>
                                            <Button
                                                startIcon={<BlockIcon/>}
                                                variant='contained'
                                                className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                                color='secondary'
                                                onClick={() => handleClose()}>
                                                {t("general.button.cancel")}
                                            </Button>
                                            <Button
                                                startIcon={<SaveIcon/>}
                                                className='mr-0 btn btn-primary d-inline-flex'
                                                variant='contained'
                                                color='primary'
                                                type='submit'
                                                disabled={isSubmitting}>
                                                {t("general.button.save")}
                                            </Button>
                                        </div>
                                    </DialogActions>
                                </div>
                            </Form>
                        </>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(RecruitmentRequestV2Form));
