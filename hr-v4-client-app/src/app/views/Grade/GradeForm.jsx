import React, {useEffect, useState} from "react";
import {Form, Formik, useFormikContext} from "formik";
import {Button, DialogActions, DialogContent, Grid, makeStyles,} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import {observer} from "mobx-react";
import * as Yup from "yup";

import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

const useStyles = makeStyles((theme) => ({
    gridContainerForm: {
        maxHeight: "68vh",
        overflowY: "auto",
        marginBottom: 10,
    },
    textField: {
        width: "100%",
        margin: "10px 0px !important",
    },
}));

function ConflictInput() {
    const {values, setFieldValue} = useFormikContext();
    useEffect(() => {
        if (values?.grade) {
            let result = values?.grade + 1;
            setFieldValue("nextGradeId", result);
        }
    }, [setFieldValue, values.grade]);
    return (
        <>
            <Grid item md={4} sm={12} xs={12}>
                <GlobitsTextField
                    label={
                        <span>
              
              {"Bậc tiếp theo"}
                            <span style={{color: 'red'}}> * </span>
            </span>
                    }
                    name="nextGradeId"
                    type="number"
                />
            </Grid>


        </>
    )

}

export default observer(function GradeForm(props) {
    const classes = useStyles();
    const {gradeStore} = useStore();
    const {t} = useTranslation();
    const {handleClose, createGrade, editGrade, selectedGrade} = gradeStore;

    const [grade, setGrade] = useState({
        id: "",
        grade: null,
        code: "",
        name: "",
        nextGradeId: null,
        maxGrade: null,
        salaryCoefficient: null,
        description: "",
    });

    const validationSchema = Yup.object({
        grade: Yup.number().min(0, "Giá trị thấp nhất là 0").required(t("validation.required")).nullable(),
        code: Yup.string().required(t("validation.required")),
        name: Yup.string().required(t("validation.required")),
        nextGradeId: Yup.number().min(0, "Giá trị thấp nhất là 0").required(t("validation.required")).nullable(),
        maxGrade: Yup.number().min(0, "Giá trị thấp nhất là 0").required(t("validation.required")).nullable(),
        salaryCoefficient: Yup.number().min(0, "Giá trị thấp nhất là 0").required(t("validation.required")).nullable(),
    });

    useEffect(() => {
        if (selectedGrade) setGrade(selectedGrade);
    }, [selectedGrade]);

    useEffect(() => {
        console.log(grade);
    }, [grade]);


    return (
        <GlobitsPopupV2
            size={"md"}
            open={props.open}
            noDialogContent
            title={(selectedGrade?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("grade.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={grade}
                onSubmit={(values) => values.id.length === 0 ? createGrade(values) : editGrade(values)}
            >
                {({isSubmitting, values, setFieldValue}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container className={classes.gridContainerForm} spacing={2}>
                                    <Grid item md={4} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                          {t("grade.grade")}
                                                    <span style={{color: 'red'}}> * </span>
                        </span>
                                            }
                                            name="grade"
                                            type="number"
                                        />
                                    </Grid>

                                    <Grid item md={4} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                          
                          {t("grade.name")}
                                                    <span style={{color: 'red'}}> * </span>
                        </span>
                                            }
                                            name="name"/>
                                    </Grid>

                                    <Grid item md={4} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                          
                          {t("grade.code")}
                                                    <span style={{color: 'red'}}> * </span>
                        </span>
                                            }
                                            name="code"/>
                                    </Grid>

                                    <ConflictInput/>

                                    <Grid item md={4} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                          {t("grade.maxGrade")}
                                                    <span style={{color: 'red'}}> * </span>
                        </span>
                                            }
                                            name="maxGrade"
                                            type="number"
                                        />
                                    </Grid>

                                    <Grid item md={4} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                          
                          {t("grade.salaryCoefficient")}
                                                    <span style={{color: 'red'}}> * </span>
                        </span>
                                            }
                                            name="salaryCoefficient"
                                            type="number"
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("grade.description")}
                                            name="description"
                                            multiline
                                            rows={3}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className="dialog-footer">
                            <DialogActions className="p-0">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        startIcon={<BlockIcon/>}
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        onClick={() => handleClose()}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
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
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
});
