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
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingPosition } from "app/views/Position/PositionService";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import CandidateWorkingHistoryPopup from "../CandidateCUPopup/CandidateWorkingHistoryPopup";
import EditIcon from '@material-ui/icons/Edit';
import { CandidateWorkingExperience } from "app/common/Model/Candidate/CandidateWorkingExperience";
import {pagingPublicPosition} from "../../../PublicComponent/PublicService";

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

function TabCandidateWorkingExperience({ props }) {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    function handleAddNewRow(pushAction) {
        const newRow = new CandidateWorkingExperience();
        newRow.candidateId = values?.id;

        pushAction({ ...newRow });
    }

    return (
        <Grid container spacing={2}>
            {props?.public && (<h1>Kinh nghiệm làm việc</h1>)}
            <FieldArray name="candidateWorkingExperiences">
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
                                            Thêm Kinh nghiệm
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
                                            <th width="20%">Từ ngày</th>
                                            <th width="20%">Đến ngày</th>
                                            <th width="20%">Công ty</th>
                                            <th width="20%">Vị trí</th>
                                            {/* <th width="20%">Phòng ban</th> */}
                                            <th width="20%">Mức lương</th>
                                            <th width="20%">Lý do nghỉ việc</th>
                                            <th width="20%">Mô tả công việc</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {values?.candidateWorkingExperiences?.length > 0 ? (
                                            values?.candidateWorkingExperiences?.map((order, index) => (
                                                <CandidateWorkingHistoryRow
                                                    key={index}
                                                    index={index}
                                                    candidateWorkingExperiences={values?.candidateWorkingExperiences}
                                                    nameSpace={`candidateWorkingExperiences[${index}]`}
                                                    remove={() => remove(index)}
                                                    push={() => push(index)}
                                                    publicLink={props?.public}
                                                //  disabled={!hasEditPermission}
                                                />
                                            ))
                                        ) : (
                                            <tr className='row-table-body row-table-no_data'>
                                                <td colSpan={9} align='center' className="py-8">Chưa có thông tin</td>
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

export default memo(observer(TabCandidateWorkingExperience));


const CandidateWorkingHistoryRow = memo(({ index, candidateWorkingExperiences, remove, push, nameSpace, disabled, publicLink }) => {
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
        setFieldValue(`candidateWorkingExperiences[${index}]`, values);
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
                <td>
                    <GlobitsDateTimePicker
                        name={withNameSpace("startDate")}
                    />
                </td>
                <td>
                    <GlobitsDateTimePicker
                        name={withNameSpace("endDate")}
                    />
                </td>
                <td>
                    <GlobitsTextField
                        name={withNameSpace("companyName")}
                    />
                </td>
                {/* <td>
                    <GlobitsTextField
                        name={withNameSpace("departmentName")}
                    />
                </td> */}
                <td>
                    <GlobitsTextField
                        name={withNameSpace("oldPosition")}
                    />
                </td>
                <td>
                    <GlobitsVNDCurrencyInput
                        name={withNameSpace("salary")}
                    />
                </td>
                <td>
                    <GlobitsTextField
                        name={withNameSpace("leavingReason")}
                    />
                </td>
                <td>
                    <GlobitsTextField
                        name={withNameSpace("decription")}
                    />
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
                <CandidateWorkingHistoryPopup
                    open={openEditDetail}
                    handleClose={() => setOpenEditDetail(false)}
                    item={values?.candidateWorkingExperiences[index]}
                    handleSubmit={handleSaveEditDetail}
                />
            )}

        </>
    )
})
