import {
    Button,
    ButtonGroup,
    Grid,
    makeStyles,
    Tooltip
} from "@material-ui/core";
import { Add, Delete } from "@material-ui/icons";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { pagingCertificates } from "app/views/Certificate/CertificateService";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from 'react-i18next';
import AddIcon from "@material-ui/icons/Add";
import EditIcon from '@material-ui/icons/Edit';
import { pagingCountry } from "app/views/Country/CountryService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingTrainingBases } from "app/views/TrainingBase/TrainingBaseService";
import { pagingSpecialities } from "app/views/Speciality/SpecialityService";
import { pagingEducationDegrees } from "app/views/EducationDegree/EducationDegreeService";
import { pagingEducationTypes } from "app/views/EducationType/EducationTypeService";
import { EducationalHistory } from "app/common/Model/EducationalHistory";
import CandidateEducationalHistoryPopup from "../CandidateCUPopup/CandidateEducationalHistoryPopup";
import { CandidateEducationHistory } from "app/common/Model/Candidate/CandidateEducationHistory";
import {
    pagingPublicCountry, pagingPublicEducationDegrees,
    pagingPublicEducationTypes,
    pagingPublicSpecialities, pagingPublicTrainingBases
} from "../../../PublicComponent/PublicService";


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

function TabCandidateEducationalHistory({props}) {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    function handleAddNewRow(pushAction) {
        const newRow = new CandidateEducationHistory();
        newRow.candidateId = values?.id;

        pushAction({ ...newRow });
    }

    return (
        <Grid container spacing={2}>
            {props?.public && (<h1>Quá trình đào tạo</h1>)}
            <FieldArray name="candidateEducationalHistories">
                {({ insert, remove, push }) => (
                    <>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={4} md={3} lg={2}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AddIcon />}
                                            type="button"
                                            onClick={() => handleAddNewRow(push)}
                                        >
                                            Thêm quá trình
                                        </Button>
                                    </ButtonGroup>
                                </Grid>
                            </Grid>
                        </Grid>

                        <Grid item xs={12} style={{ overflowX: "auto" }}>
                            <section className={classes.tableContainer}>
                                <table className={classes.table}>
                                    <thead>
                                        <tr className={classes.tableHeader}>
                                            <th width="10%">Thao tác</th>
                                            <th width="20%">Năm nhập học</th>
                                            <th width="20%">Năm tốt nghiệp</th>
                                            <th width="20%">Cơ sở đào tạo</th>
                                            <th width="20%">Nước đào tạo</th>
                                            <th width="20%">Chuyên ngành</th>
                                            <th width="20%">Hình thức</th>
                                            <th width="20%">Bằng cấp</th>
                                            <th width="20%">Ghi chú</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {values?.candidateEducationalHistories?.length > 0 ? (
                                            values?.candidateEducationalHistories?.map((order, index) => (
                                                <EducationalHistoryRow
                                                    key={index}
                                                    index={index}
                                                    candidateEducationalHistories={values?.candidateEducationalHistories}
                                                    nameSpace={`candidateEducationalHistories[${index}]`}
                                                    remove={() => remove(index)}
                                                    push={() => push(index)}
                                                    publicLink={props?.public}
                                                //  disabled={!hasEditPermission}
                                                />
                                            ))
                                        ) : (
                                            <tr className='row-table-body row-table-no_data'>
                                                <td colSpan={10} align='center' className="py-8">Chưa có thông tin</td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </section>
                        </Grid>

                    </>
                )}
            </FieldArray>
        </Grid>
    )
}

export default memo(observer(TabCandidateEducationalHistory));


const EducationalHistoryRow = memo(({ index, candidateEducationalHistories, remove, push, nameSpace, disabled, publicLink }) => {
    const { setFieldValue, values } = useFormikContext();
    const { t } = useTranslation();

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    const [openConfirmDeletePopup, setOpenConfirmDeletePopup] = useState(false);

    function handleConfirmDeleteItem() {
        // setOpenConfirmDeletePopup(true);
        remove();
    }

    const [openEditDetail, setOpenEditDetail] = useState(false);

    function handleSaveEditDetail(values) {
        setFieldValue(`candidateEducationalHistories[${index}]`, values);
        setOpenEditDetail(false);
    }

    return (
        <>
            <tr className='row-table-body' key={index}>
                <td align='center'>
                    <Tooltip placement="top" title="Chỉnh sửa chi tiết">
                        <span
                            className="pointer tooltip"
                            style={{ cursor: 'pointer' }}
                            onClick={() => setOpenEditDetail(true)}
                        >
                            <EditIcon className="text-primary font-size-20" />
                        </span>

                    </Tooltip>

                    <Tooltip placement="top" title="Xóa">
                        <span
                            className="pointer tooltip"
                            style={{ cursor: 'pointer' }}
                            onClick={() => setOpenConfirmDeletePopup(true)}
                        >
                            <Delete className="text-red font-size-20" />
                        </span>
                    </Tooltip>
                </td>
                {/* <th width="20%">Năm nhập học theo QĐ</th> */}
                <td>
                    <GlobitsDateTimePicker
                        name={withNameSpace("startDate")}
                    />
                </td>
                {/* <th width="20%">Năm tốt nghiệp theo QĐ</th> */}
                <td>
                    <GlobitsDateTimePicker
                        name={withNameSpace("endDate")}
                    />
                </td>
                {/* <th width="20%">Cơ sở đào tạo</th> */}
                <td>
                    <GlobitsPagingAutocomplete
                        name={withNameSpace("educationalInstitution")}
                        api={publicLink ? pagingPublicTrainingBases : pagingTrainingBases}
                    />
                </td>
                {/* <th width="20%">Nước đào tạo</th> */}
                <td>
                    <GlobitsPagingAutocomplete
                        name={withNameSpace("country")}
                        api={publicLink ? pagingPublicCountry : pagingCountry}
                    />
                </td>
                {/* <th width="20%">Chuyên ngành</th> */}
                <td>
                    <GlobitsPagingAutocomplete
                        name={withNameSpace("major")}
                        api={publicLink ? pagingPublicSpecialities : pagingSpecialities}
                    />
                </td>
                {/* <th width="20%">Hình thức</th> */}
                <td>
                    <GlobitsPagingAutocomplete
                        name={withNameSpace("educationType")}
                        api={publicLink ? pagingPublicEducationTypes : pagingEducationTypes}
                    />
                </td>
                {/* <th width="20%">Bằng cấp</th> */}
                <td>
                    <GlobitsPagingAutocomplete
                        name={withNameSpace("educationDegree")}
                        api={publicLink ? pagingPublicEducationDegrees : pagingEducationDegrees}
                    />
                </td>
                {/* <th width="20%">Ghi chú</th> */}
                <td>
                    <GlobitsTextField name={withNameSpace("description")} />
                </td>
            </tr>

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={() => setOpenConfirmDeletePopup(false)}
                    onYesClick={handleConfirmDeleteItem}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa quá trình này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

            {openEditDetail && (
                <CandidateEducationalHistoryPopup
                    open={openEditDetail}
                    handleClose={() => setOpenEditDetail(false)}
                    item={values?.candidateEducationalHistories[index]}
                    handleSubmit={handleSaveEditDetail}
                />
            )}


        </>
    )
});
