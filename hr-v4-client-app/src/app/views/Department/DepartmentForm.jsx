import { Button , DialogActions , DialogContent , Grid , makeStyles } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { Form , Formik } from "formik";
import { observer } from "mobx-react";
import React , { memo , useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import { useStore } from "../../stores";
import { pagingDepartmentType } from "../DepartmentType/DepartmentTypeService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPosition } from "../Position/PositionService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import { pagingAllDepartments } from "./DepartmentService";
import SelectDepartmentPopup from "./SelectParent/SelectDepartmentPopup";
import SaveIcon from "@material-ui/icons/Save";

const useStyles = makeStyles((theme) => ({
    gridContainerForm:{
        // maxHeight: "68vh",
        // overflowY: "auto",
        // marginBottom: 10,

        "& .MuiButton-root":{
            borderRadius:"0 !important" ,
        } ,
    } ,
    textField:{
        width:"100%" ,
        margin:"10px 0px !important" ,
    } ,
}));

function DepartmentForm(props) {
    const {readOnly} = props;
    const classes = useStyles();
    const {departmentStore} = useStore();
    const {t} = useTranslation();
    const {handleClose , saveDepartment , selectedDepartment , updatePageData , openViewPopup} = departmentStore;

    const [department , setDepartment] = useState({
        id:"" ,
        code:"" ,
        name:"" ,
        value:"" ,
        shortName:null ,
        positionTitles:null ,
        positionTitleManager:null ,
        hrdepartmentType:null ,
        establishDecisionDate:null ,
        establishDecisionCode:null ,
        departmentDisplayCode:null ,
        foundedDate:null ,
        function:null ,
        foundedNumber:null ,
        description:null ,
        industryBlock:null ,
        timezone:null ,
    });

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.required")).nullable() ,
        name:Yup.string().required(t("validation.required")).nullable() ,
        //shortName: Yup.string().required(t("validation.required")).nullable(),
    });

    useEffect(() => {
        if (selectedDepartment) setDepartment(selectedDepartment);
    } , [selectedDepartment]);

    async function handleSaveForm(values) {
        await saveDepartment(values);
        await updatePageData();

        if (props && typeof props?.handleAfterSubmit === "function") {
            props?.handleAfterSubmit();
        }
    }

    const {
        isAdmin ,
    } = useStore().hrRoleUtilsStore;
    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='lg'
            open={props.open || openViewPopup}
            noDialogContent
            title={
                openViewPopup ? (t("Xem chi tiết ") + t("navigation.category.staff.departments")) : ((selectedDepartment?.id ? t("general.button.edit") : t("general.button.add")) +
                    " " +
                    t("navigation.category.staff.departments"))
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={JSON.parse(JSON.stringify(department))}
                onSubmit={handleSaveForm}>
                {({isSubmitting , values , setValues , setFieldValue}) => {
                    return (
                        <Form autoComplete='off'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} className='pb-0'>
                                        <p className='m-0 p-0 borderThrough2'>Thông tin phòng ban</p>
                                    </Grid>
                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsTextField
                                            label={t("department.code")}
                                            required name='code'
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsTextField
                                            label={"Tên phòng ban"}
                                            required name='name'
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsTextField
                                            label={t("department.shortName")}
                                            // required
                                            name='shortName'
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            name='organization'
                                            label={t("department.organization")}
                                            api={pagingAllOrg}
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <Grid container spacing={1}>
                                            <Grid item xs={9}>
                                                <GlobitsPagingAutocompleteV2
                                                    api={pagingAllDepartments}
                                                    label={t("department.parent")}
                                                    name='parent'
                                                    placeholder={"Không có phòng ban cha"}
                                                    readOnly={readOnly}
                                                />
                                            </Grid>

                                            <Grid item xs={3} className='flex align-end'>
                                                <SelectDepartmentPopup readOnly={readOnly}/>
                                            </Grid>
                                        </Grid>
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            name='hrDepartmentType'
                                            label={t("department.hrdepartmentType")}
                                            api={pagingDepartmentType}
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <GlobitsPagingAutocomplete
                                            name='positionManager'
                                            label={t("Vị trí quản lý phòng ban")}
                                            api={pagingPosition}
                                            getOptionLabel={(option) =>
                                                option?.name && option?.code
                                                    ? `${option.name} - ${option.code} - ${option?.staff ? option?.staff?.displayName : "Vaccant"
                                                    }`
                                                    : option?.name || option?.code || ""
                                            }
                                            searchObject={{
                                                departmentId:values?.id ,
                                            }}
                                            readOnly={readOnly}
                                        />
                                    </Grid>
                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsDateTimePicker
                                            label={t("department.foundedDate")}
                                            name='foundedDate'
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("department.description")}
                                            name='description'
                                            multiline
                                            rows={3}
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("department.positionTitles")}
                                            name='positionTitles'
                                            multiple
                                            searchObject={{
                                                pageIndex:1 ,
                                                pageSize:10 ,
                                            }}
                                            api={pagingPositionTitle}
                                            readOnly={readOnly}/>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("department.subDepartments")}
                                            name='children'
                                            multiple
                                            searchObject={{
                                                pageIndex:1 ,
                                                pageSize:10 ,
                                            }}
                                            api={pagingAllDepartments}
                                            getOptionDisabled={function (option) {
                                                return values?.id == option?.id || values?.parent?.id == option?.id;
                                            }}
                                            readOnly={readOnly}/>
                                    </Grid>
                                    <Grid item xs={12} className='pb-0'>
                                        <p className='m-0 p-0 borderThrough2'>Các ca làm việc của phòng ban</p>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("department.shiftWorks")}
                                            name='shiftWorks'
                                            multiple
                                            api={pagingShiftWork}
                                            getOptionDisabled={(option) => {
                                                return option?.name && option?.code
                                                    ? `${option.name} - ${option.code}`
                                                    : option?.name || option?.code;
                                            }}
                                            readOnly={readOnly}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            <DialogActions className='dialog-footer px-12'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        variant='contained'
                                        className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                        color='secondary'
                                        disabled={isSubmitting}
                                        onClick={handleClose}>
                                        {t("general.button.close")}
                                    </Button>

                                    {((isAdmin) && !readOnly) && (
                                        <Button
                                            startIcon={<SaveIcon/>}
                                            className="mr-0 btn btn-primary d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.save")}
                                        </Button>
                                    )}
                                </div>
                            </DialogActions>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(DepartmentForm));
