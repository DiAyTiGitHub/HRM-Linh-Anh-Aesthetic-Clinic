import { Button, ButtonGroup, Grid, makeStyles } from "@material-ui/core";
import { Add, Delete } from "@material-ui/icons";
import { CandidateRecruitmentRoundDocument } from "app/common/Model/Recruitment/CandidateRecruitmentRoundDocument";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo, useCallback } from "react";
import { useTranslation } from "react-i18next";
import RecruitmentPlanPersonParticipate from "../../RecruitmentPlanV2/RecruitmentPlanPersonParticipate";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SelectFile from "app/views/StaffDocumentItem/SelectFile";

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
        marginTop: "16px",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            },
        },
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            // width: "calc(100vw / 4)",
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px",
        },
    },
}));

const CandidateRecruitmentRoundDocumentList = () => {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();
    const [choose, setChoose] = React.useState(null);
    const [open, setOpen] = React.useState(null);
    const [setOpenCandidateList] = React.useState(false);

    function handleAddNewRow(push) {
        const newItem = new CandidateRecruitmentRoundDocument();
        newItem.roundOrder = values.recruitmentRounds?.length ? values.recruitmentRounds?.length + 1 : 1;
        push(newItem);
    }

    function removeRow(index) {
        const newDocuments = values?.documents
            ?.filter((item, i) => i !== index)
            ?.map((item, i) => ({
                ...item,
                roundOrder: i + 1, // nếu bạn muốn đánh số bắt đầu từ 1
            }));
        setFieldValue("documents", newDocuments);
    }

    const handleOpenCandidateList = () => {
        setOpenCandidateList(true);
    };
    const handleOpen = () => {
        setOpen(true);
    };
    const handleClose = () => {
        setOpen(false);
        setOpenCandidateList(false);
    };

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray name='documents'>
                    {({ push }) => (
                        <>
                            <Grid item xs={12}>
                                <Grid container spacing={1} className=''>
                                    <Grid item xs={12}>
                                        <ButtonGroup color='container' aria-label='outlined primary button group'>
                                            <Button onClick={() => handleAddNewRow(push)} fullWidth>
                                                <Add color='white' className='mr-2 addIcon' />
                                                Thêm tài liệu đính kèm
                                            </Button>
                                        </ButtonGroup>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={12}>
                                <section className={classes.tableContainer}>
                                    <table className={`w-100 ${classes.table}`}>
                                        <thead>
                                            <tr className={classes.tableHeader}>
                                                <th className={"text-center"} style={{ width: "5%" }}>
                                                    STT
                                                </th>
                                                <th className={"text-center"}>Thao tác</th>
                                                <th className={"text-center"}>Tệp đính kèm</th>
                                                <th className={"text-center"}>Ghi chú</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {values?.documents?.length > 0 ? (
                                                values?.documents?.map((order, index) => (
                                                    <CandidateRecruitmentRoundDocumentItem
                                                        index={index}
                                                        remove={() => removeRow(index)}
                                                        nameSpace={`documents[${index}]`}
                                                        item={order}
                                                    />
                                                ))
                                            ) : (
                                                <tr className='row-table-body row-table-no_data'>
                                                    <td colSpan={7} align='center' className='py-8'>
                                                        Chưa có vòng tuyển dụng nào
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </table>
                                </section>
                            </Grid>
                            <RecruitmentPlanPersonParticipate open={open} handleClose={handleClose} value={choose} />
                        </>
                    )}
                </FieldArray>
            </Grid>
        </Grid>
    );
};

const CandidateRecruitmentRoundDocumentItem = memo(({ index, remove, nameSpace, item }) => {
    const { t } = useTranslation();
    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`;
        return nameSpace;
    };

    const { values, setFieldValue } = useFormikContext();

    const handleAffterSubmitFile = useCallback(
        (file) => {
            setFieldValue(withNameSpace("file"), file);
        },
        [index, setFieldValue, withNameSpace, values?.staffDocumentItems] // Thêm values.staffDocumentItems vào dependencies
    );

    return (
        <tr className='row-table-body' key={index}>
            <td style={{ textAlign: "center" }}>{index + 1}</td>
            <td align='center'>
                <span className='pointer tooltip text-red' style={{ cursor: "pointer" }} onClick={remove}>
                    <Delete className='text-red' />
                </span>
            </td>

            <td>
                <SelectFile
                    name={withNameSpace("file")}
                    fileProp={item?.file}
                    showPreview={true}
                    showDowload={true}
                    showDelete={true}
                    showName={false}
                    handleAffterSubmit={handleAffterSubmitFile}
                    maxFileSize={5242880}
                />
            </td>
            <td>
                <GlobitsTextField name={withNameSpace("note")} />
            </td>
        </tr>
    );
});

export default memo(observer(CandidateRecruitmentRoundDocumentList));
