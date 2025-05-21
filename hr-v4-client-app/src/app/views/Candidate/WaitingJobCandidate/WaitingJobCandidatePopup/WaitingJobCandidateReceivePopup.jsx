import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Grid, Button } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { Form, Formik } from 'formik';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsTable from 'app/common/GlobitsTable';
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";
import GlobitsDateTimePicker from "../../../../common/form/GlobitsDateTimePicker";

function WaitingJobCandidateReceivePopup() {
    const { waitingJobCandidateStore } = useStore();
    const { t } = useTranslation();
    const {
        openReceiveJobPopup,
        handleClose,
        handleConfirmReceiveJob,
        listChosen,
        pagingWaitingJobCandidates,
    } = waitingJobCandidateStore;

    const columns = [
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
            render: (rowData) => (
                <span>
                    {rowData?.birthDate && (formatDate("DD/MM/YYYY", rowData?.birthDate))}
                </span>
            ),
        },
        {
            title: "Ngày nhận việc",
            field: "onboardDate",
            render: (rowData) => {
                return (
                    <GlobitsDateTimePicker
                        required
                        name={`listChosen[${rowData.tableData.id}].onboardDate`}
                    />
                );
            },
        },
        {
            title: "Giới tính",
            field: "gender",
            render: rowData => (
                <span>
                    {rowData?.gender === "M" ? "Nam" : rowData?.gender === "F" ? "Nữ" : ""}
                </span>
            )
        },
        {
            title: "SĐT",
            field: "phoneNumber",
            render: rowData => (
                <>
                    {rowData?.phoneNumber && (
                        <span className="pr-8">
                            {rowData?.phoneNumber}
                        </span>
                    )}
                </>
            )
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
    ];

    function handleCloseConfirmPopup() {
        handleClose();
        pagingWaitingJobCandidates();
    }

    const history = useHistory();

    async function handleConfirm(values) {
        try {
            const response = await handleConfirmReceiveJob(values.listChosen);
            if (response?.id) {
                const redirectUrl = ConstantList.ROOT_PATH + "staff/edit/" + response.id;
                history.push(redirectUrl);
            }
        }
        catch (error) {
            console.error(error);
        }
    }

    return (
        <GlobitsColorfulThemePopup
            hideFooter
            open={openReceiveJobPopup}
            handleClose={handleCloseConfirmPopup}
            size="lg"
        >
            <Formik
                initialValues={{
                    listChosen,
                }}
                validationSchema={Yup.array().of(
                    Yup.object().shape({
                        onboardDate: Yup.string()
                            .required('Vui lòng nhập ngày nhận việc cho ứng viên.')
                            .typeError('Ngày nhận việc không hợp lệ')
                    })
                )}
                onSubmit={handleConfirm}
                enableReinitialize
            >
                {({ handleSubmit, errors, touched, values, setFieldValue }) => (
                    <Form onSubmit={handleSubmit}>
                        <div className="dialog-body">
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <div className="dialogScrollContent">
                                        <h6 className="text-red">
                                            <span>
                                                <strong>CẢNH BÁO:</strong> Bạn đang thực hiện thao tác chuyển ứng viên thành nhân viên của công ty.
                                                <br />
                                                Kiểm tra kỹ lưỡng tất cả thông tin liên quan.
                                                Thao tác này không thể hoàn tác.
                                                <br />
                                            </span>
                                            <strong className='pt-4 flex'>
                                                {values.listChosen?.length <= 1 ? "Thông tin " : "Danh sách "}
                                                ứng viên NHẬN VIỆC
                                            </strong>
                                        </h6>

                                        {touched.listChosen && errors.listChosen && (
                                            <div className="text-red-500">{errors.listChosen}</div>
                                        )}

                                        <GlobitsTable
                                            data={values.listChosen}
                                            handleSelectList={(newList) => {
                                                setFieldValue("listChosen", newList);
                                            }}
                                            columns={columns}
                                            nonePagination
                                        />
                                    </div>
                                </Grid>
                            </Grid>
                            <Grid container spacing={2} justifyContent="flex-end" mt={2}>
                                <Grid item>
                                    <Button variant="outlined" onClick={handleCloseConfirmPopup}>
                                        Hủy
                                    </Button>
                                </Grid>
                                <Grid item>
                                    <Button
                                        type="submit"
                                        variant="contained"
                                        color="primary"
                                    >
                                        Xác nhận và tạo hồ sơ nhân viên
                                    </Button>
                                </Grid>
                            </Grid>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsColorfulThemePopup>
    );
}

export default memo(observer(WaitingJobCandidateReceivePopup));