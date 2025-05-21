import GlobitsPopupV2 from "../../../../common/GlobitsPopupV2";
import {Button, ButtonGroup, Grid, Icon, IconButton, Tooltip} from "@material-ui/core";
import GlobitsTable from "../../../../common/GlobitsTable";
import {Form, Formik} from "formik";
import FormikFocusError from "../../../../common/FormikFocusError";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import React from "react";
import {formatDate} from "../../../../LocalFunction";
import * as Yup from "yup";
import {useStore} from "../../../../stores";
import {useTranslation} from "react-i18next";
import {paging} from "../../../System/SystemParam/ContentTemplate/ContentTemplateService";
import {CandidateRecruitmentRoundResult} from "../../../../LocalConstants";
import GlobitsPagingAutocomplete from "../../../../common/form/GlobitsPagingAutocomplete";
import EditMailPopup from "./EditMailPopup";
import FileUploadField from "../../../../common/FileUploadField";

export default function CandidateChooseTemplatePopup({candidate, open, handleClose, handleSubmit}) {
    const {candidateStore} = useStore();
    const {t} = useTranslation();
    const {handleRemoveActionItem, getPreviewMail} = candidateStore;


    // Validation schema, chỉ kiểm tra template là bắt buộc, mailContents có thể tùy chỉnh thêm nếu cần
    const validationSchema = Yup.object({
        template: Yup.object().nullable().required(t("validation.required")),
        // mailContents không bắt buộc ở đây, bạn có thể thêm nếu muốn
    });

    // initialValues bao gồm mailContents là object lưu nội dung mail từng ứng viên theo id
    const initialValues = {candidate};

    return (
        <GlobitsPopupV2
            size={"xl"}
            open={open}
            noDialogContent
            title={"Danh sách gửi Email"}
            onClosePopup={handleClose}>
            <div style={{padding: '10px'}} className="dialog-body">
                <Formik
                    validationSchema={validationSchema}
                    enableReinitialize
                    initialValues={{...initialValues, open: false}}
                    onSubmit={(values, actions) => {
                        // Gọi hàm submit từ props, truyền theo formik values bao gồm mailContents riêng từng ứng viên
                        handleSubmit(values);
                        actions.setSubmitting(false);
                    }}
                >
                    {({isSubmitting, values, setFieldValue}) => {
                        const columns = [
                            {title: "Mã ứng viên", field: "candidateCode"},
                            {
                                title: "Họ tên", field: "displayName",
                                render: (rowData) =>
                                    <>
                                        <span>{rowData?.displayName}</span>
                                        {!rowData?.email && (
                                            <div style={{ color: 'red' }}>Không có Email</div>
                                        )}
                                    </>


                            },
                            {
                                title: "Ngày sinh",
                                field: "birthDate",
                                render: (rowData) =>
                                    <span>{rowData?.birthDate && formatDate("DD/MM/YYYY", rowData?.birthDate)}</span>,
                            },
                            {
                                title: "Kế hoạch tuyển dụng",
                                field: "recruitmentPlan",
                                render: (rowData) => (
                                    <span className="pr-8">{rowData?.recruitmentPlan?.name}</span>
                                )
                            },
                            {
                                title: "Vị trí ứng tuyển",
                                render: (rowData) => (
                                    <span className="pr-8">{rowData?.recruitmentPlan?.recruitmentRequest?.name}</span>
                                )
                            },
                            {
                                title: "Vòng hiện tại",
                                field: "currentCandidateRound",
                                render: (rowData) =>
                                    <span>{rowData?.currentCandidateRound?.recruitmentRound?.name}</span>,
                            },
                            {
                                title: "Trạng thái vòng hiện tại",
                                field: "currentCandidateRound",
                                render: (rowData) => rowData?.currentCandidateRound?.resultStatus ? CandidateRecruitmentRoundResult[rowData?.currentCandidateRound?.resultStatus] : "",
                            },
                            {
                                title: "Xem mail",
                                render: (rowData) => (
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <IconButton size="small"
                                                    disabled={!values.template}
                                                    onClick={async () => {
                                                        const candidate = values.candidate.find(candidate => candidate.id === rowData.id);
                                                        if (!candidate?.isEdit) {
                                                            let existingContent = await getPreviewMail(candidate.id, candidate?.template?.id) || "Soạn nội dung email tại đây...";
                                                            const updatedCandidates = values.candidate.map(candidate => {
                                                                if (candidate.id === rowData.id) {
                                                                    return {
                                                                        ...candidate,
                                                                        template: {
                                                                            ...candidate.template,
                                                                            content: existingContent,
                                                                        },
                                                                        isEdit: true
                                                                    };
                                                                }
                                                                return candidate;
                                                            });
                                                            setFieldValue("candidate", updatedCandidates);
                                                        }
                                                        setFieldValue("name", `candidate[${rowData.tableData.id}].template.content`);
                                                        setMailPopupOpen(true)
                                                    }}>
                                            <Icon fontSize="small" color="primary">edit</Icon>
                                        </IconButton>
                                        <Tooltip title="Hoàn tác" placement="top">
                                            <IconButton size="small"
                                                        onClick={async () => {
                                                            const candidate = values.candidate.find(candidate => candidate.id === rowData.id)
                                                            let existingContent = await getPreviewMail(candidate.id, candidate?.template?.id) || "Soạn nội dung email tại đây...";
                                                            const updatedCandidates = values.candidate.map(candidate => {
                                                                if (candidate.id === rowData.id) {
                                                                    return {
                                                                        ...candidate,
                                                                        template: {
                                                                            ...candidate.template,
                                                                            content: existingContent,
                                                                        },
                                                                        isEdit: true
                                                                    };
                                                                }
                                                                return candidate;
                                                            });
                                                            setFieldValue("candidate", updatedCandidates);
                                                        }}>
                                                <Icon fontSize="small" color="secondary">restore</Icon>
                                            </IconButton>
                                        </Tooltip>
                                    </ButtonGroup>
                                )
                            },
                            {
                                title: "Đính kèm file",
                                field: "attachedFile",
                                render: (rowData) => {
                                    const file = values.candidate.find(c => c.id === rowData.id)?.attachedFile;
                                    return (
                                        <FileUploadField
                                            id={rowData.id}
                                            value={file}
                                            onFileChange={(file) => {
                                                console.log(file)
                                                const updatedCandidates = values.candidate.map(candidate => {
                                                    if (candidate.id === rowData.id) {
                                                        return {
                                                            ...candidate,
                                                            attachedFile: file,
                                                        };
                                                    }
                                                    return candidate;
                                                });
                                                setFieldValue("candidate", updatedCandidates);
                                            }}
                                        />
                                    );
                                }
                            },
                            {
                                title: t("general.action"),
                                width: "6%",
                                align: "center",
                                render: (rowData) => (
                                    <Tooltip title="Loại bỏ" placement="top">
                                        <IconButton size="small" onClick={() => handleRemoveActionItem(rowData?.id)}>
                                            <Icon fontSize="small" color="secondary">delete</Icon>
                                        </IconButton>
                                    </Tooltip>
                                ),
                            },
                        ];
                        const setMailPopupOpen = (value) => {
                            setFieldValue("open", value);
                        }
                        return (
                            <>
                                <Form autoComplete="off">
                                    <FormikFocusError/>
                                    <Grid container spacing={2} style={{padding: '12px'}}>
                                        <Grid item xs={12} sm={8} md={9}>
                                            <div className="dialogScrollContent"
                                                 style={{maxHeight: "400px", overflowY: "auto"}}>
                                                <GlobitsTable
                                                    data={candidate}
                                                    columns={columns}
                                                    nonePagination
                                                />
                                            </div>
                                        </Grid>

                                        <Grid item xs={12} sm={4} md={3}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12}>
                                                    <GlobitsPagingAutocomplete
                                                        api={paging}
                                                        required
                                                        label={"Mẫu Email"}
                                                        name='template'
                                                        onChange={(__, value) => {
                                                            setFieldValue("template", value);
                                                            const updatedCandidates = values.candidate.map(candidate => ({
                                                                ...candidate,
                                                                template: {
                                                                    ...value,
                                                                },
                                                                isEdit: false
                                                            }));
                                                            setFieldValue("candidate", updatedCandidates);
                                                            setMailPopupOpen(false)
                                                        }}
                                                    />
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <FileUploadField
                                                        value={values?.attachedFileTemplate}
                                                        onFileChange={(file) => {
                                                            setFieldValue("attachedFileTemplate", file);
                                                        }}
                                                    />
                                                </Grid>
                                            </Grid>

                                            <div className="pt-12" style={{color: "#5e6c84"}}>
                                                {t("task.action")}
                                            </div>

                                            <div className="listButton" style={{marginTop: 12}}>
                                                <Button
                                                    variant="contained"
                                                    className="btn-green"
                                                    startIcon={<SaveIcon/>}
                                                    type="submit"
                                                    disabled={isSubmitting}
                                                >
                                                    Xác nhận
                                                </Button>

                                                <Button
                                                    startIcon={<DeleteIcon/>}
                                                    variant="contained"
                                                    onClick={handleClose}
                                                    className="btn-danger"
                                                    disabled={isSubmitting}
                                                >
                                                    Hủy bỏ
                                                </Button>
                                            </div>
                                        </Grid>
                                    </Grid>
                                </Form>
                                {values?.open && (
                                    <EditMailPopup
                                        open={values.open}
                                        handleClose={() => setMailPopupOpen(false)}
                                        name={values.name}
                                        handleSave={() => setMailPopupOpen(false)}
                                    />
                                )}
                            </>
                        )
                    }}
                </Formik>
            </div>
        </GlobitsPopupV2>
    );
}
