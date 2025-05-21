import React, { useState, useEffect, memo } from "react";
import { Button, Grid } from "@material-ui/core";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import * as Yup from "yup";
import { Form, Formik } from "formik";
import { useTranslation } from "react-i18next";
import SaveOutlinedIcon from '@material-ui/icons/SaveOutlined';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsImageUpload from "app/common/form/FileUpload/GlobitsImageUpload";

function UpdateOrgInfoSection(props) {
    const { handleCancelUpdate } = props;

    const { organizationStore } = useStore();
    const { t } = useTranslation();
    const {
        saveOrganization,
        selectedOrganization,
        getCurrentOrganizationOfCurrentUser,
        uploadImage
    } = organizationStore;

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.code")).nullable(),
        name: Yup.string().required(t("validation.name")).nullable(),
    });

    async function handleSaveForm(values) {
        await saveOrganization(values);
        await getCurrentOrganizationOfCurrentUser();
        handleCancelUpdate();
    }

    const [initialValues, setInitialValues] = useState(selectedOrganization);

    useEffect(function () {
        setInitialValues(selectedOrganization);
    }, [selectedOrganization, selectedOrganization?.id]);

    return (
        <div className="index-card p-8 pb-12">
            <Grid item xs={12} className="">
                <Formik
                    validationSchema={validationSchema}
                    enableReinitialize
                    initialValues={initialValues}
                    onSubmit={handleSaveForm}
                >
                    {({ isSubmitting, values, setFieldValue, initialValues, resetForm }) => {
                        async function handleUploadImage(value) {
                            try {
                                const { data } = await uploadImage(value);
                                setFieldValue("imagePath", data?.name);
                            }
                            catch (err) {
                                console.error(err);
                            }
                        }

                        return (
                            <Form autoComplete="off" autocomplete="off">
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <GlobitsImageUpload
                                            wrapperAvatarStyle={{
                                                objectFit: 'cover',
                                                objectPosition: 'center',
                                                width: '100%',
                                                height: 'auto',
                                                borderRadius: "unset !important",
                                                fontSize: "unset !important"
                                            }}
                                            field="file"
                                            onChange={(_, value) => handleUploadImage(value)}
                                            imagePath={values?.imagePath}
                                            name="imagePath"
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <GlobitsTextField
                                            validate
                                            label="Mã công ty"
                                            name="code"
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <GlobitsTextField
                                            validate
                                            label="Tên công ty"
                                            name="name"
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label="Website"
                                            name="website"
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <div className="pt-8 mt-12" style={{ borderTop: "1px solid #b3b3b3" }}>
                                            <div className="flex justify-end" >
                                                {/* <Button
                                                className="btn px-8 py-2 bg-light-gray d-inline-flex mr-12"
                                                type="button"
                                                onClick={() => resetForm()}
                                            >
                                                <RotateLeftIcon className="mr-6" />
                                                Đặt lại
                                            </Button> */}

                                                <Button
                                                    className="btn px-8 py-2 btn-success d-inline-flex"
                                                    // fullWidth
                                                    type="submit"
                                                >
                                                    <SaveOutlinedIcon className="mr-6" />
                                                    Lưu thông tin
                                                </Button>
                                            </div>
                                        </div>
                                    </Grid>
                                </Grid>

                            </Form>
                        )
                    }
                    }
                </Formik>
            </Grid>
        </div>
    );
}


export default memo(observer(UpdateOrgInfoSection));