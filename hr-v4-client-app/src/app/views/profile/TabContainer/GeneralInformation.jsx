import {Accordion, AccordionDetails, AccordionSummary, Grid,} from "@material-ui/core";
import {formatDate} from "app/LocalFunction";
import {useFormikContext} from "formik";
import React, {memo, useState} from "react";
import LocalConstants from "../../../LocalConstants";
import PersonCertificate from "./PersonCertificate";
import StaffAgreements from "./StaffAgreements";
import StaffEducationHistory from "./StaffEducationHistory";
import StaffInsuranceHistory from "./StaffInsuranceHistory";
import StaffMaternityHistory from "./StaffMaternityHistory";
import StaffPositionHistory from "./StaffPositionHistory";
import StaffWorkingHistory from "./StaffWorkingHistory";
import Relatives from "./Relatives";
import {observer} from "mobx-react";
import Assets from "./Assets";
import StaffWorkingLocation from "./StaffWorkingLocation";
import StaffIntroduceCost from "./StaffIntroduceCost";
import StaffSignature from "./StaffSignature";
import StaffSalaryTemplate from "./StaffSalaryTemplate";
import StaffApplyProcess from "./StaffApplyProcess";
import StaffPersonBankAccount from "./StaffPersonBankAccount";
import StaffPosition from "./StaffPosition";

function GeneralInformation() {
    const {values} = useFormikContext();
    const [openPersonCertificate, setOpenPersonCertificate] = useState(false);
    const [openStaffFamilyRelationShip, setOpenStaffFamilyRelationShip] = useState(false);
    const [openStaffEducationHistory, setOpenStaffEducationHistory] = useState(false);
    const [openStaffAgreements, setOpenStaffAgreements] = useState(false);
    const [openStaffInsuranceHistory, setOpenStaffInsuranceHistory] = useState(false);
    const [openAssets, setOpenAssets] = useState(false);
    const [openStaffWorkingHistory, setOpenStaffWorkingHistory] = useState(false);
    const [openStaffMaternityHistory, setOpenStaffMaternityHistory] = useState(false);
    const [openStaffWorkingLocation, setOpenStaffWorkingLocation] = useState(false);
    const [openStaffIntroduceCost, setOpenStaffIntroduceCost] = useState(false);
    const [openStaffSignature, setOpenStaffSignature] = useState(false);
    const [openStaffSalaryTemplate, setOpenStaffSalaryTemplate] = useState(false);
    const [openStaffApplyProcess, setOpenStaffApplyProcess] = useState(false);
    const [openStaffPersonBankAccount, setOpenStaffPersonBankAccount] = useState(false);
    const [openStaffPosition, setOpenStaffPosition] = useState(false);


    return (
        <>
            <TabAccordionSummary title='Thông tin cá nhân'>
                <Grid container spacing={2}>
                    <Grid item sm={4} xs={12} className="flex-column gap-1">
                        <p>
                            <span className="text-muted mr-15">Họ và tên:</span>
                            <span>{values?.displayName}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Quê quán tỉnh:</span>
                            <span>{values?.province?.name}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Tình trạng hôn nhân: </span>
                            <span>{LocalConstants.ListMaritalStatus.find(e => e.value === values?.maritalStatus)?.name || ""}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Nơi ở hiện nay: </span>
                            <span>{values?.currentResidence}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Nơi cấp: </span>
                            <span>{values?.idNumberIssueBy}</span>
                        </p>
                        {/* <p>
              <span className="text-muted mr-15">Gia đình xuất thân: </span>
              <span>{LocalConstants.ListFamilyComeFrom.find(e => e.id === values?.familyComeFrom)?.name || ""}</span>
            </p> */}
                        <p>
                            <span className="text-muted mr-15">Tôn giáo: </span>
                            <span>{values?.religion?.name}</span>
                        </p>
                    </Grid>

                    <Grid item sm={4} xs={12} className="flex-column gap-1">
                        <p>
                            <span className="text-muted mr-15">Giới tính:</span>
                            <span>{LocalConstants.ListGender.find(e => e.id === values?.gender)?.name || ""}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Quận huyện:</span>
                            <span>{values?.district?.name}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Hộ khẩu thường trú: </span>
                            <span>{values?.permanentResidence}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15"> Số CMTND/CCCD: </span>
                            <span>{values?.idNumber}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Quốc tịch: </span>
                            <span>{values?.nationality?.name}</span>
                        </p>
                        {/* <p>
              <span className="text-muted mr-15">Gia đình thuộc diện ưu tiên: </span>
              <span>{LocalConstants.ListFamilyPriority.find(e => e.id === values?.familyPriority)?.name || ""}</span>
            </p> */}
                        <p>
                            <span className="text-muted mr-15">Email: </span>
                            <span>{values?.email}</span>
                        </p>
                    </Grid>

                    <Grid item sm={4} xs={12} className="flex-column gap-1">
                        <p>
                            <span className="text-muted mr-15">Ngày sinh:</span>
                            <span>{formatDate("DD/MM/YYYY", values?.birthDate)}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Phường xã: </span>
                            <span>{values?.administrativeunit?.name}</span>
                        </p>

                        <p>
                            <span className="text-muted mr-15">Nơi sinh: </span>
                            <span>{values?.birthPlace}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Ngày cấp:</span>
                            <span>{formatDate("DD/MM/YYYY", values?.idNumberIssueDate)}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Dân tộc: </span>
                            <span>{values?.ethnics?.name}</span>
                        </p>
                    </Grid>
                </Grid>
            </TabAccordionSummary>

            <TabAccordionSummary title={'Thông tin hồ sơ nhân viên'}>
                <Grid container spacing={2}>
                    <Grid item sm={4} xs={12} className="flex-column gap-1">
                        <p>
                            <span className="text-muted mr-15">Trạng thái nhân viên:</span>
                            <span>{values?.status?.name}</span>
                        </p>
                        {/* <p>
              <span className="text-muted mr-15">Loại hợp đồng: </span>
              <span>{values?.labourAgreementType?.name}</span>
            </p> */}
                        {/* <p>
              <span className="text-muted mr-15">Ngày hợp đồng: </span>
              <span>{formatDate("DD/MM/YYYY", values?.contractDate)} </span>
            </p> */}
                        <p>
                            <span className="text-muted mr-15">Ngày tuyển dụng: </span>
                            <span>{formatDate("DD/MM/YYYY", values?.recruitmentDate)} </span>
                        </p>
                        {/* <p>
              <span className="text-muted mr-15">Chức danh chuyên môn:</span>
              <span>{values?.professionalTitles}</span>
            </p> */}
                    </Grid>

                    <Grid item sm={4} xs={12} className="flex-column gap-1">
                        {/* <p>
              <span className="text-muted mr-15">Ngày về cơ quan hiện tại:</span>
              <span>{formatDate("DD/MM/YYYY", values?.startDate)} </span>
            </p> */}
                        <p>
                            <span className="text-muted mr-15">Nghề nghiệp khi tuyển dụng:</span>
                            <span>{values?.jobTitle}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Hệ số phụ cấp: </span>
                            <span>{values?.allowanceCoefficient}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Bậc lương: </span>
                            <p>{values?.salaryLeve}</p>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Sổ BHXH: </span>
                            <p>{values?.socialInsuranceNumber}</p>
                        </p>
                    </Grid>

                    <Grid item sm={4} xs={12} className="flex-column gap-1">
                        <p>
                            <span className="text-muted mr-15">Mã nhân viên: </span>
                            <p>{values?.staffCode}</p>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Phòng ban: </span>
                            <span>{values?.department?.name}</span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Ngày hưởng phụ cấp:</span>
                            <span>{formatDate("DD/MM/YYYY", values?.dateOfReceivingAllowance)} </span>
                        </p>
                        <p>
                            <span className="text-muted mr-15">Hệ số lương: </span>
                            <p>{values?.salaryCoefficient}</p>
                        </p>
                    </Grid>
                </Grid>
            </TabAccordionSummary>

            <Grid container spacing={2}>
                <Grid item md={6} sm={12} xs={12}>
                    <TabAccordionSummary
                        title='Vị trí công tác'
                        open={openStaffPosition}
                        handleOnClick={() => setOpenStaffPosition(!openStaffPosition)}>
                        {openStaffPosition && <StaffPosition/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openStaffWorkingHistory}
                        handleOnClick={() => setOpenStaffWorkingHistory(!openStaffWorkingHistory)}
                        title="Quá trình công tác"
                    >
                        {openStaffWorkingHistory && <StaffWorkingHistory/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openStaffWorkingLocation}
                        handleOnClick={() => setOpenStaffWorkingLocation(!openStaffWorkingLocation)}
                        title="Địa điểm làm việc"
                    >
                        {openStaffWorkingLocation && <StaffWorkingLocation/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        title='Tài khoản ngân hàng'
                        open={openStaffPersonBankAccount}
                        handleOnClick={() => setOpenStaffPersonBankAccount(!openStaffPersonBankAccount)}>
                        {openStaffPersonBankAccount && <StaffPersonBankAccount/>}
                    </TabAccordionSummary>

                    <TabAccordionSummary
                        title='Công cụ dụng cụ'
                        open={openAssets}
                        handleOnClick={() => setOpenAssets(!openAssets)}>
                        {openAssets && <Assets/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openStaffEducationHistory}
                        handleOnClick={() => setOpenStaffEducationHistory(!openStaffEducationHistory)}
                        title='Quá trình đào tạo'
                    >
                        {openStaffEducationHistory && <StaffEducationHistory/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openPersonCertificate}
                        handleOnClick={() => setOpenPersonCertificate(!openPersonCertificate)}
                        title="Chứng chỉ/chứng nhận">
                        {openPersonCertificate && <PersonCertificate/>}
                    </TabAccordionSummary>

                    <TabAccordionSummary
                        open={openStaffAgreements}
                        handleOnClick={() => setOpenStaffAgreements(!openStaffAgreements)}
                        title="Hợp đồng"
                    >
                        {openStaffAgreements && <StaffAgreements/>}
                    </TabAccordionSummary>
                </Grid>

                <Grid item md={6} sm={12} xs={12}>
                    <TabAccordionSummary
                        open={openStaffInsuranceHistory}
                        handleOnClick={() => setOpenStaffInsuranceHistory(!openStaffInsuranceHistory)}
                        title="Quá trình đóng BHXH"
                    >
                        {openStaffInsuranceHistory && <StaffInsuranceHistory/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openStaffFamilyRelationShip}
                        handleOnClick={() => setOpenStaffFamilyRelationShip(!openStaffFamilyRelationShip)}
                        title="Quan hệ thân nhân"
                    >
                        {openStaffFamilyRelationShip && <Relatives/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openStaffIntroduceCost}
                        handleOnClick={() => setOpenStaffIntroduceCost(!openStaffIntroduceCost)}
                        title="Chi phí giới thiệu"
                    >
                        {openStaffIntroduceCost && <StaffIntroduceCost/>}
                    </TabAccordionSummary>

                    <TabAccordionSummary
                        open={openStaffSignature}
                        handleOnClick={() => setOpenStaffSignature(!openStaffSignature)}
                        title="Chữ ký"
                    >
                        {openStaffSignature && <StaffSignature/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openStaffSalaryTemplate}
                        handleOnClick={() => setOpenStaffSalaryTemplate(!openStaffSalaryTemplate)}
                        title="Mẫu bảng lương áp dụng"
                    >
                        {openStaffSalaryTemplate && <StaffSalaryTemplate/>}
                    </TabAccordionSummary>
                    <TabAccordionSummary
                        open={openStaffApplyProcess}
                        handleOnClick={() => setOpenStaffApplyProcess(!openStaffApplyProcess)}
                        title="Quá trình ứng tuyển"
                    >
                        {openStaffApplyProcess && <StaffApplyProcess/>}
                    </TabAccordionSummary>

                    {values?.gender === "F" && (
                        <TabAccordionSummary
                            title='Quá trình thai sản'
                            open={openStaffMaternityHistory}
                            handleOnClick={() => setOpenStaffMaternityHistory(!openStaffMaternityHistory)}>
                            {openStaffMaternityHistory && <StaffMaternityHistory/>}
                        </TabAccordionSummary>
                    )}
                </Grid>
            </Grid>
        </>
    );
};

function TabAccordionSummary({children, title, component, open = true, handleOnClick}) {

    const [expanded, setExpanded] = useState(open);
    return (
        <Accordion
            component="section"
            expanded={expanded}
            onChange={(_, value) => {
                setExpanded(value)
                if (handleOnClick) handleOnClick(value);
            }}
            className="card accordion-root my-10"
        >
            <AccordionSummary>
                <svg className="accordion-icon" fill="#000000" width="24px" height="24px" viewBox="0 0 24 24"
                     enableBackground="new 0 0 24 24">
                    <g strokeWidth="0"></g>
                    <g strokeLinecap="round" strokeLinejoin="round"></g>
                    <g>
                        <path
                            d="M9.9,17.2c-0.6,0-1-0.4-1-1c0-0.3,0.1-0.5,0.3-0.7l3.5-3.5L9.2,8.5c-0.4-0.4-0.4-1,0-1.4c0.4-0.4,1-0.4,1.4,0l4.2,4.2c0.4,0.4,0.4,1,0,1.4c0,0,0,0,0,0l-4.2,4.2C10.4,17.1,10.1,17.2,9.9,17.2z"></path>
                    </g>
                </svg>
                <p className="accordion-title">{title}</p>
            </AccordionSummary>

            <AccordionDetails>
                {children ? children : component ? component : ''}
            </AccordionDetails>
        </Accordion>
    )
}

export default memo(observer(GeneralInformation))