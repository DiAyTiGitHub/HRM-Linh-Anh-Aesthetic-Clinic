import React , { useEffect , useRef } from 'react';
import { renderAsync } from 'docx-preview';
import { viewerLabourAgreement } from "./StaffLabourAgreementService";
import { useStore } from "../../stores";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid , makeStyles } from "@material-ui/core";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles({
    root:{
        overflowY:'auto' ,
        "& .docx-wrapper":{
            padding:"10px" ,
            "& .docx":{
                margin:"0px" ,
            }
        }
    } ,
});
const LabourAgreementViewer = ({id}) => {
    const {staffLabourAgreementStore} = useStore();
    const {t} = useTranslation();
    const classes = useStyles();
    const containerRef = useRef(null);

    const {
        openPreviewPopup ,
        handleClose ,
        handleExportHDLD
    } = staffLabourAgreementStore;

    useEffect(() => {
        const fetchAndRenderDocx = async () => {
            try {
                const blob = await viewerLabourAgreement(id)

                await renderAsync(blob , containerRef.current);
            } catch (error) {
                console.error("Lỗi khi tải hợp đồng:" , error);
            }
        };

        fetchAndRenderDocx();
    } , [id]);

    async function handleSaveForm() {
        try {
            await handleExportHDLD(id)
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="md"
            open={openPreviewPopup}
            noDialogContent
            title={"Thông tin hợp đồng"}
            onClosePopup={handleClose}
        >
            <Formik
                onSubmit={handleSaveForm}
                initialValues={{}}>
                {({isSubmitting , values , setFieldValue , initialValues , setFieldTouched , setFieldError}) => {

                    return (
                        <Form autoComplete="off">
                            <DialogContent className='o-hidden dialog-body w-100'>
                                <div
                                    className={classes.root}
                                    ref={containerRef}
                                />
                            </DialogContent>
                            <DialogActions className="dialog-footer">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        disabled={isSubmitting}
                                        onClick={handleClose}
                                    >
                                        {t("Thoát")}
                                    </Button>
                                    <Button
                                        className="mr-0 btn btn-primary d-inline-flex"
                                        variant="contained"
                                        color="primary"
                                        type="submit"
                                        disabled={isSubmitting}
                                    >
                                        {t("Tải hợp đồng")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </Form>
                    );
                }
                }
            </Formik>
        </GlobitsPopupV2>
    );
};

export default LabourAgreementViewer;
