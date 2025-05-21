import React , { memo } from "react";
import { Formik , Form } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { Grid , DialogActions , Button , DialogContent , makeStyles } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { useParams } from "react-router";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import FormikFocusError from "app/common/FormikFocusError";
import * as Yup from "yup";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import { formatDate } from "app/LocalFunction";
import { Radio } from "@material-ui/core";
import StaffWorkScheduleViewStatisticPopup from "../StaffWorkScheduleV2/StaffWorkScheduleViewStatisticPopup";

const useStyles = makeStyles({
    root:{
        "& .MuiDialogContent-root":{
            overflow:"auto !important" ,
        } ,
    } ,
    marginAuto:{
        display:"flex" ,
        "& label":{
            margin:"auto" ,
            marginRight:"10px" ,
            fontWeight:"500" ,
            fontSize:"16px" ,
        } ,
    } ,
    table:{
        minWidth:650 ,
        border:"3px solid #2a80c8 !important" ,
        borderCollapse:"collapse" ,

        "& .MuiTableCell-root":{
            border:"none" ,
        } ,

        "& .MuiTableRow-head":{
            backgroundColor:"#2a80c8" ,
            border:"1px solid #2a80c8" ,
        } ,

        "& .MuiTableCell-head":{
            border:"1px solid #2a80c8" ,
            color:"#fff" ,
        } ,

        "& .MuiTableCell-body":{
            border:"1px solid #2a80c8" ,
        } ,

        "& .MuiFormGroup-root":{
            display:"flex" ,
            justifyContent:"center" ,
            alignItems:"center" ,
        } ,
    } ,
    tableBody:{
        "& .MuiCheckbox-root":{
            margin:"auto" ,
        } ,
        "& .MuiTextField-root":{
            padding:"5px" ,
        } ,
    } ,
    headerDate:{
        fontSize:"22px" ,
        fontWeight:"700" ,
    } ,
    displayFlex:{
        display:"flex" ,
        justifyContent:"center" ,
        alignItems:"center" ,
    } ,
});

function TimeSheetDetailEditForm(props) {
    const {readOnly} = props;
    const {t} = useTranslation();
    const {id} = useParams();

    const {
        timeSheetDetailStore ,
    } = useStore();

    const classes = useStyles();

    const {
        openCreateEditPopup ,
        handleClose ,
        saveTimeSheetDetail ,
        selectedTimeSheetDetail ,
        openViewPopup
    } = timeSheetDetailStore;

    const validationSchema = Yup.object().shape({
        workingDate:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required("Ngày không được để trống")
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable() ,
        employee:Yup.object().required("Nhân viên không được để trống").nullable() ,
        staffWorkSchedule:Yup.object().required("Ca làm việc không được để trống").nullable() ,
        startTime:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required("Thời gian bắt đầu không được để trống")
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable() ,

    });

    return (
        <GlobitsPopupV2
            size='sm'
            onClosePopup={handleClose}
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={
                (openViewPopup && t("Xem chi tiết") + " Thông tin lần chấm công") ||
                ((selectedTimeSheetDetail?.id
                    ? t("general.button.edit")
                    : t("general.button.add")) + ' ' + t("Thông tin lần chấm công"))
            }
        >
            <Formik
                enableReinitialize
                initialValues={selectedTimeSheetDetail}
                validationSchema={validationSchema}
                onSubmit={(values) => saveTimeSheetDetail(values)}
            >
                {({values , setFieldValue , submitForm}) => {

                    return (
                        <Form autoComplete='off'>
                            <div className={`dialog-body`}>
                                <DialogContent className='p-12'>
                                    <FormikFocusError/>

                                    <Grid container spacing={2}>

                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thông tin ca làm việc
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsPagingAutocomplete
                                                label={"Nhân viên"}
                                                name='employee'
                                                getOptionLabel={(option) =>
                                                    option?.displayName && option?.staffCode
                                                        ? `${option.displayName} - ${option.staffCode}`
                                                        : option?.displayName || option?.staffCode || ''
                                                }
                                                api={pagingStaff}
                                                displayData='displayName' required
                                                readOnly={readOnly}
                                                disabled/>
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                name='workingDate'
                                                label='Ngày'
                                                disabled
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>


                                        <Grid
                                            item
                                            xs={12}
                                            // sm={6}
                                        >
                                            <StaffWorkScheduleViewStatisticPopup/>

                                        </Grid>

                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thời gian chấm công
                                            </p>
                                        </Grid>

                                        {/* <Grid item xs={12}>
                    <GlobitsTextField label={"Địa chỉ IP checkin"} name='addressIPCheckIn' />
                  </Grid> */}
                                        {/* <Grid item xs={12}>
                    <GlobitsTextField label={"Địa chỉ IP checkout"} name='addressIPCheckOut' />
                  </Grid> */}

                                        {!values?.staffWorkSchedule?.allowOneEntryOnly && values?.staffWorkSchedule?.shiftWork?.timePeriods?.length > 0 && (
                                            <Grid item xs={12}>

                                                <strong>
                                                    Giai đoạn làm việc trong ca
                                                </strong>

                                                <TableContainer component={Paper}>
                                                    <Table
                                                        className={`${classes.table}`}
                                                        aria-label="simple table"
                                                    >
                                                        <TableHead>
                                                            <TableRow>
                                                                <TableCell
                                                                    align="center"
                                                                    className="py-4"
                                                                    style={{width:"10%"}}
                                                                >
                                                                    Chọn
                                                                </TableCell>

                                                                <TableCell
                                                                    align="center"
                                                                    className="py-4"
                                                                    style={{width:"15%"}}
                                                                >
                                                                    {t("timeKeeping.startTime")}
                                                                </TableCell>

                                                                <TableCell
                                                                    align="center"
                                                                    className="py-4"
                                                                    style={{width:"15%"}}
                                                                >
                                                                    {t("timeKeeping.endTime")}
                                                                </TableCell>

                                                                <TableCell
                                                                    align="center"
                                                                    className="py-4"
                                                                    style={{width:"20%"}}
                                                                >
                                                                    Tỷ lệ
                                                                </TableCell>
                                                            </TableRow>
                                                        </TableHead>

                                                        <TableBody>
                                                            {values?.staffWorkSchedule?.shiftWork?.timePeriods.length > 0
                                                                ? values?.staffWorkSchedule?.shiftWork?.timePeriods?.map(
                                                                    function (timePeriod , index) {

                                                                        return (
                                                                            <TableRow key={index}
                                                                                      className={classes.tableBody}>
                                                                                <TableCell align="center">
                                                                                    <Radio
                                                                                        name="radSelected"
                                                                                        value={values?.shiftWorkTimePeriod?.id}
                                                                                        checked={values?.shiftWorkTimePeriod?.id === timePeriod?.id}
                                                                                        //onClick={(event) => handleChooseTimePeriod(timePeriod)}
                                                                                        disabled
                                                                                        readOnly={readOnly}
                                                                                    />
                                                                                </TableCell>

                                                                                <TableCell align="center">
                                                                                    {formatDate("HH:mm" , timePeriod?.startTime)}fdsfdas
                                                                                </TableCell>

                                                                                <TableCell align="center">
                                                                                    {formatDate("HH:mm" , timePeriod?.endTime)}
                                                                                </TableCell>

                                                                                <TableCell align="center">
                                                                                    {`${(timePeriod?.workRatio || 0) * 100}% ngày công`}
                                                                                </TableCell>
                                                                            </TableRow>
                                                                        );
                                                                    }
                                                                )
                                                                : "Chưa có dữ liệu !"}
                                                        </TableBody>
                                                    </Table>
                                                </TableContainer>
                                            </Grid>
                                        )}


                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={"Thời gian chấm công vào"}
                                                name='startTime'
                                                isDateTimePicker
                                                readOnly={readOnly}
                                                required/>
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={"Thời gian chấm công ra"}
                                                name='endTime'
                                                readOnly={readOnly}
                                                isDateTimePicker
                                            />
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer dialog-footer-v2 py-8 px-12'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex'
                                            color='secondary'
                                            onClick={handleClose}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>
                                        {!readOnly && (
                                            <Button
                                                startIcon={<SaveIcon/>}
                                                className='mr-0 btn btn-primary d-inline-flex'
                                                variant='contained'
                                                color='primary'
                                                type='submit'
                                            >
                                                {t("general.button.save")}
                                            </Button>
                                        )}
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(TimeSheetDetailEditForm));
