import { Button, ButtonGroup, DialogActions, DialogContent, Grid, Icon, IconButton } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { Form, Formik, useFormikContext } from "formik";
import { useStore } from "../../../../../stores";
import GlobitsTextField from "../../../../../common/form/GlobitsTextField";
import GlobitsDateTimePicker from "../../../../../common/form/GlobitsDateTimePicker";
import React from "react";
import { formatDate } from "../../../../../LocalFunction";
import GlobitsTable from "../../../../../common/GlobitsTable";
import AddIcon from "@material-ui/icons/Add";
import PopupStaff from "../TabChooseStaff/PopupStaff";
import FormikFocusError from "../../../../../common/FormikFocusError";
import GlobitsPopupV2 from "../../../../../common/GlobitsPopupV2";

export default observer(function StaffSalaryTemplateForm({ handleAfterSubmit, updateListOnClose, open }) {
    const { popupStaffStore, popupStaffSalaryTemplateStore, salaryTemplateStore } = useStore();
    const { t } = useTranslation();
    const {
        handleClose, openCreate, saveListStaffSalaryTemplate, defaultValuesForm,
    } = popupStaffSalaryTemplateStore;
    const { handleOpen } = popupStaffStore;

    const {
        selectedSalaryTemplate
    } = salaryTemplateStore;

    async function handleFormSubmit(values) {
        const newValues = {
            ...values, salaryTemplate: selectedSalaryTemplate,
        }
        await saveListStaffSalaryTemplate(newValues);
    }

    return (
        <GlobitsPopupV2
            open={openCreate}
            size='md'
            noDialogContent
            title={"Thêm mới nhân viên sử dụng mẫu bảng lương"}
            onClosePopup={handleClose}
        >
            <Formik
                enableReinitialize
                initialValues={defaultValuesForm}
                onSubmit={(values) => handleFormSubmit(values)}>
                {({ isSubmitting }) => (<Form autoComplete='off'>
                    <FormikFocusError />
                    <div className='dialog-body'>
                        <DialogContent className='o-hidden p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsTextField
                                        value={selectedSalaryTemplate?.name}
                                        label={"Mẫu bảng lương sử dụng"}
                                        name={"salaryTemplateName"}
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsDateTimePicker label={"Thời gian bắt đầu"} name='fromDate' required />
                                </Grid>
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsDateTimePicker label={"Thời gian kết thúc"} name='toDate' required />
                                </Grid>
                                <Grid item xs={12}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                        style={{ marginBottom: 15 }}
                                    >
                                        <Button
                                            startIcon={<AddIcon />}
                                            onClick={handleOpen}
                                        >
                                            Chọn nhân viên thực hiện
                                        </Button>
                                    </ButtonGroup>


                                    <ChooseStaffList />

                                    <PopupStaff />

                                </Grid>
                            </Grid>

                        </DialogContent>
                    </div>
                    <div className='dialog-footer'>
                        <DialogActions className='p-0'>
                            <div className='flex flex-space-between flex-middle'>
                                <Button startIcon={<BlockIcon />} variant='contained'
                                    className='mr-12 btn btn-secondary d-inline-flex' color='secondary'
                                    onClick={() => {
                                        handleClose()
                                    }}>
                                    {t("general.button.cancel")}
                                </Button>
                                <Button startIcon={<SaveIcon />}
                                    className='mr-0 btn btn-primary d-inline-flex'
                                    variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </div>
                </Form>)}
            </Formik>
        </GlobitsPopupV2>);
});

const ChooseStaffList = observer(() => {
    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();

    const handleRemoveSelectedStaff = (chosenStaff) => {
        let valuesNew = values?.staffs.filter(item => String(item.id) !== String(chosenStaff?.id));
        setFieldValue("staffs", valuesNew)
    };
    const columns = [{
        title: t("general.action"), minWidth: "48px", align: "center", render: (rowData) => (<IconButton
            size="small"
            onClick={(event) => {
                event.stopPropagation();
                handleRemoveSelectedStaff(rowData);
            }}
        >
            <Icon fontSize="small" color="secondary">
                delete
            </Icon>
        </IconButton>),
    }, {
        title: "Mã nhân viên",
        field: "staffCode",
        align: "center",
        render: (rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
    }, {
        title: "Nhân viên", minWidth: "200px", render: (rowData) => (<>
            {rowData.displayName && (<p className='m-0'>
                <strong>{rowData.displayName}</strong>
            </p>)}

            {rowData.birthDate && <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>}

            {rowData.gender && <p className='m-0'>Giới
                tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}

            {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
        </>),
    }, {
        title: "Thông tin liên hệ", field: "info", minWidth: "200px", render: (rowData) => (<>
            {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

            {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}
        </>),
    },

    {
        title: "Đơn vị đang công tác",
        field: "organization.name",
        align: "left",
        minWidth: "120px",
        render: (rowData) => <span className='pr-6'>{rowData?.organization?.name}</span>,
    },

    {
        title: "Phòng ban",
        field: "department.name",
        align: "left",
        minWidth: "120px",
        render: (rowData) => <span className='pr-6'>{rowData?.department?.name}</span>,
    },

    {
        title: "Chức danh",
        field: "positionTitle.name",
        align: "left",
        minWidth: "120px",
        render: (rowData) => <span className='pr-6'>{rowData?.positionTitle?.name}</span>,
    },

    {
        title: "Vị trí",
        field: "currentPosition.name",
        align: "left",
        minWidth: "120px",
        render: (rowData) => <span className='pr-6'>{rowData?.currentPosition?.name}</span>,
    },

    {
        title: "Nơi ở hiện tại",
        field: "currentResidence",
        align: "left",
        minWidth: "180px",
        render: (rowData) => <span className='pr-6'>{rowData?.currentResidence}</span>,
    },];

    return (<GlobitsTable
        data={values?.staffs || []}
        columns={columns}
        nonePagination
    />);
})