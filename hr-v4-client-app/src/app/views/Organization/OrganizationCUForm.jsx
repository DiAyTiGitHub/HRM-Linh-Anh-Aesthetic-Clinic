import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form , Formik } from "formik";
import { observer } from "mobx-react";
import React , { memo , useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import { pagingAdministratives } from "../AdministrativeUnit/AdministrativeUnitService";
import SelectOrganizationPopup from "./SelectParent/SelectOrganizationPopup";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import {CodePrefixes, OrganizationType} from "../../LocalConstants";

function OrganizationCUForm(props) {
    const {organizationStore} = useStore();
    const {t} = useTranslation();
    const {readOnly} = props;
    const {
        handleClose ,
        saveOrganization ,
        pagingOrganization ,
        selectedOrganization ,
        openCreateEditPopup ,
        uploadImage ,
        openViewPopup,
        autoGenCode
    } = organizationStore;

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")).nullable() ,
        name:Yup.string().required(t("validation.name")).nullable() ,
    });

    const [organization , setOrganization] = useState(null);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.DON_VI);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...selectedOrganization, ...{code:code}};
            setOrganization(updated);
        }
    };
    useEffect(() => {
        if(!selectedOrganization?.id){
            autoGenCodeFunc();
        }
    }, []);
    useEffect(() => {
        if (selectedOrganization) setOrganization(selectedOrganization);
    } , [selectedOrganization]);

    async function handleSaveForm(values) {
        await saveOrganization(values);
    }

    const {isAdmin} = useStore().hrRoleUtilsStore;

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='md'
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? (t("general.button.view") + " " + t("thông tin đơn vị")) : ((selectedOrganization?.id ? t("general.button.edit") : t("general.button.add")) + " " + "thông tin đơn vị")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={JSON.parse(JSON.stringify(organization))}
                onSubmit={handleSaveForm}
            >
                {({isSubmitting , values , setFieldValue , initialValues}) => {
                    async function handleUploadImage(value) {
                        try {
                            const {data} = await uploadImage(value);
                            setFieldValue("imagePath" , data?.name);
                        } catch (err) {
                            console.error(err);
                        }
                    }

                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError/>

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thông tin đơn vị
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label='Mã đơn vị'
                                                name='code'
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label='Tên đơn vị'
                                                name='name'
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label='Website'
                                                name='website'
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label='Mã số thuế'
                                                name='taxCode'
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsDateTimePicker
                                                label={t("department.foundedDate")}
                                                name="foundedDate"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                name='representative'
                                                label="Người đại diện đơn vị"
                                                api={pagingStaff}
                                                getOptionLabel={(option) =>
                                                    option?.displayName && option?.staffCode
                                                        ? `${option.displayName} - ${option.staffCode}`
                                                        : option?.displayName || option?.staffCode || ''
                                                }
                                                displayData='displayName'
                                                readOnly={readOnly}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                name='organizationType'
                                                label="Loại đơn vị"
                                                keyValue={"value"}
                                                options={OrganizationType.getListData()}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <Grid container spacing={1}>
                                                <Grid item xs={9}>
                                                    <GlobitsTextField
                                                        label={t("Đơn vị cha")}
                                                        name='parent.name'
                                                        placeholder={"Không có đơn vị cha"}
                                                        disabled
                                                        value={values?.parent ? values?.parent?.name : "Không có đơn vị cha"}
                                                        readOnly={readOnly}
                                                    />
                                                </Grid>

                                                <Grid item xs={3} className="flex align-end">
                                                    <SelectOrganizationPopup readOnly={readOnly}/>
                                                </Grid>
                                            </Grid>
                                        </Grid>

                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Địa chỉ đơn vị
                                            </p>
                                        </Grid>

                                        <Grid item md={4} sm={6} xs={12}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Tỉnh")}
                                                name='province'
                                                value={values?.province}
                                                api={pagingAdministratives}
                                                searchObject={{level:3}}
                                                handleChange={(_ , value) => {
                                                    setFieldValue("province" , value);
                                                    setFieldValue("district" , null);
                                                    setFieldValue("administrativeUnit" , null);
                                                }}
                                                readOnly={readOnly}/>
                                        </Grid>

                                        <Grid item md={4} sm={6} xs={12}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("humanResourcesInformation.district")}
                                                name='district'
                                                value={values?.district}
                                                api={pagingAdministratives}
                                                searchObject={{
                                                    level:2 ,
                                                    parentId:values?.province?.id ,
                                                }}
                                                allowLoadOptions={!!values?.province?.id}
                                                clearOptionOnClose
                                                handleChange={(_ , value) => {
                                                    setFieldValue("district" , value);
                                                    setFieldValue("administrativeUnit" , null);
                                                }}
                                                readOnly={readOnly}/>
                                        </Grid>

                                        <Grid item md={4} sm={6} xs={12}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("humanResourcesInformation.wards")}
                                                name='administrativeUnit'
                                                api={pagingAdministratives}
                                                searchObject={{
                                                    level:1 ,
                                                    parentId:values?.district?.id ,
                                                }}
                                                allowLoadOptions={!!values?.district?.id}
                                                handleChange={(_ , value) => {
                                                    setFieldValue("administrativeUnit" , value);
                                                }}
                                                clearOptionOnClose
                                                readOnly={readOnly}/>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label='Địa chỉ chi tiết'
                                                name='addressDetail'
                                                readOnly={readOnly}/>
                                        </Grid>

                                        {/* <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Phòng ban trực thuộc đơn vị
                                            </p>
                                        </Grid>

                                        <Grid item xs={12}>
                                            <ListDepartment />
                                        </Grid> */}
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button startIcon={<BlockIcon/>} variant='contained'
                                                className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                                color='secondary'
                                                onClick={() => handleClose()}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        {((isAdmin) && !readOnly) && (
                                            <Button startIcon={<SaveIcon/>}
                                                    className='mr-0 btn btn-primary d-inline-flex'
                                                    variant='contained' color='primary' type='submit'
                                                    disabled={isSubmitting}>
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

export default memo(observer(OrganizationCUForm));
