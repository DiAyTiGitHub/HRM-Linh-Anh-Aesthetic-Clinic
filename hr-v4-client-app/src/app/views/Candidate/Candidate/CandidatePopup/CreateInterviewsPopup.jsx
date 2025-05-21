import {
    Button ,
    ButtonGroup ,
    DialogActions ,
    DialogContent ,
    Grid ,
    Icon ,
    IconButton ,
    makeStyles ,
    Tooltip ,
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { StaffInterviewSchedule } from "app/common/Model/StaffInterviewSchedule";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { ErrorMessage , FieldArray , Form , Formik , useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";

export default observer(function CreateInterviewsPopup(props) {
    const {candidateStore , interviewScheduleStore} = useStore(); // Đổi từ `evaluationItemStore` sang `interviewScheduleStore`
    const {t} = useTranslation();

    const {handleClose , listOnDelete , openCreateInterviewsPopup , selectedRound} = candidateStore;
    const {createInterviewSchedules} = interviewScheduleStore;
    const [interviewSchedule , setInterviewSchedule] = useState(null);

    const validationSchema = Yup.object({
        interviewTime:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .nullable() ,
        candidates:Yup.array().min(1 , "Phải có ít nhất một ứng viên") ,
        staffInterviewSchedules:Yup.array()
            .min(1 , "Phải có ít nhất một người phỏng vấn")
            .of(
                Yup.object().shape({
                    interviewer:Yup.object().nullable().required("Người phỏng vấn là bắt buộc") ,
                })
            ) ,
    });

    const handleSaveForm = async (values) => {
        const candidateIds = values?.candidates?.map((item) => {
            return item?.id;
        });
        values.candidateIds = candidateIds;
        const dto = {
            ... values ,
            interviewTime:values?.interviewTime ? new Date(values?.interviewTime).getTime() : null ,
            candidateIds:candidateIds ,
        };
        await createInterviewSchedules(dto);
    };

    useEffect(() => {
        const interviewer = selectedRound?.participatingPeople?.map((item) => {
            return {
                interviewer:item ,
            };
        });
        console.log("interviewer" , interviewer);
        setInterviewSchedule({
            candidates:listOnDelete ,
            interviewTime:selectedRound?.takePlaceDate ,
            interviewLocation:selectedRound?.interviewLocation ,
            note:null ,
            status:LocalConstants?.InterviewScheduleStatus?.getListData()[0]?.value ,
            staffInterviewSchedules:interviewer || [] ,
            recruitmentRound:selectedRound ,
        });
    } , []);

    return (
        <GlobitsPopupV2
            size={"md"}
            open={openCreateInterviewsPopup}
            noDialogContent
            title={"Tạo lịch phỏng vấn"}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={interviewSchedule}
                onSubmit={handleSaveForm}>
                {({isSubmitting , values}) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden'>
                                <Grid container spacing={2}>
                                    <Grid item xs={6}>
                                        <GlobitsSelectInput
                                            options={LocalConstants?.InterviewScheduleStatus?.getListData()}
                                            label={"Trạng thái"}
                                            name='status'
                                            // disabled={
                                            //     !(!!values?.interviewTime && new Date() > new Date(values.interviewTime))
                                            // }
                                        />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <GlobitsDateTimePicker
                                            required
                                            name='interviewTime'
                                            label={"Thời gian phỏng vấn"}
                                            validate={true}
                                            format='dd/MM/yyyy HH:mm'
                                            isDateTimePicker={true}
                                        />
                                    </Grid>

                                    <Grid item xs={6}>
                                        <GlobitsTextField label={"Địa điểm phỏng vấn"} name='interviewLocation'/>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField label='Ghi chú' name='note' multiline rows={3}/>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <ListChosenCandidateApproved/>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <StaffInterviewSchedulesList/>
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon/>}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={handleClose}>
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
                )}
            </Formik>
        </GlobitsPopupV2>
    );
});

const useStyles = makeStyles((readOnly) => ({
    root:{
        background:"#E4f5fc" ,
        padding:"10px 15px" ,
        borderRadius:"5px" ,
    } ,
    groupContainer:{
        width:"100%" ,
        "& .MuiOutlinedInput-root":{
            borderRadius:"0!important" ,
        } ,
    } ,
    tableContainer:{
        marginTop:"2px" ,
        overflowX:"auto" ,
        overflowY:"hidden" ,
        "& table":{
            border:"1px solid #ccc" ,
            borderCollapse:"collapse" ,
            "& td":{
                border:"1px solid #ccc" ,
            } ,
        } ,
    } ,
    tableHeader:(readOnly) => ({
        width:"100%" ,
        borderBottom:"1px solid #ccc" ,
        marginBottom:"8px" ,
        "& th":{
            width:readOnly ? "calc(100vw / 6)" : "calc(100vw / 7)" ,
            border:"1px solid #ccc" ,
            padding:"8px 0 8px 4px" ,
        } ,
    }) ,
    table:{
        width:"100%" ,
        borderCollapse:"collapse" ,
        "& th, & td":{
            padding:"8px" ,
            textAlign:"center" ,
            borderBottom:"1px solid #ddd" ,
        } ,
    } ,
}));

const StaffInterviewSchedulesList = (props) => {
    const {readOnly = false} = props;

    const {t} = useTranslation();
    const classes = useStyles(readOnly);
    const {values , setFieldValue , errors , touched} = useFormikContext();

    return (
        <FieldArray name='staffInterviewSchedules'>
            {({push , remove}) => (
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <ButtonGroup color='container' aria-label='outlined primary button group'>
                            <Button
                                startIcon={<AddIcon/>}
                                type='button'
                                onClick={() => push(new StaffInterviewSchedule())}
                                disabled={readOnly}>
                                {t("Thêm người phụ trách")}
                            </Button>
                        </ButtonGroup>
                    </Grid>
                    {touched?.staffInterviewSchedules && errors?.staffInterviewSchedules && (
                        <Grid item xs={12}>
                            <ErrorMessage name='staffInterviewSchedules' className='text-danger' component='span'/>
                        </Grid>
                    )}
                    <Grid item xs={12}>
                        <section className={classes.tableContainer}>
                            <table className={classes.table}>
                                <thead>
                                <tr className={classes.tableHeader}>
                                    {!readOnly && <th>{t("general.action")}</th>}
                                    <th>
                                        Nhân viên phụ trách <span style={{color:"red"}}> * </span>
                                    </th>
                                    <th>Vai trò</th>
                                    <th>Ghi chú</th>
                                </tr>
                                </thead>
                                <tbody>
                                {values?.staffInterviewSchedules?.length > 0 ? (
                                    values.staffInterviewSchedules.map((_ , index) => (
                                        <tr key={index}>
                                            {!readOnly && (
                                                <td style={{textAlign:"center"}}>
                                                    <Tooltip title='Xóa giai đoạn' placement='top' arrow>
                                                        <IconButton size='small' onClick={() => remove(index)}>
                                                            <Icon fontSize='small' color='secondary'>
                                                                delete
                                                            </Icon>
                                                        </IconButton>
                                                    </Tooltip>
                                                </td>
                                            )}
                                            <td>
                                                <GlobitsPagingAutocompleteV2
                                                    name={`staffInterviewSchedules[${index}].interviewer`}
                                                    required={true}
                                                    api={pagingStaff}
                                                    getOptionLabel={(option) =>
                                                        option?.displayName && option?.staffCode
                                                            ? `${option.displayName} - ${option.staffCode}`
                                                            : option?.displayName || option?.staffCode || ""
                                                    }
                                                />
                                            </td>
                                            <td>
                                                <GlobitsTextField
                                                    name={`staffInterviewSchedules[${index}].interviewRole`}
                                                />
                                            </td>
                                            <td>
                                                <GlobitsTextField name={`staffInterviewSchedules[${index}].note`}/>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={7} align='center' className='py-8'>
                                            Chưa có dữ liệu
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </section>
                    </Grid>
                </Grid>
            )}
        </FieldArray>
    );
};

const ListChosenCandidateApproved = (props) => {
    const {readOnly = false} = props;

    const {t} = useTranslation();
    const classes = useStyles(readOnly);
    const {values , setFieldValue , errors , touched} = useFormikContext();

    return (
        <FieldArray name='candidates'>
            {({push , remove}) => (
                <Grid container spacing={2}>
                    {touched?.candidates && errors?.candidates && (
                        <Grid item xs={12}>
                            <ErrorMessage name='candidates' className='text-danger' component='span'/>
                        </Grid>
                    )}
                    <Grid item xs={12}>
                        <section className={classes.tableContainer}>
                            <table className={classes.table}>
                                <thead>
                                <tr className={classes.tableHeader}>
                                    <th>Thao tác</th>
                                    <th>Tên Ứng viên</th>
                                    <th>Mã ứng viên</th>
                                </tr>
                                </thead>
                                <tbody>
                                {values?.candidates?.length > 0 ? (
                                    values.candidates.map((_ , index) => (
                                        <tr key={index}>
                                            {!readOnly && (
                                                <td style={{textAlign:"center"}}>
                                                    <Tooltip title='Xóa giai đoạn' placement='top' arrow>
                                                        <IconButton size='small' onClick={() => remove(index)}>
                                                            <Icon fontSize='small' color='secondary'>
                                                                delete
                                                            </Icon>
                                                        </IconButton>
                                                    </Tooltip>
                                                </td>
                                            )}
                                            <td>
                                                <GlobitsTextField
                                                    readOnly
                                                    name={`candidates[${index}].displayName`}
                                                />
                                            </td>
                                            <td>
                                                <GlobitsTextField
                                                    readOnly
                                                    name={`candidates[${index}].candidateCode`}
                                                />
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={7} align='center' className='py-8'>
                                            Chưa có dữ liệu
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </section>
                    </Grid>
                </Grid>
            )}
        </FieldArray>
    );
};
