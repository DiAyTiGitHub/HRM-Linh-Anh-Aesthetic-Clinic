import React, {memo, useState} from "react";
import {FieldArray, useFormikContext} from "formik";
import {Grid, makeStyles, ButtonGroup, Button} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import {observer} from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import LocalConstants, {
    RECRUITMENT_REQUEST,
    RecruitmentRequestStatus,
    RecruitmentRoundsResult
} from "app/LocalConstants";
import AddIcon from "@material-ui/icons/Add";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import {Delete} from "@material-ui/icons";
import {useEffect} from "react";
import {getById as getRecruitmentById} from 'app/views/Recruitment/Recruitment/RecruitmentService';
import {pagingRecruitment} from "app/views/Recruitment/Recruitment/RecruitmentService";

import {pagingAllOrg} from "app/views/Organization/OrganizationService";
import {pagingAllDepartments} from "app/views/Department/DepartmentService";
import {pagingPositionTitle} from "../../../PositionTitle/PositionTitleService";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import {pagingRecruitmentPlan} from "../../../Recruitment/RecruitmentPlanV2/RecruitmentPlanV2Service";
import GlobitsPagingAutocomplete from "../../../../common/form/GlobitsPagingAutocomplete";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "2px",
        overflowX: "auto",
        overflowY: "hidden",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            width: "calc(100vw / 4)",
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));

function TabRecruitmentInfo() {
    const {values, setFieldValue} = useFormikContext();
    const {t} = useTranslation();

    const [openDepartmentPopup, setOpenDepartmentPopup] = useState(false);

    function handleClosePopup() {
        //setFieldValue("department", values?.department);
        setOpenDepartmentPopup(false);
    };

    function handleChangeRecruitment(_, recruitment) {
        if (!recruitment || (values?.recruitment && values?.recruitment?.id === recruitment?.id)) {
            setFieldValue("recruitment", null);
            setFieldValue("department", null);
            setFieldValue("organization", null);
        } else {
            setFieldValue("recruitment", recruitment);
            setFieldValue("department", recruitment?.department);
            setFieldValue("organization", recruitment?.organization);
            setFieldValue("recruitmentRounds", []);
        }
        setFieldValue("positionTitle", null);
    }

    return (
        <>
            <Grid container spacing={2}>
                {
                    values?.id && (
                        <Grid item xs={12} sm={6} md={4}>
                            <GlobitsTextField
                                label={t("Mã ứng viên")}
                                name="candidateCode"
                                id={"candidateCode"}
                                readOnly
                            />
                        </Grid>
                    )
                }

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsDateTimePicker
                        label={t("Ngày nộp hồ sơ")}
                        name="submissionDate"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsPagingAutocompleteV2
                        label={t("Kế hoạch tuyển dụng")}
                        name="recruitmentPlan"
                        id={"recruitmentPlan"}
                        api={pagingRecruitmentPlan}
                        searchObject={{
                            personInCharge: true,
                            recruitmentRequestStatus: [RECRUITMENT_REQUEST.START_RECRUITING, RECRUITMENT_REQUEST.RECRUITING],
                        }}
                        handleChange={(_, value) => {
                            setFieldValue("recruitmentPlan", value);
                            setFieldValue("organization", value?.recruitmentRequest?.organization);
                            setFieldValue("department", value?.recruitmentRequest?.hrDepartment);
                            setFieldValue("positionTitle", value?.recruitmentRequest?.recruitmentRequestItem?.positionTitle);
                        }}
                    />
                </Grid>

                <Grid item md={4} sm={6} xs={12}>
                    <GlobitsPagingAutocompleteV2
                        name="organization"
                        label="Đơn vị"
                        api={pagingAllOrg}
                        disabled={values?.recruitment?.id}
                        handleChange={(_, value) => {
                            setFieldValue("organization", value);
                            setFieldValue("department", null);
                            setFieldValue("positionTitle", null);
                        }}
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsPagingAutocompleteV2
                        label={t("Phòng ban")}
                        name="department"
                        api={pagingAllDepartments}
                        handleChange={(_, value) => {
                            setFieldValue("department", value);
                            setFieldValue("positionTitle", null);
                        }}
                        searchObject={{
                            pageIndex: 1,
                            pageSize: 9999,
                            keyword: "",
                            organizationId: values?.organization?.id,
                        }}
                        disabled={values?.recruitment?.id}
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsPagingAutocomplete
                        label={"Chức danh cần tuyển"}
                        validate
                        name="positionTitle"
                        id={"positionTitle"}
                        api={pagingPositionTitle}
                        disabled={!values?.department}
                        searchObject={{
                            pageIndex: 1,
                            pageSize: 9999,
                            keyword: "",
                            departmentId: values?.department?.id,
                        }}

                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={t("Mức lương kỳ vọng (VNĐ)")}
                        name="desiredPay"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsDateTimePicker
                        label={t("Ngày có thể bắt đầu làm việc")}
                        name="possibleWorkingDate"
                    />
                </Grid>

                {values?.status === LocalConstants.CandidateStatus.APPROVED.value && (
                    <Grid item xs={12} sm={6} md={4}>
                        <GlobitsDateTimePicker
                            label={t("Ngày phỏng vấn/thi tuyển")}
                            name="interviewDate"
                        />
                    </Grid>
                )}

                {values?.status === LocalConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.value && (
                    <Grid item xs={12} sm={6} md={4}>
                        <GlobitsDateTimePicker
                            label={t("Ngày nhận việc")}
                            name="onboardDate"
                            id={"onboardDate"}

                        />
                    </Grid>
                )}

                <Grid item xs={12} sm={6} md={4}>
                    <ChooseUsingStaffSection
                        label="Người giới thiệu"
                        placeholder="Người giới thiệu"
                        name="introducer"
                    />
                </Grid>
            </Grid>

            {/* <CandidateRecruitment /> */}
        </>
    );
}

export default memo(observer(TabRecruitmentInfo));

const CandidateRecruitment = memo(({disabled}) => {
    const classes = useStyles();
    const {t} = useTranslation();
    const {values} = useFormikContext();

    const [listRecruitmentRound, setListRecruitmentRound] = useState([]);
    const [itemDelete, setItemDelete] = useState(null);

    useEffect(() => {
        if (values?.recruitment?.id) {
            getRecruitmentById(values?.recruitment?.id).then(res => {
                setListRecruitmentRound(res?.data?.recruitmentRounds || [])
            })
        } else {
            setListRecruitmentRound([])
        }
    }, [values?.recruitment])

    return (
        <FieldArray name="recruitmentRounds">
            {({remove, push}) => (
                <>
                    <h5 className="mt-24">Kết Quả Tuyển Dụng</h5>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={4} md={3} lg={2}>
                            <ButtonGroup color="container" aria-label="outlined primary button group">
                                <Button
                                    startIcon={<AddIcon/>}
                                    type="button" onClick={() => push({})}
                                    disabled={!values?.recruitment || disabled}
                                >
                                    Thêm kết quả
                                </Button>
                            </ButtonGroup>
                        </Grid>
                    </Grid>

                    <section className={classes.tableContainer} style={{overflowX: "auto", tableLayout: 'fixed'}}>
                        <table className={classes.table}>
                            <thead>
                            <tr className={classes.tableHeader}>
                                <th width="40px">Thao tác</th>
                                <th>Vòng</th>
                                <th width="30%">Kết quả</th>
                                <th width="30%">Ghi chú</th>
                            </tr>
                            </thead>
                            <tbody>
                            {values?.recruitmentRounds?.map((_, index) => (
                                <tr>
                                    <td align="center">
                                        {!disabled &&
                                            <span
                                                className="pointer tooltip"
                                                style={{cursor: 'pointer'}}
                                                onClick={() => setItemDelete(index)}
                                            >
                                                    <Delete className="text-red"/>
                                                </span>
                                        }
                                    </td>
                                    <td>
                                        <GlobitsAutocomplete
                                            name={`recruitmentRounds[${index}].recruitmentRound`}
                                            options={listRecruitmentRound}
                                            getOptionDisabled={(option) => values?.recruitmentRounds?.some(item => option?.id === item?.recruitmentRound?.id)}
                                        />
                                    </td>
                                    <td>
                                        <GlobitsSelectInput name={`recruitmentRounds[${index}].result`}
                                                            options={RecruitmentRoundsResult.getListData()}/>
                                    </td>
                                    <td>
                                        <GlobitsTextField name={`recruitmentRounds[${index}].note`}/>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </section>

                    <GlobitsConfirmationDialog
                        open={itemDelete !== null}
                        onConfirmDialogClose={() => setItemDelete(null)}
                        onYesClick={() => remove(itemDelete)}
                        title={t("confirm_dialog.delete.title")}
                        text={"Bạn có chắc muốn kết quả này không?"}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                </>
            )}
        </FieldArray>
    )
})