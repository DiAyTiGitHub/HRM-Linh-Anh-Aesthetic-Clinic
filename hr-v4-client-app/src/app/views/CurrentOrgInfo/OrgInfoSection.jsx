import React, { memo } from "react";
import { Grid } from "@material-ui/core";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import GlobitsImageUpload from "app/common/form/FileUpload/GlobitsImageUpload";
import { Form, Formik } from "formik";

function OrgInfoSection() {
    const { organizationStore } = useStore();
    const {
        selectedOrganization
    } = organizationStore;

    return (
        <div className="index-card p-8 pb-12">
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        validationSchema={{}}
                        enableReinitialize
                        initialValues={selectedOrganization}
                        onSubmit={function () { }}
                    >
                        {({ isSubmitting, values, setFieldValue, initialValues }) => {

                            return (
                                <Form autoComplete="off" autocomplete="off">
                                    <GlobitsImageUpload
                                        disabled
                                        wrapperAvatarStyle={{
                                            objectFit: 'cover',
                                            objectPosition: 'center',
                                            maxHeight: "320px",
                                            width: '100%',
                                            height: 'auto',
                                            borderRadius: "unset !important",
                                            fontSize: "unset !important"
                                        }}
                                        field="file"
                                        imagePath={values?.imagePath}
                                    />
                                </Form>
                            )
                        }}
                    </Formik>

                </Grid>
                {/* <Grid item xs={12} className="flex align-center py-0">
                    <strong>Mã công ty:</strong>
                    <p
                        className="organizationInfoField m-0 pl-8 text-dark-green"
                    >
                        {selectedOrganization?.code ? (" " + selectedOrganization?.code) : ""}
                    </p>
                </Grid> */}
                <Grid item xs={12} className="flex align-center justify-center py-0">
                    {/* <strong>Tên công ty:</strong> */}
                    <p
                        className="organizationInfoField m-0 pl-8"
                    >
                        {selectedOrganization?.name ? (" " + selectedOrganization?.name) : ""}
                    </p>

                    {selectedOrganization?.code && (
                        <p
                            className="organizationInfoField m-0 pl-8 text-dark-green"
                        >
                            {"(" + selectedOrganization?.code + ")"}
                            {/* (selectedOrganization?.code) */}
                        </p>
                    )}
                </Grid>

                <Grid item xs={12} className="flex align-center justify-center py-0">
                    {/* <strong>Website:</strong> */}

                    <a
                        className="organizationInfoField m-0 pl-8 hyperLink"
                        href={selectedOrganization?.website}
                    >
                        {selectedOrganization?.website ? (" " + selectedOrganization?.website) : ""}
                    </a>

                    {/* {selectedOrganization?.website && (
                                        <LinkPreview url={selectedOrganization?.website} />
                                    )} */}
                </Grid>
            </Grid>

        </div>

    );
}


export default memo(observer(OrgInfoSection));