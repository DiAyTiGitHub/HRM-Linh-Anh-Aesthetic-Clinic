import React , { memo , useEffect , useState } from "react";
import { Formik , Form } from "formik";
import {
    Grid , makeStyles , DialogActions , Button , DialogContent ,
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import * as Yup from "yup";

import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";
import FormikFocusError from "app/common/FormikFocusError";
import { pagingRankTitle } from "../RankTitle/RankTitleService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { pagingPositionRole } from "../PositionRole/PositionRoleService";
import LocalConstants, {CodePrefixes} from "app/LocalConstants";
import ChoosingParentPositionTitlePopup from "./ChoosingParentPositionTitlePopup";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";
import GlobitsEditor from "../../common/form/GlobitsEditor";
import { pagingParentPositionTitle , pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingHasPermissionDepartments } from "../HumanResourcesInformation/StaffService";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";

function PositionTitleV2CUForm({readOnly}) {
    const {positionTitleV2Store} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        savePositionTitle ,
        pagingPositionTitle ,
        selectedPositionTitle ,
        openCreateEditPopup ,
        openViewPopup,
        autoGenCode
    } = positionTitleV2Store;

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")).nullable() ,
        name:Yup.string().required(t("validation.name")).nullable() ,
        parent:Yup.object().required(t("validation.required")).nullable() ,
    });

    async function handleSaveForm(values) {
        await savePositionTitle(values);
        await pagingPositionTitle();
    }

    const [initialValues , setInitialValues] = useState(selectedPositionTitle);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.CHUC_DANH);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...selectedPositionTitle, code};
            setInitialValues(updated);
        }
    };
    useEffect(() => {
        if(!selectedPositionTitle?.id){
            autoGenCodeFunc();
        }
    }, []);
    useEffect(function () {
        setInitialValues(selectedPositionTitle);
    } , [selectedPositionTitle , selectedPositionTitle?.id]);

    const {isAdmin} = useStore().hrRoleUtilsStore

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="md"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? (t("general.button.view") + " " + t("chức danh")) : (selectedPositionTitle?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "chức danh"}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}
            >
                {({isSubmitting , values , setFieldValue , initialValues}) => {

                    return (
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError/>

                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <p className='m-0 p-0 borderThrough2'>
                                                Thông tin chức danh
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label="Mã chức danh"
                                                name="code"
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                required
                                                label="Tên chức danh"
                                                name="name"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label="Tên khác"
                                                name="otherName"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                label="Tên viết tắt"
                                                name="shortName"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                label={"Nhóm ngạch"}
                                                name="parent"
                                                api={pagingParentPositionTitle}
                                                getOptionLabel={(option) =>
                                                    option?.name && option?.code
                                                        ? `${option.name} - ${option.code}`
                                                        : option?.name || option?.code || ''
                                                }
                                                required

                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                label={"Cấp bậc"}
                                                name="rankTitle"
                                                api={pagingRankTitle}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsNumberInput
                                                label={"Số ngày tuyển dụng"}
                                                name="recruitmentDays"
                                                api={pagingRankTitle}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <p className='m-0 p-0 borderThrough2'>Ngày công chuẩn</p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                hideNullOption
                                                label={"Cách tính ngày công chuẩn"}
                                                name="workDayCalculationType"
                                                keyValue='value'
                                                options={LocalConstants.PositionTitleWorkdayCalculationType.getListData()}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        {
                                            values?.workDayCalculationType === LocalConstants.PositionTitleWorkdayCalculationType.FIXED.value && (
                                                <Grid item xs={12} sm={6} md={4}>
                                                    <GlobitsVNDCurrencyInput
                                                        label={"Số ngày làm việc ước tính (tháng)"}
                                                        name="estimatedWorkingDays"
                                                        readOnly={readOnly}
                                                    />
                                                </Grid>
                                            )
                                        }

                                        <Grid item xs={12}>
                                            <p className='m-0 p-0 borderThrough2'>Khác</p>
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Chức danh trực thuộc phòng ban")}
                                                name="departments"
                                                multiple
                                                api={pagingHasPermissionDepartments}
                                                getOptionLabel={(option) =>
                                                    [option?.name , option?.code].filter(Boolean).join(' - ') || ''
                                                }
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item sm={12}>
                                            <GlobitsEditor
                                                label="Mô tả"
                                                name="description"
                                                multiline
                                                rows={2}
                                                readOnly={readOnly}
                                            />
                                        </Grid>
                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className="dialog-footer py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
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
        </GlobitsPopupV2>);
}

export default memo(observer(PositionTitleV2CUForm));
