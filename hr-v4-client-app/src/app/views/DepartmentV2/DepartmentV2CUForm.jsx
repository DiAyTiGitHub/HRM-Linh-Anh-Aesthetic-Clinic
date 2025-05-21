import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { pagingPosition } from "app/views/Position/PositionService";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import SelectDepartmentComponent from "../../common/SelectComponent/SelectDepartment/SelectDepartmentComponent";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingDepartmentType } from "../DepartmentType/DepartmentTypeService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import AutoFillDepartmentInfo from "./AutoFillDepartmentInfo";
import { CodePrefixes } from "../../LocalConstants";

function DepartmentV2CUForm(props) {
    const { t } = useTranslation();

    const { departmentV2Store } = useStore();
    const { readOnly } = props;
    const { isAdmin } = useStore().hrRoleUtilsStore;
    const { handleClose, saveDepartment, pagingAllDepartment, selectedDepartment, openCreateEditPopup, openViewPopup, autoGenCode } =
        departmentV2Store;

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")).nullable(),
        name: Yup.string().required(t("validation.required")).nullable(),
        // shortName: Yup.string().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        await saveDepartment(values);
        await pagingAllDepartment();
    }

    const [initialValues, setInitialValues] = useState(selectedDepartment);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.DANH_SACH_PHONG_BAN);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = { ...selectedDepartment, code };
            setInitialValues(updated);
        }
    };
    useEffect(() => {
        if (!selectedDepartment?.id) {
            autoGenCodeFunc();
        }
    }, []);
    useEffect(
        function () {
            setInitialValues({
                ...selectedDepartment,
            });
        },
        [selectedDepartment, selectedDepartment?.id]
    );

    return (
        <GlobitsPopupV2
            size='md'
            scroll={"body"}
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={
                openViewPopup
                    ? t("general.button.view") + " " + t("Phòng ban")
                    : (selectedDepartment?.id ? t("general.button.edit") : t("general.button.add")) + " " + "Phòng ban"
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
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
                                            required
                                            name='code'
                                            readOnly={readOnly || !!values?.id}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsTextField
                                            label={"Tên phòng ban"}
                                            required
                                            name='name'
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

                                    <AutoFillDepartmentInfo readOnly={readOnly} />

                                    {/* <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <SelectDepartmentComponent
                                            name={"parent"}
                                            label={t("department.parent")}
                                            clearFields={["title"]}
                                            disabled={readOnly}
                                            disabledTextFieldOnly={true}
                                            readOnly={readOnly}
                                            placeholder={"Không có phòng ban cha"}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            name='hrDepartmentType'
                                            label={t("department.hrdepartmentType")}
                                            api={pagingDepartmentType}
                                            getOptionLabel={(option) =>
                                                option?.name && option?.sortNumber
                                                    ? `${option.name} - ${option.sortNumber}`
                                                    : option?.name || option?.sortNumber || ""
                                            }
                                            readOnly={readOnly}
                                        />
                                    </Grid> */}

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
                                                departmentId: values?.id,
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
                                                pageIndex: 1,
                                                pageSize: 10,
                                            }}
                                            api={pagingPositionTitle}
                                            readOnly={readOnly}
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("department.subDepartments")}
                                            name='children'
                                            multiple
                                            searchObject={{
                                                pageIndex: 1,
                                                pageSize: 10,
                                            }}
                                            api={pagingAllDepartments}
                                            getOptionDisabled={function (option) {
                                                return values?.id === option?.id || values?.parent?.id === option?.id;
                                            }}
                                            readOnly={readOnly}
                                        />
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
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                        color='secondary'
                                        onClick={() => handleClose()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    {isAdmin && !readOnly && (
                                        <Button
                                            startIcon={<SaveIcon />}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'
                                            disabled={isSubmitting}>
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

export default memo(observer(DepartmentV2CUForm));
