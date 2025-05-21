import {Checkbox, FormControlLabel, Grid, Typography} from "@material-ui/core";
import GlobitsTextField from "../../../../common/form/GlobitsTextField";
import GlobitsDateTimePicker from "../../../../common/form/GlobitsDateTimePicker";
import FormGroup from "@material-ui/core/FormGroup";
import React from "react";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";

const EvaluationFormStaffInformation = ({listContractType}) => {
    const {t} = useTranslation();
    const {values, setFieldValue} = useFormikContext();
    return (
        <>
            <Grid container spacing={2}>
                <Grid item xs={12} md={3}>
                    <GlobitsTextField label={t("Họ tên")} name='staffName' disabled/>
                </Grid>
                <Grid item xs={12} md={3}>
                    <GlobitsTextField label={t("Mã nhân viên")} name='staffCode' disabled/>
                </Grid>
                <Grid item xs={12} md={3}>
                    <GlobitsTextField label={t("Chức danh")} name='position' disabled/>
                </Grid>
                <Grid item xs={12} md={3}>
                    <GlobitsTextField label={t("Ban")} name='department' disabled/>
                </Grid>
                <Grid item xs={12} md={3}>
                    <GlobitsTextField label={t("Phòng/Cơ sở")} name='division' disabled/>
                </Grid>
                <Grid item xs={12} md={3}>
                    <GlobitsTextField label={t("Bộ phận/Nhóm")} name='team' disabled/>
                </Grid>
                <Grid item xs={12} md={3}>
                    <GlobitsTextField label={t("Quản lý trực tiếp")} name='directManagerName' disabled/>
                </Grid>
                <Grid item xs={12} md={3}>
                    <Grid container spacing={2}>
                        <Grid item xs={12} md={6}>
                            <GlobitsDateTimePicker label={t("Ngày nhận việc")} name='hireDate' disabled />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <GlobitsDateTimePicker label={t("Thời hạn HĐLĐ trước")} name='previousContractDuration' disabled/>
                        </Grid>
                    </Grid>
                </Grid>

                <Grid item xs={12} md={12}>
                    <FormGroup className={"flex align-center"} row>
                        <Typography style={{marginRight: "50px", fontWeight: '700'}}>
                            Loại HĐLĐ:
                        </Typography>
                        {
                            listContractType?.map(contract => {
                                if(contract.code === "XĐTH" || contract.code === "KXĐTH"){
                                    return (
                                        <FormControlLabel
                                            key={contract.id}
                                            control={
                                                <Checkbox
                                                    name="contractTypeName"
                                                    checked={values?.contractTypeId === contract?.id}
                                                    onChange={(__, value) => {
                                                        if (value) {
                                                            setFieldValue('contractTypeId', contract?.id)
                                                        } else {
                                                            setFieldValue('contractTypeId', null)
                                                        }
                                                    }}
                                                />
                                            }
                                            label={contract.name}
                                        />
                                    );
                                }
                            })
                        }
                    </FormGroup>
                </Grid>
            </Grid>
        </>
    )
}
export default EvaluationFormStaffInformation