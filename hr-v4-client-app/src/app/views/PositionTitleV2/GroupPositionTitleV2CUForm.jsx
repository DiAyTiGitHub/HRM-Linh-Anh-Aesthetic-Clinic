import React , { memo , useEffect , useState } from "react";
import { Formik , Form } from "formik";
import {
    Grid , DialogActions , Button , DialogContent ,
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import * as Yup from "yup";

import GlobitsTextField from "../../common/form/GlobitsTextField";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsEditor from "../../common/form/GlobitsEditor";
import {CodePrefixes} from "../../LocalConstants";

function GroupPositionTitleV2CUForm({readOnly}) {
    const {positionTitleV2Store} = useStore();
    const {t} = useTranslation();
    const {
        openViewPopup ,autoGenCode,
        handleClose , savePositionTitle , pagingParentPositionTitle , selectedPositionTitle , openCreateEditPopup
    } = positionTitleV2Store;

    const validationSchema = Yup.object({
        // code: Yup.string().required(t("validation.code")).nullable(),
        name:Yup.string().required(t("validation.name")).nullable() ,
    });

    async function handleSaveForm(values) {
        await savePositionTitle(values);
        await pagingParentPositionTitle();
    }

    const [initialValues , setInitialValues] = useState(selectedPositionTitle);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.NHOM_NGACH);
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
    useEffect(() => {
        if (selectedPositionTitle?.id) {
        } else {
            setInitialValues(selectedPositionTitle);
        }
    }, [selectedPositionTitle, selectedPositionTitle?.id]);

    const {isAdmin} = useStore().hrRoleUtilsStore

    return (<GlobitsPopupV2
        scroll={"body"}
        size="md"
        open={openCreateEditPopup || openViewPopup}
        noDialogContent
        title={openViewPopup ? (t("general.button.view") + ' ' + "nhóm ngạch") : (selectedPositionTitle?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "nhóm ngạch"}
        onClosePopup={handleClose}
    >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}
        >
            {({isSubmitting , values , setFieldValue , initialValues}) => {

                return (<Form autoComplete="off" autocomplete="off">
                    <div className="dialog-body">
                        <DialogContent className="p-12">
                            <FormikFocusError/>

                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6}>
                                    <GlobitsTextField
                                        label="Mã nhóm ngạch"
                                        name="code"
                                        validate
                                        readOnly={readOnly}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsTextField
                                        validate
                                        label="Tên nhóm ngạch"
                                        name="name"
                                        readOnly={readOnly}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsTextField
                                        label="Tên khác"
                                        name="otherName"
                                        readOnly={readOnly}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsTextField
                                        label="Tên viết tắt"
                                        name="shortName"
                                        readOnly={readOnly}
                                    />
                                </Grid>

                                <Grid item sm={12}>
                                    <GlobitsEditor
                                        label="Mô tả"
                                        name="description"
                                        multiline
                                        rows={3}
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
                                    <Button startIcon={<SaveIcon/>} className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                )}
                            </div>
                        </DialogActions>
                    </div>
                </Form>);
            }}
        </Formik>
    </GlobitsPopupV2>);
}

export default memo(observer(GroupPositionTitleV2CUForm));
