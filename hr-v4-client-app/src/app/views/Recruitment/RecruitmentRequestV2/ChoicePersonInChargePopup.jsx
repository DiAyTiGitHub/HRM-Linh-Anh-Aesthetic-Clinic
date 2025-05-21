import { Button, DialogActions, DialogContent, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import * as Yup from "yup";
import GlobitsDateTime from "../../../common/form/GlobitsDateTime";
import GlobitsDateTimePicker from "../../../common/form/GlobitsDateTimePicker";

function ChoicePersonInChargePopup() {
    const { recruitmentRequestStore } = useStore();
    const { t } = useTranslation();

    const { handleClose, openChoicePersonInChargePopup, listChosen, handlePersonInCharge } = recruitmentRequestStore;

    const validationSchema = Yup.object({
        staff: Yup.object().nullable().required(t("validation.required")),
        recruitmentRequest: Yup.array()
            .min(1, "Cần chọn ít nhất một yêu cầu tuyển dụng")
            .required(t("validation.required")),
        recruitingStartDate: Yup.date().nullable().required(t("validation.required")),
    });
    async function handleSaveForm(values) {
        // Check if recruitmentRequest list is empty
        if (!values.recruitmentRequest || values.recruitmentRequest.length === 0) {
            // Show toast notification
            toast.error("Vui lòng chọn ít nhất một yêu cầu tuyển dụng");
            return;
        }

        const dto = {
            staffId: values.staff?.id,
            chosenIds: values.recruitmentRequest.map((item) => item.id),
        };

        // Call your API or service to save the data
        const response = await handlePersonInCharge(dto);

        // Proceed with your save logic if list is not empty
        // ... your existing save logic here ...
    }
    return (
        <GlobitsPopupV2
            size='md'
            scroll={"body"}
            open={openChoicePersonInChargePopup}
            noDialogContent
            title={"Gán nhân sự cho yêu cầu tuyển dụng"}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={{
                    staff: null,
                    recruitmentRequest: listChosen || [],
                    recruitingStartDate: new Date(),
                }}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={6} className='mb-12'>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("Nhân viên")}
                                            name='staff'
                                            api={pagingStaff}
                                            getOptionLabel={(option) => {
                                                return `${option?.displayName} - ${option?.staffCode}`;
                                            }}
                                            required
                                        />
                                    </Grid>
                                    <Grid item xs={6} className='mb-12'>
                                        <GlobitsDateTimePicker
                                            label={"Ngày bắt đầu tuyển dụng"}
                                            name='recruitingStartDate'
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <ListRecruitmentRequest />
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            <DialogActions className='dialog-footer px-12'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        disabled={isSubmitting}
                                        onClick={handleClose}>
                                        {t("general.button.close")}
                                    </Button>
                                    <Button
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='primary'
                                        type='submit'
                                        disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

const ListRecruitmentRequest = () => {
    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext(); // 👈 Lấy Formik context

    const handleRemoveActionItem = (id) => {
        const newList = values.recruitmentRequest.filter((item) => item.id !== id);
        setFieldValue("recruitmentRequest", newList);
    };
    const columns = [
        {
            title: t("general.action"),
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
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
            title: "Mã",
            field: "code",
            align: "left",
        },

        {
            title: "Tên yêu cầu",
            field: "name",
            align: "left",
        },
        {
            title: "Số lượng",
            field: "quantity",
            align: "left",
        },
        {
            title: "Trạng thái",
            field: "status",
            align: "left",
            render: (row) => {
                return (
                    <span>
                        {
                            LocalConstants.RecruitmentRequestStatus.getListData().find((i) => i.value == row?.status)
                                ?.name
                        }
                    </span>
                );
            },
        },
        {
            title: "Đơn vị",
            field: "organization.name",
            align: "left",
        },
        {
            title: "Phòng ban",
            field: "hrDepartment.name",
            align: "left",
        },

        {
            title: "Chức danh cần tuyển",
            field: "positionTitle.name",
            align: "left",
        },
    ];

    return <GlobitsTable data={values?.recruitmentRequest} columns={columns} />;
};
export default memo(observer(ChoicePersonInChargePopup));
