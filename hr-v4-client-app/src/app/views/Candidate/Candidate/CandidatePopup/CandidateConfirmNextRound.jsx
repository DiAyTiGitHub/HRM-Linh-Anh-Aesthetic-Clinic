import {observer} from "mobx-react";
import React, {memo} from "react";
import {Button, Grid, Icon, IconButton, Tooltip} from "@material-ui/core";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import {useStore} from "app/stores";
import {Form, Formik} from "formik";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import {formatDate} from "app/LocalFunction";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants, {CandidateRecruitmentRoundStatusLabel, CandidateStatus, HttpStatus} from "app/LocalConstants";
import {toJS} from "mobx";

function CandidatePopupNextRound(props) {
    const {type} = props;

    const {candidateStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        handleRemoveActionItem,
        listOnDelete,
        handleSelectListDelete,
        listCandidates,
        openPopupNextRound,
        pagingCandidates
    } = candidateStore;
    const {passListToNextRound, responseData,handleClearResponseData} = useStore().candidateRecruitmentRoundStore;
    const columns = [
        {
            title: t("general.action"),
            width: "6%",
            align: "center",
            render: (rowData) => {
                return (
                    <div className='flex flex-middle w-100 justify-center'>
                        <Tooltip title='Loại bỏ' placement='top'>
                            <IconButton className='' size='small' onClick={() => handleRemoveActionItem(rowData?.id)}>
                                <Icon fontSize='small' color='secondary'>
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: "Mã ứng viên",
            field: "candidateCode",
        },
        {
            title: "Họ tên",
            field: "displayName",
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            render: (rowData) => <span>{rowData?.birthDate && formatDate("DD/MM/YYYY", rowData?.birthDate)}</span>,
        },
        {
            title: "Trạng thái",
            minWidth: "150px",
            render: (rowData) => {
                const status = responseData?.[rowData?.currentCandidateRound?.id]?.status;
                if (status === HttpStatus.OK) {
                    return "Thành Công";
                } else if (status !== undefined && status !== null) {
                    return "Thất bại";
                }
                return "";
            }
        },
        {
            title: "Nội dung",
            minWidth: "150px",
            render: (rowData) => {
                console.log(responseData)
                return responseData?.[rowData?.currentCandidateRound?.id]?.message || ""
            }
        },
        {
            title: "Đơn vị tuyển dụng",
            field: "recruitmentPlan.recruitmentRequest.organization.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Phòng ban tuyển dụng",
            field: "recruitmentPlan.recruitmentRequest.hrDepartment.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Vị trí tuyển dụng",
            field: "positionTitle.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Vòng hiện tại",
            field: "currentCandidateRound",
            minWidth: "150px",
            render: (rowData) => <span>{rowData?.currentCandidateRound?.recruitmentRound?.name}</span>,
        },
        {
            title: "Trạng thái vòng phỏng vấn",
            field: "currentCandidateRound",
            minWidth: "150px",
            render: (rowData) =>
                rowData?.currentCandidateRound?.status
                    ? CandidateRecruitmentRoundStatusLabel[rowData?.currentCandidateRound?.status]
                    : "",
        },
    ];

    const close = () => {
        handleClearResponseData()
        pagingCandidates()
        handleClose()
    }

    return (
        <GlobitsColorfulThemePopup
            open={openPopupNextRound}
            handleClose={close}
            hideFooter
            size='lg'
            onConfirm={{}}>
            <div className='dialog-body'>
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={8} md={9}>
                        <div className='dialogScrollContent'>
                            <h6 className='text-red'>
                                <strong>
                                    {listOnDelete?.length <= 1 ? "Thông tin " : "Danh sách "}
                                    {/* ứng viên được chọn {getStatusLabel(type).toUpperCase()} */}
                                </strong>
                            </h6>
                            <GlobitsTable
                                data={listOnDelete}
                                handleSelectList={handleSelectListDelete}
                                columns={columns}
                                nonePagination
                            />
                        </div>
                    </Grid>

                    <Grid item xs={12} sm={4} md={3}>
                        <Formik
                            enableReinitialize
                            initialValues={listCandidates}
                            onSubmit={async (values) => {
                                try {
                                    const payload = listOnDelete.map(item => item.currentCandidateRound?.id);
                                    await passListToNextRound(payload);
                                } catch (error) {
                                    console.error('Có lỗi xảy ra:', error);
                                }
                            }}>
                            {({isSubmitting, values, setFieldValue, initialValues}) => {
                                return (
                                    <Form autoComplete='off' autocomplete='off'>
                                        <FormikFocusError/>

                                        <Grid container spacing={2}>
                                            {(type === CandidateStatus.REJECTED.value ||
                                                type === CandidateStatus.NOT_SCREENED.value) && (
                                                <Grid item xs={12}>
                                                    <GlobitsTextField
                                                        required
                                                        label='Lý do từ chối'
                                                        name='refusalReason'
                                                        multiline
                                                        rows={3}
                                                    />
                                                </Grid>
                                            )}

                                            {type === CandidateStatus.APPROVED.value && (
                                                <Grid item xs={12}>
                                                    <GlobitsDateTimePicker
                                                        isDateTimePicker
                                                        required
                                                        name='interviewDate'
                                                        label='Ngày phỏng vấn/thi tuyển'
                                                    />
                                                </Grid>
                                            )}
                                            {type === CandidateStatus.APPROVED.value && (
                                                <Grid item xs={12}>
                                                    <GlobitsTextField label='Vị trí dự thi' name='examPosition'/>
                                                </Grid>
                                            )}
                                        </Grid>

                                        <div className='pt-12' style={{color: "#5e6c84"}}>
                                            {t("task.action")}
                                        </div>

                                        <div className='listButton'>
                                            <Button
                                                variant='contained'
                                                className='btn-green'
                                                startIcon={<SaveIcon/>}
                                                type='submit'
                                                disabled={isSubmitting}>
                                                Xác nhận
                                            </Button>

                                            <Button
                                                startIcon={<DeleteIcon/>}
                                                variant='contained'
                                                onClick={close}
                                                className='btn-danger'
                                                disabled={isSubmitting}>
                                                Hủy bỏ
                                            </Button>
                                        </div>
                                    </Form>
                                );
                            }}
                        </Formik>
                    </Grid>
                </Grid>
            </div>
        </GlobitsColorfulThemePopup>
    );
}

export default memo(observer(CandidatePopupNextRound));
