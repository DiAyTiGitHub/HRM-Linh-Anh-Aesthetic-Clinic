import { Form , Formik } from "formik";
import { useTranslation } from "react-i18next";
import React , { useEffect , useState } from "react";
import {
    Box ,
    Button ,
    Grid ,
    Table ,
    TableBody ,
    TableCell , TableRow ,
    Typography
} from "@material-ui/core";

import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import ConstantList from "../../../../appConfig";

import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import { useStore } from "../../../../stores";
import { useParams } from "react-router-dom";
import { PositionRelationshipType } from "../../../../LocalConstants";
import { observer } from "mobx-react";
import { toast } from "react-toastify";
import ContentEvaluation from "./ContentEvaluation";
import ConclusionForm from "./ConclusionForm";
import EvaluationFormStaffInformation from "./EvaluationFormStaffInformation";

function EvaluationTicketSaveForm() {
    const {t} = useTranslation();
    const [contract , setContract] = useState(null);
    const {id,templateId} = useParams()
    const {
        form ,
        getEvaluationFormsById ,
        saveEvaluationForms ,
        evaluatePerson ,
        handleEvaluatePerson,
        getTemplate,
        template
    } = useStore().evaluationTicketStore
    const {
        selectedStaff ,
        getStaff
    } = useStore().staffStore
    const {
        getLastLabourAgreement
    } = useStore().staffLabourAgreementStore
    const {
        getAllContractType ,
        listContractType
    } = useStore().contractTypeStore
    // Example of initial values - you should replace this with the actual values.
    const fetchPreviousContract = async () => {
        const data = await getLastLabourAgreement(id);
        if (data) {
            setContract(data);
        }
    };
    const fetchData = async () => {
        if (window.location.href.includes('edit')) {
            await getEvaluationFormsById(id);
        } else {
            getStaff(id);
            if(templateId)
                getTemplate(templateId);
        }
    };
    const [row , setRows] = useState([])
    useEffect(() => {
        getAllContractType()
        fetchPreviousContract();
        fetchData();
    } , [id]);

    useEffect(() => {
        handleEvaluatePerson()
        if (form?.items) {
            const newRows = form.items.map(value => ({
                item:{
                    id:value.itemId ,
                    name:value.itemName
                } ,
                selfEvaluate:value?.selfEvaluate ,
                managementEvaluate:value?.managementEvaluate ,

            }));
            setRows(prevState => [... prevState , ... newRows]);
        }
    } , [form]);

    useEffect(() => {
        setRows(template?.items||[])
    },[template])

    const getDirectManager = selectedStaff?.currentPosition?.relationships
        ?.filter(relationship => relationship.relationshipType === PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.value) // Lọc phần tử có type === 3
        ?.map(relationship => ({
            directManagerId:relationship?.supervisor?.staff?.id ,
            directManagerName:relationship?.supervisor?.staff?.displayName
        }));
    const initialValues = window.location.href.includes('edit') ? {
                // thông tin nhân viên
                id:form?.id ,
                staffId:form?.staffId ,
                staffName:form?.staffName ,
                staffCode:form?.staffCode ,
                staffPositionId:form?.staffPositionId ,
                position:form?.position ,
                staffDepartmentId:form?.staffDepartmentId ,
                department:form?.department ,
                staffDivisionId:form?.staffDivisionId ,
                division:form?.division ,
                staffTeamId:form?.staffTeamId ,
                team:form?.team ,
                directManagerId:form?.directManagerId ,
                directManagerName:form?.directManagerName ,
                hireDate:form?.hireDate ,
                previousContractDuration:form?.previousContractDuration ,
                contractTypeId:form?.contractTypeId ,
                contractTypeName:form?.contractTypeName ,
                //A. NỘI DUNG ĐÁNH GIÁ:
                advantage:form?.advantage ,//Ưu điểm
                disadvantage:form?.disadvantage ,//Nhược điểm
                companyPolicyCompliance:form?.companyPolicyCompliance ,//Chấp hành nội quy, quy định của công ty
                coworkerRelationship:form?.coworkerRelationship ,//Mối quan hệ với đồng nghiệp
                senseOfResponsibility:form?.senseOfResponsibility ,//Tinh thần trách nhiệm
                //B. KẾT LUẬN:
                contractRecommendation:form?.contractRecommendation , //Đạt yêu cầu, đề xuất ký HĐLĐ = true / false = Không đạt yêu cầu
                //Đạt yêu cầu, đề xuất ký HĐLĐ
                contractRecommendationDateFrom:form?.contractRecommendationDateFrom , //Đạt yêu cầu, đề xuất ký HĐLĐ từ ngày
                contractRecommendationDateTo:form?.contractRecommendationDateTo , //Đạt yêu cầu, đề xuất ký HĐLĐ đến ngày
                ... (form?.positionTitleId != null && form?.positionTitleName != null ? {
                    positionTitle:{
                        id:form?.positionTitleId ,
                        name:form?.positionTitleName
                    } ,
                } : {positionTitle:null}) ,
                positionTitleId:form?.positionTitleId ,//Chức danh
                positionTitleName:form?.positionTitleName ,
                ... (form?.rankTitleId != null && form?.rankTitleName ? {
                        rankTitle:{
                            id:form?.rankTitleId ,
                            name:form?.rankTitleName
                        } ,
                    } : {}
                ) ,
                rankTitleId:form?.rankTitleId ,//Cấp bậc
                rankTitleName:
                form?.rankTitleName ,
                baseSalary:
                form?.baseSalary ,//Lương cứng
                allowanceAmount:
                form?.allowanceAmount ,//Phụ cấp
                effectiveFromDate:
                form?.effectiveFromDate , //Thời gian áp dụng: Từ ngày
                //Không đạt yêu cầu
                cooperationStatus:
                form?.cooperationStatus , //Ngừng hợp tác kể từ ngày = true / Bố trí sang vị trí khác = false
                //Ngừng hợp tác kể từ ngày
                collaborationEndDate:
                form?.collaborationEndDate , //Ngừng hợp tác kể từ ngày
                //Bố trí sang vị trí khác
                newPosition:{
                    id:form?.newPositionId ,
                    name:form?.newPositionName ,
                }
                ,//Bố trí sang vị trí khác
                newPositionId:form?.newPositionId ,
                newPositionName:form?.newPositionName ,
                newPositionTransferDate:form?.newPositionTransferDate
            } :
            {
                staffId:id ,
                staffName:
                selectedStaff?.displayName ,
                staffCode:
                selectedStaff?.staffCode ,
                staffPositionId:
                selectedStaff?.positionTitle?.id ,
                position:
                selectedStaff?.positionTitle?.name ,
                staffDepartment:{
                    id:selectedStaff?.staffDepartment?.id ,
                    name:selectedStaff?.staffDepartment?.name
                } ,
                staffDepartmentId:selectedStaff?.staffDepartment?.id ,
                department:selectedStaff?.staffDepartment?.name ,
                staffDivisionId:selectedStaff?.staffDivision?.id ,
                division:selectedStaff?.staffDivision?.name ,
                staffTeamId:selectedStaff?.staffTeam?.id ,
                team:selectedStaff?.staffTeam?.name ,
                directManager:{
                    id:getDirectManager?.[0]?.directManagerId ,
                    name:getDirectManager?.[0]?.directManagerName
                } ,
                ... getDirectManager?.[0] ,
                hireDate:
                contract?.startDate ,
                previousContractDuration:
                contract?.endDate ,
                contractType:{
                    id:contract?.contractType?.id ,
                    name:
                    contract?.contractType?.name
                } ,
                contractTypeName:contract?.contractType?.name ,
                contractTypeId:
                contract?.contractType?.id ,
                positionTitle:null
            }
    ;
    // Example submit handler
    const handleSaveForm = async (values) => {
        const isValid = row.every(row => row.item?.id);
        if (!isValid) {
            toast.warning("Vui lòng chọn đầu mục công việc và đánh giá đầy đủ.");
            return;
        }

        const items = row?.map(r => {
            return {
                itemId:r?.item?.id ,
                managementEvaluate:r?.managementEvaluate ,
                selfEvaluate:r?.selfEvaluate
            }
        })
        await saveEvaluationForms({... values , items})
    };
    return (
        <>
            <div style={{marginBottom:'100px'}}>
                <TableContainer
                    component={Paper}
                    sx={{
                        margin:"auto" ,
                        border:"2px solid black" ,
                        boxShadow:3 ,
                    }}>
                    <Table>
                        <TableBody>
                            <TableRow>
                                {/* Cột Logo */}
                                <TableCell
                                    sx={{
                                        width:"25%" ,
                                        textAlign:"center" ,
                                        border:"2px solid black" ,
                                        backgroundColor:"#f9f9f9" ,
                                    }}
                                >
                                    <Box>
                                        <img
                                            src={ConstantList.ROOT_PATH + "assets/images/logo.png"}
                                            style={{
                                                maxWidth:"90px" ,
                                                display:"block" ,
                                                margin:"auto" ,
                                            }}
                                        />
                                    </Box>
                                </TableCell>

                                {/* Cột Tiêu đề */}
                                <TableCell
                                    sx={{
                                        textAlign:"center" ,
                                        border:"2px solid black" ,
                                        fontWeight:"bold" ,
                                        fontSize:"1.2rem" ,
                                        padding:"20px" ,
                                    }}
                                >
                                    <Typography variant="h5" fontWeight="bold">
                                        ĐÁNH GIÁ NHÂN SỰ
                                    </Typography>
                                    <Typography variant="h5" fontWeight="bold">
                                        TÁI KÍ HỢP ĐỒNG LAO ĐỘNG
                                    </Typography>
                                </TableCell>

                                {/* Cột Thông tin */}
                                <TableCell
                                    sx={{
                                        width:"20%" ,
                                        border:"2px solid black" ,
                                        fontSize:"0.9rem" ,
                                        padding:"5px" ,
                                    }}
                                >
                                    <Table size="small">
                                        <TableBody>
                                            <TableRow>
                                                <TableCell sx={{fontWeight:"bold" , padding:"6px"}}>Mã số</TableCell>
                                                <TableCell sx={{padding:"6px"}}></TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell sx={{fontWeight:"bold" , padding:"6px"}}>Lần BH</TableCell>
                                                <TableCell sx={{padding:"6px"}}></TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell sx={{fontWeight:"bold" , padding:"6px"}}>Ngày BH</TableCell>
                                                <TableCell sx={{padding:"6px"}}></TableCell>
                                            </TableRow>
                                        </TableBody>
                                    </Table>
                                </TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>
                <Formik
                    enableReinitialize
                    initialValues={initialValues} // Set initialValues here
                    onSubmit={handleSaveForm} // Set onSubmit handler

                >
                    {({isSubmitting , values , setFieldValue , initialValues}) => (
                        <Form autoComplete="off" style={{backgroundColor:"white"}}>
                            <Grid container style={{
                                padding:10 ,
                                backgroundColor:"white"
                            }}>
                                <EvaluationFormStaffInformation listContractType={listContractType}/>
                                <Grid container style={{display:'flex' , gap:12}}>
                                    <ContentEvaluation row={row} setRows={setRows} evaluatePerson={evaluatePerson}/>
                                    <ConclusionForm evaluatePerson={evaluatePerson}/>
                                </Grid>
                            </Grid>
                            <div className='dialog-footer bg-white flex items-center justify-center gap-4'>
                                <Button
                                    className="ml-8 d-inline-flex py-2 px-8 btnHrStyle btn-primary"
                                    type="submit">
                                    <SaveOutlinedIcon className="mr-6"/>
                                    Lưu thông tin
                                </Button>
                            </div>
                        </Form>
                    )}
                </Formik>
            </div>
        </>
    );
};

export default observer(EvaluationTicketSaveForm);
