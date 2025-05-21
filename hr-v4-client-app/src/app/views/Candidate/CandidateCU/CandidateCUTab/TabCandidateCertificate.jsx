import {
    Button,
    ButtonGroup,
    Grid,
    makeStyles
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
import {pagingPublicCertificate} from "../../../PublicComponent/PublicService";

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

function TabCandidateCertificate({ props }) {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    return (
        <Grid container spacing={2}>
            {props?.public && (<h1>Chứng chỉ</h1>)}
            <FieldArray name="candidateCertificates">
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
                                            onClick={() => {
                                                push({
                                                    candidateCertificates: null,
                                                })
                                            }}
                                        >
                                            Thêm chứng chỉ
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
                                            <th width="30%">Tên</th>
                                            <th width="30%">Chứng chỉ</th>
                                            <th width="20%">Cấp độ</th>
                                            <th width="10%">Ngày nhận</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {values?.candidateCertificates?.length > 0 ? (
                                            values?.candidateCertificates?.map((order, index) => (
                                                <CandidateCertificateRow
                                                    key={index}
                                                    index={index}
                                                    props={props}
                                                    candidateCertificates={values?.candidateCertificates}
                                                    nameSpace={`candidateCertificates[${index}]`}
                                                    remove={() => remove(index)}
                                                    push={() => push(index)}
                                                //  disabled={!hasEditPermission}
                                                />
                                            ))
                                        ) : (
                                            <tr className='row-table-body row-table-no_data'>
                                                <td colSpan={5} align='center' className="py-8">Chưa có thông tin</td>
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

export default memo(observer(TabCandidateCertificate));


const CandidateCertificateRow = memo(({ props,index, candidateCertificates, remove, push, nameSpace, disabled }) => {
    const { setFieldValue, values } = useFormikContext();
    const { t } = useTranslation();
    const handleTabKeyPress = (event) => {
        if (event.key === 'Tab') {
            if ((Number(index) === Number(candidateCertificates?.length - 1))) {
                push();
            }
        }
    };

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    const [openConfirmDeletePopup, setOpenConfirmDeletePopup] = useState(false);

    function handleConfirmDeleteItem() {
        // setOpenConfirmDeletePopup(true);
        remove();
    }

    return (
        <>
            <tr className='row-table-body' key={index}>
                {!disabled &&
                    <td align='center'>
                        <span
                            className="pointer tooltip"
                            style={{ cursor: 'pointer' }}
                            onClick={() => setOpenConfirmDeletePopup(true)}
                        >
                            <Delete className="text-red" />
                        </span>
                    </td>
                }

                <td>
                    <GlobitsTextField name={withNameSpace("name")} />
                </td>
                <td>
                    <GlobitsPagingAutocompleteV2 api={ props?.public ? pagingPublicCertificate : pagingCertificates} name={withNameSpace("certificate")} />
                </td>
                <td>
                    <GlobitsTextField name={withNameSpace("level")} />
                </td>
                <td>
                    <GlobitsDateTimePicker
                        disableFuture
                        name={withNameSpace("issueDate")}
                    />
                </td>
            </tr>

            <GlobitsConfirmationDialog
                open={openConfirmDeletePopup}
                onConfirmDialogClose={() => setOpenConfirmDeletePopup(false)}
                onYesClick={handleConfirmDeleteItem}
                title={t("confirm_dialog.delete.title")}
                text={"Bạn có chắc muốn xóa chứng chỉ này?"}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />

        </>
    )
})
